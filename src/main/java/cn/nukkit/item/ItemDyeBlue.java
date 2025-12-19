package cn.nukkit.item;

import cn.nukkit.utils.DyeColor;

public class ItemDyeBlue extends ItemDye {
    public ItemDyeBlue() {
        this(0, 1);
    }

    public ItemDyeBlue(Integer meta) {
        this(meta, 1);
    }

    public ItemDyeBlue(Integer meta, int count) {
        super(BLUE_DYE, meta, count, "Blue Dye");
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BLUE;
    }
}
