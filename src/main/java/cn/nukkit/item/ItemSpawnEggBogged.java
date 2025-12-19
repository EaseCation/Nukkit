package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggBogged extends ItemSpawnEgg {
    public ItemSpawnEggBogged() {
        this(0, 1);
    }

    public ItemSpawnEggBogged(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggBogged(Integer meta, int count) {
        super(BOGGED_SPAWN_EGG, meta, count, "Bogged Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.BOGGED;
    }
}