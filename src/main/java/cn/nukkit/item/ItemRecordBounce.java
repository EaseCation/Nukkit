package cn.nukkit.item;

import cn.nukkit.network.protocol.LevelSoundEventPacket;

public class ItemRecordBounce extends ItemRecord {
    public ItemRecordBounce() {
        this(0, 1);
    }

    public ItemRecordBounce(Integer meta) {
        this(meta, 1);
    }

    public ItemRecordBounce(Integer meta, int count) {
        super(MUSIC_DISC_BOUNCE, meta, count);
    }

    @Override
    public int getSoundEvent() {
        return LevelSoundEventPacket.SOUND_RECORD_BOUNCE;
    }

    @Override
    public int getDuration() {
        return 234 * 20;
    }

    @Override
    public int getComparatorSignal() {
        return 8;
    }

    @Override
    public String getTranslationIdentifier() {
        return "record_bounce";
    }
}
