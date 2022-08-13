package cn.nukkit.item;

import cn.nukkit.network.protocol.LevelSoundEventPacket;

/**
 * @author CreeperFace
 */
public class ItemRecordMall extends ItemRecord {

    public ItemRecordMall() {
        this(0, 1);
    }

    public ItemRecordMall(Integer meta) {
        this(meta, 1);
    }

    public ItemRecordMall(Integer meta, int count) {
        super(Item.MUSIC_DISC_MALL, meta, count);
    }

    @Override
    public int getSoundEvent() {
        return LevelSoundEventPacket.SOUND_RECORD_MALL;
    }

    @Override
    public String getTranslationIdentifier() {
        return "record_mall";
    }
}
