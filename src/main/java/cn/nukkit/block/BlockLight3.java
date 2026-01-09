package cn.nukkit.block;

public class BlockLight3 extends BlockLight {
    BlockLight3() {

    }

    @Override
    public int getId() {
        return LIGHT_BLOCK_3;
    }

    @Override
    public int getLightLevel() {
        return 3;
    }

    @Override
    protected int getNextLightLevelBlockId() {
        return LIGHT_BLOCK_4;
    }
}
