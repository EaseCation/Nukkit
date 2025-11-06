package cn.nukkit.item;

import cn.nukkit.item.armortrim.TrimMaterialNames;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemQuartz extends Item {

    public ItemQuartz() {
        this(0, 1);
    }

    public ItemQuartz(Integer meta) {
        this(meta, 1);
    }

    public ItemQuartz(Integer meta, int count) {
        super(QUARTZ, meta, count, "Nether Quartz");
    }

    @Override
    public float getFurnaceXpMultiplier() {
        return 0.2f;
    }

    @Override
    public String getTrimMaterialName() {
        return TrimMaterialNames.QUARTZ;
    }
}
