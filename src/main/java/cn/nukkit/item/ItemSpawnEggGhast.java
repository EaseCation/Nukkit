package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggGhast extends ItemSpawnEgg {
    public ItemSpawnEggGhast() {
        this(0, 1);
    }

    public ItemSpawnEggGhast(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggGhast(Integer meta, int count) {
        super(GHAST_SPAWN_EGG, meta, count, "Ghast Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.GHAST;
    }
}