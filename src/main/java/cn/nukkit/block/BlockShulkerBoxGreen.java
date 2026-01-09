package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockShulkerBoxGreen extends BlockShulkerBox {

    BlockShulkerBoxGreen() {

    }

    @Override
    public int getId() {
        return GREEN_SHULKER_BOX;
    }

    @Override
    public String getName() {
        return "Green Shulker Box";
    }

    @Override
    public BlockColor getColor() {
        return getDyeColor().getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.GREEN;
    }
}
