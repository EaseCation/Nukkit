package cn.nukkit.block.edu;

import cn.nukkit.block.BlockTorch;

public class BlockTorchUnderwater extends BlockTorch {

    protected BlockTorchUnderwater() {

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

    @Override
    public boolean isChemistryFeature() {
        return true;
    }
}
