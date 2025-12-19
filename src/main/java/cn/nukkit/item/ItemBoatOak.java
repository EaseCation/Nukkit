package cn.nukkit.item;

public class ItemBoatOak extends ItemBoat {
    public ItemBoatOak() {
        this(0, 1);
    }

    public ItemBoatOak(Integer meta) {
        this(meta, 1);
    }

    public ItemBoatOak(Integer meta, int count) {
        super(OAK_BOAT, meta, count, "Oak Boat");
    }

    @Override
    public int getBoatType() {
        return OAK;
    }
}
