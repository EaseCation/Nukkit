package cn.nukkit.block;

public class BlockLight15 extends BlockLight {
    BlockLight15() {

    }

    @Override
    public int getId() {
        return LIGHT_BLOCK_15;
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    protected int getNextLightLevelBlockId() {
        return LIGHT_BLOCK_0;
    }
}
