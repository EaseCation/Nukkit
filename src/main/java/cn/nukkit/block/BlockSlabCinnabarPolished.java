package cn.nukkit.block;

public class BlockSlabCinnabarPolished extends BlockSlabCinnabar {
    BlockSlabCinnabarPolished() {
    }

    @Override
    public int getId() {
        return POLISHED_CINNABAR_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Polished Cinnabar Slab" : "Polished Cinnabar Slab";
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return POLISHED_CINNABAR_DOUBLE_SLAB;
    }
}
