package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggZoglin extends ItemSpawnEgg {
    public ItemSpawnEggZoglin() {
        this(0, 1);
    }

    public ItemSpawnEggZoglin(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggZoglin(Integer meta, int count) {
        super(ZOGLIN_SPAWN_EGG, meta, count, "Zoglin Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.ZOGLIN;
    }
}