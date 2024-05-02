package cn.nukkit.item;

import cn.nukkit.network.protocol.LevelSoundEventPacket;

public class ItemRecordRelic extends ItemRecord {
    public ItemRecordRelic() {
        this(0, 1);
    }

    public ItemRecordRelic(Integer meta) {
        this(meta, 1);
    }

    public ItemRecordRelic(Integer meta, int count) {
        super(Item.MUSIC_DISC_RELIC, meta, count);
    }

    @Override
    public int getSoundEvent() {
        return LevelSoundEventPacket.SOUND_RECORD_RELIC;
    }

    @Override
    public int getDuration() {
        return 218 * 20;
    }

    @Override
    public int getComparatorSignal() {
        return 14;
    }

    @Override
    public String getTranslationIdentifier() {
        return "record_relic";
    }
}
