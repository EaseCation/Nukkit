package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemSpawnEggTurtle extends ItemSpawnEgg {
    public ItemSpawnEggTurtle() {
        this(0, 1);
    }

    public ItemSpawnEggTurtle(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEggTurtle(Integer meta, int count) {
        super(TURTLE_SPAWN_EGG, meta, count, "Turtle Spawn Egg");
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public int getEntityId() {
        return EntityID.TURTLE;
    }
}