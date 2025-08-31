package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockLanternCopperWeathered extends BlockLanternCopper {
    public BlockLanternCopperWeathered() {
        this(0);
    }

    public BlockLanternCopperWeathered(int meta) {
        super(meta);

    }

    @Override
    public int getId() {
        return WEATHERED_COPPER_LANTERN;
    }

    @Override
    public String getName() {
        return "Weathered Copper Lantern";
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
        return WAXED_WEATHERED_COPPER_LANTERN;
    }

    @Override
    public int getIncrementAgeBlockId() {
        return OXIDIZED_COPPER_LANTERN;
    }

    @Override
    public int getDecrementAgeBlockId() {
        return EXPOSED_COPPER_LANTERN;
    }
}
