package cn.nukkit.item;

import cn.nukkit.utils.DyeColor;

public class ItemDyeRed extends ItemDye {
    public ItemDyeRed() {
        this(0, 1);
    }

    public ItemDyeRed(Integer meta) {
        this(meta, 1);
    }

    public ItemDyeRed(Integer meta, int count) {
        super(RED_DYE, meta, count, "Red Dye");
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.RED;
    }
}
