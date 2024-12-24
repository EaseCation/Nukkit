package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockMossPale extends BlockMoss {
    public BlockMossPale() {
    }

    @Override
    public int getId() {
        return PALE_MOSS_BLOCK;
    }

    @Override
    public String getName() {
        return "Pale Moss Block";
    }

    @Override
    public int getBurnChance() {
        return 15;
    }

    @Override
    public int getBurnAbility() {
        return 100;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.LIGHT_GRAY_BLOCK_COLOR;
    }
}
