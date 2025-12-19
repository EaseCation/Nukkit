package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggCaveSpider extends ItemSpawnEgg {
    public ItemSpawnEggCaveSpider() {
        this(0, 1);
    }

    public ItemSpawnEggCaveSpider(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggCaveSpider(Integer meta, int count) {
        super(CAVE_SPIDER_SPAWN_EGG, meta, count, "Cave Spider Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.CAVE_SPIDER;
    }
}