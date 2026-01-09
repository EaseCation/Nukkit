package cn.nukkit.block;

public class BlockWoodStrippedMangrove extends BlockLogStrippedMangrove {
    BlockWoodStrippedMangrove() {

    }

    @Override
    public int getId() {
        return STRIPPED_MANGROVE_WOOD;
    }

    @Override
    public String getName() {
        return "Stripped Mangrove Wood";
    }

    @Override
    public boolean isWood() {
        return true;
    }
}
