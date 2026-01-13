package cn.nukkit.block;

public class BlockTulipOrange extends BlockFlower {
    BlockTulipOrange() {

    }

    @Override
    public int getId() {
        return ORANGE_TULIP;
    }

    @Override
    public String getName() {
        return "Orange Tulip";
    }

    @Override
    public String getDescriptionId() {
        return "tile.red_flower.tulipOrange.name";
    }
}
