package cn.nukkit.block;

public class BlockWoodStrippedBirch extends BlockLogStrippedBirch {
    BlockWoodStrippedBirch() {

    }

    @Override
    public int getId() {
        return STRIPPED_BIRCH_WOOD;
    }

    @Override
    public String getName() {
        return "Stripped Birch Wood";
    }

    @Override
    public boolean isWood() {
        return true;
    }
}
