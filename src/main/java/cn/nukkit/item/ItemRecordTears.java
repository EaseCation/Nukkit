package cn.nukkit.item;

import cn.nukkit.network.protocol.LevelSoundEventPacket;

public class ItemRecordTears extends ItemRecord {
    public ItemRecordTears() {
        this(0, 1);
    }

    public ItemRecordTears(Integer meta) {
        this(meta, 1);
    }

    public ItemRecordTears(Integer meta, int count) {
        super(Item.MUSIC_DISC_TEARS, meta, count);
    }

    @Override
    public int getSoundEvent() {
        return LevelSoundEventPacket.SOUND_RECORD_TEARS;
    }

    @Override
    public int getDuration() {
        return 175 * 20;
    }

    @Override
    public int getComparatorSignal() {
        return 10;
    }

    @Override
    public String getTranslationIdentifier() {
        return "record_tears";
    }
}
