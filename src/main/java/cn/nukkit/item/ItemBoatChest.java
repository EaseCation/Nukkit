package cn.nukkit.item;

import cn.nukkit.entity.EntityFactory;
import cn.nukkit.entity.item.EntityBoatChest;

public class ItemBoatChest extends ItemBoat {
    public ItemBoatChest() {
        this(0, 1);
    }

    public ItemBoatChest(Integer meta) {
        this(meta, 1);
    }

    public ItemBoatChest(Integer meta, int count) {
        super(CHEST_BOAT, meta, count, "Chest Boat");
    }

    @Override
    protected EntityFactory getEntityFactory() {
        return EntityBoatChest::new;
    }
}
