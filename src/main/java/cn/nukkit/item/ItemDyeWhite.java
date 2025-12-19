package cn.nukkit.item;

import cn.nukkit.utils.DyeColor;

public class ItemDyeWhite extends ItemDye {
    public ItemDyeWhite() {
        this(0, 1);
    }

    public ItemDyeWhite(Integer meta) {
        this(meta, 1);
    }

    public ItemDyeWhite(Integer meta, int count) {
        super(WHITE_DYE, meta, count, "White Dye");
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.WHITE;
    }
}
