package cn.nukkit.block;

public class BlockLight2 extends BlockLight {
    BlockLight2() {

    }

    @Override
    public int getId() {
        return LIGHT_BLOCK_2;
    }

    @Override
    public int getLightLevel() {
        return 2;
    }

    @Override
    protected int getNextLightLevelBlockId() {
        return LIGHT_BLOCK_3;
    }
}
