package cn.nukkit.item;

public class ItemScrapNetherite extends Item {

    public ItemScrapNetherite() {
        this(0, 1);
    }

    public ItemScrapNetherite(Integer meta) {
        this(meta, 1);
    }

    public ItemScrapNetherite(Integer meta, int count) {
        super(NETHERITE_SCRAP, meta, count, "Netherite Scrap");
    }

    @Override
    public boolean isFireResistant() {
        return true;
    }
}
