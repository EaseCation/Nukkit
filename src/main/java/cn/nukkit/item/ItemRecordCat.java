package cn.nukkit.item;

import cn.nukkit.network.protocol.LevelSoundEventPacket;

/**
 * @author CreeperFace
 */
public class ItemRecordCat extends ItemRecord {

    public ItemRecordCat() {
        this(0, 1);
    }

    public ItemRecordCat(Integer meta) {
        this(meta, 1);
    }

    public ItemRecordCat(Integer meta, int count) {
        super(Item.MUSIC_DISC_CAT, meta, count);
    }

    @Override
    public int getSoundEvent() {
        return LevelSoundEventPacket.SOUND_RECORD_CAT;
    }

    @Override
    public int getDuration() {
        return 185 * 20;
    }

    @Override
    public int getComparatorSignal() {
        return 2;
    }

    @Override
    public String getTranslationIdentifier() {
        return "record_cat";
    }
}
