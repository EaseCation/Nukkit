package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockCarpetBlue extends BlockCarpet {
    BlockCarpetBlue() {

    }

    @Override
    public int getId() {
        return BLUE_CARPET;
    }

    @Override
    public String getName() {
        return "Blue Carpet";
    }

    @Override
    public BlockColor getColor() {
        return DyeColor.BLUE.getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BLUE;
    }
}
