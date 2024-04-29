package cn.nukkit.item;

public class ItemPotterySherdScrape extends Item {
    public ItemPotterySherdScrape() {
        this(0, 1);
    }

    public ItemPotterySherdScrape(Integer meta) {
        this(meta, 1);
    }

    public ItemPotterySherdScrape(Integer meta, int count) {
        super(SCRAPE_POTTERY_SHERD, meta, count, "Scrape Pottery Sherd");
    }
}
