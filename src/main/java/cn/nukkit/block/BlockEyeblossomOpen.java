package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockEyeblossomOpen extends BlockFlower {
    BlockEyeblossomOpen() {

    }

    @Override
    public int getId() {
        return OPEN_EYEBLOSSOM;
    }

    @Override
    public String getName() {
        return "Open Eyeblossom";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }
}
