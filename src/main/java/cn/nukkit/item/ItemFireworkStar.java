package cn.nukkit.item;

import cn.nukkit.utils.DyeColor;

public class ItemFireworkStar extends Item {

    public ItemFireworkStar() {
        this(0, 1);
    }

    public ItemFireworkStar(Integer meta) {
        this(meta, 1);
    }

    public ItemFireworkStar(Integer meta, int count) {
        super(FIREWORK_STAR, meta, count, "Firework Star");
    }

    public DyeColor getColor() {
        return DyeColor.getByDyeData(getDamage());
    }
}
