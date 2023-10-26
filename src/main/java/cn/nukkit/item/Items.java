package cn.nukkit.item;

import cn.nukkit.GameVersion;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.potion.PotionID;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicInteger;

import static cn.nukkit.GameVersion.*;
import static cn.nukkit.SharedConstants.ENABLE_ITEM_NAME_PERSISTENCE;
import static cn.nukkit.item.ItemID.*;

public final class Items {
    static final int BASE_INTERNAL_ID = 0x3ff;
    private static final AtomicInteger CUSTOM_ITEM_ID_ALLOCATOR = new AtomicInteger(CUSTOM_ITEM);

    private static final ItemFactory[] ITEM_FACTORIES = new ItemFactory[Short.MAX_VALUE];

    private static final Item[][] ITEM_CACHE = new Item[Short.MAX_VALUE][];
    private static final Item[][] BLOCK_CACHE = new Item[BlockID.UNDEFINED][];
    private static final Object2IntMap<String> CUSTOM_ITEM_IDENTIFIER_TO_ID = new Object2IntOpenHashMap<>();

    public static void registerVanillaItems() {
        registerItem(IRON_SHOVEL, ItemShovelIron.class, ItemShovelIron::new);
        registerItem(IRON_PICKAXE, ItemPickaxeIron.class, ItemPickaxeIron::new);
        registerItem(IRON_AXE, ItemAxeIron.class, ItemAxeIron::new);
        registerItem(FLINT_AND_STEEL, ItemFlintSteel.class, ItemFlintSteel::new);
        registerItem(APPLE, ItemApple.class, ItemApple::new);
        registerItem(BOW, ItemBow.class, ItemBow::new);
        registerItem(ARROW, ItemArrow.class, ItemArrow::new, ItemArrow.TIPPED_ARROW + PotionID.UNDEFINED - 1);
        registerItem(COAL, ItemCoal.class, ItemCoal::new, 1);
        registerItem(DIAMOND, ItemDiamond.class, ItemDiamond::new);
        registerItem(IRON_INGOT, ItemIngotIron.class, ItemIngotIron::new);
        registerItem(GOLD_INGOT, ItemIngotGold.class, ItemIngotGold::new);
        registerItem(IRON_SWORD, ItemSwordIron.class, ItemSwordIron::new);
        registerItem(WOODEN_SWORD, ItemSwordWood.class, ItemSwordWood::new);
        registerItem(WOODEN_SHOVEL, ItemShovelWood.class, ItemShovelWood::new);
        registerItem(WOODEN_PICKAXE, ItemPickaxeWood.class, ItemPickaxeWood::new);
        registerItem(WOODEN_AXE, ItemAxeWood.class, ItemAxeWood::new);
        registerItem(STONE_SWORD, ItemSwordStone.class, ItemSwordStone::new);
        registerItem(STONE_SHOVEL, ItemShovelStone.class, ItemShovelStone::new);
        registerItem(STONE_PICKAXE, ItemPickaxeStone.class, ItemPickaxeStone::new);
        registerItem(STONE_AXE, ItemAxeStone.class, ItemAxeStone::new);
        registerItem(DIAMOND_SWORD, ItemSwordDiamond.class, ItemSwordDiamond::new);
        registerItem(DIAMOND_SHOVEL, ItemShovelDiamond.class, ItemShovelDiamond::new);
        registerItem(DIAMOND_PICKAXE, ItemPickaxeDiamond.class, ItemPickaxeDiamond::new);
        registerItem(DIAMOND_AXE, ItemAxeDiamond.class, ItemAxeDiamond::new);
        registerItem(STICK, ItemStick.class, ItemStick::new);
        registerItem(BOWL, ItemBowl.class, ItemBowl::new);
        registerItem(MUSHROOM_STEW, ItemMushroomStew.class, ItemMushroomStew::new);
        registerItem(GOLDEN_SWORD, ItemSwordGold.class, ItemSwordGold::new);
        registerItem(GOLDEN_SHOVEL, ItemShovelGold.class, ItemShovelGold::new);
        registerItem(GOLDEN_PICKAXE, ItemPickaxeGold.class, ItemPickaxeGold::new);
        registerItem(GOLDEN_AXE, ItemAxeGold.class, ItemAxeGold::new);
        registerItem(STRING, ItemString.class, ItemString::new);
        registerItem(FEATHER, ItemFeather.class, ItemFeather::new);
        registerItem(GUNPOWDER, ItemGunpowder.class, ItemGunpowder::new);
        registerItem(WOODEN_HOE, ItemHoeWood.class, ItemHoeWood::new);
        registerItem(STONE_HOE, ItemHoeStone.class, ItemHoeStone::new);
        registerItem(IRON_HOE, ItemHoeIron.class, ItemHoeIron::new);
        registerItem(DIAMOND_HOE, ItemHoeDiamond.class, ItemHoeDiamond::new);
        registerItem(GOLDEN_HOE, ItemHoeGold.class, ItemHoeGold::new);
        registerItem(WHEAT_SEEDS, ItemSeedsWheat.class, ItemSeedsWheat::new);
        registerItem(WHEAT, ItemWheat.class, ItemWheat::new);
        registerItem(BREAD, ItemBread.class, ItemBread::new);
        registerItem(LEATHER_HELMET, ItemHelmetLeather.class, ItemHelmetLeather::new);
        registerItem(LEATHER_CHESTPLATE, ItemChestplateLeather.class, ItemChestplateLeather::new);
        registerItem(LEATHER_LEGGINGS, ItemLeggingsLeather.class, ItemLeggingsLeather::new);
        registerItem(LEATHER_BOOTS, ItemBootsLeather.class, ItemBootsLeather::new);
        registerItem(CHAINMAIL_HELMET, ItemHelmetChain.class, ItemHelmetChain::new);
        registerItem(CHAINMAIL_CHESTPLATE, ItemChestplateChain.class, ItemChestplateChain::new);
        registerItem(CHAINMAIL_LEGGINGS, ItemLeggingsChain.class, ItemLeggingsChain::new);
        registerItem(CHAINMAIL_BOOTS, ItemBootsChain.class, ItemBootsChain::new);
        registerItem(IRON_HELMET, ItemHelmetIron.class, ItemHelmetIron::new);
        registerItem(IRON_CHESTPLATE, ItemChestplateIron.class, ItemChestplateIron::new);
        registerItem(IRON_LEGGINGS, ItemLeggingsIron.class, ItemLeggingsIron::new);
        registerItem(IRON_BOOTS, ItemBootsIron.class, ItemBootsIron::new);
        registerItem(DIAMOND_HELMET, ItemHelmetDiamond.class, ItemHelmetDiamond::new);
        registerItem(DIAMOND_CHESTPLATE, ItemChestplateDiamond.class, ItemChestplateDiamond::new);
        registerItem(DIAMOND_LEGGINGS, ItemLeggingsDiamond.class, ItemLeggingsDiamond::new);
        registerItem(DIAMOND_BOOTS, ItemBootsDiamond.class, ItemBootsDiamond::new);
        registerItem(GOLDEN_HELMET, ItemHelmetGold.class, ItemHelmetGold::new);
        registerItem(GOLDEN_CHESTPLATE, ItemChestplateGold.class, ItemChestplateGold::new);
        registerItem(GOLDEN_LEGGINGS, ItemLeggingsGold.class, ItemLeggingsGold::new);
        registerItem(GOLDEN_BOOTS, ItemBootsGold.class, ItemBootsGold::new);
        registerItem(FLINT, ItemFlint.class, ItemFlint::new);
        registerItem(PORKCHOP, ItemPorkchopRaw.class, ItemPorkchopRaw::new);
        registerItem(COOKED_PORKCHOP, ItemPorkchopCooked.class, ItemPorkchopCooked::new);
        registerItem(PAINTING, ItemPainting.class, ItemPainting::new);
        registerItem(GOLDEN_APPLE, ItemAppleGold.class, ItemAppleGold::new);
        registerItem(OAK_SIGN, ItemSign.class, ItemSign::new);
        registerItem(WOODEN_DOOR, ItemDoorWood.class, ItemDoorWood::new);
        registerItem(BUCKET, ItemBucket.class, ItemBucket::new, ItemBucket.UNDEFINED_BUCKET - 1);

        registerItem(MINECART, ItemMinecart.class, ItemMinecart::new);
        registerItem(SADDLE, ItemSaddle.class, ItemSaddle::new);
        registerItem(IRON_DOOR, ItemDoorIron.class, ItemDoorIron::new);
        registerItem(REDSTONE, ItemRedstone.class, ItemRedstone::new);
        registerItem(SNOWBALL, ItemSnowball.class, ItemSnowball::new);
        registerItem(BOAT, ItemBoat.class, ItemBoat::new, ItemBoat.UNDEFINED_BOAT - 1);
        registerItem(LEATHER, ItemLeather.class, ItemLeather::new);

        registerItem(BRICK, ItemBrick.class, ItemBrick::new);
        registerItem(CLAY_BALL, ItemClay.class, ItemClay::new);
        registerItem(SUGAR_CANE, ItemSugarcane.class, ItemSugarcane::new);
        registerItem(PAPER, ItemPaper.class, ItemPaper::new);
        registerItem(BOOK, ItemBook.class, ItemBook::new);
        registerItem(SLIME_BALL, ItemSlimeball.class, ItemSlimeball::new);
        registerItem(CHEST_MINECART, ItemMinecartChest.class, ItemMinecartChest::new);

        registerItem(EGG, ItemEgg.class, ItemEgg::new);
        registerItem(COMPASS, ItemCompass.class, ItemCompass::new);
        registerItem(FISHING_ROD, ItemFishingRod.class, ItemFishingRod::new);
        registerItem(CLOCK, ItemClock.class, ItemClock::new);
        registerItem(GLOWSTONE_DUST, ItemGlowstoneDust.class, ItemGlowstoneDust::new);
        registerItem(COD, ItemFish.class, ItemFish::new);
        registerItem(COOKED_COD, ItemFishCooked.class, ItemFishCooked::new);
        registerItem(DYE, ItemDye.class, ItemDye::new, ItemDye.DYE_COUNT - 1);
        registerItem(BONE, ItemBone.class, ItemBone::new);
        registerItem(SUGAR, ItemSugar.class, ItemSugar::new);
        registerItem(CAKE, ItemCake.class, ItemCake::new);
        registerItem(BED, ItemBed.class, ItemBed::new, 15);
        registerItem(REPEATER, ItemRedstoneRepeater.class, ItemRedstoneRepeater::new);
        registerItem(COOKIE, ItemCookie.class, ItemCookie::new);
        registerItem(FILLED_MAP, ItemMap.class, ItemMap::new, ItemMap.UNDEFINED_MAP - 1);
        registerItem(SHEARS, ItemShears.class, ItemShears::new);
        registerItem(MELON_SLICE, ItemMelon.class, ItemMelon::new);
        registerItem(PUMPKIN_SEEDS, ItemSeedsPumpkin.class, ItemSeedsPumpkin::new);
        registerItem(MELON_SEEDS, ItemSeedsMelon.class, ItemSeedsMelon::new);
        registerItem(BEEF, ItemBeefRaw.class, ItemBeefRaw::new);
        registerItem(COOKED_BEEF, ItemSteak.class, ItemSteak::new);
        registerItem(CHICKEN, ItemChickenRaw.class, ItemChickenRaw::new);
        registerItem(COOKED_CHICKEN, ItemChickenCooked.class, ItemChickenCooked::new);
        registerItem(ROTTEN_FLESH, ItemRottenFlesh.class, ItemRottenFlesh::new);
        registerItem(ENDER_PEARL, ItemEnderPearl.class, ItemEnderPearl::new);
        registerItem(BLAZE_ROD, ItemBlazeRod.class, ItemBlazeRod::new);
        registerItem(GHAST_TEAR, ItemGhastTear.class, ItemGhastTear::new);
        registerItem(GOLD_NUGGET, ItemNuggetGold.class, ItemNuggetGold::new);
        registerItem(NETHER_WART, ItemNetherWart.class, ItemNetherWart::new);
        registerItem(POTION, ItemPotion.class, ItemPotion::new, PotionID.UNDEFINED - 1);
        registerItem(GLASS_BOTTLE, ItemGlassBottle.class, ItemGlassBottle::new);
        registerItem(SPIDER_EYE, ItemSpiderEye.class, ItemSpiderEye::new);
        registerItem(FERMENTED_SPIDER_EYE, ItemSpiderEyeFermented.class, ItemSpiderEyeFermented::new);
        registerItem(BLAZE_POWDER, ItemBlazePowder.class, ItemBlazePowder::new);
        registerItem(MAGMA_CREAM, ItemMagmaCream.class, ItemMagmaCream::new);
        registerItem(BREWING_STAND, ItemBrewingStand.class, ItemBrewingStand::new);
        registerItem(CAULDRON, ItemCauldron.class, ItemCauldron::new);
        registerItem(ENDER_EYE, ItemEnderEye.class, ItemEnderEye::new);
        registerItem(GLISTERING_MELON_SLICE, ItemMelonGlistering.class, ItemMelonGlistering::new);
        registerItem(SPAWN_EGG, ItemSpawnEgg.class, ItemSpawnEgg::new, 0xff);
        registerItem(EXPERIENCE_BOTTLE, ItemExpBottle.class, ItemExpBottle::new);
        registerItem(FIRE_CHARGE, ItemFireCharge.class, ItemFireCharge::new);
        registerItem(WRITABLE_BOOK, ItemBookAndQuill.class, ItemBookAndQuill::new);
        registerItem(WRITTEN_BOOK, ItemBookWritten.class, ItemBookWritten::new);
        registerItem(EMERALD, ItemEmerald.class, ItemEmerald::new);
        registerItem(FRAME, ItemItemFrame.class, ItemItemFrame::new);
        registerItem(FLOWER_POT, ItemFlowerPot.class, ItemFlowerPot::new);
        registerItem(CARROT, ItemCarrot.class, ItemCarrot::new);
        registerItem(POTATO, ItemPotato.class, ItemPotato::new);
        registerItem(BAKED_POTATO, ItemPotatoBaked.class, ItemPotatoBaked::new);
        registerItem(POISONOUS_POTATO, ItemPotatoPoisonous.class, ItemPotatoPoisonous::new);
        registerItem(EMPTY_MAP, ItemEmptyMap.class, ItemEmptyMap::new, ItemEmptyMap.UNDEFINED_EMPTY_MAP - 1);
        registerItem(GOLDEN_CARROT, ItemCarrotGolden.class, ItemCarrotGolden::new);
        registerItem(SKULL, ItemSkull.class, ItemSkull::new, ItemSkull.UNDEFINED_HEAD - 1);
        registerItem(CARROT_ON_A_STICK, ItemCarrotOnAStick.class, ItemCarrotOnAStick::new);
        registerItem(NETHER_STAR, ItemNetherStar.class, ItemNetherStar::new);
        registerItem(PUMPKIN_PIE, ItemPumpkinPie.class, ItemPumpkinPie::new);
        registerItem(FIREWORK_ROCKET, ItemFirework.class, ItemFirework::new);
        registerItem(FIREWORK_STAR, ItemFireworkStar.class, ItemFireworkStar::new, 15);
        registerItem(ENCHANTED_BOOK, ItemBookEnchanted.class, ItemBookEnchanted::new);
        registerItem(COMPARATOR, ItemRedstoneComparator.class, ItemRedstoneComparator::new);
        registerItem(NETHERBRICK, ItemNetherBrick.class, ItemNetherBrick::new);
        registerItem(QUARTZ, ItemQuartz.class, ItemQuartz::new);
        registerItem(TNT_MINECART, ItemMinecartTNT.class, ItemMinecartTNT::new);
        registerItem(HOPPER_MINECART, ItemMinecartHopper.class, ItemMinecartHopper::new);
        registerItem(PRISMARINE_SHARD, ItemPrismarineShard.class, ItemPrismarineShard::new);
        registerItem(HOPPER, ItemHopper.class, ItemHopper::new);
        registerItem(RABBIT, ItemRabbitRaw.class, ItemRabbitRaw::new);
        registerItem(COOKED_RABBIT, ItemRabbitCooked.class, ItemRabbitCooked::new);
        registerItem(RABBIT_STEW, ItemRabbitStew.class, ItemRabbitStew::new);
        registerItem(RABBIT_FOOT, ItemRabbitFoot.class, ItemRabbitFoot::new);
        registerItem(RABBIT_HIDE, ItemRabbitHide.class, ItemRabbitHide::new);
        registerItem(LEATHER_HORSE_ARMOR, ItemHorseArmorLeather.class, ItemHorseArmorLeather::new);
        registerItem(IRON_HORSE_ARMOR, ItemHorseArmorIron.class, ItemHorseArmorIron::new);
        registerItem(GOLDEN_HORSE_ARMOR, ItemHorseArmorGold.class, ItemHorseArmorGold::new);
        registerItem(DIAMOND_HORSE_ARMOR, ItemHorseArmorDiamond.class, ItemHorseArmorDiamond::new);
        registerItem(LEAD, ItemLead.class, ItemLead::new);
        registerItem(NAME_TAG, ItemNameTag.class, ItemNameTag::new);
        registerItem(PRISMARINE_CRYSTALS, ItemPrismarineCrystals.class, ItemPrismarineCrystals::new);
        registerItem(MUTTON, ItemMuttonRaw.class, ItemMuttonRaw::new);
        registerItem(COOKED_MUTTON, ItemMuttonCooked.class, ItemMuttonCooked::new);
        registerItem(ARMOR_STAND, ItemArmorStand.class, ItemArmorStand::new);
        registerItem(END_CRYSTAL, ItemEndCrystal.class, ItemEndCrystal::new);
        registerItem(SPRUCE_DOOR, ItemDoorSpruce.class, ItemDoorSpruce::new);
        registerItem(BIRCH_DOOR, ItemDoorBirch.class, ItemDoorBirch::new);
        registerItem(JUNGLE_DOOR, ItemDoorJungle.class, ItemDoorJungle::new);
        registerItem(ACACIA_DOOR, ItemDoorAcacia.class, ItemDoorAcacia::new);
        registerItem(DARK_OAK_DOOR, ItemDoorDarkOak.class, ItemDoorDarkOak::new);
        registerItem(CHORUS_FRUIT, ItemChorusFruit.class, ItemChorusFruit::new);
        registerItem(POPPED_CHORUS_FRUIT, ItemChorusFruitPopped.class, ItemChorusFruitPopped::new);

        registerItem(DRAGON_BREATH, ItemDragonBreath.class, ItemDragonBreath::new);
        registerItem(SPLASH_POTION, ItemPotionSplash.class, ItemPotionSplash::new, PotionID.UNDEFINED - 1);

        registerItem(LINGERING_POTION, ItemPotionLingering.class, ItemPotionLingering::new, PotionID.UNDEFINED - 1);

        registerItem(COMMAND_BLOCK_MINECART, ItemMinecartCommandBlock.class, ItemMinecartCommandBlock::new);
        registerItem(ELYTRA, ItemElytra.class, ItemElytra::new);
        registerItem(SHULKER_SHELL, ItemShulkerShell.class, ItemShulkerShell::new);
        registerItem(BANNER, ItemBanner.class, ItemBanner::new, 15);

        registerItem(TOTEM_OF_UNDYING, ItemTotem.class, ItemTotem::new);

        registerItem(IRON_NUGGET, ItemNuggetIron.class, ItemNuggetIron::new);

        registerItem(BEETROOT, ItemBeetroot.class, ItemBeetroot::new);
        registerItem(BEETROOT_SEEDS, ItemSeedsBeetroot.class, ItemSeedsBeetroot::new);
        registerItem(BEETROOT_SOUP, ItemBeetrootSoup.class, ItemBeetrootSoup::new);
        registerItem(SALMON, ItemSalmon.class, ItemSalmon::new);
        registerItem(TROPICAL_FISH, ItemClownfish.class, ItemClownfish::new);
        registerItem(PUFFERFISH, ItemPufferfish.class, ItemPufferfish::new);
        registerItem(COOKED_SALMON, ItemSalmonCooked.class, ItemSalmonCooked::new);

        registerItem(ENCHANTED_GOLDEN_APPLE, ItemAppleGoldEnchanted.class, ItemAppleGoldEnchanted::new);

        registerItem(MUSIC_DISC_11, ItemRecord11.class, ItemRecord11::new);
        registerItem(MUSIC_DISC_CAT, ItemRecordCat.class, ItemRecordCat::new);
        registerItem(MUSIC_DISC_13, ItemRecord13.class, ItemRecord13::new);
        registerItem(MUSIC_DISC_BLOCKS, ItemRecordBlocks.class, ItemRecordBlocks::new);
        registerItem(MUSIC_DISC_CHIRP, ItemRecordChirp.class, ItemRecordChirp::new);
        registerItem(MUSIC_DISC_FAR, ItemRecordFar.class, ItemRecordFar::new);
        registerItem(MUSIC_DISC_WARD, ItemRecordWard.class, ItemRecordWard::new);
        registerItem(MUSIC_DISC_MALL, ItemRecordMall.class, ItemRecordMall::new);
        registerItem(MUSIC_DISC_MELLOHI, ItemRecordMellohi.class, ItemRecordMellohi::new);
        registerItem(MUSIC_DISC_STAL, ItemRecordStal.class, ItemRecordStal::new);
        registerItem(MUSIC_DISC_STRAD, ItemRecordStrad.class, ItemRecordStrad::new);
        registerItem(MUSIC_DISC_WAIT, ItemRecordWait.class, ItemRecordWait::new);

        registerItem(KELP, ItemKelp.class, ItemKelp::new, V1_4_0);
        registerItem(TRIDENT, ItemTrident.class, ItemTrident::new, V1_4_0);
        registerItem(DRIED_KELP, ItemDriedKelp.class, ItemDriedKelp::new, V1_4_0);
        registerItem(HEART_OF_THE_SEA, ItemHeartOfTheSea.class, ItemHeartOfTheSea::new, V1_4_0);

        registerItem(NAUTILUS_SHELL, ItemNautilusShell.class, ItemNautilusShell::new, V1_5_0);
        registerItem(SCUTE, ItemScute.class, ItemScute::new, V1_5_0);
        registerItem(TURTLE_HELMET, ItemTurtleShell.class, ItemTurtleShell::new, V1_5_0);

        registerItem(PHANTOM_MEMBRANE, ItemPhantomMembrane.class, ItemPhantomMembrane::new, V1_6_0);

        registerItem(SPRUCE_SIGN, ItemSignSpruce.class, ItemSignSpruce::new, V1_9_0);
        registerItem(BIRCH_SIGN, ItemSignBirch.class, ItemSignBirch::new, V1_9_0);
        registerItem(JUNGLE_SIGN, ItemSignJungle.class, ItemSignJungle::new, V1_9_0);
        registerItem(ACACIA_SIGN, ItemSignAcacia.class, ItemSignAcacia::new, V1_9_0);
        registerItem(DARK_OAK_SIGN, ItemSignDarkOak.class, ItemSignDarkOak::new, V1_9_0);

        registerItem(BANNER_PATTERN, ItemBannerPattern.class, ItemBannerPattern::new, ItemBannerPattern.UNDEFINED_BANNER_PATTERN - 1, V1_10_0);
        registerItem(CROSSBOW, ItemCrossbow.class, ItemCrossbow::new, V1_10_0);
        registerItem(SHIELD, ItemShield.class, ItemShield::new, V1_10_0);

        registerItem(SWEET_BERRIES, ItemSweetBerries.class, ItemSweetBerries::new, V1_11_0);
        registerItem(CAMPFIRE, ItemCampfire.class, ItemCampfire::new, V1_11_0);

        registerItem(SUSPICIOUS_STEW, ItemSuspiciousStew.class, ItemSuspiciousStew::new, ItemSuspiciousStew.UNDEFINED_STEW - 1, V1_13_0);

        registerItem(HONEY_BOTTLE, ItemHoneyBottle.class, ItemHoneyBottle::new, V1_14_0);
        registerItem(HONEYCOMB, ItemHoneycomb.class, ItemHoneycomb::new, V1_14_0);

        registerItem(LODESTONE_COMPASS, ItemCompassLodestone.class, ItemCompassLodestone::new, V1_16_0);
        registerItem(NETHERITE_INGOT, ItemIngotNetherite.class, ItemIngotNetherite::new, V1_16_0);
        registerItem(NETHERITE_SWORD, ItemSwordNetherite.class, ItemSwordNetherite::new, V1_16_0);
        registerItem(NETHERITE_SHOVEL, ItemShovelNetherite.class, ItemShovelNetherite::new, V1_16_0);
        registerItem(NETHERITE_PICKAXE, ItemPickaxeNetherite.class, ItemPickaxeNetherite::new, V1_16_0);
        registerItem(NETHERITE_AXE, ItemAxeNetherite.class, ItemAxeNetherite::new, V1_16_0);
        registerItem(NETHERITE_HOE, ItemHoeNetherite.class, ItemHoeNetherite::new, V1_16_0);
        registerItem(NETHERITE_HELMET, ItemHelmetNetherite.class, ItemHelmetNetherite::new, V1_16_0);
        registerItem(NETHERITE_CHESTPLATE, ItemChestplateNetherite.class, ItemChestplateNetherite::new, V1_16_0);
        registerItem(NETHERITE_LEGGINGS, ItemLeggingsNetherite.class, ItemLeggingsNetherite::new, V1_16_0);
        registerItem(NETHERITE_BOOTS, ItemBootsNetherite.class, ItemBootsNetherite::new, V1_16_0);
        registerItem(NETHERITE_SCRAP, ItemScrapNetherite.class, ItemScrapNetherite::new, V1_16_0);
        registerItem(CRIMSON_SIGN, ItemSignCrimson.class, ItemSignCrimson::new, V1_16_0);
        registerItem(WARPED_SIGN, ItemSignWarped.class, ItemSignWarped::new, V1_16_0);
        registerItem(CRIMSON_DOOR, ItemDoorCrimson.class, ItemDoorCrimson::new, V1_16_0);
        registerItem(WARPED_DOOR, ItemDoorWarped.class, ItemDoorWarped::new, V1_16_0);
        registerItem(WARPED_FUNGUS_ON_A_STICK, ItemWarpedFungusOnAStick.class, ItemWarpedFungusOnAStick::new, V1_16_0);
        registerItem(CHAIN, ItemChain.class, ItemChain::new, V1_16_0);
        registerItem(MUSIC_DISC_PIGSTEP, ItemRecordPigstep.class, ItemRecordPigstep::new, V1_16_0);
        registerItem(NETHER_SPROUTS, ItemNetherSprouts.class, ItemNetherSprouts::new, V1_16_0);
        registerItem(SOUL_CAMPFIRE, ItemCampfireSoul.class, ItemCampfireSoul::new, V1_16_0);

        registerItem(GLOW_INK_SAC, ItemGlowInkSac.class, ItemGlowInkSac::new, V1_17_0);
        registerItem(RAW_IRON, ItemRawIron.class, ItemRawIron::new, V1_17_0);
        registerItem(RAW_GOLD, ItemRawGold.class, ItemRawGold::new, V1_17_0);
        registerItem(RAW_COPPER, ItemRawCopper.class, ItemRawCopper::new, V1_17_0);
        registerItem(AMETHYST_SHARD, ItemAmethystShard.class, ItemAmethystShard::new, V1_17_0);
        registerItem(SPYGLASS, ItemSpyglass.class, ItemSpyglass::new, V1_17_0);
        registerItem(GLOW_FRAME, ItemItemFrameGlow.class, ItemItemFrameGlow::new, V1_17_0);

        registerItem(MUSIC_DISC_OTHERSIDE, ItemRecordOtherside.class, ItemRecordOtherside::new, V1_18_0);

        registerItem(GOAT_HORN, ItemGoatHorn.class, ItemGoatHorn::new, ItemGoatHorn.UNDEFINED_GOAT_HORN - 1, V1_19_0);
        registerItem(MUSIC_DISC_5, ItemRecord5.class, ItemRecord5::new, V1_19_0);
        registerItem(DISC_FRAGMENT_5, ItemDiscFragment5.class, ItemDiscFragment5::new, V1_19_0);
        registerItem(RECOVERY_COMPASS, ItemCompassRecovery.class, ItemCompassRecovery::new, V1_19_0);
        registerItem(ECHO_SHARD, ItemEchoShard.class, ItemEchoShard::new, V1_19_0);
        registerItem(MANGROVE_DOOR, ItemDoorMangrove.class, ItemDoorMangrove::new, V1_19_0);
        registerItem(MANGROVE_SIGN, ItemSignMangrove.class, ItemSignMangrove::new, V1_19_0);

        registerItem(MUSIC_DISC_RELIC, ItemRecordRelic.class, ItemRecordRelic::new, V1_20_0);

        initializeItemBlockCache();
    }

