package cn.nukkit.block.edu;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockTransparent;
import cn.nukkit.block.SupportType;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityChalkboard;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Mth;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

import javax.annotation.Nullable;

public class BlockChalkboard extends BlockTransparent implements Faceable {
    public BlockChalkboard() {
        this(0);
    }

    public BlockChalkboard(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CHALKBOARD;
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.CHALKBOARD_BLOCK;
    }

    @Override
    public String getName() {
        return "Chalkboard";
    }

    @Override
    public float getHardness() {
        return 1;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return null;
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return false;
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        BlockEntityChalkboard chalkboard = getBaseBlockEntity();
        int meta;
        if (chalkboard == null) {
            meta = 0;
        } else {
            meta = Mth.clamp(chalkboard.getSize(), BlockEntityChalkboard.SIZE_1X1, BlockEntityChalkboard.SIZE_3X2);
        }
        return Item.get(Item.BOARD, meta);
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        BlockEntityChalkboard chalkboard = getBlockEntity();
        if (chalkboard == null || !chalkboard.isBase()) {
            return new Item[0];
        }
        return super.getDrops(item, player);
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (face == BlockFace.DOWN) {
            return false;
        }

        int size = item.is(Item.BOARD) ? switch (item.getDamage()) {
            case BlockEntityChalkboard.SIZE_2X1 -> BlockEntityChalkboard.SIZE_2X1;
            case BlockEntityChalkboard.SIZE_3X2 -> BlockEntityChalkboard.SIZE_3X2;
            default -> BlockEntityChalkboard.SIZE_1X1;
        } : BlockEntityChalkboard.SIZE_1X1;
        boolean onGround = face == BlockFace.UP && player != null;

        BlockFace facing;
        if (onGround) {
            if (size == BlockEntityChalkboard.SIZE_1X1) {
                facing = null;
                setDamage(Mth.floor(((player.getYaw() + 180) * 16 / 360) + 0.5) & 0xf);
            } else {
                facing = player.getHorizontalFacing().getOpposite();
                setDamage(facing.getHorizontalIndex() * 4);
            }
        } else {
            facing = face;
            setDamage(facing.getHorizontalIndex());
        }

        int thisX = getFloorX();
        int thisY = getFloorY();
        int thisZ = getFloorZ();

        if (size != BlockEntityChalkboard.SIZE_1X1) {
            int height;
            Vector3 left;
            Vector3 right = getSideVec(facing.rotateYCCW());

            if (size == BlockEntityChalkboard.SIZE_2X1) {
                height = 0;
                left = this;
            } else if (!level.getHeightRange().isValidBlockY(thisY + 1)) {
                return false;
            } else {
                height = 1;
                left = getSideVec(facing.rotateY());
            }

            int leftX = left.getFloorX();
            int leftZ = left.getFloorZ();
            int rightX = right.getFloorX();
            int rightZ = right.getFloorZ();

            for (int x = Math.min(leftX, rightX); x <= Math.max(leftX, rightX); x++) {
                for (int z = Math.min(leftZ, rightZ); z <= Math.max(leftZ, rightZ); z++) {
                    for (int y = thisY; y <= thisY + height; y++) {
                        if (x == thisX && y == thisY && z == thisZ) {
                            continue;
                        }

                        Block side = level.getBlock(x, y, z);

                        if (!side.canBeReplaced()) {
                            return false;
                        }

                        if (side.isWaterSource()) {
                            level.setExtraBlock(side, side, true, false);
                        }
                        level.setBlock(side, clone(), true, false);
                        createBlockEntity(item, side, thisX, thisY, thisZ, onGround, size);
                    }
                }
            }
        }

        if (block.isWaterSource()) {
            level.setExtraBlock(this, block, true, false);
        }
        level.setBlock(this, this, true);
        createBlockEntity(item, this, thisX, thisY, thisZ, onGround, size);
        return true;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        //TODO: UI
        return true;
    }

    @Override
    public boolean isChemistryFeature() {
        return true;
    }

    @Override
    public BlockFace getBlockFace() {
        BlockEntityChalkboard chalkboard = getBaseBlockEntity();
        if (chalkboard == null) {
            return BlockFace.SOUTH;
        }
        return BlockFace.fromHorizontalIndex(chalkboard.isOnGround() ? (getDamage() + 1) / 4 : getDamage());
    }

    private static BlockEntityChalkboard createBlockEntity(@Nullable Item item, Position pos, int baseX, int baseY, int baseZ, boolean onGround, int size) {
        CompoundTag nbt = BlockEntity.getDefaultCompound(pos, BlockEntity.CHALKBOARD_BLOCK);

        nbt.putInt(BlockEntityChalkboard.TAG_BASE_X, baseX);
        nbt.putInt(BlockEntityChalkboard.TAG_BASE_Y, baseY);
        nbt.putInt(BlockEntityChalkboard.TAG_BASE_Z, baseZ);

        nbt.putBoolean(BlockEntityChalkboard.TAG_ON_GROUND, onGround);
        nbt.putInt(BlockEntityChalkboard.TAG_SIZE, size);

        if (item != null && item.hasCustomBlockData()) {
            for (Tag tag : item.getCustomBlockData().getAllTags()) {
                nbt.put(tag.getName(), tag);
            }
        }

        return (BlockEntityChalkboard) BlockEntities.createBlockEntity(BlockEntityType.CHALKBOARD_BLOCK, pos.getChunk(), nbt);
    }

    @Nullable
    protected BlockEntityChalkboard getBlockEntity() {
        if (level == null) {
            return null;
        }
        if (level.getBlockEntity(this) instanceof BlockEntityChalkboard blockEntity) {
            return blockEntity;
        }
        return null;
    }

    @Nullable
    private BlockEntityChalkboard getBaseBlockEntity() {
        BlockEntityChalkboard blockEntity = getBlockEntity();
        if (blockEntity == null) {
            return null;
        }
        if (blockEntity.isBase()) {
            return blockEntity;
        }
        if (level.getBlockEntity(blockEntity.getBaseX(), blockEntity.getBaseY(), blockEntity.getBaseZ()) instanceof BlockEntityChalkboard base) {
            return base;
        }
        return null;
    }
}
