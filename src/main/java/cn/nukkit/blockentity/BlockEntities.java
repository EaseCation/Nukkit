package cn.nukkit.blockentity;

import cn.nukkit.GameVersion;
import cn.nukkit.block.Block;
import cn.nukkit.block.Blocks;
import cn.nukkit.blockentity.BlockEntityModBlock.ModBlockEntityDefinition;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static cn.nukkit.GameVersion.*;
import static cn.nukkit.blockentity.BlockEntityID.*;

@Log4j2
public final class BlockEntities {

    private static final Object2IntMap<String> ID_TO_TYPE = new Object2IntOpenHashMap<>();
    private static final String[] TYPE_TO_ID = new String[BlockEntityType.UNDEFINED];
    private static final BlockEntityFactory[] TYPE_TO_FACTORY = new BlockEntityFactory[BlockEntityType.UNDEFINED];

    private static final Object2IntMap<String> CUSTOM_NAME_TO_TYPE = new Object2IntOpenHashMap<>();
    private static final AtomicInteger CUSTOM_TYPE_ALLOCATOR = new AtomicInteger(BlockEntityType.MOD_BLOCK);

    public static void registerVanillaBlockEntities() {
        ID_TO_TYPE.put(MOD_BLOCK, BlockEntityType.MOD_BLOCK);

        registerBlockEntity(BlockEntityType.FURNACE, FURNACE, BlockEntityFurnace::new);
        registerBlockEntity(BlockEntityType.CHEST, CHEST, BlockEntityChest::new);
        registerBlockEntity(BlockEntityType.NETHER_REACTOR, NETHER_REACTOR, BlockEntityNetherReactor::new);
        registerBlockEntity(BlockEntityType.SIGN, SIGN, BlockEntitySign::new);
        registerBlockEntity(BlockEntityType.MOB_SPAWNER, MOB_SPAWNER, BlockEntityMobSpawner::new);
        registerBlockEntity(BlockEntityType.SKULL, SKULL, BlockEntitySkull::new);
        registerBlockEntity(BlockEntityType.FLOWER_POT, FLOWER_POT, BlockEntityFlowerPot::new);
        registerBlockEntity(BlockEntityType.BREWING_STAND, BREWING_STAND, BlockEntityBrewingStand::new);
        registerBlockEntity(BlockEntityType.ENCHANT_TABLE, ENCHANT_TABLE, BlockEntityEnchantTable::new);
        registerBlockEntity(BlockEntityType.DAYLIGHT_DETECTOR, DAYLIGHT_DETECTOR, BlockEntityDaylightDetector::new);
        registerBlockEntity(BlockEntityType.MUSIC, MUSIC, BlockEntityMusic::new);
        registerBlockEntity(BlockEntityType.COMPARATOR, COMPARATOR, BlockEntityComparator::new);
        registerBlockEntity(BlockEntityType.DISPENSER, DISPENSER, BlockEntityDispenser::new);
        registerBlockEntity(BlockEntityType.DROPPER, DROPPER, BlockEntityDropper::new);
        registerBlockEntity(BlockEntityType.HOPPER, HOPPER, BlockEntityHopper::new);
        registerBlockEntity(BlockEntityType.CAULDRON, CAULDRON, BlockEntityCauldron::new);
        registerBlockEntity(BlockEntityType.ITEM_FRAME, ITEM_FRAME, BlockEntityItemFrame::new);
        registerBlockEntity(BlockEntityType.PISTON_ARM, PISTON_ARM, BlockEntityPistonArm::new);
        registerBlockEntity(BlockEntityType.MOVING_BLOCK, MOVING_BLOCK, BlockEntityMovingBlock::new);
        registerBlockEntity(BlockEntityType.BEACON, BEACON, BlockEntityBeacon::new);
        registerBlockEntity(BlockEntityType.END_PORTAL, END_PORTAL, BlockEntityEndPortal::new);
        registerBlockEntity(BlockEntityType.ENDER_CHEST, ENDER_CHEST, BlockEntityEnderChest::new);
        registerBlockEntity(BlockEntityType.END_GATEWAY, END_GATEWAY, BlockEntityEndGateway::new);
        registerBlockEntity(BlockEntityType.SHULKER_BOX, SHULKER_BOX, BlockEntityShulkerBox::new);
        registerBlockEntity(BlockEntityType.COMMAND_BLOCK, COMMAND_BLOCK, BlockEntityCommandBlock::new);
        registerBlockEntity(BlockEntityType.BED, BED, BlockEntityBed::new);
        registerBlockEntity(BlockEntityType.BANNER, BANNER, BlockEntityBanner::new);
        registerBlockEntity(BlockEntityType.STRUCTURE_BLOCK, STRUCTURE_BLOCK, BlockEntityStructureBlock::new);
        registerBlockEntity(BlockEntityType.JUKEBOX, JUKEBOX, BlockEntityJukebox::new);
        registerBlockEntity(BlockEntityType.CHEMISTRY_TABLE, CHEMISTRY_TABLE, BlockEntityChemistryTable::new);

        registerBlockEntity(BlockEntityType.CONDUIT, CONDUIT, BlockEntityConduit::new, V1_5_0);

        registerBlockEntity(BlockEntityType.JIGSAW_BLOCK, JIGSAW_BLOCK, BlockEntityJigsawBlock::new, V1_10_0);
        registerBlockEntity(BlockEntityType.LECTERN, LECTERN, BlockEntityLectern::new, V1_10_0);

        registerBlockEntity(BlockEntityType.BLAST_FURNACE, BLAST_FURNACE, BlockEntityBlastFurnace::new, V1_11_0);
        registerBlockEntity(BlockEntityType.SMOKER, SMOKER, BlockEntitySmoker::new, V1_11_0);
        registerBlockEntity(BlockEntityType.BELL, BELL, BlockEntityBell::new, V1_11_0);
        registerBlockEntity(BlockEntityType.CAMPFIRE, CAMPFIRE, BlockEntityCampfire::new, V1_11_0);
        registerBlockEntity(BlockEntityType.BARREL, BARREL, BlockEntityBarrel::new, V1_11_0);

        registerBlockEntity(BlockEntityType.BEEHIVE, BEEHIVE, BlockEntityBeehive::new, V1_14_0);

        registerBlockEntity(BlockEntityType.LODESTONE, LODESTONE, BlockEntityLodestone::new, V1_16_0);

        registerBlockEntity(BlockEntityType.SPORE_BLOSSOM, SPORE_BLOSSOM, BlockEntitySporeBlossom::new, V1_17_0);
        registerBlockEntity(BlockEntityType.GLOW_ITEM_FRAME, GLOW_ITEM_FRAME, BlockEntityGlowItemFrame::new, V1_17_0);

        registerBlockEntity(BlockEntityType.SCULK_SENSOR, SCULK_SENSOR, BlockEntitySculkSensor::new, V1_19_0);
        registerBlockEntity(BlockEntityType.SCULK_CATALYST, SCULK_CATALYST, BlockEntitySculkCatalyst::new, V1_19_0);
        registerBlockEntity(BlockEntityType.SCULK_SHRIEKER, SCULK_SHRIEKER, BlockEntitySculkShrieker::new, V1_19_0);

        registerBlockEntity(BlockEntityType.HANGING_SIGN, HANGING_SIGN, BlockEntityHangingSign::new, V1_20_0);
        registerBlockEntity(BlockEntityType.CHISELED_BOOKSHELF, CHISELED_BOOKSHELF, BlockEntityChiseledBookshelf::new, V1_20_0);
        registerBlockEntity(BlockEntityType.BRUSHABLE_BLOCK, BRUSHABLE_BLOCK, BlockEntityBrushableBlock::new, V1_20_0);
        registerBlockEntity(BlockEntityType.DECORATED_POT, DECORATED_POT, BlockEntityDecoratedPot::new, V1_20_0);
        registerBlockEntity(BlockEntityType.CALIBRATED_SCULK_SENSOR, CALIBRATED_SCULK_SENSOR, BlockEntityCalibratedSculkSensor::new, V1_20_0);

        registerBlockEntity(BlockEntityType.CRAFTER, CRAFTER, BlockEntityCrafter::new, V1_21_0);
        registerBlockEntity(BlockEntityType.TRIAL_SPAWNER, TRIAL_SPAWNER, BlockEntityTrialSpawner::new, V1_21_0);
        registerBlockEntity(BlockEntityType.VAULT, VAULT, BlockEntityVault::new, V1_21_0);

        registerBlockEntity(BlockEntityType.CHALKBOARD_BLOCK, CHALKBOARD_BLOCK, BlockEntityChalkboard::new, V1_21_50);
        registerBlockEntity(BlockEntityType.CREAKING_HEART, CREAKING_HEART, BlockEntityCreakingHeart::new, V1_21_50);

        registerBlockEntity(BlockEntityType.COPPER_GOLEM_STATUE, COPPER_GOLEM_STATUE, BlockEntityCopperGolemStatue::new, V1_21_111);
        registerBlockEntity(BlockEntityType.SHELF, SHELF, BlockEntityShelf::new, V1_21_111);
    }

