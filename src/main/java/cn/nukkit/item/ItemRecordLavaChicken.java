package cn.nukkit.item;

import cn.nukkit.network.protocol.LevelSoundEventPacket;

public class ItemRecordLavaChicken extends ItemRecord {
    public ItemRecordLavaChicken() {
        this(0, 1);
    }

    public ItemRecordLavaChicken(Integer meta) {
        this(meta, 1);
    }

    public ItemRecordLavaChicken(Integer meta, int count) {
        super(Item.MUSIC_DISC_LAVA_CHICKEN, meta, count);
    }

    @Override
    public int getSoundEvent() {
        return LevelSoundEventPacket.SOUND_RECORD_LAVA_CHICKEN;
    }

    @Override
    public int getDuration() {
        return 134 * 20;
    }

    @Override
    public int getComparatorSignal() {
        return 9;
    }

    @Override
    public String getTranslationIdentifier() {
        return "record_lava_chicken";
    }
}
