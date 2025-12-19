package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggNpc extends ItemSpawnEgg {
    public ItemSpawnEggNpc() {
        this(0, 1);
    }

    public ItemSpawnEggNpc(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggNpc(Integer meta, int count) {
        super(NPC_SPAWN_EGG, meta, count, "NPC Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.NPC;
    }
}