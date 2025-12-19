package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggPhantom extends ItemSpawnEgg {
    public ItemSpawnEggPhantom() {
        this(0, 1);
    }

    public ItemSpawnEggPhantom(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggPhantom(Integer meta, int count) {
        super(PHANTOM_SPAWN_EGG, meta, count, "Phantom Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.PHANTOM;
    }
}