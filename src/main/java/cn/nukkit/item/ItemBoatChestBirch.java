package cn.nukkit.item;

public class ItemBoatChestBirch extends ItemBoatChest {
    public ItemBoatChestBirch() {
        this(0, 1);
    }

    public ItemBoatChestBirch(Integer meta) {
        this(meta, 1);
    }

    public ItemBoatChestBirch(Integer meta, int count) {
        super(BIRCH_CHEST_BOAT, meta, count, "Birch Boat with Chest");
    }

    @Override
    public int getBoatType() {
        return BIRCH;
    }
}