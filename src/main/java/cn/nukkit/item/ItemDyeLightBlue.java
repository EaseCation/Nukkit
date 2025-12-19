package cn.nukkit.item;

import cn.nukkit.utils.DyeColor;

public class ItemDyeLightBlue extends ItemDye {
    public ItemDyeLightBlue() {
        this(0, 1);
    }

    public ItemDyeLightBlue(Integer meta) {
        this(meta, 1);
    }

    public ItemDyeLightBlue(Integer meta, int count) {
        super(LIGHT_BLUE_DYE, meta, count, "Light Blue Dye");
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.LIGHT_BLUE;
    }
}
