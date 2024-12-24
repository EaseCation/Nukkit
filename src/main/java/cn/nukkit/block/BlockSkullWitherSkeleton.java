package cn.nukkit.block;

public class BlockSkullWitherSkeleton extends BlockSkull {
    public BlockSkullWitherSkeleton() {
        this(0);
    }

    public BlockSkullWitherSkeleton(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WITHER_SKELETON_SKULL;
    }

    @Override
    public String getName() {
        return "Wither Skeleton Head";
    }
}
