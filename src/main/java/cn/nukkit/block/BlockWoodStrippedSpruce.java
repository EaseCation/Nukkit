package cn.nukkit.block;

public class BlockWoodStrippedSpruce extends BlockLogStrippedSpruce {
    BlockWoodStrippedSpruce() {

    }

    @Override
    public int getId() {
        return STRIPPED_SPRUCE_WOOD;
    }

    @Override
    public String getName() {
        return "Stripped Spruce Wood";
    }

    @Override
    public boolean isWood() {
        return true;
    }

    @Override
    public String getDescriptionId() {
        return "tile.wood.stripped.spruce.name";
    }
}
