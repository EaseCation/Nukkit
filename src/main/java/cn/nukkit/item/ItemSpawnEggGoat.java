package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggGoat extends ItemSpawnEgg {
    public ItemSpawnEggGoat() {
        this(0, 1);
    }

    public ItemSpawnEggGoat(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggGoat(Integer meta, int count) {
        super(GOAT_SPAWN_EGG, meta, count, "Goat Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.GOAT;
    }
}