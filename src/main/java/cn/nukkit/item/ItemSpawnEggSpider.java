package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggSpider extends ItemSpawnEgg {
    public ItemSpawnEggSpider() {
        this(0, 1);
    }

    public ItemSpawnEggSpider(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggSpider(Integer meta, int count) {
        super(SPIDER_SPAWN_EGG, meta, count, "Spider Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.SPIDER;
    }
}