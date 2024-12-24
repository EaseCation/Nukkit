package cn.nukkit.item;

public class ItemBundleGray extends ItemBundle {
    public ItemBundleGray() {
        this( 0, 1);
    }

    public ItemBundleGray(Integer meta) {
        this(meta, 1);
    }

    public ItemBundleGray(Integer meta, int count) {
        super(GRAY_BUNDLE, meta, count, "Gray Bundle");
    }
}
