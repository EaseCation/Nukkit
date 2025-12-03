package cn.nukkit.blockentity;

import cn.nukkit.GameVersion;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nullable;

import static cn.nukkit.GameVersion.*;
import static cn.nukkit.blockentity.BlockEntityID.*;

@Log4j2
public final class BlockEntities {

    private static final Object2IntMap<String> ID_TO_TYPE = new Object2IntOpenHashMap<>();
    private static final String[] TYPE_TO_ID = new String[BlockEntityType.UNDEFINED];
    private static final BlockEntityFactory[] TYPE_TO_FACTORY = new BlockEntityFactory[BlockEntityType.UNDEFINED];

    /**
     * 自定义方块实体起始 ID。
     * 原生方块实体类型 ID 范围为 1-60（定义在 BlockEntityType 中），
     * 自定义类型从 1000 开始，确保与原生 ID 完全隔离，避免冲突。
     */
    public static final int CUSTOM_TYPE_START = 1000;

    // 自定义方块实体的动态存储（使用 Map 而非数组，支持动态扩展）
    private static final Object2IntMap<String> CUSTOM_ID_TO_TYPE = new Object2IntOpenHashMap<>();
    private static final Int2ObjectMap<String> CUSTOM_TYPE_TO_ID = new Int2ObjectOpenHashMap<>();
    private static final Int2ObjectMap<BlockEntityFactory> CUSTOM_TYPE_TO_FACTORY = new Int2ObjectOpenHashMap<>();
    private static int nextCustomType = CUSTOM_TYPE_START;

    static {
        // 设置默认返回值，用于区分"未找到"和"有效值 0"
        CUSTOM_ID_TO_TYPE.defaultReturnValue(-1);
    }

