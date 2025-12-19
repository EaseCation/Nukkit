package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggZombieVillager extends ItemSpawnEgg {
    public ItemSpawnEggZombieVillager() {
        this(0, 1);
    }

    public ItemSpawnEggZombieVillager(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggZombieVillager(Integer meta, int count) {
        super(ZOMBIE_VILLAGER_SPAWN_EGG, meta, count, "Zombie Villager Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.ZOMBIE_VILLAGER_V2;
    }
}