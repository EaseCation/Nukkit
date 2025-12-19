package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggPanda extends ItemSpawnEgg {
    public ItemSpawnEggPanda() {
        this(0, 1);
    }

    public ItemSpawnEggPanda(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggPanda(Integer meta, int count) {
        super(PANDA_SPAWN_EGG, meta, count, "Panda Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.PANDA;
    }
}