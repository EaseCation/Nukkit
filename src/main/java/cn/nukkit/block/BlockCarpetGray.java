package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockCarpetGray extends BlockCarpet {
    BlockCarpetGray() {

    }

    @Override
    public int getId() {
        return GRAY_CARPET;
    }

    @Override
    public String getName() {
        return "Gray Carpet";
    }

    @Override
    public BlockColor getColor() {
        return DyeColor.GRAY.getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.GRAY;
    }
}
