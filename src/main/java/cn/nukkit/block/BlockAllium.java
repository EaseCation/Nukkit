package cn.nukkit.block;

public class BlockAllium extends BlockFlower {
    BlockAllium() {

    }

    @Override
    public int getId() {
        return ALLIUM;
    }

    @Override
    public String getName() {
        return "Allium";
    }

    @Override
    public String getDescriptionId() {
        return "tile.red_flower.allium.name";
    }
}
