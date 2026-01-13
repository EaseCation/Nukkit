package cn.nukkit.block;

public class BlockSandstoneChiseled extends BlockSandstone {
    BlockSandstoneChiseled() {

    }

    @Override
    public int getId() {
        return CHISELED_SANDSTONE;
    }

    @Override
    public String getName() {
        return "Chiseled Sandstone";
    }

    @Override
    public String getDescriptionId() {
        return "tile.sandstone.chiseled.name";
    }
}
