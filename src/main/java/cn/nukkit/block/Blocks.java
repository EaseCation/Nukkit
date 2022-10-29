package cn.nukkit.block;

import cn.nukkit.GameVersion;
import cn.nukkit.block.edu.*;

import static cn.nukkit.GameVersion.*;
import static cn.nukkit.block.BlockID.*;

public final class Blocks {
    static final Block AIR = new BlockAir();

    public static void registerVanillaBlocks() {
        registerBlock(COMMAND_BLOCK, BlockCommand.class);
        registerBlock(REPEATING_COMMAND_BLOCK, BlockCommandRepeating.class);
        registerBlock(CHAIN_COMMAND_BLOCK, BlockCommandChain.class);
        registerBlock(HARD_GLASS_PANE, BlockGlassPaneHard.class);
        registerBlock(HARD_STAINED_GLASS_PANE, BlockGlassPaneStainedHard.class);
        registerBlock(CHEMICAL_HEAT, BlockChemicalHeat.class);
        registerBlock(COLORED_TORCH_RG, BlockTorchColoredRedGreen.class);
        registerBlock(COLORED_TORCH_BP, BlockTorchColoredBluePurple.class);
        registerBlock(FROSTED_ICE, BlockIceFrosted.class); //TODO: melt
        registerBlock(CHEMISTRY_TABLE, BlockChemistryTable.class);
        registerBlock(UNDERWATER_TORCH, BlockTorchUnderwater.class);
        registerBlock(INFO_UPDATE, BlockInfoUpdate.class);
        registerBlock(INFO_UPDATE2, BlockInfoUpdate2.class);
        registerBlock(STRUCTURE_BLOCK, BlockStructure.class);
        registerBlock(HARD_GLASS, BlockGlassHard.class);
        registerBlock(HARD_STAINED_GLASS, BlockGlassStainedHard.class);
        registerBlock(RESERVED6, BlockReserved6.class);

        registerBlock(PRISMARINE_STAIRS, BlockStairsPrismarine.class, V1_4_0);
        registerBlock(DARK_PRISMARINE_STAIRS, BlockStairsDarkPrismarine.class, V1_4_0);
        registerBlock(PRISMARINE_BRICKS_STAIRS, BlockStairsPrismarineBrick.class, V1_4_0);
        registerBlock(STRIPPED_SPRUCE_LOG, BlockWoodStrippedSpruce.class, V1_4_0);
        registerBlock(STRIPPED_BIRCH_LOG, BlockWoodStrippedBirch.class, V1_4_0);
        registerBlock(STRIPPED_JUNGLE_LOG, BlockWoodStrippedJungle.class, V1_4_0);
        registerBlock(STRIPPED_ACACIA_LOG, BlockWoodStrippedAcacia.class, V1_4_0);
        registerBlock(STRIPPED_DARK_OAK_LOG, BlockWoodStrippedDarkOak.class, V1_4_0);
        registerBlock(STRIPPED_OAK_LOG, BlockWoodStrippedOak.class, V1_4_0);
        registerBlock(BLUE_ICE, BlockIceBlue.class, V1_4_0);
        registerElements();
        registerBlock(SEAGRASS, BlockSeagrass.class, V1_4_0);
        registerBlock(CORAL, BlockCoral.class, V1_4_0);
        registerBlock(CORAL_BLOCK, BlockCoralBlock.class, V1_4_0);
        registerBlock(CORAL_FAN, BlockCoralFan.class, V1_4_0);
        registerBlock(CORAL_FAN_DEAD, BlockCoralFanDead.class, V1_4_0);
        registerBlock(CORAL_FAN_HANG, BlockCoralFanHang.class, V1_4_0);
        registerBlock(CORAL_FAN_HANG2, BlockCoralFanHang2.class, V1_4_0);
        registerBlock(CORAL_FAN_HANG3, BlockCoralFanHang3.class, V1_4_0);
        registerBlock(BLOCK_KELP, BlockKelp.class, V1_4_0);
        registerBlock(DRIED_KELP_BLOCK, BlockDriedKelp.class, V1_4_0);
        registerBlock(ACACIA_BUTTON, BlockButtonAcacia.class, V1_4_0);
        registerBlock(BIRCH_BUTTON, BlockButtonBirch.class, V1_4_0);
        registerBlock(DARK_OAK_BUTTON, BlockButtonDarkOak.class, V1_4_0);
        registerBlock(JUNGLE_BUTTON, BlockButtonJungle.class, V1_4_0);
        registerBlock(SPRUCE_BUTTON, BlockButtonSpruce.class, V1_4_0);
        registerBlock(ACACIA_TRAPDOOR, BlockTrapdoorAcacia.class, V1_4_0);
        registerBlock(BIRCH_TRAPDOOR, BlockTrapdoorBirch.class, V1_4_0);
        registerBlock(DARK_OAK_TRAPDOOR, BlockTrapdoorDarkOak.class, V1_4_0);
        registerBlock(JUNGLE_TRAPDOOR, BlockTrapdoorJungle.class, V1_4_0);
        registerBlock(SPRUCE_TRAPDOOR, BlockTrapdoorSpruce.class, V1_4_0);
        registerBlock(ACACIA_PRESSURE_PLATE, BlockPressurePlateAcacia.class, V1_4_0);
        registerBlock(BIRCH_PRESSURE_PLATE, BlockPressurePlateBirch.class, V1_4_0);
        registerBlock(DARK_OAK_PRESSURE_PLATE, BlockPressurePlateDarkOak.class, V1_4_0);
        registerBlock(JUNGLE_PRESSURE_PLATE, BlockPressurePlateJungle.class, V1_4_0);
        registerBlock(SPRUCE_PRESSURE_PLATE, BlockPressurePlateSpruce.class, V1_4_0);
        registerBlock(CARVED_PUMPKIN, BlockPumpkinCarved.class, V1_4_0);
        registerBlock(SEA_PICKLE, BlockSeaPickle.class, V1_4_0);

        registerBlock(CONDUIT, BlockConduit.class, V1_5_0); //TODO: activate
        registerBlock(TURTLE_EGG, BlockTurtleEgg.class, V1_5_0);
        registerBlock(BUBBLE_COLUMN, BlockBubbleColumn.class, V1_5_0);

        registerBlock(BARRIER, BlockBarrier.class, V1_6_0);

        registerBlock(BAMBOO, BlockBamboo.class, V1_8_0);
        registerBlock(BAMBOO_SAPLING, BlockBambooSapling.class, V1_8_0);
        registerBlock(SCAFFOLDING, BlockScaffolding.class, V1_8_0);

        registerBlock(STONE_SLAB3, BlockSlabStone3.class, V1_9_0);
        registerBlock(STONE_SLAB4, BlockSlabStone4.class, V1_9_0);
        registerBlock(DOUBLE_STONE_SLAB3, BlockDoubleSlabStone3.class, V1_9_0);
        registerBlock(DOUBLE_STONE_SLAB4, BlockDoubleSlabStone4.class, V1_9_0);
        registerBlock(GRANITE_STAIRS, BlockStairsGranite.class, V1_9_0);
        registerBlock(DIORITE_STAIRS, BlockStairsDiorite.class, V1_9_0);
        registerBlock(ANDESITE_STAIRS, BlockStairsAndesite.class, V1_9_0);
        registerBlock(POLISHED_GRANITE_STAIRS, BlockStairsPolishedGranite.class, V1_9_0);
        registerBlock(POLISHED_DIORITE_STAIRS, BlockStairsPolishedDiorite.class, V1_9_0);
        registerBlock(POLISHED_ANDESITE_STAIRS, BlockStairsPolishedAndesite.class, V1_9_0);
        registerBlock(MOSSY_STONE_BRICK_STAIRS, BlockStairsMossyStoneBrick.class, V1_9_0);
        registerBlock(SMOOTH_RED_SANDSTONE_STAIRS, BlockStairsSmoothRedSandstone.class, V1_9_0);
        registerBlock(SMOOTH_SANDSTONE_STAIRS, BlockStairsSmoothSandstone.class, V1_9_0);
        registerBlock(END_BRICK_STAIRS, BlockStairsEndBrick.class, V1_9_0);
        registerBlock(MOSSY_COBBLESTONE_STAIRS, BlockStairsMossyCobblestone.class, V1_9_0);
        registerBlock(NORMAL_STONE_STAIRS, BlockStairsStone.class, V1_9_0);
        registerBlock(SPRUCE_STANDING_SIGN, BlockSignPostSpruce.class, V1_9_0);
        registerBlock(SPRUCE_WALL_SIGN, BlockWallSignSpruce.class, V1_9_0);
        registerBlock(SMOOTH_STONE, BlockSmoothStone.class, V1_9_0);
        registerBlock(RED_NETHER_BRICK_STAIRS, BlockStairsRedNetherBrick.class, V1_9_0);
        registerBlock(SMOOTH_QUARTZ_STAIRS, BlockStairsSmoothQuartz.class, V1_9_0);
        registerBlock(BIRCH_STANDING_SIGN, BlockSignPostBirch.class, V1_9_0);
        registerBlock(BIRCH_WALL_SIGN, BlockWallSignBirch.class, V1_9_0);
        registerBlock(JUNGLE_STANDING_SIGN, BlockSignPostJungle.class, V1_9_0);
        registerBlock(JUNGLE_WALL_SIGN, BlockWallSignJungle.class, V1_9_0);
        registerBlock(ACACIA_STANDING_SIGN, BlockSignPostAcacia.class, V1_9_0);
        registerBlock(ACACIA_WALL_SIGN, BlockWallSignAcacia.class, V1_9_0);
        registerBlock(DARKOAK_STANDING_SIGN, BlockSignPostDarkOak.class, V1_9_0);
        registerBlock(DARKOAK_WALL_SIGN, BlockWallSignDarkOak.class, V1_9_0);
        registerBlock(LAVA_CAULDRON, BlockCauldronLava.class, V1_9_0); //TODO

        registerBlock(LECTERN, BlockLectern.class, V1_10_0);
        registerBlock(LOOM, BlockLoom.class, V1_10_0);
        registerBlock(LANTERN, BlockLantern.class, V1_10_0);
        registerBlock(JIGSAW, BlockJigsaw.class, V1_10_0);
        registerBlock(WOOD, BlockWoodBark.class, V1_10_0);

        registerBlock(GRINDSTONE, BlockGrindstone.class, V1_11_0);
        registerBlock(BLAST_FURNACE, BlockFurnaceBlast.class, V1_11_0);
        registerBlock(STONECUTTER_BLOCK, BlockStonecutter.class, V1_11_0);
        registerBlock(SMOKER, BlockSmoker.class, V1_11_0);
        registerBlock(LIT_SMOKER, BlockSmokerBurning.class, V1_11_0);
        registerBlock(CARTOGRAPHY_TABLE, BlockCartographyTable.class, V1_11_0);
        registerBlock(FLETCHING_TABLE, BlockFletchingTable.class, V1_11_0);
        registerBlock(SMITHING_TABLE, BlockSmithingTable.class, V1_11_0); //TODO: 1.16.0 ui
        registerBlock(BARREL, BlockBarrel.class, V1_11_0);
        registerBlock(BELL, BlockBell.class, V1_11_0);
        registerBlock(SWEET_BERRY_BUSH, BlockSweetBerryBush.class, V1_11_0);
        registerBlock(BLOCK_CAMPFIRE, BlockCampfire.class, V1_11_0);
        registerBlock(COMPOSTER, BlockComposter.class, V1_11_0);
        registerBlock(LIT_BLAST_FURNACE, BlockFurnaceBurningBlast.class, V1_11_0);

        registerBlock(BLOCK_CAMERA, BlockCamera.class, V1_13_0);
        registerBlock(LIGHT_BLOCK, BlockLight.class, V1_13_0);
        registerBlock(WITHER_ROSE, BlockWitherRose.class, V1_13_0);
        registerBlock(STICKY_PISTON_ARM_COLLISION, BlockPistonHeadSticky.class, V1_13_0);

        registerBlock(BEE_NEST, BlockBeeNest.class, V1_14_0);
        registerBlock(BEEHIVE, BlockBeehive.class, V1_14_0);
        registerBlock(HONEY_BLOCK, BlockHoney.class, V1_14_0);
        registerBlock(HONEYCOMB_BLOCK, BlockHoneycomb.class, V1_14_0);

        registerBlock(ALLOW, BlockAllow.class, V1_16_0);
        registerBlock(DENY, BlockDeny.class, V1_16_0);
        registerBlock(BORDER_BLOCK, BlockBorder.class, V1_16_0);
        registerBlock(STRUCTURE_VOID, BlockStructureVoid.class, V1_16_0);
        // lodestone
        //
    }

