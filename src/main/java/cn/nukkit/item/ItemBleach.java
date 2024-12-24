package cn.nukkit.item;

public class ItemBleach extends Item {
    public ItemBleach() {
        this(0, 1);
    }

    public ItemBleach(Integer meta) {
        this(meta, 1);
    }

    public ItemBleach(Integer meta, int count) {
        super(BLEACH, meta, count, "Bleach");
    }

    @Override
    public boolean isChemistryFeature() {
        return true;
    }
}
