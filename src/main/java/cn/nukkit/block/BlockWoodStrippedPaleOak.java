package cn.nukkit.block;

public class BlockWoodStrippedPaleOak extends BlockLogStrippedPaleOak {
    public BlockWoodStrippedPaleOak() {
        this(0);
    }

    public BlockWoodStrippedPaleOak(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STRIPPED_PALE_OAK_WOOD;
    }

    @Override
    public String getName() {
        return "Stripped Pale Oak Wood";
    }
}
