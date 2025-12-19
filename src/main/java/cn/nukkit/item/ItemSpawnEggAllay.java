package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggAllay extends ItemSpawnEgg {
    public ItemSpawnEggAllay() {
        this(0, 1);
    }

    public ItemSpawnEggAllay(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggAllay(Integer meta, int count) {
        super(ALLAY_SPAWN_EGG, meta, count, "Allay Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.ALLAY;
    }
}