package cn.nukkit.item;

import cn.nukkit.network.protocol.LevelSoundEventPacket;

public class ItemRecordPrecipice extends ItemRecord {
    public ItemRecordPrecipice() {
        this(0, 1);
    }

    public ItemRecordPrecipice(Integer meta) {
        this(meta, 1);
    }

    public ItemRecordPrecipice(Integer meta, int count) {
        super(Item.MUSIC_DISC_PRECIPICE, meta, count);
    }

    @Override
    public int getSoundEvent() {
        return LevelSoundEventPacket.SOUND_RECORD_PRECIPICE;
    }

    @Override
    public int getDuration() {
        return 299 * 20;
    }

    @Override
    public int getComparatorSignal() {
        return 13;
    }

    @Override
    public String getTranslationIdentifier() {
        return "record_precipice";
    }
}
