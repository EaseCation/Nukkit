package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.food.Food;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nullable;

/**
 * @author Nukkit Project Team
 */
public class BlockCake extends BlockTransparentMeta {

    public BlockCake(int meta) {
        super(meta);
    }

    public BlockCake() {
        this(0);
    }

    @Override
    public String getName() {
        return "Cake Block";
    }

    @Override
    public int getId() {
        return BLOCK_CAKE;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public double getResistance() {
        return 2.5;
    }

    @Override
    public double getMinX() {
        return this.x + (1 + getDamage() * 2) / 16.0;
    }

    @Override
    public double getMinZ() {
        return this.z + 0.0625;
    }

    @Override
    public double getMaxX() {
        return this.x - 0.0625 + 1;
    }

    @Override
    public double getMaxY() {
        return this.y + 0.5;
    }

    @Override
    public double getMaxZ() {
        return this.z - 0.0625 + 1;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (canSurvive()) {
            getLevel().setBlock(block, this, true, true);

            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!canSurvive()) {
                getLevel().setBlock(this, Block.get(BlockID.AIR), true);

                return Level.BLOCK_UPDATE_NORMAL;
            }
        }

        return 0;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[0];
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.CAKE);
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, Player player) {
        if (item.isBlockItem()) {
            switch (item.getBlockId()) {
                case CANDLE:
                    tryAddCandle(item, player, CANDLE_CAKE);
                    return true;
                case WHITE_CANDLE:
                    tryAddCandle(item, player, WHITE_CANDLE_CAKE);
                    return true;
                case ORANGE_CANDLE:
                    tryAddCandle(item, player, ORANGE_CANDLE_CAKE);
                    return true;
                case MAGENTA_CANDLE:
                    tryAddCandle(item, player, MAGENTA_CANDLE_CAKE);
                    return true;
                case LIGHT_BLUE_CANDLE:
                    tryAddCandle(item, player, LIGHT_BLUE_CANDLE_CAKE);
                    return true;
                case YELLOW_CANDLE:
                    tryAddCandle(item, player, YELLOW_CANDLE_CAKE);
                    return true;
                case LIME_CANDLE:
                    tryAddCandle(item, player, LIME_CANDLE_CAKE);
                    return true;
                case PINK_CANDLE:
                    tryAddCandle(item, player, PINK_CANDLE_CAKE);
                    return true;
                case GRAY_CANDLE:
                    tryAddCandle(item, player, GRAY_CANDLE_CAKE);
                    return true;
                case LIGHT_GRAY_CANDLE:
                    tryAddCandle(item, player, LIGHT_GRAY_CANDLE_CAKE);
                    return true;
                case CYAN_CANDLE:
                    tryAddCandle(item, player, CYAN_CANDLE_CAKE);
                    return true;
                case PURPLE_CANDLE:
                    tryAddCandle(item, player, PURPLE_CANDLE_CAKE);
                    return true;
                case BLUE_CANDLE:
                    tryAddCandle(item, player, BLUE_CANDLE_CAKE);
                    return true;
                case BROWN_CANDLE:
                    tryAddCandle(item, player, BROWN_CANDLE_CAKE);
                    return true;
                case GREEN_CANDLE:
                    tryAddCandle(item, player, GREEN_CANDLE_CAKE);
                    return true;
                case RED_CANDLE:
                    tryAddCandle(item, player, RED_CANDLE_CAKE);
                    return true;
                case BLACK_CANDLE:
                    tryAddCandle(item, player, BLACK_CANDLE_CAKE);
                    return true;
            }
        }

        if (player != null && (player.getFoodData().getLevel() < player.getFoodData().getMaxLevel() || player.isCreative() || player.getServer().getDifficulty() == 0)) {
            if (!Food.getByRelative(this).eatenBy(player)) {
                return true;
            }

            level.addLevelSoundEvent(player.getEyePosition(), LevelSoundEventPacket.SOUND_BURP);

            int bite = getDamage();
            if (bite >= 0x6) {
                level.useBreakOn(this);
                return true;
            }

            setDamage(bite + 1);
            level.setBlock(this, this, true);
            return true;
        }

        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
    }

    @Override
    public int getComparatorInputOverride() {
        return (7 - this.getDamage()) * 2;
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
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
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return false;
    }

    @Override
    public boolean isCake() {
        return true;
    }

    private boolean tryAddCandle(Item item, @Nullable Player player, int candleCakeBlockId) {
        if (getDamage() != 0) {
            return false;
        }

        if (player != null && !player.isCreative()) {
            item.pop();
        }

        level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_AMBIENT_CANDLE);

        level.setBlock(this, get(candleCakeBlockId), true);
        return true;
    }

    protected boolean canSurvive() {
        Block below = down();
        return !below.isAir() && !below.isLiquid();
    }
}