    private static boolean registerBlockEntity(int type, String name, BlockEntityFactory factory) {
        if (ID_TO_TYPE.putIfAbsent(name, type) != 0) {
            throw new IllegalArgumentException("Duplicate block entity ID: " + name);
        }
        if (TYPE_TO_ID[type] != null) {
            throw new IllegalArgumentException("Duplicate block entity type: " + type);
        }
        TYPE_TO_ID[type] = name;
        TYPE_TO_FACTORY[type] = factory;
        return true;
    }

    /**
     * @param version min required base game version
     */
    private static boolean registerBlockEntity(int type, String name, BlockEntityFactory factory, GameVersion version) {
        if (!version.isAvailable()) {
//            return false;
        }
        return registerBlockEntity(type, name, factory);
    }

    public static int registerCustomBlockEntity(int blockId) {
        return registerCustomBlockEntity(blockId, true);
    }

    public static int registerCustomBlockEntity(int blockId, Consumer<BlockEntityModBlock> serverTick) {
        return registerCustomBlockEntity(blockId, true, serverTick);
    }

    public static int registerCustomBlockEntity(int blockId, boolean movable) {
        return registerCustomBlockEntity(blockId, movable, false);
    }

    public static int registerCustomBlockEntity(int blockId, boolean movable, Consumer<BlockEntityModBlock> serverTick) {
        return registerCustomBlockEntity(blockId, movable, serverTick, false);
    }

