package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggVillager extends ItemSpawnEgg {
    public ItemSpawnEggVillager() {
        this(0, 1);
    }

    public ItemSpawnEggVillager(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggVillager(Integer meta, int count) {
        super(VILLAGER_SPAWN_EGG, meta, count, "Villager Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.VILLAGER_V2;
    }
}