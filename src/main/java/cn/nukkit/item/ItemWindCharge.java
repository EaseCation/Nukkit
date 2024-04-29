package cn.nukkit.item;

import cn.nukkit.entity.projectile.ProjectileFactory;

public class ItemWindCharge extends /*Projectile*/Item {
    public ItemWindCharge() {
        this(0, 1);
    }

    public ItemWindCharge(Integer meta) {
        this(meta, 1);
    }

    public ItemWindCharge(Integer meta, int count) {
        super(WIND_CHARGE, meta, count, "Wind Charge");
    }
/*
    @Override
    public ProjectileFactory getProjectileEntityFactory() {
        return EntityWindCharge::new;
    }

    @Override
    public float getThrowForce() {
        return 1.5f;
    }
*/
}
