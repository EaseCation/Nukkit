package cn.nukkit.item;

import cn.nukkit.network.protocol.LevelSoundEventPacket;

/**
 * @author CreeperFace
 */
public class ItemRecord13 extends ItemRecord {

    public ItemRecord13() {
        this(0, 1);
    }

    public ItemRecord13(Integer meta) {
        this(meta, 1);
    }

    public ItemRecord13(Integer meta, int count) {
        super(Item.MUSIC_DISC_13, meta, count);
    }

    @Override
    public int getSoundEvent() {
        return LevelSoundEventPacket.SOUND_RECORD_13;
    }

    @Override
    public int getDuration() {
        return 178 * 20;
    }

    @Override
    public int getComparatorSignal() {
        return 1;
    }

    @Override
    public String getTranslationIdentifier() {
        return "record_13";
    }
}
