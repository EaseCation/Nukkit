package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggCamel extends ItemSpawnEgg {
    public ItemSpawnEggCamel() {
        this(0, 1);
    }

    public ItemSpawnEggCamel(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggCamel(Integer meta, int count) {
        super(CAMEL_SPAWN_EGG, meta, count, "Camel Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.CAMEL;
    }
}