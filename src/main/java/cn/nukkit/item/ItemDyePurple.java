package cn.nukkit.item;

import cn.nukkit.utils.DyeColor;

public class ItemDyePurple extends ItemDye {
    public ItemDyePurple() {
        this(0, 1);
    }

    public ItemDyePurple(Integer meta) {
        this(meta, 1);
    }

    public ItemDyePurple(Integer meta, int count) {
        super(PURPLE_DYE, meta, count, "Purple Dye");
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.PURPLE;
    }
}
