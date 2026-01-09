package cn.nukkit.block;

public class BlockLight0 extends BlockLight {
    BlockLight0() {

    }

    @Override
    public int getId() {
        return LIGHT_BLOCK_0;
    }

    @Override
    public int getLightLevel() {
        return 0;
    }

    @Override
    protected int getNextLightLevelBlockId() {
        return LIGHT_BLOCK_1;
    }
}
