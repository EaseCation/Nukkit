package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockShulkerBoxCyan extends BlockShulkerBox {

    BlockShulkerBoxCyan() {

    }

    @Override
    public int getId() {
        return CYAN_SHULKER_BOX;
    }

    @Override
    public String getName() {
        return "Cyan Shulker Box";
    }

    @Override
    public BlockColor getColor() {
        return getDyeColor().getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.CYAN;
    }
}
