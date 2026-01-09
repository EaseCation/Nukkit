package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockLanternCopperOxidized extends BlockLanternCopper {
    BlockLanternCopperOxidized() {

    }

    @Override
    public int getId() {
        return OXIDIZED_COPPER_LANTERN;
    }

    @Override
    public String getName() {
        return "Oxidized Copper Lantern";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WARPED_NYLIUM_BLOCK_COLOR;
    }

    @Override
    public int getCopperAge() {
        return 3;
    }

    @Override
    public int getWaxedBlockId() {
        return WAXED_OXIDIZED_COPPER_LANTERN;
    }

    @Override
    public int getIncrementAgeBlockId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getDecrementAgeBlockId() {
        return WEATHERED_COPPER_LANTERN;
    }
}
