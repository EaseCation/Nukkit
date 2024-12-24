package cn.nukkit.block;

public class BlockSkullZombie extends BlockSkull {
    public BlockSkullZombie() {
        this(0);
    }

    public BlockSkullZombie(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return ZOMBIE_HEAD;
    }

    @Override
    public String getName() {
        return "Zombie Head";
    }
}
