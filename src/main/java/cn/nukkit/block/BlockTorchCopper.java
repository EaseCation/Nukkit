package cn.nukkit.block;

public class BlockTorchCopper extends BlockTorch {
    public BlockTorchCopper() {
        this(0);
    }

    public BlockTorchCopper(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return COPPER_TORCH;
    }

    @Override
    public String getName() {
        return "Copper Torch";
    }
}
