package cn.nukkit.item;

import cn.nukkit.network.protocol.LevelSoundEventPacket;

/**
 * @author CreeperFace
 */
public class ItemRecordBlocks extends ItemRecord {

    public ItemRecordBlocks() {
        this(0, 1);
    }

    public ItemRecordBlocks(Integer meta) {
        this(meta, 1);
    }

    public ItemRecordBlocks(Integer meta, int count) {
        super(Item.MUSIC_DISC_BLOCKS, meta, count);
    }

    @Override
    public int getSoundEvent() {
        return LevelSoundEventPacket.SOUND_RECORD_BLOCKS;
    }

    @Override
    public int getDuration() {
        return 345 * 20;
    }

    @Override
    public int getComparatorSignal() {
        return 3;
    }

    @Override
    public String getTranslationIdentifier() {
        return "record_blocks";
    }
}
