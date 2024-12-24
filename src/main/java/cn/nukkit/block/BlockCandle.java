package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Mth;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;

public class BlockCandle extends BlockTransparent {
    public static final int CANDLE_COUNT_MASK = 0b11;
    public static final int LIT_BIT = 0b100;

    public BlockCandle() {
        this(0);
    }

    public BlockCandle(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CANDLE;
    }

    @Override
    public String getName() {
        return "Candle";
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
    public boolean isSolid() {
        return false;
    }

    @Override
    public int getLightLevel() {
        return isLit() ? getCandleCount() * 3 : 0;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        Item[] drops = new Item[getCandleCount()];
        for (int i = 0; i < drops.length; i++) {
            drops[i] = toItem(true);
        }
        return drops;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (block.getId() == getId()) {
            BlockCandle candle = (BlockCandle) block;
            int count = candle.getCandleCount();
            if (count == 4) {
                return false;
            }

            level.addLevelSoundEvent(candle.blockCenter(), LevelSoundEventPacket.SOUND_ITEM_USE_ON, candle.getFullId());

            candle.setCandleCount(count + 1);
            level.setBlock(candle, candle, true);
            return true;
        }

        if (block.isLiquid() || !block.isAir() && block.canContainWater() && level.getExtraBlock(this).isWater()) {
            return false;
        }

        if (!SupportType.hasCenterSupport(down(), BlockFace.UP)) {
            return false;
        }

        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        int id = item.getId();

        if (id == AIR) {
            if (!isLit()) {
                return false;
            }

            level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_EXTINGUISH_CANDLE);

            setLit(false);
            level.setBlock(this, this, true);
            return true;
        }

        if (id == getItemId()) {
            int count = getCandleCount();
            if (count == 4) {
                return false;
            }

            if (player != null && !player.isCreative()) {
                item.pop();
            }

            level.addLevelSoundEvent(blockCenter(), LevelSoundEventPacket.SOUND_ITEM_USE_ON, getFullId());

            setCandleCount(count + 1);
            level.setBlock(this, this, true);
            return true;
        }

        if (id == Item.FIRE_CHARGE) {
            if (isLit()) {
                return true;
            }

            if (player != null && !player.isCreative()) {
                item.pop();
            }

            level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_IGNITE);
            level.addLevelEvent(this, LevelEventPacket.EVENT_SOUND_GHAST_SHOOT, 78642);

            setLit(true);
            level.setBlock(this, this, true);
            return true;
        }
        if (id == Item.FLINT_AND_STEEL || item.isSword() && item.hasEnchantment(Enchantment.FIRE_ASPECT)) {
            if (isLit()) {
                return true;
            }

            if (player != null && player.isSurvivalLike() && item.hurtAndBreak(1) < 0) {
                item.pop();
                player.level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_BREAK);
            }

            level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_IGNITE);

            setLit(true);
            level.setBlock(this, this, true);
            return true;
        }

        return false;
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

        if ((entity instanceof EntityLiving || entity instanceof EntityProjectile) && entity.isOnFire()) {
            level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_IGNITE);

            setLit(true);
            level.setBlock(this, this, true);
        }
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        switch (getCandleCount()) {
            default:
            case 1:
                return new SimpleAxisAlignedBB(x + 7 / 16.0, y, z + 7 / 16.0, x + 1 - 7 / 16.0, y + 6 / 16.0, z + 1 - 7 / 16.0);
            case 2:
                return new SimpleAxisAlignedBB(x + 5 / 16.0, y, z + 7 / 16.0, x + 1 - 5 / 16.0, y + 6 / 16.0, z + 1 - 6 / 16.0);
            case 3:
                return new SimpleAxisAlignedBB(x + 5 / 16.0, y, z + 6 / 16.0, x + 1 - 6 / 16.0, y + 6 / 16.0, z + 1 - 5 / 16.0);
            case 4:
                return new SimpleAxisAlignedBB(x + 5 / 16.0, y, z + 5 / 16.0, x + 1 - 5 / 16.0, y + 6 / 16.0, z + 1 - 6 / 16.0);
        }
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }

    @Override
    public boolean isCandle() {
        return true;
    }

    public int getCandleCount() {
        return (getDamage() & CANDLE_COUNT_MASK) + 1;
    }

    public void setCandleCount(int count) {
        setDamage((getDamage() & ~CANDLE_COUNT_MASK) | ((Mth.clamp(count, 1, 4) - 1) & CANDLE_COUNT_MASK));
    }

    public boolean isLit() {
        return (getDamage() & LIT_BIT) == LIT_BIT;
    }

    public void setLit(boolean lit) {
        setDamage(lit ? getDamage() | LIT_BIT : getDamage() & ~LIT_BIT);
    }
}
