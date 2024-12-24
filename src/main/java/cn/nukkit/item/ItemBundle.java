package cn.nukkit.item;

public class ItemBundle extends Item {
    public ItemBundle() {
        this( 0, 1);
    }

    public ItemBundle(Integer meta) {
        this(meta, 1);
    }

    public ItemBundle(Integer meta, int count) {
        super(BUNDLE, meta, count, "Bundle");
    }

    protected ItemBundle(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    @Override
    public boolean isBundle() {
        return true;
    }
}
