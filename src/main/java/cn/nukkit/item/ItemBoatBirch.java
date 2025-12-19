package cn.nukkit.item;

public class ItemBoatBirch extends ItemBoat {
    public ItemBoatBirch() {
        this(0, 1);
    }

    public ItemBoatBirch(Integer meta) {
        this(meta, 1);
    }

    public ItemBoatBirch(Integer meta, int count) {
        super(BIRCH_BOAT, meta, count, "Birch Boat");
    }

    @Override
    public int getBoatType() {
        return BIRCH;
    }
}
