package cn.nukkit.block;

public class BlockSkullPlayer extends BlockSkull {
    BlockSkullPlayer() {

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
