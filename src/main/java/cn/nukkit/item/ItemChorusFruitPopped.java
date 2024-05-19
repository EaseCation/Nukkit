package cn.nukkit.item;

public class ItemChorusFruitPopped extends Item {

    public ItemChorusFruitPopped() {
        this(0, 1);
    }

    public ItemChorusFruitPopped(Integer meta) {
        this(meta, 1);
    }

    public ItemChorusFruitPopped(Integer meta, int count) {
        super(POPPED_CHORUS_FRUIT, meta, count, "Popped Chorus Fruit");
    }

    @Override
    public float getFurnaceXpMultiplier() {
        return 0.1f;
    }
}
