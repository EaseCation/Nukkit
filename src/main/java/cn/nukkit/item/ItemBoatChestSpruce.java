package cn.nukkit.item;

public class ItemBoatChestSpruce extends ItemBoatChest {
    public ItemBoatChestSpruce() {
        this(0, 1);
    }

    public ItemBoatChestSpruce(Integer meta) {
        this(meta, 1);
    }

    public ItemBoatChestSpruce(Integer meta, int count) {
        super(SPRUCE_CHEST_BOAT, meta, count, "Spruce Boat with Chest");
    }

    @Override
    public int getBoatType() {
        return SPRUCE;
    }
}
