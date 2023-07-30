package cn.nukkit.level.particle;

import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;

/**
 * Created on 2015/11/21 by xtypr.
 * Package cn.nukkit.level.particle in project Nukkit .
 */
public class ItemBreakParticle extends GenericParticle {
    public ItemBreakParticle(Vector3 pos, Item item) {
        this(pos, item.getId(), item.getDamage());
    }

    public ItemBreakParticle(Vector3 pos, int itemId) {
        this(pos, itemId, 0);
    }

    public ItemBreakParticle(Vector3 pos, int itemId, int itemMeta) {
        super(pos, Particle.ICON_CRACK, (itemId << 16) | itemMeta);
    }
}
