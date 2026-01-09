package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockShulkerBoxOrange extends BlockShulkerBox {

    BlockShulkerBoxOrange() {

    }

    @Override
    public int getId() {
        return ORANGE_SHULKER_BOX;
    }

    @Override
    public String getName() {
        return "Orange Shulker Box";
    }

    @Override
    public BlockColor getColor() {
        return getDyeColor().getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.ORANGE;
    }
}
