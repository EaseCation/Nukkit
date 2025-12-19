package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggVindicator extends ItemSpawnEgg {
    public ItemSpawnEggVindicator() {
        this(0, 1);
    }

    public ItemSpawnEggVindicator(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggVindicator(Integer meta, int count) {
        super(VINDICATOR_SPAWN_EGG, meta, count, "Vindicator Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.VINDICATOR;
    }
}