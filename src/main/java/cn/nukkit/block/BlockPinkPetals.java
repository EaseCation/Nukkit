package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockPinkPetals extends BlockSegmentable {
    BlockPinkPetals() {

    }

    @Override
    public int getId() {
        return PINK_PETALS;
    }

    @Override
    public String getName() {
        return "Pink Petals";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PINK_BLOCK_COLOR;
    }
}
