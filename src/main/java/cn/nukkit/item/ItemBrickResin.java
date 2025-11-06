package cn.nukkit.item;

import cn.nukkit.item.armortrim.TrimMaterialNames;

public class ItemBrickResin extends Item {
    public ItemBrickResin() {
        this(0, 1);
    }

    public ItemBrickResin(Integer meta) {
        this(meta, 1);
    }

    public ItemBrickResin(Integer meta, int count) {
        super(RESIN_BRICK, meta, count, "Resin Brick");
    }

    @Override
    public float getFurnaceXpMultiplier() {
        return 0.1f;
    }

    @Override
    public String getTrimMaterialName() {
        return TrimMaterialNames.RESIN;
    }
}
