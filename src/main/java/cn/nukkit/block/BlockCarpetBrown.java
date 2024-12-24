package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockCarpetBrown extends BlockCarpet {
    public BlockCarpetBrown() {
    }

    @Override
    public int getId() {
        return BROWN_CARPET;
    }

    @Override
    public String getName() {
        return "Brown Carpet";
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
