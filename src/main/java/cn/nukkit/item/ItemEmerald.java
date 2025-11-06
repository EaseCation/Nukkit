package cn.nukkit.item;

import cn.nukkit.item.armortrim.TrimMaterialNames;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemEmerald extends Item {

    public ItemEmerald() {
        this(0, 1);
    }

    public ItemEmerald(Integer meta) {
        this(meta, 1);
    }

    public ItemEmerald(Integer meta, int count) {
        super(EMERALD, meta, count, "Emerald");
    }

    @Override
    public float getFurnaceXpMultiplier() {
        return 1;
    }

    @Override
    public String getTrimMaterialName() {
        return TrimMaterialNames.EMERALD;
    }
}
