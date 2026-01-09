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

import static cn.nukkit.GameVersion.*;

/**
 * Created on 2015/12/6 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockSnowLayer extends BlockFallable {
    public static final int HEIGHT_MASK = 0b111;
    public static final int COVERED_BIT = 0b1000;

    BlockSnowLayer() {

    }

    @Override
    public String getName() {
        return "Snow";
    }

    @Override
    public int getId() {
        return SNOW_LAYER;
    }

    @Override
    public float getHardness() {
        return 0.1f;
    }

    @Override
    public float getResistance() {
        return 0.5f;
    }

    @Override
    public int getToolType() {
        return BlockToolType.SHOVEL;
    }

    @Override
    public boolean canBeReplaced() {
        return !isFull() && (!isCovered() || level.getExtraBlock(this).canBeReplaced());
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (block.isLiquid() || !block.isAir() && block.canContainWater() && level.getExtraBlock(this).isWater()) {
            return false;
        }

        if (!canSurvive(down())) {
            return false;
        }

        if (block.isAir()) {
            return super.place(item, block, target, face, fx, fy, fz, player);
        }

        if (block.getId() == getId()) {
            return block.onActivate(item, face, fx, fy, fz, player);
        }

        if (block.canContainSnow()) {
            level.setExtraBlock(this, block, true, false);
            setDamage(COVERED_BIT);
            return super.place(item, block, target, face, fx, fy, fz, player);
        }

        return false;
    }

    private boolean canSurvive(Block below) {
        return below.getId() != ICE && below.getId() != FROSTED_ICE && (below.isLeaves() || SupportType.hasFullSupport(below, BlockFace.UP));
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block below = null;
            boolean removeCovered = false;
            if (isCovered()) {
                Block extra = level.getExtraBlock(this);
                if (extra.canContainSnow()) {
                    below = down();
                    int id = below.getId();
                    int extraId = extra.getId();
                    if (id != GRASS_BLOCK && id != DIRT && id != FARMLAND && id != MYCELIUM && id != PODZOL && id != DIRT_WITH_ROOTS && id != MOSS_BLOCK && id != PALE_MOSS_BLOCK && id != MUD && id != MUDDY_MANGROVE_ROOTS
                            && (extraId != SHORT_DRY_GRASS && extraId != TALL_DRY_GRASS || id != SAND && id != RED_SAND && id != SUSPICIOUS_SAND && !below.isTerracotta())) {
                        level.setExtraBlock(this, Blocks.air(), true, false);
                        if (level.gameRules.getBoolean(GameRule.DO_TILE_DROPS)) {
                            for (Item drop : extra.getDrops(Item.get(AIR))) {
                                level.dropItem(this, drop);
                            }
                        }

                        setDamage(getDamage() & ~COVERED_BIT);
                        removeCovered = true;
                    }
                }
            }

            if (super.onUpdate(type) == type) {
                return type;
            }

            if (!this.canSurvive(below != null ? below : down())) {
                this.level.useBreakOn(this, true);
                if (this.level.getGameRules().getBoolean(GameRule.DO_TILE_DROPS)) {
                    this.level.dropItem(this, this.toItem(true));
                }
                return type;
            }

            if (removeCovered) {
                level.setBlock(this, this, true, false);
                return type;
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (this.getLevel().getBlockLightAt((int) this.x, (int) this.y, (int) this.z) >= 10) {
                BlockFadeEvent event = new BlockFadeEvent(this, (this.getDamage() & HEIGHT_MASK) > 0 ? get(SNOW_LAYER, this.getDamage() & ~HEIGHT_MASK | (this.getDamage() & HEIGHT_MASK) - 1) : get(AIR));
                level.getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    Block newBlock = event.getNewState();
                    if (newBlock.isAir() && isCovered()) {
                        newBlock = level.getExtraBlock(this);
                    }
                    level.setBlock(this, newBlock, true);
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
    public Item[] getDrops(Item item, Player player) {
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
        if (V1_20_30.isAvailable()) {
            return y + height * 2 / 16.0;
        }
        return height < 3 ? this.y : height == 7 ? this.y + 1 : this.y + 0.5;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (item.isShovel()) {
            if (player != null && player.isSurvivalLike() && item.hurtAndBreak(1) < 0) {
                item.pop();
                player.level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_BREAK);
            }

            level.addLevelSoundEvent(blockCenter(), LevelSoundEventPacket.SOUND_ITEM_USE_ON, getFullId());

            this.level.useBreakOn(this, item.clone().clearNamedTag());
            return true;
        }
        if (item.getId() == SNOW_LAYER) {
            if (!isFull()) {
                this.setDamage(this.getDamage() & ~HEIGHT_MASK | (this.getDamage() & HEIGHT_MASK) + 1);
                this.level.setBlock(this, this, true);

                if (player != null && (player.gamemode & 0x1) == 0) {
                    item.count--;
                }

                level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_PLACE, getFullId());
                return true;
            } else if (face.isHorizontal()) {
                Block block = getSide(face);
                if (block.is(SNOW_LAYER) && (block.getDamage() & HEIGHT_MASK) != HEIGHT_MASK) {
                    block.setDamage(block.getDamage() & ~HEIGHT_MASK | (block.getDamage() & HEIGHT_MASK) + 1);
                    level.setBlock(block, block, true);

                    if (player != null && !player.isSurvivalLike()) {
                        item.pop();
                    }

                    level.addLevelSoundEvent(block, LevelSoundEventPacket.SOUND_PLACE, block.getFullId());
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean canPassThrough() {
        if (V1_20_30.isAvailable()) {
            return (this.getDamage() & HEIGHT_MASK) == 0;
        }
        return (this.getDamage() & HEIGHT_MASK) < 3;
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

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public Item[] getSilkTouchResource() {
        int height = getDamage() & HEIGHT_MASK;
        if (height == HEIGHT_MASK) {
            return new Item[]{Item.get(SNOW)};
        }
        return new Item[]{Item.get(SNOW_LAYER, 0, 1 + height)};
    }

    public boolean isCovered() {
        return (getDamage() & COVERED_BIT) != 0;
    }

    public boolean isFull() {
        return (getDamage() & HEIGHT_MASK) == HEIGHT_MASK;
    }
}
