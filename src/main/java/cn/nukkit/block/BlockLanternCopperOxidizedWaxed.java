package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockLanternCopperOxidizedWaxed extends BlockLanternCopperWaxed {
    BlockLanternCopperOxidizedWaxed() {

    }

    @Override
    public int getId() {
        return WAXED_OXIDIZED_COPPER_LANTERN;
    }

    @Override
    public String getName() {
        return "Waxed Oxidized Copper Lantern";
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
    public int getIncrementAgeBlockId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getDecrementAgeBlockId() {
        return WAXED_WEATHERED_COPPER_LANTERN;
    }

    @Override
    public int getDewaxedBlockId() {
        return OXIDIZED_COPPER_LANTERN;
    }
}
