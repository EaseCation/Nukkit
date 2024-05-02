package cn.nukkit.item;

import cn.nukkit.network.protocol.LevelSoundEventPacket;

/**
 * @author CreeperFace
 */
public class ItemRecordStrad extends ItemRecord {

    public ItemRecordStrad() {
        this(0, 1);
    }

    public ItemRecordStrad(Integer meta) {
        this(meta, 1);
    }

    public ItemRecordStrad(Integer meta, int count) {
        super(Item.MUSIC_DISC_STRAD, meta, count);
    }

    @Override
    public int getSoundEvent() {
        return LevelSoundEventPacket.SOUND_RECORD_STRAD;
    }

    @Override
    public int getDuration() {
        return 188 * 20;
    }

    @Override
    public int getComparatorSignal() {
        return 9;
    }

    @Override
    public String getTranslationIdentifier() {
        return "record_strad";
    }
}
