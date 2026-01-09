package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSaplingPaleOak extends BlockSapling {
    BlockSaplingPaleOak() {

    }

    @Override
    public int getId() {
        return PALE_OAK_SAPLING;
    }

    @Override
    public String getName() {
        return "Pale Oak Sapling";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.QUARTZ_BLOCK_COLOR;
    }

    @Override
    protected void grow() {
        //TODO
    }
}
