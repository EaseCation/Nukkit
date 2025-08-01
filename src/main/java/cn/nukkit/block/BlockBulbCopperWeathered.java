package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockBulbCopperWeathered extends BlockBulbCopper {
    public BlockBulbCopperWeathered() {
        this(0);
    }

    public BlockBulbCopperWeathered(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WEATHERED_COPPER_BULB;
    }

    @Override
    public String getName() {
        return "Weathered Copper Bulb";
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
    public int getWaxedBlockId() {
        return WAXED_WEATHERED_COPPER_BULB;
    }

    @Override
    public int getIncrementAgeBlockId() {
        return OXIDIZED_COPPER_BULB;
    }

    @Override
    public int getDecrementAgeBlockId() {
        return EXPOSED_COPPER_BULB;
    }
}
