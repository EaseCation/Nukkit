package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockCarpetYellow extends BlockCarpet {
    BlockCarpetYellow() {

    }

    @Override
    public int getId() {
        return YELLOW_CARPET;
    }

    @Override
    public String getName() {
        return "Yellow Carpet";
    }

    @Override
    public BlockColor getColor() {
        return DyeColor.YELLOW.getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.YELLOW;
    }
}
