package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockWoolWhite extends BlockWool {
    public BlockWoolWhite() {
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
    public BlockColor getColor() {
        return DyeColor.WHITE.getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.WHITE;
    }
}
