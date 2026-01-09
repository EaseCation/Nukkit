package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockCarpetWhite extends BlockCarpet {
    BlockCarpetWhite() {

    }

    @Override
    public int getId() {
        return WHITE_CARPET;
    }

    @Override
    public String getName() {
        return "White Carpet";
    }

    @Override
    public BlockColor getColor() {
        return DyeColor.WHITE.getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.WHITE;
    }
}
