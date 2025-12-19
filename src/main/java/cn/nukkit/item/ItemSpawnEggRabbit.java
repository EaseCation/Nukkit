package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggRabbit extends ItemSpawnEgg {
    public ItemSpawnEggRabbit() {
        this(0, 1);
    }

    public ItemSpawnEggRabbit(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggRabbit(Integer meta, int count) {
        super(RABBIT_SPAWN_EGG, meta, count, "Rabbit Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.RABBIT;
    }
}