package cn.nukkit.block.state;

import cn.nukkit.block.state.enumeration.*;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockFace.Axis;
import cn.nukkit.math.Mirror;
import cn.nukkit.math.OctahedralGroup;
import cn.nukkit.math.Rotation;
import cn.nukkit.utils.Utils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.UnaryOperator;

import static cn.nukkit.block.state.BlockStateIntegerValues.*;
import static cn.nukkit.block.state.BlockStates.*;

final class BlockStateBehaviours {
    private static final Int2ObjectMap<BiFunction<BlockInstance, Rotation, BlockInstance>> ROTATIONS = new Int2ObjectOpenHashMap<>();
    private static final Int2ObjectMap<BiFunction<BlockInstance, Mirror, BlockInstance>> MIRRORS = new Int2ObjectOpenHashMap<>();

    static {
        // E W S N
        register(CORAL_DIRECTION, BlockStateBehaviours::rotateCoralDirection, BlockStateBehaviours::mirrorCoralDirection);

        // S W N E (General) || E W S N (Trapdoors)
        register(DIRECTION, BlockStateBehaviours::rotateDirection, BlockStateBehaviours::mirrorDirection);

        // D U N S W E (General) || D U S N E W (End Rod)
        register(FACING_DIRECTION, BlockStateBehaviours::rotateFacingDirection, BlockStateBehaviours::mirrorFacingDirection);

        // S S_SW SW W_SW W W_NW NW N_NW N N_NE NE E_NE E E_SE SE S_SE
        register(GROUND_SIGN_DIRECTION, BlockStateBehaviours::rotateGroundSignDirection, BlockStateBehaviours::mirrorGroundSignDirection);

        // P NW N NE W U E SW S SE B P P P C B
        register(HUGE_MUSHROOM_BITS, BlockStateBehaviours::rotateHugeMushroomBits, BlockStateBehaviours::mirrorHugeMushroomBits);

        // DX E W S N UZ UX DZ
        register(LEVER_DIRECTION, BlockStateBehaviours::rotateLeverDirection, BlockStateBehaviours::mirrorLeverDirection);

        // D U N S W E
        register(MINECRAFT_BLOCK_FACE, BlockStateBehaviours::rotateMinecraftBlockFace, BlockStateBehaviours::mirrorMinecraftBlockFace);

        // S W N E
        register(MINECRAFT_CARDINAL_DIRECTION, BlockStateBehaviours::rotateMinecraftCardinalDirection, BlockStateBehaviours::mirrorMinecraftCardinalDirection);

        register(MINECRAFT_CONNECTION_EAST, BlockStateBehaviours::rotateMinecraftCardinalConnections, BlockStateBehaviours::mirrorMinecraftCardinalConnections);
        //register(MINECRAFT_CONNECTION_NORTH, BlockStateBehaviours::rotateMinecraftCardinalConnections, BlockStateBehaviours::mirrorMinecraftCardinalConnections);
        //register(MINECRAFT_CONNECTION_SOUTH, BlockStateBehaviours::rotateMinecraftCardinalConnections, BlockStateBehaviours::mirrorMinecraftCardinalConnections);
        //register(MINECRAFT_CONNECTION_WEST, BlockStateBehaviours::rotateMinecraftCardinalConnections, BlockStateBehaviours::mirrorMinecraftCardinalConnections);

        // D U N S W E
        register(MINECRAFT_FACING_DIRECTION, BlockStateBehaviours::rotateMinecraftFacingDirection, BlockStateBehaviours::mirrorMinecraftFacingDirection);

        // D U S W N E
        register(MULTI_FACE_DIRECTION_BITS, BlockStateBehaviours::rotateMultiFaceDirectionBits, BlockStateBehaviours::mirrorMultiFaceDirectionBits);

        register(ORIENTATION, BlockStateBehaviours::rotateOrientation, BlockStateBehaviours::mirrorOrientation);

        register(PALE_MOSS_CARPET_SIDE_EAST, BlockStateBehaviours::rotatePaleMossCarpetSide, BlockStateBehaviours::mirrorPaleMossCarpetSide);
        //register(PALE_MOSS_CARPET_SIDE_NORTH, BlockStateBehaviours::rotatePaleMossCarpetSide, BlockStateBehaviours::mirrorPaleMossCarpetSide);
        //register(PALE_MOSS_CARPET_SIDE_SOUTH, BlockStateBehaviours::rotatePaleMossCarpetSide, BlockStateBehaviours::mirrorPaleMossCarpetSide);
        //register(PALE_MOSS_CARPET_SIDE_WEST, BlockStateBehaviours::rotatePaleMossCarpetSide, BlockStateBehaviours::mirrorPaleMossCarpetSide);

        register(PILLAR_AXIS, BlockStateBehaviours::rotatePillarAxis, null);

        register(PORTAL_AXIS, BlockStateBehaviours::rotatePortalAxis, null);

        // Z X E W N S SE SW NW NE
        register(RAIL_DIRECTION, BlockStateBehaviours::rotateRailDirection, BlockStateBehaviours::mirrorRailDirection);

        // N W S E (Down) || N E S W (Up)
        register(ROTATION, BlockStateBehaviours::rotateRotation, BlockStateBehaviours::mirrorRotation);

        // D E W S N U
        register(TORCH_FACING_DIRECTION, BlockStateBehaviours::rotateTorchFacingDirection, BlockStateBehaviours::mirrorTorchFacingDirection);

        // S W N E
        register(VINE_DIRECTION_BITS, BlockStateBehaviours::rotateVineDirectionBits, BlockStateBehaviours::mirrorVineDirectionBits);

        register(WALL_CONNECTION_TYPE_EAST, BlockStateBehaviours::rotateWallConnectionType, BlockStateBehaviours::mirrorWallConnectionType);
        //register(WALL_CONNECTION_TYPE_NORTH, BlockStateBehaviours::rotateWallConnectionType, BlockStateBehaviours::mirrorWallConnectionType);
        //register(WALL_CONNECTION_TYPE_SOUTH, BlockStateBehaviours::rotateWallConnectionType, BlockStateBehaviours::mirrorWallConnectionType);
        //register(WALL_CONNECTION_TYPE_WEST, BlockStateBehaviours::rotateWallConnectionType, BlockStateBehaviours::mirrorWallConnectionType);

        // E W S N
        register(WEIRDO_DIRECTION, BlockStateBehaviours::rotateWeirdoDirection, BlockStateBehaviours::mirrorWeirdoDirection);

        // ground_sign_direction (Standing) || direction (Wall)
        register(CHALKBOARD_DIRECTION, BlockStateBehaviours::rotateChalkboardDirection, BlockStateBehaviours::mirrorChalkboardDirection);
    }

