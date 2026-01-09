package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCopperChiseledOxidizedWaxed extends BlockCopperChiseledWaxed {
    BlockCopperChiseledOxidizedWaxed() {

    }

    @Override
    public int getId() {
        return WAXED_OXIDIZED_CHISELED_COPPER;
    }

    @Override
    public String getName() {
        return "Waxed Oxidized Chiseled Copper";
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
        return WAXED_WEATHERED_CHISELED_COPPER;
    }

    @Override
    public int getDewaxedBlockId() {
        return OXIDIZED_CHISELED_COPPER;
    }
}
