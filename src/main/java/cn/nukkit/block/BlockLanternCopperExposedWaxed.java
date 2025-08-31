package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockLanternCopperExposedWaxed extends BlockLanternCopperWaxed {
    public BlockLanternCopperExposedWaxed() {
        this(0);
    }

    public BlockLanternCopperExposedWaxed(int meta) {
        super(meta);

    }

    @Override
    public int getId() {
        return WAXED_EXPOSED_COPPER_LANTERN;
    }

    @Override
    public String getName() {
        return "Waxed Exposed Copper Lantern";
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
    public int getIncrementAgeBlockId() {
        return WAXED_WEATHERED_COPPER_LANTERN;
    }

    @Override
    public int getDecrementAgeBlockId() {
        return WAXED_COPPER_LANTERN;
    }

    @Override
    public int getDewaxedBlockId() {
        return EXPOSED_COPPER_LANTERN;
    }
}
