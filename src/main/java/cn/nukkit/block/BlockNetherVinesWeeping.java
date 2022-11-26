package cn.nukkit.block;

import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class BlockNetherVinesWeeping extends BlockNetherVines {
    public BlockNetherVinesWeeping() {
        this(0);
    }

    public BlockNetherVinesWeeping(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WEEPING_VINES;
    }

    @Override
    public String getName() {
        return "Weeping Vines";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.NETHER_BLOCK_COLOR;
    }

    @Override
    protected boolean canSurvive() {
        Block above = up();
        int id = above.getId();
        return id == WEEPING_VINES || SupportType.hasFullSupport(above, BlockFace.DOWN);
    }

    @Override
    protected void onFertilized() {
        Block head = this;
        Block below;
        while ((below = head.down()).getId() == WEEPING_VINES) {
            head = below;
        }

        int minHeight = level.getMinHeight();
        int headY = head.getFloorY();
        if (headY <= minHeight) {
            return;
        }

        int age = getDamage();
        int x = head.getFloorX();
        int z = head.getFloorZ();
        int y = headY;
        float probability = 1;
        Random random = ThreadLocalRandom.current();

        while (--y > minHeight && level.getBlock(x, y, z).isAir() && random.nextFloat() < probability) {
            age = Math.min(age + 1, MAX_AGE);
            level.setBlock(x, y, z, get(WEEPING_VINES, age), true, true);
            probability *= FERTILIZER_GROW_PROBABILITY_DECREASE_RATE;
        }
    }

    @Override
    protected BlockFace getGrowDirection() {
        return BlockFace.DOWN;
    }
}
