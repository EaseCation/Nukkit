package cn.nukkit.block;

public class BlockSkullPlayer extends BlockSkull {
    public BlockSkullPlayer() {
        this(0);
    }

    public BlockSkullPlayer(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return PLAYER_HEAD;
    }

    @Override
    public String getName() {
        return "Player Head";
    }
}
