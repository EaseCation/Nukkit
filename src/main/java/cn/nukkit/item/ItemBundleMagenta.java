package cn.nukkit.item;

public class ItemBundleMagenta extends ItemBundle {
    public ItemBundleMagenta() {
        this( 0, 1);
    }

    public ItemBundleMagenta(Integer meta) {
        this(meta, 1);
    }

    public ItemBundleMagenta(Integer meta, int count) {
        super(MAGENTA_BUNDLE, meta, count, "Magenta Bundle");
    }
}
