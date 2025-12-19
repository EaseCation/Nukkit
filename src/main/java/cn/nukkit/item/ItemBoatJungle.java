package cn.nukkit.item;

public class ItemBoatJungle extends ItemBoat {
    public ItemBoatJungle() {
        this(0, 1);
    }

    public ItemBoatJungle(Integer meta) {
        this(meta, 1);
    }

    public ItemBoatJungle(Integer meta, int count) {
        super(JUNGLE_BOAT, meta, count, "Jungle Boat");
    }

    @Override
    public int getBoatType() {
        return JUNGLE;
    }
}
