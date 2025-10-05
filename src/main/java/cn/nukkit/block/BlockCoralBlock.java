package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.GameVersion.*;
import static cn.nukkit.SharedConstants.*;

public class BlockCoralBlock extends BlockSolid {

    public static final int COLOR_MASK = 0b111;
    public static final int DEAD_BIT = 0b1000;

    public static final int BLUE = 0;
    public static final int PINK = 1;
    public static final int PURPLE = 2;
    public static final int RED = 3;
    public static final int YELLOW = 4;

    private static final String[] NAMES = new String[]{
            "Tube Coral Block",
            "Brain Coral Block",
            "Bubble Coral Block",
            "Fire Coral Block",
            "Horn Coral Block",
            "Coral Block",
            "Coral Block",
            "Coral Block",
    };

    public BlockCoralBlock() {
        this(0);
    }

    public BlockCoralBlock(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CORAL_BLOCK;
    }

    @Override
    public boolean isStackedByData() {
        return !V1_21_0.isAvailable();
    }

    @Override
    public String getName() {
        return (isDead() ? "Dead " : "") + NAMES[getCoralColor()];
    }

    @Override
    public float getHardness() {
        if (ENABLE_BLOCK_DESTROY_SPEED_COMPATIBILITY || V1_20_30.isAvailable()) {
            return 1.5f;
        }
        return 7;
    }

    @Override
    public float getResistance() {
        return 30;
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public BlockColor getColor() {
        if (isDead()) {
            return BlockColor.GRAY_BLOCK_COLOR;
        }

        switch (getCoralColor()) {
            default:
            case BLUE:
                return BlockColor.BLUE_BLOCK_COLOR;
            case PINK:
                return BlockColor.PINK_BLOCK_COLOR;
            case PURPLE:
                return BlockColor.PURPLE_BLOCK_COLOR;
            case RED:
                return BlockColor.RED_BLOCK_COLOR;
            case YELLOW:
                return BlockColor.YELLOW_BLOCK_COLOR;
        }
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (!super.place(item, block, target, face, fx, fy, fz, player)) {
            return false;
        }

        if (!isDead()) {
            level.scheduleUpdate(this, ThreadLocalRandom.current().nextInt(40, 50));
        }
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (isDead()) {
                return 0;
            }

            level.scheduleUpdate(this, ThreadLocalRandom.current().nextInt(40, 50));
            return type;
        }

        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            for (BlockFace face : BlockFace.getValues()) {
                Block block = getSide(face);
                if (block.isWater() || level.getExtraBlock(block).isWater()) {
                    return 0;
                }
            }

            setDead(true);
            level.setBlock(this, this, true);
            return type;
        }

        return 0;
    }

    public int getCoralColor() {
        return getDamage() & COLOR_MASK;
    }

    public void setCoralColor(int color) {
        setDamage((getDamage() & ~COLOR_MASK) | (color & COLOR_MASK));
    }

    public boolean isDead() {
        return (getDamage() & DEAD_BIT) == DEAD_BIT;
    }

    public void setDead(boolean dead) {
        setDamage(dead ? getDamage() | DEAD_BIT : getDamage() & ~DEAD_BIT);
    }
}
