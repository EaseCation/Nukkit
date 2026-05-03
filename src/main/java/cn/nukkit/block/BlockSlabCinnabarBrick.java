package cn.nukkit.block;

public class BlockSlabCinnabarBrick extends BlockSlabCinnabar {
    BlockSlabCinnabarBrick() {
    }

    @Override
    public int getId() {
        return CINNABAR_BRICK_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Cinnabar Brick Slab" : "Cinnabar Brick Slab";
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return CINNABAR_BRICK_DOUBLE_SLAB;
    }
}
