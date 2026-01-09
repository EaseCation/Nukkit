package cn.nukkit.block;

public class BlockLight6 extends BlockLight {
    BlockLight6() {

    }

    @Override
    public int getId() {
        return LIGHT_BLOCK_6;
    }

    @Override
    public int getLightLevel() {
        return 6;
    }

    @Override
    protected int getNextLightLevelBlockId() {
        return LIGHT_BLOCK_7;
    }
}
