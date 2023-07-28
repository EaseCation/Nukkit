package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.event.block.BlockFadeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/12/6 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockSnowLayer extends BlockFallable {
    public static final int HEIGHT_MASK = 0b111;
    public static final int COVERED_BIT = 0b1000;

    private int meta;

    public BlockSnowLayer() {
        this(0);
    }

    public BlockSnowLayer(int meta) {
        this.meta = meta;
    }

    @Override
    public final int getFullId() {
        return (this.getId() << BLOCK_META_BITS) | this.getDamage();
    }

    @Override
    public final int getDamage() {
        return this.meta;
    }

    @Override
    public final void setDamage(int meta) {
        this.meta = meta;
    }

    @Override
    public String getName() {
        return "Top Snow";
    }

    @Override
    public int getId() {
        return SNOW_LAYER;
    }

    @Override
    public double getHardness() {
        return 0.1;
    }

    @Override
    public double getResistance() {
        return 0.5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    public boolean canBeReplaced() {
        return !isFull() && level.getExtraBlock(this).canBeReplaced();
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (block.isLiquid() || !block.isAir() && block.canContainWater() && level.getExtraBlock(this).isWater()) {
            return false;
        }

        if (!canSurvive()) {
            return false;
        }

        if (block.isAir()) {
            return super.place(item, block, target, face, fx, fy, fz, player);
        }

        if (block.getId() == getId()) {
            return onActivate(item, face, player);
        }

        if (block.canContainSnow()) {
            level.setExtraBlock(this, block, true, false);
            setDamage(COVERED_BIT);
            return super.place(item, block, target, face, fx, fy, fz, player);
        }

        return false;
    }

    private boolean canSurvive() {
        Block below = this.down();
        return below.getId() != ICE && below.getId() != FROSTED_ICE && (below.isLeaves() || SupportType.hasFullSupport(below, BlockFace.UP));
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (super.onUpdate(type) == type) {
                return type;
            }

            if (!this.canSurvive()) {
                this.level.useBreakOn(this, null, null, true);
                if (this.level.getGameRules().getBoolean(GameRule.DO_TILE_DROPS)) {
                    this.level.dropItem(this, this.toItem(true));
                    return type;
                }
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (this.getLevel().getBlockLightAt((int) this.x, (int) this.y, (int) this.z) >= 10) {
                BlockFadeEvent event = new BlockFadeEvent(this, (this.getDamage() & HEIGHT_MASK) > 0 ? get(SNOW_LAYER, this.getDamage() - 1) : get(AIR));
                level.getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    level.setBlock(this, event.getNewState(), true);
                    return Level.BLOCK_UPDATE_NORMAL;
                }
            }
        }
        return 0;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.SNOWBALL);
    }

    @Override
    public Item pick(boolean addUserData) {
        return Item.get(SNOW_LAYER);
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isShovel() && item.getTier() >= ItemTool.TIER_WOODEN) {
            Item drop = this.toItem(true);
            int height = this.getDamage() & HEIGHT_MASK;
            drop.setCount(height < 3 ? 1 : height < 5 ? 2 : height == 7 ? 4 : 3);
            return new Item[]{drop};
        } else {
            return new Item[0];
        }
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SNOW_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean isTransparent() {
        return !isFull();
    }

    @Override
    public boolean canBeFlowedInto() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return isFull();
    }

    @Override
    public double getMaxY() {
        int height = this.getDamage() & HEIGHT_MASK;
        return height < 3 ? this.y : height == 7 ? this.y + 1 : this.y + 0.5;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, Player player) {
        if (item.isShovel()) {
            if (player != null && !player.isCreative()) {
                item.useOn(this);
            }
            this.level.useBreakOn(this, item.clone().clearNamedTag(), null, true);
            return true;
        } else if (item.getId() == SNOW_LAYER) {
            if (!isFull()) {
                this.setDamage(this.getDamage() + 1);
                this.level.setBlock(this ,this, true);

                if (player != null && (player.gamemode & 0x1) == 0) {
                    item.count--;
                }

                level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_PLACE, getFullId());
                return true;
            }
        }
        return false;
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
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return isFull();
    }

    @Override
    protected boolean canSlide(Block below) {
        return below.getId() == SNOW_LAYER && (below.getDamage() & HEIGHT_MASK) != HEIGHT_MASK || super.canSlide(below);
    }

    @Override
    protected int getFallingBlockDamage() {
        return getDamage() & HEIGHT_MASK;
    }

    public boolean isFull() {
        return (getDamage() & HEIGHT_MASK) == HEIGHT_MASK;
    }
}
