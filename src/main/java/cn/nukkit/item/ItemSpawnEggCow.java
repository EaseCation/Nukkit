package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggCow extends ItemSpawnEgg {
    public ItemSpawnEggCow() {
        this(0, 1);
    }

    public ItemSpawnEggCow(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggCow(Integer meta, int count) {
        super(COW_SPAWN_EGG, meta, count, "Cow Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.COW;
    }
}