    public static void registerVanillaBlockEntities() {
        registerBlockEntity(BlockEntityType.FURNACE, FURNACE, BlockEntityFurnace.class, BlockEntityFurnace::new);
        registerBlockEntity(BlockEntityType.CHEST, CHEST, BlockEntityChest.class, BlockEntityChest::new);
        registerBlockEntity(BlockEntityType.NETHER_REACTOR, NETHER_REACTOR, BlockEntityNetherReactor.class, BlockEntityNetherReactor::new);
        registerBlockEntity(BlockEntityType.SIGN, SIGN, BlockEntitySign.class, BlockEntitySign::new);
        registerBlockEntity(BlockEntityType.MOB_SPAWNER, MOB_SPAWNER, BlockEntityMobSpawner.class, BlockEntityMobSpawner::new);
        registerBlockEntity(BlockEntityType.SKULL, SKULL, BlockEntitySkull.class, BlockEntitySkull::new);
        registerBlockEntity(BlockEntityType.FLOWER_POT, FLOWER_POT, BlockEntityFlowerPot.class, BlockEntityFlowerPot::new);
        registerBlockEntity(BlockEntityType.BREWING_STAND, BREWING_STAND, BlockEntityBrewingStand.class, BlockEntityBrewingStand::new);
        registerBlockEntity(BlockEntityType.ENCHANT_TABLE, ENCHANT_TABLE, BlockEntityEnchantTable.class, BlockEntityEnchantTable::new);
        registerBlockEntity(BlockEntityType.DAYLIGHT_DETECTOR, DAYLIGHT_DETECTOR, BlockEntityDaylightDetector.class, BlockEntityDaylightDetector::new);
        registerBlockEntity(BlockEntityType.MUSIC, MUSIC, BlockEntityMusic.class, BlockEntityMusic::new);
        registerBlockEntity(BlockEntityType.COMPARATOR, COMPARATOR, BlockEntityComparator.class, BlockEntityComparator::new);
        registerBlockEntity(BlockEntityType.DISPENSER, DISPENSER, BlockEntityDispenser.class, BlockEntityDispenser::new);
        registerBlockEntity(BlockEntityType.DROPPER, DROPPER, BlockEntityDropper.class, BlockEntityDropper::new);
        registerBlockEntity(BlockEntityType.HOPPER, HOPPER, BlockEntityHopper.class, BlockEntityHopper::new);
        registerBlockEntity(BlockEntityType.CAULDRON, CAULDRON, BlockEntityCauldron.class, BlockEntityCauldron::new);
        registerBlockEntity(BlockEntityType.ITEM_FRAME, ITEM_FRAME, BlockEntityItemFrame.class, BlockEntityItemFrame::new);
        registerBlockEntity(BlockEntityType.PISTON_ARM, PISTON_ARM, BlockEntityPistonArm.class, BlockEntityPistonArm::new);
        registerBlockEntity(BlockEntityType.MOVING_BLOCK, MOVING_BLOCK, BlockEntityMovingBlock.class, BlockEntityMovingBlock::new);
        registerBlockEntity(BlockEntityType.BEACON, BEACON, BlockEntityBeacon.class, BlockEntityBeacon::new);
        registerBlockEntity(BlockEntityType.END_PORTAL, END_PORTAL, BlockEntityEndPortal.class, BlockEntityEndPortal::new);
        registerBlockEntity(BlockEntityType.ENDER_CHEST, ENDER_CHEST, BlockEntityEnderChest.class, BlockEntityEnderChest::new);
        registerBlockEntity(BlockEntityType.END_GATEWAY, END_GATEWAY, BlockEntityEndGateway.class, BlockEntityEndGateway::new);
        registerBlockEntity(BlockEntityType.SHULKER_BOX, SHULKER_BOX, BlockEntityShulkerBox.class, BlockEntityShulkerBox::new);
        registerBlockEntity(BlockEntityType.COMMAND_BLOCK, COMMAND_BLOCK, BlockEntityCommandBlock.class, BlockEntityCommandBlock::new);
        registerBlockEntity(BlockEntityType.BED, BED, BlockEntityBed.class, BlockEntityBed::new);
        registerBlockEntity(BlockEntityType.BANNER, BANNER, BlockEntityBanner.class, BlockEntityBanner::new);
        registerBlockEntity(BlockEntityType.STRUCTURE_BLOCK, STRUCTURE_BLOCK, BlockEntityStructureBlock.class, BlockEntityStructureBlock::new);
        registerBlockEntity(BlockEntityType.JUKEBOX, JUKEBOX, BlockEntityJukebox.class, BlockEntityJukebox::new);
        registerBlockEntity(BlockEntityType.CHEMISTRY_TABLE, CHEMISTRY_TABLE, BlockEntityChemistryTable.class, BlockEntityChemistryTable::new);

        registerBlockEntity(BlockEntityType.CONDUIT, CONDUIT, BlockEntityConduit.class, BlockEntityConduit::new, V1_5_0);

        registerBlockEntity(BlockEntityType.JIGSAW_BLOCK, JIGSAW_BLOCK, BlockEntityJigsawBlock.class, BlockEntityJigsawBlock::new, V1_10_0);
        registerBlockEntity(BlockEntityType.LECTERN, LECTERN, BlockEntityLectern.class, BlockEntityLectern::new, V1_10_0);

        registerBlockEntity(BlockEntityType.BLAST_FURNACE, BLAST_FURNACE, BlockEntityBlastFurnace.class, BlockEntityBlastFurnace::new, V1_11_0);
        registerBlockEntity(BlockEntityType.SMOKER, SMOKER, BlockEntitySmoker.class, BlockEntitySmoker::new, V1_11_0);
        registerBlockEntity(BlockEntityType.BELL, BELL, BlockEntityBell.class, BlockEntityBell::new, V1_11_0);
        registerBlockEntity(BlockEntityType.CAMPFIRE, CAMPFIRE, BlockEntityCampfire.class, BlockEntityCampfire::new, V1_11_0);
        registerBlockEntity(BlockEntityType.BARREL, BARREL, BlockEntityBarrel.class, BlockEntityBarrel::new, V1_11_0);

        registerBlockEntity(BlockEntityType.BEEHIVE, BEEHIVE, BlockEntityBeehive.class, BlockEntityBeehive::new, V1_14_0);

        registerBlockEntity(BlockEntityType.LODESTONE, LODESTONE, BlockEntityLodestone.class, BlockEntityLodestone::new, V1_16_0);

        registerBlockEntity(BlockEntityType.SPORE_BLOSSOM, SPORE_BLOSSOM, BlockEntitySporeBlossom.class, BlockEntitySporeBlossom::new, V1_17_0);
        registerBlockEntity(BlockEntityType.GLOW_ITEM_FRAME, GLOW_ITEM_FRAME, BlockEntityGlowItemFrame.class, BlockEntityGlowItemFrame::new, V1_17_0);

        registerBlockEntity(BlockEntityType.SCULK_SENSOR, SCULK_SENSOR, BlockEntitySculkSensor.class, BlockEntitySculkSensor::new, V1_19_0);
        registerBlockEntity(BlockEntityType.SCULK_CATALYST, SCULK_CATALYST, BlockEntitySculkCatalyst.class, BlockEntitySculkCatalyst::new, V1_19_0);
        registerBlockEntity(BlockEntityType.SCULK_SHRIEKER, SCULK_SHRIEKER, BlockEntitySculkShrieker.class, BlockEntitySculkShrieker::new, V1_19_0);

        registerBlockEntity(BlockEntityType.HANGING_SIGN, HANGING_SIGN, BlockEntityHangingSign.class, BlockEntityHangingSign::new, V1_20_0);
        registerBlockEntity(BlockEntityType.CHISELED_BOOKSHELF, CHISELED_BOOKSHELF, BlockEntityChiseledBookshelf.class, BlockEntityChiseledBookshelf::new, V1_20_0);
        registerBlockEntity(BlockEntityType.BRUSHABLE_BLOCK, BRUSHABLE_BLOCK, BlockEntityBrushableBlock.class, BlockEntityBrushableBlock::new, V1_20_0);
        registerBlockEntity(BlockEntityType.DECORATED_POT, DECORATED_POT, BlockEntityDecoratedPot.class, BlockEntityDecoratedPot::new, V1_20_0);
        registerBlockEntity(BlockEntityType.CALIBRATED_SCULK_SENSOR, CALIBRATED_SCULK_SENSOR, BlockEntityCalibratedSculkSensor.class, BlockEntityCalibratedSculkSensor::new, V1_20_0);

        registerBlockEntity(BlockEntityType.CRAFTER, CRAFTER, BlockEntityCrafter.class, BlockEntityCrafter::new, V1_21_0);
        registerBlockEntity(BlockEntityType.TRIAL_SPAWNER, TRIAL_SPAWNER, BlockEntityTrialSpawner.class, BlockEntityTrialSpawner::new, V1_21_0);
        registerBlockEntity(BlockEntityType.VAULT, VAULT, BlockEntityVault.class, BlockEntityVault::new, V1_21_0);

        registerBlockEntity(BlockEntityType.CHALKBOARD_BLOCK, CHALKBOARD_BLOCK, BlockEntityChalkboard.class, BlockEntityChalkboard::new, V1_21_50);
        registerBlockEntity(BlockEntityType.CREAKING_HEART, CREAKING_HEART, BlockEntityCreakingHeart.class, BlockEntityCreakingHeart::new, V1_21_50);

        registerBlockEntity(BlockEntityType.COPPER_GOLEM_STATUE, COPPER_GOLEM_STATUE, BlockEntityCopperGolemStatue.class, BlockEntityCopperGolemStatue::new, V1_21_111);
        registerBlockEntity(BlockEntityType.SHELF, SHELF, BlockEntityShelf.class, BlockEntityShelf::new, V1_21_111);
    }

