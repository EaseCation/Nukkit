package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggIronGolem extends ItemSpawnEgg {
    public ItemSpawnEggIronGolem() {
        this(0, 1);
    }

    public ItemSpawnEggIronGolem(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggIronGolem(Integer meta, int count) {
        super(IRON_GOLEM_SPAWN_EGG, meta, count, "Iron Golem Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.IRON_GOLEM;
    }
}