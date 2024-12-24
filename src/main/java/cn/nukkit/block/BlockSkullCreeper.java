package cn.nukkit.block;

public class BlockSkullCreeper extends BlockSkull {
    public BlockSkullCreeper() {
        this(0);
    }

    public BlockSkullCreeper(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CREEPER_HEAD;
    }

    @Override
    public String getName() {
        return "Creeper Head";
    }
}
