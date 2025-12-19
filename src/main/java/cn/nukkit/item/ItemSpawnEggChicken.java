package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggChicken extends ItemSpawnEgg {
    public ItemSpawnEggChicken() {
        this(0, 1);
    }

    public ItemSpawnEggChicken(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggChicken(Integer meta, int count) {
        super(CHICKEN_SPAWN_EGG, meta, count, "Chicken Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.CHICKEN;
    }
}
