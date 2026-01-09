package cn.nukkit.block;

public class BlockLight8 extends BlockLight {
    BlockLight8() {

    }

    @Override
    public int getId() {
        return LIGHT_BLOCK_8;
    }

    @Override
    public int getLightLevel() {
        return 8;
    }

    @Override
    protected int getNextLightLevelBlockId() {
        return LIGHT_BLOCK_9;
    }
}
