package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockStairsMangrove extends BlockStairsWood {
    BlockStairsMangrove() {

    }

    @Override
    public int getId() {
        return MANGROVE_STAIRS;
    }

    @Override
    public String getName() {
        return "Mangrove Stairs";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }
}
