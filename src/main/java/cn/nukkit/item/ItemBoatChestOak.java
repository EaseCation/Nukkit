package cn.nukkit.item;

public class ItemBoatChestOak extends ItemBoatChest {
    public ItemBoatChestOak() {
        this(0, 1);
    }

    public ItemBoatChestOak(Integer meta) {
        this(meta, 1);
    }

    public ItemBoatChestOak(Integer meta, int count) {
        super(OAK_CHEST_BOAT, meta, count, "Oak Boat with Chest");
    }

    @Override
    public int getBoatType() {
        return OAK;
    }
}
