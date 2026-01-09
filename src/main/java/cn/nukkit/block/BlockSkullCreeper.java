package cn.nukkit.block;

public class BlockSkullCreeper extends BlockSkull {
    BlockSkullCreeper() {

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
