package cn.nukkit.item;

import cn.nukkit.item.armortrim.TrimMaterialNames;

public class ItemIngotCopper extends Item {
    public ItemIngotCopper() {
        this(0, 1);
    }

    public ItemIngotCopper(Integer meta) {
        this(meta, 1);
    }

    public ItemIngotCopper(Integer meta, int count) {
        super(COPPER_INGOT, 0, count, "Copper Ingot");
    }

    @Override
    public float getFurnaceXpMultiplier() {
        return 0.7f;
    }

    @Override
    public String getTrimMaterialName() {
        return TrimMaterialNames.COPPER;
    }
}
