package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockCarpetPurple extends BlockCarpet {
    BlockCarpetPurple() {

    }

    @Override
    public int getId() {
        return PURPLE_CARPET;
    }

    @Override
    public String getName() {
        return "Purple Carpet";
    }

    @Override
    public BlockColor getColor() {
        return DyeColor.PURPLE.getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.PURPLE;
    }
}
