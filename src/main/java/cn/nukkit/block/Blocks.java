package cn.nukkit.block;

import cn.nukkit.GameVersion;
import cn.nukkit.block.edu.*;
import cn.nukkit.block.state.BlockTypes;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.item.ItemBlockNames;
import cn.nukkit.item.Items;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.DyeColor;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntFunction;

import static cn.nukkit.GameVersion.*;
import static cn.nukkit.block.BlockID.*;

@Log4j2
public final class Blocks {
    static final Block AIR = new BlockAir();

    private static final Object2IntMap<String> BLOCK_NAME_TO_ID = new Object2IntOpenHashMap<>();
    private static final Object2IntMap<String> BLOCK_FULL_NAME_TO_ID = new Object2IntOpenHashMap<>();
    private static final String[] ID_TO_BLOCK_NAME = new String[Block.BLOCK_ID_COUNT];
    private static final String[] ID_TO_BLOCK_FULL_NAME = new String[Block.BLOCK_ID_COUNT];
    private static final Map<String, String> BLOCK_ALIASES_MAP = new Object2ObjectOpenHashMap<>();

    private static final Object2IntMap<String> ITEM_NAME_TO_ID = new Object2IntOpenHashMap<>();
    private static final Object2IntMap<String> ITEM_FULL_NAME_TO_ID = new Object2IntOpenHashMap<>();
    private static final String[] ID_TO_ITEM_NAME = new String[Block.BLOCK_ID_COUNT];
    private static final String[] ID_TO_ITEM_FULL_NAME = new String[Block.BLOCK_ID_COUNT];
    private static final Map<String, String> ITEM_ALIASES_MAP = new Object2ObjectOpenHashMap<>();

    private static final Object2IntMap<String> COMPLEX_ALIASES_MAP = new Object2IntOpenHashMap<>();

    private static final Map<String, IntList> LEGACY_ALIASES_MAP = new Object2ObjectOpenHashMap<>();
    private static final Object2IntMap<String> DEPRECATED_LEGACY_ALIASES_MAP = new Object2IntOpenHashMap<>();

    private static final AtomicInteger CUSTOM_BLOCK_ID_ALLOCATOR = new AtomicInteger(Block.CUSTOM_BLOCK_FIRST_ID_NEW);

    static {
        BLOCK_NAME_TO_ID.defaultReturnValue(-1);
        BLOCK_FULL_NAME_TO_ID.defaultReturnValue(-1);
        ITEM_NAME_TO_ID.defaultReturnValue(-1);
        ITEM_FULL_NAME_TO_ID.defaultReturnValue(-1);
        COMPLEX_ALIASES_MAP.defaultReturnValue(-1);
    }

    static void registerVanillaBlocks() {
        BlockTypes.init();

        registerBlock(BlockNames.AIR, ItemBlockNames.AIR, BlockID.AIR, BlockAir.class);
        registerBlock(BlockNames.STONE, ItemBlockNames.STONE, STONE, BlockStone.class);
        registerBlock(BlockNames.GRASS, ItemBlockNames.GRASS, GRASS_BLOCK, BlockGrass.class);
        registerBlock(BlockNames.DIRT, ItemBlockNames.DIRT, DIRT, BlockDirt.class);
        registerBlock(BlockNames.COBBLESTONE, ItemBlockNames.COBBLESTONE, COBBLESTONE, BlockCobblestone.class);
        registerBlock(BlockNames.PLANKS, ItemBlockNames.PLANKS, PLANKS, BlockPlanks.class);
        registerBlock(BlockNames.SAPLING, ItemBlockNames.SAPLING, SAPLING, BlockSapling.class);
        registerBlock(BlockNames.BEDROCK, ItemBlockNames.BEDROCK, BEDROCK, BlockBedrock.class);
        registerBlock(BlockNames.FLOWING_WATER, ItemBlockNames.FLOWING_WATER, FLOWING_WATER, BlockWater.class);
        registerBlock(BlockNames.WATER, ItemBlockNames.WATER, WATER, BlockWaterStill.class);
        registerBlock(BlockNames.FLOWING_LAVA, ItemBlockNames.FLOWING_LAVA, FLOWING_LAVA, BlockLava.class);
        registerBlock(BlockNames.LAVA, ItemBlockNames.LAVA, LAVA, BlockLavaStill.class);
        registerBlock(BlockNames.SAND, ItemBlockNames.SAND, SAND, BlockSand.class);
        registerBlock(BlockNames.GRAVEL, ItemBlockNames.GRAVEL, GRAVEL, BlockGravel.class);
        registerBlock(BlockNames.GOLD_ORE, ItemBlockNames.GOLD_ORE, GOLD_ORE, BlockOreGold.class);
        registerBlock(BlockNames.IRON_ORE, ItemBlockNames.IRON_ORE, IRON_ORE, BlockOreIron.class);
        registerBlock(BlockNames.COAL_ORE, ItemBlockNames.COAL_ORE, COAL_ORE, BlockOreCoal.class);
        //registerBlock(BlockNames.LOG, ItemBlockNames.LOG, LOG, BlockWood.class);
        registerBlock(BlockNames.LEAVES, ItemBlockNames.LEAVES, LEAVES, BlockLeaves.class);
        registerBlock(BlockNames.SPONGE, ItemBlockNames.SPONGE, SPONGE, BlockSponge.class);
        registerBlock(BlockNames.GLASS, ItemBlockNames.GLASS, GLASS, BlockGlass.class);
        registerBlock(BlockNames.LAPIS_ORE, ItemBlockNames.LAPIS_ORE, LAPIS_ORE, BlockOreLapis.class);
        registerBlock(BlockNames.LAPIS_BLOCK, ItemBlockNames.LAPIS_BLOCK, LAPIS_BLOCK, BlockLapis.class);
        registerBlock(BlockNames.DISPENSER, ItemBlockNames.DISPENSER, DISPENSER, BlockDispenser.class);
        registerBlock(BlockNames.SANDSTONE, ItemBlockNames.SANDSTONE, SANDSTONE, BlockSandstone.class);
        registerBlock(BlockNames.NOTEBLOCK, ItemBlockNames.NOTEBLOCK, NOTEBLOCK, BlockNoteblock.class);
        registerBlock(BlockNames.BED, ItemBlockNames.ITEM_BED, BLOCK_BED, BlockBed.class);
        registerBlock(BlockNames.GOLDEN_RAIL, ItemBlockNames.GOLDEN_RAIL, GOLDEN_RAIL, BlockRailPowered.class);
        registerBlock(BlockNames.DETECTOR_RAIL, ItemBlockNames.DETECTOR_RAIL, DETECTOR_RAIL, BlockRailDetector.class);
        registerBlock(BlockNames.STICKY_PISTON, ItemBlockNames.STICKY_PISTON, STICKY_PISTON, BlockPistonSticky.class);
        registerBlock(BlockNames.WEB, ItemBlockNames.WEB, WEB, BlockCobweb.class);
        registerBlock(BlockNames.TALLGRASS, ItemBlockNames.TALLGRASS, SHORT_GRASS, BlockTallGrass.class);
        registerBlock(BlockNames.DEADBUSH, ItemBlockNames.DEADBUSH, DEADBUSH, BlockDeadBush.class);
        registerBlock(BlockNames.PISTON, ItemBlockNames.PISTON, PISTON, BlockPiston.class);
        registerBlock(BlockNames.PISTON_ARM_COLLISION, ItemBlockNames.PISTONARMCOLLISION, PISTON_ARM_COLLISION, BlockPistonHead.class);
        //registerBlock(BlockNames.WOOL, ItemBlockNames.WOOL, WOOL, BlockWool.class);
        registerBlock(BlockNames.YELLOW_FLOWER, ItemBlockNames.YELLOW_FLOWER, DANDELION, BlockDandelion.class);
        registerBlock(BlockNames.RED_FLOWER, ItemBlockNames.RED_FLOWER, RED_FLOWER, BlockFlower.class);
        registerBlock(BlockNames.BROWN_MUSHROOM, ItemBlockNames.BROWN_MUSHROOM, BROWN_MUSHROOM, BlockMushroomBrown.class);
        registerBlock(BlockNames.RED_MUSHROOM, ItemBlockNames.RED_MUSHROOM, RED_MUSHROOM, BlockMushroomRed.class);
        registerBlock(BlockNames.GOLD_BLOCK, ItemBlockNames.GOLD_BLOCK, GOLD_BLOCK, BlockGold.class);
        registerBlock(BlockNames.IRON_BLOCK, ItemBlockNames.IRON_BLOCK, IRON_BLOCK, BlockIron.class);
        registerBlock(BlockNames.DOUBLE_STONE_BLOCK_SLAB, ItemBlockNames.DOUBLE_STONE_BLOCK_SLAB, DOUBLE_STONE_BLOCK_SLAB, BlockDoubleSlabStone.class);
        registerBlock(BlockNames.STONE_BLOCK_SLAB, ItemBlockNames.STONE_BLOCK_SLAB, STONE_BLOCK_SLAB, BlockSlabStone.class);
        registerBlock(BlockNames.BRICK_BLOCK, ItemBlockNames.BRICK_BLOCK, BRICK_BLOCK, BlockBricks.class);
        registerBlock(BlockNames.TNT, ItemBlockNames.TNT, TNT, BlockTNT.class);
        registerBlock(BlockNames.BOOKSHELF, ItemBlockNames.BOOKSHELF, BOOKSHELF, BlockBookshelf.class);
        registerBlock(BlockNames.MOSSY_COBBLESTONE, ItemBlockNames.MOSSY_COBBLESTONE, MOSSY_COBBLESTONE, BlockMossStone.class);
        registerBlock(BlockNames.OBSIDIAN, ItemBlockNames.OBSIDIAN, OBSIDIAN, BlockObsidian.class);
        registerBlock(BlockNames.TORCH, ItemBlockNames.TORCH, TORCH, BlockTorch.class);
        registerBlock(BlockNames.FIRE, ItemBlockNames.FIRE, FIRE, BlockFire.class);
        registerBlock(BlockNames.MOB_SPAWNER, ItemBlockNames.MOB_SPAWNER, MOB_SPAWNER, BlockMobSpawner.class);
        registerBlock(BlockNames.OAK_STAIRS, ItemBlockNames.OAK_STAIRS, OAK_STAIRS, BlockStairsWood.class);
        registerBlock(BlockNames.CHEST, ItemBlockNames.CHEST, CHEST, BlockChest.class);
        registerBlock(BlockNames.REDSTONE_WIRE, ItemBlockNames.REDSTONE_WIRE, REDSTONE_WIRE, BlockRedstoneWire.class);
        registerBlock(BlockNames.DIAMOND_ORE, ItemBlockNames.DIAMOND_ORE, DIAMOND_ORE, BlockOreDiamond.class);
        registerBlock(BlockNames.DIAMOND_BLOCK, ItemBlockNames.DIAMOND_BLOCK, DIAMOND_BLOCK, BlockDiamond.class);
        registerBlock(BlockNames.CRAFTING_TABLE, ItemBlockNames.CRAFTING_TABLE, CRAFTING_TABLE, BlockCraftingTable.class);
        registerBlock(BlockNames.WHEAT, ItemBlockNames.ITEM_WHEAT, BLOCK_WHEAT, BlockWheat.class);
        registerBlock(BlockNames.FARMLAND, ItemBlockNames.FARMLAND, FARMLAND, BlockFarmland.class);
        registerBlock(BlockNames.FURNACE, ItemBlockNames.FURNACE, FURNACE, BlockFurnace.class);
        registerBlock(BlockNames.LIT_FURNACE, ItemBlockNames.LIT_FURNACE, LIT_FURNACE, BlockFurnaceBurning.class);
        registerBlock(BlockNames.STANDING_SIGN, ItemBlockNames.STANDING_SIGN, STANDING_SIGN, BlockSignPost.class);
        registerBlock(BlockNames.WOODEN_DOOR, ItemBlockNames.ITEM_WOODEN_DOOR, BLOCK_WOODEN_DOOR, BlockDoorWood.class);
        registerBlock(BlockNames.LADDER, ItemBlockNames.LADDER, LADDER, BlockLadder.class);
        registerBlock(BlockNames.RAIL, ItemBlockNames.RAIL, RAIL, BlockRail.class);
        registerBlock(BlockNames.STONE_STAIRS, ItemBlockNames.STONE_STAIRS, STONE_STAIRS, BlockStairsCobblestone.class);
        registerBlock(BlockNames.WALL_SIGN, ItemBlockNames.WALL_SIGN, WALL_SIGN, BlockWallSign.class);
        registerBlock(BlockNames.LEVER, ItemBlockNames.LEVER, LEVER, BlockLever.class);
        registerBlock(BlockNames.STONE_PRESSURE_PLATE, ItemBlockNames.STONE_PRESSURE_PLATE, STONE_PRESSURE_PLATE, BlockPressurePlateStone.class);
        registerBlock(BlockNames.IRON_DOOR, ItemBlockNames.ITEM_IRON_DOOR, BLOCK_IRON_DOOR, BlockDoorIron.class);
        registerBlock(BlockNames.WOODEN_PRESSURE_PLATE, ItemBlockNames.WOODEN_PRESSURE_PLATE, WOODEN_PRESSURE_PLATE, BlockPressurePlateWood.class);
        registerBlock(BlockNames.REDSTONE_ORE, ItemBlockNames.REDSTONE_ORE, REDSTONE_ORE, BlockOreRedstone.class);
        registerBlock(BlockNames.LIT_REDSTONE_ORE, ItemBlockNames.LIT_REDSTONE_ORE, LIT_REDSTONE_ORE, BlockOreRedstoneGlowing.class);
        registerBlock(BlockNames.UNLIT_REDSTONE_TORCH, ItemBlockNames.UNLIT_REDSTONE_TORCH, UNLIT_REDSTONE_TORCH, BlockRedstoneTorchUnlit.class);
        registerBlock(BlockNames.REDSTONE_TORCH, ItemBlockNames.REDSTONE_TORCH, REDSTONE_TORCH, BlockRedstoneTorch.class);
        registerBlock(BlockNames.STONE_BUTTON, ItemBlockNames.STONE_BUTTON, STONE_BUTTON, BlockButtonStone.class);
        registerBlock(BlockNames.SNOW_LAYER, ItemBlockNames.SNOW_LAYER, SNOW_LAYER, BlockSnowLayer.class);
        registerBlock(BlockNames.ICE, ItemBlockNames.ICE, ICE, BlockIce.class);
        registerBlock(BlockNames.SNOW, ItemBlockNames.SNOW, SNOW, BlockSnow.class);
        registerBlock(BlockNames.CACTUS, ItemBlockNames.CACTUS, CACTUS, BlockCactus.class);
        registerBlock(BlockNames.CLAY, ItemBlockNames.CLAY, CLAY, BlockClay.class);
        registerBlock(BlockNames.REEDS, ItemBlockNames.ITEM_REEDS, BLOCK_REEDS, BlockSugarcane.class);
        registerBlock(BlockNames.JUKEBOX, ItemBlockNames.JUKEBOX, JUKEBOX, BlockJukebox.class);
        //registerBlock(BlockNames.FENCE, ItemBlockNames.FENCE, FENCE, BlockFence.class);
        registerBlock(BlockNames.PUMPKIN, ItemBlockNames.PUMPKIN, PUMPKIN, BlockPumpkin.class);
        registerBlock(BlockNames.NETHERRACK, ItemBlockNames.NETHERRACK, NETHERRACK, BlockNetherrack.class);
        registerBlock(BlockNames.SOUL_SAND, ItemBlockNames.SOUL_SAND, SOUL_SAND, BlockSoulSand.class);
        registerBlock(BlockNames.GLOWSTONE, ItemBlockNames.GLOWSTONE, GLOWSTONE, BlockGlowstone.class);
        registerBlock(BlockNames.PORTAL, ItemBlockNames.PORTAL, PORTAL, BlockNetherPortal.class);
        registerBlock(BlockNames.LIT_PUMPKIN, ItemBlockNames.LIT_PUMPKIN, LIT_PUMPKIN, BlockPumpkinLit.class);
        registerBlock(BlockNames.CAKE, ItemBlockNames.ITEM_CAKE, BLOCK_CAKE, BlockCake.class);
        registerBlock(BlockNames.UNPOWERED_REPEATER, ItemBlockNames.UNPOWERED_REPEATER, UNPOWERED_REPEATER, BlockRedstoneRepeaterUnpowered.class);
        registerBlock(BlockNames.POWERED_REPEATER, ItemBlockNames.POWERED_REPEATER, POWERED_REPEATER, BlockRedstoneRepeaterPowered.class);
        registerBlock(BlockNames.INVISIBLE_BEDROCK, ItemBlockNames.INVISIBLEBEDROCK, INVISIBLE_BEDROCK, BlockBedrockInvisible.class);
        registerBlock(BlockNames.TRAPDOOR, ItemBlockNames.TRAPDOOR, TRAPDOOR, BlockTrapdoor.class);
        registerBlock(BlockNames.MONSTER_EGG, ItemBlockNames.MONSTER_EGG, MONSTER_EGG, BlockMonsterEgg.class);
        registerBlock(BlockNames.STONEBRICK, ItemBlockNames.STONEBRICK, STONEBRICK, BlockBricksStone.class);
        registerBlock(BlockNames.BROWN_MUSHROOM_BLOCK, ItemBlockNames.BROWN_MUSHROOM_BLOCK, BROWN_MUSHROOM_BLOCK, BlockHugeMushroomBrown.class);
        registerBlock(BlockNames.RED_MUSHROOM_BLOCK, ItemBlockNames.RED_MUSHROOM_BLOCK, RED_MUSHROOM_BLOCK, BlockHugeMushroomRed.class);
        registerBlock(BlockNames.IRON_BARS, ItemBlockNames.IRON_BARS, IRON_BARS, BlockIronBars.class);
        registerBlock(BlockNames.GLASS_PANE, ItemBlockNames.GLASS_PANE, GLASS_PANE, BlockGlassPane.class);
        registerBlock(BlockNames.MELON_BLOCK, ItemBlockNames.MELON_BLOCK, MELON_BLOCK, BlockMelon.class);
        registerBlock(BlockNames.PUMPKIN_STEM, ItemBlockNames.PUMPKIN_STEM, PUMPKIN_STEM, BlockStemPumpkin.class);
        registerBlock(BlockNames.MELON_STEM, ItemBlockNames.MELON_STEM, MELON_STEM, BlockStemMelon.class);
        registerBlock(BlockNames.VINE, ItemBlockNames.VINE, VINE, BlockVine.class);
        registerBlock(BlockNames.FENCE_GATE, ItemBlockNames.FENCE_GATE, FENCE_GATE, BlockFenceGate.class);
        registerBlock(BlockNames.BRICK_STAIRS, ItemBlockNames.BRICK_STAIRS, BRICK_STAIRS, BlockStairsBrick.class);
        registerBlock(BlockNames.STONE_BRICK_STAIRS, ItemBlockNames.STONE_BRICK_STAIRS, STONE_BRICK_STAIRS, BlockStairsStoneBrick.class);
        registerBlock(BlockNames.MYCELIUM, ItemBlockNames.MYCELIUM, MYCELIUM, BlockMycelium.class);
        registerBlock(BlockNames.WATERLILY, ItemBlockNames.WATERLILY, WATERLILY, BlockWaterLily.class);
        registerBlock(BlockNames.NETHER_BRICK, ItemBlockNames.NETHER_BRICK, NETHER_BRICK, BlockBricksNether.class);
        registerBlock(BlockNames.NETHER_BRICK_FENCE, ItemBlockNames.NETHER_BRICK_FENCE, NETHER_BRICK_FENCE, BlockFenceNetherBrick.class);
        registerBlock(BlockNames.NETHER_BRICK_STAIRS, ItemBlockNames.NETHER_BRICK_STAIRS, NETHER_BRICK_STAIRS, BlockStairsNetherBrick.class);
        registerBlock(BlockNames.NETHER_WART, ItemBlockNames.ITEM_NETHER_WART, BLOCK_NETHER_WART, BlockNetherWart.class);
        registerBlock(BlockNames.ENCHANTING_TABLE, ItemBlockNames.ENCHANTING_TABLE, ENCHANTING_TABLE, BlockEnchantingTable.class);
        registerBlock(BlockNames.BREWING_STAND, ItemBlockNames.ITEM_BREWING_STAND, BLOCK_BREWING_STAND, BlockBrewingStand.class);
        registerBlock(BlockNames.CAULDRON, ItemBlockNames.ITEM_CAULDRON, BLOCK_CAULDRON, BlockCauldron.class);
        registerBlock(BlockNames.END_PORTAL, ItemBlockNames.END_PORTAL, END_PORTAL, BlockEndPortal.class);
        registerBlock(BlockNames.END_PORTAL_FRAME, ItemBlockNames.END_PORTAL_FRAME, END_PORTAL_FRAME, BlockEndPortalFrame.class);
        registerBlock(BlockNames.END_STONE, ItemBlockNames.END_STONE, END_STONE, BlockEndStone.class);
        registerBlock(BlockNames.DRAGON_EGG, ItemBlockNames.DRAGON_EGG, DRAGON_EGG, BlockDragonEgg.class);
        registerBlock(BlockNames.REDSTONE_LAMP, ItemBlockNames.REDSTONE_LAMP, REDSTONE_LAMP, BlockRedstoneLamp.class);
        registerBlock(BlockNames.LIT_REDSTONE_LAMP, ItemBlockNames.LIT_REDSTONE_LAMP, LIT_REDSTONE_LAMP, BlockRedstoneLampLit.class);
        registerBlock(BlockNames.DROPPER, ItemBlockNames.DROPPER, DROPPER, BlockDropper.class);
        registerBlock(BlockNames.ACTIVATOR_RAIL, ItemBlockNames.ACTIVATOR_RAIL, ACTIVATOR_RAIL, BlockRailActivator.class);
        registerBlock(BlockNames.COCOA, ItemBlockNames.COCOA, COCOA, BlockCocoa.class);
        registerBlock(BlockNames.SANDSTONE_STAIRS, ItemBlockNames.SANDSTONE_STAIRS, SANDSTONE_STAIRS, BlockStairsSandstone.class);
        registerBlock(BlockNames.EMERALD_ORE, ItemBlockNames.EMERALD_ORE, EMERALD_ORE, BlockOreEmerald.class);
        registerBlock(BlockNames.ENDER_CHEST, ItemBlockNames.ENDER_CHEST, ENDER_CHEST, BlockEnderChest.class);
        registerBlock(BlockNames.TRIPWIRE_HOOK, ItemBlockNames.TRIPWIRE_HOOK, TRIPWIRE_HOOK, BlockTripWireHook.class);
        registerBlock(BlockNames.TRIP_WIRE, ItemBlockNames.TRIP_WIRE, TRIP_WIRE, BlockTripWire.class);
        registerBlock(BlockNames.EMERALD_BLOCK, ItemBlockNames.EMERALD_BLOCK, EMERALD_BLOCK, BlockEmerald.class);
        registerBlock(BlockNames.SPRUCE_STAIRS, ItemBlockNames.SPRUCE_STAIRS, SPRUCE_STAIRS, BlockStairsSpruce.class);
        registerBlock(BlockNames.BIRCH_STAIRS, ItemBlockNames.BIRCH_STAIRS, BIRCH_STAIRS, BlockStairsBirch.class);
        registerBlock(BlockNames.JUNGLE_STAIRS, ItemBlockNames.JUNGLE_STAIRS, JUNGLE_STAIRS, BlockStairsJungle.class);
        registerBlock(BlockNames.COMMAND_BLOCK, ItemBlockNames.COMMAND_BLOCK, COMMAND_BLOCK, BlockCommand.class);
        registerBlock(BlockNames.BEACON, ItemBlockNames.BEACON, BEACON, BlockBeacon.class);
        registerBlock(BlockNames.COBBLESTONE_WALL, ItemBlockNames.COBBLESTONE_WALL, COBBLESTONE_WALL, BlockWallCobblestone.class);
        registerBlock(BlockNames.FLOWER_POT, ItemBlockNames.ITEM_FLOWER_POT, BLOCK_FLOWER_POT, BlockFlowerPot.class);
        registerBlock(BlockNames.CARROTS, ItemBlockNames.CARROTS, CARROTS, BlockCarrot.class);
        registerBlock(BlockNames.POTATOES, ItemBlockNames.POTATOES, POTATOES, BlockPotato.class);
        registerBlock(BlockNames.WOODEN_BUTTON, ItemBlockNames.WOODEN_BUTTON, WOODEN_BUTTON, BlockButtonWooden.class);
        registerBlock(BlockNames.SKULL, ItemBlockNames.ITEM_SKULL, BLOCK_SKULL, BlockSkull.class);
        registerBlock(BlockNames.ANVIL, ItemBlockNames.ANVIL, ANVIL, BlockAnvil.class);
        registerBlock(BlockNames.TRAPPED_CHEST, ItemBlockNames.TRAPPED_CHEST, TRAPPED_CHEST, BlockTrappedChest.class);
        registerBlock(BlockNames.LIGHT_WEIGHTED_PRESSURE_PLATE, ItemBlockNames.LIGHT_WEIGHTED_PRESSURE_PLATE, LIGHT_WEIGHTED_PRESSURE_PLATE, BlockWeightedPressurePlateLight.class);
        registerBlock(BlockNames.HEAVY_WEIGHTED_PRESSURE_PLATE, ItemBlockNames.HEAVY_WEIGHTED_PRESSURE_PLATE, HEAVY_WEIGHTED_PRESSURE_PLATE, BlockWeightedPressurePlateHeavy.class);
        registerBlock(BlockNames.UNPOWERED_COMPARATOR, ItemBlockNames.UNPOWERED_COMPARATOR, UNPOWERED_COMPARATOR, BlockRedstoneComparatorUnpowered.class);
        registerBlock(BlockNames.POWERED_COMPARATOR, ItemBlockNames.POWERED_COMPARATOR, POWERED_COMPARATOR, BlockRedstoneComparatorPowered.class);
        registerBlock(BlockNames.DAYLIGHT_DETECTOR, ItemBlockNames.DAYLIGHT_DETECTOR, DAYLIGHT_DETECTOR, BlockDaylightDetector.class);
        registerBlock(BlockNames.REDSTONE_BLOCK, ItemBlockNames.REDSTONE_BLOCK, REDSTONE_BLOCK, BlockRedstone.class);
        registerBlock(BlockNames.QUARTZ_ORE, ItemBlockNames.QUARTZ_ORE, QUARTZ_ORE, BlockOreQuartz.class);
        registerBlock(BlockNames.HOPPER, ItemBlockNames.ITEM_HOPPER, BLOCK_HOPPER, BlockHopper.class);
        registerBlock(BlockNames.QUARTZ_BLOCK, ItemBlockNames.QUARTZ_BLOCK, QUARTZ_BLOCK, BlockQuartz.class);
        registerBlock(BlockNames.QUARTZ_STAIRS, ItemBlockNames.QUARTZ_STAIRS, QUARTZ_STAIRS, BlockStairsQuartz.class);
        registerBlock(BlockNames.DOUBLE_WOODEN_SLAB, ItemBlockNames.DOUBLE_WOODEN_SLAB, DOUBLE_WOODEN_SLAB, BlockDoubleSlabWood.class);
        registerBlock(BlockNames.WOODEN_SLAB, ItemBlockNames.WOODEN_SLAB, WOODEN_SLAB, BlockSlabWood.class);
        registerBlock(BlockNames.STAINED_HARDENED_CLAY, ItemBlockNames.STAINED_HARDENED_CLAY, STAINED_HARDENED_CLAY, BlockTerracottaStained.class);
        registerBlock(BlockNames.STAINED_GLASS_PANE, ItemBlockNames.STAINED_GLASS_PANE, STAINED_GLASS_PANE, BlockGlassPaneStained.class);
        registerBlock(BlockNames.LEAVES2, ItemBlockNames.LEAVES2, LEAVES2, BlockLeaves2.class);
        //registerBlock(BlockNames.LOG2, ItemBlockNames.LOG2, LOG2, BlockWood2.class);
        registerBlock(BlockNames.ACACIA_STAIRS, ItemBlockNames.ACACIA_STAIRS, ACACIA_STAIRS, BlockStairsAcacia.class);
        registerBlock(BlockNames.DARK_OAK_STAIRS, ItemBlockNames.DARK_OAK_STAIRS, DARK_OAK_STAIRS, BlockStairsDarkOak.class);
        registerBlock(BlockNames.SLIME, ItemBlockNames.SLIME, SLIME, BlockSlime.class);

        registerBlock(BlockNames.IRON_TRAPDOOR, ItemBlockNames.IRON_TRAPDOOR, IRON_TRAPDOOR, BlockTrapdoorIron.class);
        registerBlock(BlockNames.PRISMARINE, ItemBlockNames.PRISMARINE, PRISMARINE, BlockPrismarine.class);
        registerBlock(BlockNames.SEA_LANTERN, ItemBlockNames.SEA_LANTERN, SEA_LANTERN, BlockSeaLantern.class);
        registerBlock(BlockNames.HAY_BLOCK, ItemBlockNames.HAY_BLOCK, HAY_BLOCK, BlockHayBale.class);
        //registerBlock(BlockNames.CARPET, ItemBlockNames.CARPET, CARPET, BlockCarpet.class);
        registerBlock(BlockNames.HARDENED_CLAY, ItemBlockNames.HARDENED_CLAY, HARDENED_CLAY, BlockTerracotta.class);
        registerBlock(BlockNames.COAL_BLOCK, ItemBlockNames.COAL_BLOCK, COAL_BLOCK, BlockCoal.class);
        registerBlock(BlockNames.PACKED_ICE, ItemBlockNames.PACKED_ICE, PACKED_ICE, BlockIcePacked.class);
        registerBlock(BlockNames.DOUBLE_PLANT, ItemBlockNames.DOUBLE_PLANT, DOUBLE_PLANT, BlockDoublePlant.class);
        registerBlock(BlockNames.STANDING_BANNER, ItemBlockNames.STANDING_BANNER, STANDING_BANNER, BlockBanner.class);
        registerBlock(BlockNames.WALL_BANNER, ItemBlockNames.WALL_BANNER, WALL_BANNER, BlockWallBanner.class);
        registerBlock(BlockNames.DAYLIGHT_DETECTOR_INVERTED, ItemBlockNames.DAYLIGHT_DETECTOR_INVERTED, DAYLIGHT_DETECTOR_INVERTED, BlockDaylightDetectorInverted.class);
        registerBlock(BlockNames.RED_SANDSTONE, ItemBlockNames.RED_SANDSTONE, RED_SANDSTONE, BlockRedSandstone.class);
        registerBlock(BlockNames.RED_SANDSTONE_STAIRS, ItemBlockNames.RED_SANDSTONE_STAIRS, RED_SANDSTONE_STAIRS, BlockStairsRedSandstone.class);
        registerBlock(BlockNames.DOUBLE_STONE_BLOCK_SLAB2, ItemBlockNames.DOUBLE_STONE_BLOCK_SLAB2, DOUBLE_STONE_BLOCK_SLAB2, BlockDoubleSlabRedSandstone.class);
        registerBlock(BlockNames.STONE_BLOCK_SLAB2, ItemBlockNames.STONE_BLOCK_SLAB2, STONE_BLOCK_SLAB2, BlockSlabRedSandstone.class);
        registerBlock(BlockNames.SPRUCE_FENCE_GATE, ItemBlockNames.SPRUCE_FENCE_GATE, SPRUCE_FENCE_GATE, BlockFenceGateSpruce.class);
        registerBlock(BlockNames.BIRCH_FENCE_GATE, ItemBlockNames.BIRCH_FENCE_GATE, BIRCH_FENCE_GATE, BlockFenceGateBirch.class);
        registerBlock(BlockNames.JUNGLE_FENCE_GATE, ItemBlockNames.JUNGLE_FENCE_GATE, JUNGLE_FENCE_GATE, BlockFenceGateJungle.class);
        registerBlock(BlockNames.DARK_OAK_FENCE_GATE, ItemBlockNames.DARK_OAK_FENCE_GATE, DARK_OAK_FENCE_GATE, BlockFenceGateDarkOak.class);
        registerBlock(BlockNames.ACACIA_FENCE_GATE, ItemBlockNames.ACACIA_FENCE_GATE, ACACIA_FENCE_GATE, BlockFenceGateAcacia.class);
        registerBlock(BlockNames.REPEATING_COMMAND_BLOCK, ItemBlockNames.REPEATING_COMMAND_BLOCK, REPEATING_COMMAND_BLOCK, BlockCommandRepeating.class);
        registerBlock(BlockNames.CHAIN_COMMAND_BLOCK, ItemBlockNames.CHAIN_COMMAND_BLOCK, CHAIN_COMMAND_BLOCK, BlockCommandChain.class);
        registerBlock(BlockNames.HARD_GLASS_PANE, ItemBlockNames.HARD_GLASS_PANE, HARD_GLASS_PANE, BlockGlassPaneHard.class);
        registerBlock(BlockNames.HARD_STAINED_GLASS_PANE, ItemBlockNames.HARD_STAINED_GLASS_PANE, HARD_STAINED_GLASS_PANE, BlockGlassPaneStainedHard.class);
        registerBlock(BlockNames.CHEMICAL_HEAT, ItemBlockNames.CHEMICAL_HEAT, CHEMICAL_HEAT, BlockChemicalHeat.class);
        registerBlock(BlockNames.SPRUCE_DOOR, ItemBlockNames.ITEM_SPRUCE_DOOR, BLOCK_SPRUCE_DOOR, BlockDoorSpruce.class);
        registerBlock(BlockNames.BIRCH_DOOR, ItemBlockNames.ITEM_BIRCH_DOOR, BLOCK_BIRCH_DOOR, BlockDoorBirch.class);
        registerBlock(BlockNames.JUNGLE_DOOR, ItemBlockNames.ITEM_JUNGLE_DOOR, BLOCK_JUNGLE_DOOR, BlockDoorJungle.class);
        registerBlock(BlockNames.ACACIA_DOOR, ItemBlockNames.ITEM_ACACIA_DOOR, BLOCK_ACACIA_DOOR, BlockDoorAcacia.class);
        registerBlock(BlockNames.DARK_OAK_DOOR, ItemBlockNames.ITEM_DARK_OAK_DOOR, BLOCK_DARK_OAK_DOOR, BlockDoorDarkOak.class);
        registerBlock(BlockNames.GRASS_PATH, ItemBlockNames.GRASS_PATH, GRASS_PATH, BlockGrassPath.class);
        registerBlock(BlockNames.FRAME, ItemBlockNames.ITEM_FRAME, BLOCK_FRAME, BlockItemFrame.class);
        registerBlock(BlockNames.CHORUS_FLOWER, ItemBlockNames.CHORUS_FLOWER, CHORUS_FLOWER, BlockChorusFlower.class);
        registerBlock(BlockNames.PURPUR_BLOCK, ItemBlockNames.PURPUR_BLOCK, PURPUR_BLOCK, BlockPurpur.class);
        registerBlock(BlockNames.COLORED_TORCH_RG, ItemBlockNames.COLORED_TORCH_RG, COLORED_TORCH_RG, BlockTorchColoredRedGreen.class);
        registerBlock(BlockNames.PURPUR_STAIRS, ItemBlockNames.PURPUR_STAIRS, PURPUR_STAIRS, BlockStairsPurpur.class);
        registerBlock(BlockNames.COLORED_TORCH_BP, ItemBlockNames.COLORED_TORCH_BP, COLORED_TORCH_BP, BlockTorchColoredBluePurple.class);
        registerBlock(BlockNames.UNDYED_SHULKER_BOX, ItemBlockNames.UNDYED_SHULKER_BOX, UNDYED_SHULKER_BOX, BlockShulkerBox.class);
        registerBlock(BlockNames.END_BRICKS, ItemBlockNames.END_BRICKS, END_BRICKS, BlockBricksEndStone.class);
        registerBlock(BlockNames.FROSTED_ICE, ItemBlockNames.FROSTED_ICE, FROSTED_ICE, BlockIceFrosted.class);
        registerBlock(BlockNames.END_ROD, ItemBlockNames.END_ROD, END_ROD, BlockEndRod.class);
        registerBlock(BlockNames.END_GATEWAY, ItemBlockNames.END_GATEWAY, END_GATEWAY, BlockEndGateway.class);

        registerBlock(BlockNames.MAGMA, ItemBlockNames.MAGMA, MAGMA, BlockMagma.class);
        registerBlock(BlockNames.NETHER_WART_BLOCK, ItemBlockNames.NETHER_WART_BLOCK, NETHER_WART_BLOCK, BlockNetherWartBlock.class);
        registerBlock(BlockNames.RED_NETHER_BRICK, ItemBlockNames.RED_NETHER_BRICK, RED_NETHER_BRICK, BlockBricksRedNether.class);
        registerBlock(BlockNames.BONE_BLOCK, ItemBlockNames.BONE_BLOCK, BONE_BLOCK, BlockBone.class);

        //registerBlock(BlockNames.SHULKER_BOX, ItemBlockNames.SHULKER_BOX, SHULKER_BOX, BlockShulkerBox.class);
        registerBlock(BlockNames.PURPLE_GLAZED_TERRACOTTA, ItemBlockNames.PURPLE_GLAZED_TERRACOTTA, PURPLE_GLAZED_TERRACOTTA, BlockTerracottaGlazedPurple.class);
        registerBlock(BlockNames.WHITE_GLAZED_TERRACOTTA, ItemBlockNames.WHITE_GLAZED_TERRACOTTA, WHITE_GLAZED_TERRACOTTA, BlockTerracottaGlazedWhite.class);
        registerBlock(BlockNames.ORANGE_GLAZED_TERRACOTTA, ItemBlockNames.ORANGE_GLAZED_TERRACOTTA, ORANGE_GLAZED_TERRACOTTA, BlockTerracottaGlazedOrange.class);
        registerBlock(BlockNames.MAGENTA_GLAZED_TERRACOTTA, ItemBlockNames.MAGENTA_GLAZED_TERRACOTTA, MAGENTA_GLAZED_TERRACOTTA, BlockTerracottaGlazedMagenta.class);
        registerBlock(BlockNames.LIGHT_BLUE_GLAZED_TERRACOTTA, ItemBlockNames.LIGHT_BLUE_GLAZED_TERRACOTTA, LIGHT_BLUE_GLAZED_TERRACOTTA, BlockTerracottaGlazedLightBlue.class);
        registerBlock(BlockNames.YELLOW_GLAZED_TERRACOTTA, ItemBlockNames.YELLOW_GLAZED_TERRACOTTA, YELLOW_GLAZED_TERRACOTTA, BlockTerracottaGlazedYellow.class);
        registerBlock(BlockNames.LIME_GLAZED_TERRACOTTA, ItemBlockNames.LIME_GLAZED_TERRACOTTA, LIME_GLAZED_TERRACOTTA, BlockTerracottaGlazedLime.class);
        registerBlock(BlockNames.PINK_GLAZED_TERRACOTTA, ItemBlockNames.PINK_GLAZED_TERRACOTTA, PINK_GLAZED_TERRACOTTA, BlockTerracottaGlazedPink.class);
        registerBlock(BlockNames.GRAY_GLAZED_TERRACOTTA, ItemBlockNames.GRAY_GLAZED_TERRACOTTA, GRAY_GLAZED_TERRACOTTA, BlockTerracottaGlazedGray.class);
        registerBlock(BlockNames.SILVER_GLAZED_TERRACOTTA, ItemBlockNames.SILVER_GLAZED_TERRACOTTA, SILVER_GLAZED_TERRACOTTA, BlockTerracottaGlazedSilver.class);
        registerBlock(BlockNames.CYAN_GLAZED_TERRACOTTA, ItemBlockNames.CYAN_GLAZED_TERRACOTTA, CYAN_GLAZED_TERRACOTTA, BlockTerracottaGlazedCyan.class);

        registerBlock(BlockNames.BLUE_GLAZED_TERRACOTTA, ItemBlockNames.BLUE_GLAZED_TERRACOTTA, BLUE_GLAZED_TERRACOTTA, BlockTerracottaGlazedBlue.class);
        registerBlock(BlockNames.BROWN_GLAZED_TERRACOTTA, ItemBlockNames.BROWN_GLAZED_TERRACOTTA, BROWN_GLAZED_TERRACOTTA, BlockTerracottaGlazedBrown.class);
        registerBlock(BlockNames.GREEN_GLAZED_TERRACOTTA, ItemBlockNames.GREEN_GLAZED_TERRACOTTA, GREEN_GLAZED_TERRACOTTA, BlockTerracottaGlazedGreen.class);
        registerBlock(BlockNames.RED_GLAZED_TERRACOTTA, ItemBlockNames.RED_GLAZED_TERRACOTTA, RED_GLAZED_TERRACOTTA, BlockTerracottaGlazedRed.class);
        registerBlock(BlockNames.BLACK_GLAZED_TERRACOTTA, ItemBlockNames.BLACK_GLAZED_TERRACOTTA, BLACK_GLAZED_TERRACOTTA, BlockTerracottaGlazedBlack.class);
        //registerBlock(BlockNames.CONCRETE, ItemBlockNames.CONCRETE, CONCRETE, BlockConcrete.class);
        registerBlock(BlockNames.CONCRETE_POWDER, ItemBlockNames.CONCRETE_POWDER, CONCRETE_POWDER, BlockConcretePowder.class);
        registerBlock(BlockNames.CHEMISTRY_TABLE, ItemBlockNames.CHEMISTRY_TABLE, CHEMISTRY_TABLE, BlockChemistryTable.class);
        registerBlock(BlockNames.UNDERWATER_TORCH, ItemBlockNames.UNDERWATER_TORCH, UNDERWATER_TORCH, BlockTorchUnderwater.class);
        registerBlock(BlockNames.CHORUS_PLANT, ItemBlockNames.CHORUS_PLANT, CHORUS_PLANT, BlockChorusPlant.class);
        registerBlock(BlockNames.STAINED_GLASS, ItemBlockNames.STAINED_GLASS, STAINED_GLASS, BlockGlassStained.class);

        registerBlock(BlockNames.PODZOL, ItemBlockNames.PODZOL, PODZOL, BlockPodzol.class);
        registerBlock(BlockNames.BEETROOT, ItemBlockNames.ITEM_BEETROOT, BLOCK_BEETROOT, BlockBeetroot.class);
        registerBlock(BlockNames.STONECUTTER, ItemBlockNames.STONECUTTER, STONECUTTER, BlockStonecutterLegacy.class);
        registerBlock(BlockNames.GLOWINGOBSIDIAN, ItemBlockNames.GLOWINGOBSIDIAN, GLOWINGOBSIDIAN, BlockObsidianGlowing.class);
        registerBlock(BlockNames.NETHERREACTOR, ItemBlockNames.NETHERREACTOR, NETHERREACTOR, BlockNetherReactor.class);
        registerBlock(BlockNames.INFO_UPDATE, ItemBlockNames.INFO_UPDATE, INFO_UPDATE, BlockInfoUpdate.class);
        registerBlock(BlockNames.INFO_UPDATE2, ItemBlockNames.INFO_UPDATE2, INFO_UPDATE2, BlockInfoUpdate2.class);
        registerBlock(BlockNames.MOVING_BLOCK, ItemBlockNames.MOVING_BLOCK, MOVING_BLOCK, BlockMoving.class);
        registerBlock(BlockNames.OBSERVER, ItemBlockNames.OBSERVER, OBSERVER, BlockObserver.class);
        registerBlock(BlockNames.STRUCTURE_BLOCK, ItemBlockNames.STRUCTURE_BLOCK, STRUCTURE_BLOCK, BlockStructure.class);
        registerBlock(BlockNames.HARD_GLASS, ItemBlockNames.HARD_GLASS, HARD_GLASS, BlockGlassHard.class);
        registerBlock(BlockNames.HARD_STAINED_GLASS, ItemBlockNames.HARD_STAINED_GLASS, HARD_STAINED_GLASS, BlockGlassStainedHard.class);
        registerBlock(BlockNames.RESERVED6, ItemBlockNames.RESERVED6, RESERVED6, BlockReserved6.class);

        registerBlock(BlockNames.PRISMARINE_STAIRS, ItemBlockNames.PRISMARINE_STAIRS, PRISMARINE_STAIRS, BlockStairsPrismarine.class, V1_4_0);
        registerBlock(BlockNames.DARK_PRISMARINE_STAIRS, ItemBlockNames.DARK_PRISMARINE_STAIRS, DARK_PRISMARINE_STAIRS, BlockStairsDarkPrismarine.class, V1_4_0);
        registerBlock(BlockNames.PRISMARINE_BRICKS_STAIRS, ItemBlockNames.PRISMARINE_BRICKS_STAIRS, PRISMARINE_BRICKS_STAIRS, BlockStairsPrismarineBrick.class, V1_4_0);
        registerBlock(BlockNames.STRIPPED_SPRUCE_LOG, ItemBlockNames.STRIPPED_SPRUCE_LOG, STRIPPED_SPRUCE_LOG, BlockLogStrippedSpruce.class, V1_4_0);
        registerBlock(BlockNames.STRIPPED_BIRCH_LOG, ItemBlockNames.STRIPPED_BIRCH_LOG, STRIPPED_BIRCH_LOG, BlockLogStrippedBirch.class, V1_4_0);
        registerBlock(BlockNames.STRIPPED_JUNGLE_LOG, ItemBlockNames.STRIPPED_JUNGLE_LOG, STRIPPED_JUNGLE_LOG, BlockLogStrippedJungle.class, V1_4_0);
        registerBlock(BlockNames.STRIPPED_ACACIA_LOG, ItemBlockNames.STRIPPED_ACACIA_LOG, STRIPPED_ACACIA_LOG, BlockLogStrippedAcacia.class, V1_4_0);
        registerBlock(BlockNames.STRIPPED_DARK_OAK_LOG, ItemBlockNames.STRIPPED_DARK_OAK_LOG, STRIPPED_DARK_OAK_LOG, BlockLogStrippedDarkOak.class, V1_4_0);
        registerBlock(BlockNames.STRIPPED_OAK_LOG, ItemBlockNames.STRIPPED_OAK_LOG, STRIPPED_OAK_LOG, BlockLogStrippedOak.class, V1_4_0);
        registerBlock(BlockNames.BLUE_ICE, ItemBlockNames.BLUE_ICE, BLUE_ICE, BlockIceBlue.class, V1_4_0);
        registerElements();
        registerBlock(BlockNames.SEAGRASS, ItemBlockNames.SEAGRASS, SEAGRASS, BlockSeagrass.class, V1_4_0);
        //registerBlock(BlockNames.CORAL, ItemBlockNames.CORAL, CORAL, BlockCoral.class, V1_4_0);
        registerBlock(BlockNames.CORAL_BLOCK, ItemBlockNames.CORAL_BLOCK, CORAL_BLOCK, BlockCoralBlock.class, V1_4_0);
        registerBlock(BlockNames.CORAL_FAN, ItemBlockNames.CORAL_FAN, CORAL_FAN, BlockCoralFan.class, V1_4_0);
        registerBlock(BlockNames.CORAL_FAN_DEAD, ItemBlockNames.CORAL_FAN_DEAD, CORAL_FAN_DEAD, BlockCoralFanDead.class, V1_4_0);
        registerBlock(BlockNames.CORAL_FAN_HANG, ItemBlockNames.CORAL_FAN_HANG, CORAL_FAN_HANG, BlockCoralFanHang.class, V1_4_0);
        registerBlock(BlockNames.CORAL_FAN_HANG2, ItemBlockNames.CORAL_FAN_HANG2, CORAL_FAN_HANG2, BlockCoralFanHang2.class, V1_4_0);
        registerBlock(BlockNames.CORAL_FAN_HANG3, ItemBlockNames.CORAL_FAN_HANG3, CORAL_FAN_HANG3, BlockCoralFanHang3.class, V1_4_0);
        registerBlock(BlockNames.KELP, ItemBlockNames.ITEM_KELP, BLOCK_KELP, BlockKelp.class, V1_4_0);
        registerBlock(BlockNames.DRIED_KELP_BLOCK, ItemBlockNames.DRIED_KELP_BLOCK, DRIED_KELP_BLOCK, BlockDriedKelp.class, V1_4_0);
        registerBlock(BlockNames.ACACIA_BUTTON, ItemBlockNames.ACACIA_BUTTON, ACACIA_BUTTON, BlockButtonAcacia.class, V1_4_0);
        registerBlock(BlockNames.BIRCH_BUTTON, ItemBlockNames.BIRCH_BUTTON, BIRCH_BUTTON, BlockButtonBirch.class, V1_4_0);
        registerBlock(BlockNames.DARK_OAK_BUTTON, ItemBlockNames.DARK_OAK_BUTTON, DARK_OAK_BUTTON, BlockButtonDarkOak.class, V1_4_0);
        registerBlock(BlockNames.JUNGLE_BUTTON, ItemBlockNames.JUNGLE_BUTTON, JUNGLE_BUTTON, BlockButtonJungle.class, V1_4_0);
        registerBlock(BlockNames.SPRUCE_BUTTON, ItemBlockNames.SPRUCE_BUTTON, SPRUCE_BUTTON, BlockButtonSpruce.class, V1_4_0);
        registerBlock(BlockNames.ACACIA_TRAPDOOR, ItemBlockNames.ACACIA_TRAPDOOR, ACACIA_TRAPDOOR, BlockTrapdoorAcacia.class, V1_4_0);
        registerBlock(BlockNames.BIRCH_TRAPDOOR, ItemBlockNames.BIRCH_TRAPDOOR, BIRCH_TRAPDOOR, BlockTrapdoorBirch.class, V1_4_0);
        registerBlock(BlockNames.DARK_OAK_TRAPDOOR, ItemBlockNames.DARK_OAK_TRAPDOOR, DARK_OAK_TRAPDOOR, BlockTrapdoorDarkOak.class, V1_4_0);
        registerBlock(BlockNames.JUNGLE_TRAPDOOR, ItemBlockNames.JUNGLE_TRAPDOOR, JUNGLE_TRAPDOOR, BlockTrapdoorJungle.class, V1_4_0);
        registerBlock(BlockNames.SPRUCE_TRAPDOOR, ItemBlockNames.SPRUCE_TRAPDOOR, SPRUCE_TRAPDOOR, BlockTrapdoorSpruce.class, V1_4_0);
        registerBlock(BlockNames.ACACIA_PRESSURE_PLATE, ItemBlockNames.ACACIA_PRESSURE_PLATE, ACACIA_PRESSURE_PLATE, BlockPressurePlateAcacia.class, V1_4_0);
        registerBlock(BlockNames.BIRCH_PRESSURE_PLATE, ItemBlockNames.BIRCH_PRESSURE_PLATE, BIRCH_PRESSURE_PLATE, BlockPressurePlateBirch.class, V1_4_0);
        registerBlock(BlockNames.DARK_OAK_PRESSURE_PLATE, ItemBlockNames.DARK_OAK_PRESSURE_PLATE, DARK_OAK_PRESSURE_PLATE, BlockPressurePlateDarkOak.class, V1_4_0);
        registerBlock(BlockNames.JUNGLE_PRESSURE_PLATE, ItemBlockNames.JUNGLE_PRESSURE_PLATE, JUNGLE_PRESSURE_PLATE, BlockPressurePlateJungle.class, V1_4_0);
        registerBlock(BlockNames.SPRUCE_PRESSURE_PLATE, ItemBlockNames.SPRUCE_PRESSURE_PLATE, SPRUCE_PRESSURE_PLATE, BlockPressurePlateSpruce.class, V1_4_0);
        registerBlock(BlockNames.CARVED_PUMPKIN, ItemBlockNames.CARVED_PUMPKIN, CARVED_PUMPKIN, BlockPumpkinCarved.class, V1_4_0);
        registerBlock(BlockNames.SEA_PICKLE, ItemBlockNames.SEA_PICKLE, SEA_PICKLE, BlockSeaPickle.class, V1_4_0);

        registerBlock(BlockNames.CONDUIT, ItemBlockNames.CONDUIT, CONDUIT, BlockConduit.class, V1_5_0); //TODO: activate
        registerBlock(BlockNames.TURTLE_EGG, ItemBlockNames.TURTLE_EGG, TURTLE_EGG, BlockTurtleEgg.class, V1_5_0);
        registerBlock(BlockNames.BUBBLE_COLUMN, ItemBlockNames.BUBBLE_COLUMN, BUBBLE_COLUMN, BlockBubbleColumn.class, V1_5_0);

        registerBlock(BlockNames.BARRIER, ItemBlockNames.BARRIER, BARRIER, BlockBarrier.class, V1_6_0);

        registerBlock(BlockNames.BAMBOO, ItemBlockNames.BAMBOO, BAMBOO, BlockBamboo.class, V1_8_0);
        registerBlock(BlockNames.BAMBOO_SAPLING, ItemBlockNames.BAMBOO_SAPLING, BAMBOO_SAPLING, BlockBambooSapling.class, V1_8_0);
        registerBlock(BlockNames.SCAFFOLDING, ItemBlockNames.SCAFFOLDING, SCAFFOLDING, BlockScaffolding.class, V1_8_0);

        registerBlock(BlockNames.STONE_BLOCK_SLAB3, ItemBlockNames.STONE_BLOCK_SLAB3, STONE_BLOCK_SLAB3, BlockSlabStone3.class, V1_9_0);
        registerBlock(BlockNames.STONE_BLOCK_SLAB4, ItemBlockNames.STONE_BLOCK_SLAB4, STONE_BLOCK_SLAB4, BlockSlabStone4.class, V1_9_0);
        registerBlock(BlockNames.DOUBLE_STONE_BLOCK_SLAB3, ItemBlockNames.DOUBLE_STONE_BLOCK_SLAB3, DOUBLE_STONE_BLOCK_SLAB3, BlockDoubleSlabStone3.class, V1_9_0);
        registerBlock(BlockNames.DOUBLE_STONE_BLOCK_SLAB4, ItemBlockNames.DOUBLE_STONE_BLOCK_SLAB4, DOUBLE_STONE_BLOCK_SLAB4, BlockDoubleSlabStone4.class, V1_9_0);
        registerBlock(BlockNames.GRANITE_STAIRS, ItemBlockNames.GRANITE_STAIRS, GRANITE_STAIRS, BlockStairsGranite.class, V1_9_0);
        registerBlock(BlockNames.DIORITE_STAIRS, ItemBlockNames.DIORITE_STAIRS, DIORITE_STAIRS, BlockStairsDiorite.class, V1_9_0);
        registerBlock(BlockNames.ANDESITE_STAIRS, ItemBlockNames.ANDESITE_STAIRS, ANDESITE_STAIRS, BlockStairsAndesite.class, V1_9_0);
        registerBlock(BlockNames.POLISHED_GRANITE_STAIRS, ItemBlockNames.POLISHED_GRANITE_STAIRS, POLISHED_GRANITE_STAIRS, BlockStairsPolishedGranite.class, V1_9_0);
        registerBlock(BlockNames.POLISHED_DIORITE_STAIRS, ItemBlockNames.POLISHED_DIORITE_STAIRS, POLISHED_DIORITE_STAIRS, BlockStairsPolishedDiorite.class, V1_9_0);
        registerBlock(BlockNames.POLISHED_ANDESITE_STAIRS, ItemBlockNames.POLISHED_ANDESITE_STAIRS, POLISHED_ANDESITE_STAIRS, BlockStairsPolishedAndesite.class, V1_9_0);
        registerBlock(BlockNames.MOSSY_STONE_BRICK_STAIRS, ItemBlockNames.MOSSY_STONE_BRICK_STAIRS, MOSSY_STONE_BRICK_STAIRS, BlockStairsMossyStoneBrick.class, V1_9_0);
        registerBlock(BlockNames.SMOOTH_RED_SANDSTONE_STAIRS, ItemBlockNames.SMOOTH_RED_SANDSTONE_STAIRS, SMOOTH_RED_SANDSTONE_STAIRS, BlockStairsSmoothRedSandstone.class, V1_9_0);
        registerBlock(BlockNames.SMOOTH_SANDSTONE_STAIRS, ItemBlockNames.SMOOTH_SANDSTONE_STAIRS, SMOOTH_SANDSTONE_STAIRS, BlockStairsSmoothSandstone.class, V1_9_0);
        registerBlock(BlockNames.END_BRICK_STAIRS, ItemBlockNames.END_BRICK_STAIRS, END_BRICK_STAIRS, BlockStairsEndBrick.class, V1_9_0);
        registerBlock(BlockNames.MOSSY_COBBLESTONE_STAIRS, ItemBlockNames.MOSSY_COBBLESTONE_STAIRS, MOSSY_COBBLESTONE_STAIRS, BlockStairsMossyCobblestone.class, V1_9_0);
        registerBlock(BlockNames.NORMAL_STONE_STAIRS, ItemBlockNames.NORMAL_STONE_STAIRS, NORMAL_STONE_STAIRS, BlockStairsStone.class, V1_9_0);
        registerBlock(BlockNames.SPRUCE_STANDING_SIGN, ItemBlockNames.SPRUCE_STANDING_SIGN, SPRUCE_STANDING_SIGN, BlockSignPostSpruce.class, V1_9_0);
        registerBlock(BlockNames.SPRUCE_WALL_SIGN, ItemBlockNames.SPRUCE_WALL_SIGN, SPRUCE_WALL_SIGN, BlockWallSignSpruce.class, V1_9_0);
        registerBlock(BlockNames.SMOOTH_STONE, ItemBlockNames.SMOOTH_STONE, SMOOTH_STONE, BlockSmoothStone.class, V1_9_0);
        registerBlock(BlockNames.RED_NETHER_BRICK_STAIRS, ItemBlockNames.RED_NETHER_BRICK_STAIRS, RED_NETHER_BRICK_STAIRS, BlockStairsRedNetherBrick.class, V1_9_0);
        registerBlock(BlockNames.SMOOTH_QUARTZ_STAIRS, ItemBlockNames.SMOOTH_QUARTZ_STAIRS, SMOOTH_QUARTZ_STAIRS, BlockStairsSmoothQuartz.class, V1_9_0);
        registerBlock(BlockNames.BIRCH_STANDING_SIGN, ItemBlockNames.BIRCH_STANDING_SIGN, BIRCH_STANDING_SIGN, BlockSignPostBirch.class, V1_9_0);
        registerBlock(BlockNames.BIRCH_WALL_SIGN, ItemBlockNames.BIRCH_WALL_SIGN, BIRCH_WALL_SIGN, BlockWallSignBirch.class, V1_9_0);
        registerBlock(BlockNames.JUNGLE_STANDING_SIGN, ItemBlockNames.JUNGLE_STANDING_SIGN, JUNGLE_STANDING_SIGN, BlockSignPostJungle.class, V1_9_0);
        registerBlock(BlockNames.JUNGLE_WALL_SIGN, ItemBlockNames.JUNGLE_WALL_SIGN, JUNGLE_WALL_SIGN, BlockWallSignJungle.class, V1_9_0);
        registerBlock(BlockNames.ACACIA_STANDING_SIGN, ItemBlockNames.ACACIA_STANDING_SIGN, ACACIA_STANDING_SIGN, BlockSignPostAcacia.class, V1_9_0);
        registerBlock(BlockNames.ACACIA_WALL_SIGN, ItemBlockNames.ACACIA_WALL_SIGN, ACACIA_WALL_SIGN, BlockWallSignAcacia.class, V1_9_0);
        registerBlock(BlockNames.DARKOAK_STANDING_SIGN, ItemBlockNames.DARKOAK_STANDING_SIGN, DARKOAK_STANDING_SIGN, BlockSignPostDarkOak.class, V1_9_0);
        registerBlock(BlockNames.DARKOAK_WALL_SIGN, ItemBlockNames.DARKOAK_WALL_SIGN, DARKOAK_WALL_SIGN, BlockWallSignDarkOak.class, V1_9_0);
        if (!V1_20_0.isAvailable()) {
            registerBlock(BlockNames.LAVA_CAULDRON, ItemBlockNames.LAVA_CAULDRON, LAVA_CAULDRON, BlockCauldronLava.class, V1_9_0);
        }

        registerBlock(BlockNames.LECTERN, ItemBlockNames.LECTERN, LECTERN, BlockLectern.class, V1_10_0);
        registerBlock(BlockNames.LOOM, ItemBlockNames.LOOM, LOOM, BlockLoom.class, V1_10_0);
        registerBlock(BlockNames.LANTERN, ItemBlockNames.LANTERN, LANTERN, BlockLantern.class, V1_10_0);
        registerBlock(BlockNames.JIGSAW, ItemBlockNames.JIGSAW, JIGSAW, BlockJigsaw.class, V1_10_0);
        registerBlock(BlockNames.WOOD, ItemBlockNames.WOOD, WOOD, BlockWoodBark.class, V1_10_0);

        registerBlock(BlockNames.GRINDSTONE, ItemBlockNames.GRINDSTONE, GRINDSTONE, BlockGrindstone.class, V1_11_0);
        registerBlock(BlockNames.BLAST_FURNACE, ItemBlockNames.BLAST_FURNACE, BLAST_FURNACE, BlockFurnaceBlast.class, V1_11_0);
        registerBlock(BlockNames.STONECUTTER_BLOCK, ItemBlockNames.STONECUTTER_BLOCK, STONECUTTER_BLOCK, BlockStonecutter.class, V1_11_0);
        registerBlock(BlockNames.SMOKER, ItemBlockNames.SMOKER, SMOKER, BlockSmoker.class, V1_11_0);
        registerBlock(BlockNames.LIT_SMOKER, ItemBlockNames.LIT_SMOKER, LIT_SMOKER, BlockSmokerBurning.class, V1_11_0);
        registerBlock(BlockNames.CARTOGRAPHY_TABLE, ItemBlockNames.CARTOGRAPHY_TABLE, CARTOGRAPHY_TABLE, BlockCartographyTable.class, V1_11_0);
        registerBlock(BlockNames.FLETCHING_TABLE, ItemBlockNames.FLETCHING_TABLE, FLETCHING_TABLE, BlockFletchingTable.class, V1_11_0);
        registerBlock(BlockNames.SMITHING_TABLE, ItemBlockNames.SMITHING_TABLE, SMITHING_TABLE, BlockSmithingTable.class, V1_11_0);
        registerBlock(BlockNames.BARREL, ItemBlockNames.BARREL, BARREL, BlockBarrel.class, V1_11_0);
        registerBlock(BlockNames.BELL, ItemBlockNames.BELL, BELL, BlockBell.class, V1_11_0);
        registerBlock(BlockNames.SWEET_BERRY_BUSH, ItemBlockNames.SWEET_BERRY_BUSH, SWEET_BERRY_BUSH, BlockSweetBerryBush.class, V1_11_0);
        registerBlock(BlockNames.CAMPFIRE, ItemBlockNames.ITEM_CAMPFIRE, BLOCK_CAMPFIRE, BlockCampfire.class, V1_11_0);
        registerBlock(BlockNames.COMPOSTER, ItemBlockNames.COMPOSTER, COMPOSTER, BlockComposter.class, V1_11_0);
        registerBlock(BlockNames.LIT_BLAST_FURNACE, ItemBlockNames.LIT_BLAST_FURNACE, LIT_BLAST_FURNACE, BlockFurnaceBurningBlast.class, V1_11_0);

        registerBlock(BlockNames.CAMERA, ItemBlockNames.ITEM_CAMERA, BLOCK_CAMERA, BlockCamera.class, V1_13_0);
        registerBlock(BlockNames.LIGHT_BLOCK, ItemBlockNames.LIGHT_BLOCK, LIGHT_BLOCK, BlockLight.class, V1_13_0);
        registerBlock(BlockNames.WITHER_ROSE, ItemBlockNames.WITHER_ROSE, WITHER_ROSE, BlockWitherRose.class, V1_13_0);
        registerBlock(BlockNames.STICKY_PISTON_ARM_COLLISION, ItemBlockNames.STICKY_PISTON_ARM_COLLISION, STICKY_PISTON_ARM_COLLISION, BlockPistonHeadSticky.class, V1_13_0);

        registerBlock(BlockNames.BEE_NEST, ItemBlockNames.BEE_NEST, BEE_NEST, BlockBeeNest.class, V1_14_0);
        registerBlock(BlockNames.BEEHIVE, ItemBlockNames.BEEHIVE, BEEHIVE, BlockBeehive.class, V1_14_0);
        registerBlock(BlockNames.HONEY_BLOCK, ItemBlockNames.HONEY_BLOCK, HONEY_BLOCK, BlockHoney.class, V1_14_0);
        registerBlock(BlockNames.HONEYCOMB_BLOCK, ItemBlockNames.HONEYCOMB_BLOCK, HONEYCOMB_BLOCK, BlockHoneycomb.class, V1_14_0);

        registerBlock(BlockNames.ALLOW, ItemBlockNames.ALLOW, ALLOW, BlockAllow.class, V1_16_0);
        registerBlock(BlockNames.DENY, ItemBlockNames.DENY, DENY, BlockDeny.class, V1_16_0);
        registerBlock(BlockNames.BORDER_BLOCK, ItemBlockNames.BORDER_BLOCK, BORDER_BLOCK, BlockBorder.class, V1_16_0);
        registerBlock(BlockNames.STRUCTURE_VOID, ItemBlockNames.STRUCTURE_VOID, STRUCTURE_VOID, BlockStructureVoid.class, V1_16_0);
        registerBlock(BlockNames.LODESTONE, ItemBlockNames.LODESTONE, LODESTONE, BlockLodestone.class, V1_16_0);
        registerBlock(BlockNames.CRIMSON_ROOTS, ItemBlockNames.CRIMSON_ROOTS, CRIMSON_ROOTS, BlockNetherRootsCrimson.class, V1_16_0);
        registerBlock(BlockNames.WARPED_ROOTS, ItemBlockNames.WARPED_ROOTS, WARPED_ROOTS, BlockNetherRootsWarped.class, V1_16_0);
        registerBlock(BlockNames.CRIMSON_STEM, ItemBlockNames.CRIMSON_STEM, CRIMSON_STEM, BlockFungusStemCrimson.class, V1_16_0);
        registerBlock(BlockNames.WARPED_STEM, ItemBlockNames.WARPED_STEM, WARPED_STEM, BlockFungusStemWarped.class, V1_16_0);
        registerBlock(BlockNames.WARPED_WART_BLOCK, ItemBlockNames.WARPED_WART_BLOCK, WARPED_WART_BLOCK, BlockNetherWartBlockWarped.class, V1_16_0);
        registerBlock(BlockNames.CRIMSON_FUNGUS, ItemBlockNames.CRIMSON_FUNGUS, CRIMSON_FUNGUS, BlockNetherFungusCrimson.class, V1_16_0); //TODO: onFertilized
        registerBlock(BlockNames.WARPED_FUNGUS, ItemBlockNames.WARPED_FUNGUS, WARPED_FUNGUS, BlockNetherFungusWarped.class, V1_16_0); //TODO: onFertilized
        registerBlock(BlockNames.SHROOMLIGHT, ItemBlockNames.SHROOMLIGHT, SHROOMLIGHT, BlockShroomlight.class, V1_16_0);
        registerBlock(BlockNames.WEEPING_VINES, ItemBlockNames.WEEPING_VINES, WEEPING_VINES, BlockNetherVinesWeeping.class, V1_16_0);
        registerBlock(BlockNames.CRIMSON_NYLIUM, ItemBlockNames.CRIMSON_NYLIUM, CRIMSON_NYLIUM, BlockNyliumCrimson.class, V1_16_0);
        registerBlock(BlockNames.WARPED_NYLIUM, ItemBlockNames.WARPED_NYLIUM, WARPED_NYLIUM, BlockNyliumWarped.class, V1_16_0);
        registerBlock(BlockNames.BASALT, ItemBlockNames.BASALT, BASALT, BlockBasalt.class, V1_16_0);
        registerBlock(BlockNames.POLISHED_BASALT, ItemBlockNames.POLISHED_BASALT, POLISHED_BASALT, BlockBasaltPolished.class, V1_16_0);
        registerBlock(BlockNames.SOUL_SOIL, ItemBlockNames.SOUL_SOIL, SOUL_SOIL, BlockSoulSoil.class, V1_16_0);
        registerBlock(BlockNames.SOUL_FIRE, ItemBlockNames.SOUL_FIRE, SOUL_FIRE, BlockFireBlue.class, V1_16_0);
        registerBlock(BlockNames.NETHER_SPROUTS, ItemBlockNames.ITEM_NETHER_SPROUTS, BLOCK_NETHER_SPROUTS, BlockNetherSprouts.class, V1_16_0);
        registerBlock(BlockNames.TARGET, ItemBlockNames.TARGET, TARGET, BlockTarget.class, V1_16_0);
        registerBlock(BlockNames.STRIPPED_CRIMSON_STEM, ItemBlockNames.STRIPPED_CRIMSON_STEM, STRIPPED_CRIMSON_STEM, BlockFungusStemStrippedCrimson.class, V1_16_0);
        registerBlock(BlockNames.STRIPPED_WARPED_STEM, ItemBlockNames.STRIPPED_WARPED_STEM, STRIPPED_WARPED_STEM, BlockFungusStemStrippedWarped.class, V1_16_0);
        registerBlock(BlockNames.CRIMSON_PLANKS, ItemBlockNames.CRIMSON_PLANKS, CRIMSON_PLANKS, BlockPlanksCrimson.class, V1_16_0);
        registerBlock(BlockNames.WARPED_PLANKS, ItemBlockNames.WARPED_PLANKS, WARPED_PLANKS, BlockPlanksWarped.class, V1_16_0);
        registerBlock(BlockNames.CRIMSON_DOOR, ItemBlockNames.ITEM_CRIMSON_DOOR, BLOCK_CRIMSON_DOOR, BlockDoorCrimson.class, V1_16_0);
        registerBlock(BlockNames.WARPED_DOOR, ItemBlockNames.ITEM_WARPED_DOOR, BLOCK_WARPED_DOOR, BlockDoorWarped.class, V1_16_0);
        registerBlock(BlockNames.CRIMSON_TRAPDOOR, ItemBlockNames.CRIMSON_TRAPDOOR, CRIMSON_TRAPDOOR, BlockTrapdoorCrimson.class, V1_16_0);
        registerBlock(BlockNames.WARPED_TRAPDOOR, ItemBlockNames.WARPED_TRAPDOOR, WARPED_TRAPDOOR, BlockTrapdoorWarped.class, V1_16_0);
        registerBlock(BlockNames.CRIMSON_STANDING_SIGN, ItemBlockNames.CRIMSON_STANDING_SIGN, CRIMSON_STANDING_SIGN, BlockSignPostCrimson.class, V1_16_0);
        registerBlock(BlockNames.WARPED_STANDING_SIGN, ItemBlockNames.WARPED_STANDING_SIGN, WARPED_STANDING_SIGN, BlockSignPostWarped.class, V1_16_0);
        registerBlock(BlockNames.CRIMSON_WALL_SIGN, ItemBlockNames.CRIMSON_WALL_SIGN, CRIMSON_WALL_SIGN, BlockWallSignCrimson.class, V1_16_0);
        registerBlock(BlockNames.WARPED_WALL_SIGN, ItemBlockNames.WARPED_WALL_SIGN, WARPED_WALL_SIGN, BlockWallSignWarped.class, V1_16_0);
        registerBlock(BlockNames.CRIMSON_STAIRS, ItemBlockNames.CRIMSON_STAIRS, CRIMSON_STAIRS, BlockStairsCrimson.class, V1_16_0);
        registerBlock(BlockNames.WARPED_STAIRS, ItemBlockNames.WARPED_STAIRS, WARPED_STAIRS, BlockStairsWarped.class, V1_16_0);
        registerBlock(BlockNames.CRIMSON_FENCE, ItemBlockNames.CRIMSON_FENCE, CRIMSON_FENCE, BlockFenceCrimson.class, V1_16_0);
        registerBlock(BlockNames.WARPED_FENCE, ItemBlockNames.WARPED_FENCE, WARPED_FENCE, BlockFenceWarped.class, V1_16_0);
        registerBlock(BlockNames.CRIMSON_FENCE_GATE, ItemBlockNames.CRIMSON_FENCE_GATE, CRIMSON_FENCE_GATE, BlockFenceGateCrimson.class, V1_16_0);
        registerBlock(BlockNames.WARPED_FENCE_GATE, ItemBlockNames.WARPED_FENCE_GATE, WARPED_FENCE_GATE, BlockFenceGateWarped.class, V1_16_0);
        registerBlock(BlockNames.CRIMSON_BUTTON, ItemBlockNames.CRIMSON_BUTTON, CRIMSON_BUTTON, BlockButtonCrimson.class, V1_16_0);
        registerBlock(BlockNames.WARPED_BUTTON, ItemBlockNames.WARPED_BUTTON, WARPED_BUTTON, BlockButtonWarped.class, V1_16_0);
        registerBlock(BlockNames.CRIMSON_PRESSURE_PLATE, ItemBlockNames.CRIMSON_PRESSURE_PLATE, CRIMSON_PRESSURE_PLATE, BlockPressurePlateCrimson.class, V1_16_0);
        registerBlock(BlockNames.WARPED_PRESSURE_PLATE, ItemBlockNames.WARPED_PRESSURE_PLATE, WARPED_PRESSURE_PLATE, BlockPressurePlateWarped.class, V1_16_0);
        registerBlock(BlockNames.CRIMSON_SLAB, ItemBlockNames.CRIMSON_SLAB, CRIMSON_SLAB, BlockSlabCrimson.class, V1_16_0);
        registerBlock(BlockNames.WARPED_SLAB, ItemBlockNames.WARPED_SLAB, WARPED_SLAB, BlockSlabWarped.class, V1_16_0);
        registerBlock(BlockNames.CRIMSON_DOUBLE_SLAB, ItemBlockNames.CRIMSON_DOUBLE_SLAB, CRIMSON_DOUBLE_SLAB, BlockDoubleSlabCrimson.class, V1_16_0);
        registerBlock(BlockNames.WARPED_DOUBLE_SLAB, ItemBlockNames.WARPED_DOUBLE_SLAB, WARPED_DOUBLE_SLAB, BlockDoubleSlabWarped.class, V1_16_0);
        registerBlock(BlockNames.SOUL_TORCH, ItemBlockNames.SOUL_TORCH, SOUL_TORCH, BlockTorchSoul.class, V1_16_0);
        registerBlock(BlockNames.SOUL_LANTERN, ItemBlockNames.SOUL_LANTERN, SOUL_LANTERN, BlockLanternSoul.class, V1_16_0);
        registerBlock(BlockNames.NETHERITE_BLOCK, ItemBlockNames.NETHERITE_BLOCK, NETHERITE_BLOCK, BlockNetherite.class, V1_16_0);
        registerBlock(BlockNames.ANCIENT_DEBRIS, ItemBlockNames.ANCIENT_DEBRIS, ANCIENT_DEBRIS, BlockAncientDebris.class, V1_16_0);
        registerBlock(BlockNames.RESPAWN_ANCHOR, ItemBlockNames.RESPAWN_ANCHOR, RESPAWN_ANCHOR, BlockRespawnAnchor.class, V1_16_0);
        registerBlock(BlockNames.BLACKSTONE, ItemBlockNames.BLACKSTONE, BLACKSTONE, BlockBlackstone.class, V1_16_0);
        registerBlock(BlockNames.POLISHED_BLACKSTONE_BRICKS, ItemBlockNames.POLISHED_BLACKSTONE_BRICKS, POLISHED_BLACKSTONE_BRICKS, BlockBricksBlackstonePolished.class, V1_16_0);
        registerBlock(BlockNames.POLISHED_BLACKSTONE_BRICK_STAIRS, ItemBlockNames.POLISHED_BLACKSTONE_BRICK_STAIRS, POLISHED_BLACKSTONE_BRICK_STAIRS, BlockStairsPolishedBlackstoneBrick.class, V1_16_0);
        registerBlock(BlockNames.BLACKSTONE_STAIRS, ItemBlockNames.BLACKSTONE_STAIRS, BLACKSTONE_STAIRS, BlockStairsBlackstone.class, V1_16_0);
        registerBlock(BlockNames.BLACKSTONE_WALL, ItemBlockNames.BLACKSTONE_WALL, BLACKSTONE_WALL, BlockWallBlackstone.class, V1_16_0);
        registerBlock(BlockNames.POLISHED_BLACKSTONE_BRICK_WALL, ItemBlockNames.POLISHED_BLACKSTONE_BRICK_WALL, POLISHED_BLACKSTONE_BRICK_WALL, BlockWallBlackstoneBrickPolished.class, V1_16_0);
        registerBlock(BlockNames.CHISELED_POLISHED_BLACKSTONE, ItemBlockNames.CHISELED_POLISHED_BLACKSTONE, CHISELED_POLISHED_BLACKSTONE, BlockBlackstonePolishedChiseled.class, V1_16_0);
        registerBlock(BlockNames.CRACKED_POLISHED_BLACKSTONE_BRICKS, ItemBlockNames.CRACKED_POLISHED_BLACKSTONE_BRICKS, CRACKED_POLISHED_BLACKSTONE_BRICKS, BlockBricksBlackstonePolishedCracked.class, V1_16_0);
        registerBlock(BlockNames.GILDED_BLACKSTONE, ItemBlockNames.GILDED_BLACKSTONE, GILDED_BLACKSTONE, BlockBlackstoneGilded.class, V1_16_0);
        registerBlock(BlockNames.BLACKSTONE_SLAB, ItemBlockNames.BLACKSTONE_SLAB, BLACKSTONE_SLAB, BlockSlabBlackstone.class, V1_16_0);
        registerBlock(BlockNames.BLACKSTONE_DOUBLE_SLAB, ItemBlockNames.BLACKSTONE_DOUBLE_SLAB, BLACKSTONE_DOUBLE_SLAB, BlockDoubleSlabBlackstone.class, V1_16_0);
        registerBlock(BlockNames.POLISHED_BLACKSTONE_BRICK_SLAB, ItemBlockNames.POLISHED_BLACKSTONE_BRICK_SLAB, POLISHED_BLACKSTONE_BRICK_SLAB, BlockSlabBlackstoneBrickPolished.class, V1_16_0);
        registerBlock(BlockNames.POLISHED_BLACKSTONE_BRICK_DOUBLE_SLAB, ItemBlockNames.POLISHED_BLACKSTONE_BRICK_DOUBLE_SLAB, POLISHED_BLACKSTONE_BRICK_DOUBLE_SLAB, BlockDoubleSlabBlackstoneBrickPolished.class, V1_16_0);
        registerBlock(BlockNames.CHAIN, ItemBlockNames.ITEM_CHAIN, BLOCK_CHAIN, BlockChain.class, V1_16_0);
        registerBlock(BlockNames.TWISTING_VINES, ItemBlockNames.TWISTING_VINES, TWISTING_VINES, BlockNetherVinesTwisting.class, V1_16_0);
        registerBlock(BlockNames.NETHER_GOLD_ORE, ItemBlockNames.NETHER_GOLD_ORE, NETHER_GOLD_ORE, BlockOreGoldNether.class, V1_16_0);
        registerBlock(BlockNames.CRYING_OBSIDIAN, ItemBlockNames.CRYING_OBSIDIAN, CRYING_OBSIDIAN, BlockObsidianCrying.class, V1_16_0);
        registerBlock(BlockNames.SOUL_CAMPFIRE, ItemBlockNames.ITEM_SOUL_CAMPFIRE, BLOCK_SOUL_CAMPFIRE, BlockCampfireSoul.class, V1_16_0);
        registerBlock(BlockNames.POLISHED_BLACKSTONE, ItemBlockNames.POLISHED_BLACKSTONE, POLISHED_BLACKSTONE, BlockBlackstonePolished.class, V1_16_0);
        registerBlock(BlockNames.POLISHED_BLACKSTONE_STAIRS, ItemBlockNames.POLISHED_BLACKSTONE_STAIRS, POLISHED_BLACKSTONE_STAIRS, BlockStairsPolishedBlackstone.class, V1_16_0);
        registerBlock(BlockNames.POLISHED_BLACKSTONE_SLAB, ItemBlockNames.POLISHED_BLACKSTONE_SLAB, POLISHED_BLACKSTONE_SLAB, BlockSlabBlackstonePolished.class, V1_16_0);
        registerBlock(BlockNames.POLISHED_BLACKSTONE_DOUBLE_SLAB, ItemBlockNames.POLISHED_BLACKSTONE_DOUBLE_SLAB, POLISHED_BLACKSTONE_DOUBLE_SLAB, BlockDoubleSlabBlackstonePolished.class, V1_16_0);
        registerBlock(BlockNames.POLISHED_BLACKSTONE_PRESSURE_PLATE, ItemBlockNames.POLISHED_BLACKSTONE_PRESSURE_PLATE, POLISHED_BLACKSTONE_PRESSURE_PLATE, BlockPressurePlateBlackstonePolished.class, V1_16_0);
        registerBlock(BlockNames.POLISHED_BLACKSTONE_BUTTON, ItemBlockNames.POLISHED_BLACKSTONE_BUTTON, POLISHED_BLACKSTONE_BUTTON, BlockButtonBlackstonePolished.class, V1_16_0);
        registerBlock(BlockNames.POLISHED_BLACKSTONE_WALL, ItemBlockNames.POLISHED_BLACKSTONE_WALL, POLISHED_BLACKSTONE_WALL, BlockWallBlackstonePolished.class, V1_16_0);
        registerBlock(BlockNames.WARPED_HYPHAE, ItemBlockNames.WARPED_HYPHAE, WARPED_HYPHAE, BlockHyphaeWarped.class, V1_16_0);
        registerBlock(BlockNames.CRIMSON_HYPHAE, ItemBlockNames.CRIMSON_HYPHAE, CRIMSON_HYPHAE, BlockHyphaeCrimson.class, V1_16_0);
        registerBlock(BlockNames.STRIPPED_CRIMSON_HYPHAE, ItemBlockNames.STRIPPED_CRIMSON_HYPHAE, STRIPPED_CRIMSON_HYPHAE, BlockHyphaeStrippedCrimson.class, V1_16_0);
        registerBlock(BlockNames.STRIPPED_WARPED_HYPHAE, ItemBlockNames.STRIPPED_WARPED_HYPHAE, STRIPPED_WARPED_HYPHAE, BlockHyphaeStrippedWarped.class, V1_16_0);
        registerBlock(BlockNames.CHISELED_NETHER_BRICKS, ItemBlockNames.CHISELED_NETHER_BRICKS, CHISELED_NETHER_BRICKS, BlockBricksNetherChiseled.class, V1_16_0);
        registerBlock(BlockNames.CRACKED_NETHER_BRICKS, ItemBlockNames.CRACKED_NETHER_BRICKS, CRACKED_NETHER_BRICKS, BlockBricksNetherCracked.class, V1_16_0);
        registerBlock(BlockNames.QUARTZ_BRICKS, ItemBlockNames.QUARTZ_BRICKS, QUARTZ_BRICKS, BlockBricksQuartz.class, V1_16_0);

        registerBlock(BlockNames.UNKNOWN, ItemBlockNames.UNKNOWN, UNKNOWN, BlockUnknownBlock.class, V1_16_100);

        registerBlock(BlockNames.POWDER_SNOW, ItemBlockNames.POWDER_SNOW, POWDER_SNOW, BlockSnowPowder.class, V1_17_0);
        registerBlock(BlockNames.POINTED_DRIPSTONE, ItemBlockNames.POINTED_DRIPSTONE, POINTED_DRIPSTONE, BlockDripstonePointed.class, V1_17_0);
        registerBlock(BlockNames.COPPER_ORE, ItemBlockNames.COPPER_ORE, COPPER_ORE, BlockOreCopper.class, V1_17_0);
        registerBlock(BlockNames.LIGHTNING_ROD, ItemBlockNames.LIGHTNING_ROD, LIGHTNING_ROD, BlockLightningRod.class, V1_17_0);
        registerBlock(BlockNames.DRIPSTONE_BLOCK, ItemBlockNames.DRIPSTONE_BLOCK, DRIPSTONE_BLOCK, BlockDripstone.class, V1_17_0);
        registerBlock(BlockNames.DIRT_WITH_ROOTS, ItemBlockNames.DIRT_WITH_ROOTS, DIRT_WITH_ROOTS, BlockDirtRooted.class, V1_17_0);
        registerBlock(BlockNames.HANGING_ROOTS, ItemBlockNames.HANGING_ROOTS, HANGING_ROOTS, BlockHangingRoots.class, V1_17_0);
        registerBlock(BlockNames.MOSS_BLOCK, ItemBlockNames.MOSS_BLOCK, MOSS_BLOCK, BlockMoss.class, V1_17_0); //TODO: onFertilized
        registerBlock(BlockNames.SPORE_BLOSSOM, ItemBlockNames.SPORE_BLOSSOM, SPORE_BLOSSOM, BlockSporeBlossom.class, V1_17_0);
        registerBlock(BlockNames.CAVE_VINES, ItemBlockNames.CAVE_VINES, CAVE_VINES, BlockCaveVines.class, V1_17_0);
        registerBlock(BlockNames.BIG_DRIPLEAF, ItemBlockNames.BIG_DRIPLEAF, BIG_DRIPLEAF, BlockDripleafBig.class, V1_17_0);
        registerBlock(BlockNames.AZALEA_LEAVES, ItemBlockNames.AZALEA_LEAVES, AZALEA_LEAVES, BlockLeavesAzalea.class, V1_17_0);
        registerBlock(BlockNames.AZALEA_LEAVES_FLOWERED, ItemBlockNames.AZALEA_LEAVES_FLOWERED, AZALEA_LEAVES_FLOWERED, BlockLeavesAzaleaFlowered.class, V1_17_0);
        registerBlock(BlockNames.CALCITE, ItemBlockNames.CALCITE, CALCITE, BlockCalcite.class, V1_17_0);
        registerBlock(BlockNames.AMETHYST_BLOCK, ItemBlockNames.AMETHYST_BLOCK, AMETHYST_BLOCK, BlockAmethyst.class, V1_17_0);
        registerBlock(BlockNames.BUDDING_AMETHYST, ItemBlockNames.BUDDING_AMETHYST, BUDDING_AMETHYST, BlockAmethystBudding.class, V1_17_0);
        registerBlock(BlockNames.AMETHYST_CLUSTER, ItemBlockNames.AMETHYST_CLUSTER, AMETHYST_CLUSTER, BlockAmethystCluster.class, V1_17_0);
        registerBlock(BlockNames.LARGE_AMETHYST_BUD, ItemBlockNames.LARGE_AMETHYST_BUD, LARGE_AMETHYST_BUD, BlockAmethystBudLarge.class, V1_17_0);
        registerBlock(BlockNames.MEDIUM_AMETHYST_BUD, ItemBlockNames.MEDIUM_AMETHYST_BUD, MEDIUM_AMETHYST_BUD, BlockAmethystBudMedium.class, V1_17_0);
        registerBlock(BlockNames.SMALL_AMETHYST_BUD, ItemBlockNames.SMALL_AMETHYST_BUD, SMALL_AMETHYST_BUD, BlockAmethystBudSmall.class, V1_17_0);
        registerBlock(BlockNames.TUFF, ItemBlockNames.TUFF, TUFF, BlockTuff.class, V1_17_0);
        registerBlock(BlockNames.TINTED_GLASS, ItemBlockNames.TINTED_GLASS, TINTED_GLASS, BlockGlassTinted.class, V1_17_0);
        registerBlock(BlockNames.MOSS_CARPET, ItemBlockNames.MOSS_CARPET, MOSS_CARPET, BlockCarpetMoss.class, V1_17_0);
        registerBlock(BlockNames.SMALL_DRIPLEAF_BLOCK, ItemBlockNames.SMALL_DRIPLEAF_BLOCK, SMALL_DRIPLEAF_BLOCK, BlockDripleafSmall.class, V1_17_0);
        registerBlock(BlockNames.AZALEA, ItemBlockNames.AZALEA, AZALEA, BlockAzalea.class, V1_17_0); //TODO: onFertilized
        registerBlock(BlockNames.FLOWERING_AZALEA, ItemBlockNames.FLOWERING_AZALEA, FLOWERING_AZALEA, BlockAzaleaFlowering.class, V1_17_0); //TODO: onFertilized
        registerBlock(BlockNames.GLOW_FRAME, ItemBlockNames.ITEM_GLOW_FRAME, BLOCK_GLOW_FRAME, BlockItemFrameGlow.class, V1_17_0);
        registerBlock(BlockNames.COPPER_BLOCK, ItemBlockNames.COPPER_BLOCK, COPPER_BLOCK, BlockCopper.class, V1_17_0);
        registerBlock(BlockNames.EXPOSED_COPPER, ItemBlockNames.EXPOSED_COPPER, EXPOSED_COPPER, BlockCopperExposed.class, V1_17_0);
        registerBlock(BlockNames.WEATHERED_COPPER, ItemBlockNames.WEATHERED_COPPER, WEATHERED_COPPER, BlockCopperWeathered.class, V1_17_0);
        registerBlock(BlockNames.OXIDIZED_COPPER, ItemBlockNames.OXIDIZED_COPPER, OXIDIZED_COPPER, BlockCopperOxidized.class, V1_17_0);
        registerBlock(BlockNames.WAXED_COPPER, ItemBlockNames.WAXED_COPPER, WAXED_COPPER, BlockCopperWaxed.class, V1_17_0);
        registerBlock(BlockNames.WAXED_EXPOSED_COPPER, ItemBlockNames.WAXED_EXPOSED_COPPER, WAXED_EXPOSED_COPPER, BlockCopperExposedWaxed.class, V1_17_0);
        registerBlock(BlockNames.WAXED_WEATHERED_COPPER, ItemBlockNames.WAXED_WEATHERED_COPPER, WAXED_WEATHERED_COPPER, BlockCopperWeatheredWaxed.class, V1_17_0);
        registerBlock(BlockNames.CUT_COPPER, ItemBlockNames.CUT_COPPER, CUT_COPPER, BlockCopperCut.class, V1_17_0);
        registerBlock(BlockNames.EXPOSED_CUT_COPPER, ItemBlockNames.EXPOSED_CUT_COPPER, EXPOSED_CUT_COPPER, BlockCopperCutExposed.class, V1_17_0);
        registerBlock(BlockNames.WEATHERED_CUT_COPPER, ItemBlockNames.WEATHERED_CUT_COPPER, WEATHERED_CUT_COPPER, BlockCopperCutWeathered.class, V1_17_0);
        registerBlock(BlockNames.OXIDIZED_CUT_COPPER, ItemBlockNames.OXIDIZED_CUT_COPPER, OXIDIZED_CUT_COPPER, BlockCopperCutOxidized.class, V1_17_0);
        registerBlock(BlockNames.WAXED_CUT_COPPER, ItemBlockNames.WAXED_CUT_COPPER, WAXED_CUT_COPPER, BlockCopperCutWaxed.class, V1_17_0);
        registerBlock(BlockNames.WAXED_EXPOSED_CUT_COPPER, ItemBlockNames.WAXED_EXPOSED_CUT_COPPER, WAXED_EXPOSED_CUT_COPPER, BlockCopperCutExposedWaxed.class, V1_17_0);
        registerBlock(BlockNames.WAXED_WEATHERED_CUT_COPPER, ItemBlockNames.WAXED_WEATHERED_CUT_COPPER, WAXED_WEATHERED_CUT_COPPER, BlockCopperCutWeatheredWaxed.class, V1_17_0);
        registerBlock(BlockNames.CUT_COPPER_STAIRS, ItemBlockNames.CUT_COPPER_STAIRS, CUT_COPPER_STAIRS, BlockStairsCopperCut.class, V1_17_0);
        registerBlock(BlockNames.EXPOSED_CUT_COPPER_STAIRS, ItemBlockNames.EXPOSED_CUT_COPPER_STAIRS, EXPOSED_CUT_COPPER_STAIRS, BlockStairsCopperCutExposed.class, V1_17_0);
        registerBlock(BlockNames.WEATHERED_CUT_COPPER_STAIRS, ItemBlockNames.WEATHERED_CUT_COPPER_STAIRS, WEATHERED_CUT_COPPER_STAIRS, BlockStairsCopperCutWeathered.class, V1_17_0);
        registerBlock(BlockNames.OXIDIZED_CUT_COPPER_STAIRS, ItemBlockNames.OXIDIZED_CUT_COPPER_STAIRS, OXIDIZED_CUT_COPPER_STAIRS, BlockStairsCopperCutOxidized.class, V1_17_0);
        registerBlock(BlockNames.WAXED_CUT_COPPER_STAIRS, ItemBlockNames.WAXED_CUT_COPPER_STAIRS, WAXED_CUT_COPPER_STAIRS, BlockStairsCopperCutWaxed.class, V1_17_0);
        registerBlock(BlockNames.WAXED_EXPOSED_CUT_COPPER_STAIRS, ItemBlockNames.WAXED_EXPOSED_CUT_COPPER_STAIRS, WAXED_EXPOSED_CUT_COPPER_STAIRS, BlockStairsCopperCutExposedWaxed.class, V1_17_0);
        registerBlock(BlockNames.WAXED_WEATHERED_CUT_COPPER_STAIRS, ItemBlockNames.WAXED_WEATHERED_CUT_COPPER_STAIRS, WAXED_WEATHERED_CUT_COPPER_STAIRS, BlockStairsCopperCutWeatheredWaxed.class, V1_17_0);
        registerBlock(BlockNames.CUT_COPPER_SLAB, ItemBlockNames.CUT_COPPER_SLAB, CUT_COPPER_SLAB, BlockSlabCopperCut.class, V1_17_0);
        registerBlock(BlockNames.EXPOSED_CUT_COPPER_SLAB, ItemBlockNames.EXPOSED_CUT_COPPER_SLAB, EXPOSED_CUT_COPPER_SLAB, BlockSlabCopperCutExposed.class, V1_17_0);
        registerBlock(BlockNames.WEATHERED_CUT_COPPER_SLAB, ItemBlockNames.WEATHERED_CUT_COPPER_SLAB, WEATHERED_CUT_COPPER_SLAB, BlockSlabCopperCutWeathered.class, V1_17_0);
        registerBlock(BlockNames.OXIDIZED_CUT_COPPER_SLAB, ItemBlockNames.OXIDIZED_CUT_COPPER_SLAB, OXIDIZED_CUT_COPPER_SLAB, BlockSlabCopperCutOxidized.class, V1_17_0);
        registerBlock(BlockNames.WAXED_CUT_COPPER_SLAB, ItemBlockNames.WAXED_CUT_COPPER_SLAB, WAXED_CUT_COPPER_SLAB, BlockSlabCopperCutWaxed.class, V1_17_0);
        registerBlock(BlockNames.WAXED_EXPOSED_CUT_COPPER_SLAB, ItemBlockNames.WAXED_EXPOSED_CUT_COPPER_SLAB, WAXED_EXPOSED_CUT_COPPER_SLAB, BlockSlabCopperCutExposedWaxed.class, V1_17_0);
        registerBlock(BlockNames.WAXED_WEATHERED_CUT_COPPER_SLAB, ItemBlockNames.WAXED_WEATHERED_CUT_COPPER_SLAB, WAXED_WEATHERED_CUT_COPPER_SLAB, BlockSlabCopperCutWeatheredWaxed.class, V1_17_0);
        registerBlock(BlockNames.DOUBLE_CUT_COPPER_SLAB, ItemBlockNames.DOUBLE_CUT_COPPER_SLAB, DOUBLE_CUT_COPPER_SLAB, BlockDoubleSlabCopperCut.class, V1_17_0);
        registerBlock(BlockNames.EXPOSED_DOUBLE_CUT_COPPER_SLAB, ItemBlockNames.EXPOSED_DOUBLE_CUT_COPPER_SLAB, EXPOSED_DOUBLE_CUT_COPPER_SLAB, BlockDoubleSlabCopperCutExposed.class, V1_17_0);
        registerBlock(BlockNames.WEATHERED_DOUBLE_CUT_COPPER_SLAB, ItemBlockNames.WEATHERED_DOUBLE_CUT_COPPER_SLAB, WEATHERED_DOUBLE_CUT_COPPER_SLAB, BlockDoubleSlabCopperCutWeathered.class, V1_17_0);
        registerBlock(BlockNames.OXIDIZED_DOUBLE_CUT_COPPER_SLAB, ItemBlockNames.OXIDIZED_DOUBLE_CUT_COPPER_SLAB, OXIDIZED_DOUBLE_CUT_COPPER_SLAB, BlockDoubleSlabCopperCutOxidized.class, V1_17_0);
        registerBlock(BlockNames.WAXED_DOUBLE_CUT_COPPER_SLAB, ItemBlockNames.WAXED_DOUBLE_CUT_COPPER_SLAB, WAXED_DOUBLE_CUT_COPPER_SLAB, BlockDoubleSlabCopperCutWaxed.class, V1_17_0);
        registerBlock(BlockNames.WAXED_EXPOSED_DOUBLE_CUT_COPPER_SLAB, ItemBlockNames.WAXED_EXPOSED_DOUBLE_CUT_COPPER_SLAB, WAXED_EXPOSED_DOUBLE_CUT_COPPER_SLAB, BlockDoubleSlabCopperCutExposedWaxed.class, V1_17_0);
        registerBlock(BlockNames.WAXED_WEATHERED_DOUBLE_CUT_COPPER_SLAB, ItemBlockNames.WAXED_WEATHERED_DOUBLE_CUT_COPPER_SLAB, WAXED_WEATHERED_DOUBLE_CUT_COPPER_SLAB, BlockDoubleSlabCopperCutWeatheredWaxed.class, V1_17_0);
        registerBlock(BlockNames.CAVE_VINES_BODY_WITH_BERRIES, ItemBlockNames.CAVE_VINES_BODY_WITH_BERRIES, CAVE_VINES_BODY_WITH_BERRIES, BlockCaveVinesBerriesBody.class, V1_17_0);
        registerBlock(BlockNames.CAVE_VINES_HEAD_WITH_BERRIES, ItemBlockNames.CAVE_VINES_HEAD_WITH_BERRIES, CAVE_VINES_HEAD_WITH_BERRIES, BlockCaveVinesBerriesHead.class, V1_17_0);
        registerBlock(BlockNames.SMOOTH_BASALT, ItemBlockNames.SMOOTH_BASALT, SMOOTH_BASALT, BlockBasaltSmooth.class, V1_17_0);
        registerBlock(BlockNames.DEEPSLATE, ItemBlockNames.DEEPSLATE, DEEPSLATE, BlockDeepslate.class, V1_17_0);
        registerBlock(BlockNames.COBBLED_DEEPSLATE, ItemBlockNames.COBBLED_DEEPSLATE, COBBLED_DEEPSLATE, BlockDeepslateCobbled.class, V1_17_0);
        registerBlock(BlockNames.COBBLED_DEEPSLATE_SLAB, ItemBlockNames.COBBLED_DEEPSLATE_SLAB, COBBLED_DEEPSLATE_SLAB, BlockSlabDeepslateCobbled.class, V1_17_0);
        registerBlock(BlockNames.COBBLED_DEEPSLATE_STAIRS, ItemBlockNames.COBBLED_DEEPSLATE_STAIRS, COBBLED_DEEPSLATE_STAIRS, BlockStairsDeepslateCobbled.class, V1_17_0);
        registerBlock(BlockNames.COBBLED_DEEPSLATE_WALL, ItemBlockNames.COBBLED_DEEPSLATE_WALL, COBBLED_DEEPSLATE_WALL, BlockWallDeepslateCobbled.class, V1_17_0);
        registerBlock(BlockNames.POLISHED_DEEPSLATE, ItemBlockNames.POLISHED_DEEPSLATE, POLISHED_DEEPSLATE, BlockDeepslatePolished.class, V1_17_0);
        registerBlock(BlockNames.POLISHED_DEEPSLATE_SLAB, ItemBlockNames.POLISHED_DEEPSLATE_SLAB, POLISHED_DEEPSLATE_SLAB, BlockSlabDeepslatePolished.class, V1_17_0);
        registerBlock(BlockNames.POLISHED_DEEPSLATE_STAIRS, ItemBlockNames.POLISHED_DEEPSLATE_STAIRS, POLISHED_DEEPSLATE_STAIRS, BlockStairsDeepslatePolished.class, V1_17_0);
        registerBlock(BlockNames.POLISHED_DEEPSLATE_WALL, ItemBlockNames.POLISHED_DEEPSLATE_WALL, POLISHED_DEEPSLATE_WALL, BlockWallDeepslatePolished.class, V1_17_0);
        registerBlock(BlockNames.DEEPSLATE_TILES, ItemBlockNames.DEEPSLATE_TILES, DEEPSLATE_TILES, BlockDeepslateTiles.class, V1_17_0);
        registerBlock(BlockNames.DEEPSLATE_TILE_SLAB, ItemBlockNames.DEEPSLATE_TILE_SLAB, DEEPSLATE_TILE_SLAB, BlockSlabDeepslateTile.class, V1_17_0);
        registerBlock(BlockNames.DEEPSLATE_TILE_STAIRS, ItemBlockNames.DEEPSLATE_TILE_STAIRS, DEEPSLATE_TILE_STAIRS, BlockStairsDeepslateTile.class, V1_17_0);
        registerBlock(BlockNames.DEEPSLATE_TILE_WALL, ItemBlockNames.DEEPSLATE_TILE_WALL, DEEPSLATE_TILE_WALL, BlockWallDeepslateTile.class, V1_17_0);
        registerBlock(BlockNames.DEEPSLATE_BRICKS, ItemBlockNames.DEEPSLATE_BRICKS, DEEPSLATE_BRICKS, BlockBricksDeepslate.class, V1_17_0);
        registerBlock(BlockNames.DEEPSLATE_BRICK_SLAB, ItemBlockNames.DEEPSLATE_BRICK_SLAB, DEEPSLATE_BRICK_SLAB, BlockSlabDeepslateBrick.class, V1_17_0);
        registerBlock(BlockNames.DEEPSLATE_BRICK_STAIRS, ItemBlockNames.DEEPSLATE_BRICK_STAIRS, DEEPSLATE_BRICK_STAIRS, BlockStairsDeepslateBrick.class, V1_17_0);
        registerBlock(BlockNames.DEEPSLATE_BRICK_WALL, ItemBlockNames.DEEPSLATE_BRICK_WALL, DEEPSLATE_BRICK_WALL, BlockWallDeepslateBrick.class, V1_17_0);
        registerBlock(BlockNames.CHISELED_DEEPSLATE, ItemBlockNames.CHISELED_DEEPSLATE, CHISELED_DEEPSLATE, BlockDeepslateChiseled.class, V1_17_0);
        registerBlock(BlockNames.COBBLED_DEEPSLATE_DOUBLE_SLAB, ItemBlockNames.COBBLED_DEEPSLATE_DOUBLE_SLAB, COBBLED_DEEPSLATE_DOUBLE_SLAB, BlockDoubleSlabDeepslateCobbled.class, V1_17_0);
        registerBlock(BlockNames.POLISHED_DEEPSLATE_DOUBLE_SLAB, ItemBlockNames.POLISHED_DEEPSLATE_DOUBLE_SLAB, POLISHED_DEEPSLATE_DOUBLE_SLAB, BlockDoubleSlabDeepslatePolished.class, V1_17_0);
        registerBlock(BlockNames.DEEPSLATE_TILE_DOUBLE_SLAB, ItemBlockNames.DEEPSLATE_TILE_DOUBLE_SLAB, DEEPSLATE_TILE_DOUBLE_SLAB, BlockDoubleSlabDeepslateTile.class, V1_17_0);
        registerBlock(BlockNames.DEEPSLATE_BRICK_DOUBLE_SLAB, ItemBlockNames.DEEPSLATE_BRICK_DOUBLE_SLAB, DEEPSLATE_BRICK_DOUBLE_SLAB, BlockDoubleSlabDeepslateBrick.class, V1_17_0);
        registerBlock(BlockNames.DEEPSLATE_LAPIS_ORE, ItemBlockNames.DEEPSLATE_LAPIS_ORE, DEEPSLATE_LAPIS_ORE, BlockOreLapisDeepslate.class, V1_17_0);
        registerBlock(BlockNames.DEEPSLATE_IRON_ORE, ItemBlockNames.DEEPSLATE_IRON_ORE, DEEPSLATE_IRON_ORE, BlockOreIronDeepslate.class, V1_17_0);
        registerBlock(BlockNames.DEEPSLATE_GOLD_ORE, ItemBlockNames.DEEPSLATE_GOLD_ORE, DEEPSLATE_GOLD_ORE, BlockOreGoldDeepslate.class, V1_17_0);
        registerBlock(BlockNames.DEEPSLATE_REDSTONE_ORE, ItemBlockNames.DEEPSLATE_REDSTONE_ORE, DEEPSLATE_REDSTONE_ORE, BlockOreRedstoneDeepslate.class, V1_17_0);
        registerBlock(BlockNames.LIT_DEEPSLATE_REDSTONE_ORE, ItemBlockNames.LIT_DEEPSLATE_REDSTONE_ORE, LIT_DEEPSLATE_REDSTONE_ORE, BlockOreRedstoneGlowingDeepslate.class, V1_17_0);
        registerBlock(BlockNames.DEEPSLATE_DIAMOND_ORE, ItemBlockNames.DEEPSLATE_DIAMOND_ORE, DEEPSLATE_DIAMOND_ORE, BlockOreDiamondDeepslate.class, V1_17_0);
        registerBlock(BlockNames.DEEPSLATE_COAL_ORE, ItemBlockNames.DEEPSLATE_COAL_ORE, DEEPSLATE_COAL_ORE, BlockOreCoalDeepslate.class, V1_17_0);
        registerBlock(BlockNames.DEEPSLATE_EMERALD_ORE, ItemBlockNames.DEEPSLATE_EMERALD_ORE, DEEPSLATE_EMERALD_ORE, BlockOreEmeraldDeepslate.class, V1_17_0);
        registerBlock(BlockNames.DEEPSLATE_COPPER_ORE, ItemBlockNames.DEEPSLATE_COPPER_ORE, DEEPSLATE_COPPER_ORE, BlockOreCopperDeepslate.class, V1_17_0);
        registerBlock(BlockNames.CRACKED_DEEPSLATE_TILES, ItemBlockNames.CRACKED_DEEPSLATE_TILES, CRACKED_DEEPSLATE_TILES, BlockDeepslateTilesCracked.class, V1_17_0);
        registerBlock(BlockNames.CRACKED_DEEPSLATE_BRICKS, ItemBlockNames.CRACKED_DEEPSLATE_BRICKS, CRACKED_DEEPSLATE_BRICKS, BlockBricksDeepslateCracked.class, V1_17_0);
        registerBlock(BlockNames.GLOW_LICHEN, ItemBlockNames.GLOW_LICHEN, GLOW_LICHEN, BlockGlowLichen.class, V1_17_0); //TODO: onFertilized
        registerBlock(BlockNames.WAXED_OXIDIZED_COPPER, ItemBlockNames.WAXED_OXIDIZED_COPPER, WAXED_OXIDIZED_COPPER, BlockCopperOxidizedWaxed.class, V1_17_0);
        registerBlock(BlockNames.WAXED_OXIDIZED_CUT_COPPER, ItemBlockNames.WAXED_OXIDIZED_CUT_COPPER, WAXED_OXIDIZED_CUT_COPPER, BlockCopperCutOxidizedWaxed.class, V1_17_0);
        registerBlock(BlockNames.WAXED_OXIDIZED_CUT_COPPER_STAIRS, ItemBlockNames.WAXED_OXIDIZED_CUT_COPPER_STAIRS, WAXED_OXIDIZED_CUT_COPPER_STAIRS, BlockStairsCopperCutOxidizedWaxed.class, V1_17_0);
        registerBlock(BlockNames.WAXED_OXIDIZED_CUT_COPPER_SLAB, ItemBlockNames.WAXED_OXIDIZED_CUT_COPPER_SLAB, WAXED_OXIDIZED_CUT_COPPER_SLAB, BlockSlabCopperCutOxidizedWaxed.class, V1_17_0);
        registerBlock(BlockNames.WAXED_OXIDIZED_DOUBLE_CUT_COPPER_SLAB, ItemBlockNames.WAXED_OXIDIZED_DOUBLE_CUT_COPPER_SLAB, WAXED_OXIDIZED_DOUBLE_CUT_COPPER_SLAB, BlockDoubleSlabCopperCutOxidizedWaxed.class, V1_17_0);
        registerBlock(BlockNames.RAW_IRON_BLOCK, ItemBlockNames.RAW_IRON_BLOCK, RAW_IRON_BLOCK, BlockIronRaw.class, V1_17_0);
        registerBlock(BlockNames.RAW_COPPER_BLOCK, ItemBlockNames.RAW_COPPER_BLOCK, RAW_COPPER_BLOCK, BlockCopperRaw.class, V1_17_0);
        registerBlock(BlockNames.RAW_GOLD_BLOCK, ItemBlockNames.RAW_GOLD_BLOCK, RAW_GOLD_BLOCK, BlockGoldRaw.class, V1_17_0);
        registerBlock(BlockNames.INFESTED_DEEPSLATE, ItemBlockNames.INFESTED_DEEPSLATE, INFESTED_DEEPSLATE, BlockDeepslateInfested.class, V1_17_0);

        registerBlock(BlockNames.CANDLE, ItemBlockNames.CANDLE, CANDLE, BlockCandle.class, V1_17_10);
        registerBlock(BlockNames.WHITE_CANDLE, ItemBlockNames.WHITE_CANDLE, WHITE_CANDLE, BlockCandleWhite.class, V1_17_10);
        registerBlock(BlockNames.ORANGE_CANDLE, ItemBlockNames.ORANGE_CANDLE, ORANGE_CANDLE, BlockCandleOrange.class, V1_17_10);
        registerBlock(BlockNames.MAGENTA_CANDLE, ItemBlockNames.MAGENTA_CANDLE, MAGENTA_CANDLE, BlockCandleMagenta.class, V1_17_10);
        registerBlock(BlockNames.LIGHT_BLUE_CANDLE, ItemBlockNames.LIGHT_BLUE_CANDLE, LIGHT_BLUE_CANDLE, BlockCandleLightBlue.class, V1_17_10);
        registerBlock(BlockNames.YELLOW_CANDLE, ItemBlockNames.YELLOW_CANDLE, YELLOW_CANDLE, BlockCandleYellow.class, V1_17_10);
        registerBlock(BlockNames.LIME_CANDLE, ItemBlockNames.LIME_CANDLE, LIME_CANDLE, BlockCandleLime.class, V1_17_10);
        registerBlock(BlockNames.PINK_CANDLE, ItemBlockNames.PINK_CANDLE, PINK_CANDLE, BlockCandlePink.class, V1_17_10);
        registerBlock(BlockNames.GRAY_CANDLE, ItemBlockNames.GRAY_CANDLE, GRAY_CANDLE, BlockCandleGray.class, V1_17_10);
        registerBlock(BlockNames.LIGHT_GRAY_CANDLE, ItemBlockNames.LIGHT_GRAY_CANDLE, LIGHT_GRAY_CANDLE, BlockCandleLightGray.class, V1_17_10);
        registerBlock(BlockNames.CYAN_CANDLE, ItemBlockNames.CYAN_CANDLE, CYAN_CANDLE, BlockCandleCyan.class, V1_17_10);
        registerBlock(BlockNames.PURPLE_CANDLE, ItemBlockNames.PURPLE_CANDLE, PURPLE_CANDLE, BlockCandlePurple.class, V1_17_10);
        registerBlock(BlockNames.BLUE_CANDLE, ItemBlockNames.BLUE_CANDLE, BLUE_CANDLE, BlockCandleBlue.class, V1_17_10);
        registerBlock(BlockNames.BROWN_CANDLE, ItemBlockNames.BROWN_CANDLE, BROWN_CANDLE, BlockCandleBrown.class, V1_17_10);
        registerBlock(BlockNames.GREEN_CANDLE, ItemBlockNames.GREEN_CANDLE, GREEN_CANDLE, BlockCandleGreen.class, V1_17_10);
        registerBlock(BlockNames.RED_CANDLE, ItemBlockNames.RED_CANDLE, RED_CANDLE, BlockCandleRed.class, V1_17_10);
        registerBlock(BlockNames.BLACK_CANDLE, ItemBlockNames.BLACK_CANDLE, BLACK_CANDLE, BlockCandleBlack.class, V1_17_10);
        registerBlock(BlockNames.CANDLE_CAKE, ItemBlockNames.CANDLE_CAKE, CANDLE_CAKE, BlockCakeCandle.class, V1_17_10);
        registerBlock(BlockNames.WHITE_CANDLE_CAKE, ItemBlockNames.WHITE_CANDLE_CAKE, WHITE_CANDLE_CAKE, BlockCakeCandleWhite.class, V1_17_10);
        registerBlock(BlockNames.ORANGE_CANDLE_CAKE, ItemBlockNames.ORANGE_CANDLE_CAKE, ORANGE_CANDLE_CAKE, BlockCakeCandleOrange.class, V1_17_10);
        registerBlock(BlockNames.MAGENTA_CANDLE_CAKE, ItemBlockNames.MAGENTA_CANDLE_CAKE, MAGENTA_CANDLE_CAKE, BlockCakeCandleMagenta.class, V1_17_10);
        registerBlock(BlockNames.LIGHT_BLUE_CANDLE_CAKE, ItemBlockNames.LIGHT_BLUE_CANDLE_CAKE, LIGHT_BLUE_CANDLE_CAKE, BlockCakeCandleLightBlue.class, V1_17_10);
        registerBlock(BlockNames.YELLOW_CANDLE_CAKE, ItemBlockNames.YELLOW_CANDLE_CAKE, YELLOW_CANDLE_CAKE, BlockCakeCandleYellow.class, V1_17_10);
        registerBlock(BlockNames.LIME_CANDLE_CAKE, ItemBlockNames.LIME_CANDLE_CAKE, LIME_CANDLE_CAKE, BlockCakeCandleLime.class, V1_17_10);
        registerBlock(BlockNames.PINK_CANDLE_CAKE, ItemBlockNames.PINK_CANDLE_CAKE, PINK_CANDLE_CAKE, BlockCakeCandlePink.class, V1_17_10);
        registerBlock(BlockNames.GRAY_CANDLE_CAKE, ItemBlockNames.GRAY_CANDLE_CAKE, GRAY_CANDLE_CAKE, BlockCakeCandleGray.class, V1_17_10);
        registerBlock(BlockNames.LIGHT_GRAY_CANDLE_CAKE, ItemBlockNames.LIGHT_GRAY_CANDLE_CAKE, LIGHT_GRAY_CANDLE_CAKE, BlockCakeCandleLightGray.class, V1_17_10);
        registerBlock(BlockNames.CYAN_CANDLE_CAKE, ItemBlockNames.CYAN_CANDLE_CAKE, CYAN_CANDLE_CAKE, BlockCakeCandleCyan.class, V1_17_10);
        registerBlock(BlockNames.PURPLE_CANDLE_CAKE, ItemBlockNames.PURPLE_CANDLE_CAKE, PURPLE_CANDLE_CAKE, BlockCakeCandlePurple.class, V1_17_10);
        registerBlock(BlockNames.BLUE_CANDLE_CAKE, ItemBlockNames.BLUE_CANDLE_CAKE, BLUE_CANDLE_CAKE, BlockCakeCandleBlue.class, V1_17_10);
        registerBlock(BlockNames.BROWN_CANDLE_CAKE, ItemBlockNames.BROWN_CANDLE_CAKE, BROWN_CANDLE_CAKE, BlockCakeCandleBrown.class, V1_17_10);
        registerBlock(BlockNames.GREEN_CANDLE_CAKE, ItemBlockNames.GREEN_CANDLE_CAKE, GREEN_CANDLE_CAKE, BlockCakeCandleGreen.class, V1_17_10);
        registerBlock(BlockNames.RED_CANDLE_CAKE, ItemBlockNames.RED_CANDLE_CAKE, RED_CANDLE_CAKE, BlockCakeCandleRed.class, V1_17_10);
        registerBlock(BlockNames.BLACK_CANDLE_CAKE, ItemBlockNames.BLACK_CANDLE_CAKE, BLACK_CANDLE_CAKE, BlockCakeCandleBlack.class, V1_17_10);

        registerBlock(BlockNames.CLIENT_REQUEST_PLACEHOLDER_BLOCK, ItemBlockNames.CLIENT_REQUEST_PLACEHOLDER_BLOCK, CLIENT_REQUEST_PLACEHOLDER_BLOCK, BlockClientRequestPlaceholder.class, V1_17_40);

        registerBlock(BlockNames.SCULK_SENSOR, ItemBlockNames.SCULK_SENSOR, SCULK_SENSOR, BlockSculkSensor.class, V1_19_0);
        registerBlock(BlockNames.SCULK, ItemBlockNames.SCULK, SCULK, BlockSculk.class, V1_19_0);
        registerBlock(BlockNames.SCULK_VEIN, ItemBlockNames.SCULK_VEIN, SCULK_VEIN, BlockSculkVein.class, V1_19_0);
        registerBlock(BlockNames.SCULK_CATALYST, ItemBlockNames.SCULK_CATALYST, SCULK_CATALYST, BlockSculkCatalyst.class, V1_19_0);
        registerBlock(BlockNames.SCULK_SHRIEKER, ItemBlockNames.SCULK_SHRIEKER, SCULK_SHRIEKER, BlockSculkShrieker.class, V1_19_0);
        registerBlock(BlockNames.REINFORCED_DEEPSLATE, ItemBlockNames.REINFORCED_DEEPSLATE, REINFORCED_DEEPSLATE, BlockDeepslateReinforced.class, V1_19_0);
        registerBlock(BlockNames.FROG_SPAWN, ItemBlockNames.FROG_SPAWN, FROG_SPAWN, BlockFrogSpawn.class, V1_19_0);
        registerBlock(BlockNames.PEARLESCENT_FROGLIGHT, ItemBlockNames.PEARLESCENT_FROGLIGHT, PEARLESCENT_FROGLIGHT, BlockFroglightPearlescent.class, V1_19_0);
        registerBlock(BlockNames.VERDANT_FROGLIGHT, ItemBlockNames.VERDANT_FROGLIGHT, VERDANT_FROGLIGHT, BlockFroglightVerdant.class, V1_19_0);
        registerBlock(BlockNames.OCHRE_FROGLIGHT, ItemBlockNames.OCHRE_FROGLIGHT, OCHRE_FROGLIGHT, BlockFroglightOchre.class, V1_19_0);
        registerBlock(BlockNames.MANGROVE_LEAVES, ItemBlockNames.MANGROVE_LEAVES, MANGROVE_LEAVES, BlockLeavesMangrove.class, V1_19_0);
        registerBlock(BlockNames.MUD, ItemBlockNames.MUD, MUD, BlockMud.class, V1_19_0);
        registerBlock(BlockNames.MANGROVE_PROPAGULE, ItemBlockNames.MANGROVE_PROPAGULE, MANGROVE_PROPAGULE, BlockMangrovePropagule.class, V1_19_0); //TODO: onFertilized
        registerBlock(BlockNames.MUD_BRICKS, ItemBlockNames.MUD_BRICKS, MUD_BRICKS, BlockBricksMud.class, V1_19_0);
        registerBlock(BlockNames.PACKED_MUD, ItemBlockNames.PACKED_MUD, PACKED_MUD, BlockMudPacked.class, V1_19_0);
        registerBlock(BlockNames.MUD_BRICK_SLAB, ItemBlockNames.MUD_BRICK_SLAB, MUD_BRICK_SLAB, BlockSlabMudBrick.class, V1_19_0);
        registerBlock(BlockNames.MUD_BRICK_DOUBLE_SLAB, ItemBlockNames.MUD_BRICK_DOUBLE_SLAB, MUD_BRICK_DOUBLE_SLAB, BlockDoubleSlabMudBrick.class, V1_19_0);
        registerBlock(BlockNames.MUD_BRICK_STAIRS, ItemBlockNames.MUD_BRICK_STAIRS, MUD_BRICK_STAIRS, BlockStairsMudBrick.class, V1_19_0);
        registerBlock(BlockNames.MUD_BRICK_WALL, ItemBlockNames.MUD_BRICK_WALL, MUD_BRICK_WALL, BlockWallMudBrick.class, V1_19_0);
        registerBlock(BlockNames.MANGROVE_ROOTS, ItemBlockNames.MANGROVE_ROOTS, MANGROVE_ROOTS, BlockMangroveRoots.class, V1_19_0);
        registerBlock(BlockNames.MUDDY_MANGROVE_ROOTS, ItemBlockNames.MUDDY_MANGROVE_ROOTS, MUDDY_MANGROVE_ROOTS, BlockMangroveRootsMuddy.class, V1_19_0);
        registerBlock(BlockNames.MANGROVE_LOG, ItemBlockNames.MANGROVE_LOG, MANGROVE_LOG, BlockLogMangrove.class, V1_19_0);
        registerBlock(BlockNames.STRIPPED_MANGROVE_LOG, ItemBlockNames.STRIPPED_MANGROVE_LOG, STRIPPED_MANGROVE_LOG, BlockLogStrippedMangrove.class, V1_19_0);
        registerBlock(BlockNames.MANGROVE_PLANKS, ItemBlockNames.MANGROVE_PLANKS, MANGROVE_PLANKS, BlockPlanksMangrove.class, V1_19_0);
        registerBlock(BlockNames.MANGROVE_BUTTON, ItemBlockNames.MANGROVE_BUTTON, MANGROVE_BUTTON, BlockButtonMangrove.class, V1_19_0);
        registerBlock(BlockNames.MANGROVE_STAIRS, ItemBlockNames.MANGROVE_STAIRS, MANGROVE_STAIRS, BlockStairsMangrove.class, V1_19_0);
        registerBlock(BlockNames.MANGROVE_SLAB, ItemBlockNames.MANGROVE_SLAB, MANGROVE_SLAB, BlockSlabMangrove.class, V1_19_0);
        registerBlock(BlockNames.MANGROVE_PRESSURE_PLATE, ItemBlockNames.MANGROVE_PRESSURE_PLATE, MANGROVE_PRESSURE_PLATE, BlockPressurePlateMangrove.class, V1_19_0);
        registerBlock(BlockNames.MANGROVE_FENCE, ItemBlockNames.MANGROVE_FENCE, MANGROVE_FENCE, BlockFenceMangrove.class, V1_19_0);
        registerBlock(BlockNames.MANGROVE_FENCE_GATE, ItemBlockNames.MANGROVE_FENCE_GATE, MANGROVE_FENCE_GATE, BlockFenceGateMangrove.class, V1_19_0);
        registerBlock(BlockNames.MANGROVE_DOOR, ItemBlockNames.ITEM_MANGROVE_DOOR, BLOCK_MANGROVE_DOOR, BlockDoorMangrove.class, V1_19_0);
        registerBlock(BlockNames.MANGROVE_STANDING_SIGN, ItemBlockNames.MANGROVE_STANDING_SIGN, MANGROVE_STANDING_SIGN, BlockSignPostMangrove.class, V1_19_0);
        registerBlock(BlockNames.MANGROVE_WALL_SIGN, ItemBlockNames.MANGROVE_WALL_SIGN, MANGROVE_WALL_SIGN, BlockWallSignMangrove.class, V1_19_0);
        registerBlock(BlockNames.MANGROVE_TRAPDOOR, ItemBlockNames.MANGROVE_TRAPDOOR, MANGROVE_TRAPDOOR, BlockTrapdoorMangrove.class, V1_19_0);
        registerBlock(BlockNames.MANGROVE_WOOD, ItemBlockNames.MANGROVE_WOOD, MANGROVE_WOOD, BlockWoodMangrove.class, V1_19_0);
        registerBlock(BlockNames.STRIPPED_MANGROVE_WOOD, ItemBlockNames.STRIPPED_MANGROVE_WOOD, STRIPPED_MANGROVE_WOOD, BlockWoodStrippedMangrove.class, V1_19_0);
        registerBlock(BlockNames.MANGROVE_DOUBLE_SLAB, ItemBlockNames.MANGROVE_DOUBLE_SLAB, MANGROVE_DOUBLE_SLAB, BlockDoubleSlabMangrove.class, V1_19_0);

        registerBlock(BlockNames.OAK_HANGING_SIGN, ItemBlockNames.OAK_HANGING_SIGN, OAK_HANGING_SIGN, BlockHangingSignOak.class, V1_20_0);
        registerBlock(BlockNames.SPRUCE_HANGING_SIGN, ItemBlockNames.SPRUCE_HANGING_SIGN, SPRUCE_HANGING_SIGN, BlockHangingSignSpruce.class, V1_20_0);
        registerBlock(BlockNames.BIRCH_HANGING_SIGN, ItemBlockNames.BIRCH_HANGING_SIGN, BIRCH_HANGING_SIGN, BlockHangingSignBirch.class, V1_20_0);
        registerBlock(BlockNames.JUNGLE_HANGING_SIGN, ItemBlockNames.JUNGLE_HANGING_SIGN, JUNGLE_HANGING_SIGN, BlockHangingSignJungle.class, V1_20_0);
        registerBlock(BlockNames.ACACIA_HANGING_SIGN, ItemBlockNames.ACACIA_HANGING_SIGN, ACACIA_HANGING_SIGN, BlockHangingSignAcacia.class, V1_20_0);
        registerBlock(BlockNames.DARK_OAK_HANGING_SIGN, ItemBlockNames.DARK_OAK_HANGING_SIGN, DARK_OAK_HANGING_SIGN, BlockHangingSignDarkOak.class, V1_20_0);
        registerBlock(BlockNames.CRIMSON_HANGING_SIGN, ItemBlockNames.CRIMSON_HANGING_SIGN, CRIMSON_HANGING_SIGN, BlockHangingSignCrimson.class, V1_20_0);
        registerBlock(BlockNames.WARPED_HANGING_SIGN, ItemBlockNames.WARPED_HANGING_SIGN, WARPED_HANGING_SIGN, BlockHangingSignWarped.class, V1_20_0);
        registerBlock(BlockNames.MANGROVE_HANGING_SIGN, ItemBlockNames.MANGROVE_HANGING_SIGN, MANGROVE_HANGING_SIGN, BlockHangingSignMangrove.class, V1_20_0);
        registerBlock(BlockNames.BAMBOO_MOSAIC, ItemBlockNames.BAMBOO_MOSAIC, BAMBOO_MOSAIC, BlockBambooMosaic.class, V1_20_0);
        registerBlock(BlockNames.BAMBOO_PLANKS, ItemBlockNames.BAMBOO_PLANKS, BAMBOO_PLANKS, BlockPlanksBamboo.class, V1_20_0);
        registerBlock(BlockNames.BAMBOO_BUTTON, ItemBlockNames.BAMBOO_BUTTON, BAMBOO_BUTTON, BlockButtonBamboo.class, V1_20_0);
        registerBlock(BlockNames.BAMBOO_STAIRS, ItemBlockNames.BAMBOO_STAIRS, BAMBOO_STAIRS, BlockStairsBamboo.class, V1_20_0);
        registerBlock(BlockNames.BAMBOO_SLAB, ItemBlockNames.BAMBOO_SLAB, BAMBOO_SLAB, BlockSlabBamboo.class, V1_20_0);
        registerBlock(BlockNames.BAMBOO_PRESSURE_PLATE, ItemBlockNames.BAMBOO_PRESSURE_PLATE, BAMBOO_PRESSURE_PLATE, BlockPressurePlateBamboo.class, V1_20_0);
        registerBlock(BlockNames.BAMBOO_FENCE, ItemBlockNames.BAMBOO_FENCE, BAMBOO_FENCE, BlockFenceBamboo.class, V1_20_0);
        registerBlock(BlockNames.BAMBOO_FENCE_GATE, ItemBlockNames.BAMBOO_FENCE_GATE, BAMBOO_FENCE_GATE, BlockFenceGateBamboo.class, V1_20_0);
        registerBlock(BlockNames.BAMBOO_DOOR, ItemBlockNames.BAMBOO_DOOR, BAMBOO_DOOR, BlockDoorBamboo.class, V1_20_0);
        registerBlock(BlockNames.BAMBOO_STANDING_SIGN, ItemBlockNames.BAMBOO_STANDING_SIGN, BAMBOO_STANDING_SIGN, BlockSignPostBamboo.class, V1_20_0);
        registerBlock(BlockNames.BAMBOO_WALL_SIGN, ItemBlockNames.BAMBOO_WALL_SIGN, BAMBOO_WALL_SIGN, BlockWallSignBamboo.class, V1_20_0);
        registerBlock(BlockNames.BAMBOO_TRAPDOOR, ItemBlockNames.BAMBOO_TRAPDOOR, BAMBOO_TRAPDOOR, BlockTrapdoorBamboo.class, V1_20_0);
        registerBlock(BlockNames.BAMBOO_DOUBLE_SLAB, ItemBlockNames.BAMBOO_DOUBLE_SLAB, BAMBOO_DOUBLE_SLAB, BlockDoubleSlabBamboo.class, V1_20_0);
        registerBlock(BlockNames.BAMBOO_HANGING_SIGN, ItemBlockNames.BAMBOO_HANGING_SIGN, BAMBOO_HANGING_SIGN, BlockHangingSignBamboo.class, V1_20_0);
        registerBlock(BlockNames.BAMBOO_MOSAIC_STAIRS, ItemBlockNames.BAMBOO_MOSAIC_STAIRS, BAMBOO_MOSAIC_STAIRS, BlockStairsBambooMosaic.class, V1_20_0);
        registerBlock(BlockNames.BAMBOO_MOSAIC_SLAB, ItemBlockNames.BAMBOO_MOSAIC_SLAB, BAMBOO_MOSAIC_SLAB, BlockSlabBambooMosaic.class, V1_20_0);
        registerBlock(BlockNames.BAMBOO_MOSAIC_DOUBLE_SLAB, ItemBlockNames.BAMBOO_MOSAIC_DOUBLE_SLAB, BAMBOO_MOSAIC_DOUBLE_SLAB, BlockDoubleSlabBambooMosaic.class, V1_20_0);
        registerBlock(BlockNames.CHISELED_BOOKSHELF, ItemBlockNames.CHISELED_BOOKSHELF, CHISELED_BOOKSHELF, BlockBookshelfChiseled.class, V1_20_0);
        registerBlock(BlockNames.BAMBOO_BLOCK, ItemBlockNames.BAMBOO_BLOCK, BAMBOO_BLOCK, BlockBambooBlock.class, V1_20_0);
        registerBlock(BlockNames.STRIPPED_BAMBOO_BLOCK, ItemBlockNames.STRIPPED_BAMBOO_BLOCK, STRIPPED_BAMBOO_BLOCK, BlockBambooBlockStripped.class, V1_20_0);
        registerBlock(BlockNames.SUSPICIOUS_SAND, ItemBlockNames.SUSPICIOUS_SAND, SUSPICIOUS_SAND, BlockSandSuspicious.class, V1_20_0);
        registerBlock(BlockNames.CHERRY_BUTTON, ItemBlockNames.CHERRY_BUTTON, CHERRY_BUTTON, BlockButtonCherry.class, V1_20_0);
        registerBlock(BlockNames.CHERRY_DOOR, ItemBlockNames.CHERRY_DOOR, CHERRY_DOOR, BlockDoorCherry.class, V1_20_0);
        registerBlock(BlockNames.CHERRY_FENCE, ItemBlockNames.CHERRY_FENCE, CHERRY_FENCE, BlockFenceCherry.class, V1_20_0);
        registerBlock(BlockNames.CHERRY_FENCE_GATE, ItemBlockNames.CHERRY_FENCE_GATE, CHERRY_FENCE_GATE, BlockFenceGateCherry.class, V1_20_0);
        registerBlock(BlockNames.CHERRY_HANGING_SIGN, ItemBlockNames.CHERRY_HANGING_SIGN, CHERRY_HANGING_SIGN, BlockHangingSignCherry.class, V1_20_0);
        registerBlock(BlockNames.STRIPPED_CHERRY_LOG, ItemBlockNames.STRIPPED_CHERRY_LOG, STRIPPED_CHERRY_LOG, BlockLogStrippedCherry.class, V1_20_0);
        registerBlock(BlockNames.CHERRY_LOG, ItemBlockNames.CHERRY_LOG, CHERRY_LOG, BlockLogCherry.class, V1_20_0);
        registerBlock(BlockNames.CHERRY_PLANKS, ItemBlockNames.CHERRY_PLANKS, CHERRY_PLANKS, BlockPlanksCherry.class, V1_20_0);
        registerBlock(BlockNames.CHERRY_PRESSURE_PLATE, ItemBlockNames.CHERRY_PRESSURE_PLATE, CHERRY_PRESSURE_PLATE, BlockPressurePlateCherry.class, V1_20_0);
        registerBlock(BlockNames.CHERRY_SLAB, ItemBlockNames.CHERRY_SLAB, CHERRY_SLAB, BlockSlabCherry.class, V1_20_0);
        registerBlock(BlockNames.CHERRY_DOUBLE_SLAB, ItemBlockNames.CHERRY_DOUBLE_SLAB, CHERRY_DOUBLE_SLAB, BlockDoubleSlabCherry.class, V1_20_0);
        registerBlock(BlockNames.CHERRY_STAIRS, ItemBlockNames.CHERRY_STAIRS, CHERRY_STAIRS, BlockStairsCherry.class, V1_20_0);
        registerBlock(BlockNames.CHERRY_STANDING_SIGN, ItemBlockNames.CHERRY_STANDING_SIGN, CHERRY_STANDING_SIGN, BlockSignPostCherry.class, V1_20_0);
        registerBlock(BlockNames.CHERRY_TRAPDOOR, ItemBlockNames.CHERRY_TRAPDOOR, CHERRY_TRAPDOOR, BlockTrapdoorCherry.class, V1_20_0);
        registerBlock(BlockNames.CHERRY_WALL_SIGN, ItemBlockNames.CHERRY_WALL_SIGN, CHERRY_WALL_SIGN, BlockWallSignCherry.class, V1_20_0);
        registerBlock(BlockNames.STRIPPED_CHERRY_WOOD, ItemBlockNames.STRIPPED_CHERRY_WOOD, STRIPPED_CHERRY_WOOD, BlockWoodStrippedCherry.class, V1_20_0);
        registerBlock(BlockNames.CHERRY_WOOD, ItemBlockNames.CHERRY_WOOD, CHERRY_WOOD, BlockWoodCherry.class, V1_20_0);
        registerBlock(BlockNames.CHERRY_SAPLING, ItemBlockNames.CHERRY_SAPLING, CHERRY_SAPLING, BlockSaplingCherry.class, V1_20_0); //TODO: onFertilized
        registerBlock(BlockNames.CHERRY_LEAVES, ItemBlockNames.CHERRY_LEAVES, CHERRY_LEAVES, BlockLeavesCherry.class, V1_20_0);
        registerBlock(BlockNames.PINK_PETALS, ItemBlockNames.PINK_PETALS, PINK_PETALS, BlockPinkPetals.class, V1_20_0);
        registerBlock(BlockNames.DECORATED_POT, ItemBlockNames.DECORATED_POT, DECORATED_POT, BlockDecoratedPot.class, V1_20_0);
        registerBlock(BlockNames.TORCHFLOWER_CROP, ItemBlockNames.TORCHFLOWER_CROP, TORCHFLOWER_CROP, BlockTorchflowerCrop.class, V1_20_0);
        registerBlock(BlockNames.TORCHFLOWER, ItemBlockNames.TORCHFLOWER, TORCHFLOWER, BlockTorchflower.class, V1_20_0);
        registerBlock(BlockNames.SUSPICIOUS_GRAVEL, ItemBlockNames.SUSPICIOUS_GRAVEL, SUSPICIOUS_GRAVEL, BlockGravelSuspicious.class, V1_20_0);
        registerBlock(BlockNames.PITCHER_CROP, ItemBlockNames.PITCHER_CROP, PITCHER_CROP, BlockPitcherCrop.class, V1_20_0);
        registerBlock(BlockNames.CALIBRATED_SCULK_SENSOR, ItemBlockNames.CALIBRATED_SCULK_SENSOR, CALIBRATED_SCULK_SENSOR, BlockSculkSensorCalibrated.class, V1_20_0);
        registerBlock(BlockNames.SNIFFER_EGG, ItemBlockNames.SNIFFER_EGG, SNIFFER_EGG, BlockSnifferEgg.class, V1_20_0);
        registerBlock(BlockNames.PITCHER_PLANT, ItemBlockNames.PITCHER_PLANT, PITCHER_PLANT, BlockPitcherPlant.class, V1_20_0);

        registerBlock(BlockNames.CRAFTER, ItemBlockNames.CRAFTER, CRAFTER, BlockCrafter.class, V1_21_0); //TODO
        registerBlock(BlockNames.VAULT, ItemBlockNames.VAULT, VAULT, BlockVault.class, V1_21_0); //TODO
        registerBlock(BlockNames.TRIAL_SPAWNER, ItemBlockNames.TRIAL_SPAWNER, TRIAL_SPAWNER, BlockTrialSpawner.class, V1_21_0); //TODO
        registerBlock(BlockNames.HEAVY_CORE, ItemBlockNames.HEAVY_CORE, HEAVY_CORE, BlockHeavyCore.class, V1_21_0);
        registerBlock(BlockNames.TUFF_SLAB, ItemBlockNames.TUFF_SLAB, TUFF_SLAB, BlockSlabTuff.class, V1_21_0);
        registerBlock(BlockNames.TUFF_DOUBLE_SLAB, ItemBlockNames.TUFF_DOUBLE_SLAB, TUFF_DOUBLE_SLAB, BlockDoubleSlabTuff.class, V1_21_0);
        registerBlock(BlockNames.TUFF_STAIRS, ItemBlockNames.TUFF_STAIRS, TUFF_STAIRS, BlockStairsTuff.class, V1_21_0);
        registerBlock(BlockNames.TUFF_WALL, ItemBlockNames.TUFF_WALL, TUFF_WALL, BlockWallTuff.class, V1_21_0);
        registerBlock(BlockNames.POLISHED_TUFF, ItemBlockNames.POLISHED_TUFF, POLISHED_TUFF, BlockTuffPolished.class, V1_21_0);
        registerBlock(BlockNames.POLISHED_TUFF_SLAB, ItemBlockNames.POLISHED_TUFF_SLAB, POLISHED_TUFF_SLAB, BlockSlabPolishedTuff.class, V1_21_0);
        registerBlock(BlockNames.POLISHED_TUFF_DOUBLE_SLAB, ItemBlockNames.POLISHED_TUFF_DOUBLE_SLAB, POLISHED_TUFF_DOUBLE_SLAB, BlockDoubleSlabPolishedTuff.class, V1_21_0);
        registerBlock(BlockNames.POLISHED_TUFF_STAIRS, ItemBlockNames.POLISHED_TUFF_STAIRS, POLISHED_TUFF_STAIRS, BlockStairsPolishedTuff.class, V1_21_0);
        registerBlock(BlockNames.POLISHED_TUFF_WALL, ItemBlockNames.POLISHED_TUFF_WALL, POLISHED_TUFF_WALL, BlockWallPolishedTuff.class, V1_21_0);
        registerBlock(BlockNames.CHISELED_TUFF, ItemBlockNames.CHISELED_TUFF, CHISELED_TUFF, BlockTuffChiseled.class, V1_21_0);
        registerBlock(BlockNames.TUFF_BRICKS, ItemBlockNames.TUFF_BRICKS, TUFF_BRICKS, BlockBricksTuff.class, V1_21_0);
        registerBlock(BlockNames.TUFF_BRICK_SLAB, ItemBlockNames.TUFF_BRICK_SLAB, TUFF_BRICK_SLAB, BlockSlabTuffBrick.class, V1_21_0);
        registerBlock(BlockNames.TUFF_BRICK_DOUBLE_SLAB, ItemBlockNames.TUFF_BRICK_DOUBLE_SLAB, TUFF_BRICK_DOUBLE_SLAB, BlockDoubleSlabTuffBrick.class, V1_21_0);
        registerBlock(BlockNames.TUFF_BRICK_STAIRS, ItemBlockNames.TUFF_BRICK_STAIRS, TUFF_BRICK_STAIRS, BlockStairsTuffBrick.class, V1_21_0);
        registerBlock(BlockNames.TUFF_BRICK_WALL, ItemBlockNames.TUFF_BRICK_WALL, TUFF_BRICK_WALL, BlockWallTuffBrick.class, V1_21_0);
        registerBlock(BlockNames.CHISELED_TUFF_BRICKS, ItemBlockNames.CHISELED_TUFF_BRICKS, CHISELED_TUFF_BRICKS, BlockBricksTuffChiseled.class, V1_21_0);
        registerBlock(BlockNames.CHISELED_COPPER, ItemBlockNames.CHISELED_COPPER, CHISELED_COPPER, BlockCopperChiseled.class, V1_21_0);
        registerBlock(BlockNames.EXPOSED_CHISELED_COPPER, ItemBlockNames.EXPOSED_CHISELED_COPPER, EXPOSED_CHISELED_COPPER, BlockCopperChiseledExposed.class, V1_21_0);
        registerBlock(BlockNames.WEATHERED_CHISELED_COPPER, ItemBlockNames.WEATHERED_CHISELED_COPPER, WEATHERED_CHISELED_COPPER, BlockCopperChiseledWeathered.class, V1_21_0);
        registerBlock(BlockNames.OXIDIZED_CHISELED_COPPER, ItemBlockNames.OXIDIZED_CHISELED_COPPER, OXIDIZED_CHISELED_COPPER, BlockCopperChiseledOxidized.class, V1_21_0);
        registerBlock(BlockNames.WAXED_CHISELED_COPPER, ItemBlockNames.WAXED_CHISELED_COPPER, WAXED_CHISELED_COPPER, BlockCopperChiseledWaxed.class, V1_21_0);
        registerBlock(BlockNames.WAXED_EXPOSED_CHISELED_COPPER, ItemBlockNames.WAXED_EXPOSED_CHISELED_COPPER, WAXED_EXPOSED_CHISELED_COPPER, BlockCopperChiseledExposedWaxed.class, V1_21_0);
        registerBlock(BlockNames.WAXED_OXIDIZED_CHISELED_COPPER, ItemBlockNames.WAXED_OXIDIZED_CHISELED_COPPER, WAXED_OXIDIZED_CHISELED_COPPER, BlockCopperChiseledWeatheredWaxed.class, V1_21_0);
        registerBlock(BlockNames.WAXED_WEATHERED_CHISELED_COPPER, ItemBlockNames.WAXED_WEATHERED_CHISELED_COPPER, WAXED_WEATHERED_CHISELED_COPPER, BlockCopperChiseledOxidizedWaxed.class, V1_21_0);
        registerBlock(BlockNames.COPPER_GRATE, ItemBlockNames.COPPER_GRATE, COPPER_GRATE, BlockGrateCopper.class, V1_21_0);
        registerBlock(BlockNames.EXPOSED_COPPER_GRATE, ItemBlockNames.EXPOSED_COPPER_GRATE, EXPOSED_COPPER_GRATE, BlockGrateCopperExposed.class, V1_21_0);
        registerBlock(BlockNames.WEATHERED_COPPER_GRATE, ItemBlockNames.WEATHERED_COPPER_GRATE, WEATHERED_COPPER_GRATE, BlockGrateCopperWeathered.class, V1_21_0);
        registerBlock(BlockNames.OXIDIZED_COPPER_GRATE, ItemBlockNames.OXIDIZED_COPPER_GRATE, OXIDIZED_COPPER_GRATE, BlockGrateCopperOxidized.class, V1_21_0);
        registerBlock(BlockNames.WAXED_COPPER_GRATE, ItemBlockNames.WAXED_COPPER_GRATE, WAXED_COPPER_GRATE, BlockGrateCopperWaxed.class, V1_21_0);
        registerBlock(BlockNames.WAXED_EXPOSED_COPPER_GRATE, ItemBlockNames.WAXED_EXPOSED_COPPER_GRATE, WAXED_EXPOSED_COPPER_GRATE, BlockGrateCopperExposedWaxed.class, V1_21_0);
        registerBlock(BlockNames.WAXED_WEATHERED_COPPER_GRATE, ItemBlockNames.WAXED_WEATHERED_COPPER_GRATE, WAXED_WEATHERED_COPPER_GRATE, BlockGrateCopperWeatheredWaxed.class, V1_21_0);
        registerBlock(BlockNames.WAXED_OXIDIZED_COPPER_GRATE, ItemBlockNames.WAXED_OXIDIZED_COPPER_GRATE, WAXED_OXIDIZED_COPPER_GRATE, BlockGrateCopperOxidizedWaxed.class, V1_21_0);
        registerBlock(BlockNames.COPPER_BULB, ItemBlockNames.COPPER_BULB, COPPER_BULB, BlockBulbCopper.class, V1_21_0);
        registerBlock(BlockNames.EXPOSED_COPPER_BULB, ItemBlockNames.EXPOSED_COPPER_BULB, EXPOSED_COPPER_BULB, BlockBulbCopperExposed.class, V1_21_0);
        registerBlock(BlockNames.WEATHERED_COPPER_BULB, ItemBlockNames.WEATHERED_COPPER_BULB, WEATHERED_COPPER_BULB, BlockBulbCopperWeathered.class, V1_21_0);
        registerBlock(BlockNames.OXIDIZED_COPPER_BULB, ItemBlockNames.OXIDIZED_COPPER_BULB, OXIDIZED_COPPER_BULB, BlockBulbCopperOxidized.class, V1_21_0);
        registerBlock(BlockNames.WAXED_COPPER_BULB, ItemBlockNames.WAXED_COPPER_BULB, WAXED_COPPER_BULB, BlockBulbCopperWaxed.class, V1_21_0);
        registerBlock(BlockNames.WAXED_EXPOSED_COPPER_BULB, ItemBlockNames.WAXED_EXPOSED_COPPER_BULB, WAXED_EXPOSED_COPPER_BULB, BlockBulbCopperExposedWaxed.class, V1_21_0);
        registerBlock(BlockNames.WAXED_WEATHERED_COPPER_BULB, ItemBlockNames.WAXED_WEATHERED_COPPER_BULB, WAXED_WEATHERED_COPPER_BULB, BlockBulbCopperWeatheredWaxed.class, V1_21_0);
        registerBlock(BlockNames.WAXED_OXIDIZED_COPPER_BULB, ItemBlockNames.WAXED_OXIDIZED_COPPER_BULB, WAXED_OXIDIZED_COPPER_BULB, BlockBulbCopperOxidizedWaxed.class, V1_21_0);
        registerBlock(BlockNames.COPPER_DOOR, ItemBlockNames.COPPER_DOOR, COPPER_DOOR, BlockDoorCopper.class, V1_21_0);
        registerBlock(BlockNames.EXPOSED_COPPER_DOOR, ItemBlockNames.EXPOSED_COPPER_DOOR, EXPOSED_COPPER_DOOR, BlockDoorCopperExposed.class, V1_21_0);
        registerBlock(BlockNames.WEATHERED_COPPER_DOOR, ItemBlockNames.WEATHERED_COPPER_DOOR, WEATHERED_COPPER_DOOR, BlockDoorCopperWeathered.class, V1_21_0);
        registerBlock(BlockNames.OXIDIZED_COPPER_DOOR, ItemBlockNames.OXIDIZED_COPPER_DOOR, OXIDIZED_COPPER_DOOR, BlockDoorCopperOxidized.class, V1_21_0);
        registerBlock(BlockNames.WAXED_COPPER_DOOR, ItemBlockNames.WAXED_COPPER_DOOR, WAXED_COPPER_DOOR, BlockDoorCopperWaxed.class, V1_21_0);
        registerBlock(BlockNames.WAXED_EXPOSED_COPPER_DOOR, ItemBlockNames.WAXED_EXPOSED_COPPER_DOOR, WAXED_EXPOSED_COPPER_DOOR, BlockDoorCopperExposedWaxed.class, V1_21_0);
        registerBlock(BlockNames.WAXED_WEATHERED_COPPER_DOOR, ItemBlockNames.WAXED_WEATHERED_COPPER_DOOR, WAXED_WEATHERED_COPPER_DOOR, BlockDoorCopperWeatheredWaxed.class, V1_21_0);
        registerBlock(BlockNames.WAXED_OXIDIZED_COPPER_DOOR, ItemBlockNames.WAXED_OXIDIZED_COPPER_DOOR, WAXED_OXIDIZED_COPPER_DOOR, BlockDoorCopperOxidizedWaxed.class, V1_21_0);
        registerBlock(BlockNames.COPPER_TRAPDOOR, ItemBlockNames.COPPER_TRAPDOOR, COPPER_TRAPDOOR, BlockTrapdoorCopper.class, V1_21_0);
        registerBlock(BlockNames.EXPOSED_COPPER_TRAPDOOR, ItemBlockNames.EXPOSED_COPPER_TRAPDOOR, EXPOSED_COPPER_TRAPDOOR, BlockTrapdoorCopperExposed.class, V1_21_0);
        registerBlock(BlockNames.WEATHERED_COPPER_TRAPDOOR, ItemBlockNames.WEATHERED_COPPER_TRAPDOOR, WEATHERED_COPPER_TRAPDOOR, BlockTrapdoorCopperWeathered.class, V1_21_0);
        registerBlock(BlockNames.OXIDIZED_COPPER_TRAPDOOR, ItemBlockNames.OXIDIZED_COPPER_TRAPDOOR, OXIDIZED_COPPER_TRAPDOOR, BlockTrapdoorCopperOxidized.class, V1_21_0);
        registerBlock(BlockNames.WAXED_COPPER_TRAPDOOR, ItemBlockNames.WAXED_COPPER_TRAPDOOR, WAXED_COPPER_TRAPDOOR, BlockTrapdoorCopperWaxed.class, V1_21_0);
        registerBlock(BlockNames.WAXED_EXPOSED_COPPER_TRAPDOOR, ItemBlockNames.WAXED_EXPOSED_COPPER_TRAPDOOR, WAXED_EXPOSED_COPPER_TRAPDOOR, BlockTrapdoorCopperExposedWaxed.class, V1_21_0);
        registerBlock(BlockNames.WAXED_WEATHERED_COPPER_TRAPDOOR, ItemBlockNames.WAXED_WEATHERED_COPPER_TRAPDOOR, WAXED_WEATHERED_COPPER_TRAPDOOR, BlockTrapdoorCopperWeatheredWaxed.class, V1_21_0);
        registerBlock(BlockNames.WAXED_OXIDIZED_COPPER_TRAPDOOR, ItemBlockNames.WAXED_OXIDIZED_COPPER_TRAPDOOR, WAXED_OXIDIZED_COPPER_TRAPDOOR, BlockTrapdoorCopperOxidizedWaxed.class, V1_21_0);

        registerBlock(BlockNames.CHALKBOARD, ItemBlockNames.CHALKBOARD, CHALKBOARD, BlockChalkboard.class, V1_21_50);
        registerBlock(BlockNames.PALE_OAK_BUTTON, ItemBlockNames.PALE_OAK_BUTTON, PALE_OAK_BUTTON, BlockButtonPaleOak.class, V1_21_50);
        registerBlock(BlockNames.PALE_OAK_DOOR, ItemBlockNames.PALE_OAK_DOOR, PALE_OAK_DOOR, BlockDoorPaleOak.class, V1_21_50);
        registerBlock(BlockNames.PALE_OAK_FENCE, ItemBlockNames.PALE_OAK_FENCE, PALE_OAK_FENCE, BlockFencePaleOak.class, V1_21_50);
        registerBlock(BlockNames.PALE_OAK_FENCE_GATE, ItemBlockNames.PALE_OAK_FENCE_GATE, PALE_OAK_FENCE_GATE, BlockFenceGatePaleOak.class, V1_21_50);
        registerBlock(BlockNames.PALE_OAK_HANGING_SIGN, ItemBlockNames.PALE_OAK_HANGING_SIGN, PALE_OAK_HANGING_SIGN, BlockHangingSignPaleOak.class, V1_21_50);
        registerBlock(BlockNames.STRIPPED_PALE_OAK_LOG, ItemBlockNames.STRIPPED_PALE_OAK_LOG, STRIPPED_PALE_OAK_LOG, BlockLogStrippedPaleOak.class, V1_21_50);
        registerBlock(BlockNames.PALE_OAK_LOG, ItemBlockNames.PALE_OAK_LOG, PALE_OAK_LOG, BlockLogPaleOak.class, V1_21_50);
        registerBlock(BlockNames.PALE_OAK_PLANKS, ItemBlockNames.PALE_OAK_PLANKS, PALE_OAK_PLANKS, BlockPlanksPaleOak.class, V1_21_50);
        registerBlock(BlockNames.PALE_OAK_PRESSURE_PLATE, ItemBlockNames.PALE_OAK_PRESSURE_PLATE, PALE_OAK_PRESSURE_PLATE, BlockPressurePlatePaleOak.class, V1_21_50);
        registerBlock(BlockNames.PALE_OAK_SLAB, ItemBlockNames.PALE_OAK_SLAB, PALE_OAK_SLAB, BlockSlabPaleOak.class, V1_21_50);
        registerBlock(BlockNames.PALE_OAK_DOUBLE_SLAB, ItemBlockNames.PALE_OAK_DOUBLE_SLAB, PALE_OAK_DOUBLE_SLAB, BlockDoubleSlabPaleOak.class, V1_21_50);
        registerBlock(BlockNames.PALE_OAK_STAIRS, ItemBlockNames.PALE_OAK_STAIRS, PALE_OAK_STAIRS, BlockStairsPaleOak.class, V1_21_50);
        registerBlock(BlockNames.PALE_OAK_STANDING_SIGN, ItemBlockNames.PALE_OAK_STANDING_SIGN, PALE_OAK_STANDING_SIGN, BlockSignPostPaleOak.class, V1_21_50);
        registerBlock(BlockNames.PALE_OAK_TRAPDOOR, ItemBlockNames.PALE_OAK_TRAPDOOR, PALE_OAK_TRAPDOOR, BlockTrapdoorPaleOak.class, V1_21_50);
        registerBlock(BlockNames.PALE_OAK_WALL_SIGN, ItemBlockNames.PALE_OAK_WALL_SIGN, PALE_OAK_WALL_SIGN, BlockWallSignPaleOak.class, V1_21_50);
        registerBlock(BlockNames.STRIPPED_PALE_OAK_WOOD, ItemBlockNames.STRIPPED_PALE_OAK_WOOD, STRIPPED_PALE_OAK_WOOD, BlockWoodStrippedPaleOak.class, V1_21_50);
        registerBlock(BlockNames.PALE_OAK_WOOD, ItemBlockNames.PALE_OAK_WOOD, PALE_OAK_WOOD, BlockWoodPaleOak.class, V1_21_50);
        registerBlock(BlockNames.PALE_OAK_SAPLING, ItemBlockNames.PALE_OAK_SAPLING, PALE_OAK_SAPLING, BlockSaplingPaleOak.class, V1_21_50); //TODO: onFertilized
        registerBlock(BlockNames.PALE_OAK_LEAVES, ItemBlockNames.PALE_OAK_LEAVES, PALE_OAK_LEAVES, BlockLeavesPaleOak.class, V1_21_50);
        registerBlock(BlockNames.PALE_MOSS_BLOCK, ItemBlockNames.PALE_MOSS_BLOCK, PALE_MOSS_BLOCK, BlockMossPale.class, V1_21_50);
        registerBlock(BlockNames.PALE_MOSS_CARPET, ItemBlockNames.PALE_MOSS_CARPET, PALE_MOSS_CARPET, BlockCarpetMossPale.class, V1_21_50);
        registerBlock(BlockNames.PALE_HANGING_MOSS, ItemBlockNames.PALE_HANGING_MOSS, PALE_HANGING_MOSS, BlockHangingMossPale.class, V1_21_50);
        registerBlock(BlockNames.CREAKING_HEART, ItemBlockNames.CREAKING_HEART, CREAKING_HEART, BlockCreakingHeart.class, V1_21_50);
        registerBlock(BlockNames.RESIN_BRICKS, ItemBlockNames.RESIN_BRICKS, RESIN_BRICKS, BlockBricks.class, V1_21_50);
        registerBlock(BlockNames.RESIN_BRICK_SLAB, ItemBlockNames.RESIN_BRICK_SLAB, RESIN_BRICK_SLAB, BlockSlabResinBrick.class, V1_21_50);
        registerBlock(BlockNames.RESIN_BRICK_DOUBLE_SLAB, ItemBlockNames.RESIN_BRICK_DOUBLE_SLAB, RESIN_BRICK_DOUBLE_SLAB, BlockDoubleSlabResinBrick.class, V1_21_50);
        registerBlock(BlockNames.RESIN_BRICK_STAIRS, ItemBlockNames.RESIN_BRICK_STAIRS, RESIN_BRICK_STAIRS, BlockStairsResinBrick.class, V1_21_50);
        registerBlock(BlockNames.RESIN_BRICK_WALL, ItemBlockNames.RESIN_BRICK_WALL, RESIN_BRICK_WALL, BlockWallResinBrick.class, V1_21_50);
        registerBlock(BlockNames.OPEN_EYEBLOSSOM, ItemBlockNames.OPEN_EYEBLOSSOM, OPEN_EYEBLOSSOM, BlockEyeblossomOpen.class, V1_21_50);
        registerBlock(BlockNames.CLOSED_EYEBLOSSOM, ItemBlockNames.CLOSED_EYEBLOSSOM, CLOSED_EYEBLOSSOM, BlockEyeblossomClosed.class, V1_21_50);
        registerBlock(BlockNames.CHISELED_RESIN_BRICKS, ItemBlockNames.CHISELED_RESIN_BRICKS, CHISELED_RESIN_BRICKS, BlockBricksResinChiseled.class, V1_21_50);
        registerBlock(BlockNames.RESIN_BLOCK, ItemBlockNames.RESIN_BLOCK, RESIN_BLOCK, BlockResin.class, V1_21_50);
        registerBlock(BlockNames.RESIN_CLUMP, ItemBlockNames.RESIN_CLUMP, RESIN_CLUMP, BlockResinClump.class, V1_21_50);

        registerFlattenedBlocks();

        registerBlockAliases();
        registerItemAliases();
        registerComplexAliases();
    }

    private static void registerFlattenedBlocks() {
        registerBlock(BlockNames.WHITE_WOOL, ItemBlockNames.WHITE_WOOL, WHITE_WOOL, BlockWoolWhite.class, V1_19_70);
        registerBlock(BlockNames.ORANGE_WOOL, ItemBlockNames.ORANGE_WOOL, ORANGE_WOOL, BlockWoolOrange.class, V1_19_70);
        registerBlock(BlockNames.MAGENTA_WOOL, ItemBlockNames.MAGENTA_WOOL, MAGENTA_WOOL, BlockWoolMagenta.class, V1_19_70);
        registerBlock(BlockNames.LIGHT_BLUE_WOOL, ItemBlockNames.LIGHT_BLUE_WOOL, LIGHT_BLUE_WOOL, BlockWoolLightBlue.class, V1_19_70);
        registerBlock(BlockNames.YELLOW_WOOL, ItemBlockNames.YELLOW_WOOL, YELLOW_WOOL, BlockWoolYellow.class, V1_19_70);
        registerBlock(BlockNames.LIME_WOOL, ItemBlockNames.LIME_WOOL, LIME_WOOL, BlockWoolLime.class, V1_19_70);
        registerBlock(BlockNames.PINK_WOOL, ItemBlockNames.PINK_WOOL, PINK_WOOL, BlockWoolPink.class, V1_19_70);
        registerBlock(BlockNames.GRAY_WOOL, ItemBlockNames.GRAY_WOOL, GRAY_WOOL, BlockWoolGray.class, V1_19_70);
        registerBlock(BlockNames.LIGHT_GRAY_WOOL, ItemBlockNames.LIGHT_GRAY_WOOL, LIGHT_GRAY_WOOL, BlockWoolLightGray.class, V1_19_70);
        registerBlock(BlockNames.CYAN_WOOL, ItemBlockNames.CYAN_WOOL, CYAN_WOOL, BlockWoolCyan.class, V1_19_70);
        registerBlock(BlockNames.PURPLE_WOOL, ItemBlockNames.PURPLE_WOOL, PURPLE_WOOL, BlockWoolPurple.class, V1_19_70);
        registerBlock(BlockNames.BLUE_WOOL, ItemBlockNames.BLUE_WOOL, BLUE_WOOL, BlockWoolBlue.class, V1_19_70);
        registerBlock(BlockNames.BROWN_WOOL, ItemBlockNames.BROWN_WOOL, BROWN_WOOL, BlockWoolBrown.class, V1_19_70);
        registerBlock(BlockNames.GREEN_WOOL, ItemBlockNames.GREEN_WOOL, GREEN_WOOL, BlockWoolGreen.class, V1_19_70);
        registerBlock(BlockNames.RED_WOOL, ItemBlockNames.RED_WOOL, RED_WOOL, BlockWoolRed.class, V1_19_70);
        registerBlock(BlockNames.BLACK_WOOL, ItemBlockNames.BLACK_WOOL, BLACK_WOOL, BlockWoolBlack.class, V1_19_70);

        registerBlock(BlockNames.OAK_LOG, ItemBlockNames.OAK_LOG, OAK_LOG, BlockLogOak.class, V1_19_80);
        registerBlock(BlockNames.SPRUCE_LOG, ItemBlockNames.SPRUCE_LOG, SPRUCE_LOG, BlockLogSpruce.class, V1_19_80);
        registerBlock(BlockNames.BIRCH_LOG, ItemBlockNames.BIRCH_LOG, BIRCH_LOG, BlockLogBirch.class, V1_19_80);
        registerBlock(BlockNames.JUNGLE_LOG, ItemBlockNames.JUNGLE_LOG, JUNGLE_LOG, BlockLogJungle.class, V1_19_80);

        registerBlock(BlockNames.ACACIA_LOG, ItemBlockNames.ACACIA_LOG, ACACIA_LOG, BlockLogAcacia.class, V1_19_80);
        registerBlock(BlockNames.DARK_OAK_LOG, ItemBlockNames.DARK_OAK_LOG, DARK_OAK_LOG, BlockLogDarkOak.class, V1_19_80);

        registerBlock(BlockNames.OAK_FENCE, ItemBlockNames.OAK_FENCE, OAK_FENCE, BlockFenceOak.class, V1_19_80);
        registerBlock(BlockNames.SPRUCE_FENCE, ItemBlockNames.SPRUCE_FENCE, SPRUCE_FENCE, BlockFenceSpruce.class, V1_19_80);
        registerBlock(BlockNames.BIRCH_FENCE, ItemBlockNames.BIRCH_FENCE, BIRCH_FENCE, BlockFenceBirch.class, V1_19_80);
        registerBlock(BlockNames.JUNGLE_FENCE, ItemBlockNames.JUNGLE_FENCE, JUNGLE_FENCE, BlockFenceJungle.class, V1_19_80);
        registerBlock(BlockNames.ACACIA_FENCE, ItemBlockNames.ACACIA_FENCE, ACACIA_FENCE, BlockFenceAcacia.class, V1_19_80);
        registerBlock(BlockNames.DARK_OAK_FENCE, ItemBlockNames.DARK_OAK_FENCE, DARK_OAK_FENCE, BlockFenceDarkOak.class, V1_19_80);

        registerBlock(BlockNames.TUBE_CORAL, ItemBlockNames.TUBE_CORAL, TUBE_CORAL, BlockCoralTube.class, V1_20_0);
        registerBlock(BlockNames.BRAIN_CORAL, ItemBlockNames.BRAIN_CORAL, BRAIN_CORAL, BlockCoralBrain.class, V1_20_0);
        registerBlock(BlockNames.BUBBLE_CORAL, ItemBlockNames.BUBBLE_CORAL, BUBBLE_CORAL, BlockCoralBubble.class, V1_20_0);
        registerBlock(BlockNames.FIRE_CORAL, ItemBlockNames.FIRE_CORAL, FIRE_CORAL, BlockCoralFire.class, V1_20_0);
        registerBlock(BlockNames.HORN_CORAL, ItemBlockNames.HORN_CORAL, HORN_CORAL, BlockCoralHorn.class, V1_20_0);

        registerBlock(BlockNames.DEAD_TUBE_CORAL, ItemBlockNames.DEAD_TUBE_CORAL, DEAD_TUBE_CORAL, BlockCoralTubeDead.class, V1_20_0);
        registerBlock(BlockNames.DEAD_BRAIN_CORAL, ItemBlockNames.DEAD_BRAIN_CORAL, DEAD_BRAIN_CORAL, BlockCoralBrainDead.class, V1_20_0);
        registerBlock(BlockNames.DEAD_BUBBLE_CORAL, ItemBlockNames.DEAD_BUBBLE_CORAL, DEAD_BUBBLE_CORAL, BlockCoralBubbleDead.class, V1_20_0);
        registerBlock(BlockNames.DEAD_FIRE_CORAL, ItemBlockNames.DEAD_FIRE_CORAL, DEAD_FIRE_CORAL, BlockCoralFireDead.class, V1_20_0);
        registerBlock(BlockNames.DEAD_HORN_CORAL, ItemBlockNames.DEAD_HORN_CORAL, DEAD_HORN_CORAL, BlockCoralHornDead.class, V1_20_0);

        registerBlock(BlockNames.WHITE_CARPET, ItemBlockNames.WHITE_CARPET, WHITE_CARPET, BlockCarpetWhite.class, V1_20_0);
        registerBlock(BlockNames.ORANGE_CARPET, ItemBlockNames.ORANGE_CARPET, ORANGE_CARPET, BlockCarpetOrange.class, V1_20_0);
        registerBlock(BlockNames.MAGENTA_CARPET, ItemBlockNames.MAGENTA_CARPET, MAGENTA_CARPET, BlockCarpetMagenta.class, V1_20_0);
        registerBlock(BlockNames.LIGHT_BLUE_CARPET, ItemBlockNames.LIGHT_BLUE_CARPET, LIGHT_BLUE_CARPET, BlockCarpetLightBlue.class, V1_20_0);
        registerBlock(BlockNames.YELLOW_CARPET, ItemBlockNames.YELLOW_CARPET, YELLOW_CARPET, BlockCarpetYellow.class, V1_20_0);
        registerBlock(BlockNames.LIME_CARPET, ItemBlockNames.LIME_CARPET, LIME_CARPET, BlockCarpetLime.class, V1_20_0);
        registerBlock(BlockNames.PINK_CARPET, ItemBlockNames.PINK_CARPET, PINK_CARPET, BlockCarpetPink.class, V1_20_0);
        registerBlock(BlockNames.GRAY_CARPET, ItemBlockNames.GRAY_CARPET, GRAY_CARPET, BlockCarpetGray.class, V1_20_0);
        registerBlock(BlockNames.LIGHT_GRAY_CARPET, ItemBlockNames.LIGHT_GRAY_CARPET, LIGHT_GRAY_CARPET, BlockCarpetLightGray.class, V1_20_0);
        registerBlock(BlockNames.CYAN_CARPET, ItemBlockNames.CYAN_CARPET, CYAN_CARPET, BlockCarpetCyan.class, V1_20_0);
        registerBlock(BlockNames.PURPLE_CARPET, ItemBlockNames.PURPLE_CARPET, PURPLE_CARPET, BlockCarpetPurple.class, V1_20_0);
        registerBlock(BlockNames.BLUE_CARPET, ItemBlockNames.BLUE_CARPET, BLUE_CARPET, BlockCarpetBlue.class, V1_20_0);
        registerBlock(BlockNames.BROWN_CARPET, ItemBlockNames.BROWN_CARPET, BROWN_CARPET, BlockCarpetBrown.class, V1_20_0);
        registerBlock(BlockNames.GREEN_CARPET, ItemBlockNames.GREEN_CARPET, GREEN_CARPET, BlockCarpetGreen.class, V1_20_0);
        registerBlock(BlockNames.RED_CARPET, ItemBlockNames.RED_CARPET, RED_CARPET, BlockCarpetRed.class, V1_20_0);
        registerBlock(BlockNames.BLACK_CARPET, ItemBlockNames.BLACK_CARPET, BLACK_CARPET, BlockCarpetBlack.class, V1_20_0);

        registerBlock(BlockNames.WHITE_SHULKER_BOX, ItemBlockNames.WHITE_SHULKER_BOX, WHITE_SHULKER_BOX, BlockShulkerBoxWhite.class, V1_20_10);
        registerBlock(BlockNames.ORANGE_SHULKER_BOX, ItemBlockNames.ORANGE_SHULKER_BOX, ORANGE_SHULKER_BOX, BlockShulkerBoxOrange.class, V1_20_10);
        registerBlock(BlockNames.MAGENTA_SHULKER_BOX, ItemBlockNames.MAGENTA_SHULKER_BOX, MAGENTA_SHULKER_BOX, BlockShulkerBoxMagenta.class, V1_20_10);
        registerBlock(BlockNames.LIGHT_BLUE_SHULKER_BOX, ItemBlockNames.LIGHT_BLUE_SHULKER_BOX, LIGHT_BLUE_SHULKER_BOX, BlockShulkerBoxLightBlue.class, V1_20_10);
        registerBlock(BlockNames.YELLOW_SHULKER_BOX, ItemBlockNames.YELLOW_SHULKER_BOX, YELLOW_SHULKER_BOX, BlockShulkerBoxYellow.class, V1_20_10);
        registerBlock(BlockNames.LIME_SHULKER_BOX, ItemBlockNames.LIME_SHULKER_BOX, LIME_SHULKER_BOX, BlockShulkerBoxLime.class, V1_20_10);
        registerBlock(BlockNames.PINK_SHULKER_BOX, ItemBlockNames.PINK_SHULKER_BOX, PINK_SHULKER_BOX, BlockShulkerBoxPink.class, V1_20_10);
        registerBlock(BlockNames.GRAY_SHULKER_BOX, ItemBlockNames.GRAY_SHULKER_BOX, GRAY_SHULKER_BOX, BlockShulkerBoxGray.class, V1_20_10);
        registerBlock(BlockNames.LIGHT_GRAY_SHULKER_BOX, ItemBlockNames.LIGHT_GRAY_SHULKER_BOX, LIGHT_GRAY_SHULKER_BOX, BlockShulkerBoxLightGray.class, V1_20_10);
        registerBlock(BlockNames.CYAN_SHULKER_BOX, ItemBlockNames.CYAN_SHULKER_BOX, CYAN_SHULKER_BOX, BlockShulkerBoxCyan.class, V1_20_10);
        registerBlock(BlockNames.PURPLE_SHULKER_BOX, ItemBlockNames.PURPLE_SHULKER_BOX, PURPLE_SHULKER_BOX, BlockShulkerBoxPurple.class, V1_20_10);
        registerBlock(BlockNames.BLUE_SHULKER_BOX, ItemBlockNames.BLUE_SHULKER_BOX, BLUE_SHULKER_BOX, BlockShulkerBoxBlue.class, V1_20_10);
        registerBlock(BlockNames.BROWN_SHULKER_BOX, ItemBlockNames.BROWN_SHULKER_BOX, BROWN_SHULKER_BOX, BlockShulkerBoxBrown.class, V1_20_10);
        registerBlock(BlockNames.GREEN_SHULKER_BOX, ItemBlockNames.GREEN_SHULKER_BOX, GREEN_SHULKER_BOX, BlockShulkerBoxGreen.class, V1_20_10);
        registerBlock(BlockNames.RED_SHULKER_BOX, ItemBlockNames.RED_SHULKER_BOX, RED_SHULKER_BOX, BlockShulkerBoxRed.class, V1_20_10);
        registerBlock(BlockNames.BLACK_SHULKER_BOX, ItemBlockNames.BLACK_SHULKER_BOX, BLACK_SHULKER_BOX, BlockShulkerBoxBlack.class, V1_20_10);

        registerBlock(BlockNames.WHITE_CONCRETE, ItemBlockNames.WHITE_CONCRETE, WHITE_CONCRETE, BlockConcreteWhite.class, V1_20_10);
        registerBlock(BlockNames.ORANGE_CONCRETE, ItemBlockNames.ORANGE_CONCRETE, ORANGE_CONCRETE, BlockConcreteOrange.class, V1_20_10);
        registerBlock(BlockNames.MAGENTA_CONCRETE, ItemBlockNames.MAGENTA_CONCRETE, MAGENTA_CONCRETE, BlockConcreteMagenta.class, V1_20_10);
        registerBlock(BlockNames.LIGHT_BLUE_CONCRETE, ItemBlockNames.LIGHT_BLUE_CONCRETE, LIGHT_BLUE_CONCRETE, BlockConcreteLightBlue.class, V1_20_10);
        registerBlock(BlockNames.YELLOW_CONCRETE, ItemBlockNames.YELLOW_CONCRETE, YELLOW_CONCRETE, BlockConcreteYellow.class, V1_20_10);
        registerBlock(BlockNames.LIME_CONCRETE, ItemBlockNames.LIME_CONCRETE, LIME_CONCRETE, BlockConcreteLime.class, V1_20_10);
        registerBlock(BlockNames.PINK_CONCRETE, ItemBlockNames.PINK_CONCRETE, PINK_CONCRETE, BlockConcretePink.class, V1_20_10);
        registerBlock(BlockNames.GRAY_CONCRETE, ItemBlockNames.GRAY_CONCRETE, GRAY_CONCRETE, BlockConcreteGray.class, V1_20_10);
        registerBlock(BlockNames.LIGHT_GRAY_CONCRETE, ItemBlockNames.LIGHT_GRAY_CONCRETE, LIGHT_GRAY_CONCRETE, BlockConcreteLightGray.class, V1_20_10);
        registerBlock(BlockNames.CYAN_CONCRETE, ItemBlockNames.CYAN_CONCRETE, CYAN_CONCRETE, BlockConcreteCyan.class, V1_20_10);
        registerBlock(BlockNames.PURPLE_CONCRETE, ItemBlockNames.PURPLE_CONCRETE, PURPLE_CONCRETE, BlockConcretePurple.class, V1_20_10);
        registerBlock(BlockNames.BLUE_CONCRETE, ItemBlockNames.BLUE_CONCRETE, BLUE_CONCRETE, BlockConcreteBlue.class, V1_20_10);
        registerBlock(BlockNames.BROWN_CONCRETE, ItemBlockNames.BROWN_CONCRETE, BROWN_CONCRETE, BlockConcreteBrown.class, V1_20_10);
        registerBlock(BlockNames.GREEN_CONCRETE, ItemBlockNames.GREEN_CONCRETE, GREEN_CONCRETE, BlockConcreteGreen.class, V1_20_10);
        registerBlock(BlockNames.RED_CONCRETE, ItemBlockNames.RED_CONCRETE, RED_CONCRETE, BlockConcreteRed.class, V1_20_10);
        registerBlock(BlockNames.BLACK_CONCRETE, ItemBlockNames.BLACK_CONCRETE, BLACK_CONCRETE, BlockConcreteBlack.class, V1_20_10);
/*
        registerBlock(BlockNames.WHITE_STAINED_GLASS, ItemBlockNames.WHITE_STAINED_GLASS, WHITE_STAINED_GLASS, BlockGlassStainedWhite.class, V1_20_30);
        registerBlock(BlockNames.ORANGE_STAINED_GLASS, ItemBlockNames.ORANGE_STAINED_GLASS, ORANGE_STAINED_GLASS, BlockGlassStainedOrange.class, V1_20_30);
        registerBlock(BlockNames.MAGENTA_STAINED_GLASS, ItemBlockNames.MAGENTA_STAINED_GLASS, MAGENTA_STAINED_GLASS, BlockGlassStainedMagenta.class, V1_20_30);
        registerBlock(BlockNames.LIGHT_BLUE_STAINED_GLASS, ItemBlockNames.LIGHT_BLUE_STAINED_GLASS, LIGHT_BLUE_STAINED_GLASS, BlockGlassStainedLightBlue.class, V1_20_30);
        registerBlock(BlockNames.YELLOW_STAINED_GLASS, ItemBlockNames.YELLOW_STAINED_GLASS, YELLOW_STAINED_GLASS, BlockGlassStainedYellow.class, V1_20_30);
        registerBlock(BlockNames.LIME_STAINED_GLASS, ItemBlockNames.LIME_STAINED_GLASS, LIME_STAINED_GLASS, BlockGlassStainedLime.class, V1_20_30);
        registerBlock(BlockNames.PINK_STAINED_GLASS, ItemBlockNames.PINK_STAINED_GLASS, PINK_STAINED_GLASS, BlockGlassStainedPink.class, V1_20_30);
        registerBlock(BlockNames.GRAY_STAINED_GLASS, ItemBlockNames.GRAY_STAINED_GLASS, GRAY_STAINED_GLASS, BlockGlassStainedGray.class, V1_20_30);
        registerBlock(BlockNames.LIGHT_GRAY_STAINED_GLASS, ItemBlockNames.LIGHT_GRAY_STAINED_GLASS, LIGHT_GRAY_STAINED_GLASS, BlockGlassStainedLightGray.class, V1_20_30);
        registerBlock(BlockNames.CYAN_STAINED_GLASS, ItemBlockNames.CYAN_STAINED_GLASS, CYAN_STAINED_GLASS, BlockGlassStainedCyan.class, V1_20_30);
        registerBlock(BlockNames.PURPLE_STAINED_GLASS, ItemBlockNames.PURPLE_STAINED_GLASS, PURPLE_STAINED_GLASS, BlockGlassStainedPurple.class, V1_20_30);
        registerBlock(BlockNames.BLUE_STAINED_GLASS, ItemBlockNames.BLUE_STAINED_GLASS, BLUE_STAINED_GLASS, BlockGlassStainedBlue.class, V1_20_30);
        registerBlock(BlockNames.BROWN_STAINED_GLASS, ItemBlockNames.BROWN_STAINED_GLASS, BROWN_STAINED_GLASS, BlockGlassStainedBrown.class, V1_20_30);
        registerBlock(BlockNames.GREEN_STAINED_GLASS, ItemBlockNames.GREEN_STAINED_GLASS, GREEN_STAINED_GLASS, BlockGlassStainedGreen.class, V1_20_30);
        registerBlock(BlockNames.RED_STAINED_GLASS, ItemBlockNames.RED_STAINED_GLASS, RED_STAINED_GLASS, BlockGlassStainedRed.class, V1_20_30);
        registerBlock(BlockNames.BLACK_STAINED_GLASS, ItemBlockNames.BLACK_STAINED_GLASS, BLACK_STAINED_GLASS, BlockGlassStainedBlack.class, V1_20_30);

        registerBlock(BlockNames.WHITE_STAINED_GLASS_PANE, ItemBlockNames.WHITE_STAINED_GLASS_PANE, WHITE_STAINED_GLASS_PANE, BlockGlassPaneStainedWhite.class, V1_20_30);
        registerBlock(BlockNames.ORANGE_STAINED_GLASS_PANE, ItemBlockNames.ORANGE_STAINED_GLASS_PANE, ORANGE_STAINED_GLASS_PANE, BlockGlassPaneStainedOrange.class, V1_20_30);
        registerBlock(BlockNames.MAGENTA_STAINED_GLASS_PANE, ItemBlockNames.MAGENTA_STAINED_GLASS_PANE, MAGENTA_STAINED_GLASS_PANE, BlockGlassPaneStainedMagenta.class, V1_20_30);
        registerBlock(BlockNames.LIGHT_BLUE_STAINED_GLASS_PANE, ItemBlockNames.LIGHT_BLUE_STAINED_GLASS_PANE, LIGHT_BLUE_STAINED_GLASS_PANE, BlockGlassPaneStainedLightBlue.class, V1_20_30);
        registerBlock(BlockNames.YELLOW_STAINED_GLASS_PANE, ItemBlockNames.YELLOW_STAINED_GLASS_PANE, YELLOW_STAINED_GLASS_PANE, BlockGlassPaneStainedYellow.class, V1_20_30);
        registerBlock(BlockNames.LIME_STAINED_GLASS_PANE, ItemBlockNames.LIME_STAINED_GLASS_PANE, LIME_STAINED_GLASS_PANE, BlockGlassPaneStainedLime.class, V1_20_30);
        registerBlock(BlockNames.PINK_STAINED_GLASS_PANE, ItemBlockNames.PINK_STAINED_GLASS_PANE, PINK_STAINED_GLASS_PANE, BlockGlassPaneStainedPink.class, V1_20_30);
        registerBlock(BlockNames.GRAY_STAINED_GLASS_PANE, ItemBlockNames.GRAY_STAINED_GLASS_PANE, GRAY_STAINED_GLASS_PANE, BlockGlassPaneStainedGray.class, V1_20_30);
        registerBlock(BlockNames.LIGHT_GRAY_STAINED_GLASS_PANE, ItemBlockNames.LIGHT_GRAY_STAINED_GLASS_PANE, LIGHT_GRAY_STAINED_GLASS_PANE, BlockGlassPaneStainedLightGray.class, V1_20_30);
        registerBlock(BlockNames.CYAN_STAINED_GLASS_PANE, ItemBlockNames.CYAN_STAINED_GLASS_PANE, CYAN_STAINED_GLASS_PANE, BlockGlassPaneStainedCyan.class, V1_20_30);
        registerBlock(BlockNames.PURPLE_STAINED_GLASS_PANE, ItemBlockNames.PURPLE_STAINED_GLASS_PANE, PURPLE_STAINED_GLASS_PANE, BlockGlassPaneStainedPurple.class, V1_20_30);
        registerBlock(BlockNames.BLUE_STAINED_GLASS_PANE, ItemBlockNames.BLUE_STAINED_GLASS_PANE, BLUE_STAINED_GLASS_PANE, BlockGlassPaneStainedBlue.class, V1_20_30);
        registerBlock(BlockNames.BROWN_STAINED_GLASS_PANE, ItemBlockNames.BROWN_STAINED_GLASS_PANE, BROWN_STAINED_GLASS_PANE, BlockGlassPaneStainedBrown.class, V1_20_30);
        registerBlock(BlockNames.GREEN_STAINED_GLASS_PANE, ItemBlockNames.GREEN_STAINED_GLASS_PANE, GREEN_STAINED_GLASS_PANE, BlockGlassPaneStainedGreen.class, V1_20_30);
        registerBlock(BlockNames.RED_STAINED_GLASS_PANE, ItemBlockNames.RED_STAINED_GLASS_PANE, RED_STAINED_GLASS_PANE, BlockGlassPaneStainedRed.class, V1_20_30);
        registerBlock(BlockNames.BLACK_STAINED_GLASS_PANE, ItemBlockNames.BLACK_STAINED_GLASS_PANE, BLACK_STAINED_GLASS_PANE, BlockGlassPaneStainedBlack.class, V1_20_30);

        registerBlock(BlockNames.WHITE_CONCRETE_POWDER, ItemBlockNames.WHITE_CONCRETE_POWDER, WHITE_CONCRETE_POWDER, BlockConcretePowderWhite.class, V1_20_30);
        registerBlock(BlockNames.ORANGE_CONCRETE_POWDER, ItemBlockNames.ORANGE_CONCRETE_POWDER, ORANGE_CONCRETE_POWDER, BlockConcretePowderOrange.class, V1_20_30);
        registerBlock(BlockNames.MAGENTA_CONCRETE_POWDER, ItemBlockNames.MAGENTA_CONCRETE_POWDER, MAGENTA_CONCRETE_POWDER, BlockConcretePowderMagenta.class, V1_20_30);
        registerBlock(BlockNames.LIGHT_BLUE_CONCRETE_POWDER, ItemBlockNames.LIGHT_BLUE_CONCRETE_POWDER, LIGHT_BLUE_CONCRETE_POWDER, BlockConcretePowderLightBlue.class, V1_20_30);
        registerBlock(BlockNames.YELLOW_CONCRETE_POWDER, ItemBlockNames.YELLOW_CONCRETE_POWDER, YELLOW_CONCRETE_POWDER, BlockConcretePowderYellow.class, V1_20_30);
        registerBlock(BlockNames.LIME_CONCRETE_POWDER, ItemBlockNames.LIME_CONCRETE_POWDER, LIME_CONCRETE_POWDER, BlockConcretePowderLime.class, V1_20_30);
        registerBlock(BlockNames.PINK_CONCRETE_POWDER, ItemBlockNames.PINK_CONCRETE_POWDER, PINK_CONCRETE_POWDER, BlockConcretePowderPink.class, V1_20_30);
        registerBlock(BlockNames.GRAY_CONCRETE_POWDER, ItemBlockNames.GRAY_CONCRETE_POWDER, GRAY_CONCRETE_POWDER, BlockConcretePowderGray.class, V1_20_30);
        registerBlock(BlockNames.LIGHT_GRAY_CONCRETE_POWDER, ItemBlockNames.LIGHT_GRAY_CONCRETE_POWDER, LIGHT_GRAY_CONCRETE_POWDER, BlockConcretePowderLightGray.class, V1_20_30);
        registerBlock(BlockNames.CYAN_CONCRETE_POWDER, ItemBlockNames.CYAN_CONCRETE_POWDER, CYAN_CONCRETE_POWDER, BlockConcretePowderCyan.class, V1_20_30);
        registerBlock(BlockNames.PURPLE_CONCRETE_POWDER, ItemBlockNames.PURPLE_CONCRETE_POWDER, PURPLE_CONCRETE_POWDER, BlockConcretePowderPurple.class, V1_20_30);
        registerBlock(BlockNames.BLUE_CONCRETE_POWDER, ItemBlockNames.BLUE_CONCRETE_POWDER, BLUE_CONCRETE_POWDER, BlockConcretePowderBlue.class, V1_20_30);
        registerBlock(BlockNames.BROWN_CONCRETE_POWDER, ItemBlockNames.BROWN_CONCRETE_POWDER, BROWN_CONCRETE_POWDER, BlockConcretePowderBrown.class, V1_20_30);
        registerBlock(BlockNames.GREEN_CONCRETE_POWDER, ItemBlockNames.GREEN_CONCRETE_POWDER, GREEN_CONCRETE_POWDER, BlockConcretePowderGreen.class, V1_20_30);
        registerBlock(BlockNames.RED_CONCRETE_POWDER, ItemBlockNames.RED_CONCRETE_POWDER, RED_CONCRETE_POWDER, BlockConcretePowderRed.class, V1_20_30);
        registerBlock(BlockNames.BLACK_CONCRETE_POWDER, ItemBlockNames.BLACK_CONCRETE_POWDER, BLACK_CONCRETE_POWDER, BlockConcretePowderBlack.class, V1_20_30);

        registerBlock(BlockNames.WHITE_TERRACOTTA, ItemBlockNames.WHITE_TERRACOTTA, WHITE_TERRACOTTA, BlockTerracottaWhite.class, V1_20_30);
        registerBlock(BlockNames.ORANGE_TERRACOTTA, ItemBlockNames.ORANGE_TERRACOTTA, ORANGE_TERRACOTTA, BlockTerracottaOrange.class, V1_20_30);
        registerBlock(BlockNames.MAGENTA_TERRACOTTA, ItemBlockNames.MAGENTA_TERRACOTTA, MAGENTA_TERRACOTTA, BlockTerracottaMagenta.class, V1_20_30);
        registerBlock(BlockNames.LIGHT_BLUE_TERRACOTTA, ItemBlockNames.LIGHT_BLUE_TERRACOTTA, LIGHT_BLUE_TERRACOTTA, BlockTerracottaLightBlue.class, V1_20_30);
        registerBlock(BlockNames.YELLOW_TERRACOTTA, ItemBlockNames.YELLOW_TERRACOTTA, YELLOW_TERRACOTTA, BlockTerracottaYellow.class, V1_20_30);
        registerBlock(BlockNames.LIME_TERRACOTTA, ItemBlockNames.LIME_TERRACOTTA, LIME_TERRACOTTA, BlockTerracottaLime.class, V1_20_30);
        registerBlock(BlockNames.PINK_TERRACOTTA, ItemBlockNames.PINK_TERRACOTTA, PINK_TERRACOTTA, BlockTerracottaPink.class, V1_20_30);
        registerBlock(BlockNames.GRAY_TERRACOTTA, ItemBlockNames.GRAY_TERRACOTTA, GRAY_TERRACOTTA, BlockTerracottaGray.class, V1_20_30);
        registerBlock(BlockNames.LIGHT_GRAY_TERRACOTTA, ItemBlockNames.LIGHT_GRAY_TERRACOTTA, LIGHT_GRAY_TERRACOTTA, BlockTerracottaLightGray.class, V1_20_30);
        registerBlock(BlockNames.CYAN_TERRACOTTA, ItemBlockNames.CYAN_TERRACOTTA, CYAN_TERRACOTTA, BlockTerracottaCyan.class, V1_20_30);
        registerBlock(BlockNames.PURPLE_TERRACOTTA, ItemBlockNames.PURPLE_TERRACOTTA, PURPLE_TERRACOTTA, BlockTerracottaPurple.class, V1_20_30);
        registerBlock(BlockNames.BLUE_TERRACOTTA, ItemBlockNames.BLUE_TERRACOTTA, BLUE_TERRACOTTA, BlockTerracottaBlue.class, V1_20_30);
        registerBlock(BlockNames.BROWN_TERRACOTTA, ItemBlockNames.BROWN_TERRACOTTA, BROWN_TERRACOTTA, BlockTerracottaBrown.class, V1_20_30);
        registerBlock(BlockNames.GREEN_TERRACOTTA, ItemBlockNames.GREEN_TERRACOTTA, GREEN_TERRACOTTA, BlockTerracottaGreen.class, V1_20_30);
        registerBlock(BlockNames.RED_TERRACOTTA, ItemBlockNames.RED_TERRACOTTA, RED_TERRACOTTA, BlockTerracottaRed.class, V1_20_30);
        registerBlock(BlockNames.BLACK_TERRACOTTA, ItemBlockNames.BLACK_TERRACOTTA, BLACK_TERRACOTTA, BlockTerracottaBlack.class, V1_20_30);

        registerBlock(BlockNames.GRANITE, ItemBlockNames.GRANITE, GRANITE, BlockGranite.class, V1_20_50);
        registerBlock(BlockNames.POLISHED_GRANITE, ItemBlockNames.POLISHED_GRANITE, POLISHED_GRANITE, BlockGranitePolished.class, V1_20_50);
        registerBlock(BlockNames.DIORITE, ItemBlockNames.DIORITE, DIORITE, BlockDiorite.class, V1_20_50);
        registerBlock(BlockNames.POLISHED_DIORITE, ItemBlockNames.POLISHED_DIORITE, POLISHED_DIORITE, BlockDioritePolished.class, V1_20_50);
        registerBlock(BlockNames.ANDESITE, ItemBlockNames.ANDESITE, ANDESITE, BlockAndesite.class, V1_20_50);
        registerBlock(BlockNames.POLISHED_ANDESITE, ItemBlockNames.POLISHED_ANDESITE, POLISHED_ANDESITE, BlockAndesitePolished.class, V1_20_50);

        registerBlock(BlockNames.OAK_PLANKS, ItemBlockNames.OAK_PLANKS, OAK_PLANKS, BlockPlanksOak.class, V1_20_50);
        registerBlock(BlockNames.SPRUCE_PLANKS, ItemBlockNames.SPRUCE_PLANKS, SPRUCE_PLANKS, BlockPlanksSpruce.class, V1_20_50);
        registerBlock(BlockNames.BIRCH_PLANKS, ItemBlockNames.BIRCH_PLANKS, BIRCH_PLANKS, BlockPlanksBirch.class, V1_20_50);
        registerBlock(BlockNames.JUNGLE_PLANKS, ItemBlockNames.JUNGLE_PLANKS, JUNGLE_PLANKS, BlockPlanksJungle.class, V1_20_50);
        registerBlock(BlockNames.ACACIA_PLANKS, ItemBlockNames.ACACIA_PLANKS, ACACIA_PLANKS, BlockPlanksAcacia.class, V1_20_50);
        registerBlock(BlockNames.DARK_OAK_PLANKS, ItemBlockNames.DARK_OAK_PLANKS, DARK_OAK_PLANKS, BlockPlanksDarkOak.class, V1_20_50);

        registerBlock(BlockNames.HARD_WHITE_STAINED_GLASS, ItemBlockNames.HARD_WHITE_STAINED_GLASS, HARD_WHITE_STAINED_GLASS, BlockGlassStainedHardWhite.class, V1_20_60);
        registerBlock(BlockNames.HARD_ORANGE_STAINED_GLASS, ItemBlockNames.HARD_ORANGE_STAINED_GLASS, HARD_ORANGE_STAINED_GLASS, BlockGlassStainedHardOrange.class, V1_20_60);
        registerBlock(BlockNames.HARD_MAGENTA_STAINED_GLASS, ItemBlockNames.HARD_MAGENTA_STAINED_GLASS, HARD_MAGENTA_STAINED_GLASS, BlockGlassStainedHardMagenta.class, V1_20_60);
        registerBlock(BlockNames.HARD_LIGHT_BLUE_STAINED_GLASS, ItemBlockNames.HARD_LIGHT_BLUE_STAINED_GLASS, HARD_LIGHT_BLUE_STAINED_GLASS, BlockGlassStainedHardLightBlue.class, V1_20_60);
        registerBlock(BlockNames.HARD_YELLOW_STAINED_GLASS, ItemBlockNames.HARD_YELLOW_STAINED_GLASS, HARD_YELLOW_STAINED_GLASS, BlockGlassStainedHardYellow.class, V1_20_60);
        registerBlock(BlockNames.HARD_LIME_STAINED_GLASS, ItemBlockNames.HARD_LIME_STAINED_GLASS, HARD_LIME_STAINED_GLASS, BlockGlassStainedHardLime.class, V1_20_60);
        registerBlock(BlockNames.HARD_PINK_STAINED_GLASS, ItemBlockNames.HARD_PINK_STAINED_GLASS, HARD_PINK_STAINED_GLASS, BlockGlassStainedHardPink.class, V1_20_60);
        registerBlock(BlockNames.HARD_GRAY_STAINED_GLASS, ItemBlockNames.HARD_GRAY_STAINED_GLASS, HARD_GRAY_STAINED_GLASS, BlockGlassStainedHardGray.class, V1_20_60);
        registerBlock(BlockNames.HARD_LIGHT_GRAY_STAINED_GLASS, ItemBlockNames.HARD_LIGHT_GRAY_STAINED_GLASS, HARD_LIGHT_GRAY_STAINED_GLASS, BlockGlassStainedHardLightGray.class, V1_20_60);
        registerBlock(BlockNames.HARD_CYAN_STAINED_GLASS, ItemBlockNames.HARD_CYAN_STAINED_GLASS, HARD_CYAN_STAINED_GLASS, BlockGlassStainedHardCyan.class, V1_20_60);
        registerBlock(BlockNames.HARD_PURPLE_STAINED_GLASS, ItemBlockNames.HARD_PURPLE_STAINED_GLASS, HARD_PURPLE_STAINED_GLASS, BlockGlassStainedHardPurple.class, V1_20_60);
        registerBlock(BlockNames.HARD_BLUE_STAINED_GLASS, ItemBlockNames.HARD_BLUE_STAINED_GLASS, HARD_BLUE_STAINED_GLASS, BlockGlassStainedHardBlue.class, V1_20_60);
        registerBlock(BlockNames.HARD_BROWN_STAINED_GLASS, ItemBlockNames.HARD_BROWN_STAINED_GLASS, HARD_BROWN_STAINED_GLASS, BlockGlassStainedHardBrown.class, V1_20_60);
        registerBlock(BlockNames.HARD_GREEN_STAINED_GLASS, ItemBlockNames.HARD_GREEN_STAINED_GLASS, HARD_GREEN_STAINED_GLASS, BlockGlassStainedHardGreen.class, V1_20_60);
        registerBlock(BlockNames.HARD_RED_STAINED_GLASS, ItemBlockNames.HARD_RED_STAINED_GLASS, HARD_RED_STAINED_GLASS, BlockGlassStainedHardRed.class, V1_20_60);
        registerBlock(BlockNames.HARD_BLACK_STAINED_GLASS, ItemBlockNames.HARD_BLACK_STAINED_GLASS, HARD_BLACK_STAINED_GLASS, BlockGlassStainedHardBlack.class, V1_20_60);

        registerBlock(BlockNames.HARD_WHITE_STAINED_GLASS_PANE, ItemBlockNames.HARD_WHITE_STAINED_GLASS_PANE, HARD_WHITE_STAINED_GLASS_PANE, BlockGlassPaneStainedHardWhite.class, V1_20_60);
        registerBlock(BlockNames.HARD_ORANGE_STAINED_GLASS_PANE, ItemBlockNames.HARD_ORANGE_STAINED_GLASS_PANE, HARD_ORANGE_STAINED_GLASS_PANE, BlockGlassPaneStainedHardOrange.class, V1_20_60);
        registerBlock(BlockNames.HARD_MAGENTA_STAINED_GLASS_PANE, ItemBlockNames.HARD_MAGENTA_STAINED_GLASS_PANE, HARD_MAGENTA_STAINED_GLASS_PANE, BlockGlassPaneStainedHardMagenta.class, V1_20_60);
        registerBlock(BlockNames.HARD_LIGHT_BLUE_STAINED_GLASS_PANE, ItemBlockNames.HARD_LIGHT_BLUE_STAINED_GLASS_PANE, HARD_LIGHT_BLUE_STAINED_GLASS_PANE, BlockGlassPaneStainedHardLightBlue.class, V1_20_60);
        registerBlock(BlockNames.HARD_YELLOW_STAINED_GLASS_PANE, ItemBlockNames.HARD_YELLOW_STAINED_GLASS_PANE, HARD_YELLOW_STAINED_GLASS_PANE, BlockGlassPaneStainedHardYellow.class, V1_20_60);
        registerBlock(BlockNames.HARD_LIME_STAINED_GLASS_PANE, ItemBlockNames.HARD_LIME_STAINED_GLASS_PANE, HARD_LIME_STAINED_GLASS_PANE, BlockGlassPaneStainedHardLime.class, V1_20_60);
        registerBlock(BlockNames.HARD_PINK_STAINED_GLASS_PANE, ItemBlockNames.HARD_PINK_STAINED_GLASS_PANE, HARD_PINK_STAINED_GLASS_PANE, BlockGlassPaneStainedHardPink.class, V1_20_60);
        registerBlock(BlockNames.HARD_GRAY_STAINED_GLASS_PANE, ItemBlockNames.HARD_GRAY_STAINED_GLASS_PANE, HARD_GRAY_STAINED_GLASS_PANE, BlockGlassPaneStainedHardGray.class, V1_20_60);
        registerBlock(BlockNames.HARD_LIGHT_GRAY_STAINED_GLASS_PANE, ItemBlockNames.HARD_LIGHT_GRAY_STAINED_GLASS_PANE, HARD_LIGHT_GRAY_STAINED_GLASS_PANE, BlockGlassPaneStainedHardLightGray.class, V1_20_60);
        registerBlock(BlockNames.HARD_CYAN_STAINED_GLASS_PANE, ItemBlockNames.HARD_CYAN_STAINED_GLASS_PANE, HARD_CYAN_STAINED_GLASS_PANE, BlockGlassPaneStainedHardCyan.class, V1_20_60);
        registerBlock(BlockNames.HARD_PURPLE_STAINED_GLASS_PANE, ItemBlockNames.HARD_PURPLE_STAINED_GLASS_PANE, HARD_PURPLE_STAINED_GLASS_PANE, BlockGlassPaneStainedHardPurple.class, V1_20_60);
        registerBlock(BlockNames.HARD_BLUE_STAINED_GLASS_PANE, ItemBlockNames.HARD_BLUE_STAINED_GLASS_PANE, HARD_BLUE_STAINED_GLASS_PANE, BlockGlassPaneStainedHardBlue.class, V1_20_60);
        registerBlock(BlockNames.HARD_BROWN_STAINED_GLASS_PANE, ItemBlockNames.HARD_BROWN_STAINED_GLASS_PANE, HARD_BROWN_STAINED_GLASS_PANE, BlockGlassPaneStainedHardBrown.class, V1_20_60);
        registerBlock(BlockNames.HARD_GREEN_STAINED_GLASS_PANE, ItemBlockNames.HARD_GREEN_STAINED_GLASS_PANE, HARD_GREEN_STAINED_GLASS_PANE, BlockGlassPaneStainedHardGreen.class, V1_20_60);
        registerBlock(BlockNames.HARD_RED_STAINED_GLASS_PANE, ItemBlockNames.HARD_RED_STAINED_GLASS_PANE, HARD_RED_STAINED_GLASS_PANE, BlockGlassPaneStainedHardRed.class, V1_20_60);
        registerBlock(BlockNames.HARD_BLACK_STAINED_GLASS_PANE, ItemBlockNames.HARD_BLACK_STAINED_GLASS_PANE, HARD_BLACK_STAINED_GLASS_PANE, BlockGlassPaneStainedHardBlack.class, V1_20_60);

        registerBlock(BlockNames.OAK_LEAVES, ItemBlockNames.OAK_LEAVES, OAK_LEAVES, BlockLeavesOak.class, V1_20_70);
        registerBlock(BlockNames.SPRUCE_LEAVES, ItemBlockNames.SPRUCE_LEAVES, SPRUCE_LEAVES, BlockLeavesSpruce.class, V1_20_70);
        registerBlock(BlockNames.BIRCH_LEAVES, ItemBlockNames.BIRCH_LEAVES, BIRCH_LEAVES, BlockLeavesBirch.class, V1_20_70);
        registerBlock(BlockNames.JUNGLE_LEAVES, ItemBlockNames.JUNGLE_LEAVES, JUNGLE_LEAVES, BlockLeavesJungle.class, V1_20_70);

        registerBlock(BlockNames.ACACIA_LEAVES, ItemBlockNames.ACACIA_LEAVES, ACACIA_LEAVES, BlockLeavesAcacia.class, V1_20_70);
        registerBlock(BlockNames.DARK_OAK_LEAVES, ItemBlockNames.DARK_OAK_LEAVES, DARK_OAK_LEAVES, BlockLeavesDarkOak.class, V1_20_70);

        registerBlock(BlockNames.OAK_SLAB, ItemBlockNames.OAK_SLAB, OAK_SLAB, BlockSlabOak.class, V1_20_70);
        registerBlock(BlockNames.SPRUCE_SLAB, ItemBlockNames.SPRUCE_SLAB, SPRUCE_SLAB, BlockSlabSpruce.class, V1_20_70);
        registerBlock(BlockNames.BIRCH_SLAB, ItemBlockNames.BIRCH_SLAB, BIRCH_SLAB, BlockSlabBirch.class, V1_20_70);
        registerBlock(BlockNames.JUNGLE_SLAB, ItemBlockNames.JUNGLE_SLAB, JUNGLE_SLAB, BlockSlabJungle.class, V1_20_70);
        registerBlock(BlockNames.ACACIA_SLAB, ItemBlockNames.ACACIA_SLAB, ACACIA_SLAB, BlockSlabAcacia.class, V1_20_70);
        registerBlock(BlockNames.DARK_OAK_SLAB, ItemBlockNames.DARK_OAK_SLAB, DARK_OAK_SLAB, BlockSlabDarkOak.class, V1_20_70);

        registerBlock(BlockNames.OAK_DOUBLE_SLAB, ItemBlockNames.OAK_DOUBLE_SLAB, OAK_DOUBLE_SLAB, BlockDoubleSlabOak.class, V1_20_70);
        registerBlock(BlockNames.SPRUCE_DOUBLE_SLAB, ItemBlockNames.SPRUCE_DOUBLE_SLAB, SPRUCE_DOUBLE_SLAB, BlockDoubleSlabSpruce.class, V1_20_70);
        registerBlock(BlockNames.BIRCH_DOUBLE_SLAB, ItemBlockNames.BIRCH_DOUBLE_SLAB, BIRCH_DOUBLE_SLAB, BlockDoubleSlabBirch.class, V1_20_70);
        registerBlock(BlockNames.JUNGLE_DOUBLE_SLAB, ItemBlockNames.JUNGLE_DOUBLE_SLAB, JUNGLE_DOUBLE_SLAB, BlockDoubleSlabJungle.class, V1_20_70);
        registerBlock(BlockNames.ACACIA_DOUBLE_SLAB, ItemBlockNames.ACACIA_DOUBLE_SLAB, ACACIA_DOUBLE_SLAB, BlockDoubleSlabAcacia.class, V1_20_70);
        registerBlock(BlockNames.DARK_OAK_DOUBLE_SLAB, ItemBlockNames.DARK_OAK_DOUBLE_SLAB, DARK_OAK_DOUBLE_SLAB, BlockDoubleSlabDarkOak.class, V1_20_70);

        registerBlock(BlockNames.OAK_WOOD, ItemBlockNames.OAK_WOOD, OAK_WOOD, BlockWoodOak.class, V1_20_70);
        registerBlock(BlockNames.SPRUCE_WOOD, ItemBlockNames.SPRUCE_WOOD, SPRUCE_WOOD, BlockWoodSpruce.class, V1_20_70);
        registerBlock(BlockNames.BIRCH_WOOD, ItemBlockNames.BIRCH_WOOD, BIRCH_WOOD, BlockWoodBirch.class, V1_20_70);
        registerBlock(BlockNames.JUNGLE_WOOD, ItemBlockNames.JUNGLE_WOOD, JUNGLE_WOOD, BlockWoodJungle.class, V1_20_70);
        registerBlock(BlockNames.ACACIA_WOOD, ItemBlockNames.ACACIA_WOOD, ACACIA_WOOD, BlockWoodAcacia.class, V1_20_70);
        registerBlock(BlockNames.DARK_OAK_WOOD, ItemBlockNames.DARK_OAK_WOOD, DARK_OAK_WOOD, BlockWoodDarkOak.class, V1_20_70);

        registerBlock(BlockNames.STRIPPED_OAK_WOOD, ItemBlockNames.STRIPPED_OAK_WOOD, STRIPPED_OAK_WOOD, BlockWoodStrippedOak.class, V1_20_70);
        registerBlock(BlockNames.STRIPPED_SPRUCE_WOOD, ItemBlockNames.STRIPPED_SPRUCE_WOOD, STRIPPED_SPRUCE_WOOD, BlockWoodStrippedSpruce.class, V1_20_70);
        registerBlock(BlockNames.STRIPPED_BIRCH_WOOD, ItemBlockNames.STRIPPED_BIRCH_WOOD, STRIPPED_BIRCH_WOOD, BlockWoodStrippedBirch.class, V1_20_70);
        registerBlock(BlockNames.STRIPPED_JUNGLE_WOOD, ItemBlockNames.STRIPPED_JUNGLE_WOOD, STRIPPED_JUNGLE_WOOD, BlockWoodStrippedJungle.class, V1_20_70);
        registerBlock(BlockNames.STRIPPED_ACACIA_WOOD, ItemBlockNames.STRIPPED_ACACIA_WOOD, STRIPPED_ACACIA_WOOD, BlockWoodStrippedAcacia.class, V1_20_70);
        registerBlock(BlockNames.STRIPPED_DARK_OAK_WOOD, ItemBlockNames.STRIPPED_DARK_OAK_WOOD, STRIPPED_DARK_OAK_WOOD, BlockWoodStrippedDarkOak.class, V1_20_70);

        registerBlock(BlockNames.OAK_SAPLING, ItemBlockNames.OAK_SAPLING, OAK_SAPLING, BlockSaplingOak.class, V1_20_80);
        registerBlock(BlockNames.SPRUCE_SAPLING, ItemBlockNames.SPRUCE_SAPLING, SPRUCE_SAPLING, BlockSaplingSpruce.class, V1_20_80);
        registerBlock(BlockNames.BIRCH_SAPLING, ItemBlockNames.BIRCH_SAPLING, BIRCH_SAPLING, BlockSaplingBirch.class, V1_20_80);
        registerBlock(BlockNames.JUNGLE_SAPLING, ItemBlockNames.JUNGLE_SAPLING, JUNGLE_SAPLING, BlockSaplingJungle.class, V1_20_80);
        registerBlock(BlockNames.ACACIA_SAPLING, ItemBlockNames.ACACIA_SAPLING, ACACIA_SAPLING, BlockSaplingAcacia.class, V1_20_80);
        registerBlock(BlockNames.DARK_OAK_SAPLING, ItemBlockNames.DARK_OAK_SAPLING, DARK_OAK_SAPLING, BlockSaplingDarkOak.class, V1_20_80);

        registerBlock(BlockNames.TUBE_CORAL_FAN, ItemBlockNames.TUBE_CORAL_FAN, TUBE_CORAL_FAN, BlockCoralFanTube.class, V1_20_80);
        registerBlock(BlockNames.BRAIN_CORAL_FAN, ItemBlockNames.BRAIN_CORAL_FAN, BRAIN_CORAL_FAN, BlockCoralFanBrain.class, V1_20_80);
        registerBlock(BlockNames.BUBBLE_CORAL_FAN, ItemBlockNames.BUBBLE_CORAL_FAN, BUBBLE_CORAL_FAN, BlockCoralFanBubble.class, V1_20_80);
        registerBlock(BlockNames.FIRE_CORAL_FAN, ItemBlockNames.FIRE_CORAL_FAN, FIRE_CORAL_FAN, BlockCoralFanFire.class, V1_20_80);
        registerBlock(BlockNames.HORN_CORAL_FAN, ItemBlockNames.HORN_CORAL_FAN, HORN_CORAL_FAN, BlockCoralFanHorn.class, V1_20_80);

        registerBlock(BlockNames.DEAD_TUBE_CORAL_FAN, ItemBlockNames.DEAD_TUBE_CORAL_FAN, DEAD_TUBE_CORAL_FAN, BlockCoralFanDeadTube.class, V1_20_80);
        registerBlock(BlockNames.DEAD_BRAIN_CORAL_FAN, ItemBlockNames.DEAD_BRAIN_CORAL_FAN, DEAD_BRAIN_CORAL_FAN, BlockCoralFanDeadBrain.class, V1_20_80);
        registerBlock(BlockNames.DEAD_BUBBLE_CORAL_FAN, ItemBlockNames.DEAD_BUBBLE_CORAL_FAN, DEAD_BUBBLE_CORAL_FAN, BlockCoralFanDeadBubble.class, V1_20_80);
        registerBlock(BlockNames.DEAD_FIRE_CORAL_FAN, ItemBlockNames.DEAD_FIRE_CORAL_FAN, DEAD_FIRE_CORAL_FAN, BlockCoralFanDeadFire.class, V1_20_80);
        registerBlock(BlockNames.DEAD_HORN_CORAL_FAN, ItemBlockNames.DEAD_HORN_CORAL_FAN, DEAD_HORN_CORAL_FAN, BlockCoralFanDeadHorn.class, V1_20_80);

        registerBlock(BlockNames.POPPY, ItemBlockNames.POPPY, POPPY, BlockPoppy.class, V1_20_80);
        registerBlock(BlockNames.BLUE_ORCHID, ItemBlockNames.BLUE_ORCHID, BLUE_ORCHID, BlockBlueOrchid.class, V1_20_80);
        registerBlock(BlockNames.ALLIUM, ItemBlockNames.ALLIUM, ALLIUM, BlockAllium.class, V1_20_80);
        registerBlock(BlockNames.AZURE_BLUET, ItemBlockNames.AZURE_BLUET, AZURE_BLUET, BlockAzureBluet.class, V1_20_80);
        registerBlock(BlockNames.RED_TULIP, ItemBlockNames.RED_TULIP, RED_TULIP, BlockTulipRed.class, V1_20_80);
        registerBlock(BlockNames.ORANGE_TULIP, ItemBlockNames.ORANGE_TULIP, ORANGE_TULIP, BlockTulipOrange.class, V1_20_80);
        registerBlock(BlockNames.WHITE_TULIP, ItemBlockNames.WHITE_TULIP, WHITE_TULIP, BlockTulipWhite.class, V1_20_80);
        registerBlock(BlockNames.PINK_TULIP, ItemBlockNames.PINK_TULIP, PINK_TULIP, BlockTulipPink.class, V1_20_80);
        registerBlock(BlockNames.OXEYE_DAISY, ItemBlockNames.OXEYE_DAISY, OXEYE_DAISY, BlockOxeyeDaisy.class, V1_20_80);
        registerBlock(BlockNames.CORNFLOWER, ItemBlockNames.CORNFLOWER, CORNFLOWER, BlockCornflower.class, V1_20_80);
        registerBlock(BlockNames.LILY_OF_THE_VALLEY, ItemBlockNames.LILY_OF_THE_VALLEY, LILY_OF_THE_VALLEY, BlockLilyOfTheValley.class, V1_20_80);

        registerBlock(BlockNames.SUNFLOWER, ItemBlockNames.SUNFLOWER, SUNFLOWER, BlockSunflower.class, V1_21_0);
        registerBlock(BlockNames.LILAC, ItemBlockNames.LILAC, LILAC, BlockLilac.class, V1_21_0);
        registerBlock(BlockNames.TALL_GRASS, ItemBlockNames.TALL_GRASS, TALL_GRASS, BlockDoubleGrass.class, V1_21_0);
        registerBlock(BlockNames.LARGE_FERN, ItemBlockNames.LARGE_FERN, LARGE_FERN, BlockFernLarge.class, V1_21_0);
        registerBlock(BlockNames.ROSE_BUSH, ItemBlockNames.ROSE_BUSH, ROSE_BUSH, BlockRoseBush.class, V1_21_0);
        registerBlock(BlockNames.PEONY, ItemBlockNames.PEONY, PEONY, BlockPeony.class, V1_21_0);

        registerBlock(BlockNames.FERN, ItemBlockNames.FERN, FERN, BlockFern.class, V1_21_0);

        registerBlock(BlockNames.TUBE_CORAL_BLOCK, ItemBlockNames.TUBE_CORAL_BLOCK, TUBE_CORAL_BLOCK, BlockCoralBlockTube.class, V1_21_0);
        registerBlock(BlockNames.BRAIN_CORAL_BLOCK, ItemBlockNames.BRAIN_CORAL_BLOCK, BRAIN_CORAL_BLOCK, BlockCoralBlockBrain.class, V1_21_0);
        registerBlock(BlockNames.BUBBLE_CORAL_BLOCK, ItemBlockNames.BUBBLE_CORAL_BLOCK, BUBBLE_CORAL_BLOCK, BlockCoralBlockBubble.class, V1_21_0);
        registerBlock(BlockNames.FIRE_CORAL_BLOCK, ItemBlockNames.FIRE_CORAL_BLOCK, FIRE_CORAL_BLOCK, BlockCoralBlockFire.class, V1_21_0);
        registerBlock(BlockNames.HORN_CORAL_BLOCK, ItemBlockNames.HORN_CORAL_BLOCK, HORN_CORAL_BLOCK, BlockCoralBlockHorn.class, V1_21_0);

        registerBlock(BlockNames.DEAD_TUBE_CORAL_BLOCK, ItemBlockNames.DEAD_TUBE_CORAL_BLOCK, DEAD_TUBE_CORAL_BLOCK, BlockCoralBlockDeadTube.class, V1_21_0);
        registerBlock(BlockNames.DEAD_BRAIN_CORAL_BLOCK, ItemBlockNames.DEAD_BRAIN_CORAL_BLOCK, DEAD_BRAIN_CORAL_BLOCK, BlockCoralBlockDeadBrain.class, V1_21_0);
        registerBlock(BlockNames.DEAD_BUBBLE_CORAL_BLOCK, ItemBlockNames.DEAD_BUBBLE_CORAL_BLOCK, DEAD_BUBBLE_CORAL_BLOCK, BlockCoralBlockDeadBubble.class, V1_21_0);
        registerBlock(BlockNames.DEAD_FIRE_CORAL_BLOCK, ItemBlockNames.DEAD_FIRE_CORAL_BLOCK, DEAD_FIRE_CORAL_BLOCK, BlockCoralBlockDeadFire.class, V1_21_0);
        registerBlock(BlockNames.DEAD_HORN_CORAL_BLOCK, ItemBlockNames.DEAD_HORN_CORAL_BLOCK, DEAD_HORN_CORAL_BLOCK, BlockCoralBlockDeadHorn.class, V1_21_0);

        registerBlock(BlockNames.SMOOTH_STONE_SLAB, ItemBlockNames.SMOOTH_STONE_SLAB, SMOOTH_STONE_SLAB, BlockSlabSmoothStone.class, V1_21_0);
        registerBlock(BlockNames.SANDSTONE_SLAB, ItemBlockNames.SANDSTONE_SLAB, SANDSTONE_SLAB, BlockSlabSandstone.class, V1_21_0);
        registerBlock(BlockNames.PETRIFIED_OAK_SLAB, ItemBlockNames.PETRIFIED_OAK_SLAB, PETRIFIED_OAK_SLAB, BlockSlabPetrifiedOak.class, V1_21_0);
        registerBlock(BlockNames.COBBLESTONE_SLAB, ItemBlockNames.COBBLESTONE_SLAB, COBBLESTONE_SLAB, BlockSlabCobblestone.class, V1_21_0);
        registerBlock(BlockNames.BRICK_SLAB, ItemBlockNames.BRICK_SLAB, BRICK_SLAB, BlockSlabBrick.class, V1_21_0);
        registerBlock(BlockNames.STONE_BRICK_SLAB, ItemBlockNames.STONE_BRICK_SLAB, STONE_BRICK_SLAB, BlockSlabStoneBrick.class, V1_21_0);
        registerBlock(BlockNames.QUARTZ_SLAB, ItemBlockNames.QUARTZ_SLAB, QUARTZ_SLAB, BlockSlabQuartz.class, V1_21_0);
        registerBlock(BlockNames.NETHER_BRICK_SLAB, ItemBlockNames.NETHER_BRICK_SLAB, NETHER_BRICK_SLAB, BlockSlabNetherBrick.class, V1_21_0);

        registerBlock(BlockNames.SMOOTH_STONE_DOUBLE_SLAB, ItemBlockNames.SMOOTH_STONE_DOUBLE_SLAB, SMOOTH_STONE_DOUBLE_SLAB, BlockDoubleSlabSmoothStone.class, V1_21_20);
        registerBlock(BlockNames.SANDSTONE_DOUBLE_SLAB, ItemBlockNames.SANDSTONE_DOUBLE_SLAB, SANDSTONE_DOUBLE_SLAB, BlockDoubleSlabSandstone.class, V1_21_20);
        registerBlock(BlockNames.PETRIFIED_OAK_DOUBLE_SLAB, ItemBlockNames.PETRIFIED_OAK_DOUBLE_SLAB, PETRIFIED_OAK_DOUBLE_SLAB, BlockDoubleSlabPetrifiedOak.class, V1_21_20);
        registerBlock(BlockNames.COBBLESTONE_DOUBLE_SLAB, ItemBlockNames.COBBLESTONE_DOUBLE_SLAB, COBBLESTONE_DOUBLE_SLAB, BlockDoubleSlabCobblestone.class, V1_21_20);
        registerBlock(BlockNames.BRICK_DOUBLE_SLAB, ItemBlockNames.BRICK_DOUBLE_SLAB, BRICK_DOUBLE_SLAB, BlockDoubleSlabBrick.class, V1_21_20);
        registerBlock(BlockNames.STONE_BRICK_DOUBLE_SLAB, ItemBlockNames.STONE_BRICK_DOUBLE_SLAB, STONE_BRICK_DOUBLE_SLAB, BlockDoubleSlabStoneBrick.class, V1_21_20);
        registerBlock(BlockNames.QUARTZ_DOUBLE_SLAB, ItemBlockNames.QUARTZ_DOUBLE_SLAB, QUARTZ_DOUBLE_SLAB, BlockDoubleSlabQuartz.class, V1_21_20);
        registerBlock(BlockNames.NETHER_BRICK_DOUBLE_SLAB, ItemBlockNames.NETHER_BRICK_DOUBLE_SLAB, NETHER_BRICK_DOUBLE_SLAB, BlockDoubleSlabNetherBrick.class, V1_21_20);

        registerBlock(BlockNames.RED_SANDSTONE_SLAB, ItemBlockNames.RED_SANDSTONE_SLAB, RED_SANDSTONE_SLAB, BlockSlabRedSandstone.class, V1_21_20);
        registerBlock(BlockNames.PURPUR_SLAB, ItemBlockNames.PURPUR_SLAB, PURPUR_SLAB, BlockSlabPurpur.class, V1_21_20);
        registerBlock(BlockNames.PRISMARINE_SLAB, ItemBlockNames.PRISMARINE_SLAB, PRISMARINE_SLAB, BlockSlabPrismarine.class, V1_21_20);
        registerBlock(BlockNames.DARK_PRISMARINE_SLAB, ItemBlockNames.DARK_PRISMARINE_SLAB, DARK_PRISMARINE_SLAB, BlockSlabDarkPrismarine.class, V1_21_20);
        registerBlock(BlockNames.PRISMARINE_BRICK_SLAB, ItemBlockNames.PRISMARINE_BRICK_SLAB, PRISMARINE_BRICK_SLAB, BlockSlabPrismarineBrick.class, V1_21_20);
        registerBlock(BlockNames.MOSSY_COBBLESTONE_SLAB, ItemBlockNames.MOSSY_COBBLESTONE_SLAB, MOSSY_COBBLESTONE_SLAB, BlockSlabMossyCobblestone.class, V1_21_20);
        registerBlock(BlockNames.SMOOTH_SANDSTONE_SLAB, ItemBlockNames.SMOOTH_SANDSTONE_SLAB, SMOOTH_SANDSTONE_SLAB, BlockSlabSmoothSandstone.class, V1_21_20);
        registerBlock(BlockNames.RED_NETHER_BRICK_SLAB, ItemBlockNames.RED_NETHER_BRICK_SLAB, RED_NETHER_BRICK_SLAB, BlockSlabRedNetherBrick.class, V1_21_20);

        registerBlock(BlockNames.RED_SANDSTONE_DOUBLE_SLAB, ItemBlockNames.RED_SANDSTONE_DOUBLE_SLAB, RED_SANDSTONE_DOUBLE_SLAB, BlockDoubleSlabRedSandstone.class, V1_21_20);
        registerBlock(BlockNames.PURPUR_DOUBLE_SLAB, ItemBlockNames.PURPUR_DOUBLE_SLAB, PURPUR_DOUBLE_SLAB, BlockDoubleSlabPurpur.class, V1_21_20);
        registerBlock(BlockNames.PRISMARINE_DOUBLE_SLAB, ItemBlockNames.PRISMARINE_DOUBLE_SLAB, PRISMARINE_DOUBLE_SLAB, BlockDoubleSlabPrismarine.class, V1_21_20);
        registerBlock(BlockNames.DARK_PRISMARINE_DOUBLE_SLAB, ItemBlockNames.DARK_PRISMARINE_DOUBLE_SLAB, DARK_PRISMARINE_DOUBLE_SLAB, BlockDoubleSlabDarkPrismarine.class, V1_21_20);
        registerBlock(BlockNames.PRISMARINE_BRICK_DOUBLE_SLAB, ItemBlockNames.PRISMARINE_BRICK_DOUBLE_SLAB, PRISMARINE_BRICK_DOUBLE_SLAB, BlockDoubleSlabPrismarineBrick.class, V1_21_20);
        registerBlock(BlockNames.MOSSY_COBBLESTONE_DOUBLE_SLAB, ItemBlockNames.MOSSY_COBBLESTONE_DOUBLE_SLAB, MOSSY_COBBLESTONE_DOUBLE_SLAB, BlockDoubleSlabMossyCobblestone.class, V1_21_20);
        registerBlock(BlockNames.SMOOTH_SANDSTONE_DOUBLE_SLAB, ItemBlockNames.SMOOTH_SANDSTONE_DOUBLE_SLAB, SMOOTH_SANDSTONE_DOUBLE_SLAB, BlockDoubleSlabSmoothSandstone.class, V1_21_20);
        registerBlock(BlockNames.RED_NETHER_BRICK_DOUBLE_SLAB, ItemBlockNames.RED_NETHER_BRICK_DOUBLE_SLAB, RED_NETHER_BRICK_DOUBLE_SLAB, BlockDoubleSlabRedNetherBrick.class, V1_21_20);

        registerBlock(BlockNames.END_STONE_BRICK_SLAB, ItemBlockNames.END_STONE_BRICK_SLAB, END_STONE_BRICK_SLAB, BlockSlabEndBrick.class, V1_21_20);
        registerBlock(BlockNames.SMOOTH_RED_SANDSTONE_SLAB, ItemBlockNames.SMOOTH_RED_SANDSTONE_SLAB, SMOOTH_RED_SANDSTONE_SLAB, BlockSlabSmoothRedSandstone.class, V1_21_20);
        registerBlock(BlockNames.POLISHED_ANDESITE_SLAB, ItemBlockNames.POLISHED_ANDESITE_SLAB, POLISHED_ANDESITE_SLAB, BlockSlabPolishedAndesite.class, V1_21_20);
        registerBlock(BlockNames.ANDESITE_SLAB, ItemBlockNames.ANDESITE_SLAB, ANDESITE_SLAB, BlockSlabAndesite.class, V1_21_20);
        registerBlock(BlockNames.DIORITE_SLAB, ItemBlockNames.DIORITE_SLAB, DIORITE_SLAB, BlockSlabDiorite.class, V1_21_20);
        registerBlock(BlockNames.POLISHED_DIORITE_SLAB, ItemBlockNames.POLISHED_DIORITE_SLAB, POLISHED_DIORITE_SLAB, BlockSlabPolishedDiorite.class, V1_21_20);
        registerBlock(BlockNames.GRANITE_SLAB, ItemBlockNames.GRANITE_SLAB, GRANITE_SLAB, BlockSlabGranite.class, V1_21_20);
        registerBlock(BlockNames.POLISHED_GRANITE_SLAB, ItemBlockNames.POLISHED_GRANITE_SLAB, POLISHED_GRANITE_SLAB, BlockSlabPolishedGranite.class, V1_21_20);

        registerBlock(BlockNames.END_STONE_BRICK_DOUBLE_SLAB, ItemBlockNames.END_STONE_BRICK_DOUBLE_SLAB, END_STONE_BRICK_DOUBLE_SLAB, BlockDoubleSlabEndBrick.class, V1_21_20);
        registerBlock(BlockNames.SMOOTH_RED_SANDSTONE_DOUBLE_SLAB, ItemBlockNames.SMOOTH_RED_SANDSTONE_DOUBLE_SLAB, SMOOTH_RED_SANDSTONE_DOUBLE_SLAB, BlockDoubleSlabSmoothRedSandstone.class, V1_21_20);
        registerBlock(BlockNames.POLISHED_ANDESITE_DOUBLE_SLAB, ItemBlockNames.POLISHED_ANDESITE_DOUBLE_SLAB, POLISHED_ANDESITE_DOUBLE_SLAB, BlockDoubleSlabPolishedAndesite.class, V1_21_20);
        registerBlock(BlockNames.ANDESITE_DOUBLE_SLAB, ItemBlockNames.ANDESITE_DOUBLE_SLAB, ANDESITE_DOUBLE_SLAB, BlockDoubleSlabAndesite.class, V1_21_20);
        registerBlock(BlockNames.DIORITE_DOUBLE_SLAB, ItemBlockNames.DIORITE_DOUBLE_SLAB, DIORITE_DOUBLE_SLAB, BlockDoubleSlabDiorite.class, V1_21_20);
        registerBlock(BlockNames.POLISHED_DIORITE_DOUBLE_SLAB, ItemBlockNames.POLISHED_DIORITE_DOUBLE_SLAB, POLISHED_DIORITE_DOUBLE_SLAB, BlockDoubleSlabPolishedDiorite.class, V1_21_20);
        registerBlock(BlockNames.GRANITE_DOUBLE_SLAB, ItemBlockNames.GRANITE_DOUBLE_SLAB, GRANITE_DOUBLE_SLAB, BlockDoubleSlabGranite.class, V1_21_20);
        registerBlock(BlockNames.POLISHED_GRANITE_DOUBLE_SLAB, ItemBlockNames.POLISHED_GRANITE_DOUBLE_SLAB, POLISHED_GRANITE_DOUBLE_SLAB, BlockDoubleSlabPolishedGranite.class, V1_21_20);

        registerBlock(BlockNames.MOSSY_STONE_BRICK_SLAB, ItemBlockNames.MOSSY_STONE_BRICK_SLAB, MOSSY_STONE_BRICK_SLAB, BlockSlabMossyStoneBrick.class, V1_21_20);
        registerBlock(BlockNames.SMOOTH_QUARTZ_SLAB, ItemBlockNames.SMOOTH_QUARTZ_SLAB, SMOOTH_QUARTZ_SLAB, BlockSlabSmoothQuartz.class, V1_21_20);
        registerBlock(BlockNames.NORMAL_STONE_SLAB, ItemBlockNames.NORMAL_STONE_SLAB, NORMAL_STONE_SLAB, BlockSlabNormalStone.class, V1_21_20);
        registerBlock(BlockNames.CUT_SANDSTONE_SLAB, ItemBlockNames.CUT_SANDSTONE_SLAB, CUT_SANDSTONE_SLAB, BlockSlabCutSandstone.class, V1_21_20);
        registerBlock(BlockNames.CUT_RED_SANDSTONE_SLAB, ItemBlockNames.CUT_RED_SANDSTONE_SLAB, CUT_RED_SANDSTONE_SLAB, BlockSlabCutRedSandstone.class, V1_21_20);

        registerBlock(BlockNames.MOSSY_STONE_BRICK_DOUBLE_SLAB, ItemBlockNames.MOSSY_STONE_BRICK_DOUBLE_SLAB, MOSSY_STONE_BRICK_DOUBLE_SLAB, BlockDoubleSlabMossyStoneBrick.class, V1_21_20);
        registerBlock(BlockNames.SMOOTH_QUARTZ_DOUBLE_SLAB, ItemBlockNames.SMOOTH_QUARTZ_DOUBLE_SLAB, SMOOTH_QUARTZ_DOUBLE_SLAB, BlockDoubleSlabSmoothQuartz.class, V1_21_20);
        registerBlock(BlockNames.NORMAL_STONE_DOUBLE_SLAB, ItemBlockNames.NORMAL_STONE_DOUBLE_SLAB, NORMAL_STONE_DOUBLE_SLAB, BlockDoubleSlabNormalStone.class, V1_21_20);
        registerBlock(BlockNames.CUT_SANDSTONE_DOUBLE_SLAB, ItemBlockNames.CUT_SANDSTONE_DOUBLE_SLAB, CUT_SANDSTONE_DOUBLE_SLAB, BlockDoubleSlabCutSandstone.class, V1_21_20);
        registerBlock(BlockNames.CUT_RED_SANDSTONE_DOUBLE_SLAB, ItemBlockNames.CUT_RED_SANDSTONE_DOUBLE_SLAB, CUT_RED_SANDSTONE_DOUBLE_SLAB, BlockDoubleSlabCutRedSandstone.class, V1_21_20);

        registerBlock(BlockNames.TUBE_CORAL_WALL_FAN, ItemBlockNames.TUBE_CORAL_WALL_FAN, TUBE_CORAL_WALL_FAN, BlockCoralFanHangTube.class, V1_21_20);
        registerBlock(BlockNames.BRAIN_CORAL_WALL_FAN, ItemBlockNames.BRAIN_CORAL_WALL_FAN, BRAIN_CORAL_WALL_FAN, BlockCoralFanHangBrain.class, V1_21_20);
        registerBlock(BlockNames.BUBBLE_CORAL_WALL_FAN, ItemBlockNames.BUBBLE_CORAL_WALL_FAN, BUBBLE_CORAL_WALL_FAN, BlockCoralFanHangBubble.class, V1_21_20);
        registerBlock(BlockNames.FIRE_CORAL_WALL_FAN, ItemBlockNames.FIRE_CORAL_WALL_FAN, FIRE_CORAL_WALL_FAN, BlockCoralFanHangFire.class, V1_21_20);
        registerBlock(BlockNames.HORN_CORAL_WALL_FAN, ItemBlockNames.HORN_CORAL_WALL_FAN, HORN_CORAL_WALL_FAN, BlockCoralFanHangHorn.class, V1_21_20);

        registerBlock(BlockNames.DEAD_TUBE_CORAL_WALL_FAN, ItemBlockNames.DEAD_TUBE_CORAL_WALL_FAN, DEAD_TUBE_CORAL_WALL_FAN, BlockCoralFanHangDeadTube.class, V1_21_20);
        registerBlock(BlockNames.DEAD_BRAIN_CORAL_WALL_FAN, ItemBlockNames.DEAD_BRAIN_CORAL_WALL_FAN, DEAD_BRAIN_CORAL_WALL_FAN, BlockCoralFanHangDeadBrain.class, V1_21_20);
        registerBlock(BlockNames.DEAD_BUBBLE_CORAL_WALL_FAN, ItemBlockNames.DEAD_BUBBLE_CORAL_WALL_FAN, DEAD_BUBBLE_CORAL_WALL_FAN, BlockCoralFanHangDeadBubble.class, V1_21_20);
        registerBlock(BlockNames.DEAD_FIRE_CORAL_WALL_FAN, ItemBlockNames.DEAD_FIRE_CORAL_WALL_FAN, DEAD_FIRE_CORAL_WALL_FAN, BlockCoralFanHangDeadFire.class, V1_21_20);
        registerBlock(BlockNames.DEAD_HORN_CORAL_WALL_FAN, ItemBlockNames.DEAD_HORN_CORAL_WALL_FAN, DEAD_HORN_CORAL_WALL_FAN, BlockCoralFanHangDeadHorn.class, V1_21_20);

        registerBlock(BlockNames.INFESTED_STONE, ItemBlockNames.INFESTED_STONE, INFESTED_STONE, BlockStoneInfested.class, V1_21_20);
        registerBlock(BlockNames.INFESTED_COBBLESTONE, ItemBlockNames.INFESTED_COBBLESTONE, INFESTED_COBBLESTONE, BlockCobblestoneInfested.class, V1_21_20);
        registerBlock(BlockNames.INFESTED_STONE_BRICKS, ItemBlockNames.INFESTED_STONE_BRICKS, INFESTED_STONE_BRICKS, BlockBricksStoneInfested.class, V1_21_20);
        registerBlock(BlockNames.INFESTED_MOSSY_STONE_BRICKS, ItemBlockNames.INFESTED_MOSSY_STONE_BRICKS, INFESTED_MOSSY_STONE_BRICKS, BlockBricksMossyStoneInfested.class, V1_21_20);
        registerBlock(BlockNames.INFESTED_CRACKED_STONE_BRICKS, ItemBlockNames.INFESTED_CRACKED_STONE_BRICKS, INFESTED_CRACKED_STONE_BRICKS, BlockBricksCrackedStoneInfested.class, V1_21_20);
        registerBlock(BlockNames.INFESTED_CHISELED_STONE_BRICKS, ItemBlockNames.INFESTED_CHISELED_STONE_BRICKS, INFESTED_CHISELED_STONE_BRICKS, BlockBricksChiseledStoneInfested.class, V1_21_20);

        registerBlock(BlockNames.STONE_BRICKS, ItemBlockNames.STONE_BRICKS, STONE_BRICKS, BlockBricksStone.class, V1_21_20);
        registerBlock(BlockNames.MOSSY_STONE_BRICKS, ItemBlockNames.MOSSY_STONE_BRICKS, MOSSY_STONE_BRICKS, BlockBricksMossyStone.class, V1_21_20);
        registerBlock(BlockNames.CRACKED_STONE_BRICKS, ItemBlockNames.CRACKED_STONE_BRICKS, CRACKED_STONE_BRICKS, BlockBricksCrackedStone.class, V1_21_20);
        registerBlock(BlockNames.CHISELED_STONE_BRICKS, ItemBlockNames.CHISELED_STONE_BRICKS, CHISELED_STONE_BRICKS, BlockBricksChiseledStone.class, V1_21_20);

        registerBlock(BlockNames.DARK_PRISMARINE, ItemBlockNames.DARK_PRISMARINE, DARK_PRISMARINE, BlockPrismarineDark.class, V1_21_20);
        registerBlock(BlockNames.PRISMARINE_BRICKS, ItemBlockNames.PRISMARINE_BRICKS, PRISMARINE_BRICKS, BlockBricksPrismarine.class, V1_21_20);

        registerBlock(BlockNames.CHISELED_SANDSTONE, ItemBlockNames.CHISELED_SANDSTONE, CHISELED_SANDSTONE, BlockSandstoneChiseled.class, V1_21_20);
        registerBlock(BlockNames.CUT_SANDSTONE, ItemBlockNames.CUT_SANDSTONE, CUT_SANDSTONE, BlockSandstoneCut.class, V1_21_20);
        registerBlock(BlockNames.SMOOTH_SANDSTONE, ItemBlockNames.SMOOTH_SANDSTONE, SMOOTH_SANDSTONE, BlockSandstoneSmooth.class, V1_21_20);

        registerBlock(BlockNames.CHISELED_RED_SANDSTONE, ItemBlockNames.CHISELED_RED_SANDSTONE, CHISELED_RED_SANDSTONE, BlockSandstoneRedChiseled.class, V1_21_20);
        registerBlock(BlockNames.CUT_RED_SANDSTONE, ItemBlockNames.CUT_RED_SANDSTONE, CUT_RED_SANDSTONE, BlockSandstoneRedCut.class, V1_21_20);
        registerBlock(BlockNames.SMOOTH_RED_SANDSTONE, ItemBlockNames.SMOOTH_RED_SANDSTONE, SMOOTH_RED_SANDSTONE, BlockSandstoneRedSmooth.class, V1_21_20);

        registerBlock(BlockNames.RED_SAND, ItemBlockNames.RED_SAND, RED_SAND, BlockSandRed.class, V1_21_20);

        registerBlock(BlockNames.COARSE_DIRT, ItemBlockNames.COARSE_DIRT, COARSE_DIRT, BlockDirtCoarse.class, V1_21_20);

        registerBlock(BlockNames.LIGHT_BLOCK_0, ItemBlockNames.LIGHT_BLOCK_0, LIGHT_BLOCK_0, BlockLight0.class, V1_21_20);
        registerBlock(BlockNames.LIGHT_BLOCK_1, ItemBlockNames.LIGHT_BLOCK_1, LIGHT_BLOCK_1, BlockLight1.class, V1_21_20);
        registerBlock(BlockNames.LIGHT_BLOCK_2, ItemBlockNames.LIGHT_BLOCK_2, LIGHT_BLOCK_2, BlockLight2.class, V1_21_20);
        registerBlock(BlockNames.LIGHT_BLOCK_3, ItemBlockNames.LIGHT_BLOCK_3, LIGHT_BLOCK_3, BlockLight3.class, V1_21_20);
        registerBlock(BlockNames.LIGHT_BLOCK_4, ItemBlockNames.LIGHT_BLOCK_4, LIGHT_BLOCK_4, BlockLight4.class, V1_21_20);
        registerBlock(BlockNames.LIGHT_BLOCK_5, ItemBlockNames.LIGHT_BLOCK_5, LIGHT_BLOCK_5, BlockLight5.class, V1_21_20);
        registerBlock(BlockNames.LIGHT_BLOCK_6, ItemBlockNames.LIGHT_BLOCK_6, LIGHT_BLOCK_6, BlockLight6.class, V1_21_20);
        registerBlock(BlockNames.LIGHT_BLOCK_7, ItemBlockNames.LIGHT_BLOCK_7, LIGHT_BLOCK_7, BlockLight7.class, V1_21_20);
        registerBlock(BlockNames.LIGHT_BLOCK_8, ItemBlockNames.LIGHT_BLOCK_8, LIGHT_BLOCK_8, BlockLight8.class, V1_21_20);
        registerBlock(BlockNames.LIGHT_BLOCK_9, ItemBlockNames.LIGHT_BLOCK_9, LIGHT_BLOCK_9, BlockLight9.class, V1_21_20);
        registerBlock(BlockNames.LIGHT_BLOCK_10, ItemBlockNames.LIGHT_BLOCK_10, LIGHT_BLOCK_10, BlockLight10.class, V1_21_20);
        registerBlock(BlockNames.LIGHT_BLOCK_11, ItemBlockNames.LIGHT_BLOCK_11, LIGHT_BLOCK_11, BlockLight11.class, V1_21_20);
        registerBlock(BlockNames.LIGHT_BLOCK_12, ItemBlockNames.LIGHT_BLOCK_12, LIGHT_BLOCK_12, BlockLight12.class, V1_21_20);
        registerBlock(BlockNames.LIGHT_BLOCK_13, ItemBlockNames.LIGHT_BLOCK_13, LIGHT_BLOCK_13, BlockLight13.class, V1_21_20);
        registerBlock(BlockNames.LIGHT_BLOCK_14, ItemBlockNames.LIGHT_BLOCK_14, LIGHT_BLOCK_14, BlockLight14.class, V1_21_20);
        registerBlock(BlockNames.LIGHT_BLOCK_15, ItemBlockNames.LIGHT_BLOCK_15, LIGHT_BLOCK_15, BlockLight15.class, V1_21_20);

        registerBlock(BlockNames.CHIPPED_ANVIL, ItemBlockNames.CHIPPED_ANVIL, CHIPPED_ANVIL, BlockAnvilChipped.class, V1_21_20);
        registerBlock(BlockNames.DAMAGED_ANVIL, ItemBlockNames.DAMAGED_ANVIL, DAMAGED_ANVIL, BlockAnvilDamaged.class, V1_21_20);
        registerBlock(BlockNames.DEPRECATED_ANVIL, ItemBlockNames.DEPRECATED_ANVIL, DEPRECATED_ANVIL, BlockAnvilDeprecated.class, V1_21_20);

        registerBlock(BlockNames.CHISELED_QUARTZ_BLOCK, ItemBlockNames.CHISELED_QUARTZ_BLOCK, CHISELED_QUARTZ_BLOCK, BlockQuartzChiseled.class, V1_21_20);
        registerBlock(BlockNames.QUARTZ_PILLAR, ItemBlockNames.QUARTZ_PILLAR, QUARTZ_PILLAR, BlockQuartzPillar.class, V1_21_20);
        registerBlock(BlockNames.SMOOTH_QUARTZ, ItemBlockNames.SMOOTH_QUARTZ, SMOOTH_QUARTZ, BlockQuartzSmooth.class, V1_21_20);

        registerBlock(BlockNames.DEPRECATED_PURPUR_BLOCK_1, ItemBlockNames.DEPRECATED_PURPUR_BLOCK_1, DEPRECATED_PURPUR_BLOCK_1, BlockPurpurChiseled.class, V1_21_30);
        registerBlock(BlockNames.PURPUR_PILLAR, ItemBlockNames.PURPUR_PILLAR, PURPUR_PILLAR, BlockPurpurPillar.class, V1_21_30);
        registerBlock(BlockNames.DEPRECATED_PURPUR_BLOCK_2, ItemBlockNames.DEPRECATED_PURPUR_BLOCK_2, DEPRECATED_PURPUR_BLOCK_2, BlockPurpurSmooth.class, V1_21_30);

        registerBlock(BlockNames.MOSSY_COBBLESTONE_WALL, ItemBlockNames.MOSSY_COBBLESTONE_WALL, MOSSY_COBBLESTONE_WALL, BlockWallMossyCobblestone.class, V1_21_30);
        registerBlock(BlockNames.GRANITE_WALL, ItemBlockNames.GRANITE_WALL, GRANITE_WALL, BlockWallGranite.class, V1_21_30);
        registerBlock(BlockNames.DIORITE_WALL, ItemBlockNames.DIORITE_WALL, DIORITE_WALL, BlockWallDiorite.class, V1_21_30);
        registerBlock(BlockNames.ANDESITE_WALL, ItemBlockNames.ANDESITE_WALL, ANDESITE_WALL, BlockWallAndesite.class, V1_21_30);
        registerBlock(BlockNames.SANDSTONE_WALL, ItemBlockNames.SANDSTONE_WALL, SANDSTONE_WALL, BlockWallSandstone.class, V1_21_30);
        registerBlock(BlockNames.BRICK_WALL, ItemBlockNames.BRICK_WALL, BRICK_WALL, BlockWallBrick.class, V1_21_30);
        registerBlock(BlockNames.STONE_BRICK_WALL, ItemBlockNames.STONE_BRICK_WALL, STONE_BRICK_WALL, BlockWallStoneBrick.class, V1_21_30);
        registerBlock(BlockNames.MOSSY_STONE_BRICK_WALL, ItemBlockNames.MOSSY_STONE_BRICK_WALL, MOSSY_STONE_BRICK_WALL, BlockWallMossyStoneBrick.class, V1_21_30);
        registerBlock(BlockNames.NETHER_BRICK_WALL, ItemBlockNames.NETHER_BRICK_WALL, NETHER_BRICK_WALL, BlockWallNetherBrick.class, V1_21_30);
        registerBlock(BlockNames.END_STONE_BRICK_WALL, ItemBlockNames.END_STONE_BRICK_WALL, END_STONE_BRICK_WALL, BlockWallEndBrick.class, V1_21_30);
        registerBlock(BlockNames.PRISMARINE_WALL, ItemBlockNames.PRISMARINE_WALL, PRISMARINE_WALL, BlockWallPrismarine.class, V1_21_30);
        registerBlock(BlockNames.RED_SANDSTONE_WALL, ItemBlockNames.RED_SANDSTONE_WALL, RED_SANDSTONE_WALL, BlockWallRedSandstone.class, V1_21_30);
        registerBlock(BlockNames.RED_NETHER_BRICK_WALL, ItemBlockNames.RED_NETHER_BRICK_WALL, RED_NETHER_BRICK_WALL, BlockWallRedNetherBrick.class, V1_21_30);

        registerBlock(BlockNames.WET_SPONGE, ItemBlockNames.WET_SPONGE, WET_SPONGE, BlockSpongeWet.class, V1_21_30);

        registerBlock(BlockNames.COLORED_TORCH_RED, ItemBlockNames.COLORED_TORCH_RED, COLORED_TORCH_RED, BlockTorchColoredRed.class, V1_21_30);
        registerBlock(BlockNames.COLORED_TORCH_GREEN, ItemBlockNames.COLORED_TORCH_GREEN, COLORED_TORCH_GREEN, BlockTorchColoredGreen.class, V1_21_30);

        registerBlock(BlockNames.COLORED_TORCH_BLUE, ItemBlockNames.COLORED_TORCH_BLUE, COLORED_TORCH_BLUE, BlockTorchColoredBlue.class, V1_21_30);
        registerBlock(BlockNames.COLORED_TORCH_PURPLE, ItemBlockNames.COLORED_TORCH_PURPLE, COLORED_TORCH_PURPLE, BlockTorchColoredPurple.class, V1_21_30);

        registerBlock(BlockNames.COMPOUND_CREATOR, ItemBlockNames.COMPOUND_CREATOR, COMPOUND_CREATOR, BlockCompoundCreator.class, V1_21_30);
        registerBlock(BlockNames.MATERIAL_REDUCER, ItemBlockNames.MATERIAL_REDUCER, MATERIAL_REDUCER, BlockMaterialReducer.class, V1_21_30);
        registerBlock(BlockNames.ELEMENT_CONSTRUCTOR, ItemBlockNames.ELEMENT_CONSTRUCTOR, ELEMENT_CONSTRUCTOR, BlockElementConstructor.class, V1_21_30);
        registerBlock(BlockNames.LAB_TABLE, ItemBlockNames.LAB_TABLE, LAB_TABLE, BlockLabTable.class, V1_21_30);

        registerBlock(BlockNames.UNDERWATER_TNT, ItemBlockNames.UNDERWATER_TNT, UNDERWATER_TNT, BlockTntUnderwater.class, V1_21_30);
*/
        registerBlock(BlockNames.WITHER_SKELETON_SKULL, ItemBlockNames.WITHER_SKELETON_SKULL, WITHER_SKELETON_SKULL, BlockSkullWitherSkeleton.class, V1_21_40);
        registerBlock(BlockNames.ZOMBIE_HEAD, ItemBlockNames.ZOMBIE_HEAD, ZOMBIE_HEAD, BlockSkullZombie.class, V1_21_40);
        registerBlock(BlockNames.PLAYER_HEAD, ItemBlockNames.PLAYER_HEAD, PLAYER_HEAD, BlockSkullPlayer.class, V1_21_40);
        registerBlock(BlockNames.CREEPER_HEAD, ItemBlockNames.CREEPER_HEAD, CREEPER_HEAD, BlockSkullCreeper.class, V1_21_40);
        registerBlock(BlockNames.DRAGON_HEAD, ItemBlockNames.DRAGON_HEAD, DRAGON_HEAD, BlockSkullDragon.class, V1_21_40);
        registerBlock(BlockNames.PIGLIN_HEAD, ItemBlockNames.PIGLIN_HEAD, PIGLIN_HEAD, BlockSkullPiglin.class, V1_21_40);

        registerBlock(BlockNames.MUSHROOM_STEM, ItemBlockNames.MUSHROOM_STEM, MUSHROOM_STEM, BlockHugeMushroomStem.class, V1_21_40);
    }

    private static void registerElements() {
        registerBlock(BlockNames.ELEMENT_0, ItemBlockNames.ELEMENT_0, ELEMENT_0, BlockElementUnknown.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_1, ItemBlockNames.ELEMENT_1, ELEMENT_1, BlockElement1.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_2, ItemBlockNames.ELEMENT_2, ELEMENT_2, BlockElement2.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_3, ItemBlockNames.ELEMENT_3, ELEMENT_3, BlockElement3.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_4, ItemBlockNames.ELEMENT_4, ELEMENT_4, BlockElement4.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_5, ItemBlockNames.ELEMENT_5, ELEMENT_5, BlockElement5.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_6, ItemBlockNames.ELEMENT_6, ELEMENT_6, BlockElement6.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_7, ItemBlockNames.ELEMENT_7, ELEMENT_7, BlockElement7.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_8, ItemBlockNames.ELEMENT_8, ELEMENT_8, BlockElement8.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_9, ItemBlockNames.ELEMENT_9, ELEMENT_9, BlockElement9.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_10, ItemBlockNames.ELEMENT_10, ELEMENT_10, BlockElement10.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_11, ItemBlockNames.ELEMENT_11, ELEMENT_11, BlockElement11.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_12, ItemBlockNames.ELEMENT_12, ELEMENT_12, BlockElement12.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_13, ItemBlockNames.ELEMENT_13, ELEMENT_13, BlockElement13.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_14, ItemBlockNames.ELEMENT_14, ELEMENT_14, BlockElement14.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_15, ItemBlockNames.ELEMENT_15, ELEMENT_15, BlockElement15.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_16, ItemBlockNames.ELEMENT_16, ELEMENT_16, BlockElement16.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_17, ItemBlockNames.ELEMENT_17, ELEMENT_17, BlockElement17.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_18, ItemBlockNames.ELEMENT_18, ELEMENT_18, BlockElement18.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_19, ItemBlockNames.ELEMENT_19, ELEMENT_19, BlockElement19.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_20, ItemBlockNames.ELEMENT_20, ELEMENT_20, BlockElement20.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_21, ItemBlockNames.ELEMENT_21, ELEMENT_21, BlockElement21.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_22, ItemBlockNames.ELEMENT_22, ELEMENT_22, BlockElement22.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_23, ItemBlockNames.ELEMENT_23, ELEMENT_23, BlockElement23.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_24, ItemBlockNames.ELEMENT_24, ELEMENT_24, BlockElement24.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_25, ItemBlockNames.ELEMENT_25, ELEMENT_25, BlockElement25.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_26, ItemBlockNames.ELEMENT_26, ELEMENT_26, BlockElement26.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_27, ItemBlockNames.ELEMENT_27, ELEMENT_27, BlockElement27.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_28, ItemBlockNames.ELEMENT_28, ELEMENT_28, BlockElement28.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_29, ItemBlockNames.ELEMENT_29, ELEMENT_29, BlockElement29.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_30, ItemBlockNames.ELEMENT_30, ELEMENT_30, BlockElement30.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_31, ItemBlockNames.ELEMENT_31, ELEMENT_31, BlockElement31.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_32, ItemBlockNames.ELEMENT_32, ELEMENT_32, BlockElement32.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_33, ItemBlockNames.ELEMENT_33, ELEMENT_33, BlockElement33.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_34, ItemBlockNames.ELEMENT_34, ELEMENT_34, BlockElement34.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_35, ItemBlockNames.ELEMENT_35, ELEMENT_35, BlockElement35.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_36, ItemBlockNames.ELEMENT_36, ELEMENT_36, BlockElement36.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_37, ItemBlockNames.ELEMENT_37, ELEMENT_37, BlockElement37.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_38, ItemBlockNames.ELEMENT_38, ELEMENT_38, BlockElement38.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_39, ItemBlockNames.ELEMENT_39, ELEMENT_39, BlockElement39.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_40, ItemBlockNames.ELEMENT_40, ELEMENT_40, BlockElement40.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_41, ItemBlockNames.ELEMENT_41, ELEMENT_41, BlockElement41.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_42, ItemBlockNames.ELEMENT_42, ELEMENT_42, BlockElement42.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_43, ItemBlockNames.ELEMENT_43, ELEMENT_43, BlockElement43.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_44, ItemBlockNames.ELEMENT_44, ELEMENT_44, BlockElement44.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_45, ItemBlockNames.ELEMENT_45, ELEMENT_45, BlockElement45.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_46, ItemBlockNames.ELEMENT_46, ELEMENT_46, BlockElement46.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_47, ItemBlockNames.ELEMENT_47, ELEMENT_47, BlockElement47.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_48, ItemBlockNames.ELEMENT_48, ELEMENT_48, BlockElement48.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_49, ItemBlockNames.ELEMENT_49, ELEMENT_49, BlockElement49.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_50, ItemBlockNames.ELEMENT_50, ELEMENT_50, BlockElement50.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_51, ItemBlockNames.ELEMENT_51, ELEMENT_51, BlockElement51.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_52, ItemBlockNames.ELEMENT_52, ELEMENT_52, BlockElement52.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_53, ItemBlockNames.ELEMENT_53, ELEMENT_53, BlockElement53.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_54, ItemBlockNames.ELEMENT_54, ELEMENT_54, BlockElement54.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_55, ItemBlockNames.ELEMENT_55, ELEMENT_55, BlockElement55.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_56, ItemBlockNames.ELEMENT_56, ELEMENT_56, BlockElement56.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_57, ItemBlockNames.ELEMENT_57, ELEMENT_57, BlockElement57.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_58, ItemBlockNames.ELEMENT_58, ELEMENT_58, BlockElement58.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_59, ItemBlockNames.ELEMENT_59, ELEMENT_59, BlockElement59.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_60, ItemBlockNames.ELEMENT_60, ELEMENT_60, BlockElement60.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_61, ItemBlockNames.ELEMENT_61, ELEMENT_61, BlockElement61.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_62, ItemBlockNames.ELEMENT_62, ELEMENT_62, BlockElement62.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_63, ItemBlockNames.ELEMENT_63, ELEMENT_63, BlockElement63.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_64, ItemBlockNames.ELEMENT_64, ELEMENT_64, BlockElement64.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_65, ItemBlockNames.ELEMENT_65, ELEMENT_65, BlockElement65.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_66, ItemBlockNames.ELEMENT_66, ELEMENT_66, BlockElement66.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_67, ItemBlockNames.ELEMENT_67, ELEMENT_67, BlockElement67.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_68, ItemBlockNames.ELEMENT_68, ELEMENT_68, BlockElement68.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_69, ItemBlockNames.ELEMENT_69, ELEMENT_69, BlockElement69.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_70, ItemBlockNames.ELEMENT_70, ELEMENT_70, BlockElement70.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_71, ItemBlockNames.ELEMENT_71, ELEMENT_71, BlockElement71.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_72, ItemBlockNames.ELEMENT_72, ELEMENT_72, BlockElement72.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_73, ItemBlockNames.ELEMENT_73, ELEMENT_73, BlockElement73.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_74, ItemBlockNames.ELEMENT_74, ELEMENT_74, BlockElement74.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_75, ItemBlockNames.ELEMENT_75, ELEMENT_75, BlockElement75.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_76, ItemBlockNames.ELEMENT_76, ELEMENT_76, BlockElement76.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_77, ItemBlockNames.ELEMENT_77, ELEMENT_77, BlockElement77.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_78, ItemBlockNames.ELEMENT_78, ELEMENT_78, BlockElement78.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_79, ItemBlockNames.ELEMENT_79, ELEMENT_79, BlockElement79.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_80, ItemBlockNames.ELEMENT_80, ELEMENT_80, BlockElement80.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_81, ItemBlockNames.ELEMENT_81, ELEMENT_81, BlockElement81.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_82, ItemBlockNames.ELEMENT_82, ELEMENT_82, BlockElement82.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_83, ItemBlockNames.ELEMENT_83, ELEMENT_83, BlockElement83.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_84, ItemBlockNames.ELEMENT_84, ELEMENT_84, BlockElement84.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_85, ItemBlockNames.ELEMENT_85, ELEMENT_85, BlockElement85.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_86, ItemBlockNames.ELEMENT_86, ELEMENT_86, BlockElement86.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_87, ItemBlockNames.ELEMENT_87, ELEMENT_87, BlockElement87.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_88, ItemBlockNames.ELEMENT_88, ELEMENT_88, BlockElement88.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_89, ItemBlockNames.ELEMENT_89, ELEMENT_89, BlockElement89.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_90, ItemBlockNames.ELEMENT_90, ELEMENT_90, BlockElement90.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_91, ItemBlockNames.ELEMENT_91, ELEMENT_91, BlockElement91.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_92, ItemBlockNames.ELEMENT_92, ELEMENT_92, BlockElement92.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_93, ItemBlockNames.ELEMENT_93, ELEMENT_93, BlockElement93.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_94, ItemBlockNames.ELEMENT_94, ELEMENT_94, BlockElement94.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_95, ItemBlockNames.ELEMENT_95, ELEMENT_95, BlockElement95.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_96, ItemBlockNames.ELEMENT_96, ELEMENT_96, BlockElement96.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_97, ItemBlockNames.ELEMENT_97, ELEMENT_97, BlockElement97.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_98, ItemBlockNames.ELEMENT_98, ELEMENT_98, BlockElement98.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_99, ItemBlockNames.ELEMENT_99, ELEMENT_99, BlockElement99.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_100, ItemBlockNames.ELEMENT_100, ELEMENT_100, BlockElement100.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_101, ItemBlockNames.ELEMENT_101, ELEMENT_101, BlockElement101.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_102, ItemBlockNames.ELEMENT_102, ELEMENT_102, BlockElement102.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_103, ItemBlockNames.ELEMENT_103, ELEMENT_103, BlockElement103.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_104, ItemBlockNames.ELEMENT_104, ELEMENT_104, BlockElement104.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_105, ItemBlockNames.ELEMENT_105, ELEMENT_105, BlockElement105.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_106, ItemBlockNames.ELEMENT_106, ELEMENT_106, BlockElement106.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_107, ItemBlockNames.ELEMENT_107, ELEMENT_107, BlockElement107.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_108, ItemBlockNames.ELEMENT_108, ELEMENT_108, BlockElement108.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_109, ItemBlockNames.ELEMENT_109, ELEMENT_109, BlockElement109.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_110, ItemBlockNames.ELEMENT_110, ELEMENT_110, BlockElement110.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_111, ItemBlockNames.ELEMENT_111, ELEMENT_111, BlockElement111.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_112, ItemBlockNames.ELEMENT_112, ELEMENT_112, BlockElement112.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_113, ItemBlockNames.ELEMENT_113, ELEMENT_113, BlockElement113.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_114, ItemBlockNames.ELEMENT_114, ELEMENT_114, BlockElement114.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_115, ItemBlockNames.ELEMENT_115, ELEMENT_115, BlockElement115.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_116, ItemBlockNames.ELEMENT_116, ELEMENT_116, BlockElement116.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_117, ItemBlockNames.ELEMENT_117, ELEMENT_117, BlockElement117.class, V1_4_0);
        registerBlock(BlockNames.ELEMENT_118, ItemBlockNames.ELEMENT_118, ELEMENT_118, BlockElement118.class, V1_4_0);
    }

    private static void registerBlockAliases() {
        registerBlockAlias(BlockNames.PISTON_ARM_COLLISION, BlockNames.PISTONARMCOLLISION, V1_18_30);
        registerBlockAlias(BlockNames.INVISIBLE_BEDROCK, BlockNames.INVISIBLEBEDROCK, V1_18_30);
        registerBlockAlias(BlockNames.TRIP_WIRE, BlockNames.TRIPWIRE, V1_18_30);
        registerBlockAlias(BlockNames.SEA_LANTERN, BlockNames.SEALANTERN, V1_18_30);
        registerBlockAlias(BlockNames.CONCRETE_POWDER, BlockNames.CONCRETEPOWDER, V1_18_30);
        registerBlockAlias(BlockNames.MOVING_BLOCK, BlockNames.MOVINGBLOCK, V1_18_30);
        registerBlockAlias(BlockNames.STICKY_PISTON_ARM_COLLISION, BlockNames.STICKYPISTONARMCOLLISION, V1_18_30);

        registerBlockAlias(BlockNames.STONE_BLOCK_SLAB, BlockNames.STONE_SLAB, V1_19_0);
        registerBlockAlias(BlockNames.STONE_BLOCK_SLAB2, BlockNames.STONE_SLAB, V1_19_0);
        registerBlockAlias(BlockNames.STONE_BLOCK_SLAB3, BlockNames.STONE_SLAB, V1_19_0);
        registerBlockAlias(BlockNames.STONE_BLOCK_SLAB4, BlockNames.STONE_SLAB, V1_19_0);
        registerBlockAlias(BlockNames.DOUBLE_STONE_BLOCK_SLAB, BlockNames.DOUBLE_STONE_SLAB, V1_19_0);
        registerBlockAlias(BlockNames.DOUBLE_STONE_BLOCK_SLAB2, BlockNames.DOUBLE_STONE_SLAB, V1_19_0);
        registerBlockAlias(BlockNames.DOUBLE_STONE_BLOCK_SLAB3, BlockNames.DOUBLE_STONE_SLAB, V1_19_0);
        registerBlockAlias(BlockNames.DOUBLE_STONE_BLOCK_SLAB4, BlockNames.DOUBLE_STONE_SLAB, V1_19_0);

        registerBlockAlias(BlockNames.GRASS_BLOCK, BlockNames.GRASS, V1_20_70);

        registerBlockAlias(BlockNames.SHORT_GRASS, BlockNames.TALLGRASS, V1_21_0);

        registerBlockAlias(BlockNames.DANDELION, BlockNames.YELLOW_FLOWER, V1_21_20);

        registerBlockAlias(BlockNames.SKELETON_SKULL, BlockNames.SKULL, V1_21_40);
        if (!V1_21_40.isAvailable()) {
            registerBlockAlias(BlockNames.WITHER_SKELETON_SKULL, BlockNames.SKULL, V1_21_40);
            registerBlockAlias(BlockNames.ZOMBIE_HEAD, BlockNames.SKULL, V1_21_40);
            registerBlockAlias(BlockNames.PLAYER_HEAD, BlockNames.SKULL, V1_21_40);
            registerBlockAlias(BlockNames.CREEPER_HEAD, BlockNames.SKULL, V1_21_40);
            registerBlockAlias(BlockNames.DRAGON_HEAD, BlockNames.SKULL, V1_21_40);
            registerBlockAlias(BlockNames.PIGLIN_HEAD, BlockNames.SKULL, V1_21_40);
        }
    }

    @SuppressWarnings("deprecation")
    private static void registerItemAliases() {
        registerItemAlias(ItemBlockNames.WHITE_GLAZED_TERRACOTTA, ItemBlockNames.GLAZED_TERRACOTTA_WHITE, V1_12_0);
        registerItemAlias(ItemBlockNames.ORANGE_GLAZED_TERRACOTTA, ItemBlockNames.GLAZED_TERRACOTTA_ORANGE, V1_12_0);
        registerItemAlias(ItemBlockNames.MAGENTA_GLAZED_TERRACOTTA, ItemBlockNames.GLAZED_TERRACOTTA_MAGENTA, V1_12_0);
        registerItemAlias(ItemBlockNames.LIGHT_BLUE_GLAZED_TERRACOTTA, ItemBlockNames.GLAZED_TERRACOTTA_LIGHT_BLUE, V1_12_0);
        registerItemAlias(ItemBlockNames.YELLOW_GLAZED_TERRACOTTA, ItemBlockNames.GLAZED_TERRACOTTA_YELLOW, V1_12_0);
        registerItemAlias(ItemBlockNames.LIME_GLAZED_TERRACOTTA, ItemBlockNames.GLAZED_TERRACOTTA_LIME, V1_12_0);
        registerItemAlias(ItemBlockNames.PINK_GLAZED_TERRACOTTA, ItemBlockNames.GLAZED_TERRACOTTA_PINK, V1_12_0);
        registerItemAlias(ItemBlockNames.GRAY_GLAZED_TERRACOTTA, ItemBlockNames.GLAZED_TERRACOTTA_GRAY, V1_12_0);
        registerItemAlias(ItemBlockNames.SILVER_GLAZED_TERRACOTTA, ItemBlockNames.GLAZED_TERRACOTTA_SILVER, V1_12_0);
        registerItemAlias(ItemBlockNames.CYAN_GLAZED_TERRACOTTA, ItemBlockNames.GLAZED_TERRACOTTA_CYAN, V1_12_0);
        registerItemAlias(ItemBlockNames.PURPLE_GLAZED_TERRACOTTA, ItemBlockNames.GLAZED_TERRACOTTA_PURPLE, V1_12_0);
        registerItemAlias(ItemBlockNames.BLUE_GLAZED_TERRACOTTA, ItemBlockNames.GLAZED_TERRACOTTA_BLUE, V1_12_0);
        registerItemAlias(ItemBlockNames.BROWN_GLAZED_TERRACOTTA, ItemBlockNames.GLAZED_TERRACOTTA_BROWN, V1_12_0);
        registerItemAlias(ItemBlockNames.GREEN_GLAZED_TERRACOTTA, ItemBlockNames.GLAZED_TERRACOTTA_GREEN, V1_12_0);
        registerItemAlias(ItemBlockNames.RED_GLAZED_TERRACOTTA, ItemBlockNames.GLAZED_TERRACOTTA_RED, V1_12_0);
        registerItemAlias(ItemBlockNames.BLACK_GLAZED_TERRACOTTA, ItemBlockNames.GLAZED_TERRACOTTA_BLACK, V1_12_0);

        registerItemAlias(ItemBlockNames.PISTON_ARM_COLLISION, ItemBlockNames.PISTONARMCOLLISION, V1_18_30);
        registerItemAlias(ItemBlockNames.INVISIBLE_BEDROCK, ItemBlockNames.INVISIBLEBEDROCK, V1_18_30);
        registerItemAlias(ItemBlockNames.ITEM_BREWING_STAND, ItemBlockNames.BREWINGSTANDBLOCK, V1_18_30);
        registerItemAlias(ItemBlockNames.TRIP_WIRE, ItemBlockNames.TRIPWIRE, V1_18_30);
        registerItemAlias(ItemBlockNames.SEA_LANTERN, ItemBlockNames.SEALANTERN, V1_18_30);
        registerItemAlias(ItemBlockNames.MOVING_BLOCK, ItemBlockNames.MOVINGBLOCK, V1_18_30);
        registerItemAlias(ItemBlockNames.STICKY_PISTON_ARM_COLLISION, ItemBlockNames.STICKYPISTONARMCOLLISION, V1_18_30);

        registerItemAlias(ItemBlockNames.STONE_BLOCK_SLAB, ItemBlockNames.DOUBLE_STONE_SLAB, V1_19_0);
        registerItemAlias(ItemBlockNames.STONE_BLOCK_SLAB2, ItemBlockNames.DOUBLE_STONE_SLAB, V1_19_0);
        registerItemAlias(ItemBlockNames.STONE_BLOCK_SLAB3, ItemBlockNames.DOUBLE_STONE_SLAB, V1_19_0);
        registerItemAlias(ItemBlockNames.STONE_BLOCK_SLAB4, ItemBlockNames.DOUBLE_STONE_SLAB, V1_19_0);
        registerItemAlias(ItemBlockNames.DOUBLE_STONE_BLOCK_SLAB, ItemBlockNames.REAL_DOUBLE_STONE_SLAB, V1_19_0);
        registerItemAlias(ItemBlockNames.DOUBLE_STONE_BLOCK_SLAB2, ItemBlockNames.REAL_DOUBLE_STONE_SLAB, V1_19_0);
        registerItemAlias(ItemBlockNames.DOUBLE_STONE_BLOCK_SLAB3, ItemBlockNames.REAL_DOUBLE_STONE_SLAB, V1_19_0);
        registerItemAlias(ItemBlockNames.DOUBLE_STONE_BLOCK_SLAB4, ItemBlockNames.REAL_DOUBLE_STONE_SLAB, V1_19_0);

        registerItemAlias(ItemBlockNames.GRASS_BLOCK, ItemBlockNames.GRASS, V1_20_70);

        registerItemAlias(ItemBlockNames.SHORT_GRASS, ItemBlockNames.TALLGRASS, V1_21_0);

        registerItemAlias(ItemBlockNames.DANDELION, ItemBlockNames.YELLOW_FLOWER, V1_21_20);
    }

    private static void registerComplexAliases() {
        registerComplexAlias(BlockNames.WHITE_WOOL, WHITE_WOOL, BlockNames.WOOL, WOOL, DyeColor.WHITE.getWoolData(), V1_19_70);
        registerComplexAlias(BlockNames.ORANGE_WOOL, ORANGE_WOOL, BlockNames.WOOL, WOOL, DyeColor.ORANGE.getWoolData(), V1_19_70);
        registerComplexAlias(BlockNames.MAGENTA_WOOL, MAGENTA_WOOL, BlockNames.WOOL, WOOL, DyeColor.MAGENTA.getWoolData(), V1_19_70);
        registerComplexAlias(BlockNames.LIGHT_BLUE_WOOL, LIGHT_BLUE_WOOL, BlockNames.WOOL, WOOL, DyeColor.LIGHT_BLUE.getWoolData(), V1_19_70);
        registerComplexAlias(BlockNames.YELLOW_WOOL, YELLOW_WOOL, BlockNames.WOOL, WOOL, DyeColor.YELLOW.getWoolData(), V1_19_70);
        registerComplexAlias(BlockNames.LIME_WOOL, LIME_WOOL, BlockNames.WOOL, WOOL, DyeColor.LIME.getWoolData(), V1_19_70);
        registerComplexAlias(BlockNames.PINK_WOOL, PINK_WOOL, BlockNames.WOOL, WOOL, DyeColor.PINK.getWoolData(), V1_19_70);
        registerComplexAlias(BlockNames.GRAY_WOOL, GRAY_WOOL, BlockNames.WOOL, WOOL, DyeColor.GRAY.getWoolData(), V1_19_70);
        registerComplexAlias(BlockNames.LIGHT_GRAY_WOOL, LIGHT_GRAY_WOOL, BlockNames.WOOL, WOOL, DyeColor.LIGHT_GRAY.getWoolData(), V1_19_70);
        registerComplexAlias(BlockNames.CYAN_WOOL, CYAN_WOOL, BlockNames.WOOL, WOOL, DyeColor.CYAN.getWoolData(), V1_19_70);
        registerComplexAlias(BlockNames.PURPLE_WOOL, PURPLE_WOOL, BlockNames.WOOL, WOOL, DyeColor.PURPLE.getWoolData(), V1_19_70);
        registerComplexAlias(BlockNames.BLUE_WOOL, BLUE_WOOL, BlockNames.WOOL, WOOL, DyeColor.BLUE.getWoolData(), V1_19_70);
        registerComplexAlias(BlockNames.BROWN_WOOL, BROWN_WOOL, BlockNames.WOOL, WOOL, DyeColor.BROWN.getWoolData(), V1_19_70);
        registerComplexAlias(BlockNames.GREEN_WOOL, GREEN_WOOL, BlockNames.WOOL, WOOL, DyeColor.GREEN.getWoolData(), V1_19_70);
        registerComplexAlias(BlockNames.RED_WOOL, RED_WOOL, BlockNames.WOOL, WOOL, DyeColor.RED.getWoolData(), V1_19_70);
        registerComplexAlias(BlockNames.BLACK_WOOL, BLACK_WOOL, BlockNames.WOOL, WOOL, DyeColor.BLACK.getWoolData(), V1_19_70);

        registerComplexAlias(BlockNames.OAK_LOG, OAK_LOG, BlockNames.LOG, LOG, BlockWood.OAK, V1_19_80);
        registerComplexAlias(BlockNames.SPRUCE_LOG, SPRUCE_LOG, BlockNames.LOG, LOG, BlockWood.SPRUCE, V1_19_80);
        registerComplexAlias(BlockNames.BIRCH_LOG, BIRCH_LOG, BlockNames.LOG, LOG, BlockWood.BIRCH, V1_19_80);
        registerComplexAlias(BlockNames.JUNGLE_LOG, JUNGLE_LOG, BlockNames.LOG, LOG, BlockWood.JUNGLE, V1_19_80);

        registerComplexAlias(BlockNames.ACACIA_LOG, ACACIA_LOG, BlockNames.LOG2, LOG2, BlockWood2.ACACIA, V1_19_80);
        registerComplexAlias(BlockNames.DARK_OAK_LOG, DARK_OAK_LOG, BlockNames.LOG2, LOG2, BlockWood2.DARK_OAK, V1_19_80);

        registerComplexAlias(BlockNames.OAK_FENCE, OAK_FENCE, BlockNames.FENCE, FENCE, BlockFence.OAK, V1_19_80);
        registerComplexAlias(BlockNames.SPRUCE_FENCE, SPRUCE_FENCE, BlockNames.FENCE, FENCE, BlockFence.SPRUCE, V1_19_80);
        registerComplexAlias(BlockNames.BIRCH_FENCE, BIRCH_FENCE, BlockNames.FENCE, FENCE, BlockFence.BIRCH, V1_19_80);
        registerComplexAlias(BlockNames.JUNGLE_FENCE, JUNGLE_FENCE, BlockNames.FENCE, FENCE, BlockFence.JUNGLE, V1_19_80);
        registerComplexAlias(BlockNames.ACACIA_FENCE, ACACIA_FENCE, BlockNames.FENCE, FENCE, BlockFence.ACACIA, V1_19_80);
        registerComplexAlias(BlockNames.DARK_OAK_FENCE, DARK_OAK_FENCE, BlockNames.FENCE, FENCE, BlockFence.DARK_OAK, V1_19_80);

        registerComplexAlias(BlockNames.TUBE_CORAL, TUBE_CORAL, BlockNames.CORAL, CORAL, BlockCoral.BLUE, V1_20_0);
        registerComplexAlias(BlockNames.BRAIN_CORAL, BRAIN_CORAL, BlockNames.CORAL, CORAL, BlockCoral.PINK, V1_20_0);
        registerComplexAlias(BlockNames.BUBBLE_CORAL, BUBBLE_CORAL, BlockNames.CORAL, CORAL, BlockCoral.PURPLE, V1_20_0);
        registerComplexAlias(BlockNames.FIRE_CORAL, FIRE_CORAL, BlockNames.CORAL, CORAL, BlockCoral.RED, V1_20_0);
        registerComplexAlias(BlockNames.HORN_CORAL, HORN_CORAL, BlockNames.CORAL, CORAL, BlockCoral.YELLOW, V1_20_0);

        registerComplexAlias(BlockNames.DEAD_TUBE_CORAL, DEAD_TUBE_CORAL, BlockNames.CORAL, CORAL, BlockCoral.DEAD_BIT | BlockCoral.BLUE, V1_20_0);
        registerComplexAlias(BlockNames.DEAD_BRAIN_CORAL, DEAD_BRAIN_CORAL, BlockNames.CORAL, CORAL, BlockCoral.DEAD_BIT | BlockCoral.PINK, V1_20_0);
        registerComplexAlias(BlockNames.DEAD_BUBBLE_CORAL, DEAD_BUBBLE_CORAL, BlockNames.CORAL, CORAL, BlockCoral.DEAD_BIT | BlockCoral.PURPLE, V1_20_0);
        registerComplexAlias(BlockNames.DEAD_FIRE_CORAL, DEAD_FIRE_CORAL, BlockNames.CORAL, CORAL, BlockCoral.DEAD_BIT | BlockCoral.RED, V1_20_0);
        registerComplexAlias(BlockNames.DEAD_HORN_CORAL, DEAD_HORN_CORAL, BlockNames.CORAL, CORAL, BlockCoral.DEAD_BIT | BlockCoral.YELLOW, V1_20_0);

        registerComplexAlias(BlockNames.WHITE_CARPET, WHITE_CARPET, BlockNames.CARPET, CARPET, DyeColor.WHITE.getWoolData(), V1_20_0);
        registerComplexAlias(BlockNames.ORANGE_CARPET, ORANGE_CARPET, BlockNames.CARPET, CARPET, DyeColor.ORANGE.getWoolData(), V1_20_0);
        registerComplexAlias(BlockNames.MAGENTA_CARPET, MAGENTA_CARPET, BlockNames.CARPET, CARPET, DyeColor.MAGENTA.getWoolData(), V1_20_0);
        registerComplexAlias(BlockNames.LIGHT_BLUE_CARPET, LIGHT_BLUE_CARPET, BlockNames.CARPET, CARPET, DyeColor.LIGHT_BLUE.getWoolData(), V1_20_0);
        registerComplexAlias(BlockNames.YELLOW_CARPET, YELLOW_CARPET, BlockNames.CARPET, CARPET, DyeColor.YELLOW.getWoolData(), V1_20_0);
        registerComplexAlias(BlockNames.LIME_CARPET, LIME_CARPET, BlockNames.CARPET, CARPET, DyeColor.LIME.getWoolData(), V1_20_0);
        registerComplexAlias(BlockNames.PINK_CARPET, PINK_CARPET, BlockNames.CARPET, CARPET, DyeColor.PINK.getWoolData(), V1_20_0);
        registerComplexAlias(BlockNames.GRAY_CARPET, GRAY_CARPET, BlockNames.CARPET, CARPET, DyeColor.GRAY.getWoolData(), V1_20_0);
        registerComplexAlias(BlockNames.LIGHT_GRAY_CARPET, LIGHT_GRAY_CARPET, BlockNames.CARPET, CARPET, DyeColor.LIGHT_GRAY.getWoolData(), V1_20_0);
        registerComplexAlias(BlockNames.CYAN_CARPET, CYAN_CARPET, BlockNames.CARPET, CARPET, DyeColor.CYAN.getWoolData(), V1_20_0);
        registerComplexAlias(BlockNames.PURPLE_CARPET, PURPLE_CARPET, BlockNames.CARPET, CARPET, DyeColor.PURPLE.getWoolData(), V1_20_0);
        registerComplexAlias(BlockNames.BLUE_CARPET, BLUE_CARPET, BlockNames.CARPET, CARPET, DyeColor.BLUE.getWoolData(), V1_20_0);
        registerComplexAlias(BlockNames.BROWN_CARPET, BROWN_CARPET, BlockNames.CARPET, CARPET, DyeColor.BROWN.getWoolData(), V1_20_0);
        registerComplexAlias(BlockNames.GREEN_CARPET, GREEN_CARPET, BlockNames.CARPET, CARPET, DyeColor.GREEN.getWoolData(), V1_20_0);
        registerComplexAlias(BlockNames.RED_CARPET, RED_CARPET, BlockNames.CARPET, CARPET, DyeColor.RED.getWoolData(), V1_20_0);
        registerComplexAlias(BlockNames.BLACK_CARPET, BLACK_CARPET, BlockNames.CARPET, CARPET, DyeColor.BLACK.getWoolData(), V1_20_0);

        registerComplexAlias(BlockNames.WHITE_SHULKER_BOX, WHITE_SHULKER_BOX, BlockNames.SHULKER_BOX, SHULKER_BOX, DyeColor.WHITE.getWoolData(), V1_20_10);
        registerComplexAlias(BlockNames.ORANGE_SHULKER_BOX, ORANGE_SHULKER_BOX, BlockNames.SHULKER_BOX, SHULKER_BOX, DyeColor.ORANGE.getWoolData(), V1_20_10);
        registerComplexAlias(BlockNames.MAGENTA_SHULKER_BOX, MAGENTA_SHULKER_BOX, BlockNames.SHULKER_BOX, SHULKER_BOX, DyeColor.MAGENTA.getWoolData(), V1_20_10);
        registerComplexAlias(BlockNames.LIGHT_BLUE_SHULKER_BOX, LIGHT_BLUE_SHULKER_BOX, BlockNames.SHULKER_BOX, SHULKER_BOX, DyeColor.LIGHT_BLUE.getWoolData(), V1_20_10);
        registerComplexAlias(BlockNames.YELLOW_SHULKER_BOX, YELLOW_SHULKER_BOX, BlockNames.SHULKER_BOX, SHULKER_BOX, DyeColor.YELLOW.getWoolData(), V1_20_10);
        registerComplexAlias(BlockNames.LIME_SHULKER_BOX, LIME_SHULKER_BOX, BlockNames.SHULKER_BOX, SHULKER_BOX, DyeColor.LIME.getWoolData(), V1_20_10);
        registerComplexAlias(BlockNames.PINK_SHULKER_BOX, PINK_SHULKER_BOX, BlockNames.SHULKER_BOX, SHULKER_BOX, DyeColor.PINK.getWoolData(), V1_20_10);
        registerComplexAlias(BlockNames.GRAY_SHULKER_BOX, GRAY_SHULKER_BOX, BlockNames.SHULKER_BOX, SHULKER_BOX, DyeColor.GRAY.getWoolData(), V1_20_10);
        registerComplexAlias(BlockNames.LIGHT_GRAY_SHULKER_BOX, LIGHT_GRAY_SHULKER_BOX, BlockNames.SHULKER_BOX, SHULKER_BOX, DyeColor.LIGHT_GRAY.getWoolData(), V1_20_10);
        registerComplexAlias(BlockNames.CYAN_SHULKER_BOX, CYAN_SHULKER_BOX, BlockNames.SHULKER_BOX, SHULKER_BOX, DyeColor.CYAN.getWoolData(), V1_20_10);
        registerComplexAlias(BlockNames.PURPLE_SHULKER_BOX, PURPLE_SHULKER_BOX, BlockNames.SHULKER_BOX, SHULKER_BOX, DyeColor.PURPLE.getWoolData(), V1_20_10);
        registerComplexAlias(BlockNames.BLUE_SHULKER_BOX, BLUE_SHULKER_BOX, BlockNames.SHULKER_BOX, SHULKER_BOX, DyeColor.BLUE.getWoolData(), V1_20_10);
        registerComplexAlias(BlockNames.BROWN_SHULKER_BOX, BROWN_SHULKER_BOX, BlockNames.SHULKER_BOX, SHULKER_BOX, DyeColor.BROWN.getWoolData(), V1_20_10);
        registerComplexAlias(BlockNames.GREEN_SHULKER_BOX, GREEN_SHULKER_BOX, BlockNames.SHULKER_BOX, SHULKER_BOX, DyeColor.GREEN.getWoolData(), V1_20_10);
        registerComplexAlias(BlockNames.RED_SHULKER_BOX, RED_SHULKER_BOX, BlockNames.SHULKER_BOX, SHULKER_BOX, DyeColor.RED.getWoolData(), V1_20_10);
        registerComplexAlias(BlockNames.BLACK_SHULKER_BOX, BLACK_SHULKER_BOX, BlockNames.SHULKER_BOX, SHULKER_BOX, DyeColor.BLACK.getWoolData(), V1_20_10);

        registerComplexAlias(BlockNames.WHITE_CONCRETE, WHITE_CONCRETE, BlockNames.CONCRETE, CONCRETE, DyeColor.WHITE.getWoolData(), V1_20_10);
        registerComplexAlias(BlockNames.ORANGE_CONCRETE, ORANGE_CONCRETE, BlockNames.CONCRETE, CONCRETE, DyeColor.ORANGE.getWoolData(), V1_20_10);
        registerComplexAlias(BlockNames.MAGENTA_CONCRETE, MAGENTA_CONCRETE, BlockNames.CONCRETE, CONCRETE, DyeColor.MAGENTA.getWoolData(), V1_20_10);
        registerComplexAlias(BlockNames.LIGHT_BLUE_CONCRETE, LIGHT_BLUE_CONCRETE, BlockNames.CONCRETE, CONCRETE, DyeColor.LIGHT_BLUE.getWoolData(), V1_20_10);
        registerComplexAlias(BlockNames.YELLOW_CONCRETE, YELLOW_CONCRETE, BlockNames.CONCRETE, CONCRETE, DyeColor.YELLOW.getWoolData(), V1_20_10);
        registerComplexAlias(BlockNames.LIME_CONCRETE, LIME_CONCRETE, BlockNames.CONCRETE, CONCRETE, DyeColor.LIME.getWoolData(), V1_20_10);
        registerComplexAlias(BlockNames.PINK_CONCRETE, PINK_CONCRETE, BlockNames.CONCRETE, CONCRETE, DyeColor.PINK.getWoolData(), V1_20_10);
        registerComplexAlias(BlockNames.GRAY_CONCRETE, GRAY_CONCRETE, BlockNames.CONCRETE, CONCRETE, DyeColor.GRAY.getWoolData(), V1_20_10);
        registerComplexAlias(BlockNames.LIGHT_GRAY_CONCRETE, LIGHT_GRAY_CONCRETE, BlockNames.CONCRETE, CONCRETE, DyeColor.LIGHT_GRAY.getWoolData(), V1_20_10);
        registerComplexAlias(BlockNames.CYAN_CONCRETE, CYAN_CONCRETE, BlockNames.CONCRETE, CONCRETE, DyeColor.CYAN.getWoolData(), V1_20_10);
        registerComplexAlias(BlockNames.PURPLE_CONCRETE, PURPLE_CONCRETE, BlockNames.CONCRETE, CONCRETE, DyeColor.PURPLE.getWoolData(), V1_20_10);
        registerComplexAlias(BlockNames.BLUE_CONCRETE, BLUE_CONCRETE, BlockNames.CONCRETE, CONCRETE, DyeColor.BLUE.getWoolData(), V1_20_10);
        registerComplexAlias(BlockNames.BROWN_CONCRETE, BROWN_CONCRETE, BlockNames.CONCRETE, CONCRETE, DyeColor.BROWN.getWoolData(), V1_20_10);
        registerComplexAlias(BlockNames.GREEN_CONCRETE, GREEN_CONCRETE, BlockNames.CONCRETE, CONCRETE, DyeColor.GREEN.getWoolData(), V1_20_10);
        registerComplexAlias(BlockNames.RED_CONCRETE, RED_CONCRETE, BlockNames.CONCRETE, CONCRETE, DyeColor.RED.getWoolData(), V1_20_10);
        registerComplexAlias(BlockNames.BLACK_CONCRETE, BLACK_CONCRETE, BlockNames.CONCRETE, CONCRETE, DyeColor.BLACK.getWoolData(), V1_20_10);

        registerComplexAlias(BlockNames.WHITE_STAINED_GLASS, WHITE_STAINED_GLASS, BlockNames.STAINED_GLASS, STAINED_GLASS, DyeColor.WHITE.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.ORANGE_STAINED_GLASS, ORANGE_STAINED_GLASS, BlockNames.STAINED_GLASS, STAINED_GLASS, DyeColor.ORANGE.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.MAGENTA_STAINED_GLASS, MAGENTA_STAINED_GLASS, BlockNames.STAINED_GLASS, STAINED_GLASS, DyeColor.MAGENTA.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.LIGHT_BLUE_STAINED_GLASS, LIGHT_BLUE_STAINED_GLASS, BlockNames.STAINED_GLASS, STAINED_GLASS, DyeColor.LIGHT_BLUE.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.YELLOW_STAINED_GLASS, YELLOW_STAINED_GLASS, BlockNames.STAINED_GLASS, STAINED_GLASS, DyeColor.YELLOW.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.LIME_STAINED_GLASS, LIME_STAINED_GLASS, BlockNames.STAINED_GLASS, STAINED_GLASS, DyeColor.LIME.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.PINK_STAINED_GLASS, PINK_STAINED_GLASS, BlockNames.STAINED_GLASS, STAINED_GLASS, DyeColor.PINK.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.GRAY_STAINED_GLASS, GRAY_STAINED_GLASS, BlockNames.STAINED_GLASS, STAINED_GLASS, DyeColor.GRAY.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.LIGHT_GRAY_STAINED_GLASS, LIGHT_GRAY_STAINED_GLASS, BlockNames.STAINED_GLASS, STAINED_GLASS, DyeColor.LIGHT_GRAY.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.CYAN_STAINED_GLASS, CYAN_STAINED_GLASS, BlockNames.STAINED_GLASS, STAINED_GLASS, DyeColor.CYAN.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.PURPLE_STAINED_GLASS, PURPLE_STAINED_GLASS, BlockNames.STAINED_GLASS, STAINED_GLASS, DyeColor.PURPLE.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.BLUE_STAINED_GLASS, BLUE_STAINED_GLASS, BlockNames.STAINED_GLASS, STAINED_GLASS, DyeColor.BLUE.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.BROWN_STAINED_GLASS, BROWN_STAINED_GLASS, BlockNames.STAINED_GLASS, STAINED_GLASS, DyeColor.BROWN.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.GREEN_STAINED_GLASS, GREEN_STAINED_GLASS, BlockNames.STAINED_GLASS, STAINED_GLASS, DyeColor.GREEN.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.RED_STAINED_GLASS, RED_STAINED_GLASS, BlockNames.STAINED_GLASS, STAINED_GLASS, DyeColor.RED.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.BLACK_STAINED_GLASS, BLACK_STAINED_GLASS, BlockNames.STAINED_GLASS, STAINED_GLASS, DyeColor.BLACK.getWoolData(), V1_20_30);

        registerComplexAlias(BlockNames.WHITE_STAINED_GLASS_PANE, WHITE_STAINED_GLASS_PANE, BlockNames.STAINED_GLASS_PANE, STAINED_GLASS_PANE, DyeColor.WHITE.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.ORANGE_STAINED_GLASS_PANE, ORANGE_STAINED_GLASS_PANE, BlockNames.STAINED_GLASS_PANE, STAINED_GLASS_PANE, DyeColor.ORANGE.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.MAGENTA_STAINED_GLASS_PANE, MAGENTA_STAINED_GLASS_PANE, BlockNames.STAINED_GLASS_PANE, STAINED_GLASS_PANE, DyeColor.MAGENTA.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.LIGHT_BLUE_STAINED_GLASS_PANE, LIGHT_BLUE_STAINED_GLASS_PANE, BlockNames.STAINED_GLASS_PANE, STAINED_GLASS_PANE, DyeColor.LIGHT_BLUE.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.YELLOW_STAINED_GLASS_PANE, YELLOW_STAINED_GLASS_PANE, BlockNames.STAINED_GLASS_PANE, STAINED_GLASS_PANE, DyeColor.YELLOW.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.LIME_STAINED_GLASS_PANE, LIME_STAINED_GLASS_PANE, BlockNames.STAINED_GLASS_PANE, STAINED_GLASS_PANE, DyeColor.LIME.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.PINK_STAINED_GLASS_PANE, PINK_STAINED_GLASS_PANE, BlockNames.STAINED_GLASS_PANE, STAINED_GLASS_PANE, DyeColor.PINK.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.GRAY_STAINED_GLASS_PANE, GRAY_STAINED_GLASS_PANE, BlockNames.STAINED_GLASS_PANE, STAINED_GLASS_PANE, DyeColor.GRAY.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.LIGHT_GRAY_STAINED_GLASS_PANE, LIGHT_GRAY_STAINED_GLASS_PANE, BlockNames.STAINED_GLASS_PANE, STAINED_GLASS_PANE, DyeColor.LIGHT_GRAY.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.CYAN_STAINED_GLASS_PANE, CYAN_STAINED_GLASS_PANE, BlockNames.STAINED_GLASS_PANE, STAINED_GLASS_PANE, DyeColor.CYAN.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.PURPLE_STAINED_GLASS_PANE, PURPLE_STAINED_GLASS_PANE, BlockNames.STAINED_GLASS_PANE, STAINED_GLASS_PANE, DyeColor.PURPLE.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.BLUE_STAINED_GLASS_PANE, BLUE_STAINED_GLASS_PANE, BlockNames.STAINED_GLASS_PANE, STAINED_GLASS_PANE, DyeColor.BLUE.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.BROWN_STAINED_GLASS_PANE, BROWN_STAINED_GLASS_PANE, BlockNames.STAINED_GLASS_PANE, STAINED_GLASS_PANE, DyeColor.BROWN.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.GREEN_STAINED_GLASS_PANE, GREEN_STAINED_GLASS_PANE, BlockNames.STAINED_GLASS_PANE, STAINED_GLASS_PANE, DyeColor.GREEN.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.RED_STAINED_GLASS_PANE, RED_STAINED_GLASS_PANE, BlockNames.STAINED_GLASS_PANE, STAINED_GLASS_PANE, DyeColor.RED.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.BLACK_STAINED_GLASS_PANE, BLACK_STAINED_GLASS_PANE, BlockNames.STAINED_GLASS_PANE, STAINED_GLASS_PANE, DyeColor.BLACK.getWoolData(), V1_20_30);

        registerComplexAlias(BlockNames.WHITE_CONCRETE_POWDER, WHITE_CONCRETE_POWDER, BlockNames.CONCRETE_POWDER, CONCRETE_POWDER, DyeColor.WHITE.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.ORANGE_CONCRETE_POWDER, ORANGE_CONCRETE_POWDER, BlockNames.CONCRETE_POWDER, CONCRETE_POWDER, DyeColor.ORANGE.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.MAGENTA_CONCRETE_POWDER, MAGENTA_CONCRETE_POWDER, BlockNames.CONCRETE_POWDER, CONCRETE_POWDER, DyeColor.MAGENTA.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.LIGHT_BLUE_CONCRETE_POWDER, LIGHT_BLUE_CONCRETE_POWDER, BlockNames.CONCRETE_POWDER, CONCRETE_POWDER, DyeColor.LIGHT_BLUE.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.YELLOW_CONCRETE_POWDER, YELLOW_CONCRETE_POWDER, BlockNames.CONCRETE_POWDER, CONCRETE_POWDER, DyeColor.YELLOW.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.LIME_CONCRETE_POWDER, LIME_CONCRETE_POWDER, BlockNames.CONCRETE_POWDER, CONCRETE_POWDER, DyeColor.LIME.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.PINK_CONCRETE_POWDER, PINK_CONCRETE_POWDER, BlockNames.CONCRETE_POWDER, CONCRETE_POWDER, DyeColor.PINK.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.GRAY_CONCRETE_POWDER, GRAY_CONCRETE_POWDER, BlockNames.CONCRETE_POWDER, CONCRETE_POWDER, DyeColor.GRAY.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.LIGHT_GRAY_CONCRETE_POWDER, LIGHT_GRAY_CONCRETE_POWDER, BlockNames.CONCRETE_POWDER, CONCRETE_POWDER, DyeColor.LIGHT_GRAY.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.CYAN_CONCRETE_POWDER, CYAN_CONCRETE_POWDER, BlockNames.CONCRETE_POWDER, CONCRETE_POWDER, DyeColor.CYAN.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.PURPLE_CONCRETE_POWDER, PURPLE_CONCRETE_POWDER, BlockNames.CONCRETE_POWDER, CONCRETE_POWDER, DyeColor.PURPLE.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.BLUE_CONCRETE_POWDER, BLUE_CONCRETE_POWDER, BlockNames.CONCRETE_POWDER, CONCRETE_POWDER, DyeColor.BLUE.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.BROWN_CONCRETE_POWDER, BROWN_CONCRETE_POWDER, BlockNames.CONCRETE_POWDER, CONCRETE_POWDER, DyeColor.BROWN.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.GREEN_CONCRETE_POWDER, GREEN_CONCRETE_POWDER, BlockNames.CONCRETE_POWDER, CONCRETE_POWDER, DyeColor.GREEN.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.RED_CONCRETE_POWDER, RED_CONCRETE_POWDER, BlockNames.CONCRETE_POWDER, CONCRETE_POWDER, DyeColor.RED.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.BLACK_CONCRETE_POWDER, BLACK_CONCRETE_POWDER, BlockNames.CONCRETE_POWDER, CONCRETE_POWDER, DyeColor.BLACK.getWoolData(), V1_20_30);

        registerComplexAlias(BlockNames.WHITE_TERRACOTTA, WHITE_TERRACOTTA, BlockNames.STAINED_HARDENED_CLAY, STAINED_HARDENED_CLAY, DyeColor.WHITE.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.ORANGE_TERRACOTTA, ORANGE_TERRACOTTA, BlockNames.STAINED_HARDENED_CLAY, STAINED_HARDENED_CLAY, DyeColor.ORANGE.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.MAGENTA_TERRACOTTA, MAGENTA_TERRACOTTA, BlockNames.STAINED_HARDENED_CLAY, STAINED_HARDENED_CLAY, DyeColor.MAGENTA.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.LIGHT_BLUE_TERRACOTTA, LIGHT_BLUE_TERRACOTTA, BlockNames.STAINED_HARDENED_CLAY, STAINED_HARDENED_CLAY, DyeColor.LIGHT_BLUE.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.YELLOW_TERRACOTTA, YELLOW_TERRACOTTA, BlockNames.STAINED_HARDENED_CLAY, STAINED_HARDENED_CLAY, DyeColor.YELLOW.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.LIME_TERRACOTTA, LIME_TERRACOTTA, BlockNames.STAINED_HARDENED_CLAY, STAINED_HARDENED_CLAY, DyeColor.LIME.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.PINK_TERRACOTTA, PINK_TERRACOTTA, BlockNames.STAINED_HARDENED_CLAY, STAINED_HARDENED_CLAY, DyeColor.PINK.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.GRAY_TERRACOTTA, GRAY_TERRACOTTA, BlockNames.STAINED_HARDENED_CLAY, STAINED_HARDENED_CLAY, DyeColor.GRAY.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.LIGHT_GRAY_TERRACOTTA, LIGHT_GRAY_TERRACOTTA, BlockNames.STAINED_HARDENED_CLAY, STAINED_HARDENED_CLAY, DyeColor.LIGHT_GRAY.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.CYAN_TERRACOTTA, CYAN_TERRACOTTA, BlockNames.STAINED_HARDENED_CLAY, STAINED_HARDENED_CLAY, DyeColor.CYAN.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.PURPLE_TERRACOTTA, PURPLE_TERRACOTTA, BlockNames.STAINED_HARDENED_CLAY, STAINED_HARDENED_CLAY, DyeColor.PURPLE.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.BLUE_TERRACOTTA, BLUE_TERRACOTTA, BlockNames.STAINED_HARDENED_CLAY, STAINED_HARDENED_CLAY, DyeColor.BLUE.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.BROWN_TERRACOTTA, BROWN_TERRACOTTA, BlockNames.STAINED_HARDENED_CLAY, STAINED_HARDENED_CLAY, DyeColor.BROWN.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.GREEN_TERRACOTTA, GREEN_TERRACOTTA, BlockNames.STAINED_HARDENED_CLAY, STAINED_HARDENED_CLAY, DyeColor.GREEN.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.RED_TERRACOTTA, RED_TERRACOTTA, BlockNames.STAINED_HARDENED_CLAY, STAINED_HARDENED_CLAY, DyeColor.RED.getWoolData(), V1_20_30);
        registerComplexAlias(BlockNames.BLACK_TERRACOTTA, BLACK_TERRACOTTA, BlockNames.STAINED_HARDENED_CLAY, STAINED_HARDENED_CLAY, DyeColor.BLACK.getWoolData(), V1_20_30);

        registerComplexAlias(BlockNames.GRANITE, GRANITE, BlockNames.STONE, STONE, BlockStone.TYPE_GRANITE, V1_20_50);
        registerComplexAlias(BlockNames.POLISHED_GRANITE, POLISHED_GRANITE, BlockNames.STONE, STONE, BlockStone.TYPE_POLISHED_GRANITE, V1_20_50);
        registerComplexAlias(BlockNames.DIORITE, DIORITE, BlockNames.STONE, STONE, BlockStone.TYPE_DIORITE, V1_20_50);
        registerComplexAlias(BlockNames.POLISHED_DIORITE, POLISHED_DIORITE, BlockNames.STONE, STONE, BlockStone.TYPE_POLISHED_DIORITE, V1_20_50);
        registerComplexAlias(BlockNames.ANDESITE, ANDESITE, BlockNames.STONE, STONE, BlockStone.TYPE_ANDESITE, V1_20_50);
        registerComplexAlias(BlockNames.POLISHED_ANDESITE, POLISHED_ANDESITE, BlockNames.STONE, STONE, BlockStone.TYPE_POLISHED_ANDESITE, V1_20_50);

        registerComplexAlias(BlockNames.OAK_PLANKS, OAK_PLANKS, BlockNames.PLANKS, PLANKS, BlockPlanks.OAK, V1_20_50);
        registerComplexAlias(BlockNames.SPRUCE_PLANKS, SPRUCE_PLANKS, BlockNames.PLANKS, PLANKS, BlockPlanks.SPRUCE, V1_20_50);
        registerComplexAlias(BlockNames.BIRCH_PLANKS, BIRCH_PLANKS, BlockNames.PLANKS, PLANKS, BlockPlanks.BIRCH, V1_20_50);
        registerComplexAlias(BlockNames.JUNGLE_PLANKS, JUNGLE_PLANKS, BlockNames.PLANKS, PLANKS, BlockPlanks.JUNGLE, V1_20_50);
        registerComplexAlias(BlockNames.ACACIA_PLANKS, ACACIA_PLANKS, BlockNames.PLANKS, PLANKS, BlockPlanks.ACACIA, V1_20_50);
        registerComplexAlias(BlockNames.DARK_OAK_PLANKS, DARK_OAK_PLANKS, BlockNames.PLANKS, PLANKS, BlockPlanks.DARK_OAK, V1_20_50);

        registerComplexAlias(BlockNames.HARD_WHITE_STAINED_GLASS, HARD_WHITE_STAINED_GLASS, BlockNames.HARD_STAINED_GLASS, HARD_STAINED_GLASS, DyeColor.WHITE.getWoolData(), V1_20_60);
        registerComplexAlias(BlockNames.HARD_ORANGE_STAINED_GLASS, HARD_ORANGE_STAINED_GLASS, BlockNames.HARD_STAINED_GLASS, HARD_STAINED_GLASS, DyeColor.ORANGE.getWoolData(), V1_20_60);
        registerComplexAlias(BlockNames.HARD_MAGENTA_STAINED_GLASS, HARD_MAGENTA_STAINED_GLASS, BlockNames.HARD_STAINED_GLASS, HARD_STAINED_GLASS, DyeColor.MAGENTA.getWoolData(), V1_20_60);
        registerComplexAlias(BlockNames.HARD_LIGHT_BLUE_STAINED_GLASS, HARD_LIGHT_BLUE_STAINED_GLASS, BlockNames.HARD_STAINED_GLASS, HARD_STAINED_GLASS, DyeColor.LIGHT_BLUE.getWoolData(), V1_20_60);
        registerComplexAlias(BlockNames.HARD_YELLOW_STAINED_GLASS, HARD_YELLOW_STAINED_GLASS, BlockNames.HARD_STAINED_GLASS, HARD_STAINED_GLASS, DyeColor.YELLOW.getWoolData(), V1_20_60);
        registerComplexAlias(BlockNames.HARD_LIME_STAINED_GLASS, HARD_LIME_STAINED_GLASS, BlockNames.HARD_STAINED_GLASS, HARD_STAINED_GLASS, DyeColor.LIME.getWoolData(), V1_20_60);
        registerComplexAlias(BlockNames.HARD_PINK_STAINED_GLASS, HARD_PINK_STAINED_GLASS, BlockNames.HARD_STAINED_GLASS, HARD_STAINED_GLASS, DyeColor.PINK.getWoolData(), V1_20_60);
        registerComplexAlias(BlockNames.HARD_GRAY_STAINED_GLASS, HARD_GRAY_STAINED_GLASS, BlockNames.HARD_STAINED_GLASS, HARD_STAINED_GLASS, DyeColor.GRAY.getWoolData(), V1_20_60);
        registerComplexAlias(BlockNames.HARD_LIGHT_GRAY_STAINED_GLASS, HARD_LIGHT_GRAY_STAINED_GLASS, BlockNames.HARD_STAINED_GLASS, HARD_STAINED_GLASS, DyeColor.LIGHT_GRAY.getWoolData(), V1_20_60);
        registerComplexAlias(BlockNames.HARD_CYAN_STAINED_GLASS, HARD_CYAN_STAINED_GLASS, BlockNames.HARD_STAINED_GLASS, HARD_STAINED_GLASS, DyeColor.CYAN.getWoolData(), V1_20_60);
        registerComplexAlias(BlockNames.HARD_PURPLE_STAINED_GLASS, HARD_PURPLE_STAINED_GLASS, BlockNames.HARD_STAINED_GLASS, HARD_STAINED_GLASS, DyeColor.PURPLE.getWoolData(), V1_20_60);
        registerComplexAlias(BlockNames.HARD_BLUE_STAINED_GLASS, HARD_BLUE_STAINED_GLASS, BlockNames.HARD_STAINED_GLASS, HARD_STAINED_GLASS, DyeColor.BLUE.getWoolData(), V1_20_60);
        registerComplexAlias(BlockNames.HARD_BROWN_STAINED_GLASS, HARD_BROWN_STAINED_GLASS, BlockNames.HARD_STAINED_GLASS, HARD_STAINED_GLASS, DyeColor.BROWN.getWoolData(), V1_20_60);
        registerComplexAlias(BlockNames.HARD_GREEN_STAINED_GLASS, HARD_GREEN_STAINED_GLASS, BlockNames.HARD_STAINED_GLASS, HARD_STAINED_GLASS, DyeColor.GREEN.getWoolData(), V1_20_60);
        registerComplexAlias(BlockNames.HARD_RED_STAINED_GLASS, HARD_RED_STAINED_GLASS, BlockNames.HARD_STAINED_GLASS, HARD_STAINED_GLASS, DyeColor.RED.getWoolData(), V1_20_60);
        registerComplexAlias(BlockNames.HARD_BLACK_STAINED_GLASS, HARD_BLACK_STAINED_GLASS, BlockNames.HARD_STAINED_GLASS, HARD_STAINED_GLASS, DyeColor.BLACK.getWoolData(), V1_20_60);

        registerComplexAlias(BlockNames.HARD_WHITE_STAINED_GLASS_PANE, HARD_WHITE_STAINED_GLASS_PANE, BlockNames.HARD_STAINED_GLASS_PANE, HARD_STAINED_GLASS_PANE, DyeColor.WHITE.getWoolData(), V1_20_60);
        registerComplexAlias(BlockNames.HARD_ORANGE_STAINED_GLASS_PANE, HARD_ORANGE_STAINED_GLASS_PANE, BlockNames.HARD_STAINED_GLASS_PANE, HARD_STAINED_GLASS_PANE, DyeColor.ORANGE.getWoolData(), V1_20_60);
        registerComplexAlias(BlockNames.HARD_MAGENTA_STAINED_GLASS_PANE, HARD_MAGENTA_STAINED_GLASS_PANE, BlockNames.HARD_STAINED_GLASS_PANE, HARD_STAINED_GLASS_PANE, DyeColor.MAGENTA.getWoolData(), V1_20_60);
        registerComplexAlias(BlockNames.HARD_LIGHT_BLUE_STAINED_GLASS_PANE, HARD_LIGHT_BLUE_STAINED_GLASS_PANE, BlockNames.HARD_STAINED_GLASS_PANE, HARD_STAINED_GLASS_PANE, DyeColor.LIGHT_BLUE.getWoolData(), V1_20_60);
        registerComplexAlias(BlockNames.HARD_YELLOW_STAINED_GLASS_PANE, HARD_YELLOW_STAINED_GLASS_PANE, BlockNames.HARD_STAINED_GLASS_PANE, HARD_STAINED_GLASS_PANE, DyeColor.YELLOW.getWoolData(), V1_20_60);
        registerComplexAlias(BlockNames.HARD_LIME_STAINED_GLASS_PANE, HARD_LIME_STAINED_GLASS_PANE, BlockNames.HARD_STAINED_GLASS_PANE, HARD_STAINED_GLASS_PANE, DyeColor.LIME.getWoolData(), V1_20_60);
        registerComplexAlias(BlockNames.HARD_PINK_STAINED_GLASS_PANE, HARD_PINK_STAINED_GLASS_PANE, BlockNames.HARD_STAINED_GLASS_PANE, HARD_STAINED_GLASS_PANE, DyeColor.PINK.getWoolData(), V1_20_60);
        registerComplexAlias(BlockNames.HARD_GRAY_STAINED_GLASS_PANE, HARD_GRAY_STAINED_GLASS_PANE, BlockNames.HARD_STAINED_GLASS_PANE, HARD_STAINED_GLASS_PANE, DyeColor.GRAY.getWoolData(), V1_20_60);
        registerComplexAlias(BlockNames.HARD_LIGHT_GRAY_STAINED_GLASS_PANE, HARD_LIGHT_GRAY_STAINED_GLASS_PANE, BlockNames.HARD_STAINED_GLASS_PANE, HARD_STAINED_GLASS_PANE, DyeColor.LIGHT_GRAY.getWoolData(), V1_20_60);
        registerComplexAlias(BlockNames.HARD_CYAN_STAINED_GLASS_PANE, HARD_CYAN_STAINED_GLASS_PANE, BlockNames.HARD_STAINED_GLASS_PANE, HARD_STAINED_GLASS_PANE, DyeColor.CYAN.getWoolData(), V1_20_60);
        registerComplexAlias(BlockNames.HARD_PURPLE_STAINED_GLASS_PANE, HARD_PURPLE_STAINED_GLASS_PANE, BlockNames.HARD_STAINED_GLASS_PANE, HARD_STAINED_GLASS_PANE, DyeColor.PURPLE.getWoolData(), V1_20_60);
        registerComplexAlias(BlockNames.HARD_BLUE_STAINED_GLASS_PANE, HARD_BLUE_STAINED_GLASS_PANE, BlockNames.HARD_STAINED_GLASS_PANE, HARD_STAINED_GLASS_PANE, DyeColor.BLUE.getWoolData(), V1_20_60);
        registerComplexAlias(BlockNames.HARD_BROWN_STAINED_GLASS_PANE, HARD_BROWN_STAINED_GLASS_PANE, BlockNames.HARD_STAINED_GLASS_PANE, HARD_STAINED_GLASS_PANE, DyeColor.BROWN.getWoolData(), V1_20_60);
        registerComplexAlias(BlockNames.HARD_GREEN_STAINED_GLASS_PANE, HARD_GREEN_STAINED_GLASS_PANE, BlockNames.HARD_STAINED_GLASS_PANE, HARD_STAINED_GLASS_PANE, DyeColor.GREEN.getWoolData(), V1_20_60);
        registerComplexAlias(BlockNames.HARD_RED_STAINED_GLASS_PANE, HARD_RED_STAINED_GLASS_PANE, BlockNames.HARD_STAINED_GLASS_PANE, HARD_STAINED_GLASS_PANE, DyeColor.RED.getWoolData(), V1_20_60);
        registerComplexAlias(BlockNames.HARD_BLACK_STAINED_GLASS_PANE, HARD_BLACK_STAINED_GLASS_PANE, BlockNames.HARD_STAINED_GLASS_PANE, HARD_STAINED_GLASS_PANE, DyeColor.BLACK.getWoolData(), V1_20_60);

        registerComplexAlias(BlockNames.OAK_LEAVES, OAK_LEAVES, BlockNames.LEAVES, LEAVES, BlockLeaves.OAK, V1_20_70);
        registerComplexAlias(BlockNames.SPRUCE_LEAVES, SPRUCE_LEAVES, BlockNames.LEAVES, LEAVES, BlockLeaves.SPRUCE, V1_20_70);
        registerComplexAlias(BlockNames.BIRCH_LEAVES, BIRCH_LEAVES, BlockNames.LEAVES, LEAVES, BlockLeaves.BIRCH, V1_20_70);
        registerComplexAlias(BlockNames.JUNGLE_LEAVES, JUNGLE_LEAVES, BlockNames.LEAVES, LEAVES, BlockLeaves.JUNGLE, V1_20_70);

        registerComplexAlias(BlockNames.ACACIA_LEAVES, ACACIA_LEAVES, BlockNames.LEAVES2, LEAVES2, BlockLeaves2.ACACIA, V1_20_70);
        registerComplexAlias(BlockNames.DARK_OAK_LEAVES, DARK_OAK_LEAVES, BlockNames.LEAVES2, LEAVES2, BlockLeaves2.DARK_OAK, V1_20_70);

        registerComplexAlias(BlockNames.OAK_SLAB, OAK_SLAB, BlockNames.WOODEN_SLAB, WOODEN_SLAB, BlockSlabWood.OAK, V1_20_70);
        registerComplexAlias(BlockNames.SPRUCE_SLAB, SPRUCE_SLAB, BlockNames.WOODEN_SLAB, WOODEN_SLAB, BlockSlabWood.SPRUCE, V1_20_70);
        registerComplexAlias(BlockNames.BIRCH_SLAB, BIRCH_SLAB, BlockNames.WOODEN_SLAB, WOODEN_SLAB, BlockSlabWood.BIRCH, V1_20_70);
        registerComplexAlias(BlockNames.JUNGLE_SLAB, JUNGLE_SLAB, BlockNames.WOODEN_SLAB, WOODEN_SLAB, BlockSlabWood.JUNGLE, V1_20_70);
        registerComplexAlias(BlockNames.ACACIA_SLAB, ACACIA_SLAB, BlockNames.WOODEN_SLAB, WOODEN_SLAB, BlockSlabWood.ACACIA, V1_20_70);
        registerComplexAlias(BlockNames.DARK_OAK_SLAB, DARK_OAK_SLAB, BlockNames.WOODEN_SLAB, WOODEN_SLAB, BlockSlabWood.DARK_OAK, V1_20_70);

        registerComplexAlias(BlockNames.OAK_DOUBLE_SLAB, OAK_DOUBLE_SLAB, BlockNames.DOUBLE_WOODEN_SLAB, DOUBLE_WOODEN_SLAB, BlockDoubleSlabWood.OAK, V1_20_70);
        registerComplexAlias(BlockNames.SPRUCE_DOUBLE_SLAB, SPRUCE_DOUBLE_SLAB, BlockNames.DOUBLE_WOODEN_SLAB, DOUBLE_WOODEN_SLAB, BlockDoubleSlabWood.SPRUCE, V1_20_70);
        registerComplexAlias(BlockNames.BIRCH_DOUBLE_SLAB, BIRCH_DOUBLE_SLAB, BlockNames.DOUBLE_WOODEN_SLAB, DOUBLE_WOODEN_SLAB, BlockDoubleSlabWood.BIRCH, V1_20_70);
        registerComplexAlias(BlockNames.JUNGLE_DOUBLE_SLAB, JUNGLE_DOUBLE_SLAB, BlockNames.DOUBLE_WOODEN_SLAB, DOUBLE_WOODEN_SLAB, BlockDoubleSlabWood.JUNGLE, V1_20_70);
        registerComplexAlias(BlockNames.ACACIA_DOUBLE_SLAB, ACACIA_DOUBLE_SLAB, BlockNames.DOUBLE_WOODEN_SLAB, DOUBLE_WOODEN_SLAB, BlockDoubleSlabWood.ACACIA, V1_20_70);
        registerComplexAlias(BlockNames.DARK_OAK_DOUBLE_SLAB, DARK_OAK_DOUBLE_SLAB, BlockNames.DOUBLE_WOODEN_SLAB, DOUBLE_WOODEN_SLAB, BlockDoubleSlabWood.DARK_OAK, V1_20_70);

        registerComplexAlias(BlockNames.OAK_WOOD, OAK_WOOD, BlockNames.WOOD, WOOD, BlockWoodBark.OAK, V1_20_70);
        registerComplexAlias(BlockNames.SPRUCE_WOOD, SPRUCE_WOOD, BlockNames.WOOD, WOOD, BlockWoodBark.SPRUCE, V1_20_70);
        registerComplexAlias(BlockNames.BIRCH_WOOD, BIRCH_WOOD, BlockNames.WOOD, WOOD, BlockWoodBark.BIRCH, V1_20_70);
        registerComplexAlias(BlockNames.JUNGLE_WOOD, JUNGLE_WOOD, BlockNames.WOOD, WOOD, BlockWoodBark.JUNGLE, V1_20_70);
        registerComplexAlias(BlockNames.ACACIA_WOOD, ACACIA_WOOD, BlockNames.WOOD, WOOD, BlockWoodBark.ACACIA, V1_20_70);
        registerComplexAlias(BlockNames.DARK_OAK_WOOD, DARK_OAK_WOOD, BlockNames.WOOD, WOOD, BlockWoodBark.DARK_OAK, V1_20_70);

        registerComplexAlias(BlockNames.STRIPPED_OAK_WOOD, STRIPPED_OAK_WOOD, BlockNames.WOOD, WOOD, BlockWoodBark.STRIPPED_BIT | BlockWoodBark.OAK, V1_20_70);
        registerComplexAlias(BlockNames.STRIPPED_SPRUCE_WOOD, STRIPPED_SPRUCE_WOOD, BlockNames.WOOD, WOOD, BlockWoodBark.STRIPPED_BIT | BlockWoodBark.SPRUCE, V1_20_70);
        registerComplexAlias(BlockNames.STRIPPED_BIRCH_WOOD, STRIPPED_BIRCH_WOOD, BlockNames.WOOD, WOOD, BlockWoodBark.STRIPPED_BIT | BlockWoodBark.BIRCH, V1_20_70);
        registerComplexAlias(BlockNames.STRIPPED_JUNGLE_WOOD, STRIPPED_JUNGLE_WOOD, BlockNames.WOOD, WOOD, BlockWoodBark.STRIPPED_BIT | BlockWoodBark.JUNGLE, V1_20_70);
        registerComplexAlias(BlockNames.STRIPPED_ACACIA_WOOD, STRIPPED_ACACIA_WOOD, BlockNames.WOOD, WOOD, BlockWoodBark.STRIPPED_BIT | BlockWoodBark.ACACIA, V1_20_70);
        registerComplexAlias(BlockNames.STRIPPED_DARK_OAK_WOOD, STRIPPED_DARK_OAK_WOOD, BlockNames.WOOD, WOOD, BlockWoodBark.STRIPPED_BIT | BlockWoodBark.DARK_OAK, V1_20_70);

        registerComplexAlias(BlockNames.OAK_SAPLING, OAK_SAPLING, BlockNames.SAPLING, SAPLING, BlockSapling.OAK, V1_20_80);
        registerComplexAlias(BlockNames.SPRUCE_SAPLING, SPRUCE_SAPLING, BlockNames.SAPLING, SAPLING, BlockSapling.SPRUCE, V1_20_80);
        registerComplexAlias(BlockNames.BIRCH_SAPLING, BIRCH_SAPLING, BlockNames.SAPLING, SAPLING, BlockSapling.BIRCH, V1_20_80);
        registerComplexAlias(BlockNames.JUNGLE_SAPLING, JUNGLE_SAPLING, BlockNames.SAPLING, SAPLING, BlockSapling.JUNGLE, V1_20_80);
        registerComplexAlias(BlockNames.ACACIA_SAPLING, ACACIA_SAPLING, BlockNames.SAPLING, SAPLING, BlockSapling.ACACIA, V1_20_80);
        registerComplexAlias(BlockNames.DARK_OAK_SAPLING, DARK_OAK_SAPLING, BlockNames.SAPLING, SAPLING, BlockSapling.DARK_OAK, V1_20_80);

        registerComplexAlias(BlockNames.TUBE_CORAL_FAN, TUBE_CORAL_FAN, BlockNames.CORAL_FAN, CORAL_FAN, BlockCoralFan.BLUE, V1_20_80);
        registerComplexAlias(BlockNames.BRAIN_CORAL_FAN, BRAIN_CORAL_FAN, BlockNames.CORAL_FAN, CORAL_FAN, BlockCoralFan.PINK, V1_20_80);
        registerComplexAlias(BlockNames.BUBBLE_CORAL_FAN, BUBBLE_CORAL_FAN, BlockNames.CORAL_FAN, CORAL_FAN, BlockCoralFan.PURPLE, V1_20_80);
        registerComplexAlias(BlockNames.FIRE_CORAL_FAN, FIRE_CORAL_FAN, BlockNames.CORAL_FAN, CORAL_FAN, BlockCoralFan.RED, V1_20_80);
        registerComplexAlias(BlockNames.HORN_CORAL_FAN, HORN_CORAL_FAN, BlockNames.CORAL_FAN, CORAL_FAN, BlockCoralFan.YELLOW, V1_20_80);

        registerComplexAlias(BlockNames.DEAD_TUBE_CORAL_FAN, DEAD_TUBE_CORAL_FAN, BlockNames.CORAL_FAN_DEAD, CORAL_FAN_DEAD, BlockCoralFanDead.BLUE, V1_20_80);
        registerComplexAlias(BlockNames.DEAD_BRAIN_CORAL_FAN, DEAD_BRAIN_CORAL_FAN, BlockNames.CORAL_FAN_DEAD, CORAL_FAN_DEAD, BlockCoralFanDead.PINK, V1_20_80);
        registerComplexAlias(BlockNames.DEAD_BUBBLE_CORAL_FAN, DEAD_BUBBLE_CORAL_FAN, BlockNames.CORAL_FAN_DEAD, CORAL_FAN_DEAD, BlockCoralFanDead.PURPLE, V1_20_80);
        registerComplexAlias(BlockNames.DEAD_FIRE_CORAL_FAN, DEAD_FIRE_CORAL_FAN, BlockNames.CORAL_FAN_DEAD, CORAL_FAN_DEAD, BlockCoralFanDead.RED, V1_20_80);
        registerComplexAlias(BlockNames.DEAD_HORN_CORAL_FAN, DEAD_HORN_CORAL_FAN, BlockNames.CORAL_FAN_DEAD, CORAL_FAN_DEAD, BlockCoralFanDead.YELLOW, V1_20_80);

        registerComplexAlias(BlockNames.POPPY, POPPY, BlockNames.RED_FLOWER, RED_FLOWER, BlockFlower.TYPE_POPPY, V1_20_80);
        registerComplexAlias(BlockNames.BLUE_ORCHID, BLUE_ORCHID, BlockNames.RED_FLOWER, RED_FLOWER, BlockFlower.TYPE_BLUE_ORCHID, V1_20_80);
        registerComplexAlias(BlockNames.ALLIUM, ALLIUM, BlockNames.RED_FLOWER, RED_FLOWER, BlockFlower.TYPE_ALLIUM, V1_20_80);
        registerComplexAlias(BlockNames.AZURE_BLUET, AZURE_BLUET, BlockNames.RED_FLOWER, RED_FLOWER, BlockFlower.TYPE_AZURE_BLUET, V1_20_80);
        registerComplexAlias(BlockNames.RED_TULIP, RED_TULIP, BlockNames.RED_FLOWER, RED_FLOWER, BlockFlower.TYPE_RED_TULIP, V1_20_80);
        registerComplexAlias(BlockNames.ORANGE_TULIP, ORANGE_TULIP, BlockNames.RED_FLOWER, RED_FLOWER, BlockFlower.TYPE_ORANGE_TULIP, V1_20_80);
        registerComplexAlias(BlockNames.WHITE_TULIP, WHITE_TULIP, BlockNames.RED_FLOWER, RED_FLOWER, BlockFlower.TYPE_WHITE_TULIP, V1_20_80);
        registerComplexAlias(BlockNames.PINK_TULIP, PINK_TULIP, BlockNames.RED_FLOWER, RED_FLOWER, BlockFlower.TYPE_PINK_TULIP, V1_20_80);
        registerComplexAlias(BlockNames.OXEYE_DAISY, OXEYE_DAISY, BlockNames.RED_FLOWER, RED_FLOWER, BlockFlower.TYPE_OXEYE_DAISY, V1_20_80);
        registerComplexAlias(BlockNames.CORNFLOWER, CORNFLOWER, BlockNames.RED_FLOWER, RED_FLOWER, BlockFlower.TYPE_CORNFLOWER, V1_20_80);
        registerComplexAlias(BlockNames.LILY_OF_THE_VALLEY, LILY_OF_THE_VALLEY, BlockNames.RED_FLOWER, RED_FLOWER, BlockFlower.TYPE_LILY_OF_THE_VALLEY, V1_20_80);

        registerComplexAlias(BlockNames.SUNFLOWER, SUNFLOWER, BlockNames.DOUBLE_PLANT, DOUBLE_PLANT, BlockDoublePlant.TYPE_SUNFLOWER, V1_21_0);
        registerComplexAlias(BlockNames.LILAC, LILAC, BlockNames.DOUBLE_PLANT, DOUBLE_PLANT, BlockDoublePlant.TYPE_LILAC, V1_21_0);
        registerComplexAlias(BlockNames.TALL_GRASS, TALL_GRASS, BlockNames.DOUBLE_PLANT, DOUBLE_PLANT, BlockDoublePlant.TYPE_TALL_GRASS, V1_21_0);
        registerComplexAlias(BlockNames.LARGE_FERN, LARGE_FERN, BlockNames.DOUBLE_PLANT, DOUBLE_PLANT, BlockDoublePlant.TYPE_LARGE_FERN, V1_21_0);
        registerComplexAlias(BlockNames.ROSE_BUSH, ROSE_BUSH, BlockNames.DOUBLE_PLANT, DOUBLE_PLANT, BlockDoublePlant.TYPE_ROSE_BUSH, V1_21_0);
        registerComplexAlias(BlockNames.PEONY, PEONY, BlockNames.DOUBLE_PLANT, DOUBLE_PLANT, BlockDoublePlant.TYPE_PEONY, V1_21_0);

        registerComplexAlias(BlockNames.FERN, FERN, BlockNames.SHORT_GRASS, SHORT_GRASS, BlockTallGrass.TYPE_FERN, V1_21_0);

        registerComplexAlias(BlockNames.TUBE_CORAL_BLOCK, TUBE_CORAL_BLOCK, BlockNames.CORAL_BLOCK, CORAL_BLOCK, BlockCoralBlock.BLUE, V1_21_0);
        registerComplexAlias(BlockNames.BRAIN_CORAL_BLOCK, BRAIN_CORAL_BLOCK, BlockNames.CORAL_BLOCK, CORAL_BLOCK, BlockCoralBlock.PINK, V1_21_0);
        registerComplexAlias(BlockNames.BUBBLE_CORAL_BLOCK, BUBBLE_CORAL_BLOCK, BlockNames.CORAL_BLOCK, CORAL_BLOCK, BlockCoralBlock.PURPLE, V1_21_0);
        registerComplexAlias(BlockNames.FIRE_CORAL_BLOCK, FIRE_CORAL_BLOCK, BlockNames.CORAL_BLOCK, CORAL_BLOCK, BlockCoralBlock.RED, V1_21_0);
        registerComplexAlias(BlockNames.HORN_CORAL_BLOCK, HORN_CORAL_BLOCK, BlockNames.CORAL_BLOCK, CORAL_BLOCK, BlockCoralBlock.YELLOW, V1_21_0);

        registerComplexAlias(BlockNames.DEAD_TUBE_CORAL_BLOCK, DEAD_TUBE_CORAL_BLOCK, BlockNames.CORAL_BLOCK, CORAL_BLOCK, BlockCoralBlock.DEAD_BIT | BlockCoralBlock.BLUE, V1_21_0);
        registerComplexAlias(BlockNames.DEAD_BRAIN_CORAL_BLOCK, DEAD_BRAIN_CORAL_BLOCK, BlockNames.CORAL_BLOCK, CORAL_BLOCK, BlockCoralBlock.DEAD_BIT | BlockCoralBlock.PINK, V1_21_0);
        registerComplexAlias(BlockNames.DEAD_BUBBLE_CORAL_BLOCK, DEAD_BUBBLE_CORAL_BLOCK, BlockNames.CORAL_BLOCK, CORAL_BLOCK, BlockCoralBlock.DEAD_BIT | BlockCoralBlock.PURPLE, V1_21_0);
        registerComplexAlias(BlockNames.DEAD_FIRE_CORAL_BLOCK, DEAD_FIRE_CORAL_BLOCK, BlockNames.CORAL_BLOCK, CORAL_BLOCK, BlockCoralBlock.DEAD_BIT | BlockCoralBlock.RED, V1_21_0);
        registerComplexAlias(BlockNames.DEAD_HORN_CORAL_BLOCK, DEAD_HORN_CORAL_BLOCK, BlockNames.CORAL_BLOCK, CORAL_BLOCK, BlockCoralBlock.DEAD_BIT | BlockCoralBlock.YELLOW, V1_21_0);

        registerComplexAlias(BlockNames.SMOOTH_STONE_SLAB, SMOOTH_STONE_SLAB, BlockNames.STONE_BLOCK_SLAB, STONE_BLOCK_SLAB, BlockSlabStone.TYPE_SMOOTH_STONE, V1_21_0);
        registerComplexAlias(BlockNames.SANDSTONE_SLAB, SANDSTONE_SLAB, BlockNames.STONE_BLOCK_SLAB, STONE_BLOCK_SLAB, BlockSlabStone.TYPE_SANDSTONE, V1_21_0);
        registerComplexAlias(BlockNames.PETRIFIED_OAK_SLAB, PETRIFIED_OAK_SLAB, BlockNames.STONE_BLOCK_SLAB, STONE_BLOCK_SLAB, BlockSlabStone.TYPE_WOOD, V1_21_0);
        registerComplexAlias(BlockNames.COBBLESTONE_SLAB, COBBLESTONE_SLAB, BlockNames.STONE_BLOCK_SLAB, STONE_BLOCK_SLAB, BlockSlabStone.TYPE_COBBLESTONE, V1_21_0);
        registerComplexAlias(BlockNames.BRICK_SLAB, BRICK_SLAB, BlockNames.STONE_BLOCK_SLAB, STONE_BLOCK_SLAB, BlockSlabStone.TYPE_BRICK, V1_21_0);
        registerComplexAlias(BlockNames.STONE_BRICK_SLAB, STONE_BRICK_SLAB, BlockNames.STONE_BLOCK_SLAB, STONE_BLOCK_SLAB, BlockSlabStone.TYPE_STONE_BRICK, V1_21_0);
        registerComplexAlias(BlockNames.QUARTZ_SLAB, QUARTZ_SLAB, BlockNames.STONE_BLOCK_SLAB, STONE_BLOCK_SLAB, BlockSlabStone.TYPE_QUARTZ, V1_21_0);
        registerComplexAlias(BlockNames.NETHER_BRICK_SLAB, NETHER_BRICK_SLAB, BlockNames.STONE_BLOCK_SLAB, STONE_BLOCK_SLAB, BlockSlabStone.TYPE_NETHER_BRICK, V1_21_0);

        registerComplexAlias(BlockNames.SMOOTH_STONE_DOUBLE_SLAB, SMOOTH_STONE_DOUBLE_SLAB, BlockNames.DOUBLE_STONE_BLOCK_SLAB, DOUBLE_STONE_BLOCK_SLAB, BlockDoubleSlabStone.TYPE_SMOOTH_STONE, V1_21_20);
        registerComplexAlias(BlockNames.SANDSTONE_DOUBLE_SLAB, SANDSTONE_DOUBLE_SLAB, BlockNames.DOUBLE_STONE_BLOCK_SLAB, DOUBLE_STONE_BLOCK_SLAB, BlockDoubleSlabStone.TYPE_SANDSTONE, V1_21_20);
        registerComplexAlias(BlockNames.PETRIFIED_OAK_DOUBLE_SLAB, PETRIFIED_OAK_DOUBLE_SLAB, BlockNames.DOUBLE_STONE_BLOCK_SLAB, DOUBLE_STONE_BLOCK_SLAB, BlockDoubleSlabStone.TYPE_WOOD, V1_21_20);
        registerComplexAlias(BlockNames.COBBLESTONE_DOUBLE_SLAB, COBBLESTONE_DOUBLE_SLAB, BlockNames.DOUBLE_STONE_BLOCK_SLAB, DOUBLE_STONE_BLOCK_SLAB, BlockDoubleSlabStone.TYPE_COBBLESTONE, V1_21_20);
        registerComplexAlias(BlockNames.BRICK_DOUBLE_SLAB, BRICK_DOUBLE_SLAB, BlockNames.DOUBLE_STONE_BLOCK_SLAB, DOUBLE_STONE_BLOCK_SLAB, BlockDoubleSlabStone.TYPE_BRICK, V1_21_20);
        registerComplexAlias(BlockNames.STONE_BRICK_DOUBLE_SLAB, STONE_BRICK_DOUBLE_SLAB, BlockNames.DOUBLE_STONE_BLOCK_SLAB, DOUBLE_STONE_BLOCK_SLAB, BlockDoubleSlabStone.TYPE_STONE_BRICK, V1_21_20);
        registerComplexAlias(BlockNames.QUARTZ_DOUBLE_SLAB, QUARTZ_DOUBLE_SLAB, BlockNames.DOUBLE_STONE_BLOCK_SLAB, DOUBLE_STONE_BLOCK_SLAB, BlockDoubleSlabStone.TYPE_QUARTZ, V1_21_20);
        registerComplexAlias(BlockNames.NETHER_BRICK_DOUBLE_SLAB, NETHER_BRICK_DOUBLE_SLAB, BlockNames.DOUBLE_STONE_BLOCK_SLAB, DOUBLE_STONE_BLOCK_SLAB, BlockDoubleSlabStone.TYPE_NETHER_BRICK, V1_21_20);

        registerComplexAlias(BlockNames.RED_SANDSTONE_SLAB, RED_SANDSTONE_SLAB, BlockNames.STONE_BLOCK_SLAB2, STONE_BLOCK_SLAB2, BlockSlabRedSandstone.TYPE_RED_SANDSTONE, V1_21_20);
        registerComplexAlias(BlockNames.PURPUR_SLAB, PURPUR_SLAB, BlockNames.STONE_BLOCK_SLAB2, STONE_BLOCK_SLAB2, BlockSlabRedSandstone.TYPE_PURPUR, V1_21_20);
        registerComplexAlias(BlockNames.PRISMARINE_SLAB, PRISMARINE_SLAB, BlockNames.STONE_BLOCK_SLAB2, STONE_BLOCK_SLAB2, BlockSlabRedSandstone.TYPE_PRISMARINE_ROUGH, V1_21_20);
        registerComplexAlias(BlockNames.DARK_PRISMARINE_SLAB, DARK_PRISMARINE_SLAB, BlockNames.STONE_BLOCK_SLAB2, STONE_BLOCK_SLAB2, BlockSlabRedSandstone.TYPE_PRISMARINE_DARK, V1_21_20);
        registerComplexAlias(BlockNames.PRISMARINE_BRICK_SLAB, PRISMARINE_BRICK_SLAB, BlockNames.STONE_BLOCK_SLAB2, STONE_BLOCK_SLAB2, BlockSlabRedSandstone.TYPE_PRISMARINE_BRICK, V1_21_20);
        registerComplexAlias(BlockNames.MOSSY_COBBLESTONE_SLAB, MOSSY_COBBLESTONE_SLAB, BlockNames.STONE_BLOCK_SLAB2, STONE_BLOCK_SLAB2, BlockSlabRedSandstone.TYPE_MOSSY_COBBLESTONE, V1_21_20);
        registerComplexAlias(BlockNames.SMOOTH_SANDSTONE_SLAB, SMOOTH_SANDSTONE_SLAB, BlockNames.STONE_BLOCK_SLAB2, STONE_BLOCK_SLAB2, BlockSlabRedSandstone.TYPE_SMOOTH_SANDSTONE, V1_21_20);
        registerComplexAlias(BlockNames.RED_NETHER_BRICK_SLAB, RED_NETHER_BRICK_SLAB, BlockNames.STONE_BLOCK_SLAB2, STONE_BLOCK_SLAB2, BlockSlabRedSandstone.TYPE_RED_NETHER_BRICK, V1_21_20);

        registerComplexAlias(BlockNames.RED_SANDSTONE_DOUBLE_SLAB, RED_SANDSTONE_DOUBLE_SLAB, BlockNames.DOUBLE_STONE_BLOCK_SLAB2, DOUBLE_STONE_BLOCK_SLAB2, BlockDoubleSlabRedSandstone.TYPE_RED_SANDSTONE, V1_21_20);
        registerComplexAlias(BlockNames.PURPUR_DOUBLE_SLAB, PURPUR_DOUBLE_SLAB, BlockNames.DOUBLE_STONE_BLOCK_SLAB2, DOUBLE_STONE_BLOCK_SLAB2, BlockDoubleSlabRedSandstone.TYPE_PURPUR, V1_21_20);
        registerComplexAlias(BlockNames.PRISMARINE_DOUBLE_SLAB, PRISMARINE_DOUBLE_SLAB, BlockNames.DOUBLE_STONE_BLOCK_SLAB2, DOUBLE_STONE_BLOCK_SLAB2, BlockDoubleSlabRedSandstone.TYPE_PRISMARINE_ROUGH, V1_21_20);
        registerComplexAlias(BlockNames.DARK_PRISMARINE_DOUBLE_SLAB, DARK_PRISMARINE_DOUBLE_SLAB, BlockNames.DOUBLE_STONE_BLOCK_SLAB2, DOUBLE_STONE_BLOCK_SLAB2, BlockDoubleSlabRedSandstone.TYPE_PRISMARINE_DARK, V1_21_20);
        registerComplexAlias(BlockNames.PRISMARINE_BRICK_DOUBLE_SLAB, PRISMARINE_BRICK_DOUBLE_SLAB, BlockNames.DOUBLE_STONE_BLOCK_SLAB2, DOUBLE_STONE_BLOCK_SLAB2, BlockDoubleSlabRedSandstone.TYPE_PRISMARINE_BRICK, V1_21_20);
        registerComplexAlias(BlockNames.MOSSY_COBBLESTONE_DOUBLE_SLAB, MOSSY_COBBLESTONE_DOUBLE_SLAB, BlockNames.DOUBLE_STONE_BLOCK_SLAB2, DOUBLE_STONE_BLOCK_SLAB2, BlockDoubleSlabRedSandstone.TYPE_MOSSY_COBBLESTONE, V1_21_20);
        registerComplexAlias(BlockNames.SMOOTH_SANDSTONE_DOUBLE_SLAB, SMOOTH_SANDSTONE_DOUBLE_SLAB, BlockNames.DOUBLE_STONE_BLOCK_SLAB2, DOUBLE_STONE_BLOCK_SLAB2, BlockDoubleSlabRedSandstone.TYPE_SMOOTH_SANDSTONE, V1_21_20);
        registerComplexAlias(BlockNames.RED_NETHER_BRICK_DOUBLE_SLAB, RED_NETHER_BRICK_DOUBLE_SLAB, BlockNames.DOUBLE_STONE_BLOCK_SLAB2, DOUBLE_STONE_BLOCK_SLAB2, BlockDoubleSlabRedSandstone.TYPE_RED_NETHER_BRICK, V1_21_20);

        registerComplexAlias(BlockNames.END_STONE_BRICK_SLAB, END_STONE_BRICK_SLAB, BlockNames.STONE_BLOCK_SLAB3, STONE_BLOCK_SLAB3, BlockSlabStone3.TYPE_END_STONE_BRICK, V1_21_20);
        registerComplexAlias(BlockNames.SMOOTH_RED_SANDSTONE_SLAB, SMOOTH_RED_SANDSTONE_SLAB, BlockNames.STONE_BLOCK_SLAB3, STONE_BLOCK_SLAB3, BlockSlabStone3.TYPE_SMOOTH_RED_SANDSTONE, V1_21_20);
        registerComplexAlias(BlockNames.POLISHED_ANDESITE_SLAB, POLISHED_ANDESITE_SLAB, BlockNames.STONE_BLOCK_SLAB3, STONE_BLOCK_SLAB3, BlockSlabStone3.TYPE_POLISHED_ANDESITE, V1_21_20);
        registerComplexAlias(BlockNames.ANDESITE_SLAB, ANDESITE_SLAB, BlockNames.STONE_BLOCK_SLAB3, STONE_BLOCK_SLAB3, BlockSlabStone3.TYPE_ANDESITE, V1_21_20);
        registerComplexAlias(BlockNames.DIORITE_SLAB, DIORITE_SLAB, BlockNames.STONE_BLOCK_SLAB3, STONE_BLOCK_SLAB3, BlockSlabStone3.TYPE_DIORITE, V1_21_20);
        registerComplexAlias(BlockNames.POLISHED_DIORITE_SLAB, POLISHED_DIORITE_SLAB, BlockNames.STONE_BLOCK_SLAB3, STONE_BLOCK_SLAB3, BlockSlabStone3.TYPE_POLISHED_DIORITE, V1_21_20);
        registerComplexAlias(BlockNames.GRANITE_SLAB, GRANITE_SLAB, BlockNames.STONE_BLOCK_SLAB3, STONE_BLOCK_SLAB3, BlockSlabStone3.TYPE_GRANITE, V1_21_20);
        registerComplexAlias(BlockNames.POLISHED_GRANITE_SLAB, POLISHED_GRANITE_SLAB, BlockNames.STONE_BLOCK_SLAB3, STONE_BLOCK_SLAB3, BlockSlabStone3.TYPE_POLISHED_GRANITE, V1_21_20);

        registerComplexAlias(BlockNames.END_STONE_BRICK_DOUBLE_SLAB, END_STONE_BRICK_DOUBLE_SLAB, BlockNames.DOUBLE_STONE_SLAB3, DOUBLE_STONE_SLAB3, BlockDoubleSlabStone3.TYPE_END_STONE_BRICK, V1_21_20);
        registerComplexAlias(BlockNames.SMOOTH_RED_SANDSTONE_DOUBLE_SLAB, SMOOTH_RED_SANDSTONE_DOUBLE_SLAB, BlockNames.DOUBLE_STONE_SLAB3, DOUBLE_STONE_SLAB3, BlockDoubleSlabStone3.TYPE_SMOOTH_RED_SANDSTONE, V1_21_20);
        registerComplexAlias(BlockNames.POLISHED_ANDESITE_DOUBLE_SLAB, POLISHED_ANDESITE_DOUBLE_SLAB, BlockNames.DOUBLE_STONE_SLAB3, DOUBLE_STONE_SLAB3, BlockDoubleSlabStone3.TYPE_POLISHED_ANDESITE, V1_21_20);
        registerComplexAlias(BlockNames.ANDESITE_DOUBLE_SLAB, ANDESITE_DOUBLE_SLAB, BlockNames.DOUBLE_STONE_SLAB3, DOUBLE_STONE_SLAB3, BlockDoubleSlabStone3.TYPE_ANDESITE, V1_21_20);
        registerComplexAlias(BlockNames.DIORITE_DOUBLE_SLAB, DIORITE_DOUBLE_SLAB, BlockNames.DOUBLE_STONE_SLAB3, DOUBLE_STONE_SLAB3, BlockDoubleSlabStone3.TYPE_DIORITE, V1_21_20);
        registerComplexAlias(BlockNames.POLISHED_DIORITE_DOUBLE_SLAB, POLISHED_DIORITE_DOUBLE_SLAB, BlockNames.DOUBLE_STONE_SLAB3, DOUBLE_STONE_SLAB3, BlockDoubleSlabStone3.TYPE_POLISHED_DIORITE, V1_21_20);
        registerComplexAlias(BlockNames.GRANITE_DOUBLE_SLAB, GRANITE_DOUBLE_SLAB, BlockNames.DOUBLE_STONE_SLAB3, DOUBLE_STONE_SLAB3, BlockDoubleSlabStone3.TYPE_GRANITE, V1_21_20);
        registerComplexAlias(BlockNames.POLISHED_GRANITE_DOUBLE_SLAB, POLISHED_GRANITE_DOUBLE_SLAB, BlockNames.DOUBLE_STONE_SLAB3, DOUBLE_STONE_SLAB3, BlockDoubleSlabStone3.TYPE_POLISHED_GRANITE, V1_21_20);

        registerComplexAlias(BlockNames.MOSSY_STONE_BRICK_SLAB, MOSSY_STONE_BRICK_SLAB, BlockNames.STONE_BLOCK_SLAB4, STONE_BLOCK_SLAB4, BlockSlabStone4.TYPE_MOSSY_STONE_BRICK, V1_21_20);
        registerComplexAlias(BlockNames.SMOOTH_QUARTZ_SLAB, SMOOTH_QUARTZ_SLAB, BlockNames.STONE_BLOCK_SLAB4, STONE_BLOCK_SLAB4, BlockSlabStone4.TYPE_SMOOTH_QUARTZ, V1_21_20);
        registerComplexAlias(BlockNames.NORMAL_STONE_SLAB, NORMAL_STONE_SLAB, BlockNames.STONE_BLOCK_SLAB4, STONE_BLOCK_SLAB4, BlockSlabStone4.TYPE_STONE, V1_21_20);
        registerComplexAlias(BlockNames.CUT_SANDSTONE_SLAB, CUT_SANDSTONE_SLAB, BlockNames.STONE_BLOCK_SLAB4, STONE_BLOCK_SLAB4, BlockSlabStone4.TYPE_CUT_SANDSTONE, V1_21_20);
        registerComplexAlias(BlockNames.CUT_RED_SANDSTONE_SLAB, CUT_RED_SANDSTONE_SLAB, BlockNames.STONE_BLOCK_SLAB4, STONE_BLOCK_SLAB4, BlockSlabStone4.TYPE_CUT_RED_SANDSTONE, V1_21_20);

        registerComplexAlias(BlockNames.MOSSY_STONE_BRICK_DOUBLE_SLAB, MOSSY_STONE_BRICK_DOUBLE_SLAB, BlockNames.DOUBLE_STONE_BLOCK_SLAB4, DOUBLE_STONE_BLOCK_SLAB4, BlockDoubleSlabStone4.TYPE_MOSSY_STONE_BRICK, V1_21_20);
        registerComplexAlias(BlockNames.SMOOTH_QUARTZ_DOUBLE_SLAB, SMOOTH_QUARTZ_DOUBLE_SLAB, BlockNames.DOUBLE_STONE_BLOCK_SLAB4, DOUBLE_STONE_BLOCK_SLAB4, BlockDoubleSlabStone4.TYPE_SMOOTH_QUARTZ, V1_21_20);
        registerComplexAlias(BlockNames.NORMAL_STONE_DOUBLE_SLAB, NORMAL_STONE_DOUBLE_SLAB, BlockNames.DOUBLE_STONE_BLOCK_SLAB4, DOUBLE_STONE_BLOCK_SLAB4, BlockDoubleSlabStone4.TYPE_STONE, V1_21_20);
        registerComplexAlias(BlockNames.CUT_SANDSTONE_DOUBLE_SLAB, CUT_SANDSTONE_DOUBLE_SLAB, BlockNames.DOUBLE_STONE_BLOCK_SLAB4, DOUBLE_STONE_BLOCK_SLAB4, BlockDoubleSlabStone4.TYPE_CUT_SANDSTONE, V1_21_20);
        registerComplexAlias(BlockNames.CUT_RED_SANDSTONE_DOUBLE_SLAB, CUT_RED_SANDSTONE_DOUBLE_SLAB, BlockNames.DOUBLE_STONE_BLOCK_SLAB4, DOUBLE_STONE_BLOCK_SLAB4, BlockDoubleSlabStone4.TYPE_CUT_RED_SANDSTONE, V1_21_20);

        registerComplexAlias(BlockNames.TUBE_CORAL_WALL_FAN, TUBE_CORAL_WALL_FAN, BlockNames.CORAL_FAN_HANG, CORAL_FAN_HANG, BlockCoralFanHang.BLUE, V1_21_20);
        registerComplexAlias(BlockNames.BRAIN_CORAL_WALL_FAN, BRAIN_CORAL_WALL_FAN, BlockNames.CORAL_FAN_HANG, CORAL_FAN_HANG, BlockCoralFanHang.PINK, V1_21_20);
        registerComplexAlias(BlockNames.BUBBLE_CORAL_WALL_FAN, BUBBLE_CORAL_WALL_FAN, BlockNames.CORAL_FAN_HANG2, CORAL_FAN_HANG2, BlockCoralFanHang2.PURPLE, V1_21_20);
        registerComplexAlias(BlockNames.FIRE_CORAL_WALL_FAN, FIRE_CORAL_WALL_FAN, BlockNames.CORAL_FAN_HANG2, CORAL_FAN_HANG2, BlockCoralFanHang2.RED, V1_21_20);
        registerComplexAlias(BlockNames.HORN_CORAL_WALL_FAN, HORN_CORAL_WALL_FAN, BlockNames.CORAL_FAN_HANG3, CORAL_FAN_HANG3, BlockCoralFanHang3.YELLOW, V1_21_20);

        registerComplexAlias(BlockNames.DEAD_TUBE_CORAL_WALL_FAN, DEAD_TUBE_CORAL_WALL_FAN, BlockNames.CORAL_FAN_HANG, CORAL_FAN_HANG, BlockCoralFanHang.DEAD_BIT | BlockCoralFanHang.BLUE, V1_21_20);
        registerComplexAlias(BlockNames.DEAD_BRAIN_CORAL_WALL_FAN, DEAD_BRAIN_CORAL_WALL_FAN, BlockNames.CORAL_FAN_HANG, CORAL_FAN_HANG, BlockCoralFanHang.DEAD_BIT | BlockCoralFanHang.PINK, V1_21_20);
        registerComplexAlias(BlockNames.DEAD_BUBBLE_CORAL_WALL_FAN, DEAD_BUBBLE_CORAL_WALL_FAN, BlockNames.CORAL_FAN_HANG2, CORAL_FAN_HANG2, BlockCoralFanHang2.DEAD_BIT | BlockCoralFanHang2.PURPLE, V1_21_20);
        registerComplexAlias(BlockNames.DEAD_FIRE_CORAL_WALL_FAN, DEAD_FIRE_CORAL_WALL_FAN, BlockNames.CORAL_FAN_HANG2, CORAL_FAN_HANG2, BlockCoralFanHang2.DEAD_BIT | BlockCoralFanHang2.RED, V1_21_20);
        registerComplexAlias(BlockNames.DEAD_HORN_CORAL_WALL_FAN, DEAD_HORN_CORAL_WALL_FAN, BlockNames.CORAL_FAN_HANG3, CORAL_FAN_HANG3, BlockCoralFanHang3.DEAD_BIT | BlockCoralFanHang3.YELLOW, V1_21_20);

        registerComplexAlias(BlockNames.INFESTED_STONE, INFESTED_STONE, BlockNames.MONSTER_EGG, MONSTER_EGG, BlockMonsterEgg.TYPE_STONE, V1_21_20);
        registerComplexAlias(BlockNames.INFESTED_COBBLESTONE, INFESTED_COBBLESTONE, BlockNames.MONSTER_EGG, MONSTER_EGG, BlockMonsterEgg.TYPE_COBBLESTONE, V1_21_20);
        registerComplexAlias(BlockNames.INFESTED_STONE_BRICKS, INFESTED_STONE_BRICKS, BlockNames.MONSTER_EGG, MONSTER_EGG, BlockMonsterEgg.TYPE_STONE_BRICK, V1_21_20);
        registerComplexAlias(BlockNames.INFESTED_MOSSY_STONE_BRICKS, INFESTED_MOSSY_STONE_BRICKS, BlockNames.MONSTER_EGG, MONSTER_EGG, BlockMonsterEgg.TYPE_MOSSY_BRICK, V1_21_20);
        registerComplexAlias(BlockNames.INFESTED_CRACKED_STONE_BRICKS, INFESTED_CRACKED_STONE_BRICKS, BlockNames.MONSTER_EGG, MONSTER_EGG, BlockMonsterEgg.TYPE_CRACKED_BRICK, V1_21_20);
        registerComplexAlias(BlockNames.INFESTED_CHISELED_STONE_BRICKS, INFESTED_CHISELED_STONE_BRICKS, BlockNames.MONSTER_EGG, MONSTER_EGG, BlockMonsterEgg.TYPE_CHISELED_BRICK, V1_21_20);

        registerComplexAlias(BlockNames.STONE_BRICKS, STONE_BRICKS, BlockNames.STONEBRICK, STONEBRICK, BlockBricksStone.NORMAL, V1_21_20);
        registerComplexAlias(BlockNames.MOSSY_STONE_BRICKS, MOSSY_STONE_BRICKS, BlockNames.STONEBRICK, STONEBRICK, BlockBricksStone.MOSSY, V1_21_20);
        registerComplexAlias(BlockNames.CRACKED_STONE_BRICKS, CRACKED_STONE_BRICKS, BlockNames.STONEBRICK, STONEBRICK, BlockBricksStone.CRACKED, V1_21_20);
        registerComplexAlias(BlockNames.CHISELED_STONE_BRICKS, CHISELED_STONE_BRICKS, BlockNames.STONEBRICK, STONEBRICK, BlockBricksStone.CHISELED, V1_21_20);

        registerComplexAlias(BlockNames.DARK_PRISMARINE, DARK_PRISMARINE, BlockNames.PRISMARINE, PRISMARINE, BlockPrismarine.DARK, V1_21_20);
        registerComplexAlias(BlockNames.PRISMARINE_BRICKS, PRISMARINE_BRICKS, BlockNames.PRISMARINE, PRISMARINE, BlockPrismarine.BRICKS, V1_21_20);

        registerComplexAlias(BlockNames.CHISELED_SANDSTONE, CHISELED_SANDSTONE, BlockNames.SANDSTONE, SANDSTONE, BlockSandstone.CHISELED, V1_21_20);
        registerComplexAlias(BlockNames.CUT_SANDSTONE, CUT_SANDSTONE, BlockNames.SANDSTONE, SANDSTONE, BlockSandstone.CUT, V1_21_20);
        registerComplexAlias(BlockNames.SMOOTH_SANDSTONE, SMOOTH_SANDSTONE, BlockNames.SANDSTONE, SANDSTONE, BlockSandstone.SMOOTH, V1_21_20);

        registerComplexAlias(BlockNames.CHISELED_RED_SANDSTONE, CHISELED_RED_SANDSTONE, BlockNames.RED_SANDSTONE, RED_SANDSTONE, BlockRedSandstone.CHISELED, V1_21_20);
        registerComplexAlias(BlockNames.CUT_RED_SANDSTONE, CUT_RED_SANDSTONE, BlockNames.RED_SANDSTONE, RED_SANDSTONE, BlockRedSandstone.CUT, V1_21_20);
        registerComplexAlias(BlockNames.SMOOTH_RED_SANDSTONE, SMOOTH_RED_SANDSTONE, BlockNames.RED_SANDSTONE, RED_SANDSTONE, BlockRedSandstone.SMOOTH, V1_21_20);

        registerComplexAlias(BlockNames.RED_SAND, RED_SAND, BlockNames.SAND, SAND, BlockSand.RED, V1_21_20);

        registerComplexAlias(BlockNames.COARSE_DIRT, COARSE_DIRT, BlockNames.DIRT, DIRT, BlockDirt.TYPE_COARSE_DIRT, V1_21_20);

        registerComplexAlias(BlockNames.LIGHT_BLOCK_0, LIGHT_BLOCK_0, BlockNames.LIGHT_BLOCK, LIGHT_BLOCK, 0, V1_21_20);
        registerComplexAlias(BlockNames.LIGHT_BLOCK_1, LIGHT_BLOCK_1, BlockNames.LIGHT_BLOCK, LIGHT_BLOCK, 1, V1_21_20);
        registerComplexAlias(BlockNames.LIGHT_BLOCK_2, LIGHT_BLOCK_2, BlockNames.LIGHT_BLOCK, LIGHT_BLOCK, 2, V1_21_20);
        registerComplexAlias(BlockNames.LIGHT_BLOCK_3, LIGHT_BLOCK_3, BlockNames.LIGHT_BLOCK, LIGHT_BLOCK, 3, V1_21_20);
        registerComplexAlias(BlockNames.LIGHT_BLOCK_4, LIGHT_BLOCK_4, BlockNames.LIGHT_BLOCK, LIGHT_BLOCK, 4, V1_21_20);
        registerComplexAlias(BlockNames.LIGHT_BLOCK_5, LIGHT_BLOCK_5, BlockNames.LIGHT_BLOCK, LIGHT_BLOCK, 5, V1_21_20);
        registerComplexAlias(BlockNames.LIGHT_BLOCK_6, LIGHT_BLOCK_6, BlockNames.LIGHT_BLOCK, LIGHT_BLOCK, 6, V1_21_20);
        registerComplexAlias(BlockNames.LIGHT_BLOCK_7, LIGHT_BLOCK_7, BlockNames.LIGHT_BLOCK, LIGHT_BLOCK, 7, V1_21_20);
        registerComplexAlias(BlockNames.LIGHT_BLOCK_8, LIGHT_BLOCK_8, BlockNames.LIGHT_BLOCK, LIGHT_BLOCK, 8, V1_21_20);
        registerComplexAlias(BlockNames.LIGHT_BLOCK_9, LIGHT_BLOCK_9, BlockNames.LIGHT_BLOCK, LIGHT_BLOCK, 9, V1_21_20);
        registerComplexAlias(BlockNames.LIGHT_BLOCK_10, LIGHT_BLOCK_10, BlockNames.LIGHT_BLOCK, LIGHT_BLOCK, 10, V1_21_20);
        registerComplexAlias(BlockNames.LIGHT_BLOCK_11, LIGHT_BLOCK_11, BlockNames.LIGHT_BLOCK, LIGHT_BLOCK, 11, V1_21_20);
        registerComplexAlias(BlockNames.LIGHT_BLOCK_12, LIGHT_BLOCK_12, BlockNames.LIGHT_BLOCK, LIGHT_BLOCK, 12, V1_21_20);
        registerComplexAlias(BlockNames.LIGHT_BLOCK_13, LIGHT_BLOCK_13, BlockNames.LIGHT_BLOCK, LIGHT_BLOCK, 13, V1_21_20);
        registerComplexAlias(BlockNames.LIGHT_BLOCK_14, LIGHT_BLOCK_14, BlockNames.LIGHT_BLOCK, LIGHT_BLOCK, 14, V1_21_20);
        registerComplexAlias(BlockNames.LIGHT_BLOCK_15, LIGHT_BLOCK_15, BlockNames.LIGHT_BLOCK, LIGHT_BLOCK, 15, V1_21_20);

        registerComplexAlias(BlockNames.CHIPPED_ANVIL, CHIPPED_ANVIL, BlockNames.ANVIL, ANVIL, BlockAnvil.SLIGHTLY_DAMAGED, V1_21_20);
        registerComplexAlias(BlockNames.DAMAGED_ANVIL, DAMAGED_ANVIL, BlockNames.ANVIL, ANVIL, BlockAnvil.VERY_DAMAGED, V1_21_20);
        registerComplexAlias(BlockNames.DEPRECATED_ANVIL, DEPRECATED_ANVIL, BlockNames.ANVIL, ANVIL, BlockAnvil.BROKEN, V1_21_20);

        registerComplexAlias(BlockNames.CHISELED_QUARTZ_BLOCK, CHISELED_QUARTZ_BLOCK, BlockNames.QUARTZ_BLOCK, QUARTZ_BLOCK, BlockQuartz.CHISELED, V1_21_20);
        registerComplexAlias(BlockNames.QUARTZ_PILLAR, QUARTZ_PILLAR, BlockNames.QUARTZ_BLOCK, QUARTZ_BLOCK, BlockQuartz.PILLAR, V1_21_20);
        registerComplexAlias(BlockNames.SMOOTH_QUARTZ, SMOOTH_QUARTZ, BlockNames.QUARTZ_BLOCK, QUARTZ_BLOCK, BlockQuartz.SMOOTH, V1_21_20);

        registerComplexAlias(BlockNames.DEPRECATED_PURPUR_BLOCK_1, DEPRECATED_PURPUR_BLOCK_1, BlockNames.PURPUR_BLOCK, PURPUR_BLOCK, BlockPurpur.CHISELED, V1_21_30);
        registerComplexAlias(BlockNames.PURPUR_PILLAR, PURPUR_PILLAR, BlockNames.PURPUR_BLOCK, PURPUR_BLOCK, BlockPurpur.PILLAR, V1_21_30);
        registerComplexAlias(BlockNames.DEPRECATED_PURPUR_BLOCK_2, DEPRECATED_PURPUR_BLOCK_2, BlockNames.PURPUR_BLOCK, PURPUR_BLOCK, BlockPurpur.SMOOTH, V1_21_30);

        registerComplexAlias(BlockNames.MOSSY_COBBLESTONE_WALL, MOSSY_COBBLESTONE_WALL, BlockNames.COBBLESTONE_WALL, COBBLESTONE_WALL, BlockWallCobblestone.TYPE_MOSSY_COBBLESTONE, V1_21_30);
        registerComplexAlias(BlockNames.GRANITE_WALL, GRANITE_WALL, BlockNames.COBBLESTONE_WALL, COBBLESTONE_WALL, BlockWallCobblestone.TYPE_GRANITE, V1_21_30);
        registerComplexAlias(BlockNames.DIORITE_WALL, DIORITE_WALL, BlockNames.COBBLESTONE_WALL, COBBLESTONE_WALL, BlockWallCobblestone.TYPE_DIORITE, V1_21_30);
        registerComplexAlias(BlockNames.ANDESITE_WALL, ANDESITE_WALL, BlockNames.COBBLESTONE_WALL, COBBLESTONE_WALL, BlockWallCobblestone.TYPE_ANDESITE, V1_21_30);
        registerComplexAlias(BlockNames.SANDSTONE_WALL, SANDSTONE_WALL, BlockNames.COBBLESTONE_WALL, COBBLESTONE_WALL, BlockWallCobblestone.TYPE_SANDSTONE, V1_21_30);
        registerComplexAlias(BlockNames.BRICK_WALL, BRICK_WALL, BlockNames.COBBLESTONE_WALL, COBBLESTONE_WALL, BlockWallCobblestone.TYPE_BRICK, V1_21_30);
        registerComplexAlias(BlockNames.STONE_BRICK_WALL, STONE_BRICK_WALL, BlockNames.COBBLESTONE_WALL, COBBLESTONE_WALL, BlockWallCobblestone.TYPE_STONE_BRICK, V1_21_30);
        registerComplexAlias(BlockNames.MOSSY_STONE_BRICK_WALL, MOSSY_STONE_BRICK_WALL, BlockNames.COBBLESTONE_WALL, COBBLESTONE_WALL, BlockWallCobblestone.TYPE_MOSSY_STONE_BRICK, V1_21_30);
        registerComplexAlias(BlockNames.NETHER_BRICK_WALL, NETHER_BRICK_WALL, BlockNames.COBBLESTONE_WALL, COBBLESTONE_WALL, BlockWallCobblestone.TYPE_NETHER_BRICK, V1_21_30);
        registerComplexAlias(BlockNames.END_STONE_BRICK_WALL, END_STONE_BRICK_WALL, BlockNames.COBBLESTONE_WALL, COBBLESTONE_WALL, BlockWallCobblestone.TYPE_END_BRICK, V1_21_30);
        registerComplexAlias(BlockNames.PRISMARINE_WALL, PRISMARINE_WALL, BlockNames.COBBLESTONE_WALL, COBBLESTONE_WALL, BlockWallCobblestone.TYPE_PRISMARINE, V1_21_30);
        registerComplexAlias(BlockNames.RED_SANDSTONE_WALL, RED_SANDSTONE_WALL, BlockNames.COBBLESTONE_WALL, COBBLESTONE_WALL, BlockWallCobblestone.TYPE_RED_SANDSTONE, V1_21_30);
        registerComplexAlias(BlockNames.RED_NETHER_BRICK_WALL, RED_NETHER_BRICK_WALL, BlockNames.COBBLESTONE_WALL, COBBLESTONE_WALL, BlockWallCobblestone.TYPE_RED_NETHER_BRICK, V1_21_30);

        registerComplexAlias(BlockNames.WET_SPONGE, WET_SPONGE, BlockNames.SPONGE, SPONGE, BlockSponge.WET, V1_21_30);

        registerComplexAlias(BlockNames.COLORED_TORCH_RED, COLORED_TORCH_RED, BlockNames.COLORED_TORCH_RG, COLORED_TORCH_RG, BlockTorchColoredRedGreen.RED, V1_21_30);
        registerComplexAlias(BlockNames.COLORED_TORCH_GREEN, COLORED_TORCH_GREEN, BlockNames.COLORED_TORCH_RG, COLORED_TORCH_RG, BlockTorchColoredRedGreen.GREEN, V1_21_30);

        registerComplexAlias(BlockNames.COLORED_TORCH_BLUE, COLORED_TORCH_BLUE, BlockNames.COLORED_TORCH_BP, COLORED_TORCH_BP, BlockTorchColoredBluePurple.BLUE, V1_21_30);
        registerComplexAlias(BlockNames.COLORED_TORCH_PURPLE, COLORED_TORCH_PURPLE, BlockNames.COLORED_TORCH_BP, COLORED_TORCH_BP, BlockTorchColoredBluePurple.PURPLE, V1_21_30);

        registerComplexAlias(BlockNames.COMPOUND_CREATOR, COMPOUND_CREATOR, BlockNames.CHEMISTRY_TABLE, CHEMISTRY_TABLE, BlockChemistryTable.TYPE_COMPOUND_CREATOR, V1_21_30);
        registerComplexAlias(BlockNames.MATERIAL_REDUCER, MATERIAL_REDUCER, BlockNames.CHEMISTRY_TABLE, CHEMISTRY_TABLE, BlockChemistryTable.TYPE_MATERIAL_REDUCER, V1_21_30);
        registerComplexAlias(BlockNames.ELEMENT_CONSTRUCTOR, ELEMENT_CONSTRUCTOR, BlockNames.CHEMISTRY_TABLE, CHEMISTRY_TABLE, BlockChemistryTable.TYPE_ELEMENT_CONSTRUCTOR, V1_21_30);
        registerComplexAlias(BlockNames.LAB_TABLE, LAB_TABLE, BlockNames.CHEMISTRY_TABLE, CHEMISTRY_TABLE, BlockChemistryTable.TYPE_LAB_TABLE, V1_21_30);

        registerComplexAlias(BlockNames.UNDERWATER_TNT, UNDERWATER_TNT, BlockNames.TNT, TNT, BlockTNT.ALLOW_UNDERWATER_BIT, V1_21_30);

        if (!V1_21_40.isAvailable()) {
            registerComplexAlias(BlockNames.MUSHROOM_STEM, MUSHROOM_STEM, BlockNames.BROWN_MUSHROOM_BLOCK, BROWN_MUSHROOM_BLOCK, BlockHugeMushroom.STEM, V1_21_40);
        }
    }

    private static Class<? extends Block> registerBlock(String blockName, String itemName, int id, Class<? extends Block> clazz) {
        return registerBlock("minecraft:" + blockName, blockName, "minecraft:" + itemName, itemName, id, clazz);
    }

    private static Class<? extends Block> registerBlock(String blockFullName, String blockShortName, String itemFullName, String itemShortName, int id, Class<? extends Block> clazz) {
        if (Block.list[id] != null) {
            throw new IllegalArgumentException("Duplicate block id: " + id);
        }
        if (BLOCK_FULL_NAME_TO_ID.containsKey(blockFullName)) {
            throw new IllegalArgumentException("Duplicate block full name: " + blockFullName);
        }
        if (ITEM_FULL_NAME_TO_ID.containsKey(itemFullName)) {
            throw new IllegalArgumentException("Duplicate block item full name: " + itemFullName);
        }
        if (BLOCK_NAME_TO_ID.containsKey(blockShortName)) {
            throw new IllegalArgumentException("Duplicate block short name: " + blockShortName);
        }
        if (ITEM_NAME_TO_ID.containsKey(itemShortName)) {
            throw new IllegalArgumentException("Duplicate block item short name: " + itemShortName);
        }

        BLOCK_NAME_TO_ID.put(blockShortName, id);
        BLOCK_FULL_NAME_TO_ID.put(blockFullName, id);
        ID_TO_BLOCK_NAME[id] = blockShortName;
        ID_TO_BLOCK_FULL_NAME[id] = blockFullName;
        ITEM_NAME_TO_ID.put(itemShortName, id);
        ITEM_FULL_NAME_TO_ID.put(itemFullName, id);
        ID_TO_ITEM_NAME[id] = itemShortName;
        ID_TO_ITEM_FULL_NAME[id] = itemFullName;

        Block.list[id] = clazz;
        return clazz;
    }

    /**
     * @param version min required base game version
     */
    private static Class<? extends Block> registerBlock(String blockName, String itemName, int id, Class<? extends Block> clazz, GameVersion version) {
        if (!version.isAvailable()) {
            return null;
        }
        return registerBlock(blockName, itemName, id, clazz);
    }

    public static Class<? extends Block> registerCustomBlock(String fullName, int id, Class<? extends CustomBlock> clazz, CompoundTag definition) {
        return registerCustomBlock(fullName, id, clazz, protocol -> definition);
    }

    public static Class<? extends Block> registerCustomBlock(String fullName, int id, Class<? extends CustomBlock> clazz, IntFunction<CompoundTag> definitionSupplier) {
        Objects.requireNonNull(clazz, "class");
        Objects.requireNonNull(definitionSupplier, "definition");
        if (fullName.split(":").length != 2) {
            throw new IllegalArgumentException("Invalid namespaced identifier: " + fullName);
        }
        if (fullName.startsWith("minecraft:")) {
            throw new IllegalArgumentException("Invalid identifier: " + fullName);
        }

        if (log.isTraceEnabled()) {
            log.trace("Register custom block {} ({}/{}) {} : {}", fullName, id, Block.getItemId(id), clazz, definitionSupplier.apply(GameVersion.getFeatureVersion().getProtocol()));
        }

        registerBlock(fullName, fullName, fullName, fullName, id, clazz);

        CustomBlock block;
        try {
            Constructor<? extends CustomBlock> constructor = clazz.getDeclaredConstructor(int.class);
            constructor.setAccessible(true);
            block = constructor.newInstance(id);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Unable to create custom block instance: " + fullName, e);
        }
        Block[] variants = new Block[block.getVariantCount()];
        variants[0] = block;
        for (int i = 1; i < variants.length; i++) {
            Block variant = block.clone();
            variant.setDamage(i);
            variants[i] = variant;
        }
        Block.variantList[id] = variants;
        Block.solid[id] = block.isSolid();
        Block.transparent[id] = block.isTransparent();
        Block.light[id] = (byte) block.getLightLevel();
        Block.lightBlock[id] = (byte) block.getLightBlock();
        Block.lightBlocking[id] = block.getLightBlock() > 0;
        if (block.isSolid()) {
            if (block.isTransparent()) {
                Block.lightFilter[id] = 1;
            } else {
                Block.lightFilter[id] = 15;
            }
        } else {
            Block.lightFilter[id] = 1;
        }

        BlockTypes.getBlockRegistry().registerBlock(fullName, id, block.getBlockLegacy());
        BlockSerializer.registerCustomBlock(fullName, id, definitionSupplier);
        Items.registerCustomBlockItem(fullName, id);

        CommandEnum.ENUM_BLOCK.getValues().put(fullName, Collections.emptySet());

        return clazz;
    }

    /**
     * @param version min required base game version
     */
    private static void registerBlockAlias(String newName, String oldName, GameVersion version) {
        if (version.isAvailable()) {
            String name = newName;
            newName = oldName;
            oldName = name;
        }
        BLOCK_ALIASES_MAP.put(newName, oldName);
    }

    /**
     * @param version min required base game version
     */
    private static void registerItemAlias(String newName, String oldName, GameVersion version) {
        if (version.isAvailable()) {
            String name = newName;
            newName = oldName;
            oldName = name;
        }
        ITEM_ALIASES_MAP.put(newName, oldName);
    }

    private static void registerComplexAlias(String newName, int newId, String oldName, int oldId, int oldMeta, GameVersion version) {
        if (version.isAvailable()) {
            IntList variants = LEGACY_ALIASES_MAP.computeIfAbsent(oldName, name -> new IntArrayList());
            while (variants.size() <= oldMeta) {
                variants.add(-1);
            }
            variants.set(oldMeta, newId);

            DEPRECATED_LEGACY_ALIASES_MAP.put(newName, Block.getFullId(oldId, oldMeta));
            return;
        }

        COMPLEX_ALIASES_MAP.put(newName, Block.getFullId(oldId, oldMeta));
    }

    public static Object2IntMap<String> getBlockNameToIdMap() {
        return BLOCK_NAME_TO_ID;
    }

    public static Object2IntMap<String> getBlockFullNameToIdMap() {
        return BLOCK_FULL_NAME_TO_ID;
    }

    public static int getIdByBlockName(String name) {
        return getIdByBlockName(name, false);
    }

    public static int getIdByBlockName(String name, boolean lookupAlias) {
        return getIdByBlockShortName(name.startsWith("minecraft:") ? name.substring(10) : name, lookupAlias);
    }

    public static int getIdByBlockShortName(String shortName) {
        return getIdByBlockShortName(shortName, false);
    }

    public static int getIdByBlockShortName(String shortName, boolean lookupAlias) {
        int id = BLOCK_NAME_TO_ID.getInt(shortName);
        if (id != -1) {
            return id;
        }

        if (lookupAlias) {
            String alias = BLOCK_ALIASES_MAP.get(shortName);
            if (alias != null) {
                return BLOCK_NAME_TO_ID.getInt(alias);
            }
        }
        return -1;
    }

    public static int getFullIdByBlockName(String name) {
        return getFullIdByBlockName(name, false);
    }

    public static int getFullIdByBlockName(String name, boolean lookupAlias) {
        return getFullIdByBlockShortName(name.startsWith("minecraft:") ? name.substring(10) : name, lookupAlias);
    }

    public static int getFullIdByBlockShortName(String shortName) {
        return getFullIdByBlockShortName(shortName, false);
    }

    public static int getFullIdByBlockShortName(String shortName, boolean lookupAlias) {
        int id = BLOCK_NAME_TO_ID.getInt(shortName);
        if (id != -1) {
            return Block.getFullId(id);
        }

        if (lookupAlias) {
            String alias = BLOCK_ALIASES_MAP.get(shortName);
            if (alias != null) {
                id = BLOCK_NAME_TO_ID.getInt(alias);
                if (id != -1) {
                    return Block.getFullId(id);
                }
            }

            IntList variants = LEGACY_ALIASES_MAP.get(shortName);
            if (variants != null) {
                id = variants.getInt(0);
                if (id != -1) {
                    return Block.getFullId(id);
                }
            }

            return COMPLEX_ALIASES_MAP.getInt(shortName);
        }
        return -1;
    }

    @Nullable
    public static String getBlockNameById(int id) {
        if (id < 0 || id >= Block.BLOCK_ID_COUNT) {
            return null;
        }
        return ID_TO_BLOCK_NAME[id];
    }

    @Nullable
    public static String getBlockFullNameById(int id) {
        if (id < 0 || id >= Block.BLOCK_ID_COUNT) {
            return null;
        }
        return ID_TO_BLOCK_FULL_NAME[id];
    }

    public static Map<String, String> getBlockAliasesMap() {
        return BLOCK_ALIASES_MAP;
    }

    public static Object2IntMap<String> getBlockItemNameToIdMap() {
        return ITEM_NAME_TO_ID;
    }

    public static Object2IntMap<String> getBlockItemFullNameToIdMap() {
        return ITEM_FULL_NAME_TO_ID;
    }

    public static int getIdByItemName(String name) {
        return getIdByItemName(name, false);
    }

    public static int getIdByItemName(String name, boolean lookupAlias) {
        return getIdByItemShortName(name.startsWith("minecraft:") ? name.substring(10) : name, lookupAlias);
    }

    public static int getIdByItemShortName(String shortName) {
        return getIdByItemShortName(shortName, false);
    }

    public static int getIdByItemShortName(String shortName, boolean lookupAlias) {
        int id = ITEM_NAME_TO_ID.getInt(shortName);
        if (id != -1) {
            return id;
        }

        if (lookupAlias) {
            String alias = ITEM_ALIASES_MAP.get(shortName);
            if (alias != null) {
                return ITEM_NAME_TO_ID.getInt(alias);
            }
        }
        return -1;
    }

    public static int getFullIdByItemName(String name) {
        return getFullIdByItemName(name, false);
    }

    public static int getFullIdByItemName(String name, boolean lookupAlias) {
        return getFullIdByItemShortName(name.startsWith("minecraft:") ? name.substring(10) : name, lookupAlias);
    }

    public static int getFullIdByItemShortName(String shortName) {
        return getFullIdByItemShortName(shortName, false);
    }

    public static int getFullIdByItemShortName(String shortName, boolean lookupAlias) {
        int id = ITEM_NAME_TO_ID.getInt(shortName);
        if (id != -1) {
            return Block.getFullId(id);
        }

        if (lookupAlias) {
            String alias = ITEM_ALIASES_MAP.get(shortName);
            if (alias != null) {
                id = ITEM_NAME_TO_ID.getInt(alias);
                if (id != -1) {
                    return Block.getFullId(id);
                }
            }

            IntList variants = LEGACY_ALIASES_MAP.get(shortName);
            if (variants != null) {
                id = variants.getInt(0);
                if (id != -1) {
                    return Block.getFullId(id);
                }
            }

            return COMPLEX_ALIASES_MAP.getInt(shortName);
        }
        return -1;
    }

    @Nullable
    public static String getItemNameById(int id) {
        if (id < 0 || id >= Block.BLOCK_ID_COUNT) {
            return null;
        }
        return ID_TO_ITEM_NAME[id];
    }

    @Nullable
    public static String getItemFullNameById(int id) {
        if (id < 0 || id >= Block.BLOCK_ID_COUNT) {
            return null;
        }
        return ID_TO_ITEM_FULL_NAME[id];
    }

    public static Map<String, String> getItemAliasesMap() {
        return ITEM_ALIASES_MAP;
    }

    public static Object2IntMap<String> getComplexAliasesMap() {
        return COMPLEX_ALIASES_MAP;
    }

    public static Map<String, IntList> getLegacyAliasesMap() {
        return LEGACY_ALIASES_MAP;
    }

    public static Object2IntMap<String> getDeprecatedLegacyAliasesMap() {
        return DEPRECATED_LEGACY_ALIASES_MAP;
    }

    public static int allocateCustomBlockId() {
        int id = CUSTOM_BLOCK_ID_ALLOCATOR.getAndIncrement();
        if (id >= Block.BLOCK_ID_COUNT) {
            throw new AssertionError("Custom block ID overflow. Please increase the capacity");
        }
        return id;
    }

    public static Block air() {
        return AIR.clone();
    }

    public static Block airUnsafe() {
        return AIR;
    }

    private Blocks() {
        throw new IllegalStateException();
    }
}
