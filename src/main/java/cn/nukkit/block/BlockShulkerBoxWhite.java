package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockShulkerBoxWhite extends BlockShulkerBox {

    public BlockShulkerBoxWhite() {
    }

    @Override
    public int getId() {
        return WHITE_SHULKER_BOX;
    }

    @Override
    public String getName() {
        return "White Shulker Box";
    }

    @Override
    public BlockColor getColor() {
        return getDyeColor().getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.WHITE;
    }
}
