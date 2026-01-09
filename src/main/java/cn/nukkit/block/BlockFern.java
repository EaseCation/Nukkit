package cn.nukkit.block;

public class BlockFern extends BlockGrassShort {
    BlockFern() {

    }

    @Override
    public int getId() {
        return FERN;
    }

    @Override
    public String getName() {
        return "Fern";
    }

    @Override
    public int getCompostableChance() {
        return 65;
    }

    @Override
    public boolean isPottable() {
        return true;
    }

    @Override
    protected int getDoublePlantBlockId() {
        return LARGE_FERN;
    }
}
