package cn.nukkit.item;

public class ItemBundleBrown extends ItemBundle {
    public ItemBundleBrown() {
        this( 0, 1);
    }

    public ItemBundleBrown(Integer meta) {
        this(meta, 1);
    }

    public ItemBundleBrown(Integer meta, int count) {
        super(BROWN_BUNDLE, meta, count, "Brown Bundle");
    }
}
