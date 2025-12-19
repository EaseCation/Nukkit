package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggSilverfish extends ItemSpawnEgg {
    public ItemSpawnEggSilverfish() {
        this(0, 1);
    }

    public ItemSpawnEggSilverfish(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggSilverfish(Integer meta, int count) {
        super(SILVERFISH_SPAWN_EGG, meta, count, "Silverfish Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.SILVERFISH;
    }
}