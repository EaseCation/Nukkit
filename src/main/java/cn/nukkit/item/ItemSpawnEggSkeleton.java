package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggSkeleton extends ItemSpawnEgg {
    public ItemSpawnEggSkeleton() {
        this(0, 1);
    }

    public ItemSpawnEggSkeleton(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggSkeleton(Integer meta, int count) {
        super(SKELETON_SPAWN_EGG, meta, count, "Skeleton Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.SKELETON;
    }
}