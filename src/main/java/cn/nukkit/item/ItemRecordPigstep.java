package cn.nukkit.item;

import cn.nukkit.network.protocol.LevelSoundEventPacket;

/**
 * @author PetteriM1
 */
public class ItemRecordPigstep extends ItemRecord {

    public ItemRecordPigstep() {
        this(0, 1);
    }

    public ItemRecordPigstep(Integer meta) {
        this(meta, 1);
    }

    public ItemRecordPigstep(Integer meta, int count) {
        super(Item.MUSIC_DISC_PIGSTEP, meta, count);
    }

    @Override
    public int getSoundEvent() {
        return LevelSoundEventPacket.SOUND_RECORD_PIGSTEP;
    }

    @Override
    public String getTranslationIdentifier() {
        return "record_5";
    }
}
