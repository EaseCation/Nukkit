package cn.nukkit.item;

public class ItemBundleGreen extends ItemBundle {
    public ItemBundleGreen() {
        this( 0, 1);
    }

    public ItemBundleGreen(Integer meta) {
        this(meta, 1);
    }

    public ItemBundleGreen(Integer meta, int count) {
        super(GREEN_BUNDLE, meta, count, "Green Bundle");
    }
}
