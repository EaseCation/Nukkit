package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggHusk extends ItemSpawnEgg {
    public ItemSpawnEggHusk() {
        this(0, 1);
    }

    public ItemSpawnEggHusk(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggHusk(Integer meta, int count) {
        super(HUSK_SPAWN_EGG, meta, count, "Husk Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.HUSK;
    }
}