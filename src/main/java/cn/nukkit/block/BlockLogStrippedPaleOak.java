package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockLogStrippedPaleOak extends BlockLogStripped {
    BlockLogStrippedPaleOak() {

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
