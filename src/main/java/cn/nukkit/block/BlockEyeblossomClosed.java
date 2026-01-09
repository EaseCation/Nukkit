package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockEyeblossomClosed extends BlockEyeblossomOpen {
    BlockEyeblossomClosed() {

    }

    @Override
    public int getId() {
        return CLOSED_EYEBLOSSOM;
    }

    @Override
    public String getName() {
        return "Closed Eyeblossom";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GREEN_TERRACOTA_BLOCK_COLOR;
    }
}
