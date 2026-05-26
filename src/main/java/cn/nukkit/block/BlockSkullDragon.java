package cn.nukkit.block;

public class BlockSkullDragon extends BlockSkull {
    BlockSkullDragon() {

    }

    @Override
    public int getId() {
        return DRAGON_HEAD;
    }

    @Override
    public String getName() {
        return "Dragon Head";
    }

    @Override
    public String getDescriptionId() {
        return "item.skull.dragon.name";
    }

    @Override
    public Instrument getTopInstrument() {
        return Instrument.ENDER_DRAGON;
    }
}
