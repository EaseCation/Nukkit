package cn.nukkit.item;

public class ItemCompassRecovery extends Item {

    public ItemCompassRecovery() {
        this(0, 1);
    }

    public ItemCompassRecovery(Integer meta) {
        this(meta, 1);
    }

    public ItemCompassRecovery(Integer meta, int count) {
        super(RECOVERY_COMPASS, meta, count, "Recovery Compass");
    }
}
