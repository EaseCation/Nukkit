package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockLogStrippedPaleOak extends BlockLogStripped {
    public BlockLogStrippedPaleOak() {
        this(0);
    }

    public BlockLogStrippedPaleOak(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STRIPPED_PALE_OAK_LOG;
    }

    @Override
    public String getName() {
        return "Stripped Pale Oak Log";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.QUARTZ_BLOCK_COLOR;
    }
}
