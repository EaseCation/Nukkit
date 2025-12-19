package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggPig extends ItemSpawnEgg {
    public ItemSpawnEggPig() {
        this(0, 1);
    }

    public ItemSpawnEggPig(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggPig(Integer meta, int count) {
        super(PIG_SPAWN_EGG, meta, count, "Pig Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.PIG;
    }
}
