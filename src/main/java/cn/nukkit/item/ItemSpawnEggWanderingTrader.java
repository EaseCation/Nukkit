package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggWanderingTrader extends ItemSpawnEgg {
    public ItemSpawnEggWanderingTrader() {
        this(0, 1);
    }

    public ItemSpawnEggWanderingTrader(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggWanderingTrader(Integer meta, int count) {
        super(WANDERING_TRADER_SPAWN_EGG, meta, count, "Wandering Trader Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.WANDERING_TRADER;
    }
}