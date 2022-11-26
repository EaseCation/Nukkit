package cn.nukkit.block;

public class BlockTorchSoul extends BlockTorch {
    public BlockTorchSoul() {
        this(0);
    }

    public BlockTorchSoul(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SOUL_TORCH;
    }

    @Override
    public String getName() {
        return "Soul Torch";
    }

    @Override
    public int getLightLevel() {
        return 10;
    }
}
