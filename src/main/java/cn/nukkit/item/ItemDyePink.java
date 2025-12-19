package cn.nukkit.item;

import cn.nukkit.utils.DyeColor;

public class ItemDyePink extends ItemDye {
    public ItemDyePink() {
        this(0, 1);
    }

    public ItemDyePink(Integer meta) {
        this(meta, 1);
    }

    public ItemDyePink(Integer meta, int count) {
        super(PINK_DYE, meta, count, "Pink Dye");
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.PINK;
    }
}
