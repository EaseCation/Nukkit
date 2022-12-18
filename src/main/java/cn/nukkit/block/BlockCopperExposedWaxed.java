package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCopperExposedWaxed extends BlockCopperWaxed {
    public BlockCopperExposedWaxed() {
    }

    @Override
    public int getId() {
        return WAXED_EXPOSED_COPPER;
    }

    @Override
    public String getName() {
        return "Waxed Exposed Copper";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.LIGHT_GRAY_TERRACOTA_BLOCK_COLOR;
    }

    @Override
    public int getCopperAge() {
        return 1;
    }

    @Override
    protected int getIncrementAgeBlockId() {
        return WAXED_WEATHERED_COPPER;
    }

    @Override
    protected int getDecrementAgeBlockId() {
        return WAXED_COPPER;
    }

    @Override
    protected int getDewaxedBlockId() {
        return EXPOSED_COPPER;
    }
}
