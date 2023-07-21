package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemMushroomStew extends ItemEdible {

    public ItemMushroomStew() {
        this(0, 1);
    }

    public ItemMushroomStew(Integer meta) {
        this(meta, 1);
    }

    public ItemMushroomStew(Integer meta, int count) {
        super(MUSHROOM_STEW, meta, 1, "Mushroom Stew");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