    private static void register(BlockState state, BiFunction<BlockInstance, Rotation, BlockInstance> rotation, BiFunction<BlockInstance, Mirror, BlockInstance> mirror) {
        if (rotation != null) {
            ROTATIONS.put(state.id, rotation);
        }
        if (mirror != null) {
            MIRRORS.put(state.id, mirror);
        }
    }

    @Nullable
    public static BiFunction<BlockInstance, Rotation, BlockInstance> getRotationBehaviour(String blockName, BlockState state) {
        if (state == DIRECTION && isTrapdoor(blockName)) {
            return BlockStateBehaviours::rotateTrapdoorDirection;
        }
        return getBehaviour(ROTATIONS, state);
    }

    @Nullable
    public static BiFunction<BlockInstance, Mirror, BlockInstance> getMirrorBehaviour(String blockName, BlockState state) {
        if (state == DIRECTION && isTrapdoor(blockName)) {
            return BlockStateBehaviours::mirrorTrapdoorDirection;
        }
        return getBehaviour(MIRRORS, state);
    }

    private static boolean isTrapdoor(String blockName) {
        return blockName.endsWith("trapdoor") && blockName.startsWith("minecraft:");
    }

    @Nullable
    private static <T> BiFunction<BlockInstance, T, BlockInstance> getBehaviour(Int2ObjectMap<BiFunction<BlockInstance, T, BlockInstance>> registry, BlockState state) {
        return registry.get(state.id);
    }

    private static BlockInstance rotateBlockFace(BlockInstance block, BlockState state, Rotation rotation) {
        return rotateBlockFace(block, state, rotation::rotate);
    }

    private static BlockInstance mirrorBlockFace(BlockInstance block, BlockState state, Mirror mirror) {
        return rotateBlockFace(block, state, mirror::mirror);
    }

