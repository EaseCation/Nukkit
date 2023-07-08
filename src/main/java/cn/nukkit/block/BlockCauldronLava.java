package cn.nukkit.block;

public class BlockCauldronLava extends BlockCauldron {

    public BlockCauldronLava() {
        this(0);
    }

    public BlockCauldronLava(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return LAVA_CAULDRON;
    }

    @Override
    public int getLightLevel() {
        return isEmpty() ? 0 : 15;
    }
}
