package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggWither extends ItemSpawnEgg {
    public ItemSpawnEggWither() {
        this(0, 1);
    }

    public ItemSpawnEggWither(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggWither(Integer meta, int count) {
        super(WITHER_SPAWN_EGG, meta, count, "Wither Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.WITHER;
    }
}