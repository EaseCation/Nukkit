package cn.nukkit.block;

public class BlockLight12 extends BlockLight {
    BlockLight12() {

    }

    @Override
    public int getId() {
        return LIGHT_BLOCK_12;
    }

    @Override
    public int getLightLevel() {
        return 12;
    }

    @Override
    protected int getNextLightLevelBlockId() {
        return LIGHT_BLOCK_13;
    }
}
