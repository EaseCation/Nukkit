package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggSlime extends ItemSpawnEgg {
    public ItemSpawnEggSlime() {
        this(0, 1);
    }

    public ItemSpawnEggSlime(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggSlime(Integer meta, int count) {
        super(SLIME_SPAWN_EGG, meta, count, "Slime Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.SLIME;
    }
}