package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggCamelHusk extends ItemSpawnEgg {
    public ItemSpawnEggCamelHusk() {
        this(0, 1);
    }

    public ItemSpawnEggCamelHusk(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggCamelHusk(Integer meta, int count) {
        super(CAMEL_HUSK_SPAWN_EGG, meta, count, "Camel Husk Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.CAMEL_HUSK;
    }
}