package cn.nukkit.math;

import cn.nukkit.utils.Utils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import javax.annotation.Nullable;

public enum Orientation {
    DOWN_EAST("down_east", BlockFace.DOWN, BlockFace.EAST),
    DOWN_NORTH("down_north", BlockFace.DOWN, BlockFace.NORTH),
    DOWN_SOUTH("down_south", BlockFace.DOWN, BlockFace.SOUTH),
    DOWN_WEST("down_west", BlockFace.DOWN, BlockFace.WEST),
    UP_EAST("up_east", BlockFace.UP, BlockFace.EAST),
    UP_NORTH("up_north", BlockFace.UP, BlockFace.NORTH),
    UP_SOUTH("up_south", BlockFace.UP, BlockFace.SOUTH),
    UP_WEST("up_west", BlockFace.UP, BlockFace.WEST),
    WEST_UP("west_up", BlockFace.WEST, BlockFace.UP),
    EAST_UP("east_up", BlockFace.EAST, BlockFace.UP),
    NORTH_UP("north_up", BlockFace.NORTH, BlockFace.UP),
    SOUTH_UP("south_up", BlockFace.SOUTH, BlockFace.UP),
    ;

    private static final Orientation[] VALUES = values();

    private static final Int2ObjectMap<Orientation> LOOKUP = Utils.make(new Int2ObjectOpenHashMap<>(VALUES.length), lookup -> {
        for (Orientation orientation : VALUES) {
            lookup.put(lookupKey(orientation.front, orientation.top), orientation);
        }
    });

    private final String name;
    private final BlockFace front;
    private final BlockFace top;

    Orientation(String name, BlockFace front, BlockFace top) {
        this.name = name;
        this.front = front;
        this.top = top;
    }

    public BlockFace getFront() {
        return front;
    }

    public BlockFace getTop() {
        return top;
    }

    @Override
    public String toString() {
        return name;
    }

    @Nullable
    public static Orientation fromFrontAndTop(BlockFace front, BlockFace top) {
        return LOOKUP.get(lookupKey(front, top));
    }

    private static int lookupKey(BlockFace front, BlockFace top) {
        return top.ordinal() << 3 | front.ordinal();
    }

    public static Orientation[] getValues() {
        return VALUES;
    }
}
