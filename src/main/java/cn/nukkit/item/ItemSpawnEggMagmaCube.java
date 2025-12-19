package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggMagmaCube extends ItemSpawnEgg {
    public ItemSpawnEggMagmaCube() {
        this(0, 1);
    }

    public ItemSpawnEggMagmaCube(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggMagmaCube(Integer meta, int count) {
        super(MAGMA_CUBE_SPAWN_EGG, meta, count, "Magma Cube Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.MAGMA_CUBE;
    }
}