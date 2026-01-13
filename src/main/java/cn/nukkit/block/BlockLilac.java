package cn.nukkit.block;

public class BlockLilac extends BlockDoublePlant {
    BlockLilac() {

    }

    @Override
    public int getId() {
        return LILAC;
    }

    @Override
    public String getName() {
        return "Lilac";
    }

    @Override
    public String getDescriptionId() {
        return "tile.double_plant.syringa.name";
    }
}
