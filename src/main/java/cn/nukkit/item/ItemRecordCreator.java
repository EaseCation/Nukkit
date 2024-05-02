package cn.nukkit.item;

import cn.nukkit.network.protocol.LevelSoundEventPacket;

public class ItemRecordCreator extends ItemRecord {
    public ItemRecordCreator() {
        this(0, 1);
    }

    public ItemRecordCreator(Integer meta) {
        this(meta, 1);
    }

    public ItemRecordCreator(Integer meta, int count) {
        super(Item.MUSIC_DISC_CREATOR, meta, count);
    }

    @Override
    public int getSoundEvent() {
        return LevelSoundEventPacket.SOUND_RECORD_CREATOR;
    }

    @Override
    public int getDuration() {
        return 176 * 20;
    }

    @Override
    public int getComparatorSignal() {
        return 12;
    }

    @Override
    public String getTranslationIdentifier() {
        return "record_creator";
    }
}
