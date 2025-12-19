package cn.nukkit.item;

import cn.nukkit.utils.DyeColor;

public class ItemDyeGray extends ItemDye {
    public ItemDyeGray() {
        this(0, 1);
    }

    public ItemDyeGray(Integer meta) {
        this(meta, 1);
    }

    public ItemDyeGray(Integer meta, int count) {
        super(GRAY_DYE, meta, count, "Gray Dye");
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.GRAY;
    }
}
