package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggTraderLlama extends ItemSpawnEgg {
    public ItemSpawnEggTraderLlama() {
        this(0, 1);
    }

    public ItemSpawnEggTraderLlama(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggTraderLlama(Integer meta, int count) {
        super(TRADER_LLAMA_SPAWN_EGG, meta, count, "Trader Llama Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.TRADER_LLAMA;
    }
}
