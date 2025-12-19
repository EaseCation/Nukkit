package cn.nukkit.item;

import cn.nukkit.utils.DyeColor;

public class ItemDyeLightGray extends ItemDye {
    public ItemDyeLightGray() {
        this(0, 1);
    }

    public ItemDyeLightGray(Integer meta) {
        this(meta, 1);
    }

    public ItemDyeLightGray(Integer meta, int count) {
        super(LIGHT_GRAY_DYE, meta, count, "Light Gray Dye");
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.LIGHT_GRAY;
    }
}
