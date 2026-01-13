package cn.nukkit.item;

import cn.nukkit.item.armortrim.TrimMaterialNames;

public class ItemLapisLazuli extends Item {
    public ItemLapisLazuli() {
        this(0, 1);
    }

    public ItemLapisLazuli(Integer meta) {
        this(meta, 1);
    }

    public ItemLapisLazuli(Integer meta, int count) {
        super(LAPIS_LAZULI, meta, count, "Lapis Lazuli");
    }

    @Override
    public String getDescriptionId() {
        return "item.dye.blue.name";
    }

    @Override
    public float getFurnaceXpMultiplier() {
        return 0.2f;
    }

    @Override
    public String getTrimMaterialName() {
        return TrimMaterialNames.LAPIS;
    }
}
