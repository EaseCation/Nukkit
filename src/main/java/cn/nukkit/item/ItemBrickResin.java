package cn.nukkit.item;

public class ItemBrickResin extends Item {
    public ItemBrickResin() {
        this(0, 1);
    }

    public ItemBrickResin(Integer meta) {
        this(meta, 1);
    }

    public ItemBrickResin(Integer meta, int count) {
        super(RESIN_BRICK, meta, count, "Resin Brick");
    }

    @Override
    public float getFurnaceXpMultiplier() {
        return 0.1f;
    }
}
