package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemIngotGold extends Item {

    public ItemIngotGold() {
        this(0, 1);
    }

    public ItemIngotGold(Integer meta) {
        this(meta, 1);
    }

    public ItemIngotGold(Integer meta, int count) {
        super(GOLD_INGOT, meta, count, "Gold Ingot");
    }

    @Override
    public float getFurnaceXpMultiplier() {
        return 1;
    }
}
