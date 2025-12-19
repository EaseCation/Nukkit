package cn.nukkit.item;

public class ItemBoatChestPaleOak extends ItemBoatChest {
    public ItemBoatChestPaleOak() {
        this(0, 1);
    }

    public ItemBoatChestPaleOak(Integer meta) {
        this(meta, 1);
    }

    public ItemBoatChestPaleOak(Integer meta, int count) {
        super(PALE_OAK_CHEST_BOAT, meta, count, "Pale Oak Boat with Chest");
    }

    @Override
    public int getBoatType() {
        return PALE_OAK;
    }
}