package cn.nukkit.block;

public class BlockCornflower extends BlockFlower {
    BlockCornflower() {

    }

    @Override
    public int getId() {
        return CORNFLOWER;
    }

    @Override
    public String getName() {
        return "Cornflower";
    }

    @Override
    public String getDescriptionId() {
        return "tile.red_flower.cornflower.name";
    }
}
