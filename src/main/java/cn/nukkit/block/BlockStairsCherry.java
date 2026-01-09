package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockStairsCherry extends BlockStairsWood {
    BlockStairsCherry() {

    }

    @Override
    public int getId() {
        return CHERRY_STAIRS;
    }

    @Override
    public String getName() {
        return "Cherry Stairs";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WHITE_TERRACOTA_BLOCK_COLOR;
    }
}
