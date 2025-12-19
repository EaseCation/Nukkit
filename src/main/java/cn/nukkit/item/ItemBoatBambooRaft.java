package cn.nukkit.item;

public class ItemBoatBambooRaft extends ItemBoat {
    public ItemBoatBambooRaft() {
        this(0, 1);
    }

    public ItemBoatBambooRaft(Integer meta) {
        this(meta, 1);
    }

    public ItemBoatBambooRaft(Integer meta, int count) {
        super(BAMBOO_RAFT, meta, count, "Bamboo Raft");
    }

    @Override
    public int getBoatType() {
        return RAFT;
    }
}
