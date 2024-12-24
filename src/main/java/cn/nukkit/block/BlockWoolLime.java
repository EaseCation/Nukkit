package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockWoolLime extends BlockWool {
    public BlockWoolLime() {
    }

    @Override
    public String getName() {
        return "Lime Wool";
    }

    @Override
    public int getId() {
        return LIME_WOOL;
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
