package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggEvoker extends ItemSpawnEgg {
    public ItemSpawnEggEvoker() {
        this(0, 1);
    }

    public ItemSpawnEggEvoker(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggEvoker(Integer meta, int count) {
        super(EVOKER_SPAWN_EGG, meta, count, "Evoker Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.EVOCATION_ILLAGER;
    }
}