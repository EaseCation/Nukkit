package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggZombieHorse extends ItemSpawnEgg {
    public ItemSpawnEggZombieHorse() {
        this(0, 1);
    }

    public ItemSpawnEggZombieHorse(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggZombieHorse(Integer meta, int count) {
        super(ZOMBIE_HORSE_SPAWN_EGG, meta, count, "Zombie Horse Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.ZOMBIE_HORSE;
    }
}