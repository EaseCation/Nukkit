package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.food.Food;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

public class BlockCakeCandle extends BlockCake {
    public static final int LIT_BIT = 0b1;

    public BlockCakeCandle() {
        this(0);
    }

    public BlockCakeCandle(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CANDLE_CAKE;
    }

    @Override
    public String getName() {
        return "Candle Cake";
    }

    @Override
    public double getMinX() {
        return this.x + 0.0625;
    }

    @Override
    public int getComparatorInputOverride() {
        return 14;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[] {
                Item.get(getItemId(getCandleBlockId())),
        };
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, Player player) {
        int id = item.getId();
        if (id == Item.FIRE_CHARGE) {
            if (isLit()) {
                return true;
            }

            if (level.getExtraBlock(this).isWaterSource()) {
                return true;
            }

            if (player != null && !player.isCreative()) {
                item.pop();
            }

            level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_IGNITE);

            setLit(true);
            level.setBlock(this, this, true);
            return true;
        }
        if (id == Item.FLINT_AND_STEEL) {
            if (isLit()) {
                return true;
            }

            if (player != null && !player.isCreative()) {
                item.useOn(this);
            }

            if (level.getExtraBlock(this).isWaterSource()) {
                return true;
            }

            level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_IGNITE);

            setLit(true);
            level.setBlock(this, this, true);
            return true;
        }
        if (item.hasEnchantment(Enchantment.FIRE_ASPECT)) {
            if (isLit()) {
                return true;
            }

            if (level.getExtraBlock(this).isWaterSource()) {
                return true;
            }

            if (player != null && !player.isCreative()) {
                item.useOn(this);
            }

            level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_IGNITE);

            setLit(true);
            level.setBlock(this, this, true);
            return true;
        }

        if (player != null && (player.getFoodData().getLevel() < player.getFoodData().getMaxLevel() || player.isCreative() || player.getServer().getDifficulty() == 0)) {
            Food.getByRelative(get(getId())).eatenBy(player);

            level.dropItem(this, Item.get(getItemId(getCandleBlockId())));

            level.setBlock(this, get(BLOCK_CAKE, 1), true);
            return true;
        }

        if (id == AIR) {
            if (!isLit()) {
                return false;
            }

            setLit(false);
            level.setBlock(this, this, true);
            return true;
        }

        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!canSurvive()) {
                level.useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }

            if (!isLit()) {
                return 0;
            }

            if (!level.getExtraBlock(this).isWaterSource()) {
                return 0;
            }

            setLit(false);
            level.setBlock(this, this, true);
            return Level.BLOCK_UPDATE_NORMAL;
        }

        return 0;
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        if (isLit()) {
            return;
        }

        if ((entity instanceof EntityLiving || entity instanceof EntityProjectile) && entity.isOnFire() && !level.getExtraBlock(this).isWaterSource()) {
            level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_IGNITE);

            setLit(true);
            level.setBlock(this, this, true);
        }
    }

    @Override
    public boolean isCandleCake() {
        return true;
    }

    protected int getCandleBlockId() {
        return CANDLE;
    }

    public boolean isLit() {
        return getDamage() == LIT_BIT;
    }

    public void setLit(boolean lit) {
        setDamage(lit ? LIT_BIT : 0);
    }
}
