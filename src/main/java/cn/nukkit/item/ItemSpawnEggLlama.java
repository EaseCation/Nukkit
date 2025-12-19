package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggLlama extends ItemSpawnEgg {
    public ItemSpawnEggLlama() {
        this(0, 1);
    }

    public ItemSpawnEggLlama(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggLlama(Integer meta, int count) {
        super(LLAMA_SPAWN_EGG, meta, count, "Llama Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.LLAMA;
    }
}