package cn.nukkit.block.state.enumeration;

import cn.nukkit.Player;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;

public enum MinecraftFacingDirectionState {
    DOWN("down"),
    UP("up"),
    NORTH("north"),
    SOUTH("south"),
    WEST("west"),
    EAST("east"),
    ;

    private static final MinecraftFacingDirectionState[] VALUES = values();

    private final String name;

    MinecraftFacingDirectionState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static MinecraftFacingDirectionState from(int value) {
        return VALUES[value];
    }

    public static MinecraftFacingDirectionState getPlacementDirection(Player player, Vector3 pos) {
        return getPlacementDirection(player, pos.getFloorX(), pos.getFloorY(), pos.getFloorZ());
    }

    public static MinecraftFacingDirectionState getPlacementDirection(Player player, BlockVector3 pos) {
        return getPlacementDirection(player, pos.getX(), pos.getY(), pos.getZ());
    }

    public static MinecraftFacingDirectionState getPlacementDirection(Player player, int x, int y, int z) {
        if (Math.abs(player.getFloorX() - x) <= 1 && Math.abs(player.getFloorZ() - z) <= 1) {
            float eyeY = player.getEyeY();
            if (eyeY - y > 2) {
                return MinecraftFacingDirectionState.DOWN;
            } else if (y - eyeY > 0) {
                return MinecraftFacingDirectionState.UP;
            }
        }
        return from(player.getHorizontalFacing().getIndex());
    }
}
