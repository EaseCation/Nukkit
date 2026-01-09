package cn.nukkit.block;

public class BlockLight5 extends BlockLight {
    BlockLight5() {

    }

    @Override
    public int getId() {
        return LIGHT_BLOCK_5;
    }

    @Override
    public int getLightLevel() {
        return 5;
    }

    @Override
    protected int getNextLightLevelBlockId() {
        return LIGHT_BLOCK_6;
    }
}
