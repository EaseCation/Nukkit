package cn.nukkit.block.edu;

public class BlockTorchColoredRedGreen extends BlockTorchColored {

    public static final int RED = 0;
    public static final int GREEN = 8;

    public BlockTorchColoredRedGreen() {
        this(0);
    }

    public BlockTorchColoredRedGreen(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return COLORED_TORCH_RG;
    }

    @Override
    public String getName() {
        return ((getDamage() & 0xf) >> FACING_DIRECTION_BITS) == RED ? "Red Torch" : "Green Torch";
    }
}
