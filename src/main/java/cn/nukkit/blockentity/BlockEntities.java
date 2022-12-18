package cn.nukkit.blockentity;

import cn.nukkit.GameVersion;

import javax.annotation.Nullable;

import static cn.nukkit.GameVersion.*;
import static cn.nukkit.blockentity.BlockEntity.*;

public final class BlockEntities {

    private static final String[] TYPE_TO_ID = new String[BlockEntityType.UNDEFINED];

    public static void registerVanillaBlockEntities() {
        registerBlockEntity(BlockEntityType.FURNACE, FURNACE, BlockEntityFurnace.class);
        registerBlockEntity(BlockEntityType.CHEST, CHEST, BlockEntityChest.class);
        registerBlockEntity(BlockEntityType.NETHER_REACTOR, NETHER_REACTOR, BlockEntityNetherReactor.class);
        registerBlockEntity(BlockEntityType.SIGN, SIGN, BlockEntitySign.class);
        registerBlockEntity(BlockEntityType.MOB_SPAWNER, MOB_SPAWNER, BlockEntityMobSpawner.class);
        registerBlockEntity(BlockEntityType.SKULL, SKULL, BlockEntitySkull.class);
        registerBlockEntity(BlockEntityType.FLOWER_POT, FLOWER_POT, BlockEntityFlowerPot.class);
        registerBlockEntity(BlockEntityType.BREWING_STAND, BREWING_STAND, BlockEntityBrewingStand.class);
        registerBlockEntity(BlockEntityType.ENCHANT_TABLE, ENCHANT_TABLE, BlockEntityEnchantTable.class);
        registerBlockEntity(BlockEntityType.DAYLIGHT_DETECTOR, DAYLIGHT_DETECTOR, BlockEntityDaylightDetector.class);
        registerBlockEntity(BlockEntityType.MUSIC, MUSIC, BlockEntityMusic.class);
        registerBlockEntity(BlockEntityType.COMPARATOR, COMPARATOR, BlockEntityComparator.class);
        registerBlockEntity(BlockEntityType.DISPENSER, DISPENSER, BlockEntityDispenser.class);
        registerBlockEntity(BlockEntityType.DROPPER, DROPPER, BlockEntityDropper.class);
        registerBlockEntity(BlockEntityType.HOPPER, HOPPER, BlockEntityHopper.class);
        registerBlockEntity(BlockEntityType.CAULDRON, CAULDRON, BlockEntityCauldron.class);
        registerBlockEntity(BlockEntityType.ITEM_FRAME, ITEM_FRAME, BlockEntityItemFrame.class);
        registerBlockEntity(BlockEntityType.PISTON_ARM, PISTON_ARM, BlockEntityPistonArm.class);
        registerBlockEntity(BlockEntityType.MOVING_BLOCK, MOVING_BLOCK, BlockEntityMovingBlock.class);
        registerBlockEntity(BlockEntityType.BEACON, BEACON, BlockEntityBeacon.class);
        registerBlockEntity(BlockEntityType.END_PORTAL, END_PORTAL, BlockEntityEndPortal.class);
        registerBlockEntity(BlockEntityType.ENDER_CHEST, ENDER_CHEST, BlockEntityEnderChest.class);
        registerBlockEntity(BlockEntityType.END_GATEWAY, END_GATEWAY, BlockEntityEndGateway.class);
        registerBlockEntity(BlockEntityType.SHULKER_BOX, SHULKER_BOX, BlockEntityShulkerBox.class);
        registerBlockEntity(BlockEntityType.COMMAND_BLOCK, COMMAND_BLOCK, BlockEntityCommandBlock.class);
        registerBlockEntity(BlockEntityType.BED, BED, BlockEntityBed.class);
        registerBlockEntity(BlockEntityType.BANNER, BANNER, BlockEntityBanner.class);
        // ChalkboardBlock
        registerBlockEntity(BlockEntityType.STRUCTURE_BLOCK, STRUCTURE_BLOCK, BlockEntityStructureBlock.class);
        registerBlockEntity(BlockEntityType.JUKEBOX, JUKEBOX, BlockEntityJukebox.class);
        registerBlockEntity(BlockEntityType.CHEMISTRY_TABLE, CHEMISTRY_TABLE, BlockEntityChemistryTable.class);

        registerBlockEntity(BlockEntityType.CONDUIT, CONDUIT, BlockEntityConduit.class, V1_5_0);

        registerBlockEntity(BlockEntityType.JIGSAW_BLOCK, JIGSAW_BLOCK, BlockEntityJigsawBlock.class, V1_10_0);
        registerBlockEntity(BlockEntityType.LECTERN, LECTERN, BlockEntityLectern.class, V1_10_0);

        registerBlockEntity(BlockEntityType.BLAST_FURNACE, BLAST_FURNACE, BlockEntityBlastFurnace.class, V1_11_0);
        registerBlockEntity(BlockEntityType.SMOKER, SMOKER, BlockEntitySmoker.class, V1_11_0);
        registerBlockEntity(BlockEntityType.BELL, BELL, BlockEntityBell.class, V1_11_0);
        registerBlockEntity(BlockEntityType.CAMPFIRE, CAMPFIRE, BlockEntityCampfire.class, V1_11_0);
        registerBlockEntity(BlockEntityType.BARREL, BARREL, BlockEntityBarrel.class, V1_11_0);

        registerBlockEntity(BlockEntityType.BEEHIVE, BEEHIVE, BlockEntityBeehive.class, V1_14_0);

        registerBlockEntity(BlockEntityType.LODESTONE, LODESTONE, BlockEntityLodestone.class, V1_16_0);

        registerBlockEntity(BlockEntityType.SPORE_BLOSSOM, SPORE_BLOSSOM, BlockEntitySporeBlossom.class, V1_17_0);
        registerBlockEntity(BlockEntityType.GLOW_ITEM_FRAME, GLOW_ITEM_FRAME, BlockEntityGlowItemFrame.class, V1_17_0);

        registerBlockEntity(BlockEntityType.SCULK_SENSOR, SCULK_SENSOR, BlockEntitySculkSensor.class, V1_19_0);
        registerBlockEntity(BlockEntityType.SCULK_CATALYST, SCULK_CATALYST, BlockEntitySculkCatalyst.class, V1_19_0);
        registerBlockEntity(BlockEntityType.SCULK_SHRIEKER, SCULK_SHRIEKER, BlockEntitySculkShrieker.class, V1_19_0);

//        registerBlockEntity(BlockEntityType.HANGING_SIGN, HANGING_SIGN, BlockEntityHangingSign.class, V1_20_0);
//        registerBlockEntity(BlockEntityType.CHISELED_BOOKSHELF, CHISELED_BOOKSHELF, BlockEntityChiseledBookshelf.class, V1_20_0);
    }

    private static Class<? extends BlockEntity> registerBlockEntity(int type, String name, Class<? extends BlockEntity> clazz) {
        if (!BlockEntity.registerBlockEntity(name, clazz)) {
//            return null;
        }
        TYPE_TO_ID[type] = name;
        return clazz;
    }

    /**
     * @param version min required base game version
     */
    private static Class<? extends BlockEntity> registerBlockEntity(int type, String name, Class<? extends BlockEntity> clazz, GameVersion version) {
        if (!version.isAvailable()) {
//            return null;
        }
        return registerBlockEntity(type, name, clazz);
    }

    @Nullable
    public static String getIdByType(int type) {
        if (type <= 0 || type >= BlockEntityType.UNDEFINED) {
            return null;
        }
        return TYPE_TO_ID[type];
    }

    private BlockEntities() {
        throw new IllegalStateException();
    }
}
