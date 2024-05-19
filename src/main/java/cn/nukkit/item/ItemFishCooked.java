package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemFishCooked extends ItemFish {

    public ItemFishCooked() {
        this(0, 1);
    }

    public ItemFishCooked(Integer meta) {
        this(meta, 1);
    }

    public ItemFishCooked(Integer meta, int count) {
        super(COOKED_COD, meta, count, "Cooked Fish");
    }

    @Override
    public float getFurnaceXpMultiplier() {
        return 0.35f;
    }
}
