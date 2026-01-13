package cn.nukkit.block;

public class BlockWoodStrippedJungle extends BlockLogStrippedJungle {
    BlockWoodStrippedJungle() {

    }

    @Override
    public int getId() {
        return STRIPPED_JUNGLE_WOOD;
    }

    @Override
    public String getName() {
        return "Stripped Jungle Wood";
    }

    @Override
    public boolean isWood() {
        return true;
    }

    @Override
    public String getDescriptionId() {
        return "tile.wood.stripped.jungle.name";
    }
}
