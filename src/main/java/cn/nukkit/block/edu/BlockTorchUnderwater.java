package cn.nukkit.block.edu;

import cn.nukkit.block.BlockTorch;

public class BlockTorchUnderwater extends BlockTorch {

    public BlockTorchUnderwater() {
        this(0);
    }

    public BlockTorchUnderwater(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return UNDERWATER_TORCH;
    }

    @Override
    public String getName() {
        return "Underwater Torch";
    }

    @Override
    public boolean canBeFlowedInto() {
        return false;
    }

    @Override
    public boolean canContainWater() {
        return true;
    }
}
