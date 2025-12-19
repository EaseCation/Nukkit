package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggHorse extends ItemSpawnEgg {
    public ItemSpawnEggHorse() {
        this(0, 1);
    }

    public ItemSpawnEggHorse(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggHorse(Integer meta, int count) {
        super(HORSE_SPAWN_EGG, meta, count, "Horse Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.HORSE;
    }
}