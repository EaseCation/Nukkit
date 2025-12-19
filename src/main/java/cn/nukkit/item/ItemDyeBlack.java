package cn.nukkit.item;

import cn.nukkit.utils.DyeColor;

public class ItemDyeBlack extends ItemDye {
    public ItemDyeBlack() {
        this(0, 1);
    }

    public ItemDyeBlack(Integer meta) {
        this(meta, 1);
    }

    public ItemDyeBlack(Integer meta, int count) {
        super(BLACK_DYE, meta, count, "Black Dye");
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BLACK;
    }
}
