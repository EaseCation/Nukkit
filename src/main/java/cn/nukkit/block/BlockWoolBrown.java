package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockWoolBrown extends BlockWool {
    public BlockWoolBrown() {
    }

    @Override
    public String getName() {
        return "Brown Wool";
    }

    @Override
    public int getId() {
        return BROWN_WOOL;
    }

    @Override
    public BlockColor getColor() {
        return DyeColor.BROWN.getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BROWN;
    }
}