    public static void registerElements() {
        registerBlock(ELEMENT_0, BlockElementUnknown.class, V1_4_0);
        registerBlock(ELEMENT_1, BlockElement1.class, V1_4_0);
        registerBlock(ELEMENT_2, BlockElement2.class, V1_4_0);
        registerBlock(ELEMENT_3, BlockElement3.class, V1_4_0);
        registerBlock(ELEMENT_4, BlockElement4.class, V1_4_0);
        registerBlock(ELEMENT_5, BlockElement5.class, V1_4_0);
        registerBlock(ELEMENT_6, BlockElement6.class, V1_4_0);
        registerBlock(ELEMENT_7, BlockElement7.class, V1_4_0);
        registerBlock(ELEMENT_8, BlockElement8.class, V1_4_0);
        registerBlock(ELEMENT_9, BlockElement9.class, V1_4_0);
        registerBlock(ELEMENT_10, BlockElement10.class, V1_4_0);
        registerBlock(ELEMENT_11, BlockElement11.class, V1_4_0);
        registerBlock(ELEMENT_12, BlockElement12.class, V1_4_0);
        registerBlock(ELEMENT_13, BlockElement13.class, V1_4_0);
        registerBlock(ELEMENT_14, BlockElement14.class, V1_4_0);
        registerBlock(ELEMENT_15, BlockElement15.class, V1_4_0);
        registerBlock(ELEMENT_16, BlockElement16.class, V1_4_0);
        registerBlock(ELEMENT_17, BlockElement17.class, V1_4_0);
        registerBlock(ELEMENT_18, BlockElement18.class, V1_4_0);
        registerBlock(ELEMENT_19, BlockElement19.class, V1_4_0);
        registerBlock(ELEMENT_20, BlockElement20.class, V1_4_0);
        registerBlock(ELEMENT_21, BlockElement21.class, V1_4_0);
        registerBlock(ELEMENT_22, BlockElement22.class, V1_4_0);
        registerBlock(ELEMENT_23, BlockElement23.class, V1_4_0);
        registerBlock(ELEMENT_24, BlockElement24.class, V1_4_0);
        registerBlock(ELEMENT_25, BlockElement25.class, V1_4_0);
        registerBlock(ELEMENT_26, BlockElement26.class, V1_4_0);
        registerBlock(ELEMENT_27, BlockElement27.class, V1_4_0);
        registerBlock(ELEMENT_28, BlockElement28.class, V1_4_0);
        registerBlock(ELEMENT_29, BlockElement29.class, V1_4_0);
        registerBlock(ELEMENT_30, BlockElement30.class, V1_4_0);
        registerBlock(ELEMENT_31, BlockElement31.class, V1_4_0);
        registerBlock(ELEMENT_32, BlockElement32.class, V1_4_0);
        registerBlock(ELEMENT_33, BlockElement33.class, V1_4_0);
        registerBlock(ELEMENT_34, BlockElement34.class, V1_4_0);
        registerBlock(ELEMENT_35, BlockElement35.class, V1_4_0);
        registerBlock(ELEMENT_36, BlockElement36.class, V1_4_0);
        registerBlock(ELEMENT_37, BlockElement37.class, V1_4_0);
        registerBlock(ELEMENT_38, BlockElement38.class, V1_4_0);
        registerBlock(ELEMENT_39, BlockElement39.class, V1_4_0);
        registerBlock(ELEMENT_40, BlockElement40.class, V1_4_0);
        registerBlock(ELEMENT_41, BlockElement41.class, V1_4_0);
        registerBlock(ELEMENT_42, BlockElement42.class, V1_4_0);
        registerBlock(ELEMENT_43, BlockElement43.class, V1_4_0);
        registerBlock(ELEMENT_44, BlockElement44.class, V1_4_0);
        registerBlock(ELEMENT_45, BlockElement45.class, V1_4_0);
        registerBlock(ELEMENT_46, BlockElement46.class, V1_4_0);
        registerBlock(ELEMENT_47, BlockElement47.class, V1_4_0);
        registerBlock(ELEMENT_48, BlockElement48.class, V1_4_0);
        registerBlock(ELEMENT_49, BlockElement49.class, V1_4_0);
        registerBlock(ELEMENT_50, BlockElement50.class, V1_4_0);
        registerBlock(ELEMENT_51, BlockElement51.class, V1_4_0);
        registerBlock(ELEMENT_52, BlockElement52.class, V1_4_0);
        registerBlock(ELEMENT_53, BlockElement53.class, V1_4_0);
        registerBlock(ELEMENT_54, BlockElement54.class, V1_4_0);
        registerBlock(ELEMENT_55, BlockElement55.class, V1_4_0);
        registerBlock(ELEMENT_56, BlockElement56.class, V1_4_0);
        registerBlock(ELEMENT_57, BlockElement57.class, V1_4_0);
        registerBlock(ELEMENT_58, BlockElement58.class, V1_4_0);
        registerBlock(ELEMENT_59, BlockElement59.class, V1_4_0);
        registerBlock(ELEMENT_60, BlockElement60.class, V1_4_0);
        registerBlock(ELEMENT_61, BlockElement61.class, V1_4_0);
        registerBlock(ELEMENT_62, BlockElement62.class, V1_4_0);
        registerBlock(ELEMENT_63, BlockElement63.class, V1_4_0);
        registerBlock(ELEMENT_64, BlockElement64.class, V1_4_0);
        registerBlock(ELEMENT_65, BlockElement65.class, V1_4_0);
        registerBlock(ELEMENT_66, BlockElement66.class, V1_4_0);
        registerBlock(ELEMENT_67, BlockElement67.class, V1_4_0);
        registerBlock(ELEMENT_68, BlockElement68.class, V1_4_0);
        registerBlock(ELEMENT_69, BlockElement69.class, V1_4_0);
        registerBlock(ELEMENT_70, BlockElement70.class, V1_4_0);
        registerBlock(ELEMENT_71, BlockElement71.class, V1_4_0);
        registerBlock(ELEMENT_72, BlockElement72.class, V1_4_0);
        registerBlock(ELEMENT_73, BlockElement73.class, V1_4_0);
        registerBlock(ELEMENT_74, BlockElement74.class, V1_4_0);
        registerBlock(ELEMENT_75, BlockElement75.class, V1_4_0);
        registerBlock(ELEMENT_76, BlockElement76.class, V1_4_0);
        registerBlock(ELEMENT_77, BlockElement77.class, V1_4_0);
        registerBlock(ELEMENT_78, BlockElement78.class, V1_4_0);
        registerBlock(ELEMENT_79, BlockElement79.class, V1_4_0);
        registerBlock(ELEMENT_80, BlockElement80.class, V1_4_0);
        registerBlock(ELEMENT_81, BlockElement81.class, V1_4_0);
        registerBlock(ELEMENT_82, BlockElement82.class, V1_4_0);
        registerBlock(ELEMENT_83, BlockElement83.class, V1_4_0);
        registerBlock(ELEMENT_84, BlockElement84.class, V1_4_0);
        registerBlock(ELEMENT_85, BlockElement85.class, V1_4_0);
        registerBlock(ELEMENT_86, BlockElement86.class, V1_4_0);
        registerBlock(ELEMENT_87, BlockElement87.class, V1_4_0);
        registerBlock(ELEMENT_88, BlockElement88.class, V1_4_0);
        registerBlock(ELEMENT_89, BlockElement89.class, V1_4_0);
        registerBlock(ELEMENT_90, BlockElement90.class, V1_4_0);
        registerBlock(ELEMENT_91, BlockElement91.class, V1_4_0);
        registerBlock(ELEMENT_92, BlockElement92.class, V1_4_0);
        registerBlock(ELEMENT_93, BlockElement93.class, V1_4_0);
        registerBlock(ELEMENT_94, BlockElement94.class, V1_4_0);
        registerBlock(ELEMENT_95, BlockElement95.class, V1_4_0);
        registerBlock(ELEMENT_96, BlockElement96.class, V1_4_0);
        registerBlock(ELEMENT_97, BlockElement97.class, V1_4_0);
        registerBlock(ELEMENT_98, BlockElement98.class, V1_4_0);
        registerBlock(ELEMENT_99, BlockElement99.class, V1_4_0);
        registerBlock(ELEMENT_100, BlockElement100.class, V1_4_0);
        registerBlock(ELEMENT_101, BlockElement101.class, V1_4_0);
        registerBlock(ELEMENT_102, BlockElement102.class, V1_4_0);
        registerBlock(ELEMENT_103, BlockElement103.class, V1_4_0);
        registerBlock(ELEMENT_104, BlockElement104.class, V1_4_0);
        registerBlock(ELEMENT_105, BlockElement105.class, V1_4_0);
        registerBlock(ELEMENT_106, BlockElement106.class, V1_4_0);
        registerBlock(ELEMENT_107, BlockElement107.class, V1_4_0);
        registerBlock(ELEMENT_108, BlockElement108.class, V1_4_0);
        registerBlock(ELEMENT_109, BlockElement109.class, V1_4_0);
        registerBlock(ELEMENT_110, BlockElement110.class, V1_4_0);
        registerBlock(ELEMENT_111, BlockElement111.class, V1_4_0);
        registerBlock(ELEMENT_112, BlockElement112.class, V1_4_0);
        registerBlock(ELEMENT_113, BlockElement113.class, V1_4_0);
        registerBlock(ELEMENT_114, BlockElement114.class, V1_4_0);
        registerBlock(ELEMENT_115, BlockElement115.class, V1_4_0);
        registerBlock(ELEMENT_116, BlockElement116.class, V1_4_0);
        registerBlock(ELEMENT_117, BlockElement117.class, V1_4_0);
        registerBlock(ELEMENT_118, BlockElement118.class, V1_4_0);
    }

    private static Class<? extends Block> registerBlock(int id, Class<? extends Block> clazz) {
        Block.list[id] = clazz;
        return clazz;
    }

    /**
     * @param version min required base game version
     */
    private static Class<? extends Block> registerBlock(int id, Class<? extends Block> clazz, GameVersion version) {
        if (!version.isAvailable()) {
            return null;
        }
        return registerBlock(id, clazz);
    }

    public static Block air() {
        return AIR.clone();
    }

    private Blocks() {
        throw new IllegalStateException();
    }
}
