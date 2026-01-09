package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockWoolWhite extends BlockWool {
    BlockWoolWhite() {

    }

    @Override
    public String getName() {
        return "White Wool";
    }

    @Override
    public int getId() {
        return WHITE_WOOL;
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.WHITE;
    }
}
