package cn.nukkit.block;

public abstract class BlockNetherRoots extends BlockFlower {
    @Override
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    public boolean canBeActivated() {
        return false;
    }

    @Override
    public boolean isFertilizable() {
        return false;
    }

    @Override
    protected boolean canSurvive() {
        int id = down().getId();
        return id == GRASS_BLOCK || id == DIRT || id == COARSE_DIRT || id == FARMLAND || id == PODZOL || id == MYCELIUM || id == DIRT_WITH_ROOTS || id == MOSS_BLOCK || id == PALE_MOSS_BLOCK || id == MUD || id == MUDDY_MANGROVE_ROOTS
                || id == CRIMSON_NYLIUM || id == WARPED_NYLIUM || id == SOUL_SOIL;
    }
}
