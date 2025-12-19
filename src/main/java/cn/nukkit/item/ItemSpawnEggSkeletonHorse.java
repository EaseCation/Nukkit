package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggSkeletonHorse extends ItemSpawnEgg {
    public ItemSpawnEggSkeletonHorse() {
        this(0, 1);
    }

    public ItemSpawnEggSkeletonHorse(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggSkeletonHorse(Integer meta, int count) {
        super(SKELETON_HORSE_SPAWN_EGG, meta, count, "Skeleton Horse Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.SKELETON_HORSE;
    }
}