package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggHappyGhast extends ItemSpawnEgg {
    public ItemSpawnEggHappyGhast() {
        this(0, 1);
    }

    public ItemSpawnEggHappyGhast(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggHappyGhast(Integer meta, int count) {
        super(HAPPY_GHAST_SPAWN_EGG, meta, count, "Happy Ghast Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.HAPPY_GHAST;
    }
}