package cn.nukkit.block.edu;

public class BlockTorchColoredBluePurple extends BlockTorchColored {

    public static final int BLUE = 0;
    public static final int PURPLE = 8;

    public BlockTorchColoredBluePurple() {
        this(0);
    }

    public BlockTorchColoredBluePurple(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return COLORED_TORCH_BP;
    }

    @Override
    public String getName() {
        return ((getDamage() & 0xf) >> FACING_DIRECTION_BITS) == BLUE ? "Blue Torch" : "Purple Torch";
    }
}
