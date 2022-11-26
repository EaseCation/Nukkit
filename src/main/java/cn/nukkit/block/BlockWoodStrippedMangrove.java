package cn.nukkit.block;

public class BlockWoodStrippedMangrove extends BlockLogStrippedMangrove {
    public BlockWoodStrippedMangrove() {
        this(0);
    }

    public BlockWoodStrippedMangrove(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STRIPPED_MANGROVE_WOOD;
    }

    @Override
    public String getName() {
        return "Stripped Mangrove Wood";
    }
}