    private static Class<? extends BlockEntity> registerBlockEntity(int type, String name, Class<? extends BlockEntity> clazz, BlockEntityFactory factory) {
        if (ID_TO_TYPE.putIfAbsent(name, type) != 0) {
            throw new IllegalArgumentException("Duplicate block entity ID: " + name);
        }
        if (TYPE_TO_ID[type] != null) {
            throw new IllegalArgumentException("Duplicate block entity type: " + type);
        }
        TYPE_TO_ID[type] = name;
        TYPE_TO_FACTORY[type] = factory;
        return clazz;
    }

    /**
     * @param version min required base game version
     */
    private static Class<? extends BlockEntity> registerBlockEntity(int type, String name, Class<? extends BlockEntity> clazz, BlockEntityFactory factory, GameVersion version) {
        if (!version.isAvailable()) {
//            return null;
        }
        return registerBlockEntity(type, name, clazz, factory);
    }

    /**
     * 注册自定义方块实体类型（供外部插件使用）。
     * <p>
     * 此方法自动分配一个唯一的类型 ID（>= {@link #CUSTOM_TYPE_START}），
     * 并将其与提供的字符串标识符和工厂关联。
     * <p>
     *
     * @param name 方块实体字符串标识符
     * @param clazz 方块实体类（用于类型检查，目前未强制使用）
     * @param factory 方块实体工厂，用于创建方块实体实例
     * @return 分配的类型 ID（>= 1000）
     * @throws IllegalArgumentException 如果 name 为 null、空字符串或已被注册
     */
    public static int registerCustomBlockEntity(String name, Class<? extends BlockEntity> clazz, BlockEntityFactory factory) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Block entity name cannot be null or empty");
        }
        if (factory == null) {
            throw new IllegalArgumentException("Block entity factory cannot be null");
        }
        if (CUSTOM_ID_TO_TYPE.containsKey(name) || ID_TO_TYPE.containsKey(name)) {
            throw new IllegalArgumentException("Block entity ID already registered: " + name);
        }

        int type = nextCustomType++;
        CUSTOM_ID_TO_TYPE.put(name, type);
        CUSTOM_TYPE_TO_ID.put(type, name);
        CUSTOM_TYPE_TO_FACTORY.put(type, factory);

        if (log.isTraceEnabled()) {
            log.trace("Registered custom block entity: {} -> type {} (class: {})", name, type, clazz.getName());
        }

        return type;
    }

    /**
     * 检查给定的类型 ID 是否为自定义方块实体类型。
     *
     * @param type 类型 ID
     * @return 如果 type >= {@link #CUSTOM_TYPE_START} 则返回 true
     */
    public static boolean isCustomType(int type) {
        return type >= CUSTOM_TYPE_START;
    }

    /**
     * 获取下一个可用的自定义方块实体类型 ID（不分配）。
     * <p>
     * 此方法用于预览下一个将分配的 ID，不会递增计数器。
     *
     * @return 下一个可用的类型 ID
     */
    public static int peekNextCustomType() {
        return nextCustomType;
    }

    /**
     * 根据类型 ID 获取方块实体字符串标识符。
     *
     * @param type 方块实体类型 ID
     * @return 字符串标识符，如果类型无效或未注册则返回 null
     */
    @Nullable
    public static String getIdByType(int type) {
        if (type >= CUSTOM_TYPE_START) {
            return CUSTOM_TYPE_TO_ID.get(type);
        }
        if (type <= 0 || type >= BlockEntityType.UNDEFINED) {
            return null;
        }
        return TYPE_TO_ID[type];
    }

    /**
     * 根据字符串标识符获取方块实体类型 ID。
     *
     * @param id 方块实体字符串标识符
     * @return 类型 ID，如果未找到则返回 0
     */
    public static int getTypeById(String id) {
        if (id == null) {
            return 0;
        }
        // 优先查询原生类型
        int type = ID_TO_TYPE.getInt(id);
        if (type != 0) {
            return type;
        }
        // 查询自定义类型（defaultReturnValue 已设为 -1，所以需要转换）
        int customType = CUSTOM_ID_TO_TYPE.getInt(id);
        return Math.max(customType, 0);
    }

    /**
     * 根据类型 ID 获取方块实体工厂。
     *
     * @param type 方块实体类型 ID
     * @return 方块实体工厂，如果类型无效或未注册则返回 null
     */
    @Nullable
    public static BlockEntityFactory getFactoryByType(int type) {
        if (type >= CUSTOM_TYPE_START) {
            return CUSTOM_TYPE_TO_FACTORY.get(type);
        }
        if (type <= 0 || type >= BlockEntityType.UNDEFINED) {
            return null;
        }
        return TYPE_TO_FACTORY[type];
    }

    /**
     * 使用类型 ID 创建方块实体。
     *
     * @param type 方块实体类型 ID
     * @param chunk 方块所在的区块
     * @param nbt 方块实体的 NBT 数据
     * @return 创建的方块实体实例，如果类型无效或创建失败则返回 null
     */
    @Nullable
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

    /**
     * 使用字符串标识符创建方块实体。
     *
     * @param id 方块实体字符串标识符
     * @param chunk 方块所在的区块
     * @param nbt 方块实体的 NBT 数据
     * @return 创建的方块实体实例，如果标识符无效或创建失败则返回 null
     */
    @Nullable
    public static BlockEntity createBlockEntity(String id, FullChunk chunk, CompoundTag nbt) {
        int type = getTypeById(id);
        if (type == 0) {
            return null;
        }
        return createBlockEntity(type, chunk, nbt);
    }

    private BlockEntities() {
        throw new IllegalStateException();
    }
}
