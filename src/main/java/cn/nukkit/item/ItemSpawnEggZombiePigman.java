package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggZombiePigman extends ItemSpawnEgg {
    public ItemSpawnEggZombiePigman() {
        this(0, 1);
    }

    public ItemSpawnEggZombiePigman(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggZombiePigman(Integer meta, int count) {
        super(ZOMBIE_PIGMAN_SPAWN_EGG, meta, count, "Zombie Pigman Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.ZOMBIE_PIGMAN;
    }
}