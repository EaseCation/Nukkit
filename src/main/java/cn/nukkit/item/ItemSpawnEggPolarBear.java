package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggPolarBear extends ItemSpawnEgg {
    public ItemSpawnEggPolarBear() {
        this(0, 1);
    }

    public ItemSpawnEggPolarBear(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggPolarBear(Integer meta, int count) {
        super(POLAR_BEAR_SPAWN_EGG, meta, count, "Polar Bear Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.POLAR_BEAR;
    }
}