package cn.nukkit.block;

public class BlockLight11 extends BlockLight {
    BlockLight11() {

    }

    @Override
    public int getId() {
        return LIGHT_BLOCK_11;
    }

    @Override
    public int getLightLevel() {
        return 11;
    }

    @Override
    protected int getNextLightLevelBlockId() {
        return LIGHT_BLOCK_12;
    }
}
