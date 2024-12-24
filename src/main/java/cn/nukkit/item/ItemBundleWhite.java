package cn.nukkit.item;

public class ItemBundleWhite extends ItemBundle {
    public ItemBundleWhite() {
        this( 0, 1);
    }

    public ItemBundleWhite(Integer meta) {
        this(meta, 1);
    }

    public ItemBundleWhite(Integer meta, int count) {
        super(WHITE_BUNDLE, meta, count, "White Bundle");
    }
}
