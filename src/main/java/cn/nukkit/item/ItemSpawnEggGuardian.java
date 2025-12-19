package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggGuardian extends ItemSpawnEgg {
    public ItemSpawnEggGuardian() {
        this(0, 1);
    }

    public ItemSpawnEggGuardian(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggGuardian(Integer meta, int count) {
        super(GUARDIAN_SPAWN_EGG, meta, count, "Guardian Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.GUARDIAN;
    }
}