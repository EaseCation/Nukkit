package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggBee extends ItemSpawnEgg {
    public ItemSpawnEggBee() {
        this(0, 1);
    }

    public ItemSpawnEggBee(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggBee(Integer meta, int count) {
        super(BEE_SPAWN_EGG, meta, count, "Bee Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.BEE;
    }
}