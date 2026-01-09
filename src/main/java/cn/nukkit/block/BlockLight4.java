package cn.nukkit.block;

public class BlockLight4 extends BlockLight {
    BlockLight4() {

    }

    @Override
    public int getId() {
        return LIGHT_BLOCK_4;
    }

    @Override
    public int getLightLevel() {
        return 4;
    }

    @Override
    protected int getNextLightLevelBlockId() {
        return LIGHT_BLOCK_5;
    }
}
