package cn.nukkit.item;

public class ItemCompassLodestone extends Item {

    public ItemCompassLodestone() {
        this(0, 1);
    }

    public ItemCompassLodestone(Integer meta) {
        this(meta, 1);
    }

    public ItemCompassLodestone(Integer meta, int count) {
        super(LODESTONE_COMPASS, meta, 1, "Lodestone Compass");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    //TODO: lodestone
}
