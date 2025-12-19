package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggSnowGolem extends ItemSpawnEgg {
    public ItemSpawnEggSnowGolem() {
        this(0, 1);
    }

    public ItemSpawnEggSnowGolem(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggSnowGolem(Integer meta, int count) {
        super(SNOW_GOLEM_SPAWN_EGG, meta, count, "Snow Golem Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.SNOW_GOLEM;
    }
}