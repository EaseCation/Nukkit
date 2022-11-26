package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockLogStrippedDarkOak extends BlockLogStripped {

    public BlockLogStrippedDarkOak() {
        this(0);
    }

    public BlockLogStrippedDarkOak(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STRIPPED_DARK_OAK_LOG;
    }

    @Override
    public String getName() {
        return "Stripped Dark Oak Log";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BROWN_BLOCK_COLOR;
    }
}
