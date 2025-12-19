package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggTropicalFish extends ItemSpawnEgg {
    public ItemSpawnEggTropicalFish() {
        this(0, 1);
    }

    public ItemSpawnEggTropicalFish(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggTropicalFish(Integer meta, int count) {
        super(TROPICAL_FISH_SPAWN_EGG, meta, count, "Tropical Fish Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.TROPICALFISH;
    }
}