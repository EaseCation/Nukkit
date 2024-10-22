package cn.nukkit.blockentity;

import cn.nukkit.GameVersion;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nullable;

import static cn.nukkit.GameVersion.*;
import static cn.nukkit.blockentity.BlockEntity.*;

@Log4j2
public final class BlockEntities {

    private static final String[] TYPE_TO_ID = new String[BlockEntityType.UNDEFINED];
    private static final BlockEntityFactory[] TYPE_TO_FACTORY = new BlockEntityFactory[BlockEntityType.UNDEFINED];

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
        // ChalkboardBlock
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

//        registerBlockEntity(BlockEntityType.HANGING_SIGN, HANGING_SIGN, BlockEntityHangingSign.class, BlockEntityHangingSign::new, V1_20_0);
//        registerBlockEntity(BlockEntityType.CHISELED_BOOKSHELF, CHISELED_BOOKSHELF, BlockEntityChiseledBookshelf.class, BlockEntityChiseledBookshelf::new, V1_20_0);
//        registerBlockEntity(BlockEntityType.BRUSHABLE_BLOCK, BRUSHABLE_BLOCK, BlockEntityBrushableBlock.class, BlockEntityBrushableBlock::new, V1_20_0);
//        registerBlockEntity(BlockEntityType.DECORATED_POT, DECORATED_POT, BlockEntityDecoratedPot.class, BlockEntityDecoratedPot::new, V1_20_0);
//        registerBlockEntity(BlockEntityType.CALIBRATED_SCULK_SENSOR, CALIBRATED_SCULK_SENSOR, BlockEntityCalibratedSculkSensor.class, BlockEntityCalibratedSculkSensor::new, V1_20_0);

//        registerBlockEntity(BlockEntityType.CRAFTER, CRAFTER, BlockEntityCrafter.class, BlockEntityCrafter::new, V1_21_0);
//        registerBlockEntity(BlockEntityType.TRIAL_SPAWNER, TRIAL_SPAWNER, BlockEntityTrialSpawner.class, BlockEntityTrialSpawner::new, V1_21_0);
//        registerBlockEntity(BlockEntityType.VAULT, VAULT, BlockEntityVault.class, BlockEntityVault::new, V1_21_0);

//        registerBlockEntity(BlockEntityType.CREAKING_HEART, CREAKING_HEART, BlockEntityCreakingHeart.class, BlockEntityCreakingHeart::new, V1_21_50);
    }

    private static Class<? extends BlockEntity> registerBlockEntity(int type, String name, Class<? extends BlockEntity> clazz, BlockEntityFactory factory) {
        if (!BlockEntity.registerBlockEntity(name, clazz)) {
//            return null;
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

    private BlockEntities() {
        throw new IllegalStateException();
    }
}
