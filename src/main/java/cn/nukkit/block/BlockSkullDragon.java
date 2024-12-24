package cn.nukkit.block;

public class BlockSkullDragon extends BlockSkull {
    public BlockSkullDragon() {
        this(0);
    }

    public BlockSkullDragon(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DRAGON_HEAD;
    }

    @Override
    public String getName() {
        return "Dragon Head";
    }
}
