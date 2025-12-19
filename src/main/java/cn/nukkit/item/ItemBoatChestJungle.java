package cn.nukkit.item;

public class ItemBoatChestJungle extends ItemBoatChest {
    public ItemBoatChestJungle() {
        this(0, 1);
    }

    public ItemBoatChestJungle(Integer meta) {
        this(meta, 1);
    }

    public ItemBoatChestJungle(Integer meta, int count) {
        super(JUNGLE_CHEST_BOAT, meta, count, "Jungle Boat with Chest");
    }

    @Override
    public int getBoatType() {
        return JUNGLE;
    }
}