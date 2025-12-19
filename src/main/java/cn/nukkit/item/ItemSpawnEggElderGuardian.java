package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggElderGuardian extends ItemSpawnEgg {
    public ItemSpawnEggElderGuardian() {
        this(0, 1);
    }

    public ItemSpawnEggElderGuardian(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggElderGuardian(Integer meta, int count) {
        super(ELDER_GUARDIAN_SPAWN_EGG, meta, count, "Elder Guardian Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.ELDER_GUARDIAN;
    }
}