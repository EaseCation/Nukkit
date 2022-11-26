package cn.nukkit.block;

public class BlockBricksNetherCracked extends BlockBricksNether {
    public BlockBricksNetherCracked() {
    }

    @Override
    public int getId() {
        return CRACKED_NETHER_BRICKS;
    }

    @Override
    public String getName() {
        return "Cracked Nether Bricks";
    }
}
