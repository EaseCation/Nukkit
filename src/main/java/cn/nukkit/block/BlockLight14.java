package cn.nukkit.block;

public class BlockLight14 extends BlockLight {
    BlockLight14() {

    }

    @Override
    public int getId() {
        return LIGHT_BLOCK_14;
    }

    @Override
    public int getLightLevel() {
        return 14;
    }

    @Override
    protected int getNextLightLevelBlockId() {
        return LIGHT_BLOCK_15;
    }
}
