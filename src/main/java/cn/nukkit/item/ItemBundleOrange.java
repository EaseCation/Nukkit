package cn.nukkit.item;

public class ItemBundleOrange extends ItemBundle {
    public ItemBundleOrange() {
        this( 0, 1);
    }

    public ItemBundleOrange(Integer meta) {
        this(meta, 1);
    }

    public ItemBundleOrange(Integer meta, int count) {
        super(ORANGE_BUNDLE, meta, count, "Orange Bundle");
    }
}
