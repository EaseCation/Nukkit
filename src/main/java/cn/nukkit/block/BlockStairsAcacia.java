package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockStairsAcacia extends BlockStairsWood {

    BlockStairsAcacia() {

    }

    @Override
    public int getId() {
        return ACACIA_STAIRS;
    }

    @Override
    public String getName() {
        return "Acacia Stairs";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }

}
