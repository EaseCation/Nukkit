package cn.nukkit.block.state;

import cn.nukkit.block.state.enumeration.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static cn.nukkit.block.state.BlockStateIntegerValues.*;

// This file is generated automatically, do not edit it manually.
public final class BlockStates {
    private static final List<BlockState> REGISTRY = new ArrayList<>();
    private static final AtomicInteger INTERNAL_ID_ALLOCATOR = new AtomicInteger();

    public static final BooleanBlockState ACTIVE = register(new BooleanBlockState(BlockStateNames.ACTIVE));
    public static final IntegerBlockState AGE = register(new IntegerBlockState(BlockStateNames.AGE, MAX_AGE + 1));
    public static final BooleanBlockState AGE_BIT = register(new BooleanBlockState(BlockStateNames.AGE_BIT));
    public static final BooleanBlockState ALLOW_UNDERWATER_BIT = register(new BooleanBlockState(BlockStateNames.ALLOW_UNDERWATER_BIT));
    public static final BooleanBlockState ATTACHED_BIT = register(new BooleanBlockState(BlockStateNames.ATTACHED_BIT));
    public static final EnumBlockState<AttachmentState> ATTACHMENT = register(new EnumBlockState<>(BlockStateNames.ATTACHMENT, AttachmentState.values()));
    public static final EnumBlockState<BambooLeafSizeState> BAMBOO_LEAF_SIZE = register(new EnumBlockState<>(BlockStateNames.BAMBOO_LEAF_SIZE, BambooLeafSizeState.values()));
    public static final EnumBlockState<BambooStalkThicknessState> BAMBOO_STALK_THICKNESS = register(new EnumBlockState<>(BlockStateNames.BAMBOO_STALK_THICKNESS, BambooStalkThicknessState.values()));
    public static final BooleanBlockState BIG_DRIPLEAF_HEAD = register(new BooleanBlockState(BlockStateNames.BIG_DRIPLEAF_HEAD));
    public static final EnumBlockState<BigDripleafTiltState> BIG_DRIPLEAF_TILT = register(new EnumBlockState<>(BlockStateNames.BIG_DRIPLEAF_TILT, BigDripleafTiltState.values()));
    public static final IntegerBlockState BITE_COUNTER = register(new IntegerBlockState(BlockStateNames.BITE_COUNTER, MAX_BITE_COUNTER + 1));
    public static final IntegerBlockState BLOCK_LIGHT_LEVEL = register(new IntegerBlockState(BlockStateNames.BLOCK_LIGHT_LEVEL, MAX_BLOCK_LIGHT_LEVEL + 1));
    public static final BooleanBlockState BLOOM = register(new BooleanBlockState(BlockStateNames.BLOOM));
    public static final IntegerBlockState BOOKS_STORED = register(new IntegerBlockState(BlockStateNames.BOOKS_STORED, MAX_BOOKS_STORED + 1));
    public static final BooleanBlockState BREWING_STAND_SLOT_A_BIT = register(new BooleanBlockState(BlockStateNames.BREWING_STAND_SLOT_A_BIT));
    public static final BooleanBlockState BREWING_STAND_SLOT_B_BIT = register(new BooleanBlockState(BlockStateNames.BREWING_STAND_SLOT_B_BIT));
    public static final BooleanBlockState BREWING_STAND_SLOT_C_BIT = register(new BooleanBlockState(BlockStateNames.BREWING_STAND_SLOT_C_BIT));
    public static final IntegerBlockState BRUSHED_PROGRESS = register(new IntegerBlockState(BlockStateNames.BRUSHED_PROGRESS, MAX_BRUSHED_PROGRESS + 1));
    public static final BooleanBlockState BUTTON_PRESSED_BIT = register(new BooleanBlockState(BlockStateNames.BUTTON_PRESSED_BIT));
    public static final BooleanBlockState CAN_SUMMON = register(new BooleanBlockState(BlockStateNames.CAN_SUMMON));
    public static final IntegerBlockState CANDLES = register(new IntegerBlockState(BlockStateNames.CANDLES, MAX_CANDLES + 1));
    public static final EnumBlockState<CauldronLiquidState> CAULDRON_LIQUID = register(new EnumBlockState<>(BlockStateNames.CAULDRON_LIQUID, CauldronLiquidState.values()));
    public static final EnumBlockState<ChemistryTableTypeState> CHEMISTRY_TABLE_TYPE = register(new EnumBlockState<>(BlockStateNames.CHEMISTRY_TABLE_TYPE, ChemistryTableTypeState.values()));
    public static final EnumBlockState<ChiselTypeState> CHISEL_TYPE = register(new EnumBlockState<>(BlockStateNames.CHISEL_TYPE, ChiselTypeState.values()));
    public static final IntegerBlockState CLUSTER_COUNT = register(new IntegerBlockState(BlockStateNames.CLUSTER_COUNT, MAX_CLUSTER_COUNT + 1));
    public static final EnumBlockState<ColorState> COLOR = register(new EnumBlockState<>(BlockStateNames.COLOR, ColorState.values()));
    public static final BooleanBlockState COLOR_BIT = register(new BooleanBlockState(BlockStateNames.COLOR_BIT));
    public static final IntegerBlockState COMPOSTER_FILL_LEVEL = register(new IntegerBlockState(BlockStateNames.COMPOSTER_FILL_LEVEL, MAX_COMPOSTER_FILL_LEVEL + 1));
    public static final BooleanBlockState CONDITIONAL_BIT = register(new BooleanBlockState(BlockStateNames.CONDITIONAL_BIT));
    public static final EnumBlockState<CoralColorState> CORAL_COLOR = register(new EnumBlockState<>(BlockStateNames.CORAL_COLOR, CoralColorState.values()));
    public static final IntegerBlockState CORAL_DIRECTION = register(new IntegerBlockState(BlockStateNames.CORAL_DIRECTION, MAX_CORAL_DIRECTION + 1));
    public static final IntegerBlockState CORAL_FAN_DIRECTION = register(new IntegerBlockState(BlockStateNames.CORAL_FAN_DIRECTION, MAX_CORAL_FAN_DIRECTION + 1));
    public static final BooleanBlockState CORAL_HANG_TYPE_BIT = register(new BooleanBlockState(BlockStateNames.CORAL_HANG_TYPE_BIT));
    public static final BooleanBlockState COVERED_BIT = register(new BooleanBlockState(BlockStateNames.COVERED_BIT));
    public static final EnumBlockState<CrackedState> CRACKED_STATE = register(new EnumBlockState<>(BlockStateNames.CRACKED_STATE, CrackedState.values()));
    public static final BooleanBlockState CRAFTING = register(new BooleanBlockState(BlockStateNames.CRAFTING));
    public static final EnumBlockState<CreakingHeartState> CREAKING_HEART_STATE = register(new EnumBlockState<>(BlockStateNames.CREAKING_HEART_STATE, CreakingHeartState.values()));
    public static final EnumBlockState<DamageState> DAMAGE = register(new EnumBlockState<>(BlockStateNames.DAMAGE, DamageState.values()));
    public static final BooleanBlockState DEAD_BIT = register(new BooleanBlockState(BlockStateNames.DEAD_BIT));
    public static final IntegerBlockState DEPRECATED = register(new IntegerBlockState(BlockStateNames.DEPRECATED, MAX_DEPRECATED + 1));
    public static final IntegerBlockState DIRECTION = register(new IntegerBlockState(BlockStateNames.DIRECTION, MAX_DIRECTION + 1));
    public static final EnumBlockState<DirtTypeState> DIRT_TYPE = register(new EnumBlockState<>(BlockStateNames.DIRT_TYPE, DirtTypeState.values()));
    public static final BooleanBlockState DISARMED_BIT = register(new BooleanBlockState(BlockStateNames.DISARMED_BIT));
    public static final BooleanBlockState DOOR_HINGE_BIT = register(new BooleanBlockState(BlockStateNames.DOOR_HINGE_BIT));
    public static final EnumBlockState<DoublePlantTypeState> DOUBLE_PLANT_TYPE = register(new EnumBlockState<>(BlockStateNames.DOUBLE_PLANT_TYPE, DoublePlantTypeState.values()));
    public static final BooleanBlockState DRAG_DOWN = register(new BooleanBlockState(BlockStateNames.DRAG_DOWN));
    public static final EnumBlockState<DripstoneThicknessState> DRIPSTONE_THICKNESS = register(new EnumBlockState<>(BlockStateNames.DRIPSTONE_THICKNESS, DripstoneThicknessState.values()));
    public static final BooleanBlockState END_PORTAL_EYE_BIT = register(new BooleanBlockState(BlockStateNames.END_PORTAL_EYE_BIT));
    public static final BooleanBlockState EXPLODE_BIT = register(new BooleanBlockState(BlockStateNames.EXPLODE_BIT));
    public static final BooleanBlockState EXTINGUISHED = register(new BooleanBlockState(BlockStateNames.EXTINGUISHED));
    public static final IntegerBlockState FACING_DIRECTION = register(new IntegerBlockState(BlockStateNames.FACING_DIRECTION, MAX_FACING_DIRECTION + 1));
    public static final IntegerBlockState FILL_LEVEL = register(new IntegerBlockState(BlockStateNames.FILL_LEVEL, MAX_FILL_LEVEL + 1));
    public static final EnumBlockState<FlowerTypeState> FLOWER_TYPE = register(new EnumBlockState<>(BlockStateNames.FLOWER_TYPE, FlowerTypeState.values()));
    public static final IntegerBlockState GROUND_SIGN_DIRECTION = register(new IntegerBlockState(BlockStateNames.GROUND_SIGN_DIRECTION, MAX_GROUND_SIGN_DIRECTION + 1));
    public static final IntegerBlockState GROWING_PLANT_AGE = register(new IntegerBlockState(BlockStateNames.GROWING_PLANT_AGE, MAX_GROWING_PLANT_AGE + 1));
    public static final IntegerBlockState GROWTH = register(new IntegerBlockState(BlockStateNames.GROWTH, MAX_GROWTH + 1));
    public static final BooleanBlockState HANGING = register(new BooleanBlockState(BlockStateNames.HANGING));
    public static final BooleanBlockState HEAD_PIECE_BIT = register(new BooleanBlockState(BlockStateNames.HEAD_PIECE_BIT));
    public static final IntegerBlockState HEIGHT = register(new IntegerBlockState(BlockStateNames.HEIGHT, MAX_HEIGHT + 1));
    public static final IntegerBlockState HONEY_LEVEL = register(new IntegerBlockState(BlockStateNames.HONEY_LEVEL, MAX_HONEY_LEVEL + 1));
    public static final IntegerBlockState HUGE_MUSHROOM_BITS = register(new IntegerBlockState(BlockStateNames.HUGE_MUSHROOM_BITS, MAX_HUGE_MUSHROOM_BITS + 1));
    public static final BooleanBlockState IN_WALL_BIT = register(new BooleanBlockState(BlockStateNames.IN_WALL_BIT));
    public static final BooleanBlockState INFINIBURN_BIT = register(new BooleanBlockState(BlockStateNames.INFINIBURN_BIT));
    public static final BooleanBlockState ITEM_FRAME_MAP_BIT = register(new BooleanBlockState(BlockStateNames.ITEM_FRAME_MAP_BIT));
    public static final BooleanBlockState ITEM_FRAME_PHOTO_BIT = register(new BooleanBlockState(BlockStateNames.ITEM_FRAME_PHOTO_BIT));
    public static final IntegerBlockState KELP_AGE = register(new IntegerBlockState(BlockStateNames.KELP_AGE, MAX_KELP_AGE + 1));
    public static final EnumBlockState<LeverDirectionState> LEVER_DIRECTION = register(new EnumBlockState<>(BlockStateNames.LEVER_DIRECTION, LeverDirectionState.values()));
    public static final IntegerBlockState LIQUID_DEPTH = register(new IntegerBlockState(BlockStateNames.LIQUID_DEPTH, MAX_LIQUID_DEPTH + 1));
    public static final BooleanBlockState LIT = register(new BooleanBlockState(BlockStateNames.LIT));
    public static final EnumBlockState<MinecraftBlockFaceState> MINECRAFT_BLOCK_FACE = register(new EnumBlockState<>(BlockStateNames.MINECRAFT_BLOCK_FACE, MinecraftBlockFaceState.values()));
    public static final EnumBlockState<MinecraftCardinalDirectionState> MINECRAFT_CARDINAL_DIRECTION = register(new EnumBlockState<>(BlockStateNames.MINECRAFT_CARDINAL_DIRECTION, MinecraftCardinalDirectionState.values()));
    public static final EnumBlockState<MinecraftFacingDirectionState> MINECRAFT_FACING_DIRECTION = register(new EnumBlockState<>(BlockStateNames.MINECRAFT_FACING_DIRECTION, MinecraftFacingDirectionState.values()));
    public static final EnumBlockState<MinecraftVerticalHalfState> MINECRAFT_VERTICAL_HALF = register(new EnumBlockState<>(BlockStateNames.MINECRAFT_VERTICAL_HALF, MinecraftVerticalHalfState.values()));
    public static final IntegerBlockState MOISTURIZED_AMOUNT = register(new IntegerBlockState(BlockStateNames.MOISTURIZED_AMOUNT, MAX_MOISTURIZED_AMOUNT + 1));
    public static final EnumBlockState<MonsterEggStoneTypeState> MONSTER_EGG_STONE_TYPE = register(new EnumBlockState<>(BlockStateNames.MONSTER_EGG_STONE_TYPE, MonsterEggStoneTypeState.values()));
    public static final IntegerBlockState MULTI_FACE_DIRECTION_BITS = register(new IntegerBlockState(BlockStateNames.MULTI_FACE_DIRECTION_BITS, MAX_MULTI_FACE_DIRECTION_BITS + 1));
    public static final BooleanBlockState NATURAL = register(new BooleanBlockState(BlockStateNames.NATURAL));
    public static final EnumBlockState<NewLeafTypeState> NEW_LEAF_TYPE = register(new EnumBlockState<>(BlockStateNames.NEW_LEAF_TYPE, NewLeafTypeState.values()));
    public static final EnumBlockState<NewLogTypeState> NEW_LOG_TYPE = register(new EnumBlockState<>(BlockStateNames.NEW_LOG_TYPE, NewLogTypeState.values()));
    public static final BooleanBlockState NO_DROP_BIT = register(new BooleanBlockState(BlockStateNames.NO_DROP_BIT));
    public static final BooleanBlockState OCCUPIED_BIT = register(new BooleanBlockState(BlockStateNames.OCCUPIED_BIT));
    public static final EnumBlockState<OldLeafTypeState> OLD_LEAF_TYPE = register(new EnumBlockState<>(BlockStateNames.OLD_LEAF_TYPE, OldLeafTypeState.values()));
    public static final EnumBlockState<OldLogTypeState> OLD_LOG_TYPE = register(new EnumBlockState<>(BlockStateNames.OLD_LOG_TYPE, OldLogTypeState.values()));
    public static final BooleanBlockState OMINOUS = register(new BooleanBlockState(BlockStateNames.OMINOUS));
    public static final BooleanBlockState OPEN_BIT = register(new BooleanBlockState(BlockStateNames.OPEN_BIT));
    public static final EnumBlockState<OrientationState> ORIENTATION = register(new EnumBlockState<>(BlockStateNames.ORIENTATION, OrientationState.values()));
    public static final BooleanBlockState OUTPUT_LIT_BIT = register(new BooleanBlockState(BlockStateNames.OUTPUT_LIT_BIT));
    public static final BooleanBlockState OUTPUT_SUBTRACT_BIT = register(new BooleanBlockState(BlockStateNames.OUTPUT_SUBTRACT_BIT));
    public static final EnumBlockState<PaleMossCarpetSideEastState> PALE_MOSS_CARPET_SIDE_EAST = register(new EnumBlockState<>(BlockStateNames.PALE_MOSS_CARPET_SIDE_EAST, PaleMossCarpetSideEastState.values()));
    public static final EnumBlockState<PaleMossCarpetSideNorthState> PALE_MOSS_CARPET_SIDE_NORTH = register(new EnumBlockState<>(BlockStateNames.PALE_MOSS_CARPET_SIDE_NORTH, PaleMossCarpetSideNorthState.values()));
    public static final EnumBlockState<PaleMossCarpetSideSouthState> PALE_MOSS_CARPET_SIDE_SOUTH = register(new EnumBlockState<>(BlockStateNames.PALE_MOSS_CARPET_SIDE_SOUTH, PaleMossCarpetSideSouthState.values()));
    public static final EnumBlockState<PaleMossCarpetSideWestState> PALE_MOSS_CARPET_SIDE_WEST = register(new EnumBlockState<>(BlockStateNames.PALE_MOSS_CARPET_SIDE_WEST, PaleMossCarpetSideWestState.values()));
    public static final BooleanBlockState PERSISTENT_BIT = register(new BooleanBlockState(BlockStateNames.PERSISTENT_BIT));
    public static final EnumBlockState<PillarAxisState> PILLAR_AXIS = register(new EnumBlockState<>(BlockStateNames.PILLAR_AXIS, PillarAxisState.values()));
    public static final EnumBlockState<PortalAxisState> PORTAL_AXIS = register(new EnumBlockState<>(BlockStateNames.PORTAL_AXIS, PortalAxisState.values()));
    public static final BooleanBlockState POWERED_BIT = register(new BooleanBlockState(BlockStateNames.POWERED_BIT));
    public static final EnumBlockState<PrismarineBlockTypeState> PRISMARINE_BLOCK_TYPE = register(new EnumBlockState<>(BlockStateNames.PRISMARINE_BLOCK_TYPE, PrismarineBlockTypeState.values()));
    public static final IntegerBlockState PROPAGULE_STAGE = register(new IntegerBlockState(BlockStateNames.PROPAGULE_STAGE, MAX_PROPAGULE_STAGE + 1));
    public static final BooleanBlockState RAIL_DATA_BIT = register(new BooleanBlockState(BlockStateNames.RAIL_DATA_BIT));
    public static final IntegerBlockState RAIL_DIRECTION = register(new IntegerBlockState(BlockStateNames.RAIL_DIRECTION, MAX_RAIL_DIRECTION + 1));
    public static final IntegerBlockState REDSTONE_SIGNAL = register(new IntegerBlockState(BlockStateNames.REDSTONE_SIGNAL, MAX_REDSTONE_SIGNAL + 1));
    public static final IntegerBlockState REHYDRATION_LEVEL = register(new IntegerBlockState(BlockStateNames.REHYDRATION_LEVEL, MAX_REHYDRATION_LEVEL + 1));
    public static final IntegerBlockState REPEATER_DELAY = register(new IntegerBlockState(BlockStateNames.REPEATER_DELAY, MAX_REPEATER_DELAY + 1));
    public static final IntegerBlockState RESPAWN_ANCHOR_CHARGE = register(new IntegerBlockState(BlockStateNames.RESPAWN_ANCHOR_CHARGE, MAX_RESPAWN_ANCHOR_CHARGE + 1));
    public static final IntegerBlockState ROTATION = register(new IntegerBlockState(BlockStateNames.ROTATION, MAX_ROTATION + 1));
    public static final EnumBlockState<SandStoneTypeState> SAND_STONE_TYPE = register(new EnumBlockState<>(BlockStateNames.SAND_STONE_TYPE, SandStoneTypeState.values()));
    public static final EnumBlockState<SandTypeState> SAND_TYPE = register(new EnumBlockState<>(BlockStateNames.SAND_TYPE, SandTypeState.values()));
    public static final EnumBlockState<SaplingTypeState> SAPLING_TYPE = register(new EnumBlockState<>(BlockStateNames.SAPLING_TYPE, SaplingTypeState.values()));
    public static final IntegerBlockState SCULK_SENSOR_PHASE = register(new IntegerBlockState(BlockStateNames.SCULK_SENSOR_PHASE, MAX_SCULK_SENSOR_PHASE + 1));
    public static final EnumBlockState<SeaGrassTypeState> SEA_GRASS_TYPE = register(new EnumBlockState<>(BlockStateNames.SEA_GRASS_TYPE, SeaGrassTypeState.values()));
    public static final EnumBlockState<SpongeTypeState> SPONGE_TYPE = register(new EnumBlockState<>(BlockStateNames.SPONGE_TYPE, SpongeTypeState.values()));
    public static final IntegerBlockState STABILITY = register(new IntegerBlockState(BlockStateNames.STABILITY, MAX_STABILITY + 1));
    public static final BooleanBlockState STABILITY_CHECK = register(new BooleanBlockState(BlockStateNames.STABILITY_CHECK));
    public static final EnumBlockState<StoneBrickTypeState> STONE_BRICK_TYPE = register(new EnumBlockState<>(BlockStateNames.STONE_BRICK_TYPE, StoneBrickTypeState.values()));
    public static final EnumBlockState<StoneSlabTypeState> STONE_SLAB_TYPE = register(new EnumBlockState<>(BlockStateNames.STONE_SLAB_TYPE, StoneSlabTypeState.values()));
    public static final EnumBlockState<StoneSlabType2State> STONE_SLAB_TYPE_2 = register(new EnumBlockState<>(BlockStateNames.STONE_SLAB_TYPE_2, StoneSlabType2State.values()));
    public static final EnumBlockState<StoneSlabType3State> STONE_SLAB_TYPE_3 = register(new EnumBlockState<>(BlockStateNames.STONE_SLAB_TYPE_3, StoneSlabType3State.values()));
    public static final EnumBlockState<StoneSlabType4State> STONE_SLAB_TYPE_4 = register(new EnumBlockState<>(BlockStateNames.STONE_SLAB_TYPE_4, StoneSlabType4State.values()));
    public static final EnumBlockState<StoneTypeState> STONE_TYPE = register(new EnumBlockState<>(BlockStateNames.STONE_TYPE, StoneTypeState.values()));
    public static final BooleanBlockState STRIPPED_BIT = register(new BooleanBlockState(BlockStateNames.STRIPPED_BIT));
    public static final EnumBlockState<StructureBlockTypeState> STRUCTURE_BLOCK_TYPE = register(new EnumBlockState<>(BlockStateNames.STRUCTURE_BLOCK_TYPE, StructureBlockTypeState.values()));
    public static final EnumBlockState<StructureVoidTypeState> STRUCTURE_VOID_TYPE = register(new EnumBlockState<>(BlockStateNames.STRUCTURE_VOID_TYPE, StructureVoidTypeState.values()));
    public static final BooleanBlockState SUSPENDED_BIT = register(new BooleanBlockState(BlockStateNames.SUSPENDED_BIT));
    public static final EnumBlockState<TallGrassTypeState> TALL_GRASS_TYPE = register(new EnumBlockState<>(BlockStateNames.TALL_GRASS_TYPE, TallGrassTypeState.values()));
    public static final BooleanBlockState TIP = register(new BooleanBlockState(BlockStateNames.TIP));
    public static final BooleanBlockState TOGGLE_BIT = register(new BooleanBlockState(BlockStateNames.TOGGLE_BIT));
    public static final BooleanBlockState TOP_SLOT_BIT = register(new BooleanBlockState(BlockStateNames.TOP_SLOT_BIT));
    public static final EnumBlockState<TorchFacingDirectionState> TORCH_FACING_DIRECTION = register(new EnumBlockState<>(BlockStateNames.TORCH_FACING_DIRECTION, TorchFacingDirectionState.values()));
    public static final IntegerBlockState TRIAL_SPAWNER_STATE = register(new IntegerBlockState(BlockStateNames.TRIAL_SPAWNER_STATE, MAX_TRIAL_SPAWNER_STATE + 1));
    public static final BooleanBlockState TRIGGERED_BIT = register(new BooleanBlockState(BlockStateNames.TRIGGERED_BIT));
    public static final EnumBlockState<TurtleEggCountState> TURTLE_EGG_COUNT = register(new EnumBlockState<>(BlockStateNames.TURTLE_EGG_COUNT, TurtleEggCountState.values()));
    public static final IntegerBlockState TWISTING_VINES_AGE = register(new IntegerBlockState(BlockStateNames.TWISTING_VINES_AGE, MAX_TWISTING_VINES_AGE + 1));
    public static final BooleanBlockState UPDATE_BIT = register(new BooleanBlockState(BlockStateNames.UPDATE_BIT));
    public static final BooleanBlockState UPPER_BLOCK_BIT = register(new BooleanBlockState(BlockStateNames.UPPER_BLOCK_BIT));
    public static final BooleanBlockState UPSIDE_DOWN_BIT = register(new BooleanBlockState(BlockStateNames.UPSIDE_DOWN_BIT));
    public static final EnumBlockState<VaultState> VAULT_STATE = register(new EnumBlockState<>(BlockStateNames.VAULT_STATE, VaultState.values()));
    public static final IntegerBlockState VINE_DIRECTION_BITS = register(new IntegerBlockState(BlockStateNames.VINE_DIRECTION_BITS, MAX_VINE_DIRECTION_BITS + 1));
    public static final EnumBlockState<WallBlockTypeState> WALL_BLOCK_TYPE = register(new EnumBlockState<>(BlockStateNames.WALL_BLOCK_TYPE, WallBlockTypeState.values()));
    public static final EnumBlockState<WallConnectionTypeEastState> WALL_CONNECTION_TYPE_EAST = register(new EnumBlockState<>(BlockStateNames.WALL_CONNECTION_TYPE_EAST, WallConnectionTypeEastState.values()));
    public static final EnumBlockState<WallConnectionTypeNorthState> WALL_CONNECTION_TYPE_NORTH = register(new EnumBlockState<>(BlockStateNames.WALL_CONNECTION_TYPE_NORTH, WallConnectionTypeNorthState.values()));
    public static final EnumBlockState<WallConnectionTypeSouthState> WALL_CONNECTION_TYPE_SOUTH = register(new EnumBlockState<>(BlockStateNames.WALL_CONNECTION_TYPE_SOUTH, WallConnectionTypeSouthState.values()));
    public static final EnumBlockState<WallConnectionTypeWestState> WALL_CONNECTION_TYPE_WEST = register(new EnumBlockState<>(BlockStateNames.WALL_CONNECTION_TYPE_WEST, WallConnectionTypeWestState.values()));
    public static final BooleanBlockState WALL_POST_BIT = register(new BooleanBlockState(BlockStateNames.WALL_POST_BIT));
    public static final IntegerBlockState WEEPING_VINES_AGE = register(new IntegerBlockState(BlockStateNames.WEEPING_VINES_AGE, MAX_WEEPING_VINES_AGE + 1));
    public static final IntegerBlockState WEIRDO_DIRECTION = register(new IntegerBlockState(BlockStateNames.WEIRDO_DIRECTION, MAX_WEIRDO_DIRECTION + 1));
    public static final EnumBlockState<WoodTypeState> WOOD_TYPE = register(new EnumBlockState<>(BlockStateNames.WOOD_TYPE, WoodTypeState.values()));
    public static final IntegerBlockState CHALKBOARD_DIRECTION = register(new IntegerBlockState(BlockStateNames.DIRECTION, CHALKBOARD_MAX_DIRECTION + 1));

    public static final int STATE_COUNT = 512;

    static int assignId() {
        int id = INTERNAL_ID_ALLOCATOR.getAndIncrement();
        if (id >= STATE_COUNT) {
            throw new AssertionError("Custom block state internal ID overflow. Please increase the capacity");
        }
        return id;
    }

    private static <T extends BlockState> T register(T state) {
        REGISTRY.add(state);
        return state;
    }

    public static <T extends BlockState> T registerCustomBlockState(T state) {
        if (state.id != REGISTRY.size()) {
            throw new IllegalArgumentException("internal ID mismatch");
        }
        return register(state);
    }

    public static BlockState get(int id) {
        return REGISTRY.get(id);
    }

    private BlockStates() {
        throw new IllegalStateException();
    }
}
