package cn.nukkit.item;

import cn.nukkit.network.protocol.LevelSoundEventPacket;

public class ItemRecordOtherside extends ItemRecord {

    public ItemRecordOtherside() {
        this(0, 1);
    }

    public ItemRecordOtherside(Integer meta) {
        this(meta, 1);
    }

    public ItemRecordOtherside(Integer meta, int count) {
        super(Item.MUSIC_DISC_OTHERSIDE, meta, count);
    }

    @Override
    public int getSoundEvent() {
        return LevelSoundEventPacket.SOUND_RECORD_OTHERSIDE;
    }

    @Override
    public int getDuration() {
        return 195 * 20;
    }

    @Override
    public int getComparatorSignal() {
        return 14;
    }

    @Override
    public String getTranslationIdentifier() {
        return "record_otherside";
    }
}
