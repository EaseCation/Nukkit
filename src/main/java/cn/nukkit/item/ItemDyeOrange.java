package cn.nukkit.item;

import cn.nukkit.utils.DyeColor;

public class ItemDyeOrange extends ItemDye {
    public ItemDyeOrange() {
        this(0, 1);
    }

    public ItemDyeOrange(Integer meta) {
        this(meta, 1);
    }

    public ItemDyeOrange(Integer meta, int count) {
        super(ORANGE_DYE, meta, count, "Orange Dye");
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.ORANGE;
    }
}
