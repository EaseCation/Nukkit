package cn.nukkit.item;

public class ItemBoatAcacia extends ItemBoat {
    public ItemBoatAcacia() {
        this(0, 1);
    }

    public ItemBoatAcacia(Integer meta) {
        this(meta, 1);
    }

    public ItemBoatAcacia(Integer meta, int count) {
        super(ACACIA_BOAT, meta, count, "Acacia Boat");
    }

    @Override
    public int getBoatType() {
        return ACACIA;
    }
}
