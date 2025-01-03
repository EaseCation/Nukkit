package cn.nukkit.item;

/**
 * @author CreeperFace
 */
public abstract class ItemRecord extends Item {

    protected ItemRecord(int id, Integer meta, int count) {
        super(id, meta, count, "Music Disc");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    public abstract int getSoundEvent();

    public abstract int getDuration();

    public abstract int getComparatorSignal();

    public abstract String getTranslationIdentifier();
}
