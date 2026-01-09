package cn.nukkit.block;

public class BlockLight10 extends BlockLight {
    BlockLight10() {

    }

    @Override
    public int getId() {
        return LIGHT_BLOCK_10;
    }

    @Override
    public int getLightLevel() {
        return 10;
    }

    @Override
    protected int getNextLightLevelBlockId() {
        return LIGHT_BLOCK_11;
    }
}
