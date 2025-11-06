package cn.nukkit.item;

import cn.nukkit.item.armortrim.TrimMaterialNames;

public class ItemIngotNetherite extends Item {

    public ItemIngotNetherite() {
        this(0, 1);
    }

    public ItemIngotNetherite(Integer meta) {
        this(meta, 1);
    }

    public ItemIngotNetherite(Integer meta, int count) {
        super(NETHERITE_INGOT, meta, count, "Netherite Ingot");
    }

    @Override
    public boolean isFireResistant() {
        return true;
    }

    @Override
    public float getFurnaceXpMultiplier() {
        return 2;
    }

    @Override
    public String getTrimMaterialName() {
        return TrimMaterialNames.NETHERITE;
    }
}
