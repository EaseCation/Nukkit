package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggDonkey extends ItemSpawnEgg {
    public ItemSpawnEggDonkey() {
        this(0, 1);
    }

    public ItemSpawnEggDonkey(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggDonkey(Integer meta, int count) {
        super(DONKEY_SPAWN_EGG, meta, count, "Donkey Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.DONKEY;
    }
}