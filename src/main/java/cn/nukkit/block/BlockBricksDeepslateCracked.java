package cn.nukkit.block;

public class BlockBricksDeepslateCracked extends BlockBricksDeepslate {
    public BlockBricksDeepslateCracked() {
    }

    @Override
    public int getId() {
        return CRACKED_DEEPSLATE_BRICKS;
    }

    @Override
    public String getName() {
        return "Cracked Deepslate Bricks";
    }

    @Override
    public float getFurnaceXpMultiplier() {
        return 0.1f;
    }
}
