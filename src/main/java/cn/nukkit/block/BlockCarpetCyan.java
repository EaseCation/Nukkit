package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockCarpetCyan extends BlockCarpet {
    public BlockCarpetCyan() {
    }

    @Override
    public int getId() {
        return CYAN_CARPET;
    }

    @Override
    public String getName() {
        return "Cyan Carpet";
    }

    @Override
    public BlockColor getColor() {
        return DyeColor.CYAN.getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.CYAN;
    }
}
