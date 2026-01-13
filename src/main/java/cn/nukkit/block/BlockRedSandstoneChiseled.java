package cn.nukkit.block;

public class BlockRedSandstoneChiseled extends BlockRedSandstone {
    BlockRedSandstoneChiseled() {

    }

    @Override
    public int getId() {
        return CHISELED_RED_SANDSTONE;
    }

    @Override
    public String getName() {
        return "Chiseled Red Sandstone";
    }

    @Override
    public String getDescriptionId() {
        return "tile.red_sandstone.chiseled.name";
    }
}
