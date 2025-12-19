package cn.nukkit.item;

public class ItemBoatPaleOak extends ItemBoat {
    public ItemBoatPaleOak() {
        this(0, 1);
    }

    public ItemBoatPaleOak(Integer meta) {
        this(meta, 1);
    }

    public ItemBoatPaleOak(Integer meta, int count) {
        super(PALE_OAK_BOAT, meta, count, "Pale Oak Boat");
    }

    @Override
    public int getBoatType() {
        return PALE_OAK;
    }
}
