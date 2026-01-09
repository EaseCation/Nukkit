package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockLogStrippedOak extends BlockLogStripped {

    BlockLogStrippedOak() {

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
