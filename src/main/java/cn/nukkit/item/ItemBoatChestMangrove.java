package cn.nukkit.item;

public class ItemBoatChestMangrove extends ItemBoatChest {
    public ItemBoatChestMangrove() {
        this(0, 1);
    }

    public ItemBoatChestMangrove(Integer meta) {
        this(meta, 1);
    }

    public ItemBoatChestMangrove(Integer meta, int count) {
        super(MANGROVE_CHEST_BOAT, meta, count, "Mangrove Boat with Chest");
    }

    @Override
    public int getBoatType() {
        return MANGROVE;
    }
}