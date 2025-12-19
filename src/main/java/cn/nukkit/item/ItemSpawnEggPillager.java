package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggPillager extends ItemSpawnEgg {
    public ItemSpawnEggPillager() {
        this(0, 1);
    }

    public ItemSpawnEggPillager(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggPillager(Integer meta, int count) {
        super(PILLAGER_SPAWN_EGG, meta, count, "Pillager Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.PILLAGER;
    }
}