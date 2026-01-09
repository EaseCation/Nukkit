package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockShulkerBoxBlack extends BlockShulkerBox {

    BlockShulkerBoxBlack() {

    }

    @Override
    public int getId() {
        return BLACK_SHULKER_BOX;
    }

    @Override
    public String getName() {
        return "Black Shulker Box";
    }

    @Override
    public BlockColor getColor() {
        return getDyeColor().getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BLACK;
    }
}
