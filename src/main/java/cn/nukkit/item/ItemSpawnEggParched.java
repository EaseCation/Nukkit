package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggParched extends ItemSpawnEgg {
    public ItemSpawnEggParched() {
        this(0, 1);
    }

    public ItemSpawnEggParched(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggParched(Integer meta, int count) {
        super(PARCHED_SPAWN_EGG, meta, count, "Parched Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.PARCHED;
    }
}