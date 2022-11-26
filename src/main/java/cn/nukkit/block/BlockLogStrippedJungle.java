package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockLogStrippedJungle extends BlockLogStripped {

    public BlockLogStrippedJungle() {
        this(0);
    }

    public BlockLogStrippedJungle(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STRIPPED_JUNGLE_LOG;
    }

    @Override
    public String getName() {
        return "Stripped Jungle Log";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }
}
