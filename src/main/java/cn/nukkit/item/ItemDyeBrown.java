package cn.nukkit.item;

import cn.nukkit.utils.DyeColor;

public class ItemDyeBrown extends ItemDye {
    public ItemDyeBrown() {
        this(0, 1);
    }

    public ItemDyeBrown(Integer meta) {
        this(meta, 1);
    }

    public ItemDyeBrown(Integer meta, int count) {
        super(BROWN_DYE, meta, count, "Brown Dye");
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BROWN;
    }
}
