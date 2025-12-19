package cn.nukkit.item;

public class ItemBoatMangrove extends ItemBoat {
    public ItemBoatMangrove() {
        this(0, 1);
    }

    public ItemBoatMangrove(Integer meta) {
        this(meta, 1);
    }

    public ItemBoatMangrove(Integer meta, int count) {
        super(MANGROVE_BOAT, meta, count, "Mangrove Boat");
    }

    @Override
    public int getBoatType() {
        return MANGROVE;
    }
}