    public static OptionalInt getItemIdFromIdentifier(String identifier) {
        int id = CUSTOM_ITEM_IDENTIFIER_TO_ID.getOrDefault(identifier, -1);
        if (id == -1) {
            return OptionalInt.empty();
        } else {
            return OptionalInt.of(id);
        }
    }

    /**
     * deferred
     */
    public static void registerVanillaNewItems() {
        registerNewItem("copper_ingot", COPPER_INGOT, ItemIngotCopper.class, ItemIngotCopper::new, V1_17_0);
        registerNewItem("glow_berries", GLOW_BERRIES, ItemGlowBerries.class, ItemGlowBerries::new, V1_17_0);

        registerNewItem("chest_boat", CHEST_BOAT, ItemBoatChest.class, ItemBoatChest::new, ItemBoatChest.UNDEFINED_BOAT - 1, V1_19_0);
        registerNewItemAux("oak_chest_boat", CHEST_BOAT, ItemBoatChest.OAK_BOAT, V1_19_0);
        registerNewItemAux("spruce_chest_boat", CHEST_BOAT, ItemBoatChest.SPRUCE_BOAT, V1_19_0);
        registerNewItemAux("birch_chest_boat", CHEST_BOAT, ItemBoatChest.BIRCH_BOAT, V1_19_0);
        registerNewItemAux("jungle_chest_boat", CHEST_BOAT, ItemBoatChest.JUNGLE_BOAT, V1_19_0);
        registerNewItemAux("acacia_chest_boat", CHEST_BOAT, ItemBoatChest.ACACIA_BOAT, V1_19_0);
        registerNewItemAux("dark_oak_chest_boat", CHEST_BOAT, ItemBoatChest.DARK_OAK_BOAT, V1_19_0);
        registerNewItemAux("mangrove_chest_boat", CHEST_BOAT, ItemBoatChest.MANGROVE_BOAT, V1_19_0);
        registerNewItemAux("bamboo_chest_raft", CHEST_BOAT, ItemBoatChest.BAMBOO_RAFT, V1_20_0);
        registerNewItemAux("cherry_chest_boat", CHEST_BOAT, ItemBoatChest.CHERRY_BOAT, V1_20_0);

//        registerNewItem("bamboo_sign", BAMBOO_SIGN, ItemSignBamboo.class, ItemSignBamboo::new, V1_20_0);
//        registerNewItem("cherry_sign", CHERRY_SIGN, ItemSignCherry.class, ItemSignCherry::new, V1_20_0);
//        registerNewItem("torchflower_seeds", TORCHFLOWER_SEEDS, ItemSeedsTorchflower.class, ItemSeedsTorchflower::new, V1_20_0);
//        registerNewItem("pitcher_pod", PITCHER_POD, ItemPitcherPod.class, ItemPitcherPod::new, V1_20_0);
        registerNewItem("brush", BRUSH, ItemBrush.class, ItemBrush::new, V1_20_0);
        registerNewItem("archer_pottery_sherd", ARCHER_POTTERY_SHERD, ItemPotterySherdArcher.class, ItemPotterySherdArcher::new, V1_20_0);
        registerNewItem("arms_up_pottery_sherd", ARMS_UP_POTTERY_SHERD, ItemPotterySherdArmsUp.class, ItemPotterySherdArmsUp::new, V1_20_0);
        registerNewItem("prize_pottery_sherd", PRIZE_POTTERY_SHERD, ItemPotterySherdPrize.class, ItemPotterySherdPrize::new, V1_20_0);
        registerNewItem("skull_pottery_sherd", SKULL_POTTERY_SHERD, ItemPotterySherdSkull.class, ItemPotterySherdSkull::new, V1_20_0);
        registerNewItem("angler_pottery_sherd", ANGLER_POTTERY_SHERD, ItemPotterySherdAngler.class, ItemPotterySherdAngler::new, V1_20_0);
        registerNewItem("blade_pottery_sherd", BLADE_POTTERY_SHERD, ItemPotterySherdBlade.class, ItemPotterySherdBlade::new, V1_20_0);
        registerNewItem("brewer_pottery_sherd", BREWER_POTTERY_SHERD, ItemPotterySherdBrewer.class, ItemPotterySherdBrewer::new, V1_20_0);
        registerNewItem("burn_pottery_sherd", BURN_POTTERY_SHERD, ItemPotterySherdBurn.class, ItemPotterySherdBurn::new, V1_20_0);
        registerNewItem("danger_pottery_sherd", DANGER_POTTERY_SHERD, ItemPotterySherdDanger.class, ItemPotterySherdDanger::new, V1_20_0);
        registerNewItem("explorer_pottery_sherd", EXPLORER_POTTERY_SHERD, ItemPotterySherdExplorer.class, ItemPotterySherdExplorer::new, V1_20_0);
        registerNewItem("friend_pottery_sherd", FRIEND_POTTERY_SHERD, ItemPotterySherdFriend.class, ItemPotterySherdFriend::new, V1_20_0);
        registerNewItem("heart_pottery_sherd", HEART_POTTERY_SHERD, ItemPotterySherdHeart.class, ItemPotterySherdHeart::new, V1_20_0);
        registerNewItem("heartbreak_pottery_sherd", HEARTBREAK_POTTERY_SHERD, ItemPotterySherdHeartbreak.class, ItemPotterySherdHeartbreak::new, V1_20_0);
        registerNewItem("howl_pottery_sherd", HOWL_POTTERY_SHERD, ItemPotterySherdHowl.class, ItemPotterySherdHowl::new, V1_20_0);
        registerNewItem("miner_pottery_sherd", MINER_POTTERY_SHERD, ItemPotterySherdMiner.class, ItemPotterySherdMiner::new, V1_20_0);
        registerNewItem("mourner_pottery_sherd", MOURNER_POTTERY_SHERD, ItemPotterySherdMourner.class, ItemPotterySherdMourner::new, V1_20_0);
        registerNewItem("plenty_pottery_sherd", PLENTY_POTTERY_SHERD, ItemPotterySherdPlenty.class, ItemPotterySherdPlenty::new, V1_20_0);
        registerNewItem("sheaf_pottery_sherd", SHEAF_POTTERY_SHERD, ItemPotterySherdSheaf.class, ItemPotterySherdSheaf::new, V1_20_0);
        registerNewItem("shelter_pottery_sherd", SHELTER_POTTERY_SHERD, ItemPotterySherdShelter.class, ItemPotterySherdShelter::new, V1_20_0);
        registerNewItem("snort_pottery_sherd", SNORT_POTTERY_SHERD, ItemPotterySherdSnort.class, ItemPotterySherdSnort::new, V1_20_0);
        registerNewItem("netherite_upgrade_smithing_template", NETHERITE_UPGRADE_SMITHING_TEMPLATE, ItemSmithingTemplateUpgradeNetherite.class, ItemSmithingTemplateUpgradeNetherite::new, V1_20_0);
        registerNewItem("sentry_armor_trim_smithing_template", SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, ItemSmithingTemplateArmorTrimSentry.class, ItemSmithingTemplateArmorTrimSentry::new, V1_20_0);
        registerNewItem("dune_armor_trim_smithing_template", DUNE_ARMOR_TRIM_SMITHING_TEMPLATE, ItemSmithingTemplateArmorTrimDune.class, ItemSmithingTemplateArmorTrimDune::new, V1_20_0);
        registerNewItem("coast_armor_trim_smithing_template", COAST_ARMOR_TRIM_SMITHING_TEMPLATE, ItemSmithingTemplateArmorTrimCoast.class, ItemSmithingTemplateArmorTrimCoast::new, V1_20_0);
        registerNewItem("wild_armor_trim_smithing_template", WILD_ARMOR_TRIM_SMITHING_TEMPLATE, ItemSmithingTemplateArmorTrimWild.class, ItemSmithingTemplateArmorTrimWild::new, V1_20_0);
        registerNewItem("ward_armor_trim_smithing_template", WARD_ARMOR_TRIM_SMITHING_TEMPLATE, ItemSmithingTemplateArmorTrimWard.class, ItemSmithingTemplateArmorTrimWard::new, V1_20_0);
        registerNewItem("eye_armor_trim_smithing_template", EYE_ARMOR_TRIM_SMITHING_TEMPLATE, ItemSmithingTemplateArmorTrimEye.class, ItemSmithingTemplateArmorTrimEye::new, V1_20_0);
        registerNewItem("vex_armor_trim_smithing_template", VEX_ARMOR_TRIM_SMITHING_TEMPLATE, ItemSmithingTemplateArmorTrimVex.class, ItemSmithingTemplateArmorTrimVex::new, V1_20_0);
        registerNewItem("tide_armor_trim_smithing_template", TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, ItemSmithingTemplateArmorTrimTide.class, ItemSmithingTemplateArmorTrimTide::new, V1_20_0);
        registerNewItem("snout_armor_trim_smithing_template", SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, ItemSmithingTemplateArmorTrimSnout.class, ItemSmithingTemplateArmorTrimSnout::new, V1_20_0);
        registerNewItem("rib_armor_trim_smithing_template", RIB_ARMOR_TRIM_SMITHING_TEMPLATE, ItemSmithingTemplateArmorTrimRib.class, ItemSmithingTemplateArmorTrimRib::new, V1_20_0);
        registerNewItem("spire_armor_trim_smithing_template", SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, ItemSmithingTemplateArmorTrimSpire.class, ItemSmithingTemplateArmorTrimSpire::new, V1_20_0);
        registerNewItem("silence_armor_trim_smithing_template", SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, ItemSmithingTemplateArmorTrimSilence.class, ItemSmithingTemplateArmorTrimSilence::new, V1_20_0);
        registerNewItem("wayfinder_armor_trim_smithing_template", WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE, ItemSmithingTemplateArmorTrimWayfinder.class, ItemSmithingTemplateArmorTrimWayfinder::new, V1_20_0);
        registerNewItem("raiser_armor_trim_smithing_template", RAISER_ARMOR_TRIM_SMITHING_TEMPLATE, ItemSmithingTemplateArmorTrimRaiser.class, ItemSmithingTemplateArmorTrimRaiser::new, V1_20_0);
        registerNewItem("shaper_armor_trim_smithing_template", SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE, ItemSmithingTemplateArmorTrimShaper.class, ItemSmithingTemplateArmorTrimShaper::new, V1_20_0);
        registerNewItem("host_armor_trim_smithing_template", HOST_ARMOR_TRIM_SMITHING_TEMPLATE, ItemSmithingTemplateArmorTrimHost.class, ItemSmithingTemplateArmorTrimHost::new, V1_20_0);

    }

    private static Class<? extends Item> registerItem(int id, Class<? extends Item> clazz, ItemFactory factory) {
        return registerItem(id, clazz, factory, 0);
    }

    private static Class<? extends Item> registerItem(int id, Class<? extends Item> clazz, ItemFactory factory, int maxAuxVal) {
        Item.list[id] = clazz;
        ITEM_FACTORIES[id] = factory;

        Item item = factory.create(0, 1);

        int variantCount;
        if (item instanceof ItemDurable) {
            variantCount = item.getMaxDurability() + 1;
        } else {
            variantCount = maxAuxVal + 1;
        }

        Item[] variants = new Item[variantCount];
        variants[0] = item;

        for (int meta = 1; meta < variantCount; meta++) {
            variants[meta] = factory.create(meta, 1);
        }

        ITEM_CACHE[id] = variants;

        return clazz;
    }

    /**
     * @param version min required base game version
     */
    private static Class<? extends Item> registerItem(int id, Class<? extends Item> clazz, ItemFactory factory, GameVersion version) {
        return registerItem(id, clazz, factory, 0, version);
    }

    /**
     * @param version min required base game version
     */
    private static Class<? extends Item> registerItem(int id, Class<? extends Item> clazz, ItemFactory factory, int maxAuxVal, GameVersion version) {
        if (!version.isAvailable()) {
            return null;
        }
        return registerItem(id, clazz, factory, maxAuxVal);
    }

