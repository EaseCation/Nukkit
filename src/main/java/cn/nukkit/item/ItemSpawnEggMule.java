package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggMule extends ItemSpawnEgg {
    public ItemSpawnEggMule() {
        this(0, 1);
    }

    public ItemSpawnEggMule(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggMule(Integer meta, int count) {
        super(MULE_SPAWN_EGG, meta, count, "Mule Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.MULE;
    }
}