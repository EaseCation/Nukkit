package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemSteak extends ItemEdible {

    public ItemSteak() {
        this(0, 1);
    }

    public ItemSteak(Integer meta) {
        this(meta, 1);
    }

    public ItemSteak(Integer meta, int count) {
        super(COOKED_BEEF, meta, count, "Steak");
    }

    @Override
    public float getFurnaceXpMultiplier() {
        return 0.35f;
    }
}
