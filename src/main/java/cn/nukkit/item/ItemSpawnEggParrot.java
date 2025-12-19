package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggParrot extends ItemSpawnEgg {
    public ItemSpawnEggParrot() {
        this(0, 1);
    }

    public ItemSpawnEggParrot(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggParrot(Integer meta, int count) {
        super(PARROT_SPAWN_EGG, meta, count, "Parrot Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.PARROT;
    }
}