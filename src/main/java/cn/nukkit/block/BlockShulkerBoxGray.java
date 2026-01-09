package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockShulkerBoxGray extends BlockShulkerBox {

    BlockShulkerBoxGray() {

    }

    @Override
    public int getId() {
        return GRAY_SHULKER_BOX;
    }

    @Override
    public String getName() {
        return "Gray Shulker Box";
    }

    @Override
    public BlockColor getColor() {
        return getDyeColor().getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.GRAY;
    }
}
