package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggCod extends ItemSpawnEgg {
    public ItemSpawnEggCod() {
        this(0, 1);
    }

    public ItemSpawnEggCod(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggCod(Integer meta, int count) {
        super(COD_SPAWN_EGG, meta, count, "Cod Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.COD;
    }
}