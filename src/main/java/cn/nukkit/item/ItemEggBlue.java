package cn.nukkit.item;

import cn.nukkit.entity.property.EntityPropertyNames;
import cn.nukkit.entity.property.EntityPropertyStringValues;
import cn.nukkit.nbt.tag.CompoundTag;

public class ItemEggBlue extends ItemEgg {
    public ItemEggBlue() {
        this(0, 1);
    }

    public ItemEggBlue(Integer meta) {
        this(meta, 1);
    }

    public ItemEggBlue(Integer meta, int count) {
        super(BLUE_EGG, meta, count, "Blue Egg");
    }

    @Override
    protected void correctNBT(CompoundTag nbt) {
        nbt.putCompound("properties", new CompoundTag()
                .putString(EntityPropertyNames.CLIMATE_VARIANT, EntityPropertyStringValues.CLIMATE_VARIANT_COLD));
    }
}
