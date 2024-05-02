package cn.nukkit.item;

import cn.nukkit.network.protocol.LevelSoundEventPacket;

/**
 * @author CreeperFace
 */
public class ItemRecordWard extends ItemRecord {

    public ItemRecordWard() {
        this(0, 1);
    }

    public ItemRecordWard(Integer meta) {
        this(meta, 1);
    }

    public ItemRecordWard(Integer meta, int count) {
        super(Item.MUSIC_DISC_WARD, meta, count);
    }

    @Override
    public int getSoundEvent() {
        return LevelSoundEventPacket.SOUND_RECORD_WARD;
    }

    @Override
    public int getDuration() {
        return 251 * 20;
    }

    @Override
    public int getComparatorSignal() {
        return 10;
    }

    @Override
    public String getTranslationIdentifier() {
        return "record_ward";
    }
}
