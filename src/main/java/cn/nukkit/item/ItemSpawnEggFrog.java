package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggFrog extends ItemSpawnEgg {
    public ItemSpawnEggFrog() {
        this(0, 1);
    }

    public ItemSpawnEggFrog(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggFrog(Integer meta, int count) {
        super(FROG_SPAWN_EGG, meta, count, "Frog Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.FROG;
    }
}