package cn.nukkit.block;

public class BlockBricksCrackedStone extends BlockBricksStone {
    BlockBricksCrackedStone() {

    }

    @Override
    public int getId() {
        return CRACKED_STONE_BRICKS;
    }

    @Override
    public String getName() {
        return "Cracked Stone Bricks";
    }

    @Override
    public float getFurnaceXpMultiplier() {
        return 0.1f;
    }
}
