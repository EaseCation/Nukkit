package cn.nukkit.item;

import cn.nukkit.utils.DyeColor;

public class ItemDyeCyan extends ItemDye {
    public ItemDyeCyan() {
        this(0, 1);
    }

    public ItemDyeCyan(Integer meta) {
        this(meta, 1);
    }

    public ItemDyeCyan(Integer meta, int count) {
        super(CYAN_DYE, meta, count, "Cyan Dye");
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.CYAN;
    }
}
