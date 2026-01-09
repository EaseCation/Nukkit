package cn.nukkit.block;

public class BlockLight9 extends BlockLight {
    BlockLight9() {

    }

    @Override
    public int getId() {
        return LIGHT_BLOCK_9;
    }

    @Override
    public int getLightLevel() {
        return 9;
    }

    @Override
    protected int getNextLightLevelBlockId() {
        return LIGHT_BLOCK_10;
    }
}
