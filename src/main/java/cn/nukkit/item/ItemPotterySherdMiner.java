package cn.nukkit.item;

public class ItemPotterySherdMiner extends Item {
    public ItemPotterySherdMiner() {
        this(0, 1);
    }

    public ItemPotterySherdMiner(Integer meta) {
        this(meta, 1);
    }

    public ItemPotterySherdMiner(Integer meta, int count) {
        super(MINER_POTTERY_SHERD, meta, count, "Miner Pottery Sherd");
    }

    @Override
    public boolean isPotterySherd() {
        return true;
    }
}
