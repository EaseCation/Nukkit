package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggAxolotl extends ItemSpawnEgg {
    public ItemSpawnEggAxolotl() {
        this(0, 1);
    }

    public ItemSpawnEggAxolotl(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggAxolotl(Integer meta, int count) {
        super(AXOLOTL_SPAWN_EGG, meta, count, "Axolotl Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.AXOLOTL;
    }
}