package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabDarkPrismarine extends BlockSlabStone {
    BlockSlabDarkPrismarine() {

    }

    @Override
    public int getId() {
        return DARK_PRISMARINE_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Dark Prismarine Slab" : "Dark Prismarine Slab";
    }

    @Override
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIAMOND_BLOCK_COLOR;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return DARK_PRISMARINE_DOUBLE_SLAB;
    }

    @Override
    public String getDescriptionId() {
        return "tile.stone_slab2.prismarine.dark.name";
    }
}
