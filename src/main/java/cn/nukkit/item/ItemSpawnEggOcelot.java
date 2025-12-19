package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggOcelot extends ItemSpawnEgg {
    public ItemSpawnEggOcelot() {
        this(0, 1);
    }

    public ItemSpawnEggOcelot(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggOcelot(Integer meta, int count) {
        super(OCELOT_SPAWN_EGG, meta, count, "Ocelot Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.OCELOT;
    }
}