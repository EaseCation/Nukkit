package cn.nukkit.item;

public class ItemBundleCyan extends ItemBundle {
    public ItemBundleCyan() {
        this( 0, 1);
    }

    public ItemBundleCyan(Integer meta) {
        this(meta, 1);
    }

    public ItemBundleCyan(Integer meta, int count) {
        super(CYAN_BUNDLE, meta, count, "Cyan Bundle");
    }
}
