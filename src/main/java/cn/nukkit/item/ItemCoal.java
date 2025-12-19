package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemCoal extends Item {
    static final int TYPE_COAL = 0;
    static final int TYPE_CHARCOAL = 1;

    public static final int[] COALS = {
            COAL,
            CHARCOAL,
    };

    public ItemCoal() {
        this(0, 1);
    }

    public ItemCoal(Integer meta) {
        this(meta, 1);
    }

    public ItemCoal(Integer meta, int count) {
        super(COAL, meta, count, "Coal");
    }

    @Override
    public int getFuelTime() {
        return 1600;
    }

    @Override
    public float getFurnaceXpMultiplier() {
        return 0.1f;
    }

    @Override
    public boolean isCoal() {
        return true;
    }
}
