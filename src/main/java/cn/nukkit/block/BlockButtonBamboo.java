package cn.nukkit.block;

public class BlockButtonBamboo extends BlockButtonWooden {
    public BlockButtonBamboo() {
        this(0);
    }

    public BlockButtonBamboo(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BAMBOO_BUTTON;
    }

    @Override
    public String getName() {
        return "Bamboo Button";
    }
}
