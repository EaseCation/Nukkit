package cn.nukkit.block;

public class BlockLightningRodWaxed extends BlockLightningRod {
    public BlockLightningRodWaxed() {
        this(0);
    }

    public BlockLightningRodWaxed(int meta) {
        super(meta);

    }

    @Override
    public int getId() {
        return WAXED_LIGHTNING_ROD;
    }

    @Override
    public String getName() {
        return "Waxed Lightning Rod";
    }

    @Override
    public boolean isWaxed() {
        return true;
    }

    @Override
    public final int getWaxedBlockId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getIncrementAgeBlockId() {
        return WAXED_EXPOSED_LIGHTNING_ROD;
    }

    @Override
    public int getDewaxedBlockId() {
        return LIGHTNING_ROD;
    }
}
