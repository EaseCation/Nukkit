package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggEnderDragon extends ItemSpawnEgg {
    public ItemSpawnEggEnderDragon() {
        this(0, 1);
    }

    public ItemSpawnEggEnderDragon(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggEnderDragon(Integer meta, int count) {
        super(ENDER_DRAGON_SPAWN_EGG, meta, count, "Ender Dragon Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.ENDER_DRAGON;
    }
}