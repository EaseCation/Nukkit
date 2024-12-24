package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockShulkerBoxBrown extends BlockShulkerBox {

    public BlockShulkerBoxBrown() {
    }

    @Override
    public int getId() {
        return BROWN_SHULKER_BOX;
    }

    @Override
    public String getName() {
        return "Brown Shulker Box";
    }

    @Override
    public BlockColor getColor() {
        return getDyeColor().getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BROWN;
    }
}
