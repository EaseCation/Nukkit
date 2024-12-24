package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockShulkerBoxPurple extends BlockShulkerBox {

    public BlockShulkerBoxPurple() {
    }

    @Override
    public int getId() {
        return PURPLE_SHULKER_BOX;
    }

    @Override
    public String getName() {
        return "Purple Shulker Box";
    }

    @Override
    public BlockColor getColor() {
        return getDyeColor().getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.PURPLE;
    }
}
