package cn.nukkit.item;

import cn.nukkit.utils.DyeColor;

public class ItemDyeGreen extends ItemDye {
    public ItemDyeGreen() {
        this(0, 1);
    }

    public ItemDyeGreen(Integer meta) {
        this(meta, 1);
    }

    public ItemDyeGreen(Integer meta, int count) {
        super(GREEN_DYE, meta, count, "Green Dye");
    }

    @Override
    public float getFurnaceXpMultiplier() {
        return 0.2f;
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.GREEN;
    }
}
