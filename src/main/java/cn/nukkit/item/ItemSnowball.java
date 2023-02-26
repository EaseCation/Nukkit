package cn.nukkit.item;

import cn.nukkit.entity.projectile.EntitySnowball;
import cn.nukkit.entity.projectile.ProjectileFactory;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemSnowball extends ProjectileItem {

    public ItemSnowball() {
        this(0, 1);
    }

    public ItemSnowball(Integer meta) {
        this(meta, 1);
    }

    public ItemSnowball(Integer meta, int count) {
        super(SNOWBALL, 0, count, "Snowball");
    }

    @Override
    public int getMaxStackSize() {
        return 16;
    }

    @Override
    public ProjectileFactory getProjectileEntityFactory() {
        return EntitySnowball::new;
    }

    @Override
    public float getThrowForce() {
        return 1.5f;
    }
}
