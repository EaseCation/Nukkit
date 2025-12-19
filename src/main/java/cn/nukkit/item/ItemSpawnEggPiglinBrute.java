package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggPiglinBrute extends ItemSpawnEgg {
    public ItemSpawnEggPiglinBrute() {
        this(0, 1);
    }

    public ItemSpawnEggPiglinBrute(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggPiglinBrute(Integer meta, int count) {
        super(PIGLIN_BRUTE_SPAWN_EGG, meta, count, "Piglin Brute Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.PIGLIN_BRUTE;
    }
}