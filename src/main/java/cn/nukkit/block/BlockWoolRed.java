package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockWoolRed extends BlockWool {
    public BlockWoolRed() {
    }

    @Override
    public String getName() {
        return "Red Wool";
    }

    @Override
    public int getId() {
        return RED_WOOL;
    }

    @Override
    public BlockColor getColor() {
        return DyeColor.RED.getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.RED;
    }
}
