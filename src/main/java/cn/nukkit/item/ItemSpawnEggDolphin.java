package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggDolphin extends ItemSpawnEgg {
    public ItemSpawnEggDolphin() {
        this(0, 1);
    }

    public ItemSpawnEggDolphin(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggDolphin(Integer meta, int count) {
        super(DOLPHIN_SPAWN_EGG, meta, count, "Dolphin Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.DOLPHIN;
    }
}