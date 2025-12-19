package cn.nukkit.item;

public class ItemBoatSpruce extends ItemBoat {
    public ItemBoatSpruce() {
        this(0, 1);
    }

    public ItemBoatSpruce(Integer meta) {
        this(meta, 1);
    }

    public ItemBoatSpruce(Integer meta, int count) {
        super(SPRUCE_BOAT, meta, count, "Spruce Boat");
    }

    @Override
    public int getBoatType() {
        return SPRUCE;
    }
}
