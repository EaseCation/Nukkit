package cn.nukkit.item;

public class ItemBundleRed extends ItemBundle {
    public ItemBundleRed() {
        this( 0, 1);
    }

    public ItemBundleRed(Integer meta) {
        this(meta, 1);
    }

    public ItemBundleRed(Integer meta, int count) {
        super(RED_BUNDLE, meta, count, "Red Bundle");
    }
}
