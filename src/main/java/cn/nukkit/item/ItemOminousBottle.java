package cn.nukkit.item;

import cn.nukkit.network.protocol.LevelSoundEventPacket;

public class ItemOminousBottle extends ItemEdible {
    public static final int MAXIMUM_AMPLIFIER = 4;

    public ItemOminousBottle() {
        this(0, 1);
    }

    public ItemOminousBottle(Integer meta) {
        this(meta, 1);
    }

    public ItemOminousBottle(Integer meta, int count) {
        super(OMINOUS_BOTTLE, meta, count, "Ominous Bottle");
    }

    @Override
    protected int getSoundEvent() {
        return LevelSoundEventPacket.SOUND_OMINOUS_BOTTLE_END_USE;
    }
}
