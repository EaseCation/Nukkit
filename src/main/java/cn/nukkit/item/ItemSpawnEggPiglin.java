package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggPiglin extends ItemSpawnEgg {
    public ItemSpawnEggPiglin() {
        this(0, 1);
    }

    public ItemSpawnEggPiglin(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggPiglin(Integer meta, int count) {
        super(PIGLIN_SPAWN_EGG, meta, count, "Piglin Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.PIGLIN;
    }
}