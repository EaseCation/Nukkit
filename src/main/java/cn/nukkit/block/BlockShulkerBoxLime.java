package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockShulkerBoxLime extends BlockShulkerBox {

    BlockShulkerBoxLime() {

    }

    @Override
    public int getId() {
        return LIME_SHULKER_BOX;
    }

    @Override
    public String getName() {
        return "Lime Shulker Box";
    }

    @Override
    public BlockColor getColor() {
        return getDyeColor().getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.LIME;
    }
}
