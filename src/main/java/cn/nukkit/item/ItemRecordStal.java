package cn.nukkit.item;

import cn.nukkit.network.protocol.LevelSoundEventPacket;

/**
 * @author CreeperFace
 */
public class ItemRecordStal extends ItemRecord {

    public ItemRecordStal() {
        this(0, 1);
    }

    public ItemRecordStal(Integer meta) {
        this(meta, 1);
    }

    public ItemRecordStal(Integer meta, int count) {
        super(Item.MUSIC_DISC_STAL, meta, count);
    }

    @Override
    public int getSoundEvent() {
        return LevelSoundEventPacket.SOUND_RECORD_STAL;
    }

    @Override
    public int getDuration() {
        return 150 * 20;
    }

    @Override
    public int getComparatorSignal() {
        return 8;
    }

    @Override
    public String getTranslationIdentifier() {
        return "record_stal";
    }
}
