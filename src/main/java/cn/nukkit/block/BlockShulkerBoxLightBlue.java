package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockShulkerBoxLightBlue extends BlockShulkerBox {

    public BlockShulkerBoxLightBlue() {
    }

    @Override
    public int getId() {
        return LIGHT_BLUE_SHULKER_BOX;
    }

    @Override
    public String getName() {
        return "Light Blue Shulker Box";
    }

    @Override
    public BlockColor getColor() {
        return getDyeColor().getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.LIGHT_BLUE;
    }
}
