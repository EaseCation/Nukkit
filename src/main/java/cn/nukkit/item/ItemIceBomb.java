package cn.nukkit.item;

import cn.nukkit.entity.projectile.EntityIceBomb;
import cn.nukkit.entity.projectile.ProjectileFactory;

public class ItemIceBomb extends ProjectileItem {
    public ItemIceBomb() {
        this(0, 1);
    }

    public ItemIceBomb(Integer meta) {
        this(meta, 1);
    }

    public ItemIceBomb(Integer meta, int count) {
        super(ICE_BOMB, meta, count, "Ice Bomb");
    }

    @Override
    public int getMaxStackSize() {
        return 16;
    }

    @Override
    public ProjectileFactory getProjectileEntityFactory() {
        return EntityIceBomb::new;
    }

    @Override
    public float getThrowForce() {
        return 1.5f;
    }

    @Override
    public boolean isChemistryFeature() {
        return true;
    }

    @Override
    public int getCooldownDuration() {
        return 10;
    }

    @Override
    public CooldownCategory getCooldownCategory() {
        return CooldownCategory.ICE_BOMB;
    }
}
