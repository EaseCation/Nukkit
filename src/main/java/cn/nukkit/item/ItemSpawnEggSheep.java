package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggSheep extends ItemSpawnEgg {
    public ItemSpawnEggSheep() {
        this(0, 1);
    }

    public ItemSpawnEggSheep(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggSheep(Integer meta, int count) {
        super(SHEEP_SPAWN_EGG, meta, count, "Sheep Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.SHEEP;
    }
}