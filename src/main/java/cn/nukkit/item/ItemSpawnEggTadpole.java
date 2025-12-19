package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggTadpole extends ItemSpawnEgg {
    public ItemSpawnEggTadpole() {
        this(0, 1);
    }

    public ItemSpawnEggTadpole(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggTadpole(Integer meta, int count) {
        super(TADPOLE_SPAWN_EGG, meta, count, "Tadpole Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.TADPOLE;
    }
}