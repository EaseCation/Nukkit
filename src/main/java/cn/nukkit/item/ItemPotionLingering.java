package cn.nukkit.item;

import cn.nukkit.entity.item.EntityLingeringPotion;
import cn.nukkit.entity.projectile.ProjectileFactory;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;

public class ItemPotionLingering extends ProjectileItem {

    public ItemPotionLingering() {
        this(0, 1);
    }

    public ItemPotionLingering(Integer meta) {
        this(meta, 1);
    }

    public ItemPotionLingering(Integer meta, int count) {
        super(LINGERING_POTION, meta, 1, "Lingering Potion");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public ProjectileFactory getProjectileEntityFactory() {
        return EntityLingeringPotion::new;
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

    @Override
    public boolean isPotion() {
        return true;
    }

    public int getPotionId() {
        return getDamage();
    }
}
