package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggCat extends ItemSpawnEgg {
    public ItemSpawnEggCat() {
        this(0, 1);
    }

    public ItemSpawnEggCat(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggCat(Integer meta, int count) {
        super(CAT_SPAWN_EGG, meta, count, "Cat Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.CAT;
    }
}