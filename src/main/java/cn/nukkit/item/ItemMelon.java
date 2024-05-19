package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemMelon extends ItemEdible {

    public ItemMelon() {
        this(0, 1);
    }

    public ItemMelon(Integer meta) {
        this(meta, 1);
    }

    public ItemMelon(Integer meta, int count) {
        super(MELON_SLICE, meta, count, "Melon");
    }

    @Override
    public int getCompostableChance() {
        return 50;
    }
}
