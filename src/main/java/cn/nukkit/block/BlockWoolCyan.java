package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockWoolCyan extends BlockWool {
    BlockWoolCyan() {

    }

    @Override
    public String getName() {
        return "Cyan Wool";
    }

    @Override
    public int getId() {
        return CYAN_WOOL;
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.CYAN;
    }
}
