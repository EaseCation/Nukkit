package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggHoglin extends ItemSpawnEgg {
    public ItemSpawnEggHoglin() {
        this(0, 1);
    }

    public ItemSpawnEggHoglin(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggHoglin(Integer meta, int count) {
        super(HOGLIN_SPAWN_EGG, meta, count, "Hoglin Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.HOGLIN;
    }
}