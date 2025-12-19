package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggZombieNautilus extends ItemSpawnEgg {
    public ItemSpawnEggZombieNautilus() {
        this(0, 1);
    }

    public ItemSpawnEggZombieNautilus(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggZombieNautilus(Integer meta, int count) {
        super(ZOMBIE_NAUTILUS_SPAWN_EGG, meta, count, "Zombie Nautilus Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.ZOMBIE_NAUTILUS;
    }
}