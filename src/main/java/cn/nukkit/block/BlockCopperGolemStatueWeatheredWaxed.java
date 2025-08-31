package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCopperGolemStatueWeatheredWaxed extends BlockCopperGolemStatueWaxed {
    public BlockCopperGolemStatueWeatheredWaxed() {
        this(0);
    }

    public BlockCopperGolemStatueWeatheredWaxed(int meta) {
        super(meta);

    }

    @Override
    public int getId() {
        return WAXED_WEATHERED_COPPER_GOLEM_STATUE;
    }

    @Override
    public String getName() {
        return "Waxed Weathered Copper Golem Statue";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WARPED_STEM_BLOCK_COLOR;
    }

    @Override
    public int getCopperAge() {
        return 2;
    }

    @Override
    public int getIncrementAgeBlockId() {
        return WAXED_OXIDIZED_COPPER_GOLEM_STATUE;
    }

    @Override
    public int getDecrementAgeBlockId() {
        return WAXED_EXPOSED_COPPER_GOLEM_STATUE;
    }

    @Override
    public int getDewaxedBlockId() {
        return WEATHERED_COPPER_GOLEM_STATUE;
    }
}
