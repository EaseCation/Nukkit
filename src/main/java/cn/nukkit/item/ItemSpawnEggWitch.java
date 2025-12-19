package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggWitch extends ItemSpawnEgg {
    public ItemSpawnEggWitch() {
        this(0, 1);
    }

    public ItemSpawnEggWitch(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggWitch(Integer meta, int count) {
        super(WITCH_SPAWN_EGG, meta, count, "Witch Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.WITCH;
    }
}