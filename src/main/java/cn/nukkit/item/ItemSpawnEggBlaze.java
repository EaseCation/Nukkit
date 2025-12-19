package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggBlaze extends ItemSpawnEgg {
    public ItemSpawnEggBlaze() {
        this(0, 1);
    }

    public ItemSpawnEggBlaze(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggBlaze(Integer meta, int count) {
        super(BLAZE_SPAWN_EGG, meta, count, "Blaze Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.BLAZE;
    }
}