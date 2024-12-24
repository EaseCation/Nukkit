package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockWoolPink extends BlockWool {
    public BlockWoolPink() {
    }

    @Override
    public String getName() {
        return "Pink Wool";
    }

    @Override
    public int getId() {
        return PINK_WOOL;
    }

    @Override
    public BlockColor getColor() {
        return DyeColor.PINK.getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.PINK;
    }
}
