package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockWoolMagenta extends BlockWool {
    public BlockWoolMagenta() {
    }

    @Override
    public String getName() {
        return "Magenta Wool";
    }

    @Override
    public int getId() {
        return MAGENTA_WOOL;
    }

    @Override
    public BlockColor getColor() {
        return DyeColor.MAGENTA.getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.MAGENTA;
    }
}
