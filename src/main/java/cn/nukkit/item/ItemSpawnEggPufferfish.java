package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggPufferfish extends ItemSpawnEgg {
    public ItemSpawnEggPufferfish() {
        this(0, 1);
    }

    public ItemSpawnEggPufferfish(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggPufferfish(Integer meta, int count) {
        super(PUFFERFISH_SPAWN_EGG, meta, count, "Pufferfish Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.PUFFERFISH;
    }
}