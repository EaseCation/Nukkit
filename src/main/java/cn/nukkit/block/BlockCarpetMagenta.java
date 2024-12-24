package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockCarpetMagenta extends BlockCarpet {
    public BlockCarpetMagenta() {
    }

    @Override
    public int getId() {
        return MAGENTA_CARPET;
    }

    @Override
    public String getName() {
        return "Magenta Carpet";
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
