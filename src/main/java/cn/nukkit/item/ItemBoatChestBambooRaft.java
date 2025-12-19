package cn.nukkit.item;

public class ItemBoatChestBambooRaft extends ItemBoatChest {
    public ItemBoatChestBambooRaft() {
        this(0, 1);
    }

    public ItemBoatChestBambooRaft(Integer meta) {
        this(meta, 1);
    }

    public ItemBoatChestBambooRaft(Integer meta, int count) {
        super(BAMBOO_CHEST_RAFT, meta, count, "Bamboo Raft with Chest");
    }

    @Override
    public int getBoatType() {
        return RAFT;
    }
}
