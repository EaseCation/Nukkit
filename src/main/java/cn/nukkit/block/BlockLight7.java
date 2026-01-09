package cn.nukkit.block;

public class BlockLight7 extends BlockLight {
    BlockLight7() {

    }

    @Override
    public int getId() {
        return LIGHT_BLOCK_7;
    }

    @Override
    public int getLightLevel() {
        return 7;
    }

    @Override
    protected int getNextLightLevelBlockId() {
        return LIGHT_BLOCK_8;
    }
}
