package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockShulkerBoxLightGray extends BlockShulkerBox {

    public BlockShulkerBoxLightGray() {
    }

    @Override
    public int getId() {
        return LIGHT_GRAY_SHULKER_BOX;
    }

    @Override
    public String getName() {
        return "Light Gray Shulker Box";
    }

    @Override
    public BlockColor getColor() {
        return getDyeColor().getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.LIGHT_GRAY;
    }
}
