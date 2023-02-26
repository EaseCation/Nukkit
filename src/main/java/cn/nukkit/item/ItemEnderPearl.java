package cn.nukkit.item;

import cn.nukkit.entity.projectile.EntityEnderPearl;
import cn.nukkit.entity.projectile.ProjectileFactory;

public class ItemEnderPearl extends ProjectileItem {

    public ItemEnderPearl() {
        this(0, 1);
    }

    public ItemEnderPearl(Integer meta) {
        this(meta, 1);
    }

    public ItemEnderPearl(Integer meta, int count) {
        super(ENDER_PEARL, 0, count, "Ender Pearl");
    }

    @Override
    public int getMaxStackSize() {
        return 16;
    }

    @Override
    public ProjectileFactory getProjectileEntityFactory() {
        return EntityEnderPearl::new;
    }

    @Override
    public float getThrowForce() {
        return 1.5f;
    }
}
