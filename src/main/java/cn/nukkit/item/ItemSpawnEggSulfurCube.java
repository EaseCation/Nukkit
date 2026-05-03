package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggSulfurCube extends ItemSpawnEgg {
    public ItemSpawnEggSulfurCube() {
        this(0, 1);
    }

    public ItemSpawnEggSulfurCube(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggSulfurCube(Integer meta, int count) {
        super(SULFUR_CUBE_SPAWN_EGG, meta, count, "Sulfur Cube Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.SULFUR_CUBE;
    }
}