    public static int registerCustomBlockEntity(int blockId, boolean movable, boolean serverTick) {
        return registerCustomBlockEntity(blockId, movable, serverTick, false);
    }

    public static int registerCustomBlockEntity(int blockId, boolean movable, boolean serverTick, boolean clientTick) {
        return registerCustomBlockEntity(blockId, movable, serverTick ? BlockEntityModBlock::tickEvent : null, clientTick);
    }

    /**
     * @return block entity type
     */
    public static int registerCustomBlockEntity(int blockId, boolean movable, Consumer<BlockEntityModBlock> serverTick, boolean clientTick) {
        if (blockId < Block.CUSTOM_BLOCK_FIRST_ID_NEW) {
            throw new IllegalArgumentException("Only custom blocks can be registered for custom block entities: " + blockId);
        }
        String blockName = Blocks.getBlockFullNameById(blockId);
        if (blockName == null) {
            throw new IllegalArgumentException("Unknown block ID: " + blockId);
        }
        int type = CUSTOM_TYPE_ALLOCATOR.incrementAndGet();
        if (type >= BlockEntityType.UNDEFINED) {
            throw new AssertionError("Custom block entity type ID overflow. Please increase the capacity");
        }
        if (TYPE_TO_ID[type] != null) {
            throw new IllegalArgumentException("Duplicate block entity type: " + type);
        }

        CUSTOM_NAME_TO_TYPE.put(blockName, type);
        TYPE_TO_ID[type] = MOD_BLOCK;
        ModBlockEntityDefinition definition = ModBlockEntityDefinition.builder()
                .blockEntityType(type)
                .blockId(blockId)
                .blockName(blockName)
                .movable(movable)
                .serverTick(serverTick)
                .clientTick(clientTick)
                .build();
        TYPE_TO_FACTORY[type] = (chunk, nbt) -> new BlockEntityModBlock(chunk, nbt, definition);
        log.trace("Register custom block entity: {}", definition);
        return type;
    }

    @Nullable
    public static String getIdByType(int type) {
        if (type <= 0 || type >= BlockEntityType.UNDEFINED) {
            return null;
        }
        return TYPE_TO_ID[type];
    }

    @Nullable
    public static BlockEntityFactory getFactoryByType(int type) {
        if (type <= 0 || type >= BlockEntityType.UNDEFINED) {
            return null;
        }
        return TYPE_TO_FACTORY[type];
    }

    public static BlockEntity createBlockEntity(int type, FullChunk chunk, CompoundTag nbt) {
        BlockEntityFactory factory = getFactoryByType(type);
        if (factory == null) {
            return null;
        }

        try {
            return factory.create(chunk, nbt);
        } catch (Exception e) {
            log.error("Failed to create block entity: {}", type, e);
        }
        return null;
    }

    public static BlockEntity createBlockEntity(String id, FullChunk chunk, CompoundTag nbt) {
        int type = ID_TO_TYPE.getInt(id);
        if (type == BlockEntityType.MOD_BLOCK) {
            type = CUSTOM_NAME_TO_TYPE.getInt(nbt.getString("_blockName"));
        }
        if (type == 0) {
            return null;
        }
        return createBlockEntity(type, chunk, nbt);
    }

    private BlockEntities() {
        throw new IllegalStateException();
    }
}
