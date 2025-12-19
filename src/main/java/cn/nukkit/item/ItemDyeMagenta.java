package cn.nukkit.item;

import cn.nukkit.utils.DyeColor;

public class ItemDyeMagenta extends ItemDye {
    public ItemDyeMagenta() {
        this(0, 1);
    }

    public ItemDyeMagenta(Integer meta) {
        this(meta, 1);
    }

    public ItemDyeMagenta(Integer meta, int count) {
        super(MAGENTA_DYE, meta, count, "Magenta Dye");
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.MAGENTA;
    }
}
