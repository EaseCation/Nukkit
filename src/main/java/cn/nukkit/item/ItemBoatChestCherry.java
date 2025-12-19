package cn.nukkit.item;

public class ItemBoatChestCherry extends ItemBoatChest {
    public ItemBoatChestCherry() {
        this(0, 1);
    }

    public ItemBoatChestCherry(Integer meta) {
        this(meta, 1);
    }

    public ItemBoatChestCherry(Integer meta, int count) {
        super(CHERRY_CHEST_BOAT, meta, count, "Cherry Boat with Chest");
    }

    @Override
    public int getBoatType() {
        return CHERRY;
    }
}