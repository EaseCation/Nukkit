package cn.nukkit.block;

/**
 * @deprecated 1.20.0
 */
@Deprecated
public class BlockCauldronLava extends BlockCauldron {

    BlockCauldronLava() {

    }

    @Override
    public int getId() {
        return LAVA_CAULDRON;
    }

    @Override
    public int getLightLevel() {
        return isEmpty() ? 0 : 15;
    }

    @Override
    public int getLightBlock() {
        return isEmpty() ? 3 : 14;
    }
}
