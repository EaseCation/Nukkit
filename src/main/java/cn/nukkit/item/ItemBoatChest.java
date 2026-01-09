package cn.nukkit.item;

import cn.nukkit.entity.EntityFactory;
import cn.nukkit.entity.item.EntityBoatChest;

public abstract class ItemBoatChest extends ItemBoat {
    public static final int[] CHEST_BOATS = {
            OAK_CHEST_BOAT,
            SPRUCE_CHEST_BOAT,
            BIRCH_CHEST_BOAT,
            JUNGLE_CHEST_BOAT,
            ACACIA_CHEST_BOAT,
            DARK_OAK_CHEST_BOAT,
            MANGROVE_CHEST_BOAT,
            BAMBOO_CHEST_RAFT,
            CHERRY_CHEST_BOAT,
            PALE_OAK_CHEST_BOAT,
    };
    @SuppressWarnings("unused")
    private static final int[] BOATS = CHEST_BOATS;

    protected ItemBoatChest(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    @Override
    protected EntityFactory getEntityFactory() {
        return EntityBoatChest::new;
    }
}
