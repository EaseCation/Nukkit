package cn.nukkit.item;

import cn.nukkit.network.protocol.LevelSoundEventPacket;

/**
 * @author CreeperFace
 */
public class ItemRecord11 extends ItemRecord {

    public ItemRecord11() {
        this(0, 1);
    }

    public ItemRecord11(Integer meta) {
        this(meta, 1);
    }

    public ItemRecord11(Integer meta, int count) {
        super(Item.MUSIC_DISC_11, meta, count);
    }

    @Override
    public int getSoundEvent() {
        return LevelSoundEventPacket.SOUND_RECORD_11;
    }

    @Override
    public int getDuration() {
        return 71 * 20;
    }

    @Override
    public int getComparatorSignal() {
        return 11;
    }

    @Override
    public String getTranslationIdentifier() {
        return "record_11";
    }
}
