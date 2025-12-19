package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggBreeze extends ItemSpawnEgg {
    public ItemSpawnEggBreeze() {
        this(0, 1);
    }

    public ItemSpawnEggBreeze(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggBreeze(Integer meta, int count) {
        super(BREEZE_SPAWN_EGG, meta, count, "Breeze Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.BREEZE;
    }
}