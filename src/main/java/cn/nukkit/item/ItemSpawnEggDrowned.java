package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggDrowned extends ItemSpawnEgg {
    public ItemSpawnEggDrowned() {
        this(0, 1);
    }

    public ItemSpawnEggDrowned(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggDrowned(Integer meta, int count) {
        super(DROWNED_SPAWN_EGG, meta, count, "Drowned Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.DROWNED;
    }
}