package cn.nukkit.item;

public class ItemBoatDarkOak extends ItemBoat {
    public ItemBoatDarkOak() {
        this(0, 1);
    }

    public ItemBoatDarkOak(Integer meta) {
        this(meta, 1);
    }

    public ItemBoatDarkOak(Integer meta, int count) {
        super(DARK_OAK_BOAT, meta, count, "Dark Oak Boat");
    }

    @Override
    public int getBoatType() {
        return DARK_OAK;
    }
}
