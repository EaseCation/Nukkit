package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggGlowSquid extends ItemSpawnEgg {
    public ItemSpawnEggGlowSquid() {
        this(0, 1);
    }

    public ItemSpawnEggGlowSquid(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggGlowSquid(Integer meta, int count) {
        super(GLOW_SQUID_SPAWN_EGG, meta, count, "Glow Squid Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.GLOW_SQUID;
    }
}