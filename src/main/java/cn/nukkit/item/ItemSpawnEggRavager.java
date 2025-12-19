package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggRavager extends ItemSpawnEgg {
    public ItemSpawnEggRavager() {
        this(0, 1);
    }

    public ItemSpawnEggRavager(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggRavager(Integer meta, int count) {
        super(RAVAGER_SPAWN_EGG, meta, count, "Ravager Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.RAVAGER;
    }
}