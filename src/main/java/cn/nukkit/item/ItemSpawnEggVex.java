package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggVex extends ItemSpawnEgg {
    public ItemSpawnEggVex() {
        this(0, 1);
    }

    public ItemSpawnEggVex(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggVex(Integer meta, int count) {
        super(VEX_SPAWN_EGG, meta, count, "Vex Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.VEX;
    }
}