package cn.nukkit.item;

import cn.nukkit.network.protocol.LevelSoundEventPacket;

public class ItemRecord5 extends ItemRecord {

    public ItemRecord5() {
        this(0, 1);
    }

    public ItemRecord5(Integer meta) {
        this(meta, 1);
    }

    public ItemRecord5(Integer meta, int count) {
        super(Item.MUSIC_DISC_5, meta, count);
    }

    @Override
    public int getSoundEvent() {
        return LevelSoundEventPacket.SOUND_RECORD_5;
    }

    @Override
    public String getTranslationIdentifier() {
        return "record_5";
    }
}
