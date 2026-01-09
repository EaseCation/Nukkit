package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockCarpetPink extends BlockCarpet {
    BlockCarpetPink() {

    }

    @Override
    public int getId() {
        return PINK_CARPET;
    }

    @Override
    public String getName() {
        return "Pink Carpet";
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
