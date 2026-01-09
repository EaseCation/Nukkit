package cn.nukkit.block.state;

import cn.nukkit.block.BlockFullNames;
import cn.nukkit.block.BlockID;

// This file is generated automatically, do not edit it manually.
public class BlockTypes {
    private static final BlockRegistry REGISTRY = new BlockRegistry((1 << 24) | (21 << 16) | (40 << 8) | 1);

    public static final BlockLegacy ACACIA_BUTTON = REGISTRY.registerBlock(BlockFullNames.ACACIA_BUTTON, BlockID.ACACIA_BUTTON)
            .addState(BlockStates.FACING_DIRECTION)
            .addState(BlockStates.BUTTON_PRESSED_BIT);
    public static final BlockLegacy ACACIA_DOOR = REGISTRY.registerBlock(BlockFullNames.ACACIA_DOOR, BlockID.ACACIA_DOOR)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.OPEN_BIT)
            .addState(BlockStates.UPPER_BLOCK_BIT)
            .addState(BlockStates.DOOR_HINGE_BIT);
    public static final BlockLegacy ACACIA_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.ACACIA_DOUBLE_SLAB, BlockID.ACACIA_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy ACACIA_FENCE = REGISTRY.registerBlock(BlockFullNames.ACACIA_FENCE, BlockID.ACACIA_FENCE);
    public static final BlockLegacy ACACIA_FENCE_GATE = REGISTRY.registerBlock(BlockFullNames.ACACIA_FENCE_GATE, BlockID.ACACIA_FENCE_GATE)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.OPEN_BIT)
            .addState(BlockStates.IN_WALL_BIT);
    public static final BlockLegacy ACACIA_HANGING_SIGN = REGISTRY.registerBlock(BlockFullNames.ACACIA_HANGING_SIGN, BlockID.ACACIA_HANGING_SIGN)
            .addState(BlockStates.FACING_DIRECTION)
            .addState(BlockStates.GROUND_SIGN_DIRECTION)
            .addState(BlockStates.HANGING)
            .addState(BlockStates.ATTACHED_BIT);
    public static final BlockLegacy ACACIA_LEAVES = REGISTRY.registerBlock(BlockFullNames.ACACIA_LEAVES, BlockID.ACACIA_LEAVES)
            .addState(BlockStates.UPDATE_BIT)
            .addState(BlockStates.PERSISTENT_BIT);
    public static final BlockLegacy ACACIA_LOG = REGISTRY.registerBlock(BlockFullNames.ACACIA_LOG, BlockID.ACACIA_LOG)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy ACACIA_PLANKS = REGISTRY.registerBlock(BlockFullNames.ACACIA_PLANKS, BlockID.ACACIA_PLANKS);
    public static final BlockLegacy ACACIA_PRESSURE_PLATE = REGISTRY.registerBlock(BlockFullNames.ACACIA_PRESSURE_PLATE, BlockID.ACACIA_PRESSURE_PLATE)
            .addState(BlockStates.REDSTONE_SIGNAL);
    public static final BlockLegacy ACACIA_SAPLING = REGISTRY.registerBlock(BlockFullNames.ACACIA_SAPLING, BlockID.ACACIA_SAPLING)
            .addState(BlockStates.AGE_BIT);
    public static final BlockLegacy ACACIA_SLAB = REGISTRY.registerBlock(BlockFullNames.ACACIA_SLAB, BlockID.ACACIA_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy ACACIA_STAIRS = REGISTRY.registerBlock(BlockFullNames.ACACIA_STAIRS, BlockID.ACACIA_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy ACACIA_STANDING_SIGN = REGISTRY.registerBlock(BlockFullNames.ACACIA_STANDING_SIGN, BlockID.ACACIA_STANDING_SIGN)
            .addState(BlockStates.GROUND_SIGN_DIRECTION);
    public static final BlockLegacy ACACIA_TRAPDOOR = REGISTRY.registerBlock(BlockFullNames.ACACIA_TRAPDOOR, BlockID.ACACIA_TRAPDOOR)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT)
            .addState(BlockStates.OPEN_BIT);
    public static final BlockLegacy ACACIA_WALL_SIGN = REGISTRY.registerBlock(BlockFullNames.ACACIA_WALL_SIGN, BlockID.ACACIA_WALL_SIGN)
            .addState(BlockStates.FACING_DIRECTION);
    public static final BlockLegacy ACACIA_WOOD = REGISTRY.registerBlock(BlockFullNames.ACACIA_WOOD, BlockID.ACACIA_WOOD)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy ACTIVATOR_RAIL = REGISTRY.registerBlock(BlockFullNames.ACTIVATOR_RAIL, BlockID.ACTIVATOR_RAIL)
            .addState(BlockStates.RAIL_DIRECTION, BlockStateIntegerValues.ACTIVATOR_RAIL_MAX_RAIL_DIRECTION + 1)
            .addState(BlockStates.RAIL_DATA_BIT);
    public static final BlockLegacy AIR = REGISTRY.registerBlock(BlockFullNames.AIR, BlockID.AIR);
    public static final BlockLegacy ALLIUM = REGISTRY.registerBlock(BlockFullNames.ALLIUM, BlockID.ALLIUM);
    public static final BlockLegacy ALLOW = REGISTRY.registerBlock(BlockFullNames.ALLOW, BlockID.ALLOW);
    public static final BlockLegacy AMETHYST_BLOCK = REGISTRY.registerBlock(BlockFullNames.AMETHYST_BLOCK, BlockID.AMETHYST_BLOCK);
    public static final BlockLegacy AMETHYST_CLUSTER = REGISTRY.registerBlock(BlockFullNames.AMETHYST_CLUSTER, BlockID.AMETHYST_CLUSTER)
            .addState(BlockStates.MINECRAFT_BLOCK_FACE);
    public static final BlockLegacy ANCIENT_DEBRIS = REGISTRY.registerBlock(BlockFullNames.ANCIENT_DEBRIS, BlockID.ANCIENT_DEBRIS);
    public static final BlockLegacy ANDESITE = REGISTRY.registerBlock(BlockFullNames.ANDESITE, BlockID.ANDESITE);
    public static final BlockLegacy ANDESITE_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.ANDESITE_DOUBLE_SLAB, BlockID.ANDESITE_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy ANDESITE_SLAB = REGISTRY.registerBlock(BlockFullNames.ANDESITE_SLAB, BlockID.ANDESITE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy ANDESITE_STAIRS = REGISTRY.registerBlock(BlockFullNames.ANDESITE_STAIRS, BlockID.ANDESITE_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy ANDESITE_WALL = REGISTRY.registerBlock(BlockFullNames.ANDESITE_WALL, BlockID.ANDESITE_WALL)
            .addState(BlockStates.WALL_POST_BIT)
            .addState(BlockStates.WALL_CONNECTION_TYPE_NORTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_EAST)
            .addState(BlockStates.WALL_CONNECTION_TYPE_SOUTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_WEST);
    public static final BlockLegacy ANVIL = REGISTRY.registerBlock(BlockFullNames.ANVIL, BlockID.ANVIL)
            .addState(BlockStates.MINECRAFT_CARDINAL_DIRECTION);
    public static final BlockLegacy AZALEA = REGISTRY.registerBlock(BlockFullNames.AZALEA, BlockID.AZALEA);
    public static final BlockLegacy AZALEA_LEAVES = REGISTRY.registerBlock(BlockFullNames.AZALEA_LEAVES, BlockID.AZALEA_LEAVES)
            .addState(BlockStates.UPDATE_BIT)
            .addState(BlockStates.PERSISTENT_BIT);
    public static final BlockLegacy AZALEA_LEAVES_FLOWERED = REGISTRY.registerBlock(BlockFullNames.AZALEA_LEAVES_FLOWERED, BlockID.AZALEA_LEAVES_FLOWERED)
            .addState(BlockStates.UPDATE_BIT)
            .addState(BlockStates.PERSISTENT_BIT);
    public static final BlockLegacy AZURE_BLUET = REGISTRY.registerBlock(BlockFullNames.AZURE_BLUET, BlockID.AZURE_BLUET);
    public static final BlockLegacy BAMBOO = REGISTRY.registerBlock(BlockFullNames.BAMBOO, BlockID.BAMBOO)
            .addState(BlockStates.BAMBOO_STALK_THICKNESS)
            .addState(BlockStates.BAMBOO_LEAF_SIZE)
            .addState(BlockStates.AGE_BIT);
    public static final BlockLegacy BAMBOO_BLOCK = REGISTRY.registerBlock(BlockFullNames.BAMBOO_BLOCK, BlockID.BAMBOO_BLOCK)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy BAMBOO_BUTTON = REGISTRY.registerBlock(BlockFullNames.BAMBOO_BUTTON, BlockID.BAMBOO_BUTTON)
            .addState(BlockStates.FACING_DIRECTION)
            .addState(BlockStates.BUTTON_PRESSED_BIT);
    public static final BlockLegacy BAMBOO_DOOR = REGISTRY.registerBlock(BlockFullNames.BAMBOO_DOOR, BlockID.BAMBOO_DOOR)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.OPEN_BIT)
            .addState(BlockStates.UPPER_BLOCK_BIT)
            .addState(BlockStates.DOOR_HINGE_BIT);
    public static final BlockLegacy BAMBOO_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.BAMBOO_DOUBLE_SLAB, BlockID.BAMBOO_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy BAMBOO_FENCE = REGISTRY.registerBlock(BlockFullNames.BAMBOO_FENCE, BlockID.BAMBOO_FENCE);
    public static final BlockLegacy BAMBOO_FENCE_GATE = REGISTRY.registerBlock(BlockFullNames.BAMBOO_FENCE_GATE, BlockID.BAMBOO_FENCE_GATE)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.OPEN_BIT)
            .addState(BlockStates.IN_WALL_BIT);
    public static final BlockLegacy BAMBOO_HANGING_SIGN = REGISTRY.registerBlock(BlockFullNames.BAMBOO_HANGING_SIGN, BlockID.BAMBOO_HANGING_SIGN)
            .addState(BlockStates.FACING_DIRECTION)
            .addState(BlockStates.GROUND_SIGN_DIRECTION)
            .addState(BlockStates.HANGING)
            .addState(BlockStates.ATTACHED_BIT);
    public static final BlockLegacy BAMBOO_MOSAIC = REGISTRY.registerBlock(BlockFullNames.BAMBOO_MOSAIC, BlockID.BAMBOO_MOSAIC);
    public static final BlockLegacy BAMBOO_MOSAIC_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.BAMBOO_MOSAIC_DOUBLE_SLAB, BlockID.BAMBOO_MOSAIC_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy BAMBOO_MOSAIC_SLAB = REGISTRY.registerBlock(BlockFullNames.BAMBOO_MOSAIC_SLAB, BlockID.BAMBOO_MOSAIC_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy BAMBOO_MOSAIC_STAIRS = REGISTRY.registerBlock(BlockFullNames.BAMBOO_MOSAIC_STAIRS, BlockID.BAMBOO_MOSAIC_STAIRS)
            .addState(BlockStates.UPSIDE_DOWN_BIT)
            .addState(BlockStates.WEIRDO_DIRECTION);
    public static final BlockLegacy BAMBOO_PLANKS = REGISTRY.registerBlock(BlockFullNames.BAMBOO_PLANKS, BlockID.BAMBOO_PLANKS);
    public static final BlockLegacy BAMBOO_PRESSURE_PLATE = REGISTRY.registerBlock(BlockFullNames.BAMBOO_PRESSURE_PLATE, BlockID.BAMBOO_PRESSURE_PLATE)
            .addState(BlockStates.REDSTONE_SIGNAL);
    public static final BlockLegacy BAMBOO_SAPLING = REGISTRY.registerBlock(BlockFullNames.BAMBOO_SAPLING, BlockID.BAMBOO_SAPLING)
            .addState(BlockStates.AGE_BIT);
    public static final BlockLegacy BAMBOO_SLAB = REGISTRY.registerBlock(BlockFullNames.BAMBOO_SLAB, BlockID.BAMBOO_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy BAMBOO_STAIRS = REGISTRY.registerBlock(BlockFullNames.BAMBOO_STAIRS, BlockID.BAMBOO_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy BAMBOO_STANDING_SIGN = REGISTRY.registerBlock(BlockFullNames.BAMBOO_STANDING_SIGN, BlockID.BAMBOO_STANDING_SIGN)
            .addState(BlockStates.GROUND_SIGN_DIRECTION);
    public static final BlockLegacy BAMBOO_TRAPDOOR = REGISTRY.registerBlock(BlockFullNames.BAMBOO_TRAPDOOR, BlockID.BAMBOO_TRAPDOOR)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT)
            .addState(BlockStates.OPEN_BIT);
    public static final BlockLegacy BAMBOO_WALL_SIGN = REGISTRY.registerBlock(BlockFullNames.BAMBOO_WALL_SIGN, BlockID.BAMBOO_WALL_SIGN)
            .addState(BlockStates.FACING_DIRECTION);
    public static final BlockLegacy BARREL = REGISTRY.registerBlock(BlockFullNames.BARREL, BlockID.BARREL)
            .addState(BlockStates.FACING_DIRECTION)
            .addState(BlockStates.OPEN_BIT);
    public static final BlockLegacy BARRIER = REGISTRY.registerBlock(BlockFullNames.BARRIER, BlockID.BARRIER);
    public static final BlockLegacy BASALT = REGISTRY.registerBlock(BlockFullNames.BASALT, BlockID.BASALT)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy BEACON = REGISTRY.registerBlock(BlockFullNames.BEACON, BlockID.BEACON);
    public static final BlockLegacy BED = REGISTRY.registerBlock(BlockFullNames.BED, BlockID.BED)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.OCCUPIED_BIT)
            .addState(BlockStates.HEAD_PIECE_BIT);
    public static final BlockLegacy BEDROCK = REGISTRY.registerBlock(BlockFullNames.BEDROCK, BlockID.BEDROCK)
            .addState(BlockStates.INFINIBURN_BIT);
    public static final BlockLegacy BEE_NEST = REGISTRY.registerBlock(BlockFullNames.BEE_NEST, BlockID.BEE_NEST)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.HONEY_LEVEL);
    public static final BlockLegacy BEEHIVE = REGISTRY.registerBlock(BlockFullNames.BEEHIVE, BlockID.BEEHIVE)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.HONEY_LEVEL);
    public static final BlockLegacy BEETROOT = REGISTRY.registerBlock(BlockFullNames.BEETROOT, BlockID.BEETROOT)
            .addState(BlockStates.GROWTH);
    public static final BlockLegacy BELL = REGISTRY.registerBlock(BlockFullNames.BELL, BlockID.BELL)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.ATTACHMENT)
            .addState(BlockStates.TOGGLE_BIT);
    public static final BlockLegacy BIG_DRIPLEAF = REGISTRY.registerBlock(BlockFullNames.BIG_DRIPLEAF, BlockID.BIG_DRIPLEAF)
            .addState(BlockStates.BIG_DRIPLEAF_TILT)
            .addState(BlockStates.BIG_DRIPLEAF_HEAD)
            .addState(BlockStates.MINECRAFT_CARDINAL_DIRECTION);
    public static final BlockLegacy BIRCH_BUTTON = REGISTRY.registerBlock(BlockFullNames.BIRCH_BUTTON, BlockID.BIRCH_BUTTON)
            .addState(BlockStates.FACING_DIRECTION)
            .addState(BlockStates.BUTTON_PRESSED_BIT);
    public static final BlockLegacy BIRCH_DOOR = REGISTRY.registerBlock(BlockFullNames.BIRCH_DOOR, BlockID.BIRCH_DOOR)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.OPEN_BIT)
            .addState(BlockStates.UPPER_BLOCK_BIT)
            .addState(BlockStates.DOOR_HINGE_BIT);
    public static final BlockLegacy BIRCH_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.BIRCH_DOUBLE_SLAB, BlockID.BIRCH_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy BIRCH_FENCE = REGISTRY.registerBlock(BlockFullNames.BIRCH_FENCE, BlockID.BIRCH_FENCE);
    public static final BlockLegacy BIRCH_FENCE_GATE = REGISTRY.registerBlock(BlockFullNames.BIRCH_FENCE_GATE, BlockID.BIRCH_FENCE_GATE)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.OPEN_BIT)
            .addState(BlockStates.IN_WALL_BIT);
    public static final BlockLegacy BIRCH_HANGING_SIGN = REGISTRY.registerBlock(BlockFullNames.BIRCH_HANGING_SIGN, BlockID.BIRCH_HANGING_SIGN)
            .addState(BlockStates.FACING_DIRECTION)
            .addState(BlockStates.GROUND_SIGN_DIRECTION)
            .addState(BlockStates.HANGING)
            .addState(BlockStates.ATTACHED_BIT);
    public static final BlockLegacy BIRCH_LEAVES = REGISTRY.registerBlock(BlockFullNames.BIRCH_LEAVES, BlockID.BIRCH_LEAVES)
            .addState(BlockStates.UPDATE_BIT)
            .addState(BlockStates.PERSISTENT_BIT);
    public static final BlockLegacy BIRCH_LOG = REGISTRY.registerBlock(BlockFullNames.BIRCH_LOG, BlockID.BIRCH_LOG)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy BIRCH_PLANKS = REGISTRY.registerBlock(BlockFullNames.BIRCH_PLANKS, BlockID.BIRCH_PLANKS);
    public static final BlockLegacy BIRCH_PRESSURE_PLATE = REGISTRY.registerBlock(BlockFullNames.BIRCH_PRESSURE_PLATE, BlockID.BIRCH_PRESSURE_PLATE)
            .addState(BlockStates.REDSTONE_SIGNAL);
    public static final BlockLegacy BIRCH_SAPLING = REGISTRY.registerBlock(BlockFullNames.BIRCH_SAPLING, BlockID.BIRCH_SAPLING)
            .addState(BlockStates.AGE_BIT);
    public static final BlockLegacy BIRCH_SLAB = REGISTRY.registerBlock(BlockFullNames.BIRCH_SLAB, BlockID.BIRCH_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy BIRCH_STAIRS = REGISTRY.registerBlock(BlockFullNames.BIRCH_STAIRS, BlockID.BIRCH_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy BIRCH_STANDING_SIGN = REGISTRY.registerBlock(BlockFullNames.BIRCH_STANDING_SIGN, BlockID.BIRCH_STANDING_SIGN)
            .addState(BlockStates.GROUND_SIGN_DIRECTION);
    public static final BlockLegacy BIRCH_TRAPDOOR = REGISTRY.registerBlock(BlockFullNames.BIRCH_TRAPDOOR, BlockID.BIRCH_TRAPDOOR)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT)
            .addState(BlockStates.OPEN_BIT);
    public static final BlockLegacy BIRCH_WALL_SIGN = REGISTRY.registerBlock(BlockFullNames.BIRCH_WALL_SIGN, BlockID.BIRCH_WALL_SIGN)
            .addState(BlockStates.FACING_DIRECTION);
    public static final BlockLegacy BIRCH_WOOD = REGISTRY.registerBlock(BlockFullNames.BIRCH_WOOD, BlockID.BIRCH_WOOD)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy BLACK_CANDLE = REGISTRY.registerBlock(BlockFullNames.BLACK_CANDLE, BlockID.BLACK_CANDLE)
            .addState(BlockStates.CANDLES)
            .addState(BlockStates.LIT);
    public static final BlockLegacy BLACK_CANDLE_CAKE = REGISTRY.registerBlock(BlockFullNames.BLACK_CANDLE_CAKE, BlockID.BLACK_CANDLE_CAKE)
            .addState(BlockStates.LIT);
    public static final BlockLegacy BLACK_CARPET = REGISTRY.registerBlock(BlockFullNames.BLACK_CARPET, BlockID.BLACK_CARPET);
    public static final BlockLegacy BLACK_CONCRETE = REGISTRY.registerBlock(BlockFullNames.BLACK_CONCRETE, BlockID.BLACK_CONCRETE);
    public static final BlockLegacy BLACK_CONCRETE_POWDER = REGISTRY.registerBlock(BlockFullNames.BLACK_CONCRETE_POWDER, BlockID.BLACK_CONCRETE_POWDER);
    public static final BlockLegacy BLACK_GLAZED_TERRACOTTA = REGISTRY.registerBlock(BlockFullNames.BLACK_GLAZED_TERRACOTTA, BlockID.BLACK_GLAZED_TERRACOTTA)
            .addState(BlockStates.FACING_DIRECTION);
    public static final BlockLegacy BLACK_SHULKER_BOX = REGISTRY.registerBlock(BlockFullNames.BLACK_SHULKER_BOX, BlockID.BLACK_SHULKER_BOX);
    public static final BlockLegacy BLACK_STAINED_GLASS = REGISTRY.registerBlock(BlockFullNames.BLACK_STAINED_GLASS, BlockID.BLACK_STAINED_GLASS);
    public static final BlockLegacy BLACK_STAINED_GLASS_PANE = REGISTRY.registerBlock(BlockFullNames.BLACK_STAINED_GLASS_PANE, BlockID.BLACK_STAINED_GLASS_PANE);
    public static final BlockLegacy BLACK_TERRACOTTA = REGISTRY.registerBlock(BlockFullNames.BLACK_TERRACOTTA, BlockID.BLACK_TERRACOTTA);
    public static final BlockLegacy BLACK_WOOL = REGISTRY.registerBlock(BlockFullNames.BLACK_WOOL, BlockID.BLACK_WOOL);
    public static final BlockLegacy BLACKSTONE = REGISTRY.registerBlock(BlockFullNames.BLACKSTONE, BlockID.BLACKSTONE);
    public static final BlockLegacy BLACKSTONE_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.BLACKSTONE_DOUBLE_SLAB, BlockID.BLACKSTONE_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy BLACKSTONE_SLAB = REGISTRY.registerBlock(BlockFullNames.BLACKSTONE_SLAB, BlockID.BLACKSTONE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy BLACKSTONE_STAIRS = REGISTRY.registerBlock(BlockFullNames.BLACKSTONE_STAIRS, BlockID.BLACKSTONE_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy BLACKSTONE_WALL = REGISTRY.registerBlock(BlockFullNames.BLACKSTONE_WALL, BlockID.BLACKSTONE_WALL)
            .addState(BlockStates.WALL_POST_BIT)
            .addState(BlockStates.WALL_CONNECTION_TYPE_NORTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_EAST)
            .addState(BlockStates.WALL_CONNECTION_TYPE_SOUTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_WEST);
    public static final BlockLegacy BLAST_FURNACE = REGISTRY.registerBlock(BlockFullNames.BLAST_FURNACE, BlockID.BLAST_FURNACE)
            .addState(BlockStates.MINECRAFT_CARDINAL_DIRECTION);
    public static final BlockLegacy BLUE_CANDLE = REGISTRY.registerBlock(BlockFullNames.BLUE_CANDLE, BlockID.BLUE_CANDLE)
            .addState(BlockStates.CANDLES)
            .addState(BlockStates.LIT);
    public static final BlockLegacy BLUE_CANDLE_CAKE = REGISTRY.registerBlock(BlockFullNames.BLUE_CANDLE_CAKE, BlockID.BLUE_CANDLE_CAKE)
            .addState(BlockStates.LIT);
    public static final BlockLegacy BLUE_CARPET = REGISTRY.registerBlock(BlockFullNames.BLUE_CARPET, BlockID.BLUE_CARPET);
    public static final BlockLegacy BLUE_CONCRETE = REGISTRY.registerBlock(BlockFullNames.BLUE_CONCRETE, BlockID.BLUE_CONCRETE);
    public static final BlockLegacy BLUE_CONCRETE_POWDER = REGISTRY.registerBlock(BlockFullNames.BLUE_CONCRETE_POWDER, BlockID.BLUE_CONCRETE_POWDER);
    public static final BlockLegacy BLUE_GLAZED_TERRACOTTA = REGISTRY.registerBlock(BlockFullNames.BLUE_GLAZED_TERRACOTTA, BlockID.BLUE_GLAZED_TERRACOTTA)
            .addState(BlockStates.FACING_DIRECTION);
    public static final BlockLegacy BLUE_ICE = REGISTRY.registerBlock(BlockFullNames.BLUE_ICE, BlockID.BLUE_ICE);
    public static final BlockLegacy BLUE_ORCHID = REGISTRY.registerBlock(BlockFullNames.BLUE_ORCHID, BlockID.BLUE_ORCHID);
    public static final BlockLegacy BLUE_SHULKER_BOX = REGISTRY.registerBlock(BlockFullNames.BLUE_SHULKER_BOX, BlockID.BLUE_SHULKER_BOX);
    public static final BlockLegacy BLUE_STAINED_GLASS = REGISTRY.registerBlock(BlockFullNames.BLUE_STAINED_GLASS, BlockID.BLUE_STAINED_GLASS);
    public static final BlockLegacy BLUE_STAINED_GLASS_PANE = REGISTRY.registerBlock(BlockFullNames.BLUE_STAINED_GLASS_PANE, BlockID.BLUE_STAINED_GLASS_PANE);
    public static final BlockLegacy BLUE_TERRACOTTA = REGISTRY.registerBlock(BlockFullNames.BLUE_TERRACOTTA, BlockID.BLUE_TERRACOTTA);
    public static final BlockLegacy BLUE_WOOL = REGISTRY.registerBlock(BlockFullNames.BLUE_WOOL, BlockID.BLUE_WOOL);
    public static final BlockLegacy BONE_BLOCK = REGISTRY.registerBlock(BlockFullNames.BONE_BLOCK, BlockID.BONE_BLOCK)
            .addState(BlockStates.DEPRECATED)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy BOOKSHELF = REGISTRY.registerBlock(BlockFullNames.BOOKSHELF, BlockID.BOOKSHELF);
    public static final BlockLegacy BORDER_BLOCK = REGISTRY.registerBlock(BlockFullNames.BORDER_BLOCK, BlockID.BORDER_BLOCK)
            .addState(BlockStates.WALL_POST_BIT)
            .addState(BlockStates.WALL_CONNECTION_TYPE_SOUTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_NORTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_WEST)
            .addState(BlockStates.WALL_CONNECTION_TYPE_EAST);
    public static final BlockLegacy BRAIN_CORAL = REGISTRY.registerBlock(BlockFullNames.BRAIN_CORAL, BlockID.BRAIN_CORAL);
    public static final BlockLegacy BRAIN_CORAL_BLOCK = REGISTRY.registerBlock(BlockFullNames.BRAIN_CORAL_BLOCK, BlockID.BRAIN_CORAL_BLOCK);
    public static final BlockLegacy BRAIN_CORAL_FAN = REGISTRY.registerBlock(BlockFullNames.BRAIN_CORAL_FAN, BlockID.BRAIN_CORAL_FAN)
            .addState(BlockStates.CORAL_FAN_DIRECTION);
    public static final BlockLegacy BRAIN_CORAL_WALL_FAN = REGISTRY.registerBlock(BlockFullNames.BRAIN_CORAL_WALL_FAN, BlockID.BRAIN_CORAL_WALL_FAN)
            .addState(BlockStates.CORAL_DIRECTION);
    public static final BlockLegacy BREWING_STAND = REGISTRY.registerBlock(BlockFullNames.BREWING_STAND, BlockID.BREWING_STAND)
            .addState(BlockStates.BREWING_STAND_SLOT_A_BIT)
            .addState(BlockStates.BREWING_STAND_SLOT_B_BIT)
            .addState(BlockStates.BREWING_STAND_SLOT_C_BIT);
    public static final BlockLegacy BRICK_BLOCK = REGISTRY.registerBlock(BlockFullNames.BRICK_BLOCK, BlockID.BRICK_BLOCK);
    public static final BlockLegacy BRICK_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.BRICK_DOUBLE_SLAB, BlockID.BRICK_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy BRICK_SLAB = REGISTRY.registerBlock(BlockFullNames.BRICK_SLAB, BlockID.BRICK_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy BRICK_STAIRS = REGISTRY.registerBlock(BlockFullNames.BRICK_STAIRS, BlockID.BRICK_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy BRICK_WALL = REGISTRY.registerBlock(BlockFullNames.BRICK_WALL, BlockID.BRICK_WALL)
            .addState(BlockStates.WALL_POST_BIT)
            .addState(BlockStates.WALL_CONNECTION_TYPE_NORTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_EAST)
            .addState(BlockStates.WALL_CONNECTION_TYPE_SOUTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_WEST);
    public static final BlockLegacy BROWN_CANDLE = REGISTRY.registerBlock(BlockFullNames.BROWN_CANDLE, BlockID.BROWN_CANDLE)
            .addState(BlockStates.CANDLES)
            .addState(BlockStates.LIT);
    public static final BlockLegacy BROWN_CANDLE_CAKE = REGISTRY.registerBlock(BlockFullNames.BROWN_CANDLE_CAKE, BlockID.BROWN_CANDLE_CAKE)
            .addState(BlockStates.LIT);
    public static final BlockLegacy BROWN_CARPET = REGISTRY.registerBlock(BlockFullNames.BROWN_CARPET, BlockID.BROWN_CARPET);
    public static final BlockLegacy BROWN_CONCRETE = REGISTRY.registerBlock(BlockFullNames.BROWN_CONCRETE, BlockID.BROWN_CONCRETE);
    public static final BlockLegacy BROWN_CONCRETE_POWDER = REGISTRY.registerBlock(BlockFullNames.BROWN_CONCRETE_POWDER, BlockID.BROWN_CONCRETE_POWDER);
    public static final BlockLegacy BROWN_GLAZED_TERRACOTTA = REGISTRY.registerBlock(BlockFullNames.BROWN_GLAZED_TERRACOTTA, BlockID.BROWN_GLAZED_TERRACOTTA)
            .addState(BlockStates.FACING_DIRECTION);
    public static final BlockLegacy BROWN_MUSHROOM = REGISTRY.registerBlock(BlockFullNames.BROWN_MUSHROOM, BlockID.BROWN_MUSHROOM);
    public static final BlockLegacy BROWN_MUSHROOM_BLOCK = REGISTRY.registerBlock(BlockFullNames.BROWN_MUSHROOM_BLOCK, BlockID.BROWN_MUSHROOM_BLOCK)
            .addState(BlockStates.HUGE_MUSHROOM_BITS);
    public static final BlockLegacy BROWN_SHULKER_BOX = REGISTRY.registerBlock(BlockFullNames.BROWN_SHULKER_BOX, BlockID.BROWN_SHULKER_BOX);
    public static final BlockLegacy BROWN_STAINED_GLASS = REGISTRY.registerBlock(BlockFullNames.BROWN_STAINED_GLASS, BlockID.BROWN_STAINED_GLASS);
    public static final BlockLegacy BROWN_STAINED_GLASS_PANE = REGISTRY.registerBlock(BlockFullNames.BROWN_STAINED_GLASS_PANE, BlockID.BROWN_STAINED_GLASS_PANE);
    public static final BlockLegacy BROWN_TERRACOTTA = REGISTRY.registerBlock(BlockFullNames.BROWN_TERRACOTTA, BlockID.BROWN_TERRACOTTA);
    public static final BlockLegacy BROWN_WOOL = REGISTRY.registerBlock(BlockFullNames.BROWN_WOOL, BlockID.BROWN_WOOL);
    public static final BlockLegacy BUBBLE_COLUMN = REGISTRY.registerBlock(BlockFullNames.BUBBLE_COLUMN, BlockID.BUBBLE_COLUMN)
            .addState(BlockStates.DRAG_DOWN);
    public static final BlockLegacy BUBBLE_CORAL = REGISTRY.registerBlock(BlockFullNames.BUBBLE_CORAL, BlockID.BUBBLE_CORAL);
    public static final BlockLegacy BUBBLE_CORAL_BLOCK = REGISTRY.registerBlock(BlockFullNames.BUBBLE_CORAL_BLOCK, BlockID.BUBBLE_CORAL_BLOCK);
    public static final BlockLegacy BUBBLE_CORAL_FAN = REGISTRY.registerBlock(BlockFullNames.BUBBLE_CORAL_FAN, BlockID.BUBBLE_CORAL_FAN)
            .addState(BlockStates.CORAL_FAN_DIRECTION);
    public static final BlockLegacy BUBBLE_CORAL_WALL_FAN = REGISTRY.registerBlock(BlockFullNames.BUBBLE_CORAL_WALL_FAN, BlockID.BUBBLE_CORAL_WALL_FAN)
            .addState(BlockStates.CORAL_DIRECTION);
    public static final BlockLegacy BUDDING_AMETHYST = REGISTRY.registerBlock(BlockFullNames.BUDDING_AMETHYST, BlockID.BUDDING_AMETHYST);
    public static final BlockLegacy CACTUS = REGISTRY.registerBlock(BlockFullNames.CACTUS, BlockID.CACTUS)
            .addState(BlockStates.AGE);
    public static final BlockLegacy CAKE = REGISTRY.registerBlock(BlockFullNames.CAKE, BlockID.CAKE)
            .addState(BlockStates.BITE_COUNTER);
    public static final BlockLegacy CALCITE = REGISTRY.registerBlock(BlockFullNames.CALCITE, BlockID.CALCITE);
    public static final BlockLegacy CALIBRATED_SCULK_SENSOR = REGISTRY.registerBlock(BlockFullNames.CALIBRATED_SCULK_SENSOR, BlockID.CALIBRATED_SCULK_SENSOR)
            .addState(BlockStates.SCULK_SENSOR_PHASE)
            .addState(BlockStates.MINECRAFT_CARDINAL_DIRECTION);
    public static final BlockLegacy CAMERA = REGISTRY.registerBlock(BlockFullNames.CAMERA, BlockID.CAMERA);
    public static final BlockLegacy CAMPFIRE = REGISTRY.registerBlock(BlockFullNames.CAMPFIRE, BlockID.CAMPFIRE)
            .addState(BlockStates.MINECRAFT_CARDINAL_DIRECTION)
            .addState(BlockStates.EXTINGUISHED);
    public static final BlockLegacy CANDLE = REGISTRY.registerBlock(BlockFullNames.CANDLE, BlockID.CANDLE)
            .addState(BlockStates.CANDLES)
            .addState(BlockStates.LIT);
    public static final BlockLegacy CANDLE_CAKE = REGISTRY.registerBlock(BlockFullNames.CANDLE_CAKE, BlockID.CANDLE_CAKE)
            .addState(BlockStates.LIT);
    public static final BlockLegacy CARROTS = REGISTRY.registerBlock(BlockFullNames.CARROTS, BlockID.CARROTS)
            .addState(BlockStates.GROWTH);
    public static final BlockLegacy CARTOGRAPHY_TABLE = REGISTRY.registerBlock(BlockFullNames.CARTOGRAPHY_TABLE, BlockID.CARTOGRAPHY_TABLE);
    public static final BlockLegacy CARVED_PUMPKIN = REGISTRY.registerBlock(BlockFullNames.CARVED_PUMPKIN, BlockID.CARVED_PUMPKIN)
            .addState(BlockStates.MINECRAFT_CARDINAL_DIRECTION);
    public static final BlockLegacy CAULDRON = REGISTRY.registerBlock(BlockFullNames.CAULDRON, BlockID.CAULDRON)
            .addState(BlockStates.FILL_LEVEL)
            .addState(BlockStates.CAULDRON_LIQUID);
    public static final BlockLegacy CAVE_VINES = REGISTRY.registerBlock(BlockFullNames.CAVE_VINES, BlockID.CAVE_VINES)
            .addState(BlockStates.GROWING_PLANT_AGE);
    public static final BlockLegacy CAVE_VINES_BODY_WITH_BERRIES = REGISTRY.registerBlock(BlockFullNames.CAVE_VINES_BODY_WITH_BERRIES, BlockID.CAVE_VINES_BODY_WITH_BERRIES)
            .addState(BlockStates.GROWING_PLANT_AGE);
    public static final BlockLegacy CAVE_VINES_HEAD_WITH_BERRIES = REGISTRY.registerBlock(BlockFullNames.CAVE_VINES_HEAD_WITH_BERRIES, BlockID.CAVE_VINES_HEAD_WITH_BERRIES)
            .addState(BlockStates.GROWING_PLANT_AGE);
    public static final BlockLegacy CHAIN = REGISTRY.registerBlock(BlockFullNames.CHAIN, BlockID.CHAIN)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy CHAIN_COMMAND_BLOCK = REGISTRY.registerBlock(BlockFullNames.CHAIN_COMMAND_BLOCK, BlockID.CHAIN_COMMAND_BLOCK)
            .addState(BlockStates.FACING_DIRECTION)
            .addState(BlockStates.CONDITIONAL_BIT);
    public static final BlockLegacy CHALKBOARD = REGISTRY.registerBlock(BlockFullNames.CHALKBOARD, BlockID.CHALKBOARD)
            .addState(BlockStates.CHALKBOARD_DIRECTION);
    public static final BlockLegacy CHEMICAL_HEAT = REGISTRY.registerBlock(BlockFullNames.CHEMICAL_HEAT, BlockID.CHEMICAL_HEAT);
    public static final BlockLegacy CHERRY_BUTTON = REGISTRY.registerBlock(BlockFullNames.CHERRY_BUTTON, BlockID.CHERRY_BUTTON)
            .addState(BlockStates.FACING_DIRECTION)
            .addState(BlockStates.BUTTON_PRESSED_BIT);
    public static final BlockLegacy CHERRY_DOOR = REGISTRY.registerBlock(BlockFullNames.CHERRY_DOOR, BlockID.CHERRY_DOOR)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.OPEN_BIT)
            .addState(BlockStates.UPPER_BLOCK_BIT)
            .addState(BlockStates.DOOR_HINGE_BIT);
    public static final BlockLegacy CHERRY_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.CHERRY_DOUBLE_SLAB, BlockID.CHERRY_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy CHERRY_FENCE = REGISTRY.registerBlock(BlockFullNames.CHERRY_FENCE, BlockID.CHERRY_FENCE);
    public static final BlockLegacy CHERRY_FENCE_GATE = REGISTRY.registerBlock(BlockFullNames.CHERRY_FENCE_GATE, BlockID.CHERRY_FENCE_GATE)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.OPEN_BIT)
            .addState(BlockStates.IN_WALL_BIT);
    public static final BlockLegacy CHERRY_HANGING_SIGN = REGISTRY.registerBlock(BlockFullNames.CHERRY_HANGING_SIGN, BlockID.CHERRY_HANGING_SIGN)
            .addState(BlockStates.FACING_DIRECTION)
            .addState(BlockStates.GROUND_SIGN_DIRECTION)
            .addState(BlockStates.HANGING)
            .addState(BlockStates.ATTACHED_BIT);
    public static final BlockLegacy CHERRY_LEAVES = REGISTRY.registerBlock(BlockFullNames.CHERRY_LEAVES, BlockID.CHERRY_LEAVES)
            .addState(BlockStates.UPDATE_BIT)
            .addState(BlockStates.PERSISTENT_BIT);
    public static final BlockLegacy CHERRY_LOG = REGISTRY.registerBlock(BlockFullNames.CHERRY_LOG, BlockID.CHERRY_LOG)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy CHERRY_PLANKS = REGISTRY.registerBlock(BlockFullNames.CHERRY_PLANKS, BlockID.CHERRY_PLANKS);
    public static final BlockLegacy CHERRY_PRESSURE_PLATE = REGISTRY.registerBlock(BlockFullNames.CHERRY_PRESSURE_PLATE, BlockID.CHERRY_PRESSURE_PLATE)
            .addState(BlockStates.REDSTONE_SIGNAL);
    public static final BlockLegacy CHERRY_SAPLING = REGISTRY.registerBlock(BlockFullNames.CHERRY_SAPLING, BlockID.CHERRY_SAPLING)
            .addState(BlockStates.AGE_BIT);
    public static final BlockLegacy CHERRY_SLAB = REGISTRY.registerBlock(BlockFullNames.CHERRY_SLAB, BlockID.CHERRY_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy CHERRY_STAIRS = REGISTRY.registerBlock(BlockFullNames.CHERRY_STAIRS, BlockID.CHERRY_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy CHERRY_STANDING_SIGN = REGISTRY.registerBlock(BlockFullNames.CHERRY_STANDING_SIGN, BlockID.CHERRY_STANDING_SIGN)
            .addState(BlockStates.GROUND_SIGN_DIRECTION);
    public static final BlockLegacy CHERRY_TRAPDOOR = REGISTRY.registerBlock(BlockFullNames.CHERRY_TRAPDOOR, BlockID.CHERRY_TRAPDOOR)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT)
            .addState(BlockStates.OPEN_BIT);
    public static final BlockLegacy CHERRY_WALL_SIGN = REGISTRY.registerBlock(BlockFullNames.CHERRY_WALL_SIGN, BlockID.CHERRY_WALL_SIGN)
            .addState(BlockStates.FACING_DIRECTION);
    public static final BlockLegacy CHERRY_WOOD = REGISTRY.registerBlock(BlockFullNames.CHERRY_WOOD, BlockID.CHERRY_WOOD)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy CHEST = REGISTRY.registerBlock(BlockFullNames.CHEST, BlockID.CHEST)
            .addState(BlockStates.MINECRAFT_CARDINAL_DIRECTION);
    public static final BlockLegacy CHIPPED_ANVIL = REGISTRY.registerBlock(BlockFullNames.CHIPPED_ANVIL, BlockID.CHIPPED_ANVIL)
            .addState(BlockStates.MINECRAFT_CARDINAL_DIRECTION);
    public static final BlockLegacy CHISELED_BOOKSHELF = REGISTRY.registerBlock(BlockFullNames.CHISELED_BOOKSHELF, BlockID.CHISELED_BOOKSHELF)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.BOOKS_STORED);
    public static final BlockLegacy CHISELED_COPPER = REGISTRY.registerBlock(BlockFullNames.CHISELED_COPPER, BlockID.CHISELED_COPPER);
    public static final BlockLegacy CHISELED_DEEPSLATE = REGISTRY.registerBlock(BlockFullNames.CHISELED_DEEPSLATE, BlockID.CHISELED_DEEPSLATE);
    public static final BlockLegacy CHISELED_NETHER_BRICKS = REGISTRY.registerBlock(BlockFullNames.CHISELED_NETHER_BRICKS, BlockID.CHISELED_NETHER_BRICKS);
    public static final BlockLegacy CHISELED_POLISHED_BLACKSTONE = REGISTRY.registerBlock(BlockFullNames.CHISELED_POLISHED_BLACKSTONE, BlockID.CHISELED_POLISHED_BLACKSTONE);
    public static final BlockLegacy CHISELED_QUARTZ_BLOCK = REGISTRY.registerBlock(BlockFullNames.CHISELED_QUARTZ_BLOCK, BlockID.CHISELED_QUARTZ_BLOCK)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy CHISELED_RED_SANDSTONE = REGISTRY.registerBlock(BlockFullNames.CHISELED_RED_SANDSTONE, BlockID.CHISELED_RED_SANDSTONE);
    public static final BlockLegacy CHISELED_RESIN_BRICKS = REGISTRY.registerBlock(BlockFullNames.CHISELED_RESIN_BRICKS, BlockID.CHISELED_RESIN_BRICKS);
    public static final BlockLegacy CHISELED_SANDSTONE = REGISTRY.registerBlock(BlockFullNames.CHISELED_SANDSTONE, BlockID.CHISELED_SANDSTONE);
    public static final BlockLegacy CHISELED_STONE_BRICKS = REGISTRY.registerBlock(BlockFullNames.CHISELED_STONE_BRICKS, BlockID.CHISELED_STONE_BRICKS);
    public static final BlockLegacy CHISELED_TUFF = REGISTRY.registerBlock(BlockFullNames.CHISELED_TUFF, BlockID.CHISELED_TUFF);
    public static final BlockLegacy CHISELED_TUFF_BRICKS = REGISTRY.registerBlock(BlockFullNames.CHISELED_TUFF_BRICKS, BlockID.CHISELED_TUFF_BRICKS);
    public static final BlockLegacy CHORUS_FLOWER = REGISTRY.registerBlock(BlockFullNames.CHORUS_FLOWER, BlockID.CHORUS_FLOWER)
            .addState(BlockStates.AGE, BlockStateIntegerValues.CHORUS_FLOWER_MAX_AGE + 1);
    public static final BlockLegacy CHORUS_PLANT = REGISTRY.registerBlock(BlockFullNames.CHORUS_PLANT, BlockID.CHORUS_PLANT);
    public static final BlockLegacy CLAY = REGISTRY.registerBlock(BlockFullNames.CLAY, BlockID.CLAY);
    public static final BlockLegacy CLIENT_REQUEST_PLACEHOLDER_BLOCK = REGISTRY.registerBlock(BlockFullNames.CLIENT_REQUEST_PLACEHOLDER_BLOCK, BlockID.CLIENT_REQUEST_PLACEHOLDER_BLOCK);
    public static final BlockLegacy CLOSED_EYEBLOSSOM = REGISTRY.registerBlock(BlockFullNames.CLOSED_EYEBLOSSOM, BlockID.CLOSED_EYEBLOSSOM);
    public static final BlockLegacy COAL_BLOCK = REGISTRY.registerBlock(BlockFullNames.COAL_BLOCK, BlockID.COAL_BLOCK);
    public static final BlockLegacy COAL_ORE = REGISTRY.registerBlock(BlockFullNames.COAL_ORE, BlockID.COAL_ORE);
    public static final BlockLegacy COARSE_DIRT = REGISTRY.registerBlock(BlockFullNames.COARSE_DIRT, BlockID.COARSE_DIRT);
    public static final BlockLegacy COBBLED_DEEPSLATE = REGISTRY.registerBlock(BlockFullNames.COBBLED_DEEPSLATE, BlockID.COBBLED_DEEPSLATE);
    public static final BlockLegacy COBBLED_DEEPSLATE_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.COBBLED_DEEPSLATE_DOUBLE_SLAB, BlockID.COBBLED_DEEPSLATE_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy COBBLED_DEEPSLATE_SLAB = REGISTRY.registerBlock(BlockFullNames.COBBLED_DEEPSLATE_SLAB, BlockID.COBBLED_DEEPSLATE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy COBBLED_DEEPSLATE_STAIRS = REGISTRY.registerBlock(BlockFullNames.COBBLED_DEEPSLATE_STAIRS, BlockID.COBBLED_DEEPSLATE_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy COBBLED_DEEPSLATE_WALL = REGISTRY.registerBlock(BlockFullNames.COBBLED_DEEPSLATE_WALL, BlockID.COBBLED_DEEPSLATE_WALL)
            .addState(BlockStates.WALL_POST_BIT)
            .addState(BlockStates.WALL_CONNECTION_TYPE_SOUTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_NORTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_WEST)
            .addState(BlockStates.WALL_CONNECTION_TYPE_EAST);
    public static final BlockLegacy COBBLESTONE = REGISTRY.registerBlock(BlockFullNames.COBBLESTONE, BlockID.COBBLESTONE);
    public static final BlockLegacy COBBLESTONE_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.COBBLESTONE_DOUBLE_SLAB, BlockID.COBBLESTONE_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy COBBLESTONE_SLAB = REGISTRY.registerBlock(BlockFullNames.COBBLESTONE_SLAB, BlockID.COBBLESTONE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy COBBLESTONE_WALL = REGISTRY.registerBlock(BlockFullNames.COBBLESTONE_WALL, BlockID.COBBLESTONE_WALL)
            .addState(BlockStates.WALL_POST_BIT)
            .addState(BlockStates.WALL_CONNECTION_TYPE_NORTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_EAST)
            .addState(BlockStates.WALL_CONNECTION_TYPE_SOUTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_WEST);
    public static final BlockLegacy COCOA = REGISTRY.registerBlock(BlockFullNames.COCOA, BlockID.COCOA)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.AGE, BlockStateIntegerValues.COCOA_MAX_AGE + 1);
    public static final BlockLegacy COLORED_TORCH_BLUE = REGISTRY.registerBlock(BlockFullNames.COLORED_TORCH_BLUE, BlockID.COLORED_TORCH_BLUE)
            .addState(BlockStates.TORCH_FACING_DIRECTION);
    public static final BlockLegacy COLORED_TORCH_GREEN = REGISTRY.registerBlock(BlockFullNames.COLORED_TORCH_GREEN, BlockID.COLORED_TORCH_GREEN)
            .addState(BlockStates.TORCH_FACING_DIRECTION);
    public static final BlockLegacy COLORED_TORCH_PURPLE = REGISTRY.registerBlock(BlockFullNames.COLORED_TORCH_PURPLE, BlockID.COLORED_TORCH_PURPLE)
            .addState(BlockStates.TORCH_FACING_DIRECTION);
    public static final BlockLegacy COLORED_TORCH_RED = REGISTRY.registerBlock(BlockFullNames.COLORED_TORCH_RED, BlockID.COLORED_TORCH_RED)
            .addState(BlockStates.TORCH_FACING_DIRECTION);
    public static final BlockLegacy COMMAND_BLOCK = REGISTRY.registerBlock(BlockFullNames.COMMAND_BLOCK, BlockID.COMMAND_BLOCK)
            .addState(BlockStates.FACING_DIRECTION)
            .addState(BlockStates.CONDITIONAL_BIT);
    public static final BlockLegacy COMPOSTER = REGISTRY.registerBlock(BlockFullNames.COMPOSTER, BlockID.COMPOSTER)
            .addState(BlockStates.COMPOSTER_FILL_LEVEL);
    public static final BlockLegacy COMPOUND_CREATOR = REGISTRY.registerBlock(BlockFullNames.COMPOUND_CREATOR, BlockID.COMPOUND_CREATOR)
            .addState(BlockStates.DIRECTION);
    public static final BlockLegacy CONDUIT = REGISTRY.registerBlock(BlockFullNames.CONDUIT, BlockID.CONDUIT);
    public static final BlockLegacy COPPER_BLOCK = REGISTRY.registerBlock(BlockFullNames.COPPER_BLOCK, BlockID.COPPER_BLOCK);
    public static final BlockLegacy COPPER_BULB = REGISTRY.registerBlock(BlockFullNames.COPPER_BULB, BlockID.COPPER_BULB)
            .addState(BlockStates.LIT)
            .addState(BlockStates.POWERED_BIT);
    public static final BlockLegacy COPPER_DOOR = REGISTRY.registerBlock(BlockFullNames.COPPER_DOOR, BlockID.COPPER_DOOR)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.OPEN_BIT)
            .addState(BlockStates.UPPER_BLOCK_BIT)
            .addState(BlockStates.DOOR_HINGE_BIT);
    public static final BlockLegacy COPPER_GRATE = REGISTRY.registerBlock(BlockFullNames.COPPER_GRATE, BlockID.COPPER_GRATE);
    public static final BlockLegacy COPPER_ORE = REGISTRY.registerBlock(BlockFullNames.COPPER_ORE, BlockID.COPPER_ORE);
    public static final BlockLegacy COPPER_TRAPDOOR = REGISTRY.registerBlock(BlockFullNames.COPPER_TRAPDOOR, BlockID.COPPER_TRAPDOOR)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT)
            .addState(BlockStates.OPEN_BIT);
    public static final BlockLegacy CORNFLOWER = REGISTRY.registerBlock(BlockFullNames.CORNFLOWER, BlockID.CORNFLOWER);
    public static final BlockLegacy CRACKED_DEEPSLATE_BRICKS = REGISTRY.registerBlock(BlockFullNames.CRACKED_DEEPSLATE_BRICKS, BlockID.CRACKED_DEEPSLATE_BRICKS);
    public static final BlockLegacy CRACKED_DEEPSLATE_TILES = REGISTRY.registerBlock(BlockFullNames.CRACKED_DEEPSLATE_TILES, BlockID.CRACKED_DEEPSLATE_TILES);
    public static final BlockLegacy CRACKED_NETHER_BRICKS = REGISTRY.registerBlock(BlockFullNames.CRACKED_NETHER_BRICKS, BlockID.CRACKED_NETHER_BRICKS);
    public static final BlockLegacy CRACKED_POLISHED_BLACKSTONE_BRICKS = REGISTRY.registerBlock(BlockFullNames.CRACKED_POLISHED_BLACKSTONE_BRICKS, BlockID.CRACKED_POLISHED_BLACKSTONE_BRICKS);
    public static final BlockLegacy CRACKED_STONE_BRICKS = REGISTRY.registerBlock(BlockFullNames.CRACKED_STONE_BRICKS, BlockID.CRACKED_STONE_BRICKS);
    public static final BlockLegacy CRAFTER = REGISTRY.registerBlock(BlockFullNames.CRAFTER, BlockID.CRAFTER)
            .addState(BlockStates.ORIENTATION)
            .addState(BlockStates.TRIGGERED_BIT)
            .addState(BlockStates.CRAFTING);
    public static final BlockLegacy CRAFTING_TABLE = REGISTRY.registerBlock(BlockFullNames.CRAFTING_TABLE, BlockID.CRAFTING_TABLE);
    public static final BlockLegacy CREAKING_HEART = REGISTRY.registerBlock(BlockFullNames.CREAKING_HEART, BlockID.CREAKING_HEART)
            .addState(BlockStates.PILLAR_AXIS)
            .addState(BlockStates.ACTIVE)
            .addState(BlockStates.NATURAL);
    public static final BlockLegacy CREEPER_HEAD = REGISTRY.registerBlock(BlockFullNames.CREEPER_HEAD, BlockID.CREEPER_HEAD)
            .addState(BlockStates.FACING_DIRECTION);
    public static final BlockLegacy CRIMSON_BUTTON = REGISTRY.registerBlock(BlockFullNames.CRIMSON_BUTTON, BlockID.CRIMSON_BUTTON)
            .addState(BlockStates.FACING_DIRECTION)
            .addState(BlockStates.BUTTON_PRESSED_BIT);
    public static final BlockLegacy CRIMSON_DOOR = REGISTRY.registerBlock(BlockFullNames.CRIMSON_DOOR, BlockID.CRIMSON_DOOR)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.OPEN_BIT)
            .addState(BlockStates.UPPER_BLOCK_BIT)
            .addState(BlockStates.DOOR_HINGE_BIT);
    public static final BlockLegacy CRIMSON_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.CRIMSON_DOUBLE_SLAB, BlockID.CRIMSON_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy CRIMSON_FENCE = REGISTRY.registerBlock(BlockFullNames.CRIMSON_FENCE, BlockID.CRIMSON_FENCE);
    public static final BlockLegacy CRIMSON_FENCE_GATE = REGISTRY.registerBlock(BlockFullNames.CRIMSON_FENCE_GATE, BlockID.CRIMSON_FENCE_GATE)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.OPEN_BIT)
            .addState(BlockStates.IN_WALL_BIT);
    public static final BlockLegacy CRIMSON_FUNGUS = REGISTRY.registerBlock(BlockFullNames.CRIMSON_FUNGUS, BlockID.CRIMSON_FUNGUS);
    public static final BlockLegacy CRIMSON_HANGING_SIGN = REGISTRY.registerBlock(BlockFullNames.CRIMSON_HANGING_SIGN, BlockID.CRIMSON_HANGING_SIGN)
            .addState(BlockStates.FACING_DIRECTION)
            .addState(BlockStates.GROUND_SIGN_DIRECTION)
            .addState(BlockStates.HANGING)
            .addState(BlockStates.ATTACHED_BIT);
    public static final BlockLegacy CRIMSON_HYPHAE = REGISTRY.registerBlock(BlockFullNames.CRIMSON_HYPHAE, BlockID.CRIMSON_HYPHAE)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy CRIMSON_NYLIUM = REGISTRY.registerBlock(BlockFullNames.CRIMSON_NYLIUM, BlockID.CRIMSON_NYLIUM);
    public static final BlockLegacy CRIMSON_PLANKS = REGISTRY.registerBlock(BlockFullNames.CRIMSON_PLANKS, BlockID.CRIMSON_PLANKS);
    public static final BlockLegacy CRIMSON_PRESSURE_PLATE = REGISTRY.registerBlock(BlockFullNames.CRIMSON_PRESSURE_PLATE, BlockID.CRIMSON_PRESSURE_PLATE)
            .addState(BlockStates.REDSTONE_SIGNAL);
    public static final BlockLegacy CRIMSON_ROOTS = REGISTRY.registerBlock(BlockFullNames.CRIMSON_ROOTS, BlockID.CRIMSON_ROOTS);
    public static final BlockLegacy CRIMSON_SLAB = REGISTRY.registerBlock(BlockFullNames.CRIMSON_SLAB, BlockID.CRIMSON_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy CRIMSON_STAIRS = REGISTRY.registerBlock(BlockFullNames.CRIMSON_STAIRS, BlockID.CRIMSON_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy CRIMSON_STANDING_SIGN = REGISTRY.registerBlock(BlockFullNames.CRIMSON_STANDING_SIGN, BlockID.CRIMSON_STANDING_SIGN)
            .addState(BlockStates.GROUND_SIGN_DIRECTION);
    public static final BlockLegacy CRIMSON_STEM = REGISTRY.registerBlock(BlockFullNames.CRIMSON_STEM, BlockID.CRIMSON_STEM)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy CRIMSON_TRAPDOOR = REGISTRY.registerBlock(BlockFullNames.CRIMSON_TRAPDOOR, BlockID.CRIMSON_TRAPDOOR)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT)
            .addState(BlockStates.OPEN_BIT);
    public static final BlockLegacy CRIMSON_WALL_SIGN = REGISTRY.registerBlock(BlockFullNames.CRIMSON_WALL_SIGN, BlockID.CRIMSON_WALL_SIGN)
            .addState(BlockStates.FACING_DIRECTION);
    public static final BlockLegacy CRYING_OBSIDIAN = REGISTRY.registerBlock(BlockFullNames.CRYING_OBSIDIAN, BlockID.CRYING_OBSIDIAN);
    public static final BlockLegacy CUT_COPPER = REGISTRY.registerBlock(BlockFullNames.CUT_COPPER, BlockID.CUT_COPPER);
    public static final BlockLegacy CUT_COPPER_SLAB = REGISTRY.registerBlock(BlockFullNames.CUT_COPPER_SLAB, BlockID.CUT_COPPER_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy CUT_COPPER_STAIRS = REGISTRY.registerBlock(BlockFullNames.CUT_COPPER_STAIRS, BlockID.CUT_COPPER_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy CUT_RED_SANDSTONE = REGISTRY.registerBlock(BlockFullNames.CUT_RED_SANDSTONE, BlockID.CUT_RED_SANDSTONE);
    public static final BlockLegacy CUT_RED_SANDSTONE_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.CUT_RED_SANDSTONE_DOUBLE_SLAB, BlockID.CUT_RED_SANDSTONE_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy CUT_RED_SANDSTONE_SLAB = REGISTRY.registerBlock(BlockFullNames.CUT_RED_SANDSTONE_SLAB, BlockID.CUT_RED_SANDSTONE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy CUT_SANDSTONE = REGISTRY.registerBlock(BlockFullNames.CUT_SANDSTONE, BlockID.CUT_SANDSTONE);
    public static final BlockLegacy CUT_SANDSTONE_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.CUT_SANDSTONE_DOUBLE_SLAB, BlockID.CUT_SANDSTONE_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy CUT_SANDSTONE_SLAB = REGISTRY.registerBlock(BlockFullNames.CUT_SANDSTONE_SLAB, BlockID.CUT_SANDSTONE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy CYAN_CANDLE = REGISTRY.registerBlock(BlockFullNames.CYAN_CANDLE, BlockID.CYAN_CANDLE)
            .addState(BlockStates.CANDLES)
            .addState(BlockStates.LIT);
    public static final BlockLegacy CYAN_CANDLE_CAKE = REGISTRY.registerBlock(BlockFullNames.CYAN_CANDLE_CAKE, BlockID.CYAN_CANDLE_CAKE)
            .addState(BlockStates.LIT);
    public static final BlockLegacy CYAN_CARPET = REGISTRY.registerBlock(BlockFullNames.CYAN_CARPET, BlockID.CYAN_CARPET);
    public static final BlockLegacy CYAN_CONCRETE = REGISTRY.registerBlock(BlockFullNames.CYAN_CONCRETE, BlockID.CYAN_CONCRETE);
    public static final BlockLegacy CYAN_CONCRETE_POWDER = REGISTRY.registerBlock(BlockFullNames.CYAN_CONCRETE_POWDER, BlockID.CYAN_CONCRETE_POWDER);
    public static final BlockLegacy CYAN_GLAZED_TERRACOTTA = REGISTRY.registerBlock(BlockFullNames.CYAN_GLAZED_TERRACOTTA, BlockID.CYAN_GLAZED_TERRACOTTA)
            .addState(BlockStates.FACING_DIRECTION);
    public static final BlockLegacy CYAN_SHULKER_BOX = REGISTRY.registerBlock(BlockFullNames.CYAN_SHULKER_BOX, BlockID.CYAN_SHULKER_BOX);
    public static final BlockLegacy CYAN_STAINED_GLASS = REGISTRY.registerBlock(BlockFullNames.CYAN_STAINED_GLASS, BlockID.CYAN_STAINED_GLASS);
    public static final BlockLegacy CYAN_STAINED_GLASS_PANE = REGISTRY.registerBlock(BlockFullNames.CYAN_STAINED_GLASS_PANE, BlockID.CYAN_STAINED_GLASS_PANE);
    public static final BlockLegacy CYAN_TERRACOTTA = REGISTRY.registerBlock(BlockFullNames.CYAN_TERRACOTTA, BlockID.CYAN_TERRACOTTA);
    public static final BlockLegacy CYAN_WOOL = REGISTRY.registerBlock(BlockFullNames.CYAN_WOOL, BlockID.CYAN_WOOL);
    public static final BlockLegacy DAMAGED_ANVIL = REGISTRY.registerBlock(BlockFullNames.DAMAGED_ANVIL, BlockID.DAMAGED_ANVIL)
            .addState(BlockStates.MINECRAFT_CARDINAL_DIRECTION);
    public static final BlockLegacy DANDELION = REGISTRY.registerBlock(BlockFullNames.DANDELION, BlockID.DANDELION);
    public static final BlockLegacy DARK_OAK_BUTTON = REGISTRY.registerBlock(BlockFullNames.DARK_OAK_BUTTON, BlockID.DARK_OAK_BUTTON)
            .addState(BlockStates.FACING_DIRECTION)
            .addState(BlockStates.BUTTON_PRESSED_BIT);
    public static final BlockLegacy DARK_OAK_DOOR = REGISTRY.registerBlock(BlockFullNames.DARK_OAK_DOOR, BlockID.DARK_OAK_DOOR)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.OPEN_BIT)
            .addState(BlockStates.UPPER_BLOCK_BIT)
            .addState(BlockStates.DOOR_HINGE_BIT);
    public static final BlockLegacy DARK_OAK_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.DARK_OAK_DOUBLE_SLAB, BlockID.DARK_OAK_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy DARK_OAK_FENCE = REGISTRY.registerBlock(BlockFullNames.DARK_OAK_FENCE, BlockID.DARK_OAK_FENCE);
    public static final BlockLegacy DARK_OAK_FENCE_GATE = REGISTRY.registerBlock(BlockFullNames.DARK_OAK_FENCE_GATE, BlockID.DARK_OAK_FENCE_GATE)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.OPEN_BIT)
            .addState(BlockStates.IN_WALL_BIT);
    public static final BlockLegacy DARK_OAK_HANGING_SIGN = REGISTRY.registerBlock(BlockFullNames.DARK_OAK_HANGING_SIGN, BlockID.DARK_OAK_HANGING_SIGN)
            .addState(BlockStates.FACING_DIRECTION)
            .addState(BlockStates.GROUND_SIGN_DIRECTION)
            .addState(BlockStates.HANGING)
            .addState(BlockStates.ATTACHED_BIT);
    public static final BlockLegacy DARK_OAK_LEAVES = REGISTRY.registerBlock(BlockFullNames.DARK_OAK_LEAVES, BlockID.DARK_OAK_LEAVES)
            .addState(BlockStates.UPDATE_BIT)
            .addState(BlockStates.PERSISTENT_BIT);
    public static final BlockLegacy DARK_OAK_LOG = REGISTRY.registerBlock(BlockFullNames.DARK_OAK_LOG, BlockID.DARK_OAK_LOG)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy DARK_OAK_PLANKS = REGISTRY.registerBlock(BlockFullNames.DARK_OAK_PLANKS, BlockID.DARK_OAK_PLANKS);
    public static final BlockLegacy DARK_OAK_PRESSURE_PLATE = REGISTRY.registerBlock(BlockFullNames.DARK_OAK_PRESSURE_PLATE, BlockID.DARK_OAK_PRESSURE_PLATE)
            .addState(BlockStates.REDSTONE_SIGNAL);
    public static final BlockLegacy DARK_OAK_SAPLING = REGISTRY.registerBlock(BlockFullNames.DARK_OAK_SAPLING, BlockID.DARK_OAK_SAPLING)
            .addState(BlockStates.AGE_BIT);
    public static final BlockLegacy DARK_OAK_SLAB = REGISTRY.registerBlock(BlockFullNames.DARK_OAK_SLAB, BlockID.DARK_OAK_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy DARK_OAK_STAIRS = REGISTRY.registerBlock(BlockFullNames.DARK_OAK_STAIRS, BlockID.DARK_OAK_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy DARK_OAK_TRAPDOOR = REGISTRY.registerBlock(BlockFullNames.DARK_OAK_TRAPDOOR, BlockID.DARK_OAK_TRAPDOOR)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT)
            .addState(BlockStates.OPEN_BIT);
    public static final BlockLegacy DARK_OAK_WOOD = REGISTRY.registerBlock(BlockFullNames.DARK_OAK_WOOD, BlockID.DARK_OAK_WOOD)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy DARK_PRISMARINE = REGISTRY.registerBlock(BlockFullNames.DARK_PRISMARINE, BlockID.DARK_PRISMARINE);
    public static final BlockLegacy DARK_PRISMARINE_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.DARK_PRISMARINE_DOUBLE_SLAB, BlockID.DARK_PRISMARINE_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy DARK_PRISMARINE_SLAB = REGISTRY.registerBlock(BlockFullNames.DARK_PRISMARINE_SLAB, BlockID.DARK_PRISMARINE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy DARK_PRISMARINE_STAIRS = REGISTRY.registerBlock(BlockFullNames.DARK_PRISMARINE_STAIRS, BlockID.DARK_PRISMARINE_STAIRS)
            .addState(BlockStates.UPSIDE_DOWN_BIT)
            .addState(BlockStates.WEIRDO_DIRECTION);
    public static final BlockLegacy DARKOAK_STANDING_SIGN = REGISTRY.registerBlock(BlockFullNames.DARKOAK_STANDING_SIGN, BlockID.DARKOAK_STANDING_SIGN)
            .addState(BlockStates.GROUND_SIGN_DIRECTION);
    public static final BlockLegacy DARKOAK_WALL_SIGN = REGISTRY.registerBlock(BlockFullNames.DARKOAK_WALL_SIGN, BlockID.DARKOAK_WALL_SIGN)
            .addState(BlockStates.FACING_DIRECTION);
    public static final BlockLegacy DAYLIGHT_DETECTOR = REGISTRY.registerBlock(BlockFullNames.DAYLIGHT_DETECTOR, BlockID.DAYLIGHT_DETECTOR)
            .addState(BlockStates.REDSTONE_SIGNAL);
    public static final BlockLegacy DAYLIGHT_DETECTOR_INVERTED = REGISTRY.registerBlock(BlockFullNames.DAYLIGHT_DETECTOR_INVERTED, BlockID.DAYLIGHT_DETECTOR_INVERTED)
            .addState(BlockStates.REDSTONE_SIGNAL);
    public static final BlockLegacy DEAD_BRAIN_CORAL = REGISTRY.registerBlock(BlockFullNames.DEAD_BRAIN_CORAL, BlockID.DEAD_BRAIN_CORAL);
    public static final BlockLegacy DEAD_BRAIN_CORAL_BLOCK = REGISTRY.registerBlock(BlockFullNames.DEAD_BRAIN_CORAL_BLOCK, BlockID.DEAD_BRAIN_CORAL_BLOCK);
    public static final BlockLegacy DEAD_BRAIN_CORAL_FAN = REGISTRY.registerBlock(BlockFullNames.DEAD_BRAIN_CORAL_FAN, BlockID.DEAD_BRAIN_CORAL_FAN)
            .addState(BlockStates.CORAL_FAN_DIRECTION);
    public static final BlockLegacy DEAD_BRAIN_CORAL_WALL_FAN = REGISTRY.registerBlock(BlockFullNames.DEAD_BRAIN_CORAL_WALL_FAN, BlockID.DEAD_BRAIN_CORAL_WALL_FAN)
            .addState(BlockStates.CORAL_DIRECTION);
    public static final BlockLegacy DEAD_BUBBLE_CORAL = REGISTRY.registerBlock(BlockFullNames.DEAD_BUBBLE_CORAL, BlockID.DEAD_BUBBLE_CORAL);
    public static final BlockLegacy DEAD_BUBBLE_CORAL_BLOCK = REGISTRY.registerBlock(BlockFullNames.DEAD_BUBBLE_CORAL_BLOCK, BlockID.DEAD_BUBBLE_CORAL_BLOCK);
    public static final BlockLegacy DEAD_BUBBLE_CORAL_FAN = REGISTRY.registerBlock(BlockFullNames.DEAD_BUBBLE_CORAL_FAN, BlockID.DEAD_BUBBLE_CORAL_FAN)
            .addState(BlockStates.CORAL_FAN_DIRECTION);
    public static final BlockLegacy DEAD_BUBBLE_CORAL_WALL_FAN = REGISTRY.registerBlock(BlockFullNames.DEAD_BUBBLE_CORAL_WALL_FAN, BlockID.DEAD_BUBBLE_CORAL_WALL_FAN)
            .addState(BlockStates.CORAL_DIRECTION);
    public static final BlockLegacy DEAD_FIRE_CORAL = REGISTRY.registerBlock(BlockFullNames.DEAD_FIRE_CORAL, BlockID.DEAD_FIRE_CORAL);
    public static final BlockLegacy DEAD_FIRE_CORAL_BLOCK = REGISTRY.registerBlock(BlockFullNames.DEAD_FIRE_CORAL_BLOCK, BlockID.DEAD_FIRE_CORAL_BLOCK);
    public static final BlockLegacy DEAD_FIRE_CORAL_FAN = REGISTRY.registerBlock(BlockFullNames.DEAD_FIRE_CORAL_FAN, BlockID.DEAD_FIRE_CORAL_FAN)
            .addState(BlockStates.CORAL_FAN_DIRECTION);
    public static final BlockLegacy DEAD_FIRE_CORAL_WALL_FAN = REGISTRY.registerBlock(BlockFullNames.DEAD_FIRE_CORAL_WALL_FAN, BlockID.DEAD_FIRE_CORAL_WALL_FAN)
            .addState(BlockStates.CORAL_DIRECTION);
    public static final BlockLegacy DEAD_HORN_CORAL = REGISTRY.registerBlock(BlockFullNames.DEAD_HORN_CORAL, BlockID.DEAD_HORN_CORAL);
    public static final BlockLegacy DEAD_HORN_CORAL_BLOCK = REGISTRY.registerBlock(BlockFullNames.DEAD_HORN_CORAL_BLOCK, BlockID.DEAD_HORN_CORAL_BLOCK);
    public static final BlockLegacy DEAD_HORN_CORAL_FAN = REGISTRY.registerBlock(BlockFullNames.DEAD_HORN_CORAL_FAN, BlockID.DEAD_HORN_CORAL_FAN)
            .addState(BlockStates.CORAL_FAN_DIRECTION);
    public static final BlockLegacy DEAD_HORN_CORAL_WALL_FAN = REGISTRY.registerBlock(BlockFullNames.DEAD_HORN_CORAL_WALL_FAN, BlockID.DEAD_HORN_CORAL_WALL_FAN)
            .addState(BlockStates.CORAL_DIRECTION);
    public static final BlockLegacy DEAD_TUBE_CORAL = REGISTRY.registerBlock(BlockFullNames.DEAD_TUBE_CORAL, BlockID.DEAD_TUBE_CORAL);
    public static final BlockLegacy DEAD_TUBE_CORAL_BLOCK = REGISTRY.registerBlock(BlockFullNames.DEAD_TUBE_CORAL_BLOCK, BlockID.DEAD_TUBE_CORAL_BLOCK);
    public static final BlockLegacy DEAD_TUBE_CORAL_FAN = REGISTRY.registerBlock(BlockFullNames.DEAD_TUBE_CORAL_FAN, BlockID.DEAD_TUBE_CORAL_FAN)
            .addState(BlockStates.CORAL_FAN_DIRECTION);
    public static final BlockLegacy DEAD_TUBE_CORAL_WALL_FAN = REGISTRY.registerBlock(BlockFullNames.DEAD_TUBE_CORAL_WALL_FAN, BlockID.DEAD_TUBE_CORAL_WALL_FAN)
            .addState(BlockStates.CORAL_DIRECTION);
    public static final BlockLegacy DEADBUSH = REGISTRY.registerBlock(BlockFullNames.DEADBUSH, BlockID.DEADBUSH);
    public static final BlockLegacy DECORATED_POT = REGISTRY.registerBlock(BlockFullNames.DECORATED_POT, BlockID.DECORATED_POT)
            .addState(BlockStates.DIRECTION);
    public static final BlockLegacy DEEPSLATE = REGISTRY.registerBlock(BlockFullNames.DEEPSLATE, BlockID.DEEPSLATE)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy DEEPSLATE_BRICK_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.DEEPSLATE_BRICK_DOUBLE_SLAB, BlockID.DEEPSLATE_BRICK_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy DEEPSLATE_BRICK_SLAB = REGISTRY.registerBlock(BlockFullNames.DEEPSLATE_BRICK_SLAB, BlockID.DEEPSLATE_BRICK_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy DEEPSLATE_BRICK_STAIRS = REGISTRY.registerBlock(BlockFullNames.DEEPSLATE_BRICK_STAIRS, BlockID.DEEPSLATE_BRICK_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy DEEPSLATE_BRICK_WALL = REGISTRY.registerBlock(BlockFullNames.DEEPSLATE_BRICK_WALL, BlockID.DEEPSLATE_BRICK_WALL)
            .addState(BlockStates.WALL_POST_BIT)
            .addState(BlockStates.WALL_CONNECTION_TYPE_NORTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_EAST)
            .addState(BlockStates.WALL_CONNECTION_TYPE_SOUTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_WEST);
    public static final BlockLegacy DEEPSLATE_BRICKS = REGISTRY.registerBlock(BlockFullNames.DEEPSLATE_BRICKS, BlockID.DEEPSLATE_BRICKS);
    public static final BlockLegacy DEEPSLATE_COAL_ORE = REGISTRY.registerBlock(BlockFullNames.DEEPSLATE_COAL_ORE, BlockID.DEEPSLATE_COAL_ORE);
    public static final BlockLegacy DEEPSLATE_COPPER_ORE = REGISTRY.registerBlock(BlockFullNames.DEEPSLATE_COPPER_ORE, BlockID.DEEPSLATE_COPPER_ORE);
    public static final BlockLegacy DEEPSLATE_DIAMOND_ORE = REGISTRY.registerBlock(BlockFullNames.DEEPSLATE_DIAMOND_ORE, BlockID.DEEPSLATE_DIAMOND_ORE);
    public static final BlockLegacy DEEPSLATE_EMERALD_ORE = REGISTRY.registerBlock(BlockFullNames.DEEPSLATE_EMERALD_ORE, BlockID.DEEPSLATE_EMERALD_ORE);
    public static final BlockLegacy DEEPSLATE_GOLD_ORE = REGISTRY.registerBlock(BlockFullNames.DEEPSLATE_GOLD_ORE, BlockID.DEEPSLATE_GOLD_ORE);
    public static final BlockLegacy DEEPSLATE_IRON_ORE = REGISTRY.registerBlock(BlockFullNames.DEEPSLATE_IRON_ORE, BlockID.DEEPSLATE_IRON_ORE);
    public static final BlockLegacy DEEPSLATE_LAPIS_ORE = REGISTRY.registerBlock(BlockFullNames.DEEPSLATE_LAPIS_ORE, BlockID.DEEPSLATE_LAPIS_ORE);
    public static final BlockLegacy DEEPSLATE_REDSTONE_ORE = REGISTRY.registerBlock(BlockFullNames.DEEPSLATE_REDSTONE_ORE, BlockID.DEEPSLATE_REDSTONE_ORE);
    public static final BlockLegacy DEEPSLATE_TILE_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.DEEPSLATE_TILE_DOUBLE_SLAB, BlockID.DEEPSLATE_TILE_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy DEEPSLATE_TILE_SLAB = REGISTRY.registerBlock(BlockFullNames.DEEPSLATE_TILE_SLAB, BlockID.DEEPSLATE_TILE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy DEEPSLATE_TILE_STAIRS = REGISTRY.registerBlock(BlockFullNames.DEEPSLATE_TILE_STAIRS, BlockID.DEEPSLATE_TILE_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy DEEPSLATE_TILE_WALL = REGISTRY.registerBlock(BlockFullNames.DEEPSLATE_TILE_WALL, BlockID.DEEPSLATE_TILE_WALL)
            .addState(BlockStates.WALL_POST_BIT)
            .addState(BlockStates.WALL_CONNECTION_TYPE_NORTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_EAST)
            .addState(BlockStates.WALL_CONNECTION_TYPE_SOUTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_WEST);
    public static final BlockLegacy DEEPSLATE_TILES = REGISTRY.registerBlock(BlockFullNames.DEEPSLATE_TILES, BlockID.DEEPSLATE_TILES);
    public static final BlockLegacy DENY = REGISTRY.registerBlock(BlockFullNames.DENY, BlockID.DENY);
    public static final BlockLegacy DEPRECATED_ANVIL = REGISTRY.registerBlock(BlockFullNames.DEPRECATED_ANVIL, BlockID.DEPRECATED_ANVIL)
            .addState(BlockStates.MINECRAFT_CARDINAL_DIRECTION);
    public static final BlockLegacy DEPRECATED_PURPUR_BLOCK_1 = REGISTRY.registerBlock(BlockFullNames.DEPRECATED_PURPUR_BLOCK_1, BlockID.DEPRECATED_PURPUR_BLOCK_1)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy DEPRECATED_PURPUR_BLOCK_2 = REGISTRY.registerBlock(BlockFullNames.DEPRECATED_PURPUR_BLOCK_2, BlockID.DEPRECATED_PURPUR_BLOCK_2)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy DETECTOR_RAIL = REGISTRY.registerBlock(BlockFullNames.DETECTOR_RAIL, BlockID.DETECTOR_RAIL)
            .addState(BlockStates.RAIL_DIRECTION, BlockStateIntegerValues.DETECTOR_RAIL_MAX_RAIL_DIRECTION + 1)
            .addState(BlockStates.RAIL_DATA_BIT);
    public static final BlockLegacy DIAMOND_BLOCK = REGISTRY.registerBlock(BlockFullNames.DIAMOND_BLOCK, BlockID.DIAMOND_BLOCK);
    public static final BlockLegacy DIAMOND_ORE = REGISTRY.registerBlock(BlockFullNames.DIAMOND_ORE, BlockID.DIAMOND_ORE);
    public static final BlockLegacy DIORITE = REGISTRY.registerBlock(BlockFullNames.DIORITE, BlockID.DIORITE);
    public static final BlockLegacy DIORITE_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.DIORITE_DOUBLE_SLAB, BlockID.DIORITE_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy DIORITE_SLAB = REGISTRY.registerBlock(BlockFullNames.DIORITE_SLAB, BlockID.DIORITE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy DIORITE_STAIRS = REGISTRY.registerBlock(BlockFullNames.DIORITE_STAIRS, BlockID.DIORITE_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy DIORITE_WALL = REGISTRY.registerBlock(BlockFullNames.DIORITE_WALL, BlockID.DIORITE_WALL)
            .addState(BlockStates.WALL_POST_BIT)
            .addState(BlockStates.WALL_CONNECTION_TYPE_NORTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_EAST)
            .addState(BlockStates.WALL_CONNECTION_TYPE_SOUTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_WEST);
    public static final BlockLegacy DIRT = REGISTRY.registerBlock(BlockFullNames.DIRT, BlockID.DIRT);
    public static final BlockLegacy DIRT_WITH_ROOTS = REGISTRY.registerBlock(BlockFullNames.DIRT_WITH_ROOTS, BlockID.DIRT_WITH_ROOTS);
    public static final BlockLegacy DISPENSER = REGISTRY.registerBlock(BlockFullNames.DISPENSER, BlockID.DISPENSER)
            .addState(BlockStates.FACING_DIRECTION)
            .addState(BlockStates.TRIGGERED_BIT);
    public static final BlockLegacy DOUBLE_CUT_COPPER_SLAB = REGISTRY.registerBlock(BlockFullNames.DOUBLE_CUT_COPPER_SLAB, BlockID.DOUBLE_CUT_COPPER_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy DRAGON_EGG = REGISTRY.registerBlock(BlockFullNames.DRAGON_EGG, BlockID.DRAGON_EGG);
    public static final BlockLegacy DRAGON_HEAD = REGISTRY.registerBlock(BlockFullNames.DRAGON_HEAD, BlockID.DRAGON_HEAD)
            .addState(BlockStates.FACING_DIRECTION);
    public static final BlockLegacy DRIED_KELP_BLOCK = REGISTRY.registerBlock(BlockFullNames.DRIED_KELP_BLOCK, BlockID.DRIED_KELP_BLOCK);
    public static final BlockLegacy DRIPSTONE_BLOCK = REGISTRY.registerBlock(BlockFullNames.DRIPSTONE_BLOCK, BlockID.DRIPSTONE_BLOCK);
    public static final BlockLegacy DROPPER = REGISTRY.registerBlock(BlockFullNames.DROPPER, BlockID.DROPPER)
            .addState(BlockStates.FACING_DIRECTION)
            .addState(BlockStates.TRIGGERED_BIT);
    public static final BlockLegacy ELEMENT_0 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_0, BlockID.ELEMENT_0);
    public static final BlockLegacy ELEMENT_1 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_1, BlockID.ELEMENT_1);
    public static final BlockLegacy ELEMENT_10 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_10, BlockID.ELEMENT_10);
    public static final BlockLegacy ELEMENT_100 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_100, BlockID.ELEMENT_100);
    public static final BlockLegacy ELEMENT_101 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_101, BlockID.ELEMENT_101);
    public static final BlockLegacy ELEMENT_102 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_102, BlockID.ELEMENT_102);
    public static final BlockLegacy ELEMENT_103 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_103, BlockID.ELEMENT_103);
    public static final BlockLegacy ELEMENT_104 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_104, BlockID.ELEMENT_104);
    public static final BlockLegacy ELEMENT_105 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_105, BlockID.ELEMENT_105);
    public static final BlockLegacy ELEMENT_106 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_106, BlockID.ELEMENT_106);
    public static final BlockLegacy ELEMENT_107 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_107, BlockID.ELEMENT_107);
    public static final BlockLegacy ELEMENT_108 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_108, BlockID.ELEMENT_108);
    public static final BlockLegacy ELEMENT_109 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_109, BlockID.ELEMENT_109);
    public static final BlockLegacy ELEMENT_11 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_11, BlockID.ELEMENT_11);
    public static final BlockLegacy ELEMENT_110 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_110, BlockID.ELEMENT_110);
    public static final BlockLegacy ELEMENT_111 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_111, BlockID.ELEMENT_111);
    public static final BlockLegacy ELEMENT_112 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_112, BlockID.ELEMENT_112);
    public static final BlockLegacy ELEMENT_113 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_113, BlockID.ELEMENT_113);
    public static final BlockLegacy ELEMENT_114 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_114, BlockID.ELEMENT_114);
    public static final BlockLegacy ELEMENT_115 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_115, BlockID.ELEMENT_115);
    public static final BlockLegacy ELEMENT_116 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_116, BlockID.ELEMENT_116);
    public static final BlockLegacy ELEMENT_117 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_117, BlockID.ELEMENT_117);
    public static final BlockLegacy ELEMENT_118 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_118, BlockID.ELEMENT_118);
    public static final BlockLegacy ELEMENT_12 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_12, BlockID.ELEMENT_12);
    public static final BlockLegacy ELEMENT_13 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_13, BlockID.ELEMENT_13);
    public static final BlockLegacy ELEMENT_14 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_14, BlockID.ELEMENT_14);
    public static final BlockLegacy ELEMENT_15 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_15, BlockID.ELEMENT_15);
    public static final BlockLegacy ELEMENT_16 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_16, BlockID.ELEMENT_16);
    public static final BlockLegacy ELEMENT_17 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_17, BlockID.ELEMENT_17);
    public static final BlockLegacy ELEMENT_18 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_18, BlockID.ELEMENT_18);
    public static final BlockLegacy ELEMENT_19 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_19, BlockID.ELEMENT_19);
    public static final BlockLegacy ELEMENT_2 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_2, BlockID.ELEMENT_2);
    public static final BlockLegacy ELEMENT_20 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_20, BlockID.ELEMENT_20);
    public static final BlockLegacy ELEMENT_21 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_21, BlockID.ELEMENT_21);
    public static final BlockLegacy ELEMENT_22 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_22, BlockID.ELEMENT_22);
    public static final BlockLegacy ELEMENT_23 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_23, BlockID.ELEMENT_23);
    public static final BlockLegacy ELEMENT_24 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_24, BlockID.ELEMENT_24);
    public static final BlockLegacy ELEMENT_25 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_25, BlockID.ELEMENT_25);
    public static final BlockLegacy ELEMENT_26 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_26, BlockID.ELEMENT_26);
    public static final BlockLegacy ELEMENT_27 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_27, BlockID.ELEMENT_27);
    public static final BlockLegacy ELEMENT_28 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_28, BlockID.ELEMENT_28);
    public static final BlockLegacy ELEMENT_29 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_29, BlockID.ELEMENT_29);
    public static final BlockLegacy ELEMENT_3 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_3, BlockID.ELEMENT_3);
    public static final BlockLegacy ELEMENT_30 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_30, BlockID.ELEMENT_30);
    public static final BlockLegacy ELEMENT_31 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_31, BlockID.ELEMENT_31);
    public static final BlockLegacy ELEMENT_32 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_32, BlockID.ELEMENT_32);
    public static final BlockLegacy ELEMENT_33 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_33, BlockID.ELEMENT_33);
    public static final BlockLegacy ELEMENT_34 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_34, BlockID.ELEMENT_34);
    public static final BlockLegacy ELEMENT_35 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_35, BlockID.ELEMENT_35);
    public static final BlockLegacy ELEMENT_36 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_36, BlockID.ELEMENT_36);
    public static final BlockLegacy ELEMENT_37 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_37, BlockID.ELEMENT_37);
    public static final BlockLegacy ELEMENT_38 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_38, BlockID.ELEMENT_38);
    public static final BlockLegacy ELEMENT_39 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_39, BlockID.ELEMENT_39);
    public static final BlockLegacy ELEMENT_4 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_4, BlockID.ELEMENT_4);
    public static final BlockLegacy ELEMENT_40 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_40, BlockID.ELEMENT_40);
    public static final BlockLegacy ELEMENT_41 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_41, BlockID.ELEMENT_41);
    public static final BlockLegacy ELEMENT_42 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_42, BlockID.ELEMENT_42);
    public static final BlockLegacy ELEMENT_43 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_43, BlockID.ELEMENT_43);
    public static final BlockLegacy ELEMENT_44 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_44, BlockID.ELEMENT_44);
    public static final BlockLegacy ELEMENT_45 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_45, BlockID.ELEMENT_45);
    public static final BlockLegacy ELEMENT_46 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_46, BlockID.ELEMENT_46);
    public static final BlockLegacy ELEMENT_47 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_47, BlockID.ELEMENT_47);
    public static final BlockLegacy ELEMENT_48 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_48, BlockID.ELEMENT_48);
    public static final BlockLegacy ELEMENT_49 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_49, BlockID.ELEMENT_49);
    public static final BlockLegacy ELEMENT_5 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_5, BlockID.ELEMENT_5);
    public static final BlockLegacy ELEMENT_50 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_50, BlockID.ELEMENT_50);
    public static final BlockLegacy ELEMENT_51 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_51, BlockID.ELEMENT_51);
    public static final BlockLegacy ELEMENT_52 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_52, BlockID.ELEMENT_52);
    public static final BlockLegacy ELEMENT_53 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_53, BlockID.ELEMENT_53);
    public static final BlockLegacy ELEMENT_54 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_54, BlockID.ELEMENT_54);
    public static final BlockLegacy ELEMENT_55 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_55, BlockID.ELEMENT_55);
    public static final BlockLegacy ELEMENT_56 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_56, BlockID.ELEMENT_56);
    public static final BlockLegacy ELEMENT_57 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_57, BlockID.ELEMENT_57);
    public static final BlockLegacy ELEMENT_58 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_58, BlockID.ELEMENT_58);
    public static final BlockLegacy ELEMENT_59 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_59, BlockID.ELEMENT_59);
    public static final BlockLegacy ELEMENT_6 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_6, BlockID.ELEMENT_6);
    public static final BlockLegacy ELEMENT_60 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_60, BlockID.ELEMENT_60);
    public static final BlockLegacy ELEMENT_61 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_61, BlockID.ELEMENT_61);
    public static final BlockLegacy ELEMENT_62 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_62, BlockID.ELEMENT_62);
    public static final BlockLegacy ELEMENT_63 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_63, BlockID.ELEMENT_63);
    public static final BlockLegacy ELEMENT_64 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_64, BlockID.ELEMENT_64);
    public static final BlockLegacy ELEMENT_65 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_65, BlockID.ELEMENT_65);
    public static final BlockLegacy ELEMENT_66 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_66, BlockID.ELEMENT_66);
    public static final BlockLegacy ELEMENT_67 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_67, BlockID.ELEMENT_67);
    public static final BlockLegacy ELEMENT_68 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_68, BlockID.ELEMENT_68);
    public static final BlockLegacy ELEMENT_69 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_69, BlockID.ELEMENT_69);
    public static final BlockLegacy ELEMENT_7 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_7, BlockID.ELEMENT_7);
    public static final BlockLegacy ELEMENT_70 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_70, BlockID.ELEMENT_70);
    public static final BlockLegacy ELEMENT_71 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_71, BlockID.ELEMENT_71);
    public static final BlockLegacy ELEMENT_72 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_72, BlockID.ELEMENT_72);
    public static final BlockLegacy ELEMENT_73 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_73, BlockID.ELEMENT_73);
    public static final BlockLegacy ELEMENT_74 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_74, BlockID.ELEMENT_74);
    public static final BlockLegacy ELEMENT_75 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_75, BlockID.ELEMENT_75);
    public static final BlockLegacy ELEMENT_76 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_76, BlockID.ELEMENT_76);
    public static final BlockLegacy ELEMENT_77 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_77, BlockID.ELEMENT_77);
    public static final BlockLegacy ELEMENT_78 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_78, BlockID.ELEMENT_78);
    public static final BlockLegacy ELEMENT_79 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_79, BlockID.ELEMENT_79);
    public static final BlockLegacy ELEMENT_8 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_8, BlockID.ELEMENT_8);
    public static final BlockLegacy ELEMENT_80 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_80, BlockID.ELEMENT_80);
    public static final BlockLegacy ELEMENT_81 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_81, BlockID.ELEMENT_81);
    public static final BlockLegacy ELEMENT_82 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_82, BlockID.ELEMENT_82);
    public static final BlockLegacy ELEMENT_83 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_83, BlockID.ELEMENT_83);
    public static final BlockLegacy ELEMENT_84 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_84, BlockID.ELEMENT_84);
    public static final BlockLegacy ELEMENT_85 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_85, BlockID.ELEMENT_85);
    public static final BlockLegacy ELEMENT_86 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_86, BlockID.ELEMENT_86);
    public static final BlockLegacy ELEMENT_87 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_87, BlockID.ELEMENT_87);
    public static final BlockLegacy ELEMENT_88 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_88, BlockID.ELEMENT_88);
    public static final BlockLegacy ELEMENT_89 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_89, BlockID.ELEMENT_89);
    public static final BlockLegacy ELEMENT_9 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_9, BlockID.ELEMENT_9);
    public static final BlockLegacy ELEMENT_90 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_90, BlockID.ELEMENT_90);
    public static final BlockLegacy ELEMENT_91 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_91, BlockID.ELEMENT_91);
    public static final BlockLegacy ELEMENT_92 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_92, BlockID.ELEMENT_92);
    public static final BlockLegacy ELEMENT_93 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_93, BlockID.ELEMENT_93);
    public static final BlockLegacy ELEMENT_94 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_94, BlockID.ELEMENT_94);
    public static final BlockLegacy ELEMENT_95 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_95, BlockID.ELEMENT_95);
    public static final BlockLegacy ELEMENT_96 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_96, BlockID.ELEMENT_96);
    public static final BlockLegacy ELEMENT_97 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_97, BlockID.ELEMENT_97);
    public static final BlockLegacy ELEMENT_98 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_98, BlockID.ELEMENT_98);
    public static final BlockLegacy ELEMENT_99 = REGISTRY.registerBlock(BlockFullNames.ELEMENT_99, BlockID.ELEMENT_99);
    public static final BlockLegacy ELEMENT_CONSTRUCTOR = REGISTRY.registerBlock(BlockFullNames.ELEMENT_CONSTRUCTOR, BlockID.ELEMENT_CONSTRUCTOR)
            .addState(BlockStates.DIRECTION);
    public static final BlockLegacy EMERALD_BLOCK = REGISTRY.registerBlock(BlockFullNames.EMERALD_BLOCK, BlockID.EMERALD_BLOCK);
    public static final BlockLegacy EMERALD_ORE = REGISTRY.registerBlock(BlockFullNames.EMERALD_ORE, BlockID.EMERALD_ORE);
    public static final BlockLegacy ENCHANTING_TABLE = REGISTRY.registerBlock(BlockFullNames.ENCHANTING_TABLE, BlockID.ENCHANTING_TABLE);
    public static final BlockLegacy END_BRICK_STAIRS = REGISTRY.registerBlock(BlockFullNames.END_BRICK_STAIRS, BlockID.END_BRICK_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy END_BRICKS = REGISTRY.registerBlock(BlockFullNames.END_BRICKS, BlockID.END_BRICKS);
    public static final BlockLegacy END_GATEWAY = REGISTRY.registerBlock(BlockFullNames.END_GATEWAY, BlockID.END_GATEWAY);
    public static final BlockLegacy END_PORTAL = REGISTRY.registerBlock(BlockFullNames.END_PORTAL, BlockID.END_PORTAL);
    public static final BlockLegacy END_PORTAL_FRAME = REGISTRY.registerBlock(BlockFullNames.END_PORTAL_FRAME, BlockID.END_PORTAL_FRAME)
            .addState(BlockStates.MINECRAFT_CARDINAL_DIRECTION)
            .addState(BlockStates.END_PORTAL_EYE_BIT);
    public static final BlockLegacy END_ROD = REGISTRY.registerBlock(BlockFullNames.END_ROD, BlockID.END_ROD)
            .addState(BlockStates.FACING_DIRECTION);
    public static final BlockLegacy END_STONE = REGISTRY.registerBlock(BlockFullNames.END_STONE, BlockID.END_STONE);
    public static final BlockLegacy END_STONE_BRICK_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.END_STONE_BRICK_DOUBLE_SLAB, BlockID.END_STONE_BRICK_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy END_STONE_BRICK_SLAB = REGISTRY.registerBlock(BlockFullNames.END_STONE_BRICK_SLAB, BlockID.END_STONE_BRICK_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy END_STONE_BRICK_WALL = REGISTRY.registerBlock(BlockFullNames.END_STONE_BRICK_WALL, BlockID.END_STONE_BRICK_WALL)
            .addState(BlockStates.WALL_POST_BIT)
            .addState(BlockStates.WALL_CONNECTION_TYPE_NORTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_EAST)
            .addState(BlockStates.WALL_CONNECTION_TYPE_SOUTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_WEST);
    public static final BlockLegacy ENDER_CHEST = REGISTRY.registerBlock(BlockFullNames.ENDER_CHEST, BlockID.ENDER_CHEST)
            .addState(BlockStates.MINECRAFT_CARDINAL_DIRECTION);
    public static final BlockLegacy EXPOSED_CHISELED_COPPER = REGISTRY.registerBlock(BlockFullNames.EXPOSED_CHISELED_COPPER, BlockID.EXPOSED_CHISELED_COPPER);
    public static final BlockLegacy EXPOSED_COPPER = REGISTRY.registerBlock(BlockFullNames.EXPOSED_COPPER, BlockID.EXPOSED_COPPER);
    public static final BlockLegacy EXPOSED_COPPER_BULB = REGISTRY.registerBlock(BlockFullNames.EXPOSED_COPPER_BULB, BlockID.EXPOSED_COPPER_BULB)
            .addState(BlockStates.LIT)
            .addState(BlockStates.POWERED_BIT);
    public static final BlockLegacy EXPOSED_COPPER_DOOR = REGISTRY.registerBlock(BlockFullNames.EXPOSED_COPPER_DOOR, BlockID.EXPOSED_COPPER_DOOR)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.OPEN_BIT)
            .addState(BlockStates.UPPER_BLOCK_BIT)
            .addState(BlockStates.DOOR_HINGE_BIT);
    public static final BlockLegacy EXPOSED_COPPER_GRATE = REGISTRY.registerBlock(BlockFullNames.EXPOSED_COPPER_GRATE, BlockID.EXPOSED_COPPER_GRATE);
    public static final BlockLegacy EXPOSED_COPPER_TRAPDOOR = REGISTRY.registerBlock(BlockFullNames.EXPOSED_COPPER_TRAPDOOR, BlockID.EXPOSED_COPPER_TRAPDOOR)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT)
            .addState(BlockStates.OPEN_BIT);
    public static final BlockLegacy EXPOSED_CUT_COPPER = REGISTRY.registerBlock(BlockFullNames.EXPOSED_CUT_COPPER, BlockID.EXPOSED_CUT_COPPER);
    public static final BlockLegacy EXPOSED_CUT_COPPER_SLAB = REGISTRY.registerBlock(BlockFullNames.EXPOSED_CUT_COPPER_SLAB, BlockID.EXPOSED_CUT_COPPER_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy EXPOSED_CUT_COPPER_STAIRS = REGISTRY.registerBlock(BlockFullNames.EXPOSED_CUT_COPPER_STAIRS, BlockID.EXPOSED_CUT_COPPER_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy EXPOSED_DOUBLE_CUT_COPPER_SLAB = REGISTRY.registerBlock(BlockFullNames.EXPOSED_DOUBLE_CUT_COPPER_SLAB, BlockID.EXPOSED_DOUBLE_CUT_COPPER_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy FARMLAND = REGISTRY.registerBlock(BlockFullNames.FARMLAND, BlockID.FARMLAND)
            .addState(BlockStates.MOISTURIZED_AMOUNT);
    public static final BlockLegacy FENCE_GATE = REGISTRY.registerBlock(BlockFullNames.FENCE_GATE, BlockID.FENCE_GATE)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.OPEN_BIT)
            .addState(BlockStates.IN_WALL_BIT);
    public static final BlockLegacy FERN = REGISTRY.registerBlock(BlockFullNames.FERN, BlockID.FERN);
    public static final BlockLegacy FIRE = REGISTRY.registerBlock(BlockFullNames.FIRE, BlockID.FIRE)
            .addState(BlockStates.AGE);
    public static final BlockLegacy FIRE_CORAL = REGISTRY.registerBlock(BlockFullNames.FIRE_CORAL, BlockID.FIRE_CORAL);
    public static final BlockLegacy FIRE_CORAL_BLOCK = REGISTRY.registerBlock(BlockFullNames.FIRE_CORAL_BLOCK, BlockID.FIRE_CORAL_BLOCK);
    public static final BlockLegacy FIRE_CORAL_FAN = REGISTRY.registerBlock(BlockFullNames.FIRE_CORAL_FAN, BlockID.FIRE_CORAL_FAN)
            .addState(BlockStates.CORAL_FAN_DIRECTION);
    public static final BlockLegacy FIRE_CORAL_WALL_FAN = REGISTRY.registerBlock(BlockFullNames.FIRE_CORAL_WALL_FAN, BlockID.FIRE_CORAL_WALL_FAN)
            .addState(BlockStates.CORAL_DIRECTION);
    public static final BlockLegacy FLETCHING_TABLE = REGISTRY.registerBlock(BlockFullNames.FLETCHING_TABLE, BlockID.FLETCHING_TABLE);
    public static final BlockLegacy FLOWER_POT = REGISTRY.registerBlock(BlockFullNames.FLOWER_POT, BlockID.FLOWER_POT)
            .addState(BlockStates.UPDATE_BIT);
    public static final BlockLegacy FLOWERING_AZALEA = REGISTRY.registerBlock(BlockFullNames.FLOWERING_AZALEA, BlockID.FLOWERING_AZALEA);
    public static final BlockLegacy FLOWING_LAVA = REGISTRY.registerBlock(BlockFullNames.FLOWING_LAVA, BlockID.FLOWING_LAVA)
            .addState(BlockStates.LIQUID_DEPTH);
    public static final BlockLegacy FLOWING_WATER = REGISTRY.registerBlock(BlockFullNames.FLOWING_WATER, BlockID.FLOWING_WATER)
            .addState(BlockStates.LIQUID_DEPTH);
    public static final BlockLegacy FRAME = REGISTRY.registerBlock(BlockFullNames.FRAME, BlockID.FRAME)
            .addState(BlockStates.FACING_DIRECTION)
            .addState(BlockStates.ITEM_FRAME_MAP_BIT)
            .addState(BlockStates.ITEM_FRAME_PHOTO_BIT);
    public static final BlockLegacy FROG_SPAWN = REGISTRY.registerBlock(BlockFullNames.FROG_SPAWN, BlockID.FROG_SPAWN);
    public static final BlockLegacy FROSTED_ICE = REGISTRY.registerBlock(BlockFullNames.FROSTED_ICE, BlockID.FROSTED_ICE)
            .addState(BlockStates.AGE, BlockStateIntegerValues.FROSTED_ICE_MAX_AGE + 1);
    public static final BlockLegacy FURNACE = REGISTRY.registerBlock(BlockFullNames.FURNACE, BlockID.FURNACE)
            .addState(BlockStates.MINECRAFT_CARDINAL_DIRECTION);
    public static final BlockLegacy GILDED_BLACKSTONE = REGISTRY.registerBlock(BlockFullNames.GILDED_BLACKSTONE, BlockID.GILDED_BLACKSTONE);
    public static final BlockLegacy GLASS = REGISTRY.registerBlock(BlockFullNames.GLASS, BlockID.GLASS);
    public static final BlockLegacy GLASS_PANE = REGISTRY.registerBlock(BlockFullNames.GLASS_PANE, BlockID.GLASS_PANE);
    public static final BlockLegacy GLOW_FRAME = REGISTRY.registerBlock(BlockFullNames.GLOW_FRAME, BlockID.GLOW_FRAME)
            .addState(BlockStates.FACING_DIRECTION)
            .addState(BlockStates.ITEM_FRAME_MAP_BIT)
            .addState(BlockStates.ITEM_FRAME_PHOTO_BIT);
    public static final BlockLegacy GLOW_LICHEN = REGISTRY.registerBlock(BlockFullNames.GLOW_LICHEN, BlockID.GLOW_LICHEN)
            .addState(BlockStates.MULTI_FACE_DIRECTION_BITS);
    public static final BlockLegacy GLOWINGOBSIDIAN = REGISTRY.registerBlock(BlockFullNames.GLOWINGOBSIDIAN, BlockID.GLOWINGOBSIDIAN);
    public static final BlockLegacy GLOWSTONE = REGISTRY.registerBlock(BlockFullNames.GLOWSTONE, BlockID.GLOWSTONE);
    public static final BlockLegacy GOLD_BLOCK = REGISTRY.registerBlock(BlockFullNames.GOLD_BLOCK, BlockID.GOLD_BLOCK);
    public static final BlockLegacy GOLD_ORE = REGISTRY.registerBlock(BlockFullNames.GOLD_ORE, BlockID.GOLD_ORE);
    public static final BlockLegacy GOLDEN_RAIL = REGISTRY.registerBlock(BlockFullNames.GOLDEN_RAIL, BlockID.GOLDEN_RAIL)
            .addState(BlockStates.RAIL_DIRECTION, BlockStateIntegerValues.GOLDEN_RAIL_MAX_RAIL_DIRECTION + 1)
            .addState(BlockStates.RAIL_DATA_BIT);
    public static final BlockLegacy GRANITE = REGISTRY.registerBlock(BlockFullNames.GRANITE, BlockID.GRANITE);
    public static final BlockLegacy GRANITE_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.GRANITE_DOUBLE_SLAB, BlockID.GRANITE_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy GRANITE_SLAB = REGISTRY.registerBlock(BlockFullNames.GRANITE_SLAB, BlockID.GRANITE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy GRANITE_STAIRS = REGISTRY.registerBlock(BlockFullNames.GRANITE_STAIRS, BlockID.GRANITE_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy GRANITE_WALL = REGISTRY.registerBlock(BlockFullNames.GRANITE_WALL, BlockID.GRANITE_WALL)
            .addState(BlockStates.WALL_POST_BIT)
            .addState(BlockStates.WALL_CONNECTION_TYPE_NORTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_EAST)
            .addState(BlockStates.WALL_CONNECTION_TYPE_SOUTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_WEST);
    public static final BlockLegacy GRASS_BLOCK = REGISTRY.registerBlock(BlockFullNames.GRASS_BLOCK, BlockID.GRASS_BLOCK);
    public static final BlockLegacy GRASS_PATH = REGISTRY.registerBlock(BlockFullNames.GRASS_PATH, BlockID.GRASS_PATH);
    public static final BlockLegacy GRAVEL = REGISTRY.registerBlock(BlockFullNames.GRAVEL, BlockID.GRAVEL);
    public static final BlockLegacy GRAY_CANDLE = REGISTRY.registerBlock(BlockFullNames.GRAY_CANDLE, BlockID.GRAY_CANDLE)
            .addState(BlockStates.CANDLES)
            .addState(BlockStates.LIT);
    public static final BlockLegacy GRAY_CANDLE_CAKE = REGISTRY.registerBlock(BlockFullNames.GRAY_CANDLE_CAKE, BlockID.GRAY_CANDLE_CAKE)
            .addState(BlockStates.LIT);
    public static final BlockLegacy GRAY_CARPET = REGISTRY.registerBlock(BlockFullNames.GRAY_CARPET, BlockID.GRAY_CARPET);
    public static final BlockLegacy GRAY_CONCRETE = REGISTRY.registerBlock(BlockFullNames.GRAY_CONCRETE, BlockID.GRAY_CONCRETE);
    public static final BlockLegacy GRAY_CONCRETE_POWDER = REGISTRY.registerBlock(BlockFullNames.GRAY_CONCRETE_POWDER, BlockID.GRAY_CONCRETE_POWDER);
    public static final BlockLegacy GRAY_GLAZED_TERRACOTTA = REGISTRY.registerBlock(BlockFullNames.GRAY_GLAZED_TERRACOTTA, BlockID.GRAY_GLAZED_TERRACOTTA)
            .addState(BlockStates.FACING_DIRECTION);
    public static final BlockLegacy GRAY_SHULKER_BOX = REGISTRY.registerBlock(BlockFullNames.GRAY_SHULKER_BOX, BlockID.GRAY_SHULKER_BOX);
    public static final BlockLegacy GRAY_STAINED_GLASS = REGISTRY.registerBlock(BlockFullNames.GRAY_STAINED_GLASS, BlockID.GRAY_STAINED_GLASS);
    public static final BlockLegacy GRAY_STAINED_GLASS_PANE = REGISTRY.registerBlock(BlockFullNames.GRAY_STAINED_GLASS_PANE, BlockID.GRAY_STAINED_GLASS_PANE);
    public static final BlockLegacy GRAY_TERRACOTTA = REGISTRY.registerBlock(BlockFullNames.GRAY_TERRACOTTA, BlockID.GRAY_TERRACOTTA);
    public static final BlockLegacy GRAY_WOOL = REGISTRY.registerBlock(BlockFullNames.GRAY_WOOL, BlockID.GRAY_WOOL);
    public static final BlockLegacy GREEN_CANDLE = REGISTRY.registerBlock(BlockFullNames.GREEN_CANDLE, BlockID.GREEN_CANDLE)
            .addState(BlockStates.CANDLES)
            .addState(BlockStates.LIT);
    public static final BlockLegacy GREEN_CANDLE_CAKE = REGISTRY.registerBlock(BlockFullNames.GREEN_CANDLE_CAKE, BlockID.GREEN_CANDLE_CAKE)
            .addState(BlockStates.LIT);
    public static final BlockLegacy GREEN_CARPET = REGISTRY.registerBlock(BlockFullNames.GREEN_CARPET, BlockID.GREEN_CARPET);
    public static final BlockLegacy GREEN_CONCRETE = REGISTRY.registerBlock(BlockFullNames.GREEN_CONCRETE, BlockID.GREEN_CONCRETE);
    public static final BlockLegacy GREEN_CONCRETE_POWDER = REGISTRY.registerBlock(BlockFullNames.GREEN_CONCRETE_POWDER, BlockID.GREEN_CONCRETE_POWDER);
    public static final BlockLegacy GREEN_GLAZED_TERRACOTTA = REGISTRY.registerBlock(BlockFullNames.GREEN_GLAZED_TERRACOTTA, BlockID.GREEN_GLAZED_TERRACOTTA)
            .addState(BlockStates.FACING_DIRECTION);
    public static final BlockLegacy GREEN_SHULKER_BOX = REGISTRY.registerBlock(BlockFullNames.GREEN_SHULKER_BOX, BlockID.GREEN_SHULKER_BOX);
    public static final BlockLegacy GREEN_STAINED_GLASS = REGISTRY.registerBlock(BlockFullNames.GREEN_STAINED_GLASS, BlockID.GREEN_STAINED_GLASS);
    public static final BlockLegacy GREEN_STAINED_GLASS_PANE = REGISTRY.registerBlock(BlockFullNames.GREEN_STAINED_GLASS_PANE, BlockID.GREEN_STAINED_GLASS_PANE);
    public static final BlockLegacy GREEN_TERRACOTTA = REGISTRY.registerBlock(BlockFullNames.GREEN_TERRACOTTA, BlockID.GREEN_TERRACOTTA);
    public static final BlockLegacy GREEN_WOOL = REGISTRY.registerBlock(BlockFullNames.GREEN_WOOL, BlockID.GREEN_WOOL);
    public static final BlockLegacy GRINDSTONE = REGISTRY.registerBlock(BlockFullNames.GRINDSTONE, BlockID.GRINDSTONE)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.ATTACHMENT);
    public static final BlockLegacy HANGING_ROOTS = REGISTRY.registerBlock(BlockFullNames.HANGING_ROOTS, BlockID.HANGING_ROOTS);
    public static final BlockLegacy HARD_BLACK_STAINED_GLASS = REGISTRY.registerBlock(BlockFullNames.HARD_BLACK_STAINED_GLASS, BlockID.HARD_BLACK_STAINED_GLASS);
    public static final BlockLegacy HARD_BLACK_STAINED_GLASS_PANE = REGISTRY.registerBlock(BlockFullNames.HARD_BLACK_STAINED_GLASS_PANE, BlockID.HARD_BLACK_STAINED_GLASS_PANE);
    public static final BlockLegacy HARD_BLUE_STAINED_GLASS = REGISTRY.registerBlock(BlockFullNames.HARD_BLUE_STAINED_GLASS, BlockID.HARD_BLUE_STAINED_GLASS);
    public static final BlockLegacy HARD_BLUE_STAINED_GLASS_PANE = REGISTRY.registerBlock(BlockFullNames.HARD_BLUE_STAINED_GLASS_PANE, BlockID.HARD_BLUE_STAINED_GLASS_PANE);
    public static final BlockLegacy HARD_BROWN_STAINED_GLASS = REGISTRY.registerBlock(BlockFullNames.HARD_BROWN_STAINED_GLASS, BlockID.HARD_BROWN_STAINED_GLASS);
    public static final BlockLegacy HARD_BROWN_STAINED_GLASS_PANE = REGISTRY.registerBlock(BlockFullNames.HARD_BROWN_STAINED_GLASS_PANE, BlockID.HARD_BROWN_STAINED_GLASS_PANE);
    public static final BlockLegacy HARD_CYAN_STAINED_GLASS = REGISTRY.registerBlock(BlockFullNames.HARD_CYAN_STAINED_GLASS, BlockID.HARD_CYAN_STAINED_GLASS);
    public static final BlockLegacy HARD_CYAN_STAINED_GLASS_PANE = REGISTRY.registerBlock(BlockFullNames.HARD_CYAN_STAINED_GLASS_PANE, BlockID.HARD_CYAN_STAINED_GLASS_PANE);
    public static final BlockLegacy HARD_GLASS = REGISTRY.registerBlock(BlockFullNames.HARD_GLASS, BlockID.HARD_GLASS);
    public static final BlockLegacy HARD_GLASS_PANE = REGISTRY.registerBlock(BlockFullNames.HARD_GLASS_PANE, BlockID.HARD_GLASS_PANE);
    public static final BlockLegacy HARD_GRAY_STAINED_GLASS = REGISTRY.registerBlock(BlockFullNames.HARD_GRAY_STAINED_GLASS, BlockID.HARD_GRAY_STAINED_GLASS);
    public static final BlockLegacy HARD_GRAY_STAINED_GLASS_PANE = REGISTRY.registerBlock(BlockFullNames.HARD_GRAY_STAINED_GLASS_PANE, BlockID.HARD_GRAY_STAINED_GLASS_PANE);
    public static final BlockLegacy HARD_GREEN_STAINED_GLASS = REGISTRY.registerBlock(BlockFullNames.HARD_GREEN_STAINED_GLASS, BlockID.HARD_GREEN_STAINED_GLASS);
    public static final BlockLegacy HARD_GREEN_STAINED_GLASS_PANE = REGISTRY.registerBlock(BlockFullNames.HARD_GREEN_STAINED_GLASS_PANE, BlockID.HARD_GREEN_STAINED_GLASS_PANE);
    public static final BlockLegacy HARD_LIGHT_BLUE_STAINED_GLASS = REGISTRY.registerBlock(BlockFullNames.HARD_LIGHT_BLUE_STAINED_GLASS, BlockID.HARD_LIGHT_BLUE_STAINED_GLASS);
    public static final BlockLegacy HARD_LIGHT_BLUE_STAINED_GLASS_PANE = REGISTRY.registerBlock(BlockFullNames.HARD_LIGHT_BLUE_STAINED_GLASS_PANE, BlockID.HARD_LIGHT_BLUE_STAINED_GLASS_PANE);
    public static final BlockLegacy HARD_LIGHT_GRAY_STAINED_GLASS = REGISTRY.registerBlock(BlockFullNames.HARD_LIGHT_GRAY_STAINED_GLASS, BlockID.HARD_LIGHT_GRAY_STAINED_GLASS);
    public static final BlockLegacy HARD_LIGHT_GRAY_STAINED_GLASS_PANE = REGISTRY.registerBlock(BlockFullNames.HARD_LIGHT_GRAY_STAINED_GLASS_PANE, BlockID.HARD_LIGHT_GRAY_STAINED_GLASS_PANE);
    public static final BlockLegacy HARD_LIME_STAINED_GLASS = REGISTRY.registerBlock(BlockFullNames.HARD_LIME_STAINED_GLASS, BlockID.HARD_LIME_STAINED_GLASS);
    public static final BlockLegacy HARD_LIME_STAINED_GLASS_PANE = REGISTRY.registerBlock(BlockFullNames.HARD_LIME_STAINED_GLASS_PANE, BlockID.HARD_LIME_STAINED_GLASS_PANE);
    public static final BlockLegacy HARD_MAGENTA_STAINED_GLASS = REGISTRY.registerBlock(BlockFullNames.HARD_MAGENTA_STAINED_GLASS, BlockID.HARD_MAGENTA_STAINED_GLASS);
    public static final BlockLegacy HARD_MAGENTA_STAINED_GLASS_PANE = REGISTRY.registerBlock(BlockFullNames.HARD_MAGENTA_STAINED_GLASS_PANE, BlockID.HARD_MAGENTA_STAINED_GLASS_PANE);
    public static final BlockLegacy HARD_ORANGE_STAINED_GLASS = REGISTRY.registerBlock(BlockFullNames.HARD_ORANGE_STAINED_GLASS, BlockID.HARD_ORANGE_STAINED_GLASS);
    public static final BlockLegacy HARD_ORANGE_STAINED_GLASS_PANE = REGISTRY.registerBlock(BlockFullNames.HARD_ORANGE_STAINED_GLASS_PANE, BlockID.HARD_ORANGE_STAINED_GLASS_PANE);
    public static final BlockLegacy HARD_PINK_STAINED_GLASS = REGISTRY.registerBlock(BlockFullNames.HARD_PINK_STAINED_GLASS, BlockID.HARD_PINK_STAINED_GLASS);
    public static final BlockLegacy HARD_PINK_STAINED_GLASS_PANE = REGISTRY.registerBlock(BlockFullNames.HARD_PINK_STAINED_GLASS_PANE, BlockID.HARD_PINK_STAINED_GLASS_PANE);
    public static final BlockLegacy HARD_PURPLE_STAINED_GLASS = REGISTRY.registerBlock(BlockFullNames.HARD_PURPLE_STAINED_GLASS, BlockID.HARD_PURPLE_STAINED_GLASS);
    public static final BlockLegacy HARD_PURPLE_STAINED_GLASS_PANE = REGISTRY.registerBlock(BlockFullNames.HARD_PURPLE_STAINED_GLASS_PANE, BlockID.HARD_PURPLE_STAINED_GLASS_PANE);
    public static final BlockLegacy HARD_RED_STAINED_GLASS = REGISTRY.registerBlock(BlockFullNames.HARD_RED_STAINED_GLASS, BlockID.HARD_RED_STAINED_GLASS);
    public static final BlockLegacy HARD_RED_STAINED_GLASS_PANE = REGISTRY.registerBlock(BlockFullNames.HARD_RED_STAINED_GLASS_PANE, BlockID.HARD_RED_STAINED_GLASS_PANE);
    public static final BlockLegacy HARD_WHITE_STAINED_GLASS = REGISTRY.registerBlock(BlockFullNames.HARD_WHITE_STAINED_GLASS, BlockID.HARD_WHITE_STAINED_GLASS);
    public static final BlockLegacy HARD_WHITE_STAINED_GLASS_PANE = REGISTRY.registerBlock(BlockFullNames.HARD_WHITE_STAINED_GLASS_PANE, BlockID.HARD_WHITE_STAINED_GLASS_PANE);
    public static final BlockLegacy HARD_YELLOW_STAINED_GLASS = REGISTRY.registerBlock(BlockFullNames.HARD_YELLOW_STAINED_GLASS, BlockID.HARD_YELLOW_STAINED_GLASS);
    public static final BlockLegacy HARD_YELLOW_STAINED_GLASS_PANE = REGISTRY.registerBlock(BlockFullNames.HARD_YELLOW_STAINED_GLASS_PANE, BlockID.HARD_YELLOW_STAINED_GLASS_PANE);
    public static final BlockLegacy HARDENED_CLAY = REGISTRY.registerBlock(BlockFullNames.HARDENED_CLAY, BlockID.HARDENED_CLAY);
    public static final BlockLegacy HAY_BLOCK = REGISTRY.registerBlock(BlockFullNames.HAY_BLOCK, BlockID.HAY_BLOCK)
            .addState(BlockStates.DEPRECATED)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy HEAVY_CORE = REGISTRY.registerBlock(BlockFullNames.HEAVY_CORE, BlockID.HEAVY_CORE);
    public static final BlockLegacy HEAVY_WEIGHTED_PRESSURE_PLATE = REGISTRY.registerBlock(BlockFullNames.HEAVY_WEIGHTED_PRESSURE_PLATE, BlockID.HEAVY_WEIGHTED_PRESSURE_PLATE)
            .addState(BlockStates.REDSTONE_SIGNAL);
    public static final BlockLegacy HONEY_BLOCK = REGISTRY.registerBlock(BlockFullNames.HONEY_BLOCK, BlockID.HONEY_BLOCK);
    public static final BlockLegacy HONEYCOMB_BLOCK = REGISTRY.registerBlock(BlockFullNames.HONEYCOMB_BLOCK, BlockID.HONEYCOMB_BLOCK);
    public static final BlockLegacy HOPPER = REGISTRY.registerBlock(BlockFullNames.HOPPER, BlockID.HOPPER)
            .addState(BlockStates.FACING_DIRECTION)
            .addState(BlockStates.TOGGLE_BIT);
    public static final BlockLegacy HORN_CORAL = REGISTRY.registerBlock(BlockFullNames.HORN_CORAL, BlockID.HORN_CORAL);
    public static final BlockLegacy HORN_CORAL_BLOCK = REGISTRY.registerBlock(BlockFullNames.HORN_CORAL_BLOCK, BlockID.HORN_CORAL_BLOCK);
    public static final BlockLegacy HORN_CORAL_FAN = REGISTRY.registerBlock(BlockFullNames.HORN_CORAL_FAN, BlockID.HORN_CORAL_FAN)
            .addState(BlockStates.CORAL_FAN_DIRECTION);
    public static final BlockLegacy HORN_CORAL_WALL_FAN = REGISTRY.registerBlock(BlockFullNames.HORN_CORAL_WALL_FAN, BlockID.HORN_CORAL_WALL_FAN)
            .addState(BlockStates.CORAL_DIRECTION);
    public static final BlockLegacy ICE = REGISTRY.registerBlock(BlockFullNames.ICE, BlockID.ICE);
    public static final BlockLegacy INFESTED_CHISELED_STONE_BRICKS = REGISTRY.registerBlock(BlockFullNames.INFESTED_CHISELED_STONE_BRICKS, BlockID.INFESTED_CHISELED_STONE_BRICKS);
    public static final BlockLegacy INFESTED_COBBLESTONE = REGISTRY.registerBlock(BlockFullNames.INFESTED_COBBLESTONE, BlockID.INFESTED_COBBLESTONE);
    public static final BlockLegacy INFESTED_CRACKED_STONE_BRICKS = REGISTRY.registerBlock(BlockFullNames.INFESTED_CRACKED_STONE_BRICKS, BlockID.INFESTED_CRACKED_STONE_BRICKS);
    public static final BlockLegacy INFESTED_DEEPSLATE = REGISTRY.registerBlock(BlockFullNames.INFESTED_DEEPSLATE, BlockID.INFESTED_DEEPSLATE)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy INFESTED_MOSSY_STONE_BRICKS = REGISTRY.registerBlock(BlockFullNames.INFESTED_MOSSY_STONE_BRICKS, BlockID.INFESTED_MOSSY_STONE_BRICKS);
    public static final BlockLegacy INFESTED_STONE = REGISTRY.registerBlock(BlockFullNames.INFESTED_STONE, BlockID.INFESTED_STONE);
    public static final BlockLegacy INFESTED_STONE_BRICKS = REGISTRY.registerBlock(BlockFullNames.INFESTED_STONE_BRICKS, BlockID.INFESTED_STONE_BRICKS);
    public static final BlockLegacy INFO_UPDATE = REGISTRY.registerBlock(BlockFullNames.INFO_UPDATE, BlockID.INFO_UPDATE);
    public static final BlockLegacy INFO_UPDATE2 = REGISTRY.registerBlock(BlockFullNames.INFO_UPDATE2, BlockID.INFO_UPDATE2);
    public static final BlockLegacy INVISIBLE_BEDROCK = REGISTRY.registerBlock(BlockFullNames.INVISIBLE_BEDROCK, BlockID.INVISIBLE_BEDROCK);
    public static final BlockLegacy IRON_BARS = REGISTRY.registerBlock(BlockFullNames.IRON_BARS, BlockID.IRON_BARS);
    public static final BlockLegacy IRON_BLOCK = REGISTRY.registerBlock(BlockFullNames.IRON_BLOCK, BlockID.IRON_BLOCK);
    public static final BlockLegacy IRON_DOOR = REGISTRY.registerBlock(BlockFullNames.IRON_DOOR, BlockID.IRON_DOOR)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.OPEN_BIT)
            .addState(BlockStates.UPPER_BLOCK_BIT)
            .addState(BlockStates.DOOR_HINGE_BIT);
    public static final BlockLegacy IRON_ORE = REGISTRY.registerBlock(BlockFullNames.IRON_ORE, BlockID.IRON_ORE);
    public static final BlockLegacy IRON_TRAPDOOR = REGISTRY.registerBlock(BlockFullNames.IRON_TRAPDOOR, BlockID.IRON_TRAPDOOR)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT)
            .addState(BlockStates.OPEN_BIT);
    public static final BlockLegacy JIGSAW = REGISTRY.registerBlock(BlockFullNames.JIGSAW, BlockID.JIGSAW)
            .addState(BlockStates.FACING_DIRECTION)
            .addState(BlockStates.ROTATION);
    public static final BlockLegacy JUKEBOX = REGISTRY.registerBlock(BlockFullNames.JUKEBOX, BlockID.JUKEBOX);
    public static final BlockLegacy JUNGLE_BUTTON = REGISTRY.registerBlock(BlockFullNames.JUNGLE_BUTTON, BlockID.JUNGLE_BUTTON)
            .addState(BlockStates.FACING_DIRECTION)
            .addState(BlockStates.BUTTON_PRESSED_BIT);
    public static final BlockLegacy JUNGLE_DOOR = REGISTRY.registerBlock(BlockFullNames.JUNGLE_DOOR, BlockID.JUNGLE_DOOR)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.OPEN_BIT)
            .addState(BlockStates.UPPER_BLOCK_BIT)
            .addState(BlockStates.DOOR_HINGE_BIT);
    public static final BlockLegacy JUNGLE_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.JUNGLE_DOUBLE_SLAB, BlockID.JUNGLE_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy JUNGLE_FENCE = REGISTRY.registerBlock(BlockFullNames.JUNGLE_FENCE, BlockID.JUNGLE_FENCE);
    public static final BlockLegacy JUNGLE_FENCE_GATE = REGISTRY.registerBlock(BlockFullNames.JUNGLE_FENCE_GATE, BlockID.JUNGLE_FENCE_GATE)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.OPEN_BIT)
            .addState(BlockStates.IN_WALL_BIT);
    public static final BlockLegacy JUNGLE_HANGING_SIGN = REGISTRY.registerBlock(BlockFullNames.JUNGLE_HANGING_SIGN, BlockID.JUNGLE_HANGING_SIGN)
            .addState(BlockStates.FACING_DIRECTION)
            .addState(BlockStates.GROUND_SIGN_DIRECTION)
            .addState(BlockStates.HANGING)
            .addState(BlockStates.ATTACHED_BIT);
    public static final BlockLegacy JUNGLE_LEAVES = REGISTRY.registerBlock(BlockFullNames.JUNGLE_LEAVES, BlockID.JUNGLE_LEAVES)
            .addState(BlockStates.UPDATE_BIT)
            .addState(BlockStates.PERSISTENT_BIT);
    public static final BlockLegacy JUNGLE_LOG = REGISTRY.registerBlock(BlockFullNames.JUNGLE_LOG, BlockID.JUNGLE_LOG)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy JUNGLE_PLANKS = REGISTRY.registerBlock(BlockFullNames.JUNGLE_PLANKS, BlockID.JUNGLE_PLANKS);
    public static final BlockLegacy JUNGLE_PRESSURE_PLATE = REGISTRY.registerBlock(BlockFullNames.JUNGLE_PRESSURE_PLATE, BlockID.JUNGLE_PRESSURE_PLATE)
            .addState(BlockStates.REDSTONE_SIGNAL);
    public static final BlockLegacy JUNGLE_SAPLING = REGISTRY.registerBlock(BlockFullNames.JUNGLE_SAPLING, BlockID.JUNGLE_SAPLING)
            .addState(BlockStates.AGE_BIT);
    public static final BlockLegacy JUNGLE_SLAB = REGISTRY.registerBlock(BlockFullNames.JUNGLE_SLAB, BlockID.JUNGLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy JUNGLE_STAIRS = REGISTRY.registerBlock(BlockFullNames.JUNGLE_STAIRS, BlockID.JUNGLE_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy JUNGLE_STANDING_SIGN = REGISTRY.registerBlock(BlockFullNames.JUNGLE_STANDING_SIGN, BlockID.JUNGLE_STANDING_SIGN)
            .addState(BlockStates.GROUND_SIGN_DIRECTION);
    public static final BlockLegacy JUNGLE_TRAPDOOR = REGISTRY.registerBlock(BlockFullNames.JUNGLE_TRAPDOOR, BlockID.JUNGLE_TRAPDOOR)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT)
            .addState(BlockStates.OPEN_BIT);
    public static final BlockLegacy JUNGLE_WALL_SIGN = REGISTRY.registerBlock(BlockFullNames.JUNGLE_WALL_SIGN, BlockID.JUNGLE_WALL_SIGN)
            .addState(BlockStates.FACING_DIRECTION);
    public static final BlockLegacy JUNGLE_WOOD = REGISTRY.registerBlock(BlockFullNames.JUNGLE_WOOD, BlockID.JUNGLE_WOOD)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy KELP = REGISTRY.registerBlock(BlockFullNames.KELP, BlockID.KELP)
            .addState(BlockStates.KELP_AGE);
    public static final BlockLegacy LAB_TABLE = REGISTRY.registerBlock(BlockFullNames.LAB_TABLE, BlockID.LAB_TABLE)
            .addState(BlockStates.DIRECTION);
    public static final BlockLegacy LADDER = REGISTRY.registerBlock(BlockFullNames.LADDER, BlockID.LADDER)
            .addState(BlockStates.FACING_DIRECTION);
    public static final BlockLegacy LANTERN = REGISTRY.registerBlock(BlockFullNames.LANTERN, BlockID.LANTERN)
            .addState(BlockStates.HANGING);
    public static final BlockLegacy LAPIS_BLOCK = REGISTRY.registerBlock(BlockFullNames.LAPIS_BLOCK, BlockID.LAPIS_BLOCK);
    public static final BlockLegacy LAPIS_ORE = REGISTRY.registerBlock(BlockFullNames.LAPIS_ORE, BlockID.LAPIS_ORE);
    public static final BlockLegacy LARGE_AMETHYST_BUD = REGISTRY.registerBlock(BlockFullNames.LARGE_AMETHYST_BUD, BlockID.LARGE_AMETHYST_BUD)
            .addState(BlockStates.MINECRAFT_BLOCK_FACE);
    public static final BlockLegacy LARGE_FERN = REGISTRY.registerBlock(BlockFullNames.LARGE_FERN, BlockID.LARGE_FERN)
            .addState(BlockStates.UPPER_BLOCK_BIT);
    public static final BlockLegacy LAVA = REGISTRY.registerBlock(BlockFullNames.LAVA, BlockID.LAVA)
            .addState(BlockStates.LIQUID_DEPTH);
    public static final BlockLegacy LECTERN = REGISTRY.registerBlock(BlockFullNames.LECTERN, BlockID.LECTERN)
            .addState(BlockStates.MINECRAFT_CARDINAL_DIRECTION)
            .addState(BlockStates.POWERED_BIT);
    public static final BlockLegacy LEVER = REGISTRY.registerBlock(BlockFullNames.LEVER, BlockID.LEVER)
            .addState(BlockStates.LEVER_DIRECTION)
            .addState(BlockStates.OPEN_BIT);
    public static final BlockLegacy LIGHT_BLOCK_0 = REGISTRY.registerBlock(BlockFullNames.LIGHT_BLOCK_0, BlockID.LIGHT_BLOCK_0);
    public static final BlockLegacy LIGHT_BLOCK_1 = REGISTRY.registerBlock(BlockFullNames.LIGHT_BLOCK_1, BlockID.LIGHT_BLOCK_1);
    public static final BlockLegacy LIGHT_BLOCK_10 = REGISTRY.registerBlock(BlockFullNames.LIGHT_BLOCK_10, BlockID.LIGHT_BLOCK_10);
    public static final BlockLegacy LIGHT_BLOCK_11 = REGISTRY.registerBlock(BlockFullNames.LIGHT_BLOCK_11, BlockID.LIGHT_BLOCK_11);
    public static final BlockLegacy LIGHT_BLOCK_12 = REGISTRY.registerBlock(BlockFullNames.LIGHT_BLOCK_12, BlockID.LIGHT_BLOCK_12);
    public static final BlockLegacy LIGHT_BLOCK_13 = REGISTRY.registerBlock(BlockFullNames.LIGHT_BLOCK_13, BlockID.LIGHT_BLOCK_13);
    public static final BlockLegacy LIGHT_BLOCK_14 = REGISTRY.registerBlock(BlockFullNames.LIGHT_BLOCK_14, BlockID.LIGHT_BLOCK_14);
    public static final BlockLegacy LIGHT_BLOCK_15 = REGISTRY.registerBlock(BlockFullNames.LIGHT_BLOCK_15, BlockID.LIGHT_BLOCK_15);
    public static final BlockLegacy LIGHT_BLOCK_2 = REGISTRY.registerBlock(BlockFullNames.LIGHT_BLOCK_2, BlockID.LIGHT_BLOCK_2);
    public static final BlockLegacy LIGHT_BLOCK_3 = REGISTRY.registerBlock(BlockFullNames.LIGHT_BLOCK_3, BlockID.LIGHT_BLOCK_3);
    public static final BlockLegacy LIGHT_BLOCK_4 = REGISTRY.registerBlock(BlockFullNames.LIGHT_BLOCK_4, BlockID.LIGHT_BLOCK_4);
    public static final BlockLegacy LIGHT_BLOCK_5 = REGISTRY.registerBlock(BlockFullNames.LIGHT_BLOCK_5, BlockID.LIGHT_BLOCK_5);
    public static final BlockLegacy LIGHT_BLOCK_6 = REGISTRY.registerBlock(BlockFullNames.LIGHT_BLOCK_6, BlockID.LIGHT_BLOCK_6);
    public static final BlockLegacy LIGHT_BLOCK_7 = REGISTRY.registerBlock(BlockFullNames.LIGHT_BLOCK_7, BlockID.LIGHT_BLOCK_7);
    public static final BlockLegacy LIGHT_BLOCK_8 = REGISTRY.registerBlock(BlockFullNames.LIGHT_BLOCK_8, BlockID.LIGHT_BLOCK_8);
    public static final BlockLegacy LIGHT_BLOCK_9 = REGISTRY.registerBlock(BlockFullNames.LIGHT_BLOCK_9, BlockID.LIGHT_BLOCK_9);
    public static final BlockLegacy LIGHT_BLUE_CANDLE = REGISTRY.registerBlock(BlockFullNames.LIGHT_BLUE_CANDLE, BlockID.LIGHT_BLUE_CANDLE)
            .addState(BlockStates.CANDLES)
            .addState(BlockStates.LIT);
    public static final BlockLegacy LIGHT_BLUE_CANDLE_CAKE = REGISTRY.registerBlock(BlockFullNames.LIGHT_BLUE_CANDLE_CAKE, BlockID.LIGHT_BLUE_CANDLE_CAKE)
            .addState(BlockStates.LIT);
    public static final BlockLegacy LIGHT_BLUE_CARPET = REGISTRY.registerBlock(BlockFullNames.LIGHT_BLUE_CARPET, BlockID.LIGHT_BLUE_CARPET);
    public static final BlockLegacy LIGHT_BLUE_CONCRETE = REGISTRY.registerBlock(BlockFullNames.LIGHT_BLUE_CONCRETE, BlockID.LIGHT_BLUE_CONCRETE);
    public static final BlockLegacy LIGHT_BLUE_CONCRETE_POWDER = REGISTRY.registerBlock(BlockFullNames.LIGHT_BLUE_CONCRETE_POWDER, BlockID.LIGHT_BLUE_CONCRETE_POWDER);
    public static final BlockLegacy LIGHT_BLUE_GLAZED_TERRACOTTA = REGISTRY.registerBlock(BlockFullNames.LIGHT_BLUE_GLAZED_TERRACOTTA, BlockID.LIGHT_BLUE_GLAZED_TERRACOTTA)
            .addState(BlockStates.FACING_DIRECTION);
    public static final BlockLegacy LIGHT_BLUE_SHULKER_BOX = REGISTRY.registerBlock(BlockFullNames.LIGHT_BLUE_SHULKER_BOX, BlockID.LIGHT_BLUE_SHULKER_BOX);
    public static final BlockLegacy LIGHT_BLUE_STAINED_GLASS = REGISTRY.registerBlock(BlockFullNames.LIGHT_BLUE_STAINED_GLASS, BlockID.LIGHT_BLUE_STAINED_GLASS);
    public static final BlockLegacy LIGHT_BLUE_STAINED_GLASS_PANE = REGISTRY.registerBlock(BlockFullNames.LIGHT_BLUE_STAINED_GLASS_PANE, BlockID.LIGHT_BLUE_STAINED_GLASS_PANE);
    public static final BlockLegacy LIGHT_BLUE_TERRACOTTA = REGISTRY.registerBlock(BlockFullNames.LIGHT_BLUE_TERRACOTTA, BlockID.LIGHT_BLUE_TERRACOTTA);
    public static final BlockLegacy LIGHT_BLUE_WOOL = REGISTRY.registerBlock(BlockFullNames.LIGHT_BLUE_WOOL, BlockID.LIGHT_BLUE_WOOL);
    public static final BlockLegacy LIGHT_GRAY_CANDLE = REGISTRY.registerBlock(BlockFullNames.LIGHT_GRAY_CANDLE, BlockID.LIGHT_GRAY_CANDLE)
            .addState(BlockStates.CANDLES)
            .addState(BlockStates.LIT);
    public static final BlockLegacy LIGHT_GRAY_CANDLE_CAKE = REGISTRY.registerBlock(BlockFullNames.LIGHT_GRAY_CANDLE_CAKE, BlockID.LIGHT_GRAY_CANDLE_CAKE)
            .addState(BlockStates.LIT);
    public static final BlockLegacy LIGHT_GRAY_CARPET = REGISTRY.registerBlock(BlockFullNames.LIGHT_GRAY_CARPET, BlockID.LIGHT_GRAY_CARPET);
    public static final BlockLegacy LIGHT_GRAY_CONCRETE = REGISTRY.registerBlock(BlockFullNames.LIGHT_GRAY_CONCRETE, BlockID.LIGHT_GRAY_CONCRETE);
    public static final BlockLegacy LIGHT_GRAY_CONCRETE_POWDER = REGISTRY.registerBlock(BlockFullNames.LIGHT_GRAY_CONCRETE_POWDER, BlockID.LIGHT_GRAY_CONCRETE_POWDER);
    public static final BlockLegacy LIGHT_GRAY_SHULKER_BOX = REGISTRY.registerBlock(BlockFullNames.LIGHT_GRAY_SHULKER_BOX, BlockID.LIGHT_GRAY_SHULKER_BOX);
    public static final BlockLegacy LIGHT_GRAY_STAINED_GLASS = REGISTRY.registerBlock(BlockFullNames.LIGHT_GRAY_STAINED_GLASS, BlockID.LIGHT_GRAY_STAINED_GLASS);
    public static final BlockLegacy LIGHT_GRAY_STAINED_GLASS_PANE = REGISTRY.registerBlock(BlockFullNames.LIGHT_GRAY_STAINED_GLASS_PANE, BlockID.LIGHT_GRAY_STAINED_GLASS_PANE);
    public static final BlockLegacy LIGHT_GRAY_TERRACOTTA = REGISTRY.registerBlock(BlockFullNames.LIGHT_GRAY_TERRACOTTA, BlockID.LIGHT_GRAY_TERRACOTTA);
    public static final BlockLegacy LIGHT_GRAY_WOOL = REGISTRY.registerBlock(BlockFullNames.LIGHT_GRAY_WOOL, BlockID.LIGHT_GRAY_WOOL);
    public static final BlockLegacy LIGHT_WEIGHTED_PRESSURE_PLATE = REGISTRY.registerBlock(BlockFullNames.LIGHT_WEIGHTED_PRESSURE_PLATE, BlockID.LIGHT_WEIGHTED_PRESSURE_PLATE)
            .addState(BlockStates.REDSTONE_SIGNAL);
    public static final BlockLegacy LIGHTNING_ROD = REGISTRY.registerBlock(BlockFullNames.LIGHTNING_ROD, BlockID.LIGHTNING_ROD)
            .addState(BlockStates.FACING_DIRECTION);
    public static final BlockLegacy LILAC = REGISTRY.registerBlock(BlockFullNames.LILAC, BlockID.LILAC)
            .addState(BlockStates.UPPER_BLOCK_BIT);
    public static final BlockLegacy LILY_OF_THE_VALLEY = REGISTRY.registerBlock(BlockFullNames.LILY_OF_THE_VALLEY, BlockID.LILY_OF_THE_VALLEY);
    public static final BlockLegacy LIME_CANDLE = REGISTRY.registerBlock(BlockFullNames.LIME_CANDLE, BlockID.LIME_CANDLE)
            .addState(BlockStates.CANDLES)
            .addState(BlockStates.LIT);
    public static final BlockLegacy LIME_CANDLE_CAKE = REGISTRY.registerBlock(BlockFullNames.LIME_CANDLE_CAKE, BlockID.LIME_CANDLE_CAKE)
            .addState(BlockStates.LIT);
    public static final BlockLegacy LIME_CARPET = REGISTRY.registerBlock(BlockFullNames.LIME_CARPET, BlockID.LIME_CARPET);
    public static final BlockLegacy LIME_CONCRETE = REGISTRY.registerBlock(BlockFullNames.LIME_CONCRETE, BlockID.LIME_CONCRETE);
    public static final BlockLegacy LIME_CONCRETE_POWDER = REGISTRY.registerBlock(BlockFullNames.LIME_CONCRETE_POWDER, BlockID.LIME_CONCRETE_POWDER);
    public static final BlockLegacy LIME_GLAZED_TERRACOTTA = REGISTRY.registerBlock(BlockFullNames.LIME_GLAZED_TERRACOTTA, BlockID.LIME_GLAZED_TERRACOTTA)
            .addState(BlockStates.FACING_DIRECTION);
    public static final BlockLegacy LIME_SHULKER_BOX = REGISTRY.registerBlock(BlockFullNames.LIME_SHULKER_BOX, BlockID.LIME_SHULKER_BOX);
    public static final BlockLegacy LIME_STAINED_GLASS = REGISTRY.registerBlock(BlockFullNames.LIME_STAINED_GLASS, BlockID.LIME_STAINED_GLASS);
    public static final BlockLegacy LIME_STAINED_GLASS_PANE = REGISTRY.registerBlock(BlockFullNames.LIME_STAINED_GLASS_PANE, BlockID.LIME_STAINED_GLASS_PANE);
    public static final BlockLegacy LIME_TERRACOTTA = REGISTRY.registerBlock(BlockFullNames.LIME_TERRACOTTA, BlockID.LIME_TERRACOTTA);
    public static final BlockLegacy LIME_WOOL = REGISTRY.registerBlock(BlockFullNames.LIME_WOOL, BlockID.LIME_WOOL);
    public static final BlockLegacy LIT_BLAST_FURNACE = REGISTRY.registerBlock(BlockFullNames.LIT_BLAST_FURNACE, BlockID.LIT_BLAST_FURNACE)
            .addState(BlockStates.MINECRAFT_CARDINAL_DIRECTION);
    public static final BlockLegacy LIT_DEEPSLATE_REDSTONE_ORE = REGISTRY.registerBlock(BlockFullNames.LIT_DEEPSLATE_REDSTONE_ORE, BlockID.LIT_DEEPSLATE_REDSTONE_ORE);
    public static final BlockLegacy LIT_FURNACE = REGISTRY.registerBlock(BlockFullNames.LIT_FURNACE, BlockID.LIT_FURNACE)
            .addState(BlockStates.MINECRAFT_CARDINAL_DIRECTION);
    public static final BlockLegacy LIT_PUMPKIN = REGISTRY.registerBlock(BlockFullNames.LIT_PUMPKIN, BlockID.LIT_PUMPKIN)
            .addState(BlockStates.MINECRAFT_CARDINAL_DIRECTION);
    public static final BlockLegacy LIT_REDSTONE_LAMP = REGISTRY.registerBlock(BlockFullNames.LIT_REDSTONE_LAMP, BlockID.LIT_REDSTONE_LAMP);
    public static final BlockLegacy LIT_REDSTONE_ORE = REGISTRY.registerBlock(BlockFullNames.LIT_REDSTONE_ORE, BlockID.LIT_REDSTONE_ORE);
    public static final BlockLegacy LIT_SMOKER = REGISTRY.registerBlock(BlockFullNames.LIT_SMOKER, BlockID.LIT_SMOKER)
            .addState(BlockStates.MINECRAFT_CARDINAL_DIRECTION);
    public static final BlockLegacy LODESTONE = REGISTRY.registerBlock(BlockFullNames.LODESTONE, BlockID.LODESTONE);
    public static final BlockLegacy LOOM = REGISTRY.registerBlock(BlockFullNames.LOOM, BlockID.LOOM)
            .addState(BlockStates.DIRECTION);
    public static final BlockLegacy MAGENTA_CANDLE = REGISTRY.registerBlock(BlockFullNames.MAGENTA_CANDLE, BlockID.MAGENTA_CANDLE)
            .addState(BlockStates.CANDLES)
            .addState(BlockStates.LIT);
    public static final BlockLegacy MAGENTA_CANDLE_CAKE = REGISTRY.registerBlock(BlockFullNames.MAGENTA_CANDLE_CAKE, BlockID.MAGENTA_CANDLE_CAKE)
            .addState(BlockStates.LIT);
    public static final BlockLegacy MAGENTA_CARPET = REGISTRY.registerBlock(BlockFullNames.MAGENTA_CARPET, BlockID.MAGENTA_CARPET);
    public static final BlockLegacy MAGENTA_CONCRETE = REGISTRY.registerBlock(BlockFullNames.MAGENTA_CONCRETE, BlockID.MAGENTA_CONCRETE);
    public static final BlockLegacy MAGENTA_CONCRETE_POWDER = REGISTRY.registerBlock(BlockFullNames.MAGENTA_CONCRETE_POWDER, BlockID.MAGENTA_CONCRETE_POWDER);
    public static final BlockLegacy MAGENTA_GLAZED_TERRACOTTA = REGISTRY.registerBlock(BlockFullNames.MAGENTA_GLAZED_TERRACOTTA, BlockID.MAGENTA_GLAZED_TERRACOTTA)
            .addState(BlockStates.FACING_DIRECTION);
    public static final BlockLegacy MAGENTA_SHULKER_BOX = REGISTRY.registerBlock(BlockFullNames.MAGENTA_SHULKER_BOX, BlockID.MAGENTA_SHULKER_BOX);
    public static final BlockLegacy MAGENTA_STAINED_GLASS = REGISTRY.registerBlock(BlockFullNames.MAGENTA_STAINED_GLASS, BlockID.MAGENTA_STAINED_GLASS);
    public static final BlockLegacy MAGENTA_STAINED_GLASS_PANE = REGISTRY.registerBlock(BlockFullNames.MAGENTA_STAINED_GLASS_PANE, BlockID.MAGENTA_STAINED_GLASS_PANE);
    public static final BlockLegacy MAGENTA_TERRACOTTA = REGISTRY.registerBlock(BlockFullNames.MAGENTA_TERRACOTTA, BlockID.MAGENTA_TERRACOTTA);
    public static final BlockLegacy MAGENTA_WOOL = REGISTRY.registerBlock(BlockFullNames.MAGENTA_WOOL, BlockID.MAGENTA_WOOL);
    public static final BlockLegacy MAGMA = REGISTRY.registerBlock(BlockFullNames.MAGMA, BlockID.MAGMA);
    public static final BlockLegacy MANGROVE_BUTTON = REGISTRY.registerBlock(BlockFullNames.MANGROVE_BUTTON, BlockID.MANGROVE_BUTTON)
            .addState(BlockStates.FACING_DIRECTION)
            .addState(BlockStates.BUTTON_PRESSED_BIT);
    public static final BlockLegacy MANGROVE_DOOR = REGISTRY.registerBlock(BlockFullNames.MANGROVE_DOOR, BlockID.MANGROVE_DOOR)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.OPEN_BIT)
            .addState(BlockStates.UPPER_BLOCK_BIT)
            .addState(BlockStates.DOOR_HINGE_BIT);
    public static final BlockLegacy MANGROVE_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.MANGROVE_DOUBLE_SLAB, BlockID.MANGROVE_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy MANGROVE_FENCE = REGISTRY.registerBlock(BlockFullNames.MANGROVE_FENCE, BlockID.MANGROVE_FENCE);
    public static final BlockLegacy MANGROVE_FENCE_GATE = REGISTRY.registerBlock(BlockFullNames.MANGROVE_FENCE_GATE, BlockID.MANGROVE_FENCE_GATE)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.OPEN_BIT)
            .addState(BlockStates.IN_WALL_BIT);
    public static final BlockLegacy MANGROVE_HANGING_SIGN = REGISTRY.registerBlock(BlockFullNames.MANGROVE_HANGING_SIGN, BlockID.MANGROVE_HANGING_SIGN)
            .addState(BlockStates.FACING_DIRECTION)
            .addState(BlockStates.GROUND_SIGN_DIRECTION)
            .addState(BlockStates.HANGING)
            .addState(BlockStates.ATTACHED_BIT);
    public static final BlockLegacy MANGROVE_LEAVES = REGISTRY.registerBlock(BlockFullNames.MANGROVE_LEAVES, BlockID.MANGROVE_LEAVES)
            .addState(BlockStates.UPDATE_BIT)
            .addState(BlockStates.PERSISTENT_BIT);
    public static final BlockLegacy MANGROVE_LOG = REGISTRY.registerBlock(BlockFullNames.MANGROVE_LOG, BlockID.MANGROVE_LOG)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy MANGROVE_PLANKS = REGISTRY.registerBlock(BlockFullNames.MANGROVE_PLANKS, BlockID.MANGROVE_PLANKS);
    public static final BlockLegacy MANGROVE_PRESSURE_PLATE = REGISTRY.registerBlock(BlockFullNames.MANGROVE_PRESSURE_PLATE, BlockID.MANGROVE_PRESSURE_PLATE)
            .addState(BlockStates.REDSTONE_SIGNAL);
    public static final BlockLegacy MANGROVE_PROPAGULE = REGISTRY.registerBlock(BlockFullNames.MANGROVE_PROPAGULE, BlockID.MANGROVE_PROPAGULE)
            .addState(BlockStates.PROPAGULE_STAGE)
            .addState(BlockStates.HANGING);
    public static final BlockLegacy MANGROVE_ROOTS = REGISTRY.registerBlock(BlockFullNames.MANGROVE_ROOTS, BlockID.MANGROVE_ROOTS);
    public static final BlockLegacy MANGROVE_SLAB = REGISTRY.registerBlock(BlockFullNames.MANGROVE_SLAB, BlockID.MANGROVE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy MANGROVE_STAIRS = REGISTRY.registerBlock(BlockFullNames.MANGROVE_STAIRS, BlockID.MANGROVE_STAIRS)
            .addState(BlockStates.UPSIDE_DOWN_BIT)
            .addState(BlockStates.WEIRDO_DIRECTION);
    public static final BlockLegacy MANGROVE_STANDING_SIGN = REGISTRY.registerBlock(BlockFullNames.MANGROVE_STANDING_SIGN, BlockID.MANGROVE_STANDING_SIGN)
            .addState(BlockStates.GROUND_SIGN_DIRECTION);
    public static final BlockLegacy MANGROVE_TRAPDOOR = REGISTRY.registerBlock(BlockFullNames.MANGROVE_TRAPDOOR, BlockID.MANGROVE_TRAPDOOR)
            .addState(BlockStates.UPSIDE_DOWN_BIT)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.OPEN_BIT);
    public static final BlockLegacy MANGROVE_WALL_SIGN = REGISTRY.registerBlock(BlockFullNames.MANGROVE_WALL_SIGN, BlockID.MANGROVE_WALL_SIGN)
            .addState(BlockStates.FACING_DIRECTION);
    public static final BlockLegacy MANGROVE_WOOD = REGISTRY.registerBlock(BlockFullNames.MANGROVE_WOOD, BlockID.MANGROVE_WOOD)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy MATERIAL_REDUCER = REGISTRY.registerBlock(BlockFullNames.MATERIAL_REDUCER, BlockID.MATERIAL_REDUCER)
            .addState(BlockStates.DIRECTION);
    public static final BlockLegacy MEDIUM_AMETHYST_BUD = REGISTRY.registerBlock(BlockFullNames.MEDIUM_AMETHYST_BUD, BlockID.MEDIUM_AMETHYST_BUD)
            .addState(BlockStates.MINECRAFT_BLOCK_FACE);
    public static final BlockLegacy MELON_BLOCK = REGISTRY.registerBlock(BlockFullNames.MELON_BLOCK, BlockID.MELON_BLOCK);
    public static final BlockLegacy MELON_STEM = REGISTRY.registerBlock(BlockFullNames.MELON_STEM, BlockID.MELON_STEM)
            .addState(BlockStates.GROWTH)
            .addState(BlockStates.FACING_DIRECTION);
    public static final BlockLegacy MOB_SPAWNER = REGISTRY.registerBlock(BlockFullNames.MOB_SPAWNER, BlockID.MOB_SPAWNER);
    public static final BlockLegacy MOSS_BLOCK = REGISTRY.registerBlock(BlockFullNames.MOSS_BLOCK, BlockID.MOSS_BLOCK);
    public static final BlockLegacy MOSS_CARPET = REGISTRY.registerBlock(BlockFullNames.MOSS_CARPET, BlockID.MOSS_CARPET);
    public static final BlockLegacy MOSSY_COBBLESTONE = REGISTRY.registerBlock(BlockFullNames.MOSSY_COBBLESTONE, BlockID.MOSSY_COBBLESTONE);
    public static final BlockLegacy MOSSY_COBBLESTONE_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.MOSSY_COBBLESTONE_DOUBLE_SLAB, BlockID.MOSSY_COBBLESTONE_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy MOSSY_COBBLESTONE_SLAB = REGISTRY.registerBlock(BlockFullNames.MOSSY_COBBLESTONE_SLAB, BlockID.MOSSY_COBBLESTONE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy MOSSY_COBBLESTONE_STAIRS = REGISTRY.registerBlock(BlockFullNames.MOSSY_COBBLESTONE_STAIRS, BlockID.MOSSY_COBBLESTONE_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy MOSSY_COBBLESTONE_WALL = REGISTRY.registerBlock(BlockFullNames.MOSSY_COBBLESTONE_WALL, BlockID.MOSSY_COBBLESTONE_WALL)
            .addState(BlockStates.WALL_POST_BIT)
            .addState(BlockStates.WALL_CONNECTION_TYPE_NORTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_EAST)
            .addState(BlockStates.WALL_CONNECTION_TYPE_SOUTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_WEST);
    public static final BlockLegacy MOSSY_STONE_BRICK_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.MOSSY_STONE_BRICK_DOUBLE_SLAB, BlockID.MOSSY_STONE_BRICK_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy MOSSY_STONE_BRICK_SLAB = REGISTRY.registerBlock(BlockFullNames.MOSSY_STONE_BRICK_SLAB, BlockID.MOSSY_STONE_BRICK_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy MOSSY_STONE_BRICK_STAIRS = REGISTRY.registerBlock(BlockFullNames.MOSSY_STONE_BRICK_STAIRS, BlockID.MOSSY_STONE_BRICK_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy MOSSY_STONE_BRICK_WALL = REGISTRY.registerBlock(BlockFullNames.MOSSY_STONE_BRICK_WALL, BlockID.MOSSY_STONE_BRICK_WALL)
            .addState(BlockStates.WALL_POST_BIT)
            .addState(BlockStates.WALL_CONNECTION_TYPE_NORTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_EAST)
            .addState(BlockStates.WALL_CONNECTION_TYPE_SOUTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_WEST);
    public static final BlockLegacy MOSSY_STONE_BRICKS = REGISTRY.registerBlock(BlockFullNames.MOSSY_STONE_BRICKS, BlockID.MOSSY_STONE_BRICKS);
    public static final BlockLegacy MOVING_BLOCK = REGISTRY.registerBlock(BlockFullNames.MOVING_BLOCK, BlockID.MOVING_BLOCK);
    public static final BlockLegacy MUD = REGISTRY.registerBlock(BlockFullNames.MUD, BlockID.MUD);
    public static final BlockLegacy MUD_BRICK_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.MUD_BRICK_DOUBLE_SLAB, BlockID.MUD_BRICK_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy MUD_BRICK_SLAB = REGISTRY.registerBlock(BlockFullNames.MUD_BRICK_SLAB, BlockID.MUD_BRICK_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy MUD_BRICK_STAIRS = REGISTRY.registerBlock(BlockFullNames.MUD_BRICK_STAIRS, BlockID.MUD_BRICK_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy MUD_BRICK_WALL = REGISTRY.registerBlock(BlockFullNames.MUD_BRICK_WALL, BlockID.MUD_BRICK_WALL)
            .addState(BlockStates.WALL_POST_BIT)
            .addState(BlockStates.WALL_CONNECTION_TYPE_NORTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_EAST)
            .addState(BlockStates.WALL_CONNECTION_TYPE_SOUTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_WEST);
    public static final BlockLegacy MUD_BRICKS = REGISTRY.registerBlock(BlockFullNames.MUD_BRICKS, BlockID.MUD_BRICKS);
    public static final BlockLegacy MUDDY_MANGROVE_ROOTS = REGISTRY.registerBlock(BlockFullNames.MUDDY_MANGROVE_ROOTS, BlockID.MUDDY_MANGROVE_ROOTS)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy MUSHROOM_STEM = REGISTRY.registerBlock(BlockFullNames.MUSHROOM_STEM, BlockID.MUSHROOM_STEM)
            .addState(BlockStates.HUGE_MUSHROOM_BITS);
    public static final BlockLegacy MYCELIUM = REGISTRY.registerBlock(BlockFullNames.MYCELIUM, BlockID.MYCELIUM);
    public static final BlockLegacy NETHER_BRICK = REGISTRY.registerBlock(BlockFullNames.NETHER_BRICK, BlockID.NETHER_BRICK);
    public static final BlockLegacy NETHER_BRICK_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.NETHER_BRICK_DOUBLE_SLAB, BlockID.NETHER_BRICK_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy NETHER_BRICK_FENCE = REGISTRY.registerBlock(BlockFullNames.NETHER_BRICK_FENCE, BlockID.NETHER_BRICK_FENCE);
    public static final BlockLegacy NETHER_BRICK_SLAB = REGISTRY.registerBlock(BlockFullNames.NETHER_BRICK_SLAB, BlockID.NETHER_BRICK_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy NETHER_BRICK_STAIRS = REGISTRY.registerBlock(BlockFullNames.NETHER_BRICK_STAIRS, BlockID.NETHER_BRICK_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy NETHER_BRICK_WALL = REGISTRY.registerBlock(BlockFullNames.NETHER_BRICK_WALL, BlockID.NETHER_BRICK_WALL)
            .addState(BlockStates.WALL_POST_BIT)
            .addState(BlockStates.WALL_CONNECTION_TYPE_NORTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_EAST)
            .addState(BlockStates.WALL_CONNECTION_TYPE_SOUTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_WEST);
    public static final BlockLegacy NETHER_GOLD_ORE = REGISTRY.registerBlock(BlockFullNames.NETHER_GOLD_ORE, BlockID.NETHER_GOLD_ORE);
    public static final BlockLegacy NETHER_SPROUTS = REGISTRY.registerBlock(BlockFullNames.NETHER_SPROUTS, BlockID.NETHER_SPROUTS);
    public static final BlockLegacy NETHER_WART = REGISTRY.registerBlock(BlockFullNames.NETHER_WART, BlockID.NETHER_WART)
            .addState(BlockStates.AGE, BlockStateIntegerValues.NETHER_WART_MAX_AGE + 1);
    public static final BlockLegacy NETHER_WART_BLOCK = REGISTRY.registerBlock(BlockFullNames.NETHER_WART_BLOCK, BlockID.NETHER_WART_BLOCK);
    public static final BlockLegacy NETHERITE_BLOCK = REGISTRY.registerBlock(BlockFullNames.NETHERITE_BLOCK, BlockID.NETHERITE_BLOCK);
    public static final BlockLegacy NETHERRACK = REGISTRY.registerBlock(BlockFullNames.NETHERRACK, BlockID.NETHERRACK);
    public static final BlockLegacy NETHERREACTOR = REGISTRY.registerBlock(BlockFullNames.NETHERREACTOR, BlockID.NETHERREACTOR);
    public static final BlockLegacy NORMAL_STONE_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.NORMAL_STONE_DOUBLE_SLAB, BlockID.NORMAL_STONE_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy NORMAL_STONE_SLAB = REGISTRY.registerBlock(BlockFullNames.NORMAL_STONE_SLAB, BlockID.NORMAL_STONE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy NORMAL_STONE_STAIRS = REGISTRY.registerBlock(BlockFullNames.NORMAL_STONE_STAIRS, BlockID.NORMAL_STONE_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy NOTEBLOCK = REGISTRY.registerBlock(BlockFullNames.NOTEBLOCK, BlockID.NOTEBLOCK);
    public static final BlockLegacy OAK_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.OAK_DOUBLE_SLAB, BlockID.OAK_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy OAK_FENCE = REGISTRY.registerBlock(BlockFullNames.OAK_FENCE, BlockID.OAK_FENCE);
    public static final BlockLegacy OAK_HANGING_SIGN = REGISTRY.registerBlock(BlockFullNames.OAK_HANGING_SIGN, BlockID.OAK_HANGING_SIGN)
            .addState(BlockStates.FACING_DIRECTION)
            .addState(BlockStates.GROUND_SIGN_DIRECTION)
            .addState(BlockStates.HANGING)
            .addState(BlockStates.ATTACHED_BIT);
    public static final BlockLegacy OAK_LEAVES = REGISTRY.registerBlock(BlockFullNames.OAK_LEAVES, BlockID.OAK_LEAVES)
            .addState(BlockStates.UPDATE_BIT)
            .addState(BlockStates.PERSISTENT_BIT);
    public static final BlockLegacy OAK_LOG = REGISTRY.registerBlock(BlockFullNames.OAK_LOG, BlockID.OAK_LOG)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy OAK_PLANKS = REGISTRY.registerBlock(BlockFullNames.OAK_PLANKS, BlockID.OAK_PLANKS);
    public static final BlockLegacy OAK_SAPLING = REGISTRY.registerBlock(BlockFullNames.OAK_SAPLING, BlockID.OAK_SAPLING)
            .addState(BlockStates.AGE_BIT);
    public static final BlockLegacy OAK_SLAB = REGISTRY.registerBlock(BlockFullNames.OAK_SLAB, BlockID.OAK_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy OAK_STAIRS = REGISTRY.registerBlock(BlockFullNames.OAK_STAIRS, BlockID.OAK_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy OAK_WOOD = REGISTRY.registerBlock(BlockFullNames.OAK_WOOD, BlockID.OAK_WOOD)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy OBSERVER = REGISTRY.registerBlock(BlockFullNames.OBSERVER, BlockID.OBSERVER)
            .addState(BlockStates.MINECRAFT_FACING_DIRECTION)
            .addState(BlockStates.POWERED_BIT);
    public static final BlockLegacy OBSIDIAN = REGISTRY.registerBlock(BlockFullNames.OBSIDIAN, BlockID.OBSIDIAN);
    public static final BlockLegacy OCHRE_FROGLIGHT = REGISTRY.registerBlock(BlockFullNames.OCHRE_FROGLIGHT, BlockID.OCHRE_FROGLIGHT)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy OPEN_EYEBLOSSOM = REGISTRY.registerBlock(BlockFullNames.OPEN_EYEBLOSSOM, BlockID.OPEN_EYEBLOSSOM);
    public static final BlockLegacy ORANGE_CANDLE = REGISTRY.registerBlock(BlockFullNames.ORANGE_CANDLE, BlockID.ORANGE_CANDLE)
            .addState(BlockStates.CANDLES)
            .addState(BlockStates.LIT);
    public static final BlockLegacy ORANGE_CANDLE_CAKE = REGISTRY.registerBlock(BlockFullNames.ORANGE_CANDLE_CAKE, BlockID.ORANGE_CANDLE_CAKE)
            .addState(BlockStates.LIT);
    public static final BlockLegacy ORANGE_CARPET = REGISTRY.registerBlock(BlockFullNames.ORANGE_CARPET, BlockID.ORANGE_CARPET);
    public static final BlockLegacy ORANGE_CONCRETE = REGISTRY.registerBlock(BlockFullNames.ORANGE_CONCRETE, BlockID.ORANGE_CONCRETE);
    public static final BlockLegacy ORANGE_CONCRETE_POWDER = REGISTRY.registerBlock(BlockFullNames.ORANGE_CONCRETE_POWDER, BlockID.ORANGE_CONCRETE_POWDER);
    public static final BlockLegacy ORANGE_GLAZED_TERRACOTTA = REGISTRY.registerBlock(BlockFullNames.ORANGE_GLAZED_TERRACOTTA, BlockID.ORANGE_GLAZED_TERRACOTTA)
            .addState(BlockStates.FACING_DIRECTION);
    public static final BlockLegacy ORANGE_SHULKER_BOX = REGISTRY.registerBlock(BlockFullNames.ORANGE_SHULKER_BOX, BlockID.ORANGE_SHULKER_BOX);
    public static final BlockLegacy ORANGE_STAINED_GLASS = REGISTRY.registerBlock(BlockFullNames.ORANGE_STAINED_GLASS, BlockID.ORANGE_STAINED_GLASS);
    public static final BlockLegacy ORANGE_STAINED_GLASS_PANE = REGISTRY.registerBlock(BlockFullNames.ORANGE_STAINED_GLASS_PANE, BlockID.ORANGE_STAINED_GLASS_PANE);
    public static final BlockLegacy ORANGE_TERRACOTTA = REGISTRY.registerBlock(BlockFullNames.ORANGE_TERRACOTTA, BlockID.ORANGE_TERRACOTTA);
    public static final BlockLegacy ORANGE_TULIP = REGISTRY.registerBlock(BlockFullNames.ORANGE_TULIP, BlockID.ORANGE_TULIP);
    public static final BlockLegacy ORANGE_WOOL = REGISTRY.registerBlock(BlockFullNames.ORANGE_WOOL, BlockID.ORANGE_WOOL);
    public static final BlockLegacy OXEYE_DAISY = REGISTRY.registerBlock(BlockFullNames.OXEYE_DAISY, BlockID.OXEYE_DAISY);
    public static final BlockLegacy OXIDIZED_CHISELED_COPPER = REGISTRY.registerBlock(BlockFullNames.OXIDIZED_CHISELED_COPPER, BlockID.OXIDIZED_CHISELED_COPPER);
    public static final BlockLegacy OXIDIZED_COPPER = REGISTRY.registerBlock(BlockFullNames.OXIDIZED_COPPER, BlockID.OXIDIZED_COPPER);
    public static final BlockLegacy OXIDIZED_COPPER_BULB = REGISTRY.registerBlock(BlockFullNames.OXIDIZED_COPPER_BULB, BlockID.OXIDIZED_COPPER_BULB)
            .addState(BlockStates.LIT)
            .addState(BlockStates.POWERED_BIT);
    public static final BlockLegacy OXIDIZED_COPPER_DOOR = REGISTRY.registerBlock(BlockFullNames.OXIDIZED_COPPER_DOOR, BlockID.OXIDIZED_COPPER_DOOR)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.OPEN_BIT)
            .addState(BlockStates.UPPER_BLOCK_BIT)
            .addState(BlockStates.DOOR_HINGE_BIT);
    public static final BlockLegacy OXIDIZED_COPPER_GRATE = REGISTRY.registerBlock(BlockFullNames.OXIDIZED_COPPER_GRATE, BlockID.OXIDIZED_COPPER_GRATE);
    public static final BlockLegacy OXIDIZED_COPPER_TRAPDOOR = REGISTRY.registerBlock(BlockFullNames.OXIDIZED_COPPER_TRAPDOOR, BlockID.OXIDIZED_COPPER_TRAPDOOR)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT)
            .addState(BlockStates.OPEN_BIT);
    public static final BlockLegacy OXIDIZED_CUT_COPPER = REGISTRY.registerBlock(BlockFullNames.OXIDIZED_CUT_COPPER, BlockID.OXIDIZED_CUT_COPPER);
    public static final BlockLegacy OXIDIZED_CUT_COPPER_SLAB = REGISTRY.registerBlock(BlockFullNames.OXIDIZED_CUT_COPPER_SLAB, BlockID.OXIDIZED_CUT_COPPER_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy OXIDIZED_CUT_COPPER_STAIRS = REGISTRY.registerBlock(BlockFullNames.OXIDIZED_CUT_COPPER_STAIRS, BlockID.OXIDIZED_CUT_COPPER_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy OXIDIZED_DOUBLE_CUT_COPPER_SLAB = REGISTRY.registerBlock(BlockFullNames.OXIDIZED_DOUBLE_CUT_COPPER_SLAB, BlockID.OXIDIZED_DOUBLE_CUT_COPPER_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy PACKED_ICE = REGISTRY.registerBlock(BlockFullNames.PACKED_ICE, BlockID.PACKED_ICE);
    public static final BlockLegacy PACKED_MUD = REGISTRY.registerBlock(BlockFullNames.PACKED_MUD, BlockID.PACKED_MUD);
    public static final BlockLegacy PALE_HANGING_MOSS = REGISTRY.registerBlock(BlockFullNames.PALE_HANGING_MOSS, BlockID.PALE_HANGING_MOSS)
            .addState(BlockStates.TIP);
    public static final BlockLegacy PALE_MOSS_BLOCK = REGISTRY.registerBlock(BlockFullNames.PALE_MOSS_BLOCK, BlockID.PALE_MOSS_BLOCK);
    public static final BlockLegacy PALE_MOSS_CARPET = REGISTRY.registerBlock(BlockFullNames.PALE_MOSS_CARPET, BlockID.PALE_MOSS_CARPET)
            .addState(BlockStates.UPPER_BLOCK_BIT)
            .addState(BlockStates.PALE_MOSS_CARPET_SIDE_NORTH)
            .addState(BlockStates.PALE_MOSS_CARPET_SIDE_EAST)
            .addState(BlockStates.PALE_MOSS_CARPET_SIDE_SOUTH)
            .addState(BlockStates.PALE_MOSS_CARPET_SIDE_WEST);
    public static final BlockLegacy PALE_OAK_BUTTON = REGISTRY.registerBlock(BlockFullNames.PALE_OAK_BUTTON, BlockID.PALE_OAK_BUTTON)
            .addState(BlockStates.FACING_DIRECTION)
            .addState(BlockStates.BUTTON_PRESSED_BIT);
    public static final BlockLegacy PALE_OAK_DOOR = REGISTRY.registerBlock(BlockFullNames.PALE_OAK_DOOR, BlockID.PALE_OAK_DOOR)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.OPEN_BIT)
            .addState(BlockStates.UPPER_BLOCK_BIT)
            .addState(BlockStates.DOOR_HINGE_BIT);
    public static final BlockLegacy PALE_OAK_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.PALE_OAK_DOUBLE_SLAB, BlockID.PALE_OAK_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy PALE_OAK_FENCE = REGISTRY.registerBlock(BlockFullNames.PALE_OAK_FENCE, BlockID.PALE_OAK_FENCE);
    public static final BlockLegacy PALE_OAK_FENCE_GATE = REGISTRY.registerBlock(BlockFullNames.PALE_OAK_FENCE_GATE, BlockID.PALE_OAK_FENCE_GATE)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.OPEN_BIT)
            .addState(BlockStates.IN_WALL_BIT);
    public static final BlockLegacy PALE_OAK_HANGING_SIGN = REGISTRY.registerBlock(BlockFullNames.PALE_OAK_HANGING_SIGN, BlockID.PALE_OAK_HANGING_SIGN)
            .addState(BlockStates.FACING_DIRECTION)
            .addState(BlockStates.GROUND_SIGN_DIRECTION)
            .addState(BlockStates.HANGING)
            .addState(BlockStates.ATTACHED_BIT);
    public static final BlockLegacy PALE_OAK_LEAVES = REGISTRY.registerBlock(BlockFullNames.PALE_OAK_LEAVES, BlockID.PALE_OAK_LEAVES)
            .addState(BlockStates.UPDATE_BIT)
            .addState(BlockStates.PERSISTENT_BIT);
    public static final BlockLegacy PALE_OAK_LOG = REGISTRY.registerBlock(BlockFullNames.PALE_OAK_LOG, BlockID.PALE_OAK_LOG)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy PALE_OAK_PLANKS = REGISTRY.registerBlock(BlockFullNames.PALE_OAK_PLANKS, BlockID.PALE_OAK_PLANKS);
    public static final BlockLegacy PALE_OAK_PRESSURE_PLATE = REGISTRY.registerBlock(BlockFullNames.PALE_OAK_PRESSURE_PLATE, BlockID.PALE_OAK_PRESSURE_PLATE)
            .addState(BlockStates.REDSTONE_SIGNAL);
    public static final BlockLegacy PALE_OAK_SAPLING = REGISTRY.registerBlock(BlockFullNames.PALE_OAK_SAPLING, BlockID.PALE_OAK_SAPLING)
            .addState(BlockStates.AGE_BIT);
    public static final BlockLegacy PALE_OAK_SLAB = REGISTRY.registerBlock(BlockFullNames.PALE_OAK_SLAB, BlockID.PALE_OAK_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy PALE_OAK_STAIRS = REGISTRY.registerBlock(BlockFullNames.PALE_OAK_STAIRS, BlockID.PALE_OAK_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy PALE_OAK_STANDING_SIGN = REGISTRY.registerBlock(BlockFullNames.PALE_OAK_STANDING_SIGN, BlockID.PALE_OAK_STANDING_SIGN)
            .addState(BlockStates.GROUND_SIGN_DIRECTION);
    public static final BlockLegacy PALE_OAK_TRAPDOOR = REGISTRY.registerBlock(BlockFullNames.PALE_OAK_TRAPDOOR, BlockID.PALE_OAK_TRAPDOOR)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT)
            .addState(BlockStates.OPEN_BIT);
    public static final BlockLegacy PALE_OAK_WALL_SIGN = REGISTRY.registerBlock(BlockFullNames.PALE_OAK_WALL_SIGN, BlockID.PALE_OAK_WALL_SIGN)
            .addState(BlockStates.FACING_DIRECTION);
    public static final BlockLegacy PALE_OAK_WOOD = REGISTRY.registerBlock(BlockFullNames.PALE_OAK_WOOD, BlockID.PALE_OAK_WOOD)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy PEARLESCENT_FROGLIGHT = REGISTRY.registerBlock(BlockFullNames.PEARLESCENT_FROGLIGHT, BlockID.PEARLESCENT_FROGLIGHT)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy PEONY = REGISTRY.registerBlock(BlockFullNames.PEONY, BlockID.PEONY)
            .addState(BlockStates.UPPER_BLOCK_BIT);
    public static final BlockLegacy PETRIFIED_OAK_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.PETRIFIED_OAK_DOUBLE_SLAB, BlockID.PETRIFIED_OAK_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy PETRIFIED_OAK_SLAB = REGISTRY.registerBlock(BlockFullNames.PETRIFIED_OAK_SLAB, BlockID.PETRIFIED_OAK_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy PIGLIN_HEAD = REGISTRY.registerBlock(BlockFullNames.PIGLIN_HEAD, BlockID.PIGLIN_HEAD)
            .addState(BlockStates.FACING_DIRECTION);
    public static final BlockLegacy PINK_CANDLE = REGISTRY.registerBlock(BlockFullNames.PINK_CANDLE, BlockID.PINK_CANDLE)
            .addState(BlockStates.CANDLES)
            .addState(BlockStates.LIT);
    public static final BlockLegacy PINK_CANDLE_CAKE = REGISTRY.registerBlock(BlockFullNames.PINK_CANDLE_CAKE, BlockID.PINK_CANDLE_CAKE)
            .addState(BlockStates.LIT);
    public static final BlockLegacy PINK_CARPET = REGISTRY.registerBlock(BlockFullNames.PINK_CARPET, BlockID.PINK_CARPET);
    public static final BlockLegacy PINK_CONCRETE = REGISTRY.registerBlock(BlockFullNames.PINK_CONCRETE, BlockID.PINK_CONCRETE);
    public static final BlockLegacy PINK_CONCRETE_POWDER = REGISTRY.registerBlock(BlockFullNames.PINK_CONCRETE_POWDER, BlockID.PINK_CONCRETE_POWDER);
    public static final BlockLegacy PINK_GLAZED_TERRACOTTA = REGISTRY.registerBlock(BlockFullNames.PINK_GLAZED_TERRACOTTA, BlockID.PINK_GLAZED_TERRACOTTA)
            .addState(BlockStates.FACING_DIRECTION);
    public static final BlockLegacy PINK_PETALS = REGISTRY.registerBlock(BlockFullNames.PINK_PETALS, BlockID.PINK_PETALS)
            .addState(BlockStates.GROWTH)
            .addState(BlockStates.MINECRAFT_CARDINAL_DIRECTION);
    public static final BlockLegacy PINK_SHULKER_BOX = REGISTRY.registerBlock(BlockFullNames.PINK_SHULKER_BOX, BlockID.PINK_SHULKER_BOX);
    public static final BlockLegacy PINK_STAINED_GLASS = REGISTRY.registerBlock(BlockFullNames.PINK_STAINED_GLASS, BlockID.PINK_STAINED_GLASS);
    public static final BlockLegacy PINK_STAINED_GLASS_PANE = REGISTRY.registerBlock(BlockFullNames.PINK_STAINED_GLASS_PANE, BlockID.PINK_STAINED_GLASS_PANE);
    public static final BlockLegacy PINK_TERRACOTTA = REGISTRY.registerBlock(BlockFullNames.PINK_TERRACOTTA, BlockID.PINK_TERRACOTTA);
    public static final BlockLegacy PINK_TULIP = REGISTRY.registerBlock(BlockFullNames.PINK_TULIP, BlockID.PINK_TULIP);
    public static final BlockLegacy PINK_WOOL = REGISTRY.registerBlock(BlockFullNames.PINK_WOOL, BlockID.PINK_WOOL);
    public static final BlockLegacy PISTON = REGISTRY.registerBlock(BlockFullNames.PISTON, BlockID.PISTON)
            .addState(BlockStates.FACING_DIRECTION);
    public static final BlockLegacy PISTON_ARM_COLLISION = REGISTRY.registerBlock(BlockFullNames.PISTON_ARM_COLLISION, BlockID.PISTON_ARM_COLLISION)
            .addState(BlockStates.FACING_DIRECTION);
    public static final BlockLegacy PITCHER_CROP = REGISTRY.registerBlock(BlockFullNames.PITCHER_CROP, BlockID.PITCHER_CROP)
            .addState(BlockStates.UPPER_BLOCK_BIT)
            .addState(BlockStates.GROWTH);
    public static final BlockLegacy PITCHER_PLANT = REGISTRY.registerBlock(BlockFullNames.PITCHER_PLANT, BlockID.PITCHER_PLANT)
            .addState(BlockStates.UPPER_BLOCK_BIT);
    public static final BlockLegacy PLAYER_HEAD = REGISTRY.registerBlock(BlockFullNames.PLAYER_HEAD, BlockID.PLAYER_HEAD)
            .addState(BlockStates.FACING_DIRECTION);
    public static final BlockLegacy PODZOL = REGISTRY.registerBlock(BlockFullNames.PODZOL, BlockID.PODZOL);
    public static final BlockLegacy POINTED_DRIPSTONE = REGISTRY.registerBlock(BlockFullNames.POINTED_DRIPSTONE, BlockID.POINTED_DRIPSTONE)
            .addState(BlockStates.DRIPSTONE_THICKNESS)
            .addState(BlockStates.HANGING);
    public static final BlockLegacy POLISHED_ANDESITE = REGISTRY.registerBlock(BlockFullNames.POLISHED_ANDESITE, BlockID.POLISHED_ANDESITE);
    public static final BlockLegacy POLISHED_ANDESITE_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.POLISHED_ANDESITE_DOUBLE_SLAB, BlockID.POLISHED_ANDESITE_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy POLISHED_ANDESITE_SLAB = REGISTRY.registerBlock(BlockFullNames.POLISHED_ANDESITE_SLAB, BlockID.POLISHED_ANDESITE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy POLISHED_ANDESITE_STAIRS = REGISTRY.registerBlock(BlockFullNames.POLISHED_ANDESITE_STAIRS, BlockID.POLISHED_ANDESITE_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy POLISHED_BASALT = REGISTRY.registerBlock(BlockFullNames.POLISHED_BASALT, BlockID.POLISHED_BASALT)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy POLISHED_BLACKSTONE = REGISTRY.registerBlock(BlockFullNames.POLISHED_BLACKSTONE, BlockID.POLISHED_BLACKSTONE);
    public static final BlockLegacy POLISHED_BLACKSTONE_BRICK_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.POLISHED_BLACKSTONE_BRICK_DOUBLE_SLAB, BlockID.POLISHED_BLACKSTONE_BRICK_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy POLISHED_BLACKSTONE_BRICK_SLAB = REGISTRY.registerBlock(BlockFullNames.POLISHED_BLACKSTONE_BRICK_SLAB, BlockID.POLISHED_BLACKSTONE_BRICK_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy POLISHED_BLACKSTONE_BRICK_STAIRS = REGISTRY.registerBlock(BlockFullNames.POLISHED_BLACKSTONE_BRICK_STAIRS, BlockID.POLISHED_BLACKSTONE_BRICK_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy POLISHED_BLACKSTONE_BRICK_WALL = REGISTRY.registerBlock(BlockFullNames.POLISHED_BLACKSTONE_BRICK_WALL, BlockID.POLISHED_BLACKSTONE_BRICK_WALL)
            .addState(BlockStates.WALL_POST_BIT)
            .addState(BlockStates.WALL_CONNECTION_TYPE_NORTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_EAST)
            .addState(BlockStates.WALL_CONNECTION_TYPE_SOUTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_WEST);
    public static final BlockLegacy POLISHED_BLACKSTONE_BRICKS = REGISTRY.registerBlock(BlockFullNames.POLISHED_BLACKSTONE_BRICKS, BlockID.POLISHED_BLACKSTONE_BRICKS);
    public static final BlockLegacy POLISHED_BLACKSTONE_BUTTON = REGISTRY.registerBlock(BlockFullNames.POLISHED_BLACKSTONE_BUTTON, BlockID.POLISHED_BLACKSTONE_BUTTON)
            .addState(BlockStates.FACING_DIRECTION)
            .addState(BlockStates.BUTTON_PRESSED_BIT);
    public static final BlockLegacy POLISHED_BLACKSTONE_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.POLISHED_BLACKSTONE_DOUBLE_SLAB, BlockID.POLISHED_BLACKSTONE_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy POLISHED_BLACKSTONE_PRESSURE_PLATE = REGISTRY.registerBlock(BlockFullNames.POLISHED_BLACKSTONE_PRESSURE_PLATE, BlockID.POLISHED_BLACKSTONE_PRESSURE_PLATE)
            .addState(BlockStates.REDSTONE_SIGNAL);
    public static final BlockLegacy POLISHED_BLACKSTONE_SLAB = REGISTRY.registerBlock(BlockFullNames.POLISHED_BLACKSTONE_SLAB, BlockID.POLISHED_BLACKSTONE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy POLISHED_BLACKSTONE_STAIRS = REGISTRY.registerBlock(BlockFullNames.POLISHED_BLACKSTONE_STAIRS, BlockID.POLISHED_BLACKSTONE_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy POLISHED_BLACKSTONE_WALL = REGISTRY.registerBlock(BlockFullNames.POLISHED_BLACKSTONE_WALL, BlockID.POLISHED_BLACKSTONE_WALL)
            .addState(BlockStates.WALL_POST_BIT)
            .addState(BlockStates.WALL_CONNECTION_TYPE_NORTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_EAST)
            .addState(BlockStates.WALL_CONNECTION_TYPE_SOUTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_WEST);
    public static final BlockLegacy POLISHED_DEEPSLATE = REGISTRY.registerBlock(BlockFullNames.POLISHED_DEEPSLATE, BlockID.POLISHED_DEEPSLATE);
    public static final BlockLegacy POLISHED_DEEPSLATE_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.POLISHED_DEEPSLATE_DOUBLE_SLAB, BlockID.POLISHED_DEEPSLATE_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy POLISHED_DEEPSLATE_SLAB = REGISTRY.registerBlock(BlockFullNames.POLISHED_DEEPSLATE_SLAB, BlockID.POLISHED_DEEPSLATE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy POLISHED_DEEPSLATE_STAIRS = REGISTRY.registerBlock(BlockFullNames.POLISHED_DEEPSLATE_STAIRS, BlockID.POLISHED_DEEPSLATE_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy POLISHED_DEEPSLATE_WALL = REGISTRY.registerBlock(BlockFullNames.POLISHED_DEEPSLATE_WALL, BlockID.POLISHED_DEEPSLATE_WALL)
            .addState(BlockStates.WALL_POST_BIT)
            .addState(BlockStates.WALL_CONNECTION_TYPE_NORTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_EAST)
            .addState(BlockStates.WALL_CONNECTION_TYPE_SOUTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_WEST);
    public static final BlockLegacy POLISHED_DIORITE = REGISTRY.registerBlock(BlockFullNames.POLISHED_DIORITE, BlockID.POLISHED_DIORITE);
    public static final BlockLegacy POLISHED_DIORITE_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.POLISHED_DIORITE_DOUBLE_SLAB, BlockID.POLISHED_DIORITE_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy POLISHED_DIORITE_SLAB = REGISTRY.registerBlock(BlockFullNames.POLISHED_DIORITE_SLAB, BlockID.POLISHED_DIORITE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy POLISHED_DIORITE_STAIRS = REGISTRY.registerBlock(BlockFullNames.POLISHED_DIORITE_STAIRS, BlockID.POLISHED_DIORITE_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy POLISHED_GRANITE = REGISTRY.registerBlock(BlockFullNames.POLISHED_GRANITE, BlockID.POLISHED_GRANITE);
    public static final BlockLegacy POLISHED_GRANITE_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.POLISHED_GRANITE_DOUBLE_SLAB, BlockID.POLISHED_GRANITE_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy POLISHED_GRANITE_SLAB = REGISTRY.registerBlock(BlockFullNames.POLISHED_GRANITE_SLAB, BlockID.POLISHED_GRANITE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy POLISHED_GRANITE_STAIRS = REGISTRY.registerBlock(BlockFullNames.POLISHED_GRANITE_STAIRS, BlockID.POLISHED_GRANITE_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy POLISHED_TUFF = REGISTRY.registerBlock(BlockFullNames.POLISHED_TUFF, BlockID.POLISHED_TUFF);
    public static final BlockLegacy POLISHED_TUFF_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.POLISHED_TUFF_DOUBLE_SLAB, BlockID.POLISHED_TUFF_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy POLISHED_TUFF_SLAB = REGISTRY.registerBlock(BlockFullNames.POLISHED_TUFF_SLAB, BlockID.POLISHED_TUFF_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy POLISHED_TUFF_STAIRS = REGISTRY.registerBlock(BlockFullNames.POLISHED_TUFF_STAIRS, BlockID.POLISHED_TUFF_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy POLISHED_TUFF_WALL = REGISTRY.registerBlock(BlockFullNames.POLISHED_TUFF_WALL, BlockID.POLISHED_TUFF_WALL)
            .addState(BlockStates.WALL_POST_BIT)
            .addState(BlockStates.WALL_CONNECTION_TYPE_NORTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_EAST)
            .addState(BlockStates.WALL_CONNECTION_TYPE_SOUTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_WEST);
    public static final BlockLegacy POPPY = REGISTRY.registerBlock(BlockFullNames.POPPY, BlockID.POPPY);
    public static final BlockLegacy PORTAL = REGISTRY.registerBlock(BlockFullNames.PORTAL, BlockID.PORTAL)
            .addState(BlockStates.PORTAL_AXIS);
    public static final BlockLegacy POTATOES = REGISTRY.registerBlock(BlockFullNames.POTATOES, BlockID.POTATOES)
            .addState(BlockStates.GROWTH);
    public static final BlockLegacy POWDER_SNOW = REGISTRY.registerBlock(BlockFullNames.POWDER_SNOW, BlockID.POWDER_SNOW);
    public static final BlockLegacy POWERED_COMPARATOR = REGISTRY.registerBlock(BlockFullNames.POWERED_COMPARATOR, BlockID.POWERED_COMPARATOR)
            .addState(BlockStates.MINECRAFT_CARDINAL_DIRECTION)
            .addState(BlockStates.OUTPUT_SUBTRACT_BIT)
            .addState(BlockStates.OUTPUT_LIT_BIT);
    public static final BlockLegacy POWERED_REPEATER = REGISTRY.registerBlock(BlockFullNames.POWERED_REPEATER, BlockID.POWERED_REPEATER)
            .addState(BlockStates.MINECRAFT_CARDINAL_DIRECTION)
            .addState(BlockStates.REPEATER_DELAY);
    public static final BlockLegacy PRISMARINE = REGISTRY.registerBlock(BlockFullNames.PRISMARINE, BlockID.PRISMARINE);
    public static final BlockLegacy PRISMARINE_BRICK_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.PRISMARINE_BRICK_DOUBLE_SLAB, BlockID.PRISMARINE_BRICK_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy PRISMARINE_BRICK_SLAB = REGISTRY.registerBlock(BlockFullNames.PRISMARINE_BRICK_SLAB, BlockID.PRISMARINE_BRICK_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy PRISMARINE_BRICKS = REGISTRY.registerBlock(BlockFullNames.PRISMARINE_BRICKS, BlockID.PRISMARINE_BRICKS);
    public static final BlockLegacy PRISMARINE_BRICKS_STAIRS = REGISTRY.registerBlock(BlockFullNames.PRISMARINE_BRICKS_STAIRS, BlockID.PRISMARINE_BRICKS_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy PRISMARINE_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.PRISMARINE_DOUBLE_SLAB, BlockID.PRISMARINE_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy PRISMARINE_SLAB = REGISTRY.registerBlock(BlockFullNames.PRISMARINE_SLAB, BlockID.PRISMARINE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy PRISMARINE_STAIRS = REGISTRY.registerBlock(BlockFullNames.PRISMARINE_STAIRS, BlockID.PRISMARINE_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy PRISMARINE_WALL = REGISTRY.registerBlock(BlockFullNames.PRISMARINE_WALL, BlockID.PRISMARINE_WALL)
            .addState(BlockStates.WALL_POST_BIT)
            .addState(BlockStates.WALL_CONNECTION_TYPE_NORTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_EAST)
            .addState(BlockStates.WALL_CONNECTION_TYPE_SOUTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_WEST);
    public static final BlockLegacy PUMPKIN = REGISTRY.registerBlock(BlockFullNames.PUMPKIN, BlockID.PUMPKIN)
            .addState(BlockStates.MINECRAFT_CARDINAL_DIRECTION);
    public static final BlockLegacy PUMPKIN_STEM = REGISTRY.registerBlock(BlockFullNames.PUMPKIN_STEM, BlockID.PUMPKIN_STEM)
            .addState(BlockStates.GROWTH)
            .addState(BlockStates.FACING_DIRECTION);
    public static final BlockLegacy PURPLE_CANDLE = REGISTRY.registerBlock(BlockFullNames.PURPLE_CANDLE, BlockID.PURPLE_CANDLE)
            .addState(BlockStates.CANDLES)
            .addState(BlockStates.LIT);
    public static final BlockLegacy PURPLE_CANDLE_CAKE = REGISTRY.registerBlock(BlockFullNames.PURPLE_CANDLE_CAKE, BlockID.PURPLE_CANDLE_CAKE)
            .addState(BlockStates.LIT);
    public static final BlockLegacy PURPLE_CARPET = REGISTRY.registerBlock(BlockFullNames.PURPLE_CARPET, BlockID.PURPLE_CARPET);
    public static final BlockLegacy PURPLE_CONCRETE = REGISTRY.registerBlock(BlockFullNames.PURPLE_CONCRETE, BlockID.PURPLE_CONCRETE);
    public static final BlockLegacy PURPLE_CONCRETE_POWDER = REGISTRY.registerBlock(BlockFullNames.PURPLE_CONCRETE_POWDER, BlockID.PURPLE_CONCRETE_POWDER);
    public static final BlockLegacy PURPLE_GLAZED_TERRACOTTA = REGISTRY.registerBlock(BlockFullNames.PURPLE_GLAZED_TERRACOTTA, BlockID.PURPLE_GLAZED_TERRACOTTA)
            .addState(BlockStates.FACING_DIRECTION);
    public static final BlockLegacy PURPLE_SHULKER_BOX = REGISTRY.registerBlock(BlockFullNames.PURPLE_SHULKER_BOX, BlockID.PURPLE_SHULKER_BOX);
    public static final BlockLegacy PURPLE_STAINED_GLASS = REGISTRY.registerBlock(BlockFullNames.PURPLE_STAINED_GLASS, BlockID.PURPLE_STAINED_GLASS);
    public static final BlockLegacy PURPLE_STAINED_GLASS_PANE = REGISTRY.registerBlock(BlockFullNames.PURPLE_STAINED_GLASS_PANE, BlockID.PURPLE_STAINED_GLASS_PANE);
    public static final BlockLegacy PURPLE_TERRACOTTA = REGISTRY.registerBlock(BlockFullNames.PURPLE_TERRACOTTA, BlockID.PURPLE_TERRACOTTA);
    public static final BlockLegacy PURPLE_WOOL = REGISTRY.registerBlock(BlockFullNames.PURPLE_WOOL, BlockID.PURPLE_WOOL);
    public static final BlockLegacy PURPUR_BLOCK = REGISTRY.registerBlock(BlockFullNames.PURPUR_BLOCK, BlockID.PURPUR_BLOCK)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy PURPUR_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.PURPUR_DOUBLE_SLAB, BlockID.PURPUR_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy PURPUR_PILLAR = REGISTRY.registerBlock(BlockFullNames.PURPUR_PILLAR, BlockID.PURPUR_PILLAR)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy PURPUR_SLAB = REGISTRY.registerBlock(BlockFullNames.PURPUR_SLAB, BlockID.PURPUR_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy PURPUR_STAIRS = REGISTRY.registerBlock(BlockFullNames.PURPUR_STAIRS, BlockID.PURPUR_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy QUARTZ_BLOCK = REGISTRY.registerBlock(BlockFullNames.QUARTZ_BLOCK, BlockID.QUARTZ_BLOCK)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy QUARTZ_BRICKS = REGISTRY.registerBlock(BlockFullNames.QUARTZ_BRICKS, BlockID.QUARTZ_BRICKS);
    public static final BlockLegacy QUARTZ_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.QUARTZ_DOUBLE_SLAB, BlockID.QUARTZ_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy QUARTZ_ORE = REGISTRY.registerBlock(BlockFullNames.QUARTZ_ORE, BlockID.QUARTZ_ORE);
    public static final BlockLegacy QUARTZ_PILLAR = REGISTRY.registerBlock(BlockFullNames.QUARTZ_PILLAR, BlockID.QUARTZ_PILLAR)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy QUARTZ_SLAB = REGISTRY.registerBlock(BlockFullNames.QUARTZ_SLAB, BlockID.QUARTZ_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy QUARTZ_STAIRS = REGISTRY.registerBlock(BlockFullNames.QUARTZ_STAIRS, BlockID.QUARTZ_STAIRS)
            .addState(BlockStates.UPSIDE_DOWN_BIT)
            .addState(BlockStates.WEIRDO_DIRECTION);
    public static final BlockLegacy RAIL = REGISTRY.registerBlock(BlockFullNames.RAIL, BlockID.RAIL)
            .addState(BlockStates.RAIL_DIRECTION);
    public static final BlockLegacy RAW_COPPER_BLOCK = REGISTRY.registerBlock(BlockFullNames.RAW_COPPER_BLOCK, BlockID.RAW_COPPER_BLOCK);
    public static final BlockLegacy RAW_GOLD_BLOCK = REGISTRY.registerBlock(BlockFullNames.RAW_GOLD_BLOCK, BlockID.RAW_GOLD_BLOCK);
    public static final BlockLegacy RAW_IRON_BLOCK = REGISTRY.registerBlock(BlockFullNames.RAW_IRON_BLOCK, BlockID.RAW_IRON_BLOCK);
    public static final BlockLegacy RED_CANDLE = REGISTRY.registerBlock(BlockFullNames.RED_CANDLE, BlockID.RED_CANDLE)
            .addState(BlockStates.CANDLES)
            .addState(BlockStates.LIT);
    public static final BlockLegacy RED_CANDLE_CAKE = REGISTRY.registerBlock(BlockFullNames.RED_CANDLE_CAKE, BlockID.RED_CANDLE_CAKE)
            .addState(BlockStates.LIT);
    public static final BlockLegacy RED_CARPET = REGISTRY.registerBlock(BlockFullNames.RED_CARPET, BlockID.RED_CARPET);
    public static final BlockLegacy RED_CONCRETE = REGISTRY.registerBlock(BlockFullNames.RED_CONCRETE, BlockID.RED_CONCRETE);
    public static final BlockLegacy RED_CONCRETE_POWDER = REGISTRY.registerBlock(BlockFullNames.RED_CONCRETE_POWDER, BlockID.RED_CONCRETE_POWDER);
    public static final BlockLegacy RED_GLAZED_TERRACOTTA = REGISTRY.registerBlock(BlockFullNames.RED_GLAZED_TERRACOTTA, BlockID.RED_GLAZED_TERRACOTTA)
            .addState(BlockStates.FACING_DIRECTION);
    public static final BlockLegacy RED_MUSHROOM = REGISTRY.registerBlock(BlockFullNames.RED_MUSHROOM, BlockID.RED_MUSHROOM);
    public static final BlockLegacy RED_MUSHROOM_BLOCK = REGISTRY.registerBlock(BlockFullNames.RED_MUSHROOM_BLOCK, BlockID.RED_MUSHROOM_BLOCK)
            .addState(BlockStates.HUGE_MUSHROOM_BITS);
    public static final BlockLegacy RED_NETHER_BRICK = REGISTRY.registerBlock(BlockFullNames.RED_NETHER_BRICK, BlockID.RED_NETHER_BRICK);
    public static final BlockLegacy RED_NETHER_BRICK_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.RED_NETHER_BRICK_DOUBLE_SLAB, BlockID.RED_NETHER_BRICK_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy RED_NETHER_BRICK_SLAB = REGISTRY.registerBlock(BlockFullNames.RED_NETHER_BRICK_SLAB, BlockID.RED_NETHER_BRICK_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy RED_NETHER_BRICK_STAIRS = REGISTRY.registerBlock(BlockFullNames.RED_NETHER_BRICK_STAIRS, BlockID.RED_NETHER_BRICK_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy RED_NETHER_BRICK_WALL = REGISTRY.registerBlock(BlockFullNames.RED_NETHER_BRICK_WALL, BlockID.RED_NETHER_BRICK_WALL)
            .addState(BlockStates.WALL_POST_BIT)
            .addState(BlockStates.WALL_CONNECTION_TYPE_NORTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_EAST)
            .addState(BlockStates.WALL_CONNECTION_TYPE_SOUTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_WEST);
    public static final BlockLegacy RED_SAND = REGISTRY.registerBlock(BlockFullNames.RED_SAND, BlockID.RED_SAND);
    public static final BlockLegacy RED_SANDSTONE = REGISTRY.registerBlock(BlockFullNames.RED_SANDSTONE, BlockID.RED_SANDSTONE);
    public static final BlockLegacy RED_SANDSTONE_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.RED_SANDSTONE_DOUBLE_SLAB, BlockID.RED_SANDSTONE_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy RED_SANDSTONE_SLAB = REGISTRY.registerBlock(BlockFullNames.RED_SANDSTONE_SLAB, BlockID.RED_SANDSTONE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy RED_SANDSTONE_STAIRS = REGISTRY.registerBlock(BlockFullNames.RED_SANDSTONE_STAIRS, BlockID.RED_SANDSTONE_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy RED_SANDSTONE_WALL = REGISTRY.registerBlock(BlockFullNames.RED_SANDSTONE_WALL, BlockID.RED_SANDSTONE_WALL)
            .addState(BlockStates.WALL_POST_BIT)
            .addState(BlockStates.WALL_CONNECTION_TYPE_NORTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_EAST)
            .addState(BlockStates.WALL_CONNECTION_TYPE_SOUTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_WEST);
    public static final BlockLegacy RED_SHULKER_BOX = REGISTRY.registerBlock(BlockFullNames.RED_SHULKER_BOX, BlockID.RED_SHULKER_BOX);
    public static final BlockLegacy RED_STAINED_GLASS = REGISTRY.registerBlock(BlockFullNames.RED_STAINED_GLASS, BlockID.RED_STAINED_GLASS);
    public static final BlockLegacy RED_STAINED_GLASS_PANE = REGISTRY.registerBlock(BlockFullNames.RED_STAINED_GLASS_PANE, BlockID.RED_STAINED_GLASS_PANE);
    public static final BlockLegacy RED_TERRACOTTA = REGISTRY.registerBlock(BlockFullNames.RED_TERRACOTTA, BlockID.RED_TERRACOTTA);
    public static final BlockLegacy RED_TULIP = REGISTRY.registerBlock(BlockFullNames.RED_TULIP, BlockID.RED_TULIP);
    public static final BlockLegacy RED_WOOL = REGISTRY.registerBlock(BlockFullNames.RED_WOOL, BlockID.RED_WOOL);
    public static final BlockLegacy REDSTONE_BLOCK = REGISTRY.registerBlock(BlockFullNames.REDSTONE_BLOCK, BlockID.REDSTONE_BLOCK);
    public static final BlockLegacy REDSTONE_LAMP = REGISTRY.registerBlock(BlockFullNames.REDSTONE_LAMP, BlockID.REDSTONE_LAMP);
    public static final BlockLegacy REDSTONE_ORE = REGISTRY.registerBlock(BlockFullNames.REDSTONE_ORE, BlockID.REDSTONE_ORE);
    public static final BlockLegacy REDSTONE_TORCH = REGISTRY.registerBlock(BlockFullNames.REDSTONE_TORCH, BlockID.REDSTONE_TORCH)
            .addState(BlockStates.TORCH_FACING_DIRECTION);
    public static final BlockLegacy REDSTONE_WIRE = REGISTRY.registerBlock(BlockFullNames.REDSTONE_WIRE, BlockID.REDSTONE_WIRE)
            .addState(BlockStates.REDSTONE_SIGNAL);
    public static final BlockLegacy REEDS = REGISTRY.registerBlock(BlockFullNames.REEDS, BlockID.REEDS)
            .addState(BlockStates.AGE);
    public static final BlockLegacy REINFORCED_DEEPSLATE = REGISTRY.registerBlock(BlockFullNames.REINFORCED_DEEPSLATE, BlockID.REINFORCED_DEEPSLATE);
    public static final BlockLegacy REPEATING_COMMAND_BLOCK = REGISTRY.registerBlock(BlockFullNames.REPEATING_COMMAND_BLOCK, BlockID.REPEATING_COMMAND_BLOCK)
            .addState(BlockStates.FACING_DIRECTION)
            .addState(BlockStates.CONDITIONAL_BIT);
    public static final BlockLegacy RESERVED6 = REGISTRY.registerBlock(BlockFullNames.RESERVED6, BlockID.RESERVED6);
    public static final BlockLegacy RESIN_BLOCK = REGISTRY.registerBlock(BlockFullNames.RESIN_BLOCK, BlockID.RESIN_BLOCK);
    public static final BlockLegacy RESIN_BRICK_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.RESIN_BRICK_DOUBLE_SLAB, BlockID.RESIN_BRICK_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy RESIN_BRICK_SLAB = REGISTRY.registerBlock(BlockFullNames.RESIN_BRICK_SLAB, BlockID.RESIN_BRICK_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy RESIN_BRICK_STAIRS = REGISTRY.registerBlock(BlockFullNames.RESIN_BRICK_STAIRS, BlockID.RESIN_BRICK_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy RESIN_BRICK_WALL = REGISTRY.registerBlock(BlockFullNames.RESIN_BRICK_WALL, BlockID.RESIN_BRICK_WALL)
            .addState(BlockStates.WALL_POST_BIT)
            .addState(BlockStates.WALL_CONNECTION_TYPE_NORTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_EAST)
            .addState(BlockStates.WALL_CONNECTION_TYPE_SOUTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_WEST);
    public static final BlockLegacy RESIN_BRICKS = REGISTRY.registerBlock(BlockFullNames.RESIN_BRICKS, BlockID.RESIN_BRICKS);
    public static final BlockLegacy RESIN_CLUMP = REGISTRY.registerBlock(BlockFullNames.RESIN_CLUMP, BlockID.RESIN_CLUMP)
            .addState(BlockStates.MULTI_FACE_DIRECTION_BITS);
    public static final BlockLegacy RESPAWN_ANCHOR = REGISTRY.registerBlock(BlockFullNames.RESPAWN_ANCHOR, BlockID.RESPAWN_ANCHOR)
            .addState(BlockStates.RESPAWN_ANCHOR_CHARGE);
    public static final BlockLegacy ROSE_BUSH = REGISTRY.registerBlock(BlockFullNames.ROSE_BUSH, BlockID.ROSE_BUSH)
            .addState(BlockStates.UPPER_BLOCK_BIT);
    public static final BlockLegacy SAND = REGISTRY.registerBlock(BlockFullNames.SAND, BlockID.SAND);
    public static final BlockLegacy SANDSTONE = REGISTRY.registerBlock(BlockFullNames.SANDSTONE, BlockID.SANDSTONE);
    public static final BlockLegacy SANDSTONE_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.SANDSTONE_DOUBLE_SLAB, BlockID.SANDSTONE_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy SANDSTONE_SLAB = REGISTRY.registerBlock(BlockFullNames.SANDSTONE_SLAB, BlockID.SANDSTONE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy SANDSTONE_STAIRS = REGISTRY.registerBlock(BlockFullNames.SANDSTONE_STAIRS, BlockID.SANDSTONE_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy SANDSTONE_WALL = REGISTRY.registerBlock(BlockFullNames.SANDSTONE_WALL, BlockID.SANDSTONE_WALL)
            .addState(BlockStates.WALL_POST_BIT)
            .addState(BlockStates.WALL_CONNECTION_TYPE_NORTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_EAST)
            .addState(BlockStates.WALL_CONNECTION_TYPE_SOUTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_WEST);
    public static final BlockLegacy SCAFFOLDING = REGISTRY.registerBlock(BlockFullNames.SCAFFOLDING, BlockID.SCAFFOLDING)
            .addState(BlockStates.STABILITY)
            .addState(BlockStates.STABILITY_CHECK);
    public static final BlockLegacy SCULK = REGISTRY.registerBlock(BlockFullNames.SCULK, BlockID.SCULK);
    public static final BlockLegacy SCULK_CATALYST = REGISTRY.registerBlock(BlockFullNames.SCULK_CATALYST, BlockID.SCULK_CATALYST)
            .addState(BlockStates.BLOOM);
    public static final BlockLegacy SCULK_SENSOR = REGISTRY.registerBlock(BlockFullNames.SCULK_SENSOR, BlockID.SCULK_SENSOR)
            .addState(BlockStates.SCULK_SENSOR_PHASE);
    public static final BlockLegacy SCULK_SHRIEKER = REGISTRY.registerBlock(BlockFullNames.SCULK_SHRIEKER, BlockID.SCULK_SHRIEKER)
            .addState(BlockStates.ACTIVE)
            .addState(BlockStates.CAN_SUMMON);
    public static final BlockLegacy SCULK_VEIN = REGISTRY.registerBlock(BlockFullNames.SCULK_VEIN, BlockID.SCULK_VEIN)
            .addState(BlockStates.MULTI_FACE_DIRECTION_BITS);
    public static final BlockLegacy SEA_LANTERN = REGISTRY.registerBlock(BlockFullNames.SEA_LANTERN, BlockID.SEA_LANTERN);
    public static final BlockLegacy SEA_PICKLE = REGISTRY.registerBlock(BlockFullNames.SEA_PICKLE, BlockID.SEA_PICKLE)
            .addState(BlockStates.CLUSTER_COUNT)
            .addState(BlockStates.DEAD_BIT);
    public static final BlockLegacy SEAGRASS = REGISTRY.registerBlock(BlockFullNames.SEAGRASS, BlockID.SEAGRASS)
            .addState(BlockStates.SEA_GRASS_TYPE);
    public static final BlockLegacy SHORT_GRASS = REGISTRY.registerBlock(BlockFullNames.SHORT_GRASS, BlockID.SHORT_GRASS);
    public static final BlockLegacy SHROOMLIGHT = REGISTRY.registerBlock(BlockFullNames.SHROOMLIGHT, BlockID.SHROOMLIGHT);
    public static final BlockLegacy SILVER_GLAZED_TERRACOTTA = REGISTRY.registerBlock(BlockFullNames.SILVER_GLAZED_TERRACOTTA, BlockID.SILVER_GLAZED_TERRACOTTA)
            .addState(BlockStates.FACING_DIRECTION);
    public static final BlockLegacy SKELETON_SKULL = REGISTRY.registerBlock(BlockFullNames.SKELETON_SKULL, BlockID.SKELETON_SKULL)
            .addState(BlockStates.FACING_DIRECTION);
    public static final BlockLegacy SLIME = REGISTRY.registerBlock(BlockFullNames.SLIME, BlockID.SLIME);
    public static final BlockLegacy SMALL_AMETHYST_BUD = REGISTRY.registerBlock(BlockFullNames.SMALL_AMETHYST_BUD, BlockID.SMALL_AMETHYST_BUD)
            .addState(BlockStates.MINECRAFT_BLOCK_FACE);
    public static final BlockLegacy SMALL_DRIPLEAF_BLOCK = REGISTRY.registerBlock(BlockFullNames.SMALL_DRIPLEAF_BLOCK, BlockID.SMALL_DRIPLEAF_BLOCK)
            .addState(BlockStates.UPPER_BLOCK_BIT)
            .addState(BlockStates.MINECRAFT_CARDINAL_DIRECTION);
    public static final BlockLegacy SMITHING_TABLE = REGISTRY.registerBlock(BlockFullNames.SMITHING_TABLE, BlockID.SMITHING_TABLE);
    public static final BlockLegacy SMOKER = REGISTRY.registerBlock(BlockFullNames.SMOKER, BlockID.SMOKER)
            .addState(BlockStates.MINECRAFT_CARDINAL_DIRECTION);
    public static final BlockLegacy SMOOTH_BASALT = REGISTRY.registerBlock(BlockFullNames.SMOOTH_BASALT, BlockID.SMOOTH_BASALT);
    public static final BlockLegacy SMOOTH_QUARTZ = REGISTRY.registerBlock(BlockFullNames.SMOOTH_QUARTZ, BlockID.SMOOTH_QUARTZ)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy SMOOTH_QUARTZ_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.SMOOTH_QUARTZ_DOUBLE_SLAB, BlockID.SMOOTH_QUARTZ_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy SMOOTH_QUARTZ_SLAB = REGISTRY.registerBlock(BlockFullNames.SMOOTH_QUARTZ_SLAB, BlockID.SMOOTH_QUARTZ_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy SMOOTH_QUARTZ_STAIRS = REGISTRY.registerBlock(BlockFullNames.SMOOTH_QUARTZ_STAIRS, BlockID.SMOOTH_QUARTZ_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy SMOOTH_RED_SANDSTONE = REGISTRY.registerBlock(BlockFullNames.SMOOTH_RED_SANDSTONE, BlockID.SMOOTH_RED_SANDSTONE);
    public static final BlockLegacy SMOOTH_RED_SANDSTONE_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.SMOOTH_RED_SANDSTONE_DOUBLE_SLAB, BlockID.SMOOTH_RED_SANDSTONE_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy SMOOTH_RED_SANDSTONE_SLAB = REGISTRY.registerBlock(BlockFullNames.SMOOTH_RED_SANDSTONE_SLAB, BlockID.SMOOTH_RED_SANDSTONE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy SMOOTH_RED_SANDSTONE_STAIRS = REGISTRY.registerBlock(BlockFullNames.SMOOTH_RED_SANDSTONE_STAIRS, BlockID.SMOOTH_RED_SANDSTONE_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy SMOOTH_SANDSTONE = REGISTRY.registerBlock(BlockFullNames.SMOOTH_SANDSTONE, BlockID.SMOOTH_SANDSTONE);
    public static final BlockLegacy SMOOTH_SANDSTONE_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.SMOOTH_SANDSTONE_DOUBLE_SLAB, BlockID.SMOOTH_SANDSTONE_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy SMOOTH_SANDSTONE_SLAB = REGISTRY.registerBlock(BlockFullNames.SMOOTH_SANDSTONE_SLAB, BlockID.SMOOTH_SANDSTONE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy SMOOTH_SANDSTONE_STAIRS = REGISTRY.registerBlock(BlockFullNames.SMOOTH_SANDSTONE_STAIRS, BlockID.SMOOTH_SANDSTONE_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy SMOOTH_STONE = REGISTRY.registerBlock(BlockFullNames.SMOOTH_STONE, BlockID.SMOOTH_STONE);
    public static final BlockLegacy SMOOTH_STONE_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.SMOOTH_STONE_DOUBLE_SLAB, BlockID.SMOOTH_STONE_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy SMOOTH_STONE_SLAB = REGISTRY.registerBlock(BlockFullNames.SMOOTH_STONE_SLAB, BlockID.SMOOTH_STONE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy SNIFFER_EGG = REGISTRY.registerBlock(BlockFullNames.SNIFFER_EGG, BlockID.SNIFFER_EGG)
            .addState(BlockStates.CRACKED_STATE);
    public static final BlockLegacy SNOW = REGISTRY.registerBlock(BlockFullNames.SNOW, BlockID.SNOW);
    public static final BlockLegacy SNOW_LAYER = REGISTRY.registerBlock(BlockFullNames.SNOW_LAYER, BlockID.SNOW_LAYER)
            .addState(BlockStates.HEIGHT)
            .addState(BlockStates.COVERED_BIT);
    public static final BlockLegacy SOUL_CAMPFIRE = REGISTRY.registerBlock(BlockFullNames.SOUL_CAMPFIRE, BlockID.SOUL_CAMPFIRE)
            .addState(BlockStates.MINECRAFT_CARDINAL_DIRECTION)
            .addState(BlockStates.EXTINGUISHED);
    public static final BlockLegacy SOUL_FIRE = REGISTRY.registerBlock(BlockFullNames.SOUL_FIRE, BlockID.SOUL_FIRE)
            .addState(BlockStates.AGE);
    public static final BlockLegacy SOUL_LANTERN = REGISTRY.registerBlock(BlockFullNames.SOUL_LANTERN, BlockID.SOUL_LANTERN)
            .addState(BlockStates.HANGING);
    public static final BlockLegacy SOUL_SAND = REGISTRY.registerBlock(BlockFullNames.SOUL_SAND, BlockID.SOUL_SAND);
    public static final BlockLegacy SOUL_SOIL = REGISTRY.registerBlock(BlockFullNames.SOUL_SOIL, BlockID.SOUL_SOIL);
    public static final BlockLegacy SOUL_TORCH = REGISTRY.registerBlock(BlockFullNames.SOUL_TORCH, BlockID.SOUL_TORCH)
            .addState(BlockStates.TORCH_FACING_DIRECTION);
    public static final BlockLegacy SPONGE = REGISTRY.registerBlock(BlockFullNames.SPONGE, BlockID.SPONGE);
    public static final BlockLegacy SPORE_BLOSSOM = REGISTRY.registerBlock(BlockFullNames.SPORE_BLOSSOM, BlockID.SPORE_BLOSSOM);
    public static final BlockLegacy SPRUCE_BUTTON = REGISTRY.registerBlock(BlockFullNames.SPRUCE_BUTTON, BlockID.SPRUCE_BUTTON)
            .addState(BlockStates.FACING_DIRECTION)
            .addState(BlockStates.BUTTON_PRESSED_BIT);
    public static final BlockLegacy SPRUCE_DOOR = REGISTRY.registerBlock(BlockFullNames.SPRUCE_DOOR, BlockID.SPRUCE_DOOR)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.OPEN_BIT)
            .addState(BlockStates.UPPER_BLOCK_BIT)
            .addState(BlockStates.DOOR_HINGE_BIT);
    public static final BlockLegacy SPRUCE_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.SPRUCE_DOUBLE_SLAB, BlockID.SPRUCE_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy SPRUCE_FENCE = REGISTRY.registerBlock(BlockFullNames.SPRUCE_FENCE, BlockID.SPRUCE_FENCE);
    public static final BlockLegacy SPRUCE_FENCE_GATE = REGISTRY.registerBlock(BlockFullNames.SPRUCE_FENCE_GATE, BlockID.SPRUCE_FENCE_GATE)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.OPEN_BIT)
            .addState(BlockStates.IN_WALL_BIT);
    public static final BlockLegacy SPRUCE_HANGING_SIGN = REGISTRY.registerBlock(BlockFullNames.SPRUCE_HANGING_SIGN, BlockID.SPRUCE_HANGING_SIGN)
            .addState(BlockStates.FACING_DIRECTION)
            .addState(BlockStates.GROUND_SIGN_DIRECTION)
            .addState(BlockStates.HANGING)
            .addState(BlockStates.ATTACHED_BIT);
    public static final BlockLegacy SPRUCE_LEAVES = REGISTRY.registerBlock(BlockFullNames.SPRUCE_LEAVES, BlockID.SPRUCE_LEAVES)
            .addState(BlockStates.UPDATE_BIT)
            .addState(BlockStates.PERSISTENT_BIT);
    public static final BlockLegacy SPRUCE_LOG = REGISTRY.registerBlock(BlockFullNames.SPRUCE_LOG, BlockID.SPRUCE_LOG)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy SPRUCE_PLANKS = REGISTRY.registerBlock(BlockFullNames.SPRUCE_PLANKS, BlockID.SPRUCE_PLANKS);
    public static final BlockLegacy SPRUCE_PRESSURE_PLATE = REGISTRY.registerBlock(BlockFullNames.SPRUCE_PRESSURE_PLATE, BlockID.SPRUCE_PRESSURE_PLATE)
            .addState(BlockStates.REDSTONE_SIGNAL);
    public static final BlockLegacy SPRUCE_SAPLING = REGISTRY.registerBlock(BlockFullNames.SPRUCE_SAPLING, BlockID.SPRUCE_SAPLING)
            .addState(BlockStates.AGE_BIT);
    public static final BlockLegacy SPRUCE_SLAB = REGISTRY.registerBlock(BlockFullNames.SPRUCE_SLAB, BlockID.SPRUCE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy SPRUCE_STAIRS = REGISTRY.registerBlock(BlockFullNames.SPRUCE_STAIRS, BlockID.SPRUCE_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy SPRUCE_STANDING_SIGN = REGISTRY.registerBlock(BlockFullNames.SPRUCE_STANDING_SIGN, BlockID.SPRUCE_STANDING_SIGN)
            .addState(BlockStates.GROUND_SIGN_DIRECTION);
    public static final BlockLegacy SPRUCE_TRAPDOOR = REGISTRY.registerBlock(BlockFullNames.SPRUCE_TRAPDOOR, BlockID.SPRUCE_TRAPDOOR)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT)
            .addState(BlockStates.OPEN_BIT);
    public static final BlockLegacy SPRUCE_WALL_SIGN = REGISTRY.registerBlock(BlockFullNames.SPRUCE_WALL_SIGN, BlockID.SPRUCE_WALL_SIGN)
            .addState(BlockStates.FACING_DIRECTION);
    public static final BlockLegacy SPRUCE_WOOD = REGISTRY.registerBlock(BlockFullNames.SPRUCE_WOOD, BlockID.SPRUCE_WOOD)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy STANDING_BANNER = REGISTRY.registerBlock(BlockFullNames.STANDING_BANNER, BlockID.STANDING_BANNER)
            .addState(BlockStates.GROUND_SIGN_DIRECTION);
    public static final BlockLegacy STANDING_SIGN = REGISTRY.registerBlock(BlockFullNames.STANDING_SIGN, BlockID.STANDING_SIGN)
            .addState(BlockStates.GROUND_SIGN_DIRECTION);
    public static final BlockLegacy STICKY_PISTON = REGISTRY.registerBlock(BlockFullNames.STICKY_PISTON, BlockID.STICKY_PISTON)
            .addState(BlockStates.FACING_DIRECTION);
    public static final BlockLegacy STICKY_PISTON_ARM_COLLISION = REGISTRY.registerBlock(BlockFullNames.STICKY_PISTON_ARM_COLLISION, BlockID.STICKY_PISTON_ARM_COLLISION)
            .addState(BlockStates.FACING_DIRECTION);
    public static final BlockLegacy STONE = REGISTRY.registerBlock(BlockFullNames.STONE, BlockID.STONE);
    public static final BlockLegacy STONE_BRICK_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.STONE_BRICK_DOUBLE_SLAB, BlockID.STONE_BRICK_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy STONE_BRICK_SLAB = REGISTRY.registerBlock(BlockFullNames.STONE_BRICK_SLAB, BlockID.STONE_BRICK_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy STONE_BRICK_STAIRS = REGISTRY.registerBlock(BlockFullNames.STONE_BRICK_STAIRS, BlockID.STONE_BRICK_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy STONE_BRICK_WALL = REGISTRY.registerBlock(BlockFullNames.STONE_BRICK_WALL, BlockID.STONE_BRICK_WALL)
            .addState(BlockStates.WALL_POST_BIT)
            .addState(BlockStates.WALL_CONNECTION_TYPE_NORTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_EAST)
            .addState(BlockStates.WALL_CONNECTION_TYPE_SOUTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_WEST);
    public static final BlockLegacy STONE_BRICKS = REGISTRY.registerBlock(BlockFullNames.STONE_BRICKS, BlockID.STONE_BRICKS);
    public static final BlockLegacy STONE_BUTTON = REGISTRY.registerBlock(BlockFullNames.STONE_BUTTON, BlockID.STONE_BUTTON)
            .addState(BlockStates.FACING_DIRECTION)
            .addState(BlockStates.BUTTON_PRESSED_BIT);
    public static final BlockLegacy STONE_PRESSURE_PLATE = REGISTRY.registerBlock(BlockFullNames.STONE_PRESSURE_PLATE, BlockID.STONE_PRESSURE_PLATE)
            .addState(BlockStates.REDSTONE_SIGNAL);
    public static final BlockLegacy STONE_STAIRS = REGISTRY.registerBlock(BlockFullNames.STONE_STAIRS, BlockID.STONE_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy STONECUTTER = REGISTRY.registerBlock(BlockFullNames.STONECUTTER, BlockID.STONECUTTER);
    public static final BlockLegacy STONECUTTER_BLOCK = REGISTRY.registerBlock(BlockFullNames.STONECUTTER_BLOCK, BlockID.STONECUTTER_BLOCK)
            .addState(BlockStates.MINECRAFT_CARDINAL_DIRECTION);
    public static final BlockLegacy STRIPPED_ACACIA_LOG = REGISTRY.registerBlock(BlockFullNames.STRIPPED_ACACIA_LOG, BlockID.STRIPPED_ACACIA_LOG)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy STRIPPED_ACACIA_WOOD = REGISTRY.registerBlock(BlockFullNames.STRIPPED_ACACIA_WOOD, BlockID.STRIPPED_ACACIA_WOOD)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy STRIPPED_BAMBOO_BLOCK = REGISTRY.registerBlock(BlockFullNames.STRIPPED_BAMBOO_BLOCK, BlockID.STRIPPED_BAMBOO_BLOCK)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy STRIPPED_BIRCH_LOG = REGISTRY.registerBlock(BlockFullNames.STRIPPED_BIRCH_LOG, BlockID.STRIPPED_BIRCH_LOG)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy STRIPPED_BIRCH_WOOD = REGISTRY.registerBlock(BlockFullNames.STRIPPED_BIRCH_WOOD, BlockID.STRIPPED_BIRCH_WOOD)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy STRIPPED_CHERRY_LOG = REGISTRY.registerBlock(BlockFullNames.STRIPPED_CHERRY_LOG, BlockID.STRIPPED_CHERRY_LOG)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy STRIPPED_CHERRY_WOOD = REGISTRY.registerBlock(BlockFullNames.STRIPPED_CHERRY_WOOD, BlockID.STRIPPED_CHERRY_WOOD)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy STRIPPED_CRIMSON_HYPHAE = REGISTRY.registerBlock(BlockFullNames.STRIPPED_CRIMSON_HYPHAE, BlockID.STRIPPED_CRIMSON_HYPHAE)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy STRIPPED_CRIMSON_STEM = REGISTRY.registerBlock(BlockFullNames.STRIPPED_CRIMSON_STEM, BlockID.STRIPPED_CRIMSON_STEM)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy STRIPPED_DARK_OAK_LOG = REGISTRY.registerBlock(BlockFullNames.STRIPPED_DARK_OAK_LOG, BlockID.STRIPPED_DARK_OAK_LOG)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy STRIPPED_DARK_OAK_WOOD = REGISTRY.registerBlock(BlockFullNames.STRIPPED_DARK_OAK_WOOD, BlockID.STRIPPED_DARK_OAK_WOOD)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy STRIPPED_JUNGLE_LOG = REGISTRY.registerBlock(BlockFullNames.STRIPPED_JUNGLE_LOG, BlockID.STRIPPED_JUNGLE_LOG)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy STRIPPED_JUNGLE_WOOD = REGISTRY.registerBlock(BlockFullNames.STRIPPED_JUNGLE_WOOD, BlockID.STRIPPED_JUNGLE_WOOD)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy STRIPPED_MANGROVE_LOG = REGISTRY.registerBlock(BlockFullNames.STRIPPED_MANGROVE_LOG, BlockID.STRIPPED_MANGROVE_LOG)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy STRIPPED_MANGROVE_WOOD = REGISTRY.registerBlock(BlockFullNames.STRIPPED_MANGROVE_WOOD, BlockID.STRIPPED_MANGROVE_WOOD)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy STRIPPED_OAK_LOG = REGISTRY.registerBlock(BlockFullNames.STRIPPED_OAK_LOG, BlockID.STRIPPED_OAK_LOG)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy STRIPPED_OAK_WOOD = REGISTRY.registerBlock(BlockFullNames.STRIPPED_OAK_WOOD, BlockID.STRIPPED_OAK_WOOD)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy STRIPPED_PALE_OAK_LOG = REGISTRY.registerBlock(BlockFullNames.STRIPPED_PALE_OAK_LOG, BlockID.STRIPPED_PALE_OAK_LOG)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy STRIPPED_PALE_OAK_WOOD = REGISTRY.registerBlock(BlockFullNames.STRIPPED_PALE_OAK_WOOD, BlockID.STRIPPED_PALE_OAK_WOOD)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy STRIPPED_SPRUCE_LOG = REGISTRY.registerBlock(BlockFullNames.STRIPPED_SPRUCE_LOG, BlockID.STRIPPED_SPRUCE_LOG)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy STRIPPED_SPRUCE_WOOD = REGISTRY.registerBlock(BlockFullNames.STRIPPED_SPRUCE_WOOD, BlockID.STRIPPED_SPRUCE_WOOD)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy STRIPPED_WARPED_HYPHAE = REGISTRY.registerBlock(BlockFullNames.STRIPPED_WARPED_HYPHAE, BlockID.STRIPPED_WARPED_HYPHAE)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy STRIPPED_WARPED_STEM = REGISTRY.registerBlock(BlockFullNames.STRIPPED_WARPED_STEM, BlockID.STRIPPED_WARPED_STEM)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy STRUCTURE_BLOCK = REGISTRY.registerBlock(BlockFullNames.STRUCTURE_BLOCK, BlockID.STRUCTURE_BLOCK)
            .addState(BlockStates.STRUCTURE_BLOCK_TYPE);
    public static final BlockLegacy STRUCTURE_VOID = REGISTRY.registerBlock(BlockFullNames.STRUCTURE_VOID, BlockID.STRUCTURE_VOID);
    public static final BlockLegacy SUNFLOWER = REGISTRY.registerBlock(BlockFullNames.SUNFLOWER, BlockID.SUNFLOWER)
            .addState(BlockStates.UPPER_BLOCK_BIT);
    public static final BlockLegacy SUSPICIOUS_GRAVEL = REGISTRY.registerBlock(BlockFullNames.SUSPICIOUS_GRAVEL, BlockID.SUSPICIOUS_GRAVEL)
            .addState(BlockStates.HANGING)
            .addState(BlockStates.BRUSHED_PROGRESS);
    public static final BlockLegacy SUSPICIOUS_SAND = REGISTRY.registerBlock(BlockFullNames.SUSPICIOUS_SAND, BlockID.SUSPICIOUS_SAND)
            .addState(BlockStates.HANGING)
            .addState(BlockStates.BRUSHED_PROGRESS);
    public static final BlockLegacy SWEET_BERRY_BUSH = REGISTRY.registerBlock(BlockFullNames.SWEET_BERRY_BUSH, BlockID.SWEET_BERRY_BUSH)
            .addState(BlockStates.GROWTH);
    public static final BlockLegacy TALL_GRASS = REGISTRY.registerBlock(BlockFullNames.TALL_GRASS, BlockID.TALL_GRASS)
            .addState(BlockStates.UPPER_BLOCK_BIT);
    public static final BlockLegacy TARGET = REGISTRY.registerBlock(BlockFullNames.TARGET, BlockID.TARGET);
    public static final BlockLegacy TINTED_GLASS = REGISTRY.registerBlock(BlockFullNames.TINTED_GLASS, BlockID.TINTED_GLASS);
    public static final BlockLegacy TNT = REGISTRY.registerBlock(BlockFullNames.TNT, BlockID.TNT)
            .addState(BlockStates.EXPLODE_BIT);
    public static final BlockLegacy TORCH = REGISTRY.registerBlock(BlockFullNames.TORCH, BlockID.TORCH)
            .addState(BlockStates.TORCH_FACING_DIRECTION);
    public static final BlockLegacy TORCHFLOWER = REGISTRY.registerBlock(BlockFullNames.TORCHFLOWER, BlockID.TORCHFLOWER);
    public static final BlockLegacy TORCHFLOWER_CROP = REGISTRY.registerBlock(BlockFullNames.TORCHFLOWER_CROP, BlockID.TORCHFLOWER_CROP)
            .addState(BlockStates.GROWTH);
    public static final BlockLegacy TRAPDOOR = REGISTRY.registerBlock(BlockFullNames.TRAPDOOR, BlockID.TRAPDOOR)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT)
            .addState(BlockStates.OPEN_BIT);
    public static final BlockLegacy TRAPPED_CHEST = REGISTRY.registerBlock(BlockFullNames.TRAPPED_CHEST, BlockID.TRAPPED_CHEST)
            .addState(BlockStates.MINECRAFT_CARDINAL_DIRECTION);
    public static final BlockLegacy TRIAL_SPAWNER = REGISTRY.registerBlock(BlockFullNames.TRIAL_SPAWNER, BlockID.TRIAL_SPAWNER)
            .addState(BlockStates.TRIAL_SPAWNER_STATE)
            .addState(BlockStates.OMINOUS);
    public static final BlockLegacy TRIP_WIRE = REGISTRY.registerBlock(BlockFullNames.TRIP_WIRE, BlockID.TRIP_WIRE)
            .addState(BlockStates.POWERED_BIT)
            .addState(BlockStates.SUSPENDED_BIT)
            .addState(BlockStates.ATTACHED_BIT)
            .addState(BlockStates.DISARMED_BIT);
    public static final BlockLegacy TRIPWIRE_HOOK = REGISTRY.registerBlock(BlockFullNames.TRIPWIRE_HOOK, BlockID.TRIPWIRE_HOOK)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.ATTACHED_BIT)
            .addState(BlockStates.POWERED_BIT);
    public static final BlockLegacy TUBE_CORAL = REGISTRY.registerBlock(BlockFullNames.TUBE_CORAL, BlockID.TUBE_CORAL);
    public static final BlockLegacy TUBE_CORAL_BLOCK = REGISTRY.registerBlock(BlockFullNames.TUBE_CORAL_BLOCK, BlockID.TUBE_CORAL_BLOCK);
    public static final BlockLegacy TUBE_CORAL_FAN = REGISTRY.registerBlock(BlockFullNames.TUBE_CORAL_FAN, BlockID.TUBE_CORAL_FAN)
            .addState(BlockStates.CORAL_FAN_DIRECTION);
    public static final BlockLegacy TUBE_CORAL_WALL_FAN = REGISTRY.registerBlock(BlockFullNames.TUBE_CORAL_WALL_FAN, BlockID.TUBE_CORAL_WALL_FAN)
            .addState(BlockStates.CORAL_DIRECTION);
    public static final BlockLegacy TUFF = REGISTRY.registerBlock(BlockFullNames.TUFF, BlockID.TUFF);
    public static final BlockLegacy TUFF_BRICK_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.TUFF_BRICK_DOUBLE_SLAB, BlockID.TUFF_BRICK_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy TUFF_BRICK_SLAB = REGISTRY.registerBlock(BlockFullNames.TUFF_BRICK_SLAB, BlockID.TUFF_BRICK_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy TUFF_BRICK_STAIRS = REGISTRY.registerBlock(BlockFullNames.TUFF_BRICK_STAIRS, BlockID.TUFF_BRICK_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy TUFF_BRICK_WALL = REGISTRY.registerBlock(BlockFullNames.TUFF_BRICK_WALL, BlockID.TUFF_BRICK_WALL)
            .addState(BlockStates.WALL_POST_BIT)
            .addState(BlockStates.WALL_CONNECTION_TYPE_NORTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_EAST)
            .addState(BlockStates.WALL_CONNECTION_TYPE_SOUTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_WEST);
    public static final BlockLegacy TUFF_BRICKS = REGISTRY.registerBlock(BlockFullNames.TUFF_BRICKS, BlockID.TUFF_BRICKS);
    public static final BlockLegacy TUFF_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.TUFF_DOUBLE_SLAB, BlockID.TUFF_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy TUFF_SLAB = REGISTRY.registerBlock(BlockFullNames.TUFF_SLAB, BlockID.TUFF_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy TUFF_STAIRS = REGISTRY.registerBlock(BlockFullNames.TUFF_STAIRS, BlockID.TUFF_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy TUFF_WALL = REGISTRY.registerBlock(BlockFullNames.TUFF_WALL, BlockID.TUFF_WALL)
            .addState(BlockStates.WALL_POST_BIT)
            .addState(BlockStates.WALL_CONNECTION_TYPE_NORTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_EAST)
            .addState(BlockStates.WALL_CONNECTION_TYPE_SOUTH)
            .addState(BlockStates.WALL_CONNECTION_TYPE_WEST);
    public static final BlockLegacy TURTLE_EGG = REGISTRY.registerBlock(BlockFullNames.TURTLE_EGG, BlockID.TURTLE_EGG)
            .addState(BlockStates.TURTLE_EGG_COUNT)
            .addState(BlockStates.CRACKED_STATE);
    public static final BlockLegacy TWISTING_VINES = REGISTRY.registerBlock(BlockFullNames.TWISTING_VINES, BlockID.TWISTING_VINES)
            .addState(BlockStates.TWISTING_VINES_AGE);
    public static final BlockLegacy UNDERWATER_TNT = REGISTRY.registerBlock(BlockFullNames.UNDERWATER_TNT, BlockID.UNDERWATER_TNT)
            .addState(BlockStates.EXPLODE_BIT);
    public static final BlockLegacy UNDERWATER_TORCH = REGISTRY.registerBlock(BlockFullNames.UNDERWATER_TORCH, BlockID.UNDERWATER_TORCH)
            .addState(BlockStates.TORCH_FACING_DIRECTION);
    public static final BlockLegacy UNDYED_SHULKER_BOX = REGISTRY.registerBlock(BlockFullNames.UNDYED_SHULKER_BOX, BlockID.UNDYED_SHULKER_BOX);
    public static final BlockLegacy UNKNOWN = REGISTRY.registerBlock(BlockFullNames.UNKNOWN, BlockID.UNKNOWN);
    public static final BlockLegacy UNLIT_REDSTONE_TORCH = REGISTRY.registerBlock(BlockFullNames.UNLIT_REDSTONE_TORCH, BlockID.UNLIT_REDSTONE_TORCH)
            .addState(BlockStates.TORCH_FACING_DIRECTION);
    public static final BlockLegacy UNPOWERED_COMPARATOR = REGISTRY.registerBlock(BlockFullNames.UNPOWERED_COMPARATOR, BlockID.UNPOWERED_COMPARATOR)
            .addState(BlockStates.MINECRAFT_CARDINAL_DIRECTION)
            .addState(BlockStates.OUTPUT_SUBTRACT_BIT)
            .addState(BlockStates.OUTPUT_LIT_BIT);
    public static final BlockLegacy UNPOWERED_REPEATER = REGISTRY.registerBlock(BlockFullNames.UNPOWERED_REPEATER, BlockID.UNPOWERED_REPEATER)
            .addState(BlockStates.MINECRAFT_CARDINAL_DIRECTION)
            .addState(BlockStates.REPEATER_DELAY);
    public static final BlockLegacy VAULT = REGISTRY.registerBlock(BlockFullNames.VAULT, BlockID.VAULT)
            .addState(BlockStates.MINECRAFT_CARDINAL_DIRECTION)
            .addState(BlockStates.VAULT_STATE)
            .addState(BlockStates.OMINOUS);
    public static final BlockLegacy VERDANT_FROGLIGHT = REGISTRY.registerBlock(BlockFullNames.VERDANT_FROGLIGHT, BlockID.VERDANT_FROGLIGHT)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy VINE = REGISTRY.registerBlock(BlockFullNames.VINE, BlockID.VINE)
            .addState(BlockStates.VINE_DIRECTION_BITS);
    public static final BlockLegacy WALL_BANNER = REGISTRY.registerBlock(BlockFullNames.WALL_BANNER, BlockID.WALL_BANNER)
            .addState(BlockStates.FACING_DIRECTION);
    public static final BlockLegacy WALL_SIGN = REGISTRY.registerBlock(BlockFullNames.WALL_SIGN, BlockID.WALL_SIGN)
            .addState(BlockStates.FACING_DIRECTION);
    public static final BlockLegacy WARPED_BUTTON = REGISTRY.registerBlock(BlockFullNames.WARPED_BUTTON, BlockID.WARPED_BUTTON)
            .addState(BlockStates.FACING_DIRECTION)
            .addState(BlockStates.BUTTON_PRESSED_BIT);
    public static final BlockLegacy WARPED_DOOR = REGISTRY.registerBlock(BlockFullNames.WARPED_DOOR, BlockID.WARPED_DOOR)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.OPEN_BIT)
            .addState(BlockStates.UPPER_BLOCK_BIT)
            .addState(BlockStates.DOOR_HINGE_BIT);
    public static final BlockLegacy WARPED_DOUBLE_SLAB = REGISTRY.registerBlock(BlockFullNames.WARPED_DOUBLE_SLAB, BlockID.WARPED_DOUBLE_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy WARPED_FENCE = REGISTRY.registerBlock(BlockFullNames.WARPED_FENCE, BlockID.WARPED_FENCE);
    public static final BlockLegacy WARPED_FENCE_GATE = REGISTRY.registerBlock(BlockFullNames.WARPED_FENCE_GATE, BlockID.WARPED_FENCE_GATE)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.OPEN_BIT)
            .addState(BlockStates.IN_WALL_BIT);
    public static final BlockLegacy WARPED_FUNGUS = REGISTRY.registerBlock(BlockFullNames.WARPED_FUNGUS, BlockID.WARPED_FUNGUS);
    public static final BlockLegacy WARPED_HANGING_SIGN = REGISTRY.registerBlock(BlockFullNames.WARPED_HANGING_SIGN, BlockID.WARPED_HANGING_SIGN)
            .addState(BlockStates.FACING_DIRECTION)
            .addState(BlockStates.GROUND_SIGN_DIRECTION)
            .addState(BlockStates.HANGING)
            .addState(BlockStates.ATTACHED_BIT);
    public static final BlockLegacy WARPED_HYPHAE = REGISTRY.registerBlock(BlockFullNames.WARPED_HYPHAE, BlockID.WARPED_HYPHAE)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy WARPED_NYLIUM = REGISTRY.registerBlock(BlockFullNames.WARPED_NYLIUM, BlockID.WARPED_NYLIUM);
    public static final BlockLegacy WARPED_PLANKS = REGISTRY.registerBlock(BlockFullNames.WARPED_PLANKS, BlockID.WARPED_PLANKS);
    public static final BlockLegacy WARPED_PRESSURE_PLATE = REGISTRY.registerBlock(BlockFullNames.WARPED_PRESSURE_PLATE, BlockID.WARPED_PRESSURE_PLATE)
            .addState(BlockStates.REDSTONE_SIGNAL);
    public static final BlockLegacy WARPED_ROOTS = REGISTRY.registerBlock(BlockFullNames.WARPED_ROOTS, BlockID.WARPED_ROOTS);
    public static final BlockLegacy WARPED_SLAB = REGISTRY.registerBlock(BlockFullNames.WARPED_SLAB, BlockID.WARPED_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy WARPED_STAIRS = REGISTRY.registerBlock(BlockFullNames.WARPED_STAIRS, BlockID.WARPED_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy WARPED_STANDING_SIGN = REGISTRY.registerBlock(BlockFullNames.WARPED_STANDING_SIGN, BlockID.WARPED_STANDING_SIGN)
            .addState(BlockStates.GROUND_SIGN_DIRECTION);
    public static final BlockLegacy WARPED_STEM = REGISTRY.registerBlock(BlockFullNames.WARPED_STEM, BlockID.WARPED_STEM)
            .addState(BlockStates.PILLAR_AXIS);
    public static final BlockLegacy WARPED_TRAPDOOR = REGISTRY.registerBlock(BlockFullNames.WARPED_TRAPDOOR, BlockID.WARPED_TRAPDOOR)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT)
            .addState(BlockStates.OPEN_BIT);
    public static final BlockLegacy WARPED_WALL_SIGN = REGISTRY.registerBlock(BlockFullNames.WARPED_WALL_SIGN, BlockID.WARPED_WALL_SIGN)
            .addState(BlockStates.FACING_DIRECTION);
    public static final BlockLegacy WARPED_WART_BLOCK = REGISTRY.registerBlock(BlockFullNames.WARPED_WART_BLOCK, BlockID.WARPED_WART_BLOCK);
    public static final BlockLegacy WATER = REGISTRY.registerBlock(BlockFullNames.WATER, BlockID.WATER)
            .addState(BlockStates.LIQUID_DEPTH);
    public static final BlockLegacy WATERLILY = REGISTRY.registerBlock(BlockFullNames.WATERLILY, BlockID.WATERLILY);
    public static final BlockLegacy WAXED_CHISELED_COPPER = REGISTRY.registerBlock(BlockFullNames.WAXED_CHISELED_COPPER, BlockID.WAXED_CHISELED_COPPER);
    public static final BlockLegacy WAXED_COPPER = REGISTRY.registerBlock(BlockFullNames.WAXED_COPPER, BlockID.WAXED_COPPER);
    public static final BlockLegacy WAXED_COPPER_BULB = REGISTRY.registerBlock(BlockFullNames.WAXED_COPPER_BULB, BlockID.WAXED_COPPER_BULB)
            .addState(BlockStates.LIT)
            .addState(BlockStates.POWERED_BIT);
    public static final BlockLegacy WAXED_COPPER_DOOR = REGISTRY.registerBlock(BlockFullNames.WAXED_COPPER_DOOR, BlockID.WAXED_COPPER_DOOR)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.OPEN_BIT)
            .addState(BlockStates.UPPER_BLOCK_BIT)
            .addState(BlockStates.DOOR_HINGE_BIT);
    public static final BlockLegacy WAXED_COPPER_GRATE = REGISTRY.registerBlock(BlockFullNames.WAXED_COPPER_GRATE, BlockID.WAXED_COPPER_GRATE);
    public static final BlockLegacy WAXED_COPPER_TRAPDOOR = REGISTRY.registerBlock(BlockFullNames.WAXED_COPPER_TRAPDOOR, BlockID.WAXED_COPPER_TRAPDOOR)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT)
            .addState(BlockStates.OPEN_BIT);
    public static final BlockLegacy WAXED_CUT_COPPER = REGISTRY.registerBlock(BlockFullNames.WAXED_CUT_COPPER, BlockID.WAXED_CUT_COPPER);
    public static final BlockLegacy WAXED_CUT_COPPER_SLAB = REGISTRY.registerBlock(BlockFullNames.WAXED_CUT_COPPER_SLAB, BlockID.WAXED_CUT_COPPER_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy WAXED_CUT_COPPER_STAIRS = REGISTRY.registerBlock(BlockFullNames.WAXED_CUT_COPPER_STAIRS, BlockID.WAXED_CUT_COPPER_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy WAXED_DOUBLE_CUT_COPPER_SLAB = REGISTRY.registerBlock(BlockFullNames.WAXED_DOUBLE_CUT_COPPER_SLAB, BlockID.WAXED_DOUBLE_CUT_COPPER_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy WAXED_EXPOSED_CHISELED_COPPER = REGISTRY.registerBlock(BlockFullNames.WAXED_EXPOSED_CHISELED_COPPER, BlockID.WAXED_EXPOSED_CHISELED_COPPER);
    public static final BlockLegacy WAXED_EXPOSED_COPPER = REGISTRY.registerBlock(BlockFullNames.WAXED_EXPOSED_COPPER, BlockID.WAXED_EXPOSED_COPPER);
    public static final BlockLegacy WAXED_EXPOSED_COPPER_BULB = REGISTRY.registerBlock(BlockFullNames.WAXED_EXPOSED_COPPER_BULB, BlockID.WAXED_EXPOSED_COPPER_BULB)
            .addState(BlockStates.LIT)
            .addState(BlockStates.POWERED_BIT);
    public static final BlockLegacy WAXED_EXPOSED_COPPER_DOOR = REGISTRY.registerBlock(BlockFullNames.WAXED_EXPOSED_COPPER_DOOR, BlockID.WAXED_EXPOSED_COPPER_DOOR)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.OPEN_BIT)
            .addState(BlockStates.UPPER_BLOCK_BIT)
            .addState(BlockStates.DOOR_HINGE_BIT);
    public static final BlockLegacy WAXED_EXPOSED_COPPER_GRATE = REGISTRY.registerBlock(BlockFullNames.WAXED_EXPOSED_COPPER_GRATE, BlockID.WAXED_EXPOSED_COPPER_GRATE);
    public static final BlockLegacy WAXED_EXPOSED_COPPER_TRAPDOOR = REGISTRY.registerBlock(BlockFullNames.WAXED_EXPOSED_COPPER_TRAPDOOR, BlockID.WAXED_EXPOSED_COPPER_TRAPDOOR)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT)
            .addState(BlockStates.OPEN_BIT);
    public static final BlockLegacy WAXED_EXPOSED_CUT_COPPER = REGISTRY.registerBlock(BlockFullNames.WAXED_EXPOSED_CUT_COPPER, BlockID.WAXED_EXPOSED_CUT_COPPER);
    public static final BlockLegacy WAXED_EXPOSED_CUT_COPPER_SLAB = REGISTRY.registerBlock(BlockFullNames.WAXED_EXPOSED_CUT_COPPER_SLAB, BlockID.WAXED_EXPOSED_CUT_COPPER_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy WAXED_EXPOSED_CUT_COPPER_STAIRS = REGISTRY.registerBlock(BlockFullNames.WAXED_EXPOSED_CUT_COPPER_STAIRS, BlockID.WAXED_EXPOSED_CUT_COPPER_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy WAXED_EXPOSED_DOUBLE_CUT_COPPER_SLAB = REGISTRY.registerBlock(BlockFullNames.WAXED_EXPOSED_DOUBLE_CUT_COPPER_SLAB, BlockID.WAXED_EXPOSED_DOUBLE_CUT_COPPER_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy WAXED_OXIDIZED_CHISELED_COPPER = REGISTRY.registerBlock(BlockFullNames.WAXED_OXIDIZED_CHISELED_COPPER, BlockID.WAXED_OXIDIZED_CHISELED_COPPER);
    public static final BlockLegacy WAXED_OXIDIZED_COPPER = REGISTRY.registerBlock(BlockFullNames.WAXED_OXIDIZED_COPPER, BlockID.WAXED_OXIDIZED_COPPER);
    public static final BlockLegacy WAXED_OXIDIZED_COPPER_BULB = REGISTRY.registerBlock(BlockFullNames.WAXED_OXIDIZED_COPPER_BULB, BlockID.WAXED_OXIDIZED_COPPER_BULB)
            .addState(BlockStates.LIT)
            .addState(BlockStates.POWERED_BIT);
    public static final BlockLegacy WAXED_OXIDIZED_COPPER_DOOR = REGISTRY.registerBlock(BlockFullNames.WAXED_OXIDIZED_COPPER_DOOR, BlockID.WAXED_OXIDIZED_COPPER_DOOR)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.OPEN_BIT)
            .addState(BlockStates.UPPER_BLOCK_BIT)
            .addState(BlockStates.DOOR_HINGE_BIT);
    public static final BlockLegacy WAXED_OXIDIZED_COPPER_GRATE = REGISTRY.registerBlock(BlockFullNames.WAXED_OXIDIZED_COPPER_GRATE, BlockID.WAXED_OXIDIZED_COPPER_GRATE);
    public static final BlockLegacy WAXED_OXIDIZED_COPPER_TRAPDOOR = REGISTRY.registerBlock(BlockFullNames.WAXED_OXIDIZED_COPPER_TRAPDOOR, BlockID.WAXED_OXIDIZED_COPPER_TRAPDOOR)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT)
            .addState(BlockStates.OPEN_BIT);
    public static final BlockLegacy WAXED_OXIDIZED_CUT_COPPER = REGISTRY.registerBlock(BlockFullNames.WAXED_OXIDIZED_CUT_COPPER, BlockID.WAXED_OXIDIZED_CUT_COPPER);
    public static final BlockLegacy WAXED_OXIDIZED_CUT_COPPER_SLAB = REGISTRY.registerBlock(BlockFullNames.WAXED_OXIDIZED_CUT_COPPER_SLAB, BlockID.WAXED_OXIDIZED_CUT_COPPER_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy WAXED_OXIDIZED_CUT_COPPER_STAIRS = REGISTRY.registerBlock(BlockFullNames.WAXED_OXIDIZED_CUT_COPPER_STAIRS, BlockID.WAXED_OXIDIZED_CUT_COPPER_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy WAXED_OXIDIZED_DOUBLE_CUT_COPPER_SLAB = REGISTRY.registerBlock(BlockFullNames.WAXED_OXIDIZED_DOUBLE_CUT_COPPER_SLAB, BlockID.WAXED_OXIDIZED_DOUBLE_CUT_COPPER_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy WAXED_WEATHERED_CHISELED_COPPER = REGISTRY.registerBlock(BlockFullNames.WAXED_WEATHERED_CHISELED_COPPER, BlockID.WAXED_WEATHERED_CHISELED_COPPER);
    public static final BlockLegacy WAXED_WEATHERED_COPPER = REGISTRY.registerBlock(BlockFullNames.WAXED_WEATHERED_COPPER, BlockID.WAXED_WEATHERED_COPPER);
    public static final BlockLegacy WAXED_WEATHERED_COPPER_BULB = REGISTRY.registerBlock(BlockFullNames.WAXED_WEATHERED_COPPER_BULB, BlockID.WAXED_WEATHERED_COPPER_BULB)
            .addState(BlockStates.LIT)
            .addState(BlockStates.POWERED_BIT);
    public static final BlockLegacy WAXED_WEATHERED_COPPER_DOOR = REGISTRY.registerBlock(BlockFullNames.WAXED_WEATHERED_COPPER_DOOR, BlockID.WAXED_WEATHERED_COPPER_DOOR)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.OPEN_BIT)
            .addState(BlockStates.UPPER_BLOCK_BIT)
            .addState(BlockStates.DOOR_HINGE_BIT);
    public static final BlockLegacy WAXED_WEATHERED_COPPER_GRATE = REGISTRY.registerBlock(BlockFullNames.WAXED_WEATHERED_COPPER_GRATE, BlockID.WAXED_WEATHERED_COPPER_GRATE);
    public static final BlockLegacy WAXED_WEATHERED_COPPER_TRAPDOOR = REGISTRY.registerBlock(BlockFullNames.WAXED_WEATHERED_COPPER_TRAPDOOR, BlockID.WAXED_WEATHERED_COPPER_TRAPDOOR)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT)
            .addState(BlockStates.OPEN_BIT);
    public static final BlockLegacy WAXED_WEATHERED_CUT_COPPER = REGISTRY.registerBlock(BlockFullNames.WAXED_WEATHERED_CUT_COPPER, BlockID.WAXED_WEATHERED_CUT_COPPER);
    public static final BlockLegacy WAXED_WEATHERED_CUT_COPPER_SLAB = REGISTRY.registerBlock(BlockFullNames.WAXED_WEATHERED_CUT_COPPER_SLAB, BlockID.WAXED_WEATHERED_CUT_COPPER_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy WAXED_WEATHERED_CUT_COPPER_STAIRS = REGISTRY.registerBlock(BlockFullNames.WAXED_WEATHERED_CUT_COPPER_STAIRS, BlockID.WAXED_WEATHERED_CUT_COPPER_STAIRS)
            .addState(BlockStates.WEIRDO_DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT);
    public static final BlockLegacy WAXED_WEATHERED_DOUBLE_CUT_COPPER_SLAB = REGISTRY.registerBlock(BlockFullNames.WAXED_WEATHERED_DOUBLE_CUT_COPPER_SLAB, BlockID.WAXED_WEATHERED_DOUBLE_CUT_COPPER_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy WEATHERED_CHISELED_COPPER = REGISTRY.registerBlock(BlockFullNames.WEATHERED_CHISELED_COPPER, BlockID.WEATHERED_CHISELED_COPPER);
    public static final BlockLegacy WEATHERED_COPPER = REGISTRY.registerBlock(BlockFullNames.WEATHERED_COPPER, BlockID.WEATHERED_COPPER);
    public static final BlockLegacy WEATHERED_COPPER_BULB = REGISTRY.registerBlock(BlockFullNames.WEATHERED_COPPER_BULB, BlockID.WEATHERED_COPPER_BULB)
            .addState(BlockStates.LIT)
            .addState(BlockStates.POWERED_BIT);
    public static final BlockLegacy WEATHERED_COPPER_DOOR = REGISTRY.registerBlock(BlockFullNames.WEATHERED_COPPER_DOOR, BlockID.WEATHERED_COPPER_DOOR)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.OPEN_BIT)
            .addState(BlockStates.UPPER_BLOCK_BIT)
            .addState(BlockStates.DOOR_HINGE_BIT);
    public static final BlockLegacy WEATHERED_COPPER_GRATE = REGISTRY.registerBlock(BlockFullNames.WEATHERED_COPPER_GRATE, BlockID.WEATHERED_COPPER_GRATE);
    public static final BlockLegacy WEATHERED_COPPER_TRAPDOOR = REGISTRY.registerBlock(BlockFullNames.WEATHERED_COPPER_TRAPDOOR, BlockID.WEATHERED_COPPER_TRAPDOOR)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.UPSIDE_DOWN_BIT)
            .addState(BlockStates.OPEN_BIT);
    public static final BlockLegacy WEATHERED_CUT_COPPER = REGISTRY.registerBlock(BlockFullNames.WEATHERED_CUT_COPPER, BlockID.WEATHERED_CUT_COPPER);
    public static final BlockLegacy WEATHERED_CUT_COPPER_SLAB = REGISTRY.registerBlock(BlockFullNames.WEATHERED_CUT_COPPER_SLAB, BlockID.WEATHERED_CUT_COPPER_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy WEATHERED_CUT_COPPER_STAIRS = REGISTRY.registerBlock(BlockFullNames.WEATHERED_CUT_COPPER_STAIRS, BlockID.WEATHERED_CUT_COPPER_STAIRS)
            .addState(BlockStates.UPSIDE_DOWN_BIT)
            .addState(BlockStates.WEIRDO_DIRECTION);
    public static final BlockLegacy WEATHERED_DOUBLE_CUT_COPPER_SLAB = REGISTRY.registerBlock(BlockFullNames.WEATHERED_DOUBLE_CUT_COPPER_SLAB, BlockID.WEATHERED_DOUBLE_CUT_COPPER_SLAB)
            .addState(BlockStates.MINECRAFT_VERTICAL_HALF);
    public static final BlockLegacy WEB = REGISTRY.registerBlock(BlockFullNames.WEB, BlockID.WEB);
    public static final BlockLegacy WEEPING_VINES = REGISTRY.registerBlock(BlockFullNames.WEEPING_VINES, BlockID.WEEPING_VINES)
            .addState(BlockStates.WEEPING_VINES_AGE);
    public static final BlockLegacy WET_SPONGE = REGISTRY.registerBlock(BlockFullNames.WET_SPONGE, BlockID.WET_SPONGE);
    public static final BlockLegacy WHEAT = REGISTRY.registerBlock(BlockFullNames.WHEAT, BlockID.WHEAT)
            .addState(BlockStates.GROWTH);
    public static final BlockLegacy WHITE_CANDLE = REGISTRY.registerBlock(BlockFullNames.WHITE_CANDLE, BlockID.WHITE_CANDLE)
            .addState(BlockStates.LIT)
            .addState(BlockStates.CANDLES);
    public static final BlockLegacy WHITE_CANDLE_CAKE = REGISTRY.registerBlock(BlockFullNames.WHITE_CANDLE_CAKE, BlockID.WHITE_CANDLE_CAKE)
            .addState(BlockStates.LIT);
    public static final BlockLegacy WHITE_CARPET = REGISTRY.registerBlock(BlockFullNames.WHITE_CARPET, BlockID.WHITE_CARPET);
    public static final BlockLegacy WHITE_CONCRETE = REGISTRY.registerBlock(BlockFullNames.WHITE_CONCRETE, BlockID.WHITE_CONCRETE);
    public static final BlockLegacy WHITE_CONCRETE_POWDER = REGISTRY.registerBlock(BlockFullNames.WHITE_CONCRETE_POWDER, BlockID.WHITE_CONCRETE_POWDER);
    public static final BlockLegacy WHITE_GLAZED_TERRACOTTA = REGISTRY.registerBlock(BlockFullNames.WHITE_GLAZED_TERRACOTTA, BlockID.WHITE_GLAZED_TERRACOTTA)
            .addState(BlockStates.FACING_DIRECTION);
    public static final BlockLegacy WHITE_SHULKER_BOX = REGISTRY.registerBlock(BlockFullNames.WHITE_SHULKER_BOX, BlockID.WHITE_SHULKER_BOX);
    public static final BlockLegacy WHITE_STAINED_GLASS = REGISTRY.registerBlock(BlockFullNames.WHITE_STAINED_GLASS, BlockID.WHITE_STAINED_GLASS);
    public static final BlockLegacy WHITE_STAINED_GLASS_PANE = REGISTRY.registerBlock(BlockFullNames.WHITE_STAINED_GLASS_PANE, BlockID.WHITE_STAINED_GLASS_PANE);
    public static final BlockLegacy WHITE_TERRACOTTA = REGISTRY.registerBlock(BlockFullNames.WHITE_TERRACOTTA, BlockID.WHITE_TERRACOTTA);
    public static final BlockLegacy WHITE_TULIP = REGISTRY.registerBlock(BlockFullNames.WHITE_TULIP, BlockID.WHITE_TULIP);
    public static final BlockLegacy WHITE_WOOL = REGISTRY.registerBlock(BlockFullNames.WHITE_WOOL, BlockID.WHITE_WOOL);
    public static final BlockLegacy WITHER_ROSE = REGISTRY.registerBlock(BlockFullNames.WITHER_ROSE, BlockID.WITHER_ROSE);
    public static final BlockLegacy WITHER_SKELETON_SKULL = REGISTRY.registerBlock(BlockFullNames.WITHER_SKELETON_SKULL, BlockID.WITHER_SKELETON_SKULL)
            .addState(BlockStates.FACING_DIRECTION);
    public static final BlockLegacy WOODEN_BUTTON = REGISTRY.registerBlock(BlockFullNames.WOODEN_BUTTON, BlockID.WOODEN_BUTTON)
            .addState(BlockStates.FACING_DIRECTION)
            .addState(BlockStates.BUTTON_PRESSED_BIT);
    public static final BlockLegacy WOODEN_DOOR = REGISTRY.registerBlock(BlockFullNames.WOODEN_DOOR, BlockID.WOODEN_DOOR)
            .addState(BlockStates.DIRECTION)
            .addState(BlockStates.OPEN_BIT)
            .addState(BlockStates.UPPER_BLOCK_BIT)
            .addState(BlockStates.DOOR_HINGE_BIT);
    public static final BlockLegacy WOODEN_PRESSURE_PLATE = REGISTRY.registerBlock(BlockFullNames.WOODEN_PRESSURE_PLATE, BlockID.WOODEN_PRESSURE_PLATE)
            .addState(BlockStates.REDSTONE_SIGNAL);
    public static final BlockLegacy YELLOW_CANDLE = REGISTRY.registerBlock(BlockFullNames.YELLOW_CANDLE, BlockID.YELLOW_CANDLE)
            .addState(BlockStates.CANDLES)
            .addState(BlockStates.LIT);
    public static final BlockLegacy YELLOW_CANDLE_CAKE = REGISTRY.registerBlock(BlockFullNames.YELLOW_CANDLE_CAKE, BlockID.YELLOW_CANDLE_CAKE)
            .addState(BlockStates.LIT);
    public static final BlockLegacy YELLOW_CARPET = REGISTRY.registerBlock(BlockFullNames.YELLOW_CARPET, BlockID.YELLOW_CARPET);
    public static final BlockLegacy YELLOW_CONCRETE = REGISTRY.registerBlock(BlockFullNames.YELLOW_CONCRETE, BlockID.YELLOW_CONCRETE);
    public static final BlockLegacy YELLOW_CONCRETE_POWDER = REGISTRY.registerBlock(BlockFullNames.YELLOW_CONCRETE_POWDER, BlockID.YELLOW_CONCRETE_POWDER);
    public static final BlockLegacy YELLOW_GLAZED_TERRACOTTA = REGISTRY.registerBlock(BlockFullNames.YELLOW_GLAZED_TERRACOTTA, BlockID.YELLOW_GLAZED_TERRACOTTA)
            .addState(BlockStates.FACING_DIRECTION);
    public static final BlockLegacy YELLOW_SHULKER_BOX = REGISTRY.registerBlock(BlockFullNames.YELLOW_SHULKER_BOX, BlockID.YELLOW_SHULKER_BOX);
    public static final BlockLegacy YELLOW_STAINED_GLASS = REGISTRY.registerBlock(BlockFullNames.YELLOW_STAINED_GLASS, BlockID.YELLOW_STAINED_GLASS);
    public static final BlockLegacy YELLOW_STAINED_GLASS_PANE = REGISTRY.registerBlock(BlockFullNames.YELLOW_STAINED_GLASS_PANE, BlockID.YELLOW_STAINED_GLASS_PANE);
    public static final BlockLegacy YELLOW_TERRACOTTA = REGISTRY.registerBlock(BlockFullNames.YELLOW_TERRACOTTA, BlockID.YELLOW_TERRACOTTA);
    public static final BlockLegacy YELLOW_WOOL = REGISTRY.registerBlock(BlockFullNames.YELLOW_WOOL, BlockID.YELLOW_WOOL);
    public static final BlockLegacy ZOMBIE_HEAD = REGISTRY.registerBlock(BlockFullNames.ZOMBIE_HEAD, BlockID.ZOMBIE_HEAD)
            .addState(BlockStates.FACING_DIRECTION);

    static {
        REGISTRY.createPermutations();
    }

    public static void init() {
    }

    public static BlockRegistry getBlockRegistry() {
        return REGISTRY;
    }
}
