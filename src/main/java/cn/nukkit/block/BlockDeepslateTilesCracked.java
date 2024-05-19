package cn.nukkit.block;

public class BlockDeepslateTilesCracked extends BlockDeepslateTiles {
    public BlockDeepslateTilesCracked() {
    }

    @Override
    public int getId() {
        return CRACKED_DEEPSLATE_TILES;
    }

    @Override
    public String getName() {
        return "Cracked Deepslate Tiles";
    }

    @Override
    public float getFurnaceXpMultiplier() {
        return 0.1f;
    }
}
