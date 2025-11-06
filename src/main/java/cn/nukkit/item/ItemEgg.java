package cn.nukkit.item;

import cn.nukkit.entity.projectile.EntityEgg;
import cn.nukkit.entity.projectile.ProjectileFactory;
import cn.nukkit.entity.property.EntityPropertyNames;
import cn.nukkit.entity.property.EntityPropertyStringValues;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemEgg extends ProjectileItem {

    public ItemEgg() {
        this(0, 1);
    }

    public ItemEgg(Integer meta) {
        this(meta, 1);
    }

    public ItemEgg(Integer meta, int count) {
        super(EGG, meta, count, "Egg");
    }

    protected ItemEgg(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    @Override
    public ProjectileFactory getProjectileEntityFactory() {
        return EntityEgg::new;
    }

    @Override
    public float getThrowForce() {
        return 1.5f;
    }

    @Override
    public int getMaxStackSize() {
        return 16;
    }

    @Override
    protected void correctNBT(CompoundTag nbt) {
        nbt.putCompound("properties", new CompoundTag()
                .putString(EntityPropertyNames.CLIMATE_VARIANT, EntityPropertyStringValues.CLIMATE_VARIANT_TEMPERATE));
    }

    @Override
    public boolean isEgg() {
        return true;
    }
}
