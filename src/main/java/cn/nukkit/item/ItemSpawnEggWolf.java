package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggWolf extends ItemSpawnEgg {
    public ItemSpawnEggWolf() {
        this(0, 1);
    }

    public ItemSpawnEggWolf(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggWolf(Integer meta, int count) {
        super(WOLF_SPAWN_EGG, meta, count, "Wolf Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.WOLF;
    }
}