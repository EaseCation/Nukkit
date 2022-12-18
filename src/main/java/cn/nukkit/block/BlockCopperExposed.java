package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCopperExposed extends BlockCopper {
    public BlockCopperExposed() {
    }

    @Override
    public int getId() {
        return EXPOSED_COPPER;
    }

    @Override
    public String getName() {
        return "Exposed Copper";
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
    protected int getWaxedBlockId() {
        return WAXED_EXPOSED_COPPER;
    }

    @Override
    protected int getIncrementAgeBlockId() {
        return WEATHERED_COPPER;
    }

    @Override
    protected int getDecrementAgeBlockId() {
        return COPPER_BLOCK;
    }
}