    /**
     * @param version min required base game version
     */
    private static Class<? extends Item> registerNewItem(String identifier, int id, Class<? extends Item> clazz, ItemFactory factory, GameVersion version) {
        if (!ENABLE_ITEM_NAME_PERSISTENCE) {
            return null;
        }
        if (!version.isAvailable()) {
            return null;
        }
        ItemSerializer.registerItem("minecraft:" + identifier, id);
        return registerItem(id, clazz, factory);
    }

    /**
     * @param version min required base game version
     */
    private static Class<? extends Item> registerNewItem(String identifier, int id, Class<? extends Item> clazz, ItemFactory factory, int maxAuxVal, GameVersion version) {
        if (!ENABLE_ITEM_NAME_PERSISTENCE) {
            return null;
        }
        if (!version.isAvailable()) {
            return null;
        }
        ItemSerializer.registerItem("minecraft:" + identifier, id, maxAuxVal);
        return registerItem(id, clazz, factory, maxAuxVal);
    }

    /**
     * @param version min required base game version
     */
    private static void registerNewItemAux(String identifier, int id, int meta, GameVersion version) {
        if (!ENABLE_ITEM_NAME_PERSISTENCE) {
            return;
        }
        if (!version.isAvailable()) {
            return;
        }
        ItemSerializer.registerItemAux("minecraft:" + identifier, id, meta);
    }

    public static Class<? extends Item> registerCustomItem(String identifier, int id, Class<? extends Item> clazz, ItemFactory factory) {
        return registerCustomItem(identifier, id, clazz, factory, null);
    }

    public static Class<? extends Item> registerCustomItem(String identifier, int id, Class<? extends Item> clazz, ItemFactory factory, CompoundTag compound) {
        if (identifier.startsWith("minecraft:")) {
            throw new IllegalArgumentException("Invalid identifier: " + identifier);
        }
        ItemSerializer.registerCustomItem(identifier, id, compound);
        CUSTOM_ITEM_IDENTIFIER_TO_ID.put(identifier, id);
        return registerItem(id, clazz, factory);
    }

    public static int allocateCustomItemId() {
        return CUSTOM_ITEM_ID_ALLOCATOR.getAndIncrement();
    }

    private static void initializeItemBlockCache() {
        BLOCK_CACHE[AIR] = new Item[]{new ItemBlock(Block.fromItemId(AIR, 0), 0, 0)};

        for (int id = 1; id <= 0xff; id++) {
            Block[] blockVariants = Block.variantList[id];
            if (blockVariants == null) {
                continue;
            }
            createBlockItemCache(id, blockVariants.length);
        }

        for (int id = -2; id > ItemBlockID.UNDEFINED; id--) {
            int blockId = 0xff - id;
            Block[] blockVariants = Block.variantList[blockId];
            if (blockVariants == null) {
                continue;
            }
            createBlockItemCache(blockId, blockVariants.length);
        }

        for (int id = 0; id < 256; id++) {
            if (ITEM_CACHE[id] == null && BLOCK_CACHE[id] != null) {
                ITEM_CACHE[id] = BLOCK_CACHE[id];
            }
        }
    }

