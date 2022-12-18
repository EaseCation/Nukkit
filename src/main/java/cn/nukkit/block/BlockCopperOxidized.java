package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCopperOxidized extends BlockCopper {
    public BlockCopperOxidized() {
    }

    @Override
    public int getId() {
        return OXIDIZED_COPPER;
    }

    @Override
    public String getName() {
        return "Oxidized Copper";
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
    protected int getWaxedBlockId() {
        return WAXED_OXIDIZED_COPPER;
    }

    @Override
    protected int getIncrementAgeBlockId() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected int getDecrementAgeBlockId() {
        return WEATHERED_COPPER;
    }
}
