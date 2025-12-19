package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggZombie extends ItemSpawnEgg {
    public ItemSpawnEggZombie() {
        this(0, 1);
    }

    public ItemSpawnEggZombie(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggZombie(Integer meta, int count) {
        super(ZOMBIE_SPAWN_EGG, meta, count, "Zombie Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.ZOMBIE;
    }
}