package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggWarden extends ItemSpawnEgg {
    public ItemSpawnEggWarden() {
        this(0, 1);
    }

    public ItemSpawnEggWarden(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggWarden(Integer meta, int count) {
        super(WARDEN_SPAWN_EGG, meta, count, "Warden Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.WARDEN;
    }
}