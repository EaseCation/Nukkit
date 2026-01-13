package cn.nukkit.block;

public class BlockSkullPiglin extends BlockSkull {
    BlockSkullPiglin() {

    }

    @Override
    public int getId() {
        return PIGLIN_HEAD;
    }

    @Override
    public String getName() {
        return "Piglin Head";
    }

    @Override
    public String getDescriptionId() {
        return "item.skull.piglin.name";
    }
}
