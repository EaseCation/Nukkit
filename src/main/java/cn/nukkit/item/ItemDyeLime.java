package cn.nukkit.item;

import cn.nukkit.utils.DyeColor;

public class ItemDyeLime extends ItemDye {
    public ItemDyeLime() {
        this(0, 1);
    }

    public ItemDyeLime(Integer meta) {
        this(meta, 1);
    }

    public ItemDyeLime(Integer meta, int count) {
        super(LIME_DYE, meta, count, "Lime Dye");
    }

    @Override
    public float getFurnaceXpMultiplier() {
        return 0.1f;
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.LIME;
    }
}
