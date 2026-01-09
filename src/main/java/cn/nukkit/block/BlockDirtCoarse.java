package cn.nukkit.block;

public class BlockDirtCoarse extends BlockDirt {
    BlockDirtCoarse() {

    }

    @Override
    public int getId() {
        return COARSE_DIRT;
    }

    @Override
    public String getName() {
        return "Coarse Dirt";
    }

    @Override
    protected int getHoedBlockId() {
        return DIRT;
    }
}
