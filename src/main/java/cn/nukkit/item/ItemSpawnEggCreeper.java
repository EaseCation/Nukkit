package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggCreeper extends ItemSpawnEgg {
    public ItemSpawnEggCreeper() {
        this(0, 1);
    }

    public ItemSpawnEggCreeper(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggCreeper(Integer meta, int count) {
        super(CREEPER_SPAWN_EGG, meta, count, "Creeper Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.CREEPER;
    }
}