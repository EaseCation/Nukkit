package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockCarpetBlack extends BlockCarpet {
    BlockCarpetBlack() {

    }

    @Override
    public int getId() {
        return BLACK_CARPET;
    }

    @Override
    public String getName() {
        return "Black Carpet";
    }

    @Override
    public BlockColor getColor() {
        return DyeColor.BLACK.getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BLACK;
    }
}