    private static BlockInstance rotateBlockFace(BlockInstance block, BlockState state, UnaryOperator<BlockFace> rotation) {
        return block.setState(state, rotation.apply(BlockFace.fromOrdinal(block.getState(state))).getIndex());
    }

    private static BlockInstance rotateHorizontalBlockFace(BlockInstance block, BlockState state, Rotation rotation) {
        return rotateHorizontalBlockFace(block, state, rotation::rotate);
    }

    private static BlockInstance mirrorHorizontalBlockFace(BlockInstance block, BlockState state, Mirror mirror) {
        return rotateHorizontalBlockFace(block, state, mirror::mirror);
    }

    private static BlockInstance rotateHorizontalBlockFace(BlockInstance block, BlockState state, UnaryOperator<BlockFace> rotation) {
        return block.setState(state, rotation.apply(BlockFace.fromHorizontalIndex(block.getState(state))).getHorizontalIndex());
    }

    private static BlockInstance rotateReversedHorizontalBlockFace(BlockInstance block, BlockState state, Rotation rotation) {
        return rotateReversedHorizontalBlockFace(block, state, rotation::rotate);
    }

    private static BlockInstance mirrorReversedHorizontalBlockFace(BlockInstance block, BlockState state, Mirror mirror) {
        return rotateReversedHorizontalBlockFace(block, state, mirror::mirror);
    }

    private static BlockInstance rotateReversedHorizontalBlockFace(BlockInstance block, BlockState state, UnaryOperator<BlockFace> rotation) {
        return block.setState(state, rotation.apply(BlockFace.fromReversedHorizontalIndex(block.getState(state))).getReversedHorizontalIndex());
    }

    private static BlockInstance rotateAxis(BlockInstance block, BlockState state, Rotation rotation) {
        return block.setState(state, rotation.rotate(Axis.fromIndex(block.getState(state))).getIndex());
    }

    private static BlockInstance rotateGroundDirection(BlockInstance block, BlockState state, Rotation rotation) {
        return rotateGroundDirection(block, state, rotation::rotate);
    }

    private static BlockInstance mirrorGroundDirection(BlockInstance block, BlockState state, Mirror mirror) {
        return rotateGroundDirection(block, state, mirror::mirror);
    }

    private static BlockInstance rotateGroundDirection(BlockInstance block, BlockState state, IntUnaryOperator rotation) {
        return block.setState(state, rotation.applyAsInt(block.getState(state)));
    }

    private static BlockInstance rotate(BlockInstance block, BlockState stateS, BlockState stateW, BlockState stateN, BlockState stateE, Rotation rotation) {
        int valueS = block.getState(stateS);
        int valueW = block.getState(stateW);
        int valueN = block.getState(stateN);
        int valueE = block.getState(stateE);
        return switch (rotation) {
            case CLOCKWISE_90 -> block
                    .setState(stateS, valueE)
                    .setState(stateW, valueS)
                    .setState(stateN, valueW)
                    .setState(stateE, valueN);
            case CLOCKWISE_180 -> block
                    .setState(stateS, valueN)
                    .setState(stateW, valueE)
                    .setState(stateN, valueS)
                    .setState(stateE, valueW);
            case COUNTERCLOCKWISE_90 -> block
                    .setState(stateS, valueW)
                    .setState(stateW, valueN)
                    .setState(stateN, valueE)
                    .setState(stateE, valueS);
            default -> block;
        };
    }

    private static BlockInstance mirror(BlockInstance block, BlockState stateS, BlockState stateW, BlockState stateN, BlockState stateE, Mirror mirror) {
        return switch (mirror) {
            case X -> block
                    .setState(stateN, block.getState(stateS))
                    .setState(stateS, block.getState(stateN));
            case Z -> block
                    .setState(stateE, block.getState(stateW))
                    .setState(stateW, block.getState(stateE));
            default -> block;
        };
    }

    private static BlockInstance rotateCoralDirection(BlockInstance block, Rotation rotation) {
        return rotateReversedHorizontalBlockFace(block, CORAL_DIRECTION, rotation);
    }

    private static BlockInstance mirrorCoralDirection(BlockInstance block, Mirror mirror) {
        return mirrorReversedHorizontalBlockFace(block, CORAL_DIRECTION, mirror);
    }

    private static BlockInstance rotateDirection(BlockInstance block, Rotation rotation) {
        return rotateHorizontalBlockFace(block, DIRECTION, rotation);
    }

    private static BlockInstance mirrorDirection(BlockInstance block, Mirror mirror) {
        return mirrorHorizontalBlockFace(block, DIRECTION, mirror);
    }

    private static BlockInstance rotateFacingDirection(BlockInstance block, Rotation rotation) {
        return rotateBlockFace(block, FACING_DIRECTION, rotation);
    }

    private static BlockInstance mirrorFacingDirection(BlockInstance block, Mirror mirror) {
        return mirrorBlockFace(block, FACING_DIRECTION, mirror);
    }

    private static BlockInstance rotateGroundSignDirection(BlockInstance block, Rotation rotation) {
        return rotateGroundDirection(block, GROUND_SIGN_DIRECTION, rotation);
    }

    private static BlockInstance mirrorGroundSignDirection(BlockInstance block, Mirror mirror) {
        return mirrorGroundDirection(block, GROUND_SIGN_DIRECTION, mirror);
    }

    private static BlockInstance rotateHugeMushroomBits(BlockInstance block, Rotation rotation) {
        BlockState state = HUGE_MUSHROOM_BITS;
        int value = block.getState(state);
        if (value >= 10) {
            return block;
        }
        return switch (rotation) {
            case CLOCKWISE_90 -> block.setState(state, value * 3 % 10);
            case CLOCKWISE_180 -> block.setState(state, value * 9 % 10);
            case COUNTERCLOCKWISE_90 -> block.setState(state, value * 7 % 10);
            default -> block;
        };
    }

    private static final Map<Mirror, int[]> HUGE_MUSHROOM_BITS_MIRROR = Utils.make(new EnumMap<>(Mirror.class), lookup -> {
        int variantCount = MAX_HUGE_MUSHROOM_BITS + 1;
        int[] none = new int[variantCount];
        Arrays.fill(none, -1);
        lookup.put(Mirror.NONE, none);

        int[] x = none.clone();
        x[1] = 7;
        x[2] = 8;
        x[3] = 9;
        x[7] = 1;
        x[8] = 2;
        x[9] = 3;
        lookup.put(Mirror.X, x);

        int[] z = none.clone();
        z[1] = 3;
        z[3] = 1;
        z[4] = 6;
        z[6] = 4;
        z[7] = 9;
        z[9] = 7;
        lookup.put(Mirror.Z, z);
    });

    private static BlockInstance mirrorHugeMushroomBits(BlockInstance block, Mirror mirror) {
        BlockState state = HUGE_MUSHROOM_BITS;
        int value = block.getState(state);
        if (value >= 10) {
            return block;
        }
        int newValue = HUGE_MUSHROOM_BITS_MIRROR.get(mirror)[block.getState(state)];
        return newValue == -1 ? block : block.setState(state, newValue);
    }

    private static BlockInstance rotateLeverDirection(BlockInstance block, Rotation rotation) {
        return rotateLeverDirection(block, LEVER_DIRECTION_ROTATION, rotation);
    }

    private static BlockInstance mirrorLeverDirection(BlockInstance block, Mirror mirror) {
        return rotateLeverDirection(block, LEVER_DIRECTION_MIRROR, mirror);
    }

    private static final Map<Rotation, Map<LeverDirectionState, LeverDirectionState>> LEVER_DIRECTION_ROTATION = Utils.make(new EnumMap<>(Rotation.class), lookup -> {
        lookup.put(Rotation.NONE, Collections.emptyMap());

        Map<LeverDirectionState, LeverDirectionState> cw90 = new EnumMap<>(LeverDirectionState.class);
        cw90.put(LeverDirectionState.DOWN_EAST_WEST, LeverDirectionState.DOWN_NORTH_SOUTH);
        cw90.put(LeverDirectionState.EAST, LeverDirectionState.SOUTH);
        cw90.put(LeverDirectionState.WEST, LeverDirectionState.NORTH);
        cw90.put(LeverDirectionState.SOUTH, LeverDirectionState.WEST);
        cw90.put(LeverDirectionState.NORTH, LeverDirectionState.EAST);
        cw90.put(LeverDirectionState.UP_NORTH_SOUTH, LeverDirectionState.UP_EAST_WEST);
        cw90.put(LeverDirectionState.UP_EAST_WEST, LeverDirectionState.UP_NORTH_SOUTH);
        cw90.put(LeverDirectionState.DOWN_NORTH_SOUTH, LeverDirectionState.DOWN_EAST_WEST);
        lookup.put(Rotation.CLOCKWISE_90, cw90);

        Map<LeverDirectionState, LeverDirectionState> cw180 = new EnumMap<>(LeverDirectionState.class);
        cw180.put(LeverDirectionState.EAST, LeverDirectionState.WEST);
        cw180.put(LeverDirectionState.WEST, LeverDirectionState.EAST);
        cw180.put(LeverDirectionState.SOUTH, LeverDirectionState.NORTH);
        cw180.put(LeverDirectionState.NORTH, LeverDirectionState.SOUTH);
        lookup.put(Rotation.CLOCKWISE_180, cw180);

        Map<LeverDirectionState, LeverDirectionState> ccw90 = new EnumMap<>(cw90);
        ccw90.put(LeverDirectionState.EAST, LeverDirectionState.NORTH);
        ccw90.put(LeverDirectionState.WEST, LeverDirectionState.SOUTH);
        ccw90.put(LeverDirectionState.SOUTH, LeverDirectionState.EAST);
        ccw90.put(LeverDirectionState.NORTH, LeverDirectionState.WEST);
        lookup.put(Rotation.COUNTERCLOCKWISE_90, ccw90);
    });

    private static final Map<Mirror, Map<LeverDirectionState, LeverDirectionState>> LEVER_DIRECTION_MIRROR = Utils.make(new EnumMap<>(Mirror.class), lookup -> {
        lookup.put(Mirror.NONE, Collections.emptyMap());

        Map<LeverDirectionState, LeverDirectionState> x = new EnumMap<>(LeverDirectionState.class);
        x.put(LeverDirectionState.SOUTH, LeverDirectionState.NORTH);
        x.put(LeverDirectionState.NORTH, LeverDirectionState.SOUTH);
        lookup.put(Mirror.X, x);

        Map<LeverDirectionState, LeverDirectionState> z = new EnumMap<>(LeverDirectionState.class);
        z.put(LeverDirectionState.EAST, LeverDirectionState.WEST);
        z.put(LeverDirectionState.WEST, LeverDirectionState.EAST);
        lookup.put(Mirror.Z, z);
    });

    private static <T> BlockInstance rotateLeverDirection(BlockInstance block, Map<T, Map<LeverDirectionState, LeverDirectionState>> lookup, T rotation) {
        EnumBlockState<LeverDirectionState> state = LEVER_DIRECTION;
        LeverDirectionState newValue = lookup.get(rotation).get(state.getValues().get(block.getState(state)));
        return newValue == null ? block : block.setState(state, newValue);
    }

    private static BlockInstance rotateMinecraftBlockFace(BlockInstance block, Rotation rotation) {
        return rotateBlockFace(block, MINECRAFT_BLOCK_FACE, rotation);
    }

    private static BlockInstance mirrorMinecraftBlockFace(BlockInstance block, Mirror mirror) {
        return mirrorBlockFace(block, MINECRAFT_BLOCK_FACE, mirror);
    }

    private static BlockInstance rotateMinecraftCardinalDirection(BlockInstance block, Rotation rotation) {
        return rotateHorizontalBlockFace(block, MINECRAFT_CARDINAL_DIRECTION, rotation);
    }

    private static BlockInstance mirrorMinecraftCardinalDirection(BlockInstance block, Mirror mirror) {
        return mirrorHorizontalBlockFace(block, MINECRAFT_CARDINAL_DIRECTION, mirror);
    }

    private static BlockInstance rotateMinecraftCardinalConnections(BlockInstance block, Rotation rotation) {
        return rotate(block, MINECRAFT_CONNECTION_SOUTH, MINECRAFT_CONNECTION_WEST, MINECRAFT_CONNECTION_NORTH, MINECRAFT_CONNECTION_EAST, rotation);
    }

    private static BlockInstance mirrorMinecraftCardinalConnections(BlockInstance block, Mirror mirror) {
        return mirror(block, MINECRAFT_CONNECTION_SOUTH, MINECRAFT_CONNECTION_WEST, MINECRAFT_CONNECTION_NORTH, MINECRAFT_CONNECTION_EAST, mirror);
    }

    private static BlockInstance rotateMinecraftFacingDirection(BlockInstance block, Rotation rotation) {
        return rotateBlockFace(block, MINECRAFT_FACING_DIRECTION, rotation);
    }

    private static BlockInstance mirrorMinecraftFacingDirection(BlockInstance block, Mirror mirror) {
        return mirrorBlockFace(block, MINECRAFT_FACING_DIRECTION, mirror);
    }

    private static BlockInstance rotateMultiFaceDirectionBits(BlockInstance block, Rotation rotation) {
        BlockState state = MULTI_FACE_DIRECTION_BITS;
        int value = block.getState(state);
        if (value <= 0b11 || value == 0b111111) {
            return block;
        }
        int vertical = value & 0b11;
        int horizontal = value & 0b111100;
        return switch (rotation) {
            case CLOCKWISE_90 -> block.setState(state, (horizontal << 1 | horizontal >> 3) & 0b111100 | vertical);
            case CLOCKWISE_180 -> block.setState(state, (horizontal << 2 | horizontal >> 2) & 0b111100 | vertical);
            case COUNTERCLOCKWISE_90 -> block.setState(state, (horizontal << 3 | horizontal >> 1) & 0b111100 | vertical);
            default -> block;
        };
    }

    private static BlockInstance mirrorMultiFaceDirectionBits(BlockInstance block, Mirror mirror) {
        BlockState state = MULTI_FACE_DIRECTION_BITS;
        int value = block.getState(state);
        if (value <= 0b11 || value == 0b111111) {
            return block;
        }
        int horizontal = value & 0b111100;
        return switch (mirror) {
            case X -> block.setState(state, (horizontal << 2 | horizontal >> 2) & 0b010100 | value & 0b101011);
            case Z -> block.setState(state, (horizontal << 2 | horizontal >> 2) & 0b101000 | value & 0b010111);
            default -> block;
        };
    }

    private static BlockInstance rotateOrientation(BlockInstance block, Rotation rotation) {
        return rotateOrientation(block, rotation.rotation());
    }

    private static BlockInstance mirrorOrientation(BlockInstance block, Mirror mirror) {
        return rotateOrientation(block, mirror.rotation());
    }

    private static BlockInstance rotateOrientation(BlockInstance block, OctahedralGroup rotation) {
        EnumBlockState<OrientationState> state = ORIENTATION;
        return block.setState(state, rotation.rotate(state.getValues().get(block.getState(state)).getOrientation()).ordinal());
    }

    private static BlockInstance rotatePaleMossCarpetSide(BlockInstance block, Rotation rotation) {
        return rotate(block, PALE_MOSS_CARPET_SIDE_SOUTH, PALE_MOSS_CARPET_SIDE_WEST, PALE_MOSS_CARPET_SIDE_NORTH, PALE_MOSS_CARPET_SIDE_EAST, rotation);
    }

    private static BlockInstance mirrorPaleMossCarpetSide(BlockInstance block, Mirror mirror) {
        return mirror(block, PALE_MOSS_CARPET_SIDE_SOUTH, PALE_MOSS_CARPET_SIDE_WEST, PALE_MOSS_CARPET_SIDE_NORTH, PALE_MOSS_CARPET_SIDE_EAST, mirror);
    }

    private static BlockInstance rotatePillarAxis(BlockInstance block, Rotation rotation) {
        return rotateAxis(block, PILLAR_AXIS, rotation);
    }

    private static BlockInstance rotatePortalAxis(BlockInstance block, Rotation rotation) {
        return rotateAxis(block, PORTAL_AXIS, rotation);
    }

    private static BlockInstance rotateRailDirection(BlockInstance block, Rotation rotation) {
        return rotateRailDirection(block, RAIL_DIRECTION_ROTATION, rotation);
    }

    private static BlockInstance mirrorRailDirection(BlockInstance block, Mirror mirror) {
        return rotateRailDirection(block, RAIL_DIRECTION_MIRROR, mirror);
    }

    private static final Map<Rotation, int[]> RAIL_DIRECTION_ROTATION = Utils.make(new EnumMap<>(Rotation.class), lookup -> {
        int variantCount = MAX_RAIL_DIRECTION + 1;
        int[] none = new int[variantCount];
        Arrays.fill(none, -1);
        lookup.put(Rotation.NONE, none);

        int[] cw90 = none.clone();
        cw90[0] = 1;
        cw90[1] = 0;
        cw90[2] = 5;
        cw90[3] = 4;
        cw90[4] = 2;
        cw90[5] = 3;
        cw90[6] = 7;
        cw90[7] = 8;
        cw90[8] = 9;
        cw90[9] = 6;
        lookup.put(Rotation.CLOCKWISE_90, cw90);

        int[] cw180 = none.clone();
        cw180[2] = 3;
        cw180[3] = 2;
        cw180[4] = 5;
        cw180[5] = 4;
        cw180[6] = 8;
        cw180[7] = 9;
        cw180[8] = 6;
        cw180[9] = 7;
        lookup.put(Rotation.CLOCKWISE_180, cw180);

        int[] ccw90 = none.clone();
        ccw90[0] = 1;
        ccw90[1] = 0;
        ccw90[2] = 4;
        ccw90[3] = 5;
        ccw90[4] = 3;
        ccw90[5] = 2;
        ccw90[6] = 9;
        ccw90[7] = 6;
        ccw90[8] = 7;
        ccw90[9] = 8;
        lookup.put(Rotation.COUNTERCLOCKWISE_90, ccw90);
    });

    private static final Map<Mirror, int[]> RAIL_DIRECTION_MIRROR = Utils.make(new EnumMap<>(Mirror.class), lookup -> {
        int variantCount = MAX_RAIL_DIRECTION + 1;
        int[] none = new int[variantCount];
        Arrays.fill(none, -1);
        lookup.put(Mirror.NONE, none);

        int[] x = none.clone();
        x[4] = 5;
        x[5] = 4;
        x[6] = 9;
        x[7] = 8;
        x[8] = 7;
        x[9] = 6;
        lookup.put(Mirror.X, x);

        int[] z = none.clone();
        z[2] = 3;
        z[3] = 2;
        z[6] = 7;
        z[7] = 6;
        z[8] = 9;
        z[9] = 8;
        lookup.put(Mirror.Z, z);
    });

    private static <T> BlockInstance rotateRailDirection(BlockInstance block, Map<T, int[]> lookup, T rotation) {
        BlockState state = RAIL_DIRECTION;
        int newValue = lookup.get(rotation)[block.getState(state)];
        return newValue == -1 ? block : block.setState(state, newValue);
    }

    private static BlockInstance rotateRotation(BlockInstance block, Rotation rotation) {
        return rotateRotation(block, rotation::rotate);
    }

    private static BlockInstance mirrorRotation(BlockInstance block, Mirror mirror) {
        return rotateRotation(block, mirror::mirror);
    }

    private static final BlockFace[][] JIGSAW_ROTATION_BY_VALUE = new BlockFace[][]{
            new BlockFace[]{BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH, BlockFace.EAST},
            new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST},
    };

    private static int getJigsawRotationValue(BlockFace rotation, boolean facingUp) {
        return switch (rotation) {
            default -> 0;
            case WEST -> facingUp ? 3 : 1;
            case SOUTH -> 2;
            case EAST -> facingUp ? 1 : 3;
        };
    }

    private static BlockInstance rotateRotation(BlockInstance block, UnaryOperator<BlockFace> rotation) {
        BlockState state = ROTATION;
        BlockFace facing = BlockFace.fromOrdinal(block.getState(FACING_DIRECTION));
        if (facing.isHorizontal()) {
            return block;
        }
        return block.setState(state, getJigsawRotationValue(rotation.apply(JIGSAW_ROTATION_BY_VALUE[facing.getIndex()][block.getState(state)]), facing == BlockFace.UP));
    }

    private static BlockInstance rotateTorchFacingDirection(BlockInstance block, Rotation rotation) {
        return rotateTorchFacingDirection(block, rotation::rotate);
    }

    private static BlockInstance mirrorTorchFacingDirection(BlockInstance block, Mirror mirror) {
        return rotateTorchFacingDirection(block, mirror::mirror);
    }

    private static BlockInstance rotateTorchFacingDirection(BlockInstance block, UnaryOperator<BlockFace> rotation) {
        BlockState state = TORCH_FACING_DIRECTION;
        int value = block.getState(state);
        if (value == 0 || value == 5) {
            return block;
        }
        return block.setState(state, 6 - rotation.apply(BlockFace.fromOrdinal(6 - value)).getIndex());
    }

    private static BlockInstance rotateVineDirectionBits(BlockInstance block, Rotation rotation) {
        BlockState state = VINE_DIRECTION_BITS;
        int value = block.getState(state);
        return switch (rotation) {
            case CLOCKWISE_90 -> block.setState(state, (value << 1 | value >> 3) & 0xf);
            case CLOCKWISE_180 -> block.setState(state, (value << 2 | value >> 2) & 0xf);
            case COUNTERCLOCKWISE_90 -> block.setState(state, (value << 3 | value >> 1) & 0xf);
            default -> block;
        };
    }

    private static BlockInstance mirrorVineDirectionBits(BlockInstance block, Mirror mirror) {
        BlockState state = VINE_DIRECTION_BITS;
        int value = block.getState(state);
        return switch (mirror) {
            case X -> block.setState(state, (value << 2 | value >> 2) & 0b0101 | value & 0b1010);
            case Z -> block.setState(state, (value << 2 | value >> 2) & 0b1010 | value & 0b0101);
            default -> block;
        };
    }

    private static BlockInstance rotateWallConnectionType(BlockInstance block, Rotation rotation) {
        return rotate(block, WALL_CONNECTION_TYPE_SOUTH, WALL_CONNECTION_TYPE_WEST, WALL_CONNECTION_TYPE_NORTH, WALL_CONNECTION_TYPE_EAST, rotation);
    }

    private static BlockInstance mirrorWallConnectionType(BlockInstance block, Mirror mirror) {
        return mirror(block, WALL_CONNECTION_TYPE_SOUTH, WALL_CONNECTION_TYPE_WEST, WALL_CONNECTION_TYPE_NORTH, WALL_CONNECTION_TYPE_EAST, mirror);
    }

    private static BlockInstance rotateWeirdoDirection(BlockInstance block, Rotation rotation) {
        return rotateReversedHorizontalBlockFace(block, WEIRDO_DIRECTION, rotation);
    }

    private static BlockInstance mirrorWeirdoDirection(BlockInstance block, Mirror mirror) {
        return mirrorReversedHorizontalBlockFace(block, WEIRDO_DIRECTION, mirror);
    }

    private static BlockInstance rotateTrapdoorDirection(BlockInstance block, Rotation rotation) {
        return rotateReversedHorizontalBlockFace(block, DIRECTION, rotation);
    }

    private static BlockInstance mirrorTrapdoorDirection(BlockInstance block, Mirror mirror) {
        return mirrorReversedHorizontalBlockFace(block, DIRECTION, mirror);
    }

    private static BlockInstance rotateChalkboardDirection(BlockInstance block, Rotation rotation) {
        return rotateChalkboardDirection(block, true, rotation); //TODO: block entity OnGround state
    }

    private static BlockInstance rotateChalkboardDirection(BlockInstance block, boolean onGround, Rotation rotation) {
        return onGround ? rotateGroundChalkboardDirection(block, rotation) : rotateWallChalkboardDirection(block, rotation);
    }

    private static BlockInstance mirrorChalkboardDirection(BlockInstance block, Mirror mirror) {
        return mirrorChalkboardDirection(block, true, mirror); //TODO: block entity OnGround state
    }

    private static BlockInstance mirrorChalkboardDirection(BlockInstance block, boolean onGround, Mirror mirror) {
        return onGround ? mirrorGroundChalkboardDirection(block, mirror) : mirrorWallChalkboardDirection(block, mirror);
    }

    private static BlockInstance rotateGroundChalkboardDirection(BlockInstance block, Rotation rotation) {
        return rotateGroundDirection(block, CHALKBOARD_DIRECTION, rotation);
    }

    private static BlockInstance mirrorGroundChalkboardDirection(BlockInstance block, Mirror mirror) {
        return mirrorGroundDirection(block, CHALKBOARD_DIRECTION, mirror);
    }

    private static BlockInstance rotateWallChalkboardDirection(BlockInstance block, Rotation rotation) {
        return rotateHorizontalBlockFace(block, CHALKBOARD_DIRECTION, rotation);
    }

    private static BlockInstance mirrorWallChalkboardDirection(BlockInstance block, Mirror mirror) {
        return mirrorHorizontalBlockFace(block, CHALKBOARD_DIRECTION, mirror);
    }
}
