package cn.nukkit.item;

import cn.nukkit.item.armortrim.TrimMaterialNames;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemIngotIron extends Item {

    public ItemIngotIron() {
        this(0, 1);
    }

    public ItemIngotIron(Integer meta) {
        this(meta, 1);
    }

    public ItemIngotIron(Integer meta, int count) {
        super(IRON_INGOT, meta, count, "Iron Ingot");
    }

    @Override
    public float getFurnaceXpMultiplier() {
        return 0.7f;
    }

    @Override
    public String getTrimMaterialName() {
        return TrimMaterialNames.IRON;
    }
}
