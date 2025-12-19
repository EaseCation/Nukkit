package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggCopperGolem extends ItemSpawnEgg {
    public ItemSpawnEggCopperGolem() {
        this(0, 1);
    }

    public ItemSpawnEggCopperGolem(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggCopperGolem(Integer meta, int count) {
        super(COPPER_GOLEM_SPAWN_EGG, meta, count, "Copper Golem Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.COPPER_GOLEM;
    }
}