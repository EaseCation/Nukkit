package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggSquid extends ItemSpawnEgg {
    public ItemSpawnEggSquid() {
        this(0, 1);
    }

    public ItemSpawnEggSquid(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggSquid(Integer meta, int count) {
        super(SQUID_SPAWN_EGG, meta, count, "Squid Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.SQUID;
    }
}