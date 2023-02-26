package cn.nukkit.item;

import cn.nukkit.entity.item.EntityPotion;
import cn.nukkit.entity.projectile.ProjectileFactory;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * Created on 2015/12/27 by xtypr.
 * Package cn.nukkit.item in project Nukkit .
 */
public class ItemPotionSplash extends ProjectileItem {

    public ItemPotionSplash() {
        this(0, 1);
    }

    public ItemPotionSplash(Integer meta) {
        this(meta, 1);
    }

    public ItemPotionSplash(Integer meta, int count) {
        super(SPLASH_POTION, meta, count, "Splash Potion");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public ProjectileFactory getProjectileEntityFactory() {
        return EntityPotion::new;
    }

    @Override
    public float getThrowForce() {
        return 0.5f;
    }

    @Override
    protected void correctNBT(CompoundTag nbt) {
        nbt.putInt("PotionId", this.getDamage());

        nbt.putCompound("Item", NBTIO.putItemHelper(this));
    }

    public int getPotionId() {
        return getDamage();
    }
}
