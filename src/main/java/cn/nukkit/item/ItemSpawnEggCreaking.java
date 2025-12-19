package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggCreaking extends ItemSpawnEgg {
    public ItemSpawnEggCreaking() {
        this(0, 1);
    }

    public ItemSpawnEggCreaking(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggCreaking(Integer meta, int count) {
        super(CREAKING_SPAWN_EGG, meta, count, "Creaking Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.CREAKING;
    }
}