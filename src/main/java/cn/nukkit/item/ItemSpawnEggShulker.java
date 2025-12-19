package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggShulker extends ItemSpawnEgg {
    public ItemSpawnEggShulker() {
        this(0, 1);
    }

    public ItemSpawnEggShulker(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggShulker(Integer meta, int count) {
        super(SHULKER_SPAWN_EGG, meta, count, "Shulker Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.SHULKER;
    }
}