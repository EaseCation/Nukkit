package cn.nukkit.block;

public class BlockSkullPiglin extends BlockSkull {
    public BlockSkullPiglin() {
        this(0);
    }

    public BlockSkullPiglin(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return PIGLIN_HEAD;
    }

    @Override
    public String getName() {
        return "Piglin Head";
    }
}
