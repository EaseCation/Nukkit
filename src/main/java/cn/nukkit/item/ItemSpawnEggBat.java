package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggBat extends ItemSpawnEgg {
    public ItemSpawnEggBat() {
        this(0, 1);
    }

    public ItemSpawnEggBat(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggBat(Integer meta, int count) {
        super(BAT_SPAWN_EGG, meta, count, "Bat Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.BAT;
    }
}