package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggMooshroom extends ItemSpawnEgg {
    public ItemSpawnEggMooshroom() {
        this(0, 1);
    }

    public ItemSpawnEggMooshroom(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggMooshroom(Integer meta, int count) {
        super(MOOSHROOM_SPAWN_EGG, meta, count, "Mooshroom Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.MOOSHROOM;
    }
}