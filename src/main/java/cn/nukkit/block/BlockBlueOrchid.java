package cn.nukkit.block;

public class BlockBlueOrchid extends BlockFlower {
    BlockBlueOrchid() {

    }

    @Override
    public int getId() {
        return BLUE_ORCHID;
    }

    @Override
    public String getName() {
        return "Blue Orchid";
    }

    @Override
    public String getDescriptionId() {
        return "tile.red_flower.blueOrchid.name";
    }
}
