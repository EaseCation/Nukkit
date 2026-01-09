package cn.nukkit.block;

public class BlockTorchSoul extends BlockTorch {
    BlockTorchSoul() {

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
