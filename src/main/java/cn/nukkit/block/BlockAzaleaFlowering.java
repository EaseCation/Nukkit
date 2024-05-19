package cn.nukkit.block;

public class BlockAzaleaFlowering extends BlockAzalea {
    public BlockAzaleaFlowering() {
        this(0);
    }

    public BlockAzaleaFlowering(int meta) {
        super(0);
    }

    @Override
    public String getName() {
        return "Flowering Azalea";
    }

    @Override
    public int getId() {
        return FLOWERING_AZALEA;
    }

    @Override
    public int getCompostableChance() {
        return 85;
    }
}
