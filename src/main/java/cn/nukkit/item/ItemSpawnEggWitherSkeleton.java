package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggWitherSkeleton extends ItemSpawnEgg {
    public ItemSpawnEggWitherSkeleton() {
        this(0, 1);
    }

    public ItemSpawnEggWitherSkeleton(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggWitherSkeleton(Integer meta, int count) {
        super(WITHER_SKELETON_SPAWN_EGG, meta, count, "Wither Skeleton Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.WITHER_SKELETON;
    }
}