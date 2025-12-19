package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggAgent extends ItemSpawnEgg {
    public ItemSpawnEggAgent() {
        this(0, 1);
    }

    public ItemSpawnEggAgent(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggAgent(Integer meta, int count) {
        super(AGENT_SPAWN_EGG, meta, count, "Agent Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.AGENT;
    }
}