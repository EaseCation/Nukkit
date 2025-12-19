package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggEndermite extends ItemSpawnEgg {
    public ItemSpawnEggEndermite() {
        this(0, 1);
    }

    public ItemSpawnEggEndermite(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggEndermite(Integer meta, int count) {
        super(ENDERMITE_SPAWN_EGG, meta, count, "Endermite Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.ENDERMITE;
    }
}