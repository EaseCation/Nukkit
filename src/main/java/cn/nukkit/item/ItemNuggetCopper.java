package cn.nukkit.item;

public class ItemNuggetCopper extends Item {

    public ItemNuggetCopper() {
        this(0, 1);
    }

    public ItemNuggetCopper(Integer meta) {
        this(meta, 1);
    }

    public ItemNuggetCopper(Integer meta, int count) {
        super(COPPER_NUGGET, meta, count, "Copper Nugget");
    }

    @Override
    public float getFurnaceXpMultiplier() {
        return 0.1f;
    }
}
