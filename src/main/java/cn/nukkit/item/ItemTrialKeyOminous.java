package cn.nukkit.item;

public class ItemTrialKeyOminous extends Item {
    public ItemTrialKeyOminous() {
        this(0, 1);
    }

    public ItemTrialKeyOminous(Integer meta) {
        this(meta, 1);
    }

    public ItemTrialKeyOminous(Integer meta, int count) {
        super(OMINOUS_TRIAL_KEY, meta, count, "Ominous Trial Key");
    }
}
