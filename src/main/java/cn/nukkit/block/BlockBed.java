package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityBed;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.item.Item;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Dimension;
import cn.nukkit.level.Explosion;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.Faceable;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockBed extends BlockTransparentMeta implements Faceable {

    public BlockBed() {
        this(0);
    }

    public BlockBed(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BLOCK_BED;
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.BED;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public double getResistance() {
        return 1;
    }

    @Override
    public double getHardness() {
        return 0.2;
    }

    @Override
    public String getName() {
        return this.getDyeColor().getName() + " Bed Block";
    }

    @Override
    public double getMaxY() {
        return this.y + 0.5625;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, Player player) {
        if (this.level.getDimension() != Dimension.OVERWORLD) {
            if (!this.level.getGameRules().getBoolean(GameRule.RESPAWN_BLOCKS_EXPLODE)) {
                return true;
            }

            Explosion explosion = new Explosion(this.add(0.5, 0.5, 0.5), 5, this, true);
            if (!level.getExtraBlock(this).isWater()) { //TODO: check head block only
                explosion.explodeA();
            }
            explosion.explodeB();
            return true;
        }

        if (player != null && !player.hasEffect(Effect.WATER_BREATHING) && !player.hasEffect(Effect.CONDUIT_POWER)
                && this.level.getExtraBlock(this).isWater()) {
            return false;
        }

        Block blockNorth = this.north();
        Block blockSouth = this.south();
        Block blockEast = this.east();
        Block blockWest = this.west();

        Block b;
        if ((this.getDamage() & 0x08) == 0x08) {
            b = this;
        } else {
            if (blockNorth.getId() == this.getId() && (blockNorth.getDamage() & 0x08) == 0x08) {
                b = blockNorth;
            } else if (blockSouth.getId() == this.getId() && (blockSouth.getDamage() & 0x08) == 0x08) {
                b = blockSouth;
            } else if (blockEast.getId() == this.getId() && (blockEast.getDamage() & 0x08) == 0x08) {
                b = blockEast;
            } else if (blockWest.getId() == this.getId() && (blockWest.getDamage() & 0x08) == 0x08) {
                b = blockWest;
            } else {
                if (player != null) {
                    player.sendMessage(new TranslationContainer("tile.bed.notValid"));
                }

                return true;
            }
        }

        if (player != null) {
            player.setSpawnBlockPosition(b);

            int time = this.getLevel().getTime() % Level.TIME_FULL;
            boolean isNight = (time >= Level.TIME_NIGHT && time < Level.TIME_SUNRISE);
            if (!isNight && !this.level.isRaining()) {
                player.sendMessage(new TranslationContainer("tile.bed.noSleep"));
                return true;
            }

            if (!player.sleepOn(b)) {
                player.sendMessage(new TranslationContainer("tile.bed.occupied"));
            }
        }

        return true;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        Block down = this.down();
        if (SupportType.hasFullSupport(down, BlockFace.UP)) {
            Block next = this.getSide(player.getDirection());
            Block downNext = next.down();

            if (next.canBeReplaced() && SupportType.hasFullSupport(downNext, BlockFace.UP)) {
                int meta = player.getDirection().getHorizontalIndex();

                this.getLevel().setBlock(block, Block.get(this.getId(), meta), true, true);
                this.getLevel().setBlock(next, Block.get(this.getId(), meta | 0x08), true, true);

                createBlockEntity(this, item.getDamage());
                createBlockEntity(next, item.getDamage());
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onBreak(Item item) {
        Block blockNorth = this.north(); //Gets the blocks around them
        Block blockSouth = this.south();
        Block blockEast = this.east();
        Block blockWest = this.west();

        if ((this.getDamage() & 0x08) == 0x08) { //This is the Top part of bed
            if (blockNorth.getId() == BLOCK_BED && (blockNorth.getDamage() & 0x08) != 0x08) { //Checks if the block ID&&meta are right
                this.getLevel().setBlock(blockNorth, Block.get(BlockID.AIR), true, true);
            } else if (blockSouth.getId() == BLOCK_BED && (blockSouth.getDamage() & 0x08) != 0x08) {
                this.getLevel().setBlock(blockSouth, Block.get(BlockID.AIR), true, true);
            } else if (blockEast.getId() == BLOCK_BED && (blockEast.getDamage() & 0x08) != 0x08) {
                this.getLevel().setBlock(blockEast, Block.get(BlockID.AIR), true, true);
            } else if (blockWest.getId() == BLOCK_BED && (blockWest.getDamage() & 0x08) != 0x08) {
                this.getLevel().setBlock(blockWest, Block.get(BlockID.AIR), true, true);
            }
        } else { //Bottom Part of Bed
            if (blockNorth.getId() == this.getId() && (blockNorth.getDamage() & 0x08) == 0x08) {
                this.getLevel().setBlock(blockNorth, Block.get(BlockID.AIR), true, true);
            } else if (blockSouth.getId() == this.getId() && (blockSouth.getDamage() & 0x08) == 0x08) {
                this.getLevel().setBlock(blockSouth, Block.get(BlockID.AIR), true, true);
            } else if (blockEast.getId() == this.getId() && (blockEast.getDamage() & 0x08) == 0x08) {
                this.getLevel().setBlock(blockEast, Block.get(BlockID.AIR), true, true);
            } else if (blockWest.getId() == this.getId() && (blockWest.getDamage() & 0x08) == 0x08) {
                this.getLevel().setBlock(blockWest, Block.get(BlockID.AIR), true, true);
            }
        }

        this.getLevel().setBlock(this, Block.get(BlockID.AIR), true, false); // Do not update both parts to prevent duplication bug if there is two fallable blocks top of the bed

        return true;
    }

    private void createBlockEntity(Vector3 pos, int color) {
        CompoundTag nbt = BlockEntity.getDefaultCompound(pos, BlockEntity.BED);
        nbt.putByte("color", color);

        BlockEntities.createBlockEntity(BlockEntityType.BED, this.level.getChunk(pos.getFloorX() >> 4, pos.getFloorZ() >> 4), nbt);
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.BED, this.getDyeColor().getWoolData());
    }

    @Override
    public BlockColor getColor() {
        return this.getDyeColor().getColor();
    }

    public DyeColor getDyeColor() {
        if (this.level != null) {
            BlockEntity blockEntity = this.level.getBlockEntity(this);

            if (blockEntity instanceof BlockEntityBed) {
                return ((BlockEntityBed) blockEntity).getDyeColor();
            }
        }

        return DyeColor.WHITE;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & 0x7);
    }

    @Override
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public boolean sticksToPiston() {
        return false;
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return false;
    }
}
