package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockLogStrippedOak extends BlockLogStripped {

    public BlockLogStrippedOak() {
        this(0);
    }

    public BlockLogStrippedOak(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STRIPPED_OAK_LOG;
    }

    @Override
    public String getName() {
        return "Stripped Oak Log";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }
}
