package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockPrismarineDark extends BlockPrismarine {
    BlockPrismarineDark() {

    }

    @Override
    public int getId() {
        return DARK_PRISMARINE;
    }

    @Override
    public String getName() {
        return "Dark Prismarine";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIAMOND_BLOCK_COLOR;
    }

    @Override
    public String getDescriptionId() {
        return "tile.prismarine.dark.name";
    }
}
