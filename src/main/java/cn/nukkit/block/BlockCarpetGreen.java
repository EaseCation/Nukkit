package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockCarpetGreen extends BlockCarpet {
    public BlockCarpetGreen() {
    }

    @Override
    public int getId() {
        return GREEN_CARPET;
    }

    @Override
    public String getName() {
        return "Green Carpet";
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
