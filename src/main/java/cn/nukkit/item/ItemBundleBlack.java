package cn.nukkit.item;

public class ItemBundleBlack extends ItemBundle {
    public ItemBundleBlack() {
        this( 0, 1);
    }

    public ItemBundleBlack(Integer meta) {
        this(meta, 1);
    }

    public ItemBundleBlack(Integer meta, int count) {
        super(BLACK_BUNDLE, meta, count, "Black Bundle");
    }
}
