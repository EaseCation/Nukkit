package cn.nukkit.block;

public class BlockWoodStrippedOak extends BlockLogStrippedOak {
    BlockWoodStrippedOak() {

    }

    @Override
    public int getId() {
        return STRIPPED_OAK_WOOD;
    }

    @Override
    public String getName() {
        return "Stripped Oak Wood";
    }

    @Override
    public boolean isWood() {
        return true;
    }

    @Override
    public String getDescriptionId() {
        return "tile.wood.stripped.oak.name";
    }
}
