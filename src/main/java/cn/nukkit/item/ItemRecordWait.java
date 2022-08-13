package cn.nukkit.item;

import cn.nukkit.network.protocol.LevelSoundEventPacket;

/**
 * @author CreeperFace
 */
public class ItemRecordWait extends ItemRecord {

    public ItemRecordWait() {
        this(0, 1);
    }

    public ItemRecordWait(Integer meta) {
        this(meta, 1);
    }

    public ItemRecordWait(Integer meta, int count) {
        super(Item.MUSIC_DISC_WAIT, meta, count);
    }

    @Override
    public int getSoundEvent() {
        return LevelSoundEventPacket.SOUND_RECORD_WAIT;
    }

    @Override
    public String getTranslationIdentifier() {
        return "record_5";
    }
}
