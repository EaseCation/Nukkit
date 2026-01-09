package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockCarpetOrange extends BlockCarpet {
    BlockCarpetOrange() {

    }

    @Override
    public int getId() {
        return ORANGE_CARPET;
    }

    @Override
    public String getName() {
        return "Orange Carpet";
    }

    @Override
    public BlockColor getColor() {
        return DyeColor.ORANGE.getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.ORANGE;
    }
}
