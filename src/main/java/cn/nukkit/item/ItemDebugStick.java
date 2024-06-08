package cn.nukkit.item;

public class ItemDebugStick extends Item {
    public ItemDebugStick() {
        this(0, 1);
    }

    public ItemDebugStick(Integer meta) {
        this(meta, 1);
    }

    public ItemDebugStick(Integer meta, int count) {
        super(DEBUG_STICK, meta, count, "Debug Stick");
    }
}
