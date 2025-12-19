package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggFox extends ItemSpawnEgg {
    public ItemSpawnEggFox() {
        this(0, 1);
    }

    public ItemSpawnEggFox(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggFox(Integer meta, int count) {
        super(FOX_SPAWN_EGG, meta, count, "Fox Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.FOX;
    }
}