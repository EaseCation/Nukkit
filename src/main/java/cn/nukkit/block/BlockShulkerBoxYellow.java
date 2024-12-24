package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockShulkerBoxYellow extends BlockShulkerBox {

    public BlockShulkerBoxYellow() {
    }

    @Override
    public int getId() {
        return YELLOW_SHULKER_BOX;
    }

    @Override
    public String getName() {
        return "Yellow Shulker Box";
    }

    @Override
    public BlockColor getColor() {
        return getDyeColor().getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.YELLOW;
    }
}
