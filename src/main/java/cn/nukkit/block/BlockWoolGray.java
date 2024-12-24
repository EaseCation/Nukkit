package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockWoolGray extends BlockWool {
    public BlockWoolGray() {
    }

    @Override
    public String getName() {
        return "Gray Wool";
    }

    @Override
    public int getId() {
        return GRAY_WOOL;
    }

    @Override
    public BlockColor getColor() {
        return DyeColor.GRAY.getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.GRAY;
    }
}
