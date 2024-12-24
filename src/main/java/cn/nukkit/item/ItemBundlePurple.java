package cn.nukkit.item;

public class ItemBundlePurple extends ItemBundle {
    public ItemBundlePurple() {
        this( 0, 1);
    }

    public ItemBundlePurple(Integer meta) {
        this(meta, 1);
    }

    public ItemBundlePurple(Integer meta, int count) {
        super(PURPLE_BUNDLE, meta, count, "Purple Bundle");
    }
}
