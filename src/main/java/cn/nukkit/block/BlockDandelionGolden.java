package cn.nukkit.block;

public class BlockDandelionGolden extends BlockDandelion {
    BlockDandelionGolden() {
    }

    @Override
    public int getId() {
        return GOLDEN_DANDELION;
    }

    @Override
    public String getName() {
        return "Golden Dandelion";
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
        return 0;
    }

    @Override
    protected Block getUncommonFlower() {
        return this;
    }
}
