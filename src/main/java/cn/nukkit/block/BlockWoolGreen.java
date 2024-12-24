package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockWoolGreen extends BlockWool {
    public BlockWoolGreen() {
    }

    @Override
    public String getName() {
        return "Green Wool";
    }

    @Override
    public int getId() {
        return GREEN_WOOL;
    }

    @Override
    public BlockColor getColor() {
        return DyeColor.GREEN.getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.GREEN;
    }
}
