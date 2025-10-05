package cn.nukkit.item;

public class ItemNetherStar extends Item {

    public ItemNetherStar() {
        this(0, 1);
    }

    public ItemNetherStar(Integer meta) {
        this(meta, 1);
    }

    public ItemNetherStar(Integer meta, int count) {
        super(NETHER_STAR, meta, count, "Nether Star");
    }

    @Override
    public boolean shouldDespawn() {
        return false;
    }

    @Override
    public boolean isFireResistant() {
        return true;
    }

    @Override
    public boolean isExplodable() {
        return false;
    }
}
