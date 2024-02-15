package cn.nukkit.block;

import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class BlockNetherVinesTwisting extends BlockNetherVines {
    public BlockNetherVinesTwisting() {
        this(0);
    }

    public BlockNetherVinesTwisting(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return TWISTING_VINES;
    }

    @Override
    public String getName() {
        return "Twisting Vines";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CYAN_BLOCK_COLOR;
    }

    @Override
    protected boolean canSurvive() {
        Block below = down();
        int id = below.getId();
        return id == TWISTING_VINES || SupportType.hasFullSupport(below, BlockFace.UP);
    }

    @Override
    protected void onFertilized() {
        Block head = this;
        Block above;
        while ((above = head.up()).getId() == TWISTING_VINES) {
            head = above;
        }

        int maxHeight = level.getHeightRange().getMaxY() - 1;
        int headY = head.getFloorY();
        if (headY >= maxHeight) {
            return;
        }

        int age = getDamage();
        int x = head.getFloorX();
        int z = head.getFloorZ();
        int y = headY;
        float probability = 1;
        Random random = ThreadLocalRandom.current();

        while (++y < maxHeight && level.getBlock(x, y, z).isAir() && random.nextFloat() < probability) {
            age = Math.min(age + 1, MAX_AGE);
            level.setBlock(x, y, z, get(TWISTING_VINES, age), true, true);
            probability *= FERTILIZER_GROW_PROBABILITY_DECREASE_RATE;
        }
    }

    @Override
    protected BlockFace getGrowDirection() {
        return BlockFace.UP;
    }
}
