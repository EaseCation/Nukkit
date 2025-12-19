package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggStrider extends ItemSpawnEgg {
    public ItemSpawnEggStrider() {
        this(0, 1);
    }

    public ItemSpawnEggStrider(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggStrider(Integer meta, int count) {
        super(STRIDER_SPAWN_EGG, meta, count, "Strider Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.STRIDER;
    }
}