package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockShulkerBoxBlue extends BlockShulkerBox {

    BlockShulkerBoxBlue() {

    }

    @Override
    public int getId() {
        return BLUE_SHULKER_BOX;
    }

    @Override
    public String getName() {
        return "Blue Shulker Box";
    }

    @Override
    public BlockColor getColor() {
        return getDyeColor().getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BLUE;
    }
}
