package cn.nukkit.block.state.enumeration;

import cn.nukkit.Player;

public enum MinecraftCardinalDirectionState {
    SOUTH("south"),
    WEST("west"),
    NORTH("north"),
    EAST("east"),
    ;

    private static final MinecraftCardinalDirectionState[] VALUES = values();

    private final String name;

    MinecraftCardinalDirectionState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static MinecraftCardinalDirectionState from(int value) {
        return VALUES[value];
    }

    public static MinecraftCardinalDirectionState getPlacementDirection(Player player) {
        return from(player.getHorizontalFacing().getHorizontalIndex());
    }
}
