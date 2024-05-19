package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemCoal extends Item {
    public static final int TYPE_COAL = 0;
    public static final int TYPE_CHARCOAL = 1;

    public ItemCoal() {
        this(0, 1);
    }

    public ItemCoal(Integer meta) {
        this(meta, 1);
    }

    public ItemCoal(Integer meta, int count) {
        super(COAL, meta, count, meta != null && meta == TYPE_CHARCOAL ? "Charcoal" : "Coal");
    }

    @Override
    public int getFuelTime() {
        return 1600;
    }

    @Override
    public float getFurnaceXpMultiplier() {
        if (getDamage() == TYPE_CHARCOAL) {
            return 0.15f;
        }
        return 0.1f;
    }
}
