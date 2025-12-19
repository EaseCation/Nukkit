package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggArmadillo extends ItemSpawnEgg {
    public ItemSpawnEggArmadillo() {
        this(0, 1);
    }

    public ItemSpawnEggArmadillo(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggArmadillo(Integer meta, int count) {
        super(ARMADILLO_SPAWN_EGG, meta, count, "Armadillo Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.ARMADILLO;
    }
}