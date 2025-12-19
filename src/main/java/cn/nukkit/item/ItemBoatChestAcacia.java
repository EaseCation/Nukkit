package cn.nukkit.item;

public class ItemBoatChestAcacia extends ItemBoatChest {
    public ItemBoatChestAcacia() {
        this(0, 1);
    }

    public ItemBoatChestAcacia(Integer meta) {
        this(meta, 1);
    }

    public ItemBoatChestAcacia(Integer meta, int count) {
        super(ACACIA_CHEST_BOAT, meta, count, "Acacia Boat with Chest");
    }

    @Override
    public int getBoatType() {
        return ACACIA;
    }
}