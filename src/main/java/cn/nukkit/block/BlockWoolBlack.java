package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockWoolBlack extends BlockWool {
    public BlockWoolBlack() {
    }

    @Override
    public String getName() {
        return "Black Wool";
    }

    @Override
    public int getId() {
        return BLACK_WOOL;
    }

    @Override
    public BlockColor getColor() {
        return DyeColor.BLACK.getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BLACK;
    }
}
