package cn.nukkit.block;

public class BlockSkullSkeleton extends BlockSkull {
    BlockSkullSkeleton() {

    }

    @Override
    public int getId() {
        return SKELETON_SKULL;
    }

    @Override
    public String getName() {
        return "Skeleton Skull";
    }

    @Override
    public String getDescriptionId() {
        return "item.skull.skeleton.name";
    }
}
