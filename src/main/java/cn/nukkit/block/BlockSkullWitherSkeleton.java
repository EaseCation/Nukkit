package cn.nukkit.block;

public class BlockSkullWitherSkeleton extends BlockSkull {
    BlockSkullWitherSkeleton() {

    }

    @Override
    public int getId() {
        return WITHER_SKELETON_SKULL;
    }

    @Override
    public String getName() {
        return "Wither Skeleton Skull";
    }

    @Override
    public String getDescriptionId() {
        return "item.skull.wither.name";
    }
}
