package cn.nukkit.item;

public class ItemBundlePink extends ItemBundle {
    public ItemBundlePink() {
        this( 0, 1);
    }

    public ItemBundlePink(Integer meta) {
        this(meta, 1);
    }

    public ItemBundlePink(Integer meta, int count) {
        super(PINK_BUNDLE, meta, count, "Pink Bundle");
    }
}
