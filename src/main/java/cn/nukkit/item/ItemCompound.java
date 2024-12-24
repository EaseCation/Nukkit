package cn.nukkit.item;

public class ItemCompound extends Item {
    public ItemCompound() {
        this(0, 1);
    }

    public ItemCompound(Integer meta) {
        this(meta, 1);
    }

    public ItemCompound(Integer meta, int count) {
        super(COMPOUND, meta, count, "Compound");
    }

    @Override
    public boolean isChemistryFeature() {
        return true;
    }
}
