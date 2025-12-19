package cn.nukkit.item;

public class ItemBoatCherry extends ItemBoat {
    public ItemBoatCherry() {
        this(0, 1);
    }

    public ItemBoatCherry(Integer meta) {
        this(meta, 1);
    }

    public ItemBoatCherry(Integer meta, int count) {
        super(CHERRY_BOAT, meta, count, "Cherry Boat");
    }

    @Override
    public int getBoatType() {
        return CHERRY;
    }
}
