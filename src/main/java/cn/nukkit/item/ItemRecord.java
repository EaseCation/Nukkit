package cn.nukkit.item;

/**
 * @author CreeperFace
 */
public abstract class ItemRecord extends Item {

    protected ItemRecord(int id, Integer meta, int count) {
        super(id, 0, 1, "Music Disc");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    public abstract int getSoundEvent();

    public abstract String getTranslationIdentifier();
}
