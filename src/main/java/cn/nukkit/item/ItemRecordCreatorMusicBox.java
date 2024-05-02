package cn.nukkit.item;

import cn.nukkit.network.protocol.LevelSoundEventPacket;

public class ItemRecordCreatorMusicBox extends ItemRecord {
    public ItemRecordCreatorMusicBox() {
        this(0, 1);
    }

    public ItemRecordCreatorMusicBox(Integer meta) {
        this(meta, 1);
    }

    public ItemRecordCreatorMusicBox(Integer meta, int count) {
        super(Item.MUSIC_DISC_CREATOR_MUSIC_BOX, meta, count);
    }

    @Override
    public int getSoundEvent() {
        return LevelSoundEventPacket.SOUND_RECORD_CREATOR_MUSIC_BOX;
    }

    @Override
    public int getDuration() {
        return 73 * 20;
    }

    @Override
    public int getComparatorSignal() {
        return 11;
    }

    @Override
    public String getTranslationIdentifier() {
        return "record_creator_music_box";
    }
}
