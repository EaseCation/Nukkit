package cn.nukkit.item;

import cn.nukkit.network.protocol.LevelSoundEventPacket;

/**
 * @author CreeperFace
 */
public class ItemRecordChirp extends ItemRecord {

    public ItemRecordChirp() {
        this(0, 1);
    }

    public ItemRecordChirp(Integer meta) {
        this(meta, 1);
    }

    public ItemRecordChirp(Integer meta, int count) {
        super(Item.MUSIC_DISC_CHIRP, meta, count);
    }

    @Override
    public int getSoundEvent() {
        return LevelSoundEventPacket.SOUND_RECORD_CHIRP;
    }

    @Override
    public int getDuration() {
        return 185 * 20;
    }

    @Override
    public int getComparatorSignal() {
        return 4;
    }

    @Override
    public String getTranslationIdentifier() {
        return "record_chirp";
    }
}
