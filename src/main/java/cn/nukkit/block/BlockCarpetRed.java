package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockCarpetRed extends BlockCarpet {
    public BlockCarpetRed() {
    }

    @Override
    public int getId() {
        return RED_CARPET;
    }

    @Override
    public String getName() {
        return "Red Carpet";
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
