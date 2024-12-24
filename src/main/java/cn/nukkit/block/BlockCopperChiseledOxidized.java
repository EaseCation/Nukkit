package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCopperChiseledOxidized extends BlockCopperChiseled {
    public BlockCopperChiseledOxidized() {
    }

    @Override
    public int getId() {
        return OXIDIZED_CHISELED_COPPER;
    }

    @Override
    public String getName() {
        return "Oxidized Chiseled Copper";
    }

    @Override
    public int onUpdate(int type) {
        return 0;
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
        return WAXED_OXIDIZED_CHISELED_COPPER;
    }

    @Override
    public int getIncrementAgeBlockId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getDecrementAgeBlockId() {
        return WEATHERED_CHISELED_COPPER;
    }
}
