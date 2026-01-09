package cn.nukkit.block;

public class BlockSkullZombie extends BlockSkull {
    BlockSkullZombie() {

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
