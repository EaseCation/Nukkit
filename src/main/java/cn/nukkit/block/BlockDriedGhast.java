package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.passive.EntityHappyGhast;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

public class BlockDriedGhast extends BlockTransparent implements Faceable {
    public static final int DIRECTION_MASK = 0b11;
    public static final int REHYDRATION_LEVEL_MASK = 0b1100;
    public static final int REHYDRATION_LEVEL_OFFSET = 2;

    public static final int MAX_REHYDRATION_LEVEL = 3;

    BlockDriedGhast() {

    }

    @Override
    public int getId() {
        return DRIED_GHAST;
    }

    @Override
    public String getName() {
        return "Dried Ghast";
    }

    @Override
    public float getHardness() {
        return 0f;
    }

    @Override
    public float getResistance() {
        return 5;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public double getMinX() {
        return x + 3 / 16f;
    }

    @Override
    public double getMinZ() {
        return z + 3 / 16f;
    }

    @Override
    public double getMaxX() {
        return x + 1 - 3 / 16f;
    }

    @Override
    public double getMaxY() {
        return y + 10 / 16f;
    }

    @Override
    public double getMaxZ() {
        return z + 1 - 3 / 16f;
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GRAY_BLOCK_COLOR;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        setDirection(player != null ? player.getDirection().getOpposite().getHorizontalIndex() : 0);

        if (!super.place(item, block, target, face, fx, fy, fz, player)) {
            return false;
        }

        level.scheduleRandomUpdate(this, 6000);
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            int rehydration = getRehydrationLevel();
            Block extra = level.getExtraBlock(this);
            if (extra.isWaterSource()) {
                if (rehydration >= MAX_REHYDRATION_LEVEL) {
                    level.setExtraBlock(this, get(AIR), true, false);
                    level.setBlock(this, extra, true);

                    new EntityHappyGhast(getChunk(), Entity.getDefaultNBT(add(0.5, 0, 0.5))
                            .putBoolean("IsBaby", true)
                            .putFloat("Scale", 0.2375f))
                            .spawnToAll();
                    return Level.BLOCK_UPDATE_NORMAL;
                }

                setRehydrationLevel(rehydration + 1);
                level.setBlock(this, this, true);
            } else if (rehydration > 0) {
                setRehydrationLevel(rehydration - 1);
                level.setBlock(this, this, true);
            }
            level.scheduleRandomUpdate(this, 6000);
            return type;
        }
        return 0;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(getDirection());
    }

    public int getDirection() {
        return getDamage() & DIRECTION_MASK;
    }

    public void setDirection(int direction) {
        setDamage(getDamage() & ~DIRECTION_MASK | direction & DIRECTION_MASK);
    }

    public int getRehydrationLevel() {
        return (getDamage() & REHYDRATION_LEVEL_MASK) >> REHYDRATION_LEVEL_OFFSET;
    }

    public void setRehydrationLevel(int level) {
        setDamage(getDamage() & ~REHYDRATION_LEVEL_MASK | (level & REHYDRATION_LEVEL_MASK) << REHYDRATION_LEVEL_OFFSET);
    }
}
