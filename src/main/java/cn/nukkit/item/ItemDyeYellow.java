package cn.nukkit.item;

import cn.nukkit.utils.DyeColor;

public class ItemDyeYellow extends ItemDye {
    public ItemDyeYellow() {
        this(0, 1);
    }

    public ItemDyeYellow(Integer meta) {
        this(meta, 1);
    }

    public ItemDyeYellow(Integer meta, int count) {
        super(YELLOW_DYE, meta, count, "Yellow Dye");
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.YELLOW;
    }
}
