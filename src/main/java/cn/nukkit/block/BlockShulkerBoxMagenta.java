package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockShulkerBoxMagenta extends BlockShulkerBox {

    public BlockShulkerBoxMagenta() {
    }

    @Override
    public int getId() {
        return MAGENTA_SHULKER_BOX;
    }

    @Override
    public String getName() {
        return "Magenta Shulker Box";
    }

    @Override
    public BlockColor getColor() {
        return getDyeColor().getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.MAGENTA;
    }
}
