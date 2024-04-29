package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.blockentity.BlockEntityMovingBlock;
import cn.nukkit.blockentity.BlockEntityPistonArm;
import cn.nukkit.event.block.BlockPistonEvent;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.HeightRange;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.Faceable;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static cn.nukkit.GameVersion.*;

/**
 * @author CreeperFace
 */
public abstract class BlockPistonBase extends BlockTransparentMeta implements Faceable {

    public boolean sticky = false;

    public BlockPistonBase() {
        this(0);
    }

    public BlockPistonBase(int meta) {
        super(meta);
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.PISTON_ARM;
    }

    @Override
    public float getResistance() {
        return 7.5f;
    }

    @Override
    public float getHardness() {
        if (V1_20_30.isAvailable()) {
            return 1.5f;
        }
        return 0.5f;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (player != null) {
            if (Math.abs(player.getFloorX() - this.x) <= 1 && Math.abs(player.getFloorZ() - this.z) <= 1) {
                double y = player.y + player.getEyeHeight();

                if (y - this.y > 2) {
                    this.setDamage(BlockFace.UP.getIndex());
                } else if (this.y - y > 0) {
                    this.setDamage(BlockFace.DOWN.getIndex());
                } else {
                    this.setDamage(player.getHorizontalFacing().getIndex());
                }
            } else {
                this.setDamage(player.getHorizontalFacing().getIndex());
            }
        }
        this.level.setBlock(block, this, true, true);

        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.PISTON_ARM)
                .putInt("facing", this.getBlockFace().getIndex())
                .putBoolean("Sticky", this.sticky);

        BlockEntityPistonArm piston = (BlockEntityPistonArm) BlockEntities.createBlockEntity(BlockEntityType.PISTON_ARM, this.level.getChunk(getChunkX(), getChunkZ()), nbt);
        piston.powered = isPowered();

