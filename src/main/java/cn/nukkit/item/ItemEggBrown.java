package cn.nukkit.item;

import cn.nukkit.entity.property.EntityPropertyNames;
import cn.nukkit.entity.property.EntityPropertyStringValues;
import cn.nukkit.nbt.tag.CompoundTag;

public class ItemEggBrown extends ItemEgg {
    public ItemEggBrown() {
        this(0, 1);
    }

    public ItemEggBrown(Integer meta) {
        this(meta, 1);
    }

    public ItemEggBrown(Integer meta, int count) {
        super(BROWN_EGG, meta, count, "Brown Egg");
    }

    @Override
    protected void correctNBT(CompoundTag nbt) {
        nbt.putCompound("properties", new CompoundTag()
                .putString(EntityPropertyNames.CLIMATE_VARIANT, EntityPropertyStringValues.CLIMATE_VARIANT_WARM));
    }
}
