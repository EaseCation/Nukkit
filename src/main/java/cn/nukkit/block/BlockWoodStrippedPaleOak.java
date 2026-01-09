package cn.nukkit.block;

public class BlockWoodStrippedPaleOak extends BlockLogStrippedPaleOak {
    BlockWoodStrippedPaleOak() {

    }

    @Override
    public int getId() {
        return STRIPPED_PALE_OAK_WOOD;
    }

    @Override
    public String getName() {
        return "Stripped Pale Oak Wood";
    }

    @Override
    public boolean isWood() {
        return true;
    }
}
