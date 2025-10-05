package cn.nukkit.block;

public class BlockTorchflower extends BlockFlower {
    public BlockTorchflower() {
        this(0);
    }

    public BlockTorchflower(int meta) {
        super(0);
    }

    @Override
    public int getId() {
        return TORCHFLOWER;
    }

    @Override
    public boolean isStackedByData() {
        return false;
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
    public int getCompostableChance() {
        return 85;
    }

    @Override
    protected Block getUncommonFlower() {
        return this;
    }
}
