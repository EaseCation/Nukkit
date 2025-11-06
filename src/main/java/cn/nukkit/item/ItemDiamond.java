package cn.nukkit.item;

import cn.nukkit.item.armortrim.TrimMaterialNames;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemDiamond extends Item {

    public ItemDiamond() {
        this(0, 1);
    }

    public ItemDiamond(Integer meta) {
        this(meta, 1);
    }

    public ItemDiamond(Integer meta, int count) {
        super(DIAMOND, meta, count, "Diamond");
    }

    @Override
    public float getFurnaceXpMultiplier() {
        return 1f;
    }

    @Override
    public String getTrimMaterialName() {
        return TrimMaterialNames.DIAMOND;
    }
}
