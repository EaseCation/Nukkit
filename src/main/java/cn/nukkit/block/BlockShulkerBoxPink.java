package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockShulkerBoxPink extends BlockShulkerBox {

    BlockShulkerBoxPink() {

    }

    @Override
    public int getId() {
        return PINK_SHULKER_BOX;
    }

    @Override
    public String getName() {
        return "Pink Shulker Box";
    }

    @Override
    public BlockColor getColor() {
        return getDyeColor().getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.PINK;
    }
}
