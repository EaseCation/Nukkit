package cn.nukkit.item;

public class ItemRapidFertilizer extends Item {
    public ItemRapidFertilizer() {
        this(0, 1);
    }

    public ItemRapidFertilizer(Integer meta) {
        this(meta, 1);
    }

    public ItemRapidFertilizer(Integer meta, int count) {
        super(RAPID_FERTILIZER, meta, count, "Rapid Fertilizer");
    }

    @Override
    public boolean isFertilizer() {
        return true;
    }

    @Override
    public boolean isChemistryFeature() {
        return true;
    }
}
