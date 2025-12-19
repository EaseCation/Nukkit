package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggNautilus extends ItemSpawnEgg {
    public ItemSpawnEggNautilus() {
        this(0, 1);
    }

    public ItemSpawnEggNautilus(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggNautilus(Integer meta, int count) {
        super(NAUTILUS_SPAWN_EGG, meta, count, "Nautilus Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.NAUTILUS;
    }
}