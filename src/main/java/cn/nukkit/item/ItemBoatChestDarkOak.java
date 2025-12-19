package cn.nukkit.item;

public class ItemBoatChestDarkOak extends ItemBoatChest {
    public ItemBoatChestDarkOak() {
        this(0, 1);
    }

    public ItemBoatChestDarkOak(Integer meta) {
        this(meta, 1);
    }

    public ItemBoatChestDarkOak(Integer meta, int count) {
        super(DARK_OAK_CHEST_BOAT, meta, count, "Dark Oak Boat with Chest");
    }

    @Override
    public int getBoatType() {
        return DARK_OAK;
    }
}