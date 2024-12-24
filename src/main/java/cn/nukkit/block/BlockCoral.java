package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockFace.Plane;

import java.util.concurrent.ThreadLocalRandom;

public abstract class BlockCoral extends BlockFlowable {
    public static final int[] CORALS = {
            TUBE_CORAL,
            BRAIN_CORAL,
            BUBBLE_CORAL,
            FIRE_CORAL,
            HORN_CORAL,
    };
    public static final int[] DEAD_CORALS = {
            DEAD_TUBE_CORAL,
            DEAD_BRAIN_CORAL,
            DEAD_BUBBLE_CORAL,
            DEAD_FIRE_CORAL,
            DEAD_HORN_CORAL,
    };

    public static final int DEAD_BIT = 0b1000;

    public static final int BLUE = 0;
    public static final int PINK = 1;
    public static final int PURPLE = 2;
    public static final int RED = 3;
    public static final int YELLOW = 4;

    protected BlockCoral() {
        super(0);
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        return new Item[0];
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        Block extra;
        if (block.isLava() || block.isWater() && !block.isFullLiquid()
                || !block.isAir() && (extra = level.getExtraBlock(this)).isWater() && !extra.isFullLiquid()
                || !SupportType.hasCenterSupport(down(), BlockFace.UP)) {
            return false;
        }

        if (!super.place(item, block, target, face, fx, fy, fz, player)) {
            return false;
        }

        if (!isDead()) {
            level.scheduleRandomUpdate(this, ThreadLocalRandom.current().nextInt(40, 50));
        }
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!SupportType.hasCenterSupport(down(), BlockFace.UP)) {
                level.useBreakOn(this, true);
                return type;
            }

            if (isDead()) {
                return 0;
            }

            level.scheduleRandomUpdate(this, ThreadLocalRandom.current().nextInt(40, 50));
            return type;
        }

        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (level.getExtraBlock(this).isWaterSource()) {
                return 0;
            }

            for (BlockFace face : Plane.HORIZONTAL) {
                Block block = getSide(face);
                if (block.isWater() || level.getExtraBlock(block).isWater()) {
                    return 0;
                }
            }

            level.setBlock(this, get(getDeadBlockId()), true);
            return type;
        }

        return 0;
    }

    @Override
    public boolean isVegetation() {
        return true;
    }

    @Override
    public boolean isCoral() {
        return true;
    }

    protected boolean isDead() {
        return false;
    }

    protected int getDeadBlockId() {
        return getId();
    }
}
