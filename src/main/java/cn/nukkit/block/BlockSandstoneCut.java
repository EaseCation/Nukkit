package cn.nukkit.block;

public class BlockSandstoneCut extends BlockSandstone {
    BlockSandstoneCut() {

    }

    @Override
    public int getId() {
        return CUT_SANDSTONE;
    }

    @Override
    public String getName() {
        return "Cut Sandstone";
    }

    @Override
    public String getDescriptionId() {
        return "tile.sandstone.cut.name";
    }
}
