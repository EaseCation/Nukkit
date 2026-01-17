package cn.nukkit.item;

import cn.nukkit.Player;

public class ItemEmptyMap extends Item {
    public static final int NORMAL_EMPTY_MAP = 0;
    public static final int BASIC_EMPTY_MAP = 1;
    public static final int ENHANCED_EMPTY_MAP = 2;
    public static final int UNDEFINED_EMPTY_MAP = 3;

    public ItemEmptyMap() {
        this(0, 1);
    }

    public ItemEmptyMap(Integer meta) {
        this(meta, 1);
    }

    public ItemEmptyMap(Integer meta, int count) {
        super(EMPTY_MAP, meta, count, "Empty Map");
    }

    @Override
    public String getDescriptionId() {
        if (getDamage() == ENHANCED_EMPTY_MAP) {
            return "item.emptyLocatorMap.name";
        }
        return "item.emptyMap.name";
    }

    @Override
    public boolean isStackedByData() {
        return true;
    }

    @Override
    public String getInteractText(Player player) {
        return "action.interact.createMap";
    }

    @Override
    public boolean isMap() {
        return true;
    }
}
