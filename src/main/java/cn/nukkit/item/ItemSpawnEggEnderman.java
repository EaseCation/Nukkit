package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggEnderman extends ItemSpawnEgg {
    public ItemSpawnEggEnderman() {
        this(0, 1);
    }

    public ItemSpawnEggEnderman(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggEnderman(Integer meta, int count) {
        super(ENDERMAN_SPAWN_EGG, meta, count, "Enderman Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.ENDERMAN;
    }
}