package cn.nukkit.block;

public class BlockSandRed extends BlockSand {
    BlockSandRed() {

    }

    @Override
    public int getId() {
        return RED_SAND;
    }

    @Override
    public String getName() {
        return "Red Sand";
    }

    @Override
    public String getDescriptionId() {
        return "tile.sand.red.name";
    }
}
