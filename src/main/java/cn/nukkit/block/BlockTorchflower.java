package cn.nukkit.block;

public class BlockTorchflower extends BlockFlower {
    BlockTorchflower() {

    }

    @Override
    public int getId() {
        return TORCHFLOWER;
    }

    @Override
    public String getName() {
        return "Torchflower";
    }

    @Override
    public boolean canBeActivated() {
        return false;
    }

    @Override
    public boolean isFertilizable() {
        return false;
    }

    @Override
    public int getCompostableChance() {
        return 85;
    }
}
