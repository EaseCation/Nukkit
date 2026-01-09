package cn.nukkit.block;

public class BlockWoodStrippedDarkOak extends BlockLogStrippedDarkOak {
    BlockWoodStrippedDarkOak() {

    }

    @Override
    public int getId() {
        return STRIPPED_DARK_OAK_WOOD;
    }

    @Override
    public String getName() {
        return "Stripped Dark Oak Wood";
    }

    @Override
    public boolean isWood() {
        return true;
    }
}
