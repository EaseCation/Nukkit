package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggSniffer extends ItemSpawnEgg {
    public ItemSpawnEggSniffer() {
        this(0, 1);
    }

    public ItemSpawnEggSniffer(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggSniffer(Integer meta, int count) {
        super(SNIFFER_SPAWN_EGG, meta, count, "Sniffer Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.SNIFFER;
    }
}