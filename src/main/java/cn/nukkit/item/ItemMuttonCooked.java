package cn.nukkit.item;

public class ItemMuttonCooked extends ItemEdible {

    public ItemMuttonCooked() {
        this(0, 1);
    }

    public ItemMuttonCooked(Integer meta) {
        this(meta, 1);
    }

    public ItemMuttonCooked(Integer meta, int count) {
        super(COOKED_MUTTON, meta, count, "Cooked Mutton");
    }

    @Override
    public float getFurnaceXpMultiplier() {
        return 0.35f;
    }
}