    private static void createBlockItemCache(int blockId, int count) {
        Item[] variants = new Item[count];
        for (int meta = 0; meta < count; meta++) {
            variants[meta] = new ItemBlock(Block.get(blockId, meta), meta, 1);
        }
        BLOCK_CACHE[blockId] = variants;
    }

    public static Item get(int id, int meta, int count, byte[] tags) {
        Item item;

        try {
            Item[] variants = id >= 0 ? ITEM_CACHE[id] : BLOCK_CACHE[0xff - id];
            if (variants != null) {
                if (meta >= 0 && meta < variants.length) {
                    item = variants[meta].clone();

                    item.count = count;
                    if (tags.length != 0) {
                        item.setCompoundTag(tags);
                    }

                    return item;
                }
            }

            if (id < 256 && id != 166 && Block.list[id >= 0 ? id : 0xff - id] != null) {
                item = new ItemBlock(Block.fromItemId(id, meta), meta, count);
            } else {
                ItemFactory factory = id >= 0 ? ITEM_FACTORIES[id] : null;
                if (factory != null) {
                    item = factory.create(meta, count);
                } else {
                    item = new Item(id, meta, count);
                }
            }
        } catch (Exception e) {
            item = new Item(id, meta, count);
        }

        if (tags.length != 0) {
            item.setCompoundTag(tags);
        }

        return item;
    }

    public static Item get(int id, Integer meta, int count, byte[] tags) {
        Item item;

        try {
            if (meta != null) {
                Item[] variants = id >= 0 ? ITEM_CACHE[id] : BLOCK_CACHE[0xff - id];
                if (variants != null) {
                    int aux = meta;
                    if (aux >= 0 && aux < variants.length) {
                        item = variants[meta].clone();

                        item.count = count;
                        if (tags.length != 0) {
                            item.setCompoundTag(tags);
                        }

                        return item;
                    }
                }
            }

            if (id < 256 && id != 166 && Block.list[id >= 0 ? id : 0xff - id] != null) {
                item = new ItemBlock(Block.fromItemId(id, meta), meta, count);
            } else {
                ItemFactory factory = id >= 0 ? ITEM_FACTORIES[id] : null;
                if (factory != null) {
                    item = factory.create(meta, count);
                } else {
                    item = new Item(id, meta, count);
                }
            }
        } catch (Exception e) {
            item = new Item(id, meta, count);
        }

        if (tags.length != 0) {
            item.setCompoundTag(tags);
        }

        return item;
    }

    public static Item air() {
        return Item.get(AIR, 0, 0);
    }

    private Items() {
        throw new IllegalStateException();
    }
}
