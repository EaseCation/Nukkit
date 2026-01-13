package cn.nukkit.block;

public class BlockSunflower extends BlockDoublePlant {
    BlockSunflower() {

    }

    @Override
    public int getId() {
        return SUNFLOWER;
    }

    @Override
    public String getName() {
        return "Sunflower";
    }

    @Override
    public String getDescriptionId() {
        return "tile.double_plant.sunflower.name";
    }
}
