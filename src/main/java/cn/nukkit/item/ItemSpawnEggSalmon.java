package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggSalmon extends ItemSpawnEgg {
    public ItemSpawnEggSalmon() {
        this(0, 1);
    }

    public ItemSpawnEggSalmon(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggSalmon(Integer meta, int count) {
        super(SALMON_SPAWN_EGG, meta, count, "Salmon Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.SALMON;
    }
}