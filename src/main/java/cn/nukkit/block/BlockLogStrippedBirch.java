package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockLogStrippedBirch extends BlockLogStripped {

    public BlockLogStrippedBirch() {
        this(0);
    }

    public BlockLogStrippedBirch(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STRIPPED_BIRCH_LOG;
    }

    @Override
    public String getName() {
        return "Stripped Birch Log";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }
}
