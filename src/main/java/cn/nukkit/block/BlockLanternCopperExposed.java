package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockLanternCopperExposed extends BlockLanternCopper {
    public BlockLanternCopperExposed() {
        this(0);
    }

    public BlockLanternCopperExposed(int meta) {
        super(meta);

    }

    @Override
    public int getId() {
        return EXPOSED_COPPER_LANTERN;
    }

    @Override
    public String getName() {
        return "Exposed Copper Lantern";
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
    public int getWaxedBlockId() {
        return WAXED_EXPOSED_COPPER_LANTERN;
    }

    @Override
    public int getIncrementAgeBlockId() {
        return WEATHERED_COPPER_LANTERN;
    }

    @Override
    public int getDecrementAgeBlockId() {
        return COPPER_LANTERN;
    }
}