        this.checkState(piston.powered);
        return true;
    }

    @Override
    public boolean onBreak(Item item, Player player) {
        this.level.setBlock(this, Block.get(BlockID.AIR), true, true);

        Block block = this.getSide(getBlockFace());

        if (block instanceof BlockPistonHead && ((BlockPistonHead) block).getBlockFace() == this.getBlockFace()) {
            block.onBreak(item, player);
        }
        return true;
    }

    public boolean isExtended() {
        BlockFace face = getBlockFace();
        Block block = getSide(face);

        return block instanceof BlockPistonHead && ((BlockPistonHead) block).getBlockFace() == face;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_REDSTONE) {
            if (!this.level.isRedstoneEnabled()) {
                return 0;
            }

            level.scheduleUpdate(this, 1);
            return type;
        }

        if (type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (!this.level.isRedstoneEnabled()) {
                return 0;
            }

            BlockEntity blockEntity = this.level.getBlockEntity(this);
            if (blockEntity instanceof BlockEntityPistonArm) {
                BlockEntityPistonArm arm = (BlockEntityPistonArm) blockEntity;
                boolean powered = this.isPowered();

                if (arm.state % 2 == 0 && arm.powered != powered && checkState(powered)) {
                    arm.powered = powered;

                    if (arm.chunk != null) {
                        arm.chunk.setChanged();
                    }
                }
            }

            return type;
        }

        return 0;
    }

    private boolean checkState(Boolean isPowered) {
        if (!this.level.isRedstoneEnabled()) {
            return false;
        }

        if (isPowered == null) {
            isPowered = this.isPowered();
        }

        BlockFace face = getBlockFace();
        Block block = getSide(face);

        boolean isExtended;
        if (block instanceof BlockPistonHead) {
            if (((BlockPistonHead) block).getBlockFace() != face) {
                return false;
            }

            isExtended = true;
        } else {
            isExtended = false;
        }

        if (isPowered && !isExtended) {
            if (!this.doMove(true)) {
                return false;
            }

            this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_PISTON_OUT);
            return true;
        } else if (!isPowered && isExtended) {
            if (!this.doMove(false)) {
                return false;
            }

            this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_PISTON_IN);
            return true;
        }

        return false;
    }

    private boolean isPowered() {
        BlockFace face = getBlockFace();

        for (BlockFace side : BlockFace.getValues()) {
            if (side == face) {
                continue;
            }

            Block b = this.getSide(side);

            if (b.getId() == Block.REDSTONE_WIRE && b.getDamage() > 0) {
                return true;
            }

            if (this.level.isSidePowered(b, side)) {
                return true;
            }
        }

        return false;
    }

    private boolean doMove(boolean extending) {
        BlockFace direction = getBlockFace();
//        Block extendBlock = getSide(direction);
//
//        if (extending && extendBlock instanceof BlockPistonHead && ((BlockPistonHead) extendBlock).getBlockFace() != direction) {
//            return false;
//        }

        BlocksCalculator calculator = new BlocksCalculator(extending);

        boolean canMove = calculator.canMove();

        if (!canMove && extending) {
            return false;
        }

        List<BlockVector3> attached = Collections.emptyList();

        BlockPistonEvent event = new BlockPistonEvent(this, direction, calculator.getBlocksToMove(), calculator.getBlocksToDestroy(), extending);
        this.level.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false;
        }

        if (canMove && (this.sticky || extending)) {
            List<Block> destroyBlocks = calculator.getBlocksToDestroy();
            for (int i = destroyBlocks.size() - 1; i >= 0; --i) {
                Block block = destroyBlocks.get(i);
                if (block.canContainWater()) {
                    level.setExtraBlock(block, Blocks.air(), true, false);
                } else if (block.getId() == SNOW_LAYER) {
                    if (level.getExtraBlock(block).canContainSnow()) {
                        level.useBreakOn(block, ItemTool.getUniversalTool());
                    }
                }
                this.level.useBreakOn(block, ItemTool.getUniversalTool());
            }

            List<Block> newBlocks = calculator.getBlocksToMove();

            attached = newBlocks.stream().map(Vector3::asBlockVector3).collect(Collectors.toList());

            BlockFace side = extending ? direction : direction.getOpposite();

            for (Block newBlock : newBlocks) {
                Vector3 oldPos = newBlock.copyVec();
                newBlock.position(newBlock.getSidePos(side));

                BlockEntity blockEntity = this.level.getBlockEntity(oldPos);
//                Block extra = this.level.getExtraBlock(oldPos);

                level.setExtraBlock(newBlock, Blocks.air(), true, false);
                this.level.setBlock(newBlock, Block.get(BlockID.MOVING_BLOCK), true);

                CompoundTag movingBlockTag = BlockSerializer.serialize(newBlock);
//                CompoundTag movingBlockExtraTag = BlockSerializer.serialize(extra);

                CompoundTag nbt = BlockEntity.getDefaultCompound(newBlock, BlockEntity.MOVING_BLOCK)
                        .putInt("pistonPosX", this.getFloorX())
                        .putInt("pistonPosY", this.getFloorY())
                        .putInt("pistonPosZ", this.getFloorZ())
                        .putCompound("movingBlock", movingBlockTag)
//                        .putCompound("movingBlockExtra", movingBlockExtraTag)
                        .putBoolean("expanding", extending)
                        ;

                if (blockEntity != null && !(blockEntity instanceof BlockEntityMovingBlock)) {
                    blockEntity.saveNBT();

                    CompoundTag t = new CompoundTag(blockEntity.namedTag.getTags());

                    nbt.putCompound("movingEntity", t);

                    if (blockEntity instanceof InventoryHolder) {
                        ((InventoryHolder) blockEntity).getInventory().clearAll();
                    }

                    blockEntity.close();
                }

                new BlockEntityMovingBlock(this.level.getChunk(newBlock.getChunkX(), newBlock.getChunkZ()), nbt);

                if (/*(!extending || !oldPos.equalsVec(getSideVec(direction))) &&*/ this.level.getBlock(oldPos).getId() != BlockID.MOVING_BLOCK) {
                    level.setExtraBlock(oldPos, Blocks.air(), true, false);
                    this.level.setBlock(oldPos, Block.get(BlockID.AIR), true);
                }
            }
        }

        if (extending) {
            Vector3 pos = this.getSide(direction);
            level.setExtraBlock(pos, Blocks.air(), true, false);
            if (this.sticky && Block.list[STICKY_PISTON_ARM_COLLISION] != null) {
                this.level.setBlock(pos, get(STICKY_PISTON_ARM_COLLISION, this.getDamage()), true);
            } else {
                this.level.setBlock(pos, get(PISTON_ARM_COLLISION, this.getDamage()), true);
            }
        }

        BlockEntityPistonArm blockEntity = (BlockEntityPistonArm) this.level.getBlockEntity(this);
        blockEntity.move(extending, attached);
        return true;
    }

    public static boolean canPush(Block block, BlockFace face, boolean destroyBlocks, boolean extending) {
        HeightRange heightRange = block.level.getHeightRange();
        if (block.getY() >= heightRange.getMinY() && (face != BlockFace.DOWN || block.getY() != heightRange.getMinY()) &&
                block.getY() < heightRange.getMaxY() && (face != BlockFace.UP || block.getY() != heightRange.getMaxY() - 1)) {
            if (extending && !block.canBePushed() || !extending && !block.canBePulled()) {
                return false;
            }

            if (block.breaksWhenMoved()) {
                return destroyBlocks || block.sticksToPiston();
            }

            BlockEntity be = block.level.getBlockEntity(block);
            return be == null || be.isMovable();
        }

        return false;
    }

    public class BlocksCalculator {

        private final Vector3 pistonPos;
        private Vector3 armPos;
        private final Block blockToMove;
        private final BlockFace moveDirection;
        private final boolean extending;

        private final List<Block> toMove = new ArrayList<Block>() {
            @Override
            public int indexOf(Object o) {
                if (o == null) {
                    for (int i = 0; i < size(); i++) {
                        if (get(i) == null) {
                            return i;
                        }
                    }
                } else {
                    for (int i = 0; i < size(); i++) {
                        Block block = get(i);
                        if (o instanceof Block) {
                            if (((Block) o).equalsVec(block)) {
                                return i;
                            }
                        } else if (o.equals(block)) {
                            return i;
                        }
                    }
                }
                return -1;
            }

            @Override //以防万一
            public boolean contains(Object o) {
                return indexOf(o) >= 0;
            }
        };
        private final List<Block> toDestroy = new ArrayList<>();

        public BlocksCalculator(boolean extending) {
            this.pistonPos = getLocation();
            this.extending = extending;

            BlockFace face = getBlockFace();
            if (!extending) {
                this.armPos = pistonPos.getSideVec(face);
            }

            if (extending) {
                this.moveDirection = face;
                this.blockToMove = getSide(face);
            } else {
                this.moveDirection = face.getOpposite();
                if (sticky) {
                    this.blockToMove = getSide(face, 2);
                } else {
                    this.blockToMove = null;
                }
            }
        }

        public boolean canMove() {
            if (!sticky && !extending) {
                return true;
            }

            this.toMove.clear();
            this.toDestroy.clear();
            Block block = this.blockToMove;

            if (!canPush(block, this.moveDirection, true, extending)) {
                return false;
            }

            if (block.breaksWhenMoved()) {
                if (extending || block.sticksToPiston()) {
                    this.toDestroy.add(this.blockToMove);
                }

                return true;
            }

            if (!this.addBlockLine(this.blockToMove, this.moveDirection)) {
                return false;
            }

            for (int i = 0; i < this.toMove.size(); ++i) {
                Block b = this.toMove.get(i);

                if (b.getId() == SLIME && !this.addBranchingBlocks(b)) {
                    return false;
                }
            }

            return true;
        }

        private boolean addBlockLine(Block origin, BlockFace from) {
            Block block = origin.clone();

            if (block.getId() == AIR) {
                return true;
            }

            if (!canPush(origin, this.moveDirection, false, extending)) {
                return true;
            }

            if (origin.equalsVec(this.pistonPos)) {
                return true;
            }

            if (this.toMove.contains(origin)) { //TODO: (ec) Block::equals overrode
                return true;
            }

            if (this.toMove.size() >= 12) {
                return false;
            }

            this.toMove.add(block);

            int count = 1;
            List<Block> sticked = new ArrayList<>();

            while (block.getId() == SLIME) {
                block = origin.getSide(this.moveDirection.getOpposite(), count);

                if (block.getId() == AIR || !canPush(block, this.moveDirection, false, extending) || block.equalsVec(this.pistonPos)) {
                    break;
                }

                if (block.breaksWhenMoved() && block.sticksToPiston()) {
                    this.toDestroy.add(block);
                    break;
                }

                if (++count + this.toMove.size() > 12) {
                    return false;
                }

                sticked.add(block);
            }

            int stickedCount = sticked.size();

            if (stickedCount > 0) {
                this.toMove.addAll(Lists.reverse(sticked));
            }

            int step = 1;

            while (true) {
                Block nextBlock = origin.getSide(this.moveDirection, step);
                int index = this.toMove.indexOf(nextBlock); //TODO: (ec) Block::equals overrode

                if (index > -1) {
                    this.reorderListAtCollision(stickedCount, index);

                    for (int i = 0; i <= index + stickedCount; ++i) {
                        Block b = this.toMove.get(i);

                        if (b.getId() == SLIME && !this.addBranchingBlocks(b)) {
                            return false;
                        }
                    }

                    return true;
                }

                if (nextBlock.getId() == AIR || nextBlock.equalsVec(armPos)) {
                    return true;
                }

                if (!canPush(nextBlock, this.moveDirection, true, extending) || nextBlock.equalsVec(this.pistonPos)) {
                    return false;
                }

                if (nextBlock.breaksWhenMoved()) {
                    this.toDestroy.add(nextBlock);
                    return true;
                }

                if (this.toMove.size() >= 12) {
                    return false;
                }

                this.toMove.add(nextBlock);
                ++stickedCount;
                ++step;
            }
        }

        private void reorderListAtCollision(int count, int index) {
            List<Block> list = new ArrayList<>(this.toMove.subList(0, index));
            List<Block> list1 = new ArrayList<>(this.toMove.subList(this.toMove.size() - count, this.toMove.size()));
            List<Block> list2 = new ArrayList<>(this.toMove.subList(index, this.toMove.size() - count));
            this.toMove.clear();
            this.toMove.addAll(list);
            this.toMove.addAll(list1);
            this.toMove.addAll(list2);
        }

        private boolean addBranchingBlocks(Block block) {
            for (BlockFace face : BlockFace.getValues()) {
                if (face.getAxis() != this.moveDirection.getAxis() && !this.addBlockLine(block.getSide(face), face)) {
                    return false;
                }
            }

            return true;
        }

        public List<Block> getBlocksToMove() {
            return this.toMove;
        }

        public List<Block> getBlocksToDestroy() {
            return this.toDestroy;
        }
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(this.getItemId());
    }

    @Override
    public BlockFace getBlockFace() {
        BlockFace face = BlockFace.fromIndex(this.getDamage());

        return face.getHorizontalIndex() >= 0 ? face.getOpposite() : face;
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }
}
