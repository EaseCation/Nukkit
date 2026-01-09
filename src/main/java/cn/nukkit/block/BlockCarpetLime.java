package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockCarpetLime extends BlockCarpet {
    BlockCarpetLime() {

    }

    @Override
    public int getId() {
        return LIME_CARPET;
    }

    @Override
    public String getName() {
        return "Lime Carpet";
    }

    @Override
    public BlockColor getColor() {
        return DyeColor.LIME.getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.LIME;
    }
}
