package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggStray extends ItemSpawnEgg {
    public ItemSpawnEggStray() {
        this(0, 1);
    }

    public ItemSpawnEggStray(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggStray(Integer meta, int count) {
        super(STRAY_SPAWN_EGG, meta, count, "Stray Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.STRAY;
    }
}