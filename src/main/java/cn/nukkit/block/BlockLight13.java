package cn.nukkit.block;

public class BlockLight13 extends BlockLight {
    BlockLight13() {

    }

    @Override
    public int getId() {
        return LIGHT_BLOCK_13;
    }

    @Override
    public int getLightLevel() {
        return 13;
    }

    @Override
    protected int getNextLightLevelBlockId() {
        return LIGHT_BLOCK_14;
    }
}
