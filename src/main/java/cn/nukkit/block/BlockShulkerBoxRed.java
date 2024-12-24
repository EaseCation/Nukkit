package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockShulkerBoxRed extends BlockShulkerBox {

    public BlockShulkerBoxRed() {
    }

    @Override
    public int getId() {
        return RED_SHULKER_BOX;
    }

    @Override
    public String getName() {
        return "Red Shulker Box";
    }

    @Override
    public BlockColor getColor() {
        return getDyeColor().getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.RED;
    }
}
