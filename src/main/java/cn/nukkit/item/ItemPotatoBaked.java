package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemPotatoBaked extends ItemEdible {

    public ItemPotatoBaked() {
        this(0, 1);
    }

    public ItemPotatoBaked(Integer meta) {
        this(meta, 1);
    }

    public ItemPotatoBaked(Integer meta, int count) {
        super(BAKED_POTATO, meta, count, "Baked Potato");
    }

    @Override
    public float getFurnaceXpMultiplier() {
        return 0.35f;
    }

    @Override
    public int getCompostableChance() {
        return 85;
    }
}
