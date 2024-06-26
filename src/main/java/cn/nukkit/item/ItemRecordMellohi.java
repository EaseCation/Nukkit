package cn.nukkit.item;

import cn.nukkit.network.protocol.LevelSoundEventPacket;

/**
 * @author CreeperFace
 */
public class ItemRecordMellohi extends ItemRecord {

    public ItemRecordMellohi() {
        this(0, 1);
    }

    public ItemRecordMellohi(Integer meta) {
        this(meta, 1);
    }

    public ItemRecordMellohi(Integer meta, int count) {
        super(Item.MUSIC_DISC_MELLOHI, meta, count);
    }

    @Override
    public int getSoundEvent() {
        return LevelSoundEventPacket.SOUND_RECORD_MELLOHI;
    }

    @Override
    public int getDuration() {
        return 96 * 20;
    }

    @Override
    public int getComparatorSignal() {
        return 7;
    }

    @Override
    public String getTranslationIdentifier() {
        return "record_mellohi";
    }
}
