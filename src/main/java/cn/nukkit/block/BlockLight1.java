package cn.nukkit.block;

public class BlockLight1 extends BlockLight {
    BlockLight1() {

    }

    @Override
    public int getId() {
        return LIGHT_BLOCK_1;
    }

    @Override
    public int getLightLevel() {
        return 1;
    }

    @Override
    protected int getNextLightLevelBlockId() {
        return LIGHT_BLOCK_2;
    }
}
