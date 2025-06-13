package cn.nukkit.block.state.enumeration;

import cn.nukkit.Player;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;

public enum MinecraftFacingDirectionState implements BlockFaceState {
    DOWN("down", BlockFace.DOWN),
    UP("up", BlockFace.UP),
    NORTH("north", BlockFace.NORTH),
    SOUTH("south", BlockFace.SOUTH),
    WEST("west", BlockFace.WEST),
    EAST("east", BlockFace.EAST),
    ;

    private static final MinecraftFacingDirectionState[] VALUES = values();

    private final String name;
    private final BlockFace facing;

    MinecraftFacingDirectionState(String name, BlockFace facing) {
        this.name = name;
        this.facing = facing;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public BlockFace getBlockFace() {
        return facing;
    }

    public static MinecraftFacingDirectionState from(BlockFace facing) {
        return from(facing.getIndex());
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
