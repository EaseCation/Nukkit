package cn.nukkit.item;

public class ItemPotterySherdExplorer extends Item {
    public ItemPotterySherdExplorer() {
        this(0, 1);
    }

    public ItemPotterySherdExplorer(Integer meta) {
        this(meta, 1);
    }

    public ItemPotterySherdExplorer(Integer meta, int count) {
        super(EXPLORER_POTTERY_SHERD, meta, count, "Explorer Pottery Sherd");
    }

    @Override
    public boolean isPotterySherd() {
        return true;
    }
}
