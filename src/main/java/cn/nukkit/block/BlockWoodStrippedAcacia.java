package cn.nukkit.block;

public class BlockWoodStrippedAcacia extends BlockLogStrippedAcacia {
    BlockWoodStrippedAcacia() {

    }

    @Override
    public int getId() {
        return STRIPPED_ACACIA_WOOD;
    }

    @Override
    public String getName() {
        return "Stripped Acacia Wood";
    }

    @Override
    public boolean isWood() {
        return true;
    }

    @Override
    public String getDescriptionId() {
        return "tile.wood.stripped.acacia.name";
    }
}
