package cn.nukkit.item;

import cn.nukkit.GameVersion;
import cn.nukkit.block.Block;
import cn.nukkit.block.Blocks;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandEnumConstraint;
import cn.nukkit.entity.EntityID;
import cn.nukkit.loot.LootTables;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.potion.PotionID;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nullable;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.IntFunction;

import static cn.nukkit.GameVersion.*;
import static cn.nukkit.SharedConstants.*;
import static cn.nukkit.item.ItemID.*;

@Log4j2
public final class Items {
    static final int BASE_INTERNAL_ID = 0x3ff;

    private static final ItemFactory[] ITEM_FACTORIES = new ItemFactory[Short.MAX_VALUE];

    private static final Item[][] ITEM_CACHE = new Item[Short.MAX_VALUE][];
    private static final Item[][] BLOCK_CACHE = new Item[Block.BLOCK_ID_COUNT][];

    private static final Object2IntMap<String> NAME_TO_ID = new Object2IntOpenHashMap<>();
    private static final Object2IntMap<String> FULL_NAME_TO_ID = new Object2IntOpenHashMap<>();
    private static final String[] ID_TO_NAME = new String[Short.MAX_VALUE];
    private static final String[] ID_TO_FULL_NAME = new String[Short.MAX_VALUE];
    private static final Map<String, String> SIMPLE_ALIASES_MAP = new Object2ObjectOpenHashMap<>();
    private static final Map<String, int[]> COMPLEX_ALIASES_MAP = new Object2ObjectOpenHashMap<>();

    private static final Set<String>[][] ITEM_ID_TO_TAGS = new Set[Short.MAX_VALUE][];
    private static final Set<String>[] BLOCK_ID_TO_TAGS = new Set[Block.BLOCK_ID_COUNT];
    private static final Map<String, IntSet> TAG_TO_ITEM_FULL_IDS = new Object2ObjectOpenHashMap<>();

    private static final AtomicInteger CUSTOM_ITEM_ID_ALLOCATOR = new AtomicInteger(CUSTOM_ITEM);

    static {
        NAME_TO_ID.defaultReturnValue(-1);
        FULL_NAME_TO_ID.defaultReturnValue(-1);
    }

    static void registerVanillaItems() {
        registerItem(ItemNames.IRON_SHOVEL, IRON_SHOVEL, ItemShovelIron.class, ItemShovelIron::new);
        registerItem(ItemNames.IRON_PICKAXE, IRON_PICKAXE, ItemPickaxeIron.class, ItemPickaxeIron::new);
        registerItem(ItemNames.IRON_AXE, IRON_AXE, ItemAxeIron.class, ItemAxeIron::new);
        registerItem(ItemNames.FLINT_AND_STEEL, FLINT_AND_STEEL, ItemFlintSteel.class, ItemFlintSteel::new);
        registerItem(ItemNames.APPLE, APPLE, ItemApple.class, ItemApple::new);
        registerItem(ItemNames.BOW, BOW, ItemBow.class, ItemBow::new);
        registerItem(ItemNames.ARROW, ARROW, ItemArrow.class, ItemArrow::new, ItemArrow.TIPPED_ARROW + PotionID.UNDEFINED - 1);
        registerItem(ItemNames.COAL, COAL, ItemCoal.class, ItemCoal::new, -(ItemCoal.COALS.length - 1));
        registerItem(ItemNames.DIAMOND, DIAMOND, ItemDiamond.class, ItemDiamond::new);
        registerItem(ItemNames.IRON_INGOT, IRON_INGOT, ItemIngotIron.class, ItemIngotIron::new);
        registerItem(ItemNames.GOLD_INGOT, GOLD_INGOT, ItemIngotGold.class, ItemIngotGold::new);
        registerItem(ItemNames.IRON_SWORD, IRON_SWORD, ItemSwordIron.class, ItemSwordIron::new);
        registerItem(ItemNames.WOODEN_SWORD, WOODEN_SWORD, ItemSwordWood.class, ItemSwordWood::new);
        registerItem(ItemNames.WOODEN_SHOVEL, WOODEN_SHOVEL, ItemShovelWood.class, ItemShovelWood::new);
        registerItem(ItemNames.WOODEN_PICKAXE, WOODEN_PICKAXE, ItemPickaxeWood.class, ItemPickaxeWood::new);
        registerItem(ItemNames.WOODEN_AXE, WOODEN_AXE, ItemAxeWood.class, ItemAxeWood::new);
        registerItem(ItemNames.STONE_SWORD, STONE_SWORD, ItemSwordStone.class, ItemSwordStone::new);
        registerItem(ItemNames.STONE_SHOVEL, STONE_SHOVEL, ItemShovelStone.class, ItemShovelStone::new);
        registerItem(ItemNames.STONE_PICKAXE, STONE_PICKAXE, ItemPickaxeStone.class, ItemPickaxeStone::new);
        registerItem(ItemNames.STONE_AXE, STONE_AXE, ItemAxeStone.class, ItemAxeStone::new);
        registerItem(ItemNames.DIAMOND_SWORD, DIAMOND_SWORD, ItemSwordDiamond.class, ItemSwordDiamond::new);
        registerItem(ItemNames.DIAMOND_SHOVEL, DIAMOND_SHOVEL, ItemShovelDiamond.class, ItemShovelDiamond::new);
        registerItem(ItemNames.DIAMOND_PICKAXE, DIAMOND_PICKAXE, ItemPickaxeDiamond.class, ItemPickaxeDiamond::new);
        registerItem(ItemNames.DIAMOND_AXE, DIAMOND_AXE, ItemAxeDiamond.class, ItemAxeDiamond::new);
        registerItem(ItemNames.STICK, STICK, ItemStick.class, ItemStick::new);
        registerItem(ItemNames.BOWL, BOWL, ItemBowl.class, ItemBowl::new);
        registerItem(ItemNames.MUSHROOM_STEW, MUSHROOM_STEW, ItemMushroomStew.class, ItemMushroomStew::new);
        registerItem(ItemNames.GOLDEN_SWORD, GOLDEN_SWORD, ItemSwordGold.class, ItemSwordGold::new);
        registerItem(ItemNames.GOLDEN_SHOVEL, GOLDEN_SHOVEL, ItemShovelGold.class, ItemShovelGold::new);
        registerItem(ItemNames.GOLDEN_PICKAXE, GOLDEN_PICKAXE, ItemPickaxeGold.class, ItemPickaxeGold::new);
        registerItem(ItemNames.GOLDEN_AXE, GOLDEN_AXE, ItemAxeGold.class, ItemAxeGold::new);
        registerItem(ItemNames.STRING, STRING, ItemString.class, ItemString::new);
        registerItem(ItemNames.FEATHER, FEATHER, ItemFeather.class, ItemFeather::new);
        registerItem(ItemNames.GUNPOWDER, GUNPOWDER, ItemGunpowder.class, ItemGunpowder::new);
        registerItem(ItemNames.WOODEN_HOE, WOODEN_HOE, ItemHoeWood.class, ItemHoeWood::new);
        registerItem(ItemNames.STONE_HOE, STONE_HOE, ItemHoeStone.class, ItemHoeStone::new);
        registerItem(ItemNames.IRON_HOE, IRON_HOE, ItemHoeIron.class, ItemHoeIron::new);
        registerItem(ItemNames.DIAMOND_HOE, DIAMOND_HOE, ItemHoeDiamond.class, ItemHoeDiamond::new);
        registerItem(ItemNames.GOLDEN_HOE, GOLDEN_HOE, ItemHoeGold.class, ItemHoeGold::new);
        registerItem(ItemNames.WHEAT_SEEDS, WHEAT_SEEDS, ItemSeedsWheat.class, ItemSeedsWheat::new);
        registerItem(ItemNames.WHEAT, WHEAT, ItemWheat.class, ItemWheat::new);
        registerItem(ItemNames.BREAD, BREAD, ItemBread.class, ItemBread::new);
        registerItem(ItemNames.LEATHER_HELMET, LEATHER_HELMET, ItemHelmetLeather.class, ItemHelmetLeather::new);
        registerItem(ItemNames.LEATHER_CHESTPLATE, LEATHER_CHESTPLATE, ItemChestplateLeather.class, ItemChestplateLeather::new);
        registerItem(ItemNames.LEATHER_LEGGINGS, LEATHER_LEGGINGS, ItemLeggingsLeather.class, ItemLeggingsLeather::new);
        registerItem(ItemNames.LEATHER_BOOTS, LEATHER_BOOTS, ItemBootsLeather.class, ItemBootsLeather::new);
        registerItem(ItemNames.CHAINMAIL_HELMET, CHAINMAIL_HELMET, ItemHelmetChain.class, ItemHelmetChain::new);
        registerItem(ItemNames.CHAINMAIL_CHESTPLATE, CHAINMAIL_CHESTPLATE, ItemChestplateChain.class, ItemChestplateChain::new);
        registerItem(ItemNames.CHAINMAIL_LEGGINGS, CHAINMAIL_LEGGINGS, ItemLeggingsChain.class, ItemLeggingsChain::new);
        registerItem(ItemNames.CHAINMAIL_BOOTS, CHAINMAIL_BOOTS, ItemBootsChain.class, ItemBootsChain::new);
        registerItem(ItemNames.IRON_HELMET, IRON_HELMET, ItemHelmetIron.class, ItemHelmetIron::new);
        registerItem(ItemNames.IRON_CHESTPLATE, IRON_CHESTPLATE, ItemChestplateIron.class, ItemChestplateIron::new);
        registerItem(ItemNames.IRON_LEGGINGS, IRON_LEGGINGS, ItemLeggingsIron.class, ItemLeggingsIron::new);
        registerItem(ItemNames.IRON_BOOTS, IRON_BOOTS, ItemBootsIron.class, ItemBootsIron::new);
        registerItem(ItemNames.DIAMOND_HELMET, DIAMOND_HELMET, ItemHelmetDiamond.class, ItemHelmetDiamond::new);
        registerItem(ItemNames.DIAMOND_CHESTPLATE, DIAMOND_CHESTPLATE, ItemChestplateDiamond.class, ItemChestplateDiamond::new);
        registerItem(ItemNames.DIAMOND_LEGGINGS, DIAMOND_LEGGINGS, ItemLeggingsDiamond.class, ItemLeggingsDiamond::new);
        registerItem(ItemNames.DIAMOND_BOOTS, DIAMOND_BOOTS, ItemBootsDiamond.class, ItemBootsDiamond::new);
        registerItem(ItemNames.GOLDEN_HELMET, GOLDEN_HELMET, ItemHelmetGold.class, ItemHelmetGold::new);
        registerItem(ItemNames.GOLDEN_CHESTPLATE, GOLDEN_CHESTPLATE, ItemChestplateGold.class, ItemChestplateGold::new);
        registerItem(ItemNames.GOLDEN_LEGGINGS, GOLDEN_LEGGINGS, ItemLeggingsGold.class, ItemLeggingsGold::new);
        registerItem(ItemNames.GOLDEN_BOOTS, GOLDEN_BOOTS, ItemBootsGold.class, ItemBootsGold::new);
        registerItem(ItemNames.FLINT, FLINT, ItemFlint.class, ItemFlint::new);
        registerItem(ItemNames.PORKCHOP, PORKCHOP, ItemPorkchopRaw.class, ItemPorkchopRaw::new);
        registerItem(ItemNames.COOKED_PORKCHOP, COOKED_PORKCHOP, ItemPorkchopCooked.class, ItemPorkchopCooked::new);
        registerItem(ItemNames.PAINTING, PAINTING, ItemPainting.class, ItemPainting::new);
        registerItem(ItemNames.GOLDEN_APPLE, GOLDEN_APPLE, ItemAppleGold.class, ItemAppleGold::new);
        registerItem(ItemNames.OAK_SIGN, OAK_SIGN, ItemSign.class, ItemSign::new);
        registerItem(ItemNames.WOODEN_DOOR, WOODEN_DOOR, ItemDoorWood.class, ItemDoorWood::new);
        registerItem(ItemNames.BUCKET, BUCKET, ItemBucket.class, ItemBucket::new, -(ItemBucket.BUCKETS.length - 1));

        registerItem(ItemNames.MINECART, MINECART, ItemMinecart.class, ItemMinecart::new);
        registerItem(ItemNames.SADDLE, SADDLE, ItemSaddle.class, ItemSaddle::new);
        registerItem(ItemNames.IRON_DOOR, IRON_DOOR, ItemDoorIron.class, ItemDoorIron::new);
        registerItem(ItemNames.REDSTONE, REDSTONE, ItemRedstone.class, ItemRedstone::new);
        registerItem(ItemNames.SNOWBALL, SNOWBALL, ItemSnowball.class, ItemSnowball::new);
        registerItem(ItemNames.OAK_BOAT, OAK_BOAT, ItemBoatOak.class, ItemBoatOak::new, -(ItemBoat.BOATS.length - 1));
        registerItem(ItemNames.LEATHER, LEATHER, ItemLeather.class, ItemLeather::new);

        registerItem(ItemNames.BRICK, BRICK, ItemBrick.class, ItemBrick::new);
        registerItem(ItemNames.CLAY_BALL, CLAY_BALL, ItemClay.class, ItemClay::new);
        registerItem(ItemNames.SUGAR_CANE, SUGAR_CANE, ItemSugarcane.class, ItemSugarcane::new);
        registerItem(ItemNames.PAPER, PAPER, ItemPaper.class, ItemPaper::new);
        registerItem(ItemNames.BOOK, BOOK, ItemBook.class, ItemBook::new);
        registerItem(ItemNames.SLIME_BALL, SLIME_BALL, ItemSlimeball.class, ItemSlimeball::new);
        registerItem(ItemNames.CHEST_MINECART, CHEST_MINECART, ItemMinecartChest.class, ItemMinecartChest::new);

        registerItem(ItemNames.EGG, EGG, ItemEgg.class, ItemEgg::new);
        registerItem(ItemNames.COMPASS, COMPASS, ItemCompass.class, ItemCompass::new);
        registerItem(ItemNames.FISHING_ROD, FISHING_ROD, ItemFishingRod.class, ItemFishingRod::new);
        registerItem(ItemNames.CLOCK, CLOCK, ItemClock.class, ItemClock::new);
        registerItem(ItemNames.GLOWSTONE_DUST, GLOWSTONE_DUST, ItemGlowstoneDust.class, ItemGlowstoneDust::new);
        registerItem(ItemNames.COD, COD, ItemFish.class, ItemFish::new);
        registerItem(ItemNames.COOKED_COD, COOKED_COD, ItemFishCooked.class, ItemFishCooked::new);
        registerItem(ItemNames.INK_SAC, INK_SAC, ItemInkSac.class, ItemInkSac::new, -(ItemDye.DYES.length - 1));
        registerItem(ItemNames.BONE, BONE, ItemBone.class, ItemBone::new);
        registerItem(ItemNames.SUGAR, SUGAR, ItemSugar.class, ItemSugar::new);
        registerItem(ItemNames.CAKE, CAKE, ItemCake.class, ItemCake::new);
        registerItem(ItemNames.BED, BED, ItemBed.class, ItemBed::new, 15);
        registerItem(ItemNames.REPEATER, REPEATER, ItemRedstoneRepeater.class, ItemRedstoneRepeater::new);
        registerItem(ItemNames.COOKIE, COOKIE, ItemCookie.class, ItemCookie::new);
        registerItem(ItemNames.FILLED_MAP, FILLED_MAP, ItemMap.class, ItemMap::new, ItemMap.UNDEFINED_MAP - 1);
        registerItem(ItemNames.SHEARS, SHEARS, ItemShears.class, ItemShears::new);
        registerItem(ItemNames.MELON_SLICE, MELON_SLICE, ItemMelon.class, ItemMelon::new);
        registerItem(ItemNames.PUMPKIN_SEEDS, PUMPKIN_SEEDS, ItemSeedsPumpkin.class, ItemSeedsPumpkin::new);
        registerItem(ItemNames.MELON_SEEDS, MELON_SEEDS, ItemSeedsMelon.class, ItemSeedsMelon::new);
        registerItem(ItemNames.BEEF, BEEF, ItemBeefRaw.class, ItemBeefRaw::new);
        registerItem(ItemNames.COOKED_BEEF, COOKED_BEEF, ItemSteak.class, ItemSteak::new);
        registerItem(ItemNames.CHICKEN, CHICKEN, ItemChickenRaw.class, ItemChickenRaw::new);
        registerItem(ItemNames.COOKED_CHICKEN, COOKED_CHICKEN, ItemChickenCooked.class, ItemChickenCooked::new);
        registerItem(ItemNames.ROTTEN_FLESH, ROTTEN_FLESH, ItemRottenFlesh.class, ItemRottenFlesh::new);
        registerItem(ItemNames.ENDER_PEARL, ENDER_PEARL, ItemEnderPearl.class, ItemEnderPearl::new);
        registerItem(ItemNames.BLAZE_ROD, BLAZE_ROD, ItemBlazeRod.class, ItemBlazeRod::new);
        registerItem(ItemNames.GHAST_TEAR, GHAST_TEAR, ItemGhastTear.class, ItemGhastTear::new);
        registerItem(ItemNames.GOLD_NUGGET, GOLD_NUGGET, ItemNuggetGold.class, ItemNuggetGold::new);
        registerItem(ItemNames.NETHER_WART, NETHER_WART, ItemNetherWart.class, ItemNetherWart::new);
        registerItem(ItemNames.POTION, POTION, ItemPotion.class, ItemPotion::new, PotionID.UNDEFINED - 1);
        registerItem(ItemNames.GLASS_BOTTLE, GLASS_BOTTLE, ItemGlassBottle.class, ItemGlassBottle::new);
        registerItem(ItemNames.SPIDER_EYE, SPIDER_EYE, ItemSpiderEye.class, ItemSpiderEye::new);
        registerItem(ItemNames.FERMENTED_SPIDER_EYE, FERMENTED_SPIDER_EYE, ItemSpiderEyeFermented.class, ItemSpiderEyeFermented::new);
        registerItem(ItemNames.BLAZE_POWDER, BLAZE_POWDER, ItemBlazePowder.class, ItemBlazePowder::new);
        registerItem(ItemNames.MAGMA_CREAM, MAGMA_CREAM, ItemMagmaCream.class, ItemMagmaCream::new);
        registerItem(ItemNames.BREWING_STAND, BREWING_STAND, ItemBrewingStand.class, ItemBrewingStand::new);
        registerItem(ItemNames.CAULDRON, CAULDRON, ItemCauldron.class, ItemCauldron::new);
        registerItem(ItemNames.ENDER_EYE, ENDER_EYE, ItemEnderEye.class, ItemEnderEye::new);
        registerItem(ItemNames.GLISTERING_MELON_SLICE, GLISTERING_MELON_SLICE, ItemMelonGlistering.class, ItemMelonGlistering::new);
        registerItem(ItemNames.SPAWN_EGG, SPAWN_EGG, ItemSpawnEgg.class, ItemSpawnEgg::new, 0xff);
        registerItem(ItemNames.EXPERIENCE_BOTTLE, EXPERIENCE_BOTTLE, ItemExpBottle.class, ItemExpBottle::new);
        registerItem(ItemNames.FIRE_CHARGE, FIRE_CHARGE, ItemFireCharge.class, ItemFireCharge::new);
        registerItem(ItemNames.WRITABLE_BOOK, WRITABLE_BOOK, ItemBookAndQuill.class, ItemBookAndQuill::new);
        registerItem(ItemNames.WRITTEN_BOOK, WRITTEN_BOOK, ItemBookWritten.class, ItemBookWritten::new);
        registerItem(ItemNames.EMERALD, EMERALD, ItemEmerald.class, ItemEmerald::new);
        registerItem(ItemNames.FRAME, FRAME, ItemItemFrame.class, ItemItemFrame::new);
        registerItem(ItemNames.FLOWER_POT, FLOWER_POT, ItemFlowerPot.class, ItemFlowerPot::new);
        registerItem(ItemNames.CARROT, CARROT, ItemCarrot.class, ItemCarrot::new);
        registerItem(ItemNames.POTATO, POTATO, ItemPotato.class, ItemPotato::new);
        registerItem(ItemNames.BAKED_POTATO, BAKED_POTATO, ItemPotatoBaked.class, ItemPotatoBaked::new);
        registerItem(ItemNames.POISONOUS_POTATO, POISONOUS_POTATO, ItemPotatoPoisonous.class, ItemPotatoPoisonous::new);
        registerItem(ItemNames.EMPTY_MAP, EMPTY_MAP, ItemEmptyMap.class, ItemEmptyMap::new, ItemEmptyMap.UNDEFINED_EMPTY_MAP - 1);
        registerItem(ItemNames.GOLDEN_CARROT, GOLDEN_CARROT, ItemCarrotGolden.class, ItemCarrotGolden::new);
        registerItem(ItemNames.SKULL, SKULL, ItemSkull.class, ItemSkull::new, ItemSkull.HEAD_UNDEFINED - 1);
        registerItem(ItemNames.CARROT_ON_A_STICK, CARROT_ON_A_STICK, ItemCarrotOnAStick.class, ItemCarrotOnAStick::new);
        registerItem(ItemNames.NETHER_STAR, NETHER_STAR, ItemNetherStar.class, ItemNetherStar::new);
        registerItem(ItemNames.PUMPKIN_PIE, PUMPKIN_PIE, ItemPumpkinPie.class, ItemPumpkinPie::new);
        registerItem(ItemNames.FIREWORK_ROCKET, FIREWORK_ROCKET, ItemFirework.class, ItemFirework::new);
        registerItem(ItemNames.FIREWORK_STAR, FIREWORK_STAR, ItemFireworkStar.class, ItemFireworkStar::new, 15);
        registerItem(ItemNames.ENCHANTED_BOOK, ENCHANTED_BOOK, ItemBookEnchanted.class, ItemBookEnchanted::new);
        registerItem(ItemNames.COMPARATOR, COMPARATOR, ItemRedstoneComparator.class, ItemRedstoneComparator::new);
        registerItem(ItemNames.NETHERBRICK, NETHERBRICK, ItemNetherBrick.class, ItemNetherBrick::new);
        registerItem(ItemNames.QUARTZ, QUARTZ, ItemQuartz.class, ItemQuartz::new);
        registerItem(ItemNames.TNT_MINECART, TNT_MINECART, ItemMinecartTNT.class, ItemMinecartTNT::new);
        registerItem(ItemNames.HOPPER_MINECART, HOPPER_MINECART, ItemMinecartHopper.class, ItemMinecartHopper::new);
        registerItem(ItemNames.PRISMARINE_SHARD, PRISMARINE_SHARD, ItemPrismarineShard.class, ItemPrismarineShard::new);
        registerItem(ItemNames.HOPPER, HOPPER, ItemHopper.class, ItemHopper::new);
        registerItem(ItemNames.RABBIT, RABBIT, ItemRabbitRaw.class, ItemRabbitRaw::new);
        registerItem(ItemNames.COOKED_RABBIT, COOKED_RABBIT, ItemRabbitCooked.class, ItemRabbitCooked::new);
        registerItem(ItemNames.RABBIT_STEW, RABBIT_STEW, ItemRabbitStew.class, ItemRabbitStew::new);
        registerItem(ItemNames.RABBIT_FOOT, RABBIT_FOOT, ItemRabbitFoot.class, ItemRabbitFoot::new);
        registerItem(ItemNames.RABBIT_HIDE, RABBIT_HIDE, ItemRabbitHide.class, ItemRabbitHide::new);
        registerItem(ItemNames.LEATHER_HORSE_ARMOR, LEATHER_HORSE_ARMOR, ItemHorseArmorLeather.class, ItemHorseArmorLeather::new);
        registerItem(ItemNames.IRON_HORSE_ARMOR, IRON_HORSE_ARMOR, ItemHorseArmorIron.class, ItemHorseArmorIron::new);
        registerItem(ItemNames.GOLDEN_HORSE_ARMOR, GOLDEN_HORSE_ARMOR, ItemHorseArmorGold.class, ItemHorseArmorGold::new);
        registerItem(ItemNames.DIAMOND_HORSE_ARMOR, DIAMOND_HORSE_ARMOR, ItemHorseArmorDiamond.class, ItemHorseArmorDiamond::new);
        registerItem(ItemNames.LEAD, LEAD, ItemLead.class, ItemLead::new);
        registerItem(ItemNames.NAME_TAG, NAME_TAG, ItemNameTag.class, ItemNameTag::new);
        registerItem(ItemNames.PRISMARINE_CRYSTALS, PRISMARINE_CRYSTALS, ItemPrismarineCrystals.class, ItemPrismarineCrystals::new);
        registerItem(ItemNames.MUTTON, MUTTON, ItemMuttonRaw.class, ItemMuttonRaw::new);
        registerItem(ItemNames.COOKED_MUTTON, COOKED_MUTTON, ItemMuttonCooked.class, ItemMuttonCooked::new);
        registerItem(ItemNames.ARMOR_STAND, ARMOR_STAND, ItemArmorStand.class, ItemArmorStand::new);
        registerItem(ItemNames.END_CRYSTAL, END_CRYSTAL, ItemEndCrystal.class, ItemEndCrystal::new);
        registerItem(ItemNames.SPRUCE_DOOR, SPRUCE_DOOR, ItemDoorSpruce.class, ItemDoorSpruce::new);
        registerItem(ItemNames.BIRCH_DOOR, BIRCH_DOOR, ItemDoorBirch.class, ItemDoorBirch::new);
        registerItem(ItemNames.JUNGLE_DOOR, JUNGLE_DOOR, ItemDoorJungle.class, ItemDoorJungle::new);
        registerItem(ItemNames.ACACIA_DOOR, ACACIA_DOOR, ItemDoorAcacia.class, ItemDoorAcacia::new);
        registerItem(ItemNames.DARK_OAK_DOOR, DARK_OAK_DOOR, ItemDoorDarkOak.class, ItemDoorDarkOak::new);
        registerItem(ItemNames.CHORUS_FRUIT, CHORUS_FRUIT, ItemChorusFruit.class, ItemChorusFruit::new);
        registerItem(ItemNames.POPPED_CHORUS_FRUIT, POPPED_CHORUS_FRUIT, ItemChorusFruitPopped.class, ItemChorusFruitPopped::new);

        registerItem(ItemNames.DRAGON_BREATH, DRAGON_BREATH, ItemDragonBreath.class, ItemDragonBreath::new);
        registerItem(ItemNames.SPLASH_POTION, SPLASH_POTION, ItemPotionSplash.class, ItemPotionSplash::new, PotionID.UNDEFINED - 1);

        registerItem(ItemNames.LINGERING_POTION, LINGERING_POTION, ItemPotionLingering.class, ItemPotionLingering::new, PotionID.UNDEFINED - 1);

        registerItem(ItemNames.COMMAND_BLOCK_MINECART, COMMAND_BLOCK_MINECART, ItemMinecartCommandBlock.class, ItemMinecartCommandBlock::new);
        registerItem(ItemNames.ELYTRA, ELYTRA, ItemElytra.class, ItemElytra::new);
        registerItem(ItemNames.SHULKER_SHELL, SHULKER_SHELL, ItemShulkerShell.class, ItemShulkerShell::new);
        registerItem(ItemNames.BANNER, BANNER, ItemBanner.class, ItemBanner::new, 15);

        registerItem(ItemNames.TOTEM_OF_UNDYING, TOTEM_OF_UNDYING, ItemTotem.class, ItemTotem::new);

        registerItem(ItemNames.IRON_NUGGET, IRON_NUGGET, ItemNuggetIron.class, ItemNuggetIron::new);

        registerItem(ItemNames.BEETROOT, BEETROOT, ItemBeetroot.class, ItemBeetroot::new);
        registerItem(ItemNames.BEETROOT_SEEDS, BEETROOT_SEEDS, ItemSeedsBeetroot.class, ItemSeedsBeetroot::new);
        registerItem(ItemNames.BEETROOT_SOUP, BEETROOT_SOUP, ItemBeetrootSoup.class, ItemBeetrootSoup::new);
        registerItem(ItemNames.SALMON, SALMON, ItemSalmon.class, ItemSalmon::new);
        registerItem(ItemNames.TROPICAL_FISH, TROPICAL_FISH, ItemClownfish.class, ItemClownfish::new);
        registerItem(ItemNames.PUFFERFISH, PUFFERFISH, ItemPufferfish.class, ItemPufferfish::new);
        registerItem(ItemNames.COOKED_SALMON, COOKED_SALMON, ItemSalmonCooked.class, ItemSalmonCooked::new);

        registerItem(ItemNames.ENCHANTED_GOLDEN_APPLE, ENCHANTED_GOLDEN_APPLE, ItemAppleGoldEnchanted.class, ItemAppleGoldEnchanted::new);

        registerItem(ItemNames.MUSIC_DISC_11, MUSIC_DISC_11, ItemRecord11.class, ItemRecord11::new);
        registerItem(ItemNames.MUSIC_DISC_CAT, MUSIC_DISC_CAT, ItemRecordCat.class, ItemRecordCat::new);
        registerItem(ItemNames.MUSIC_DISC_13, MUSIC_DISC_13, ItemRecord13.class, ItemRecord13::new);
        registerItem(ItemNames.MUSIC_DISC_BLOCKS, MUSIC_DISC_BLOCKS, ItemRecordBlocks.class, ItemRecordBlocks::new);
        registerItem(ItemNames.MUSIC_DISC_CHIRP, MUSIC_DISC_CHIRP, ItemRecordChirp.class, ItemRecordChirp::new);
        registerItem(ItemNames.MUSIC_DISC_FAR, MUSIC_DISC_FAR, ItemRecordFar.class, ItemRecordFar::new);
        registerItem(ItemNames.MUSIC_DISC_WARD, MUSIC_DISC_WARD, ItemRecordWard.class, ItemRecordWard::new);
        registerItem(ItemNames.MUSIC_DISC_MALL, MUSIC_DISC_MALL, ItemRecordMall.class, ItemRecordMall::new);
        registerItem(ItemNames.MUSIC_DISC_MELLOHI, MUSIC_DISC_MELLOHI, ItemRecordMellohi.class, ItemRecordMellohi::new);
        registerItem(ItemNames.MUSIC_DISC_STAL, MUSIC_DISC_STAL, ItemRecordStal.class, ItemRecordStal::new);
        registerItem(ItemNames.MUSIC_DISC_STRAD, MUSIC_DISC_STRAD, ItemRecordStrad.class, ItemRecordStrad::new);
        registerItem(ItemNames.MUSIC_DISC_WAIT, MUSIC_DISC_WAIT, ItemRecordWait.class, ItemRecordWait::new);

        registerItem(ItemNames.GLOW_STICK, GLOW_STICK, ItemGlowStick.class, ItemGlowStick::new, 47, V1_4_0);
        registerItem(ItemNames.SPARKLER, SPARKLER, ItemSparkler.class, ItemSparkler::new, 46, V1_4_0);
        registerItem(ItemNames.MEDICINE, MEDICINE, ItemMedicine.class, ItemMedicine::new, 3, V1_4_0);
        registerItem(ItemNames.BALLOON, BALLOON, ItemBalloon.class, ItemBalloon::new, 15, V1_4_0);
        registerItem(ItemNames.RAPID_FERTILIZER, RAPID_FERTILIZER, ItemRapidFertilizer.class, ItemRapidFertilizer::new, V1_4_0);
        registerItem(ItemNames.BLEACH, BLEACH, ItemBleach.class, ItemBleach::new, V1_4_0);
        registerItem(ItemNames.ICE_BOMB, ICE_BOMB, ItemIceBomb.class, ItemIceBomb::new, V1_4_0);
        registerItem(ItemNames.COMPOUND, COMPOUND, ItemCompound.class, ItemCompound::new, 37, V1_4_0);

        registerItem(ItemNames.KELP, KELP, ItemKelp.class, ItemKelp::new, V1_4_0);
        registerItem(ItemNames.TRIDENT, TRIDENT, ItemTrident.class, ItemTrident::new, V1_4_0);
        registerItem(ItemNames.DRIED_KELP, DRIED_KELP, ItemDriedKelp.class, ItemDriedKelp::new, V1_4_0);
        registerItem(ItemNames.HEART_OF_THE_SEA, HEART_OF_THE_SEA, ItemHeartOfTheSea.class, ItemHeartOfTheSea::new, V1_4_0);

        registerItem(ItemNames.NAUTILUS_SHELL, NAUTILUS_SHELL, ItemNautilusShell.class, ItemNautilusShell::new, V1_5_0);
        registerItem(ItemNames.SCUTE, TURTLE_SCUTE, ItemScute.class, ItemScute::new, V1_5_0);
        registerItem(ItemNames.TURTLE_HELMET, TURTLE_HELMET, ItemTurtleShell.class, ItemTurtleShell::new, V1_5_0);

        registerItem(ItemNames.PHANTOM_MEMBRANE, PHANTOM_MEMBRANE, ItemPhantomMembrane.class, ItemPhantomMembrane::new, V1_6_0);

        registerItem(ItemNames.SPRUCE_SIGN, SPRUCE_SIGN, ItemSignSpruce.class, ItemSignSpruce::new, V1_9_0);
        registerItem(ItemNames.BIRCH_SIGN, BIRCH_SIGN, ItemSignBirch.class, ItemSignBirch::new, V1_9_0);
        registerItem(ItemNames.JUNGLE_SIGN, JUNGLE_SIGN, ItemSignJungle.class, ItemSignJungle::new, V1_9_0);
        registerItem(ItemNames.ACACIA_SIGN, ACACIA_SIGN, ItemSignAcacia.class, ItemSignAcacia::new, V1_9_0);
        registerItem(ItemNames.DARK_OAK_SIGN, DARK_OAK_SIGN, ItemSignDarkOak.class, ItemSignDarkOak::new, V1_9_0);

        registerItem(ItemNames.CREEPER_BANNER_PATTERN, CREEPER_BANNER_PATTERN, ItemBannerPatternCreeper.class, ItemBannerPatternCreeper::new, -(ItemBannerPattern.BANNER_PATTERNS.length - 1), V1_10_0);
        registerItem(ItemNames.CROSSBOW, CROSSBOW, ItemCrossbow.class, ItemCrossbow::new, V1_10_0);
        registerItem(ItemNames.SHIELD, SHIELD, ItemShield.class, ItemShield::new, V1_10_0);

        registerItem(ItemNames.SWEET_BERRIES, SWEET_BERRIES, ItemSweetBerries.class, ItemSweetBerries::new, V1_11_0);
        registerItem(ItemNames.CAMPFIRE, CAMPFIRE, ItemCampfire.class, ItemCampfire::new, V1_11_0);

        registerItem(ItemNames.CAMERA, CAMERA, ItemCamera.class, ItemCamera::new, V1_13_0);

        registerItem(ItemNames.SUSPICIOUS_STEW, SUSPICIOUS_STEW, ItemSuspiciousStew.class, ItemSuspiciousStew::new, ItemSuspiciousStew.UNDEFINED_STEW - 1, V1_13_0);

        registerItem(ItemNames.HONEY_BOTTLE, HONEY_BOTTLE, ItemHoneyBottle.class, ItemHoneyBottle::new, V1_14_0);
        registerItem(ItemNames.HONEYCOMB, HONEYCOMB, ItemHoneycomb.class, ItemHoneycomb::new, V1_14_0);

        registerItem(ItemNames.LODESTONE_COMPASS, LODESTONE_COMPASS, ItemCompassLodestone.class, ItemCompassLodestone::new, V1_16_0);
        registerItem(ItemNames.NETHERITE_INGOT, NETHERITE_INGOT, ItemIngotNetherite.class, ItemIngotNetherite::new, V1_16_0);
        registerItem(ItemNames.NETHERITE_SWORD, NETHERITE_SWORD, ItemSwordNetherite.class, ItemSwordNetherite::new, V1_16_0);
        registerItem(ItemNames.NETHERITE_SHOVEL, NETHERITE_SHOVEL, ItemShovelNetherite.class, ItemShovelNetherite::new, V1_16_0);
        registerItem(ItemNames.NETHERITE_PICKAXE, NETHERITE_PICKAXE, ItemPickaxeNetherite.class, ItemPickaxeNetherite::new, V1_16_0);
        registerItem(ItemNames.NETHERITE_AXE, NETHERITE_AXE, ItemAxeNetherite.class, ItemAxeNetherite::new, V1_16_0);
        registerItem(ItemNames.NETHERITE_HOE, NETHERITE_HOE, ItemHoeNetherite.class, ItemHoeNetherite::new, V1_16_0);
        registerItem(ItemNames.NETHERITE_HELMET, NETHERITE_HELMET, ItemHelmetNetherite.class, ItemHelmetNetherite::new, V1_16_0);
        registerItem(ItemNames.NETHERITE_CHESTPLATE, NETHERITE_CHESTPLATE, ItemChestplateNetherite.class, ItemChestplateNetherite::new, V1_16_0);
        registerItem(ItemNames.NETHERITE_LEGGINGS, NETHERITE_LEGGINGS, ItemLeggingsNetherite.class, ItemLeggingsNetherite::new, V1_16_0);
        registerItem(ItemNames.NETHERITE_BOOTS, NETHERITE_BOOTS, ItemBootsNetherite.class, ItemBootsNetherite::new, V1_16_0);
        registerItem(ItemNames.NETHERITE_SCRAP, NETHERITE_SCRAP, ItemScrapNetherite.class, ItemScrapNetherite::new, V1_16_0);
        registerItem(ItemNames.CRIMSON_SIGN, CRIMSON_SIGN, ItemSignCrimson.class, ItemSignCrimson::new, V1_16_0);
        registerItem(ItemNames.WARPED_SIGN, WARPED_SIGN, ItemSignWarped.class, ItemSignWarped::new, V1_16_0);
        registerItem(ItemNames.CRIMSON_DOOR, CRIMSON_DOOR, ItemDoorCrimson.class, ItemDoorCrimson::new, V1_16_0);
        registerItem(ItemNames.WARPED_DOOR, WARPED_DOOR, ItemDoorWarped.class, ItemDoorWarped::new, V1_16_0);
        registerItem(ItemNames.WARPED_FUNGUS_ON_A_STICK, WARPED_FUNGUS_ON_A_STICK, ItemWarpedFungusOnAStick.class, ItemWarpedFungusOnAStick::new, V1_16_0);
        registerItem(ItemNames.CHAIN, CHAIN, ItemChain.class, ItemChain::new, V1_16_0);
        registerItem(ItemNames.MUSIC_DISC_PIGSTEP, MUSIC_DISC_PIGSTEP, ItemRecordPigstep.class, ItemRecordPigstep::new, V1_16_0);
        registerItem(ItemNames.NETHER_SPROUTS, NETHER_SPROUTS, ItemNetherSprouts.class, ItemNetherSprouts::new, V1_16_0);
        registerItem(ItemNames.SOUL_CAMPFIRE, SOUL_CAMPFIRE, ItemCampfireSoul.class, ItemCampfireSoul::new, V1_16_0);

        registerItem(ItemNames.GLOW_INK_SAC, GLOW_INK_SAC, ItemGlowInkSac.class, ItemGlowInkSac::new, V1_17_0);
        registerItem(ItemNames.RAW_IRON, RAW_IRON, ItemRawIron.class, ItemRawIron::new, V1_17_0);
        registerItem(ItemNames.RAW_GOLD, RAW_GOLD, ItemRawGold.class, ItemRawGold::new, V1_17_0);
        registerItem(ItemNames.RAW_COPPER, RAW_COPPER, ItemRawCopper.class, ItemRawCopper::new, V1_17_0);
        registerItem(ItemNames.AMETHYST_SHARD, AMETHYST_SHARD, ItemAmethystShard.class, ItemAmethystShard::new, V1_17_0);
        registerItem(ItemNames.SPYGLASS, SPYGLASS, ItemSpyglass.class, ItemSpyglass::new, V1_17_0);
        registerItem(ItemNames.GLOW_FRAME, GLOW_FRAME, ItemItemFrameGlow.class, ItemItemFrameGlow::new, V1_17_0);

        registerItem(ItemNames.MUSIC_DISC_OTHERSIDE, MUSIC_DISC_OTHERSIDE, ItemRecordOtherside.class, ItemRecordOtherside::new, V1_18_0);

        registerItem(ItemNames.GOAT_HORN, GOAT_HORN, ItemGoatHorn.class, ItemGoatHorn::new, ItemGoatHorn.UNDEFINED_GOAT_HORN - 1, V1_19_0);
        registerItem(ItemNames.MUSIC_DISC_5, MUSIC_DISC_5, ItemRecord5.class, ItemRecord5::new, V1_19_0);
        registerItem(ItemNames.DISC_FRAGMENT_5, DISC_FRAGMENT_5, ItemDiscFragment5.class, ItemDiscFragment5::new, V1_19_0);
        registerItem(ItemNames.RECOVERY_COMPASS, RECOVERY_COMPASS, ItemCompassRecovery.class, ItemCompassRecovery::new, V1_19_0);
        registerItem(ItemNames.ECHO_SHARD, ECHO_SHARD, ItemEchoShard.class, ItemEchoShard::new, V1_19_0);
        registerItem(ItemNames.MANGROVE_DOOR, MANGROVE_DOOR, ItemDoorMangrove.class, ItemDoorMangrove::new, V1_19_0);
        registerItem(ItemNames.MANGROVE_SIGN, MANGROVE_SIGN, ItemSignMangrove.class, ItemSignMangrove::new, V1_19_0);

        registerItem(ItemNames.MUSIC_DISC_RELIC, MUSIC_DISC_RELIC, ItemRecordRelic.class, ItemRecordRelic::new, V1_20_0);

        registerItem(ItemNames.BREEZE_ROD, BREEZE_ROD, ItemBreezeRod.class, ItemBreezeRod::new, V1_21_0);
        registerItem(ItemNames.MACE, MACE, ItemMace.class, ItemMace::new, V1_21_0);
        registerItem(ItemNames.MUSIC_DISC_CREATOR, MUSIC_DISC_CREATOR, ItemRecordCreator.class, ItemRecordCreator::new, V1_21_0);
        registerItem(ItemNames.MUSIC_DISC_CREATOR_MUSIC_BOX, MUSIC_DISC_CREATOR_MUSIC_BOX, ItemRecordCreatorMusicBox.class, ItemRecordCreatorMusicBox::new, V1_21_0);
        registerItem(ItemNames.MUSIC_DISC_PRECIPICE, MUSIC_DISC_PRECIPICE, ItemRecordPrecipice.class, ItemRecordPrecipice::new, V1_21_0);

        registerItem(ItemNames.BOARD, BOARD, ItemBoard.class, ItemBoard::new, 2, V1_21_50);

        registerItem(ItemNames.MUSIC_DISC_TEARS, MUSIC_DISC_TEARS, ItemRecordTears.class, ItemRecordTears::new, V1_21_90);

        registerItem(ItemNames.MUSIC_DISC_LAVA_CHICKEN, MUSIC_DISC_LAVA_CHICKEN, ItemRecordLavaChicken.class, ItemRecordLavaChicken::new, V1_21_93);

        if (DEV_CLIENT_BUILD) {
            registerItem(ItemNames.DEBUG_STICK, DEBUG_STICK, ItemDebugStick.class, ItemDebugStick::new);
        }

        registerSimpleAliases();

        initializeItemBlockCache();
    }

    /**
     * deferred
     */
    public static void registerVanillaNewItems() {
        registerNewItem(ItemNames.SKULL_BANNER_PATTERN, SKULL_BANNER_PATTERN, ItemBannerPatternSkull.class, ItemBannerPatternSkull::new, V1_16_100);
        registerNewItem(ItemNames.FLOWER_BANNER_PATTERN, FLOWER_BANNER_PATTERN, ItemBannerPatternFlower.class, ItemBannerPatternFlower::new, V1_16_100);
        registerNewItem(ItemNames.MOJANG_BANNER_PATTERN, MOJANG_BANNER_PATTERN, ItemBannerPatternMojang.class, ItemBannerPatternMojang::new, V1_16_100);
        registerNewItem(ItemNames.FIELD_MASONED_BANNER_PATTERN, FIELD_MASONED_BANNER_PATTERN, ItemBannerPatternFieldMasoned.class, ItemBannerPatternFieldMasoned::new, V1_16_100);
        registerNewItem(ItemNames.BORDURE_INDENTED_BANNER_PATTERN, BORDURE_INDENTED_BANNER_PATTERN, ItemBannerPatternBordureIndented.class, ItemBannerPatternBordureIndented::new, V1_16_100);
        registerNewItem(ItemNames.PIGLIN_BANNER_PATTERN, PIGLIN_BANNER_PATTERN, ItemBannerPatternPiglin.class, ItemBannerPatternPiglin::new, V1_16_100);
        registerNewItem(ItemNames.GLOBE_BANNER_PATTERN, GLOBE_BANNER_PATTERN, ItemBannerPatternGlobe.class, ItemBannerPatternGlobe::new, V1_16_100);

        registerNewItem(ItemNames.SPRUCE_BOAT, SPRUCE_BOAT, ItemBoatSpruce.class, ItemBoatSpruce::new, V1_16_100);
        registerNewItem(ItemNames.BIRCH_BOAT, BIRCH_BOAT, ItemBoatBirch.class, ItemBoatBirch::new, V1_16_100);
        registerNewItem(ItemNames.JUNGLE_BOAT, JUNGLE_BOAT, ItemBoatJungle.class, ItemBoatJungle::new, V1_16_100);
        registerNewItem(ItemNames.ACACIA_BOAT, ACACIA_BOAT, ItemBoatAcacia.class, ItemBoatAcacia::new, V1_16_100);
        registerNewItem(ItemNames.DARK_OAK_BOAT, DARK_OAK_BOAT, ItemBoatDarkOak.class, ItemBoatDarkOak::new, V1_16_100);
        registerNewItem(ItemNames.MANGROVE_BOAT, MANGROVE_BOAT, ItemBoatMangrove.class, ItemBoatMangrove::new, V1_19_0);
        registerNewItem(ItemNames.BAMBOO_RAFT, BAMBOO_RAFT, ItemBoatBambooRaft.class, ItemBoatBambooRaft::new, V1_20_0);
        registerNewItem(ItemNames.CHERRY_BOAT, CHERRY_BOAT, ItemBoatCherry.class, ItemBoatCherry::new, V1_20_0);
        registerNewItem(ItemNames.PALE_OAK_BOAT, PALE_OAK_BOAT, ItemBoatPaleOak.class, ItemBoatPaleOak::new, V1_21_50);

        registerNewItem(ItemNames.MILK_BUCKET, MILK_BUCKET, ItemBucketMilk.class, ItemBucketMilk::new, V1_16_100);
        registerNewItem(ItemNames.COD_BUCKET, COD_BUCKET, ItemBucketCod.class, ItemBucketCod::new, V1_16_100);
        registerNewItem(ItemNames.SALMON_BUCKET, SALMON_BUCKET, ItemBucketSalmon.class, ItemBucketSalmon::new, V1_16_100);
        registerNewItem(ItemNames.TROPICAL_FISH_BUCKET, TROPICAL_FISH_BUCKET, ItemBucketTropicalFish.class, ItemBucketTropicalFish::new, V1_16_100);
        registerNewItem(ItemNames.PUFFERFISH_BUCKET, PUFFERFISH_BUCKET, ItemBucketPufferfish.class, ItemBucketPufferfish::new, V1_16_100);
        registerNewItem(ItemNames.WATER_BUCKET, WATER_BUCKET, ItemBucketWater.class, ItemBucketWater::new, V1_16_100);
        registerNewItem(ItemNames.LAVA_BUCKET, LAVA_BUCKET, ItemBucketLava.class, ItemBucketLava::new, V1_16_100);
        registerNewItem(ItemNames.POWDER_SNOW_BUCKET, POWDER_SNOW_BUCKET, ItemBucketPowderSnow.class, ItemBucketPowderSnow::new, V1_17_0);
        registerNewItem(ItemNames.AXOLOTL_BUCKET, AXOLOTL_BUCKET, ItemBucketAxolotl.class, ItemBucketAxolotl::new, V1_17_0);
        registerNewItem(ItemNames.TADPOLE_BUCKET, TADPOLE_BUCKET, ItemBucketTadpole.class, ItemBucketTadpole::new, V1_19_0);

        registerNewItem(ItemNames.CHARCOAL, CHARCOAL, ItemCharcoal.class, ItemCharcoal::new, V1_16_100);

        registerNewItem(ItemNames.COCOA_BEANS, COCOA_BEANS, ItemCocoaBeans.class, ItemCocoaBeans::new, V1_16_100);
        registerNewItem(ItemNames.LAPIS_LAZULI, LAPIS_LAZULI, ItemLapisLazuli.class, ItemLapisLazuli::new, V1_16_100);
        registerNewItem(ItemNames.BONE_MEAL, BONE_MEAL, ItemBoneMeal.class, ItemBoneMeal::new, V1_16_100);

        registerNewItem(ItemNames.BLACK_DYE, BLACK_DYE, ItemDyeBlack.class, ItemDyeBlack::new, V1_16_100);
        registerNewItem(ItemNames.RED_DYE, RED_DYE, ItemDyeRed.class, ItemDyeRed::new, V1_16_100);
        registerNewItem(ItemNames.GREEN_DYE, GREEN_DYE, ItemDyeGreen.class, ItemDyeGreen::new, V1_16_100);
        registerNewItem(ItemNames.BROWN_DYE, BROWN_DYE, ItemDyeBrown.class, ItemDyeBrown::new, V1_16_100);
        registerNewItem(ItemNames.BLUE_DYE, BLUE_DYE, ItemDyeBlue.class, ItemDyeBlue::new, V1_16_100);
        registerNewItem(ItemNames.PURPLE_DYE, PURPLE_DYE, ItemDyePurple.class, ItemDyePurple::new, V1_16_100);
        registerNewItem(ItemNames.CYAN_DYE, CYAN_DYE, ItemDyeCyan.class, ItemDyeCyan::new, V1_16_100);
        registerNewItem(ItemNames.LIGHT_GRAY_DYE, LIGHT_GRAY_DYE, ItemDyeLightGray.class, ItemDyeLightGray::new, V1_16_100);
        registerNewItem(ItemNames.GRAY_DYE, GRAY_DYE, ItemDyeGray.class, ItemDyeGray::new, V1_16_100);
        registerNewItem(ItemNames.PINK_DYE, PINK_DYE, ItemDyePink.class, ItemDyePink::new, V1_16_100);
        registerNewItem(ItemNames.LIME_DYE, LIME_DYE, ItemDyeLime.class, ItemDyeLime::new, V1_16_100);
        registerNewItem(ItemNames.YELLOW_DYE, YELLOW_DYE, ItemDyeYellow.class, ItemDyeYellow::new, V1_16_100);
        registerNewItem(ItemNames.LIGHT_BLUE_DYE, LIGHT_BLUE_DYE, ItemDyeLightBlue.class, ItemDyeLightBlue::new, V1_16_100);
        registerNewItem(ItemNames.MAGENTA_DYE, MAGENTA_DYE, ItemDyeMagenta.class, ItemDyeMagenta::new, V1_16_100);
        registerNewItem(ItemNames.ORANGE_DYE, ORANGE_DYE, ItemDyeOrange.class, ItemDyeOrange::new, V1_16_100);
        registerNewItem(ItemNames.WHITE_DYE, WHITE_DYE, ItemDyeWhite.class, ItemDyeWhite::new, V1_16_100);

        registerNewItem(ItemNames.CHICKEN_SPAWN_EGG, CHICKEN_SPAWN_EGG, ItemSpawnEggChicken.class, ItemSpawnEggChicken::new, V1_16_100);
        registerNewItem(ItemNames.COW_SPAWN_EGG, COW_SPAWN_EGG, ItemSpawnEggCow.class, ItemSpawnEggCow::new, V1_16_100);
        registerNewItem(ItemNames.PIG_SPAWN_EGG, PIG_SPAWN_EGG, ItemSpawnEggPig.class, ItemSpawnEggPig::new, V1_16_100);
        registerNewItem(ItemNames.SHEEP_SPAWN_EGG, SHEEP_SPAWN_EGG, ItemSpawnEggSheep.class, ItemSpawnEggSheep::new, V1_16_100);
        registerNewItem(ItemNames.WOLF_SPAWN_EGG, WOLF_SPAWN_EGG, ItemSpawnEggWolf.class, ItemSpawnEggWolf::new, V1_16_100);
        registerNewItem(ItemNames.VILLAGER_SPAWN_EGG, VILLAGER_SPAWN_EGG, ItemSpawnEggVillager.class, ItemSpawnEggVillager::new, V1_16_100);
        registerNewItem(ItemNames.MOOSHROOM_SPAWN_EGG, MOOSHROOM_SPAWN_EGG, ItemSpawnEggMooshroom.class, ItemSpawnEggMooshroom::new, V1_16_100);
        registerNewItem(ItemNames.SQUID_SPAWN_EGG, SQUID_SPAWN_EGG, ItemSpawnEggSquid.class, ItemSpawnEggSquid::new, V1_16_100);
        registerNewItem(ItemNames.RABBIT_SPAWN_EGG, RABBIT_SPAWN_EGG, ItemSpawnEggRabbit.class, ItemSpawnEggRabbit::new, V1_16_100);
        registerNewItem(ItemNames.BAT_SPAWN_EGG, BAT_SPAWN_EGG, ItemSpawnEggBat.class, ItemSpawnEggBat::new, V1_16_100);
        registerNewItem(ItemNames.IRON_GOLEM_SPAWN_EGG, IRON_GOLEM_SPAWN_EGG, ItemSpawnEggIronGolem.class, ItemSpawnEggIronGolem::new, V1_16_100);
        registerNewItem(ItemNames.SNOW_GOLEM_SPAWN_EGG, SNOW_GOLEM_SPAWN_EGG, ItemSpawnEggSnowGolem.class, ItemSpawnEggSnowGolem::new, V1_16_100);
        registerNewItem(ItemNames.OCELOT_SPAWN_EGG, OCELOT_SPAWN_EGG, ItemSpawnEggOcelot.class, ItemSpawnEggOcelot::new, V1_16_100);
        registerNewItem(ItemNames.HORSE_SPAWN_EGG, HORSE_SPAWN_EGG, ItemSpawnEggHorse.class, ItemSpawnEggHorse::new, V1_16_100);
        registerNewItem(ItemNames.DONKEY_SPAWN_EGG, DONKEY_SPAWN_EGG, ItemSpawnEggDonkey.class, ItemSpawnEggDonkey::new, V1_16_100);
        registerNewItem(ItemNames.MULE_SPAWN_EGG, MULE_SPAWN_EGG, ItemSpawnEggMule.class, ItemSpawnEggMule::new, V1_16_100);
        registerNewItem(ItemNames.SKELETON_HORSE_SPAWN_EGG, SKELETON_HORSE_SPAWN_EGG, ItemSpawnEggSkeletonHorse.class, ItemSpawnEggSkeletonHorse::new, V1_16_100);
        registerNewItem(ItemNames.ZOMBIE_HORSE_SPAWN_EGG, ZOMBIE_HORSE_SPAWN_EGG, ItemSpawnEggZombieHorse.class, ItemSpawnEggZombieHorse::new, V1_16_100);
        registerNewItem(ItemNames.POLAR_BEAR_SPAWN_EGG, POLAR_BEAR_SPAWN_EGG, ItemSpawnEggPolarBear.class, ItemSpawnEggPolarBear::new, V1_16_100);
        registerNewItem(ItemNames.LLAMA_SPAWN_EGG, LLAMA_SPAWN_EGG, ItemSpawnEggLlama.class, ItemSpawnEggLlama::new, V1_16_100);
        registerNewItem(ItemNames.PARROT_SPAWN_EGG, PARROT_SPAWN_EGG, ItemSpawnEggParrot.class, ItemSpawnEggParrot::new, V1_16_100);
        registerNewItem(ItemNames.DOLPHIN_SPAWN_EGG, DOLPHIN_SPAWN_EGG, ItemSpawnEggDolphin.class, ItemSpawnEggDolphin::new, V1_16_100);
        registerNewItem(ItemNames.ZOMBIE_SPAWN_EGG, ZOMBIE_SPAWN_EGG, ItemSpawnEggZombie.class, ItemSpawnEggZombie::new, V1_16_100);
        registerNewItem(ItemNames.CREEPER_SPAWN_EGG, CREEPER_SPAWN_EGG, ItemSpawnEggCreeper.class, ItemSpawnEggCreeper::new, V1_16_100);
        registerNewItem(ItemNames.SKELETON_SPAWN_EGG, SKELETON_SPAWN_EGG, ItemSpawnEggSkeleton.class, ItemSpawnEggSkeleton::new, V1_16_100);
        registerNewItem(ItemNames.SPIDER_SPAWN_EGG, SPIDER_SPAWN_EGG, ItemSpawnEggSpider.class, ItemSpawnEggSpider::new, V1_16_100);
        registerNewItem(ItemNames.ZOMBIE_PIGMAN_SPAWN_EGG, ZOMBIE_PIGMAN_SPAWN_EGG, ItemSpawnEggZombiePigman.class, ItemSpawnEggZombiePigman::new, V1_16_100);
        registerNewItem(ItemNames.SLIME_SPAWN_EGG, SLIME_SPAWN_EGG, ItemSpawnEggSlime.class, ItemSpawnEggSlime::new, V1_16_100);
        registerNewItem(ItemNames.ENDERMAN_SPAWN_EGG, ENDERMAN_SPAWN_EGG, ItemSpawnEggEnderman.class, ItemSpawnEggEnderman::new, V1_16_100);
        registerNewItem(ItemNames.SILVERFISH_SPAWN_EGG, SILVERFISH_SPAWN_EGG, ItemSpawnEggSilverfish.class, ItemSpawnEggSilverfish::new, V1_16_100);
        registerNewItem(ItemNames.CAVE_SPIDER_SPAWN_EGG, CAVE_SPIDER_SPAWN_EGG, ItemSpawnEggCaveSpider.class, ItemSpawnEggCaveSpider::new, V1_16_100);
        registerNewItem(ItemNames.GHAST_SPAWN_EGG, GHAST_SPAWN_EGG, ItemSpawnEggGhast.class, ItemSpawnEggGhast::new, V1_16_100);
        registerNewItem(ItemNames.MAGMA_CUBE_SPAWN_EGG, MAGMA_CUBE_SPAWN_EGG, ItemSpawnEggMagmaCube.class, ItemSpawnEggMagmaCube::new, V1_16_100);
        registerNewItem(ItemNames.BLAZE_SPAWN_EGG, BLAZE_SPAWN_EGG, ItemSpawnEggBlaze.class, ItemSpawnEggBlaze::new, V1_16_100);
        registerNewItem(ItemNames.ZOMBIE_VILLAGER_SPAWN_EGG, ZOMBIE_VILLAGER_SPAWN_EGG, ItemSpawnEggZombieVillager.class, ItemSpawnEggZombieVillager::new, V1_16_100);
        registerNewItem(ItemNames.WITCH_SPAWN_EGG, WITCH_SPAWN_EGG, ItemSpawnEggWitch.class, ItemSpawnEggWitch::new, V1_16_100);
        registerNewItem(ItemNames.STRAY_SPAWN_EGG, STRAY_SPAWN_EGG, ItemSpawnEggStray.class, ItemSpawnEggStray::new, V1_16_100);
        registerNewItem(ItemNames.HUSK_SPAWN_EGG, HUSK_SPAWN_EGG, ItemSpawnEggHusk.class, ItemSpawnEggHusk::new, V1_16_100);
        registerNewItem(ItemNames.WITHER_SKELETON_SPAWN_EGG, WITHER_SKELETON_SPAWN_EGG, ItemSpawnEggWitherSkeleton.class, ItemSpawnEggWitherSkeleton::new, V1_16_100);
        registerNewItem(ItemNames.GUARDIAN_SPAWN_EGG, GUARDIAN_SPAWN_EGG, ItemSpawnEggGuardian.class, ItemSpawnEggGuardian::new, V1_16_100);
        registerNewItem(ItemNames.ELDER_GUARDIAN_SPAWN_EGG, ELDER_GUARDIAN_SPAWN_EGG, ItemSpawnEggElderGuardian.class, ItemSpawnEggElderGuardian::new, V1_16_100);
        registerNewItem(ItemNames.NPC_SPAWN_EGG, NPC_SPAWN_EGG, ItemSpawnEggNpc.class, ItemSpawnEggNpc::new, V1_16_100);
        registerNewItem(ItemNames.WITHER_SPAWN_EGG, WITHER_SPAWN_EGG, ItemSpawnEggWither.class, ItemSpawnEggWither::new, V1_16_100);
        registerNewItem(ItemNames.ENDER_DRAGON_SPAWN_EGG, ENDER_DRAGON_SPAWN_EGG, ItemSpawnEggEnderDragon.class, ItemSpawnEggEnderDragon::new, V1_16_100);
        registerNewItem(ItemNames.SHULKER_SPAWN_EGG, SHULKER_SPAWN_EGG, ItemSpawnEggShulker.class, ItemSpawnEggShulker::new, V1_16_100);
        registerNewItem(ItemNames.ENDERMITE_SPAWN_EGG, ENDERMITE_SPAWN_EGG, ItemSpawnEggEndermite.class, ItemSpawnEggEndermite::new, V1_16_100);
        registerNewItem(ItemNames.AGENT_SPAWN_EGG, AGENT_SPAWN_EGG, ItemSpawnEggAgent.class, ItemSpawnEggAgent::new, V1_16_100);
        registerNewItem(ItemNames.VINDICATOR_SPAWN_EGG, VINDICATOR_SPAWN_EGG, ItemSpawnEggVindicator.class, ItemSpawnEggVindicator::new, V1_16_100);
        registerNewItem(ItemNames.PHANTOM_SPAWN_EGG, PHANTOM_SPAWN_EGG, ItemSpawnEggPhantom.class, ItemSpawnEggPhantom::new, V1_16_100);
        registerNewItem(ItemNames.RAVAGER_SPAWN_EGG, RAVAGER_SPAWN_EGG, ItemSpawnEggRavager.class, ItemSpawnEggRavager::new, V1_16_100);
        registerNewItem(ItemNames.TURTLE_SPAWN_EGG, TURTLE_SPAWN_EGG, ItemSpawnEggTurtle.class, ItemSpawnEggTurtle::new, V1_16_100);
        registerNewItem(ItemNames.CAT_SPAWN_EGG, CAT_SPAWN_EGG, ItemSpawnEggCat.class, ItemSpawnEggCat::new, V1_16_100);
        registerNewItem(ItemNames.EVOKER_SPAWN_EGG, EVOKER_SPAWN_EGG, ItemSpawnEggEvoker.class, ItemSpawnEggEvoker::new, V1_16_100);
        registerNewItem(ItemNames.VEX_SPAWN_EGG, VEX_SPAWN_EGG, ItemSpawnEggVex.class, ItemSpawnEggVex::new, V1_16_100);
        registerNewItem(ItemNames.PUFFERFISH_SPAWN_EGG, PUFFERFISH_SPAWN_EGG, ItemSpawnEggPufferfish.class, ItemSpawnEggPufferfish::new, V1_16_100);
        registerNewItem(ItemNames.SALMON_SPAWN_EGG, SALMON_SPAWN_EGG, ItemSpawnEggSalmon.class, ItemSpawnEggSalmon::new, V1_16_100);
        registerNewItem(ItemNames.DROWNED_SPAWN_EGG, DROWNED_SPAWN_EGG, ItemSpawnEggDrowned.class, ItemSpawnEggDrowned::new, V1_16_100);
        registerNewItem(ItemNames.TROPICAL_FISH_SPAWN_EGG, TROPICAL_FISH_SPAWN_EGG, ItemSpawnEggTropicalFish.class, ItemSpawnEggTropicalFish::new, V1_16_100);
        registerNewItem(ItemNames.COD_SPAWN_EGG, COD_SPAWN_EGG, ItemSpawnEggCod.class, ItemSpawnEggCod::new, V1_16_100);
        registerNewItem(ItemNames.PANDA_SPAWN_EGG, PANDA_SPAWN_EGG, ItemSpawnEggPanda.class, ItemSpawnEggPanda::new, V1_16_100);
        registerNewItem(ItemNames.PILLAGER_SPAWN_EGG, PILLAGER_SPAWN_EGG, ItemSpawnEggPillager.class, ItemSpawnEggPillager::new, V1_16_100);
        registerNewItem(ItemNames.WANDERING_TRADER_SPAWN_EGG, WANDERING_TRADER_SPAWN_EGG, ItemSpawnEggWanderingTrader.class, ItemSpawnEggWanderingTrader::new, V1_16_100);
        registerNewItem(ItemNames.FOX_SPAWN_EGG, FOX_SPAWN_EGG, ItemSpawnEggFox.class, ItemSpawnEggFox::new, V1_16_100);
        registerNewItem(ItemNames.BEE_SPAWN_EGG, BEE_SPAWN_EGG, ItemSpawnEggBee.class, ItemSpawnEggBee::new, V1_16_100);
        registerNewItem(ItemNames.PIGLIN_SPAWN_EGG, PIGLIN_SPAWN_EGG, ItemSpawnEggPiglin.class, ItemSpawnEggPiglin::new, V1_16_100);
        registerNewItem(ItemNames.HOGLIN_SPAWN_EGG, HOGLIN_SPAWN_EGG, ItemSpawnEggHoglin.class, ItemSpawnEggHoglin::new, V1_16_100);
        registerNewItem(ItemNames.STRIDER_SPAWN_EGG, STRIDER_SPAWN_EGG, ItemSpawnEggStrider.class, ItemSpawnEggStrider::new, V1_16_100);
        registerNewItem(ItemNames.ZOGLIN_SPAWN_EGG, ZOGLIN_SPAWN_EGG, ItemSpawnEggZoglin.class, ItemSpawnEggZoglin::new, V1_16_100);
        registerNewItem(ItemNames.PIGLIN_BRUTE_SPAWN_EGG, PIGLIN_BRUTE_SPAWN_EGG, ItemSpawnEggPiglinBrute.class, ItemSpawnEggPiglinBrute::new, V1_16_100);
        registerNewItem(ItemNames.GOAT_SPAWN_EGG, GOAT_SPAWN_EGG, ItemSpawnEggGoat.class, ItemSpawnEggGoat::new, V1_17_0);
        registerNewItem(ItemNames.GLOW_SQUID_SPAWN_EGG, GLOW_SQUID_SPAWN_EGG, ItemSpawnEggGlowSquid.class, ItemSpawnEggGlowSquid::new, V1_17_0);
        registerNewItem(ItemNames.AXOLOTL_SPAWN_EGG, AXOLOTL_SPAWN_EGG, ItemSpawnEggAxolotl.class, ItemSpawnEggAxolotl::new, V1_17_0);
        registerNewItem(ItemNames.WARDEN_SPAWN_EGG, WARDEN_SPAWN_EGG, ItemSpawnEggWarden.class, ItemSpawnEggWarden::new, V1_19_0);
        registerNewItem(ItemNames.FROG_SPAWN_EGG, FROG_SPAWN_EGG, ItemSpawnEggFrog.class, ItemSpawnEggFrog::new, V1_19_0);
        registerNewItem(ItemNames.TADPOLE_SPAWN_EGG, TADPOLE_SPAWN_EGG, ItemSpawnEggTadpole.class, ItemSpawnEggTadpole::new, V1_19_0);
        registerNewItem(ItemNames.ALLAY_SPAWN_EGG, ALLAY_SPAWN_EGG, ItemSpawnEggAllay.class, ItemSpawnEggAllay::new, V1_19_0);
        registerNewItem(ItemNames.TRADER_LLAMA_SPAWN_EGG, TRADER_LLAMA_SPAWN_EGG, ItemSpawnEggTraderLlama.class, ItemSpawnEggTraderLlama::new, V1_19_10);
        registerNewItem(ItemNames.CAMEL_SPAWN_EGG, CAMEL_SPAWN_EGG, ItemSpawnEggCamel.class, ItemSpawnEggCamel::new, V1_20_0);
        registerNewItem(ItemNames.SNIFFER_SPAWN_EGG, SNIFFER_SPAWN_EGG, ItemSpawnEggSniffer.class, ItemSpawnEggSniffer::new, V1_20_0);
        registerNewItem(ItemNames.ARMADILLO_SPAWN_EGG, ARMADILLO_SPAWN_EGG, ItemSpawnEggArmadillo.class, ItemSpawnEggArmadillo::new, V1_20_80);
        registerNewItem(ItemNames.BREEZE_SPAWN_EGG, BREEZE_SPAWN_EGG, ItemSpawnEggBreeze.class, ItemSpawnEggBreeze::new, V1_21_0);
        registerNewItem(ItemNames.BOGGED_SPAWN_EGG, BOGGED_SPAWN_EGG, ItemSpawnEggBogged.class, ItemSpawnEggBogged::new, V1_21_0);
        registerNewItem(ItemNames.CREAKING_SPAWN_EGG, CREAKING_SPAWN_EGG, ItemSpawnEggCreaking.class, ItemSpawnEggCreaking::new, V1_21_50);
        registerNewItem(ItemNames.HAPPY_GHAST_SPAWN_EGG, HAPPY_GHAST_SPAWN_EGG, ItemSpawnEggHappyGhast.class, ItemSpawnEggHappyGhast::new, V1_21_90);
        registerNewItem(ItemNames.COPPER_GOLEM_SPAWN_EGG, COPPER_GOLEM_SPAWN_EGG, ItemSpawnEggCopperGolem.class, ItemSpawnEggCopperGolem::new, V1_21_111);
        registerNewItem(ItemNames.NAUTILUS_SPAWN_EGG, NAUTILUS_SPAWN_EGG, ItemSpawnEggNautilus.class, ItemSpawnEggNautilus::new, V1_21_130);
        registerNewItem(ItemNames.ZOMBIE_NAUTILUS_SPAWN_EGG, ZOMBIE_NAUTILUS_SPAWN_EGG, ItemSpawnEggZombieNautilus.class, ItemSpawnEggZombieNautilus::new, V1_21_130);
        registerNewItem(ItemNames.PARCHED_SPAWN_EGG, PARCHED_SPAWN_EGG, ItemSpawnEggParched.class, ItemSpawnEggParched::new, V1_21_130);
        registerNewItem(ItemNames.CAMEL_HUSK_SPAWN_EGG, CAMEL_HUSK_SPAWN_EGG, ItemSpawnEggCamelHusk.class, ItemSpawnEggCamelHusk::new, V1_21_130);

        registerNewItem(ItemNames.COPPER_INGOT, COPPER_INGOT, ItemIngotCopper.class, ItemIngotCopper::new, V1_17_0);
        registerNewItem(ItemNames.GLOW_BERRIES, GLOW_BERRIES, ItemGlowBerries.class, ItemGlowBerries::new, V1_17_0);

        registerNewItem(ItemNames.OAK_CHEST_BOAT, OAK_CHEST_BOAT, ItemBoatChestOak.class, ItemBoatChestOak::new, -(ItemBoatChest.CHEST_BOATS.length - 1), V1_19_0);
        registerNewItem(ItemNames.SPRUCE_CHEST_BOAT, SPRUCE_CHEST_BOAT, ItemBoatChestSpruce.class, ItemBoatChestSpruce::new, V1_19_0);
        registerNewItem(ItemNames.BIRCH_CHEST_BOAT, BIRCH_CHEST_BOAT, ItemBoatChestBirch.class, ItemBoatChestBirch::new, V1_19_0);
        registerNewItem(ItemNames.JUNGLE_CHEST_BOAT, JUNGLE_CHEST_BOAT, ItemBoatChestJungle.class, ItemBoatChestJungle::new, V1_19_0);
        registerNewItem(ItemNames.ACACIA_CHEST_BOAT, ACACIA_CHEST_BOAT, ItemBoatChestAcacia.class, ItemBoatChestAcacia::new, V1_19_0);
        registerNewItem(ItemNames.DARK_OAK_CHEST_BOAT, DARK_OAK_CHEST_BOAT, ItemBoatChestDarkOak.class, ItemBoatChestDarkOak::new, V1_19_0);
        registerNewItem(ItemNames.MANGROVE_CHEST_BOAT, MANGROVE_CHEST_BOAT, ItemBoatChestMangrove.class, ItemBoatChestMangrove::new, V1_19_0);
        registerNewItem(ItemNames.BAMBOO_CHEST_RAFT, BAMBOO_CHEST_RAFT, ItemBoatChestBambooRaft.class, ItemBoatChestBambooRaft::new, V1_20_0);
        registerNewItem(ItemNames.CHERRY_CHEST_BOAT, CHERRY_CHEST_BOAT, ItemBoatChestCherry.class, ItemBoatChestCherry::new, V1_20_0);
        registerNewItem(ItemNames.PALE_OAK_CHEST_BOAT, PALE_OAK_CHEST_BOAT, ItemBoatChestPaleOak.class, ItemBoatChestPaleOak::new, V1_21_50);

        registerNewItem(ItemNames.BAMBOO_SIGN, BAMBOO_SIGN, ItemSignBamboo.class, ItemSignBamboo::new, V1_20_0);
        registerNewItem(ItemNames.CHERRY_SIGN, CHERRY_SIGN, ItemSignCherry.class, ItemSignCherry::new, V1_20_0);
        registerNewItem(ItemNames.TORCHFLOWER_SEEDS, TORCHFLOWER_SEEDS, ItemSeedsTorchflower.class, ItemSeedsTorchflower::new, V1_20_0);
        registerNewItem(ItemNames.PITCHER_POD, PITCHER_POD, ItemPitcherPod.class, ItemPitcherPod::new, V1_20_0);
        registerNewItem(ItemNames.BRUSH, BRUSH, ItemBrush.class, ItemBrush::new, V1_20_0);
        registerNewItem(ItemNames.ARCHER_POTTERY_SHERD, ARCHER_POTTERY_SHERD, ItemPotterySherdArcher.class, ItemPotterySherdArcher::new, V1_20_0);
        registerNewItem(ItemNames.ARMS_UP_POTTERY_SHERD, ARMS_UP_POTTERY_SHERD, ItemPotterySherdArmsUp.class, ItemPotterySherdArmsUp::new, V1_20_0);
        registerNewItem(ItemNames.PRIZE_POTTERY_SHERD, PRIZE_POTTERY_SHERD, ItemPotterySherdPrize.class, ItemPotterySherdPrize::new, V1_20_0);
        registerNewItem(ItemNames.SKULL_POTTERY_SHERD, SKULL_POTTERY_SHERD, ItemPotterySherdSkull.class, ItemPotterySherdSkull::new, V1_20_0);
        registerNewItem(ItemNames.ANGLER_POTTERY_SHERD, ANGLER_POTTERY_SHERD, ItemPotterySherdAngler.class, ItemPotterySherdAngler::new, V1_20_0);
        registerNewItem(ItemNames.BLADE_POTTERY_SHERD, BLADE_POTTERY_SHERD, ItemPotterySherdBlade.class, ItemPotterySherdBlade::new, V1_20_0);
        registerNewItem(ItemNames.BREWER_POTTERY_SHERD, BREWER_POTTERY_SHERD, ItemPotterySherdBrewer.class, ItemPotterySherdBrewer::new, V1_20_0);
        registerNewItem(ItemNames.BURN_POTTERY_SHERD, BURN_POTTERY_SHERD, ItemPotterySherdBurn.class, ItemPotterySherdBurn::new, V1_20_0);
        registerNewItem(ItemNames.DANGER_POTTERY_SHERD, DANGER_POTTERY_SHERD, ItemPotterySherdDanger.class, ItemPotterySherdDanger::new, V1_20_0);
        registerNewItem(ItemNames.EXPLORER_POTTERY_SHERD, EXPLORER_POTTERY_SHERD, ItemPotterySherdExplorer.class, ItemPotterySherdExplorer::new, V1_20_0);
        registerNewItem(ItemNames.FRIEND_POTTERY_SHERD, FRIEND_POTTERY_SHERD, ItemPotterySherdFriend.class, ItemPotterySherdFriend::new, V1_20_0);
        registerNewItem(ItemNames.HEART_POTTERY_SHERD, HEART_POTTERY_SHERD, ItemPotterySherdHeart.class, ItemPotterySherdHeart::new, V1_20_0);
        registerNewItem(ItemNames.HEARTBREAK_POTTERY_SHERD, HEARTBREAK_POTTERY_SHERD, ItemPotterySherdHeartbreak.class, ItemPotterySherdHeartbreak::new, V1_20_0);
        registerNewItem(ItemNames.HOWL_POTTERY_SHERD, HOWL_POTTERY_SHERD, ItemPotterySherdHowl.class, ItemPotterySherdHowl::new, V1_20_0);
        registerNewItem(ItemNames.MINER_POTTERY_SHERD, MINER_POTTERY_SHERD, ItemPotterySherdMiner.class, ItemPotterySherdMiner::new, V1_20_0);
        registerNewItem(ItemNames.MOURNER_POTTERY_SHERD, MOURNER_POTTERY_SHERD, ItemPotterySherdMourner.class, ItemPotterySherdMourner::new, V1_20_0);
        registerNewItem(ItemNames.PLENTY_POTTERY_SHERD, PLENTY_POTTERY_SHERD, ItemPotterySherdPlenty.class, ItemPotterySherdPlenty::new, V1_20_0);
        registerNewItem(ItemNames.SHEAF_POTTERY_SHERD, SHEAF_POTTERY_SHERD, ItemPotterySherdSheaf.class, ItemPotterySherdSheaf::new, V1_20_0);
        registerNewItem(ItemNames.SHELTER_POTTERY_SHERD, SHELTER_POTTERY_SHERD, ItemPotterySherdShelter.class, ItemPotterySherdShelter::new, V1_20_0);
        registerNewItem(ItemNames.SNORT_POTTERY_SHERD, SNORT_POTTERY_SHERD, ItemPotterySherdSnort.class, ItemPotterySherdSnort::new, V1_20_0);
        registerNewItem(ItemNames.NETHERITE_UPGRADE_SMITHING_TEMPLATE, NETHERITE_UPGRADE_SMITHING_TEMPLATE, ItemSmithingTemplateUpgradeNetherite.class, ItemSmithingTemplateUpgradeNetherite::new, V1_20_0);
        registerNewItem(ItemNames.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, ItemSmithingTemplateArmorTrimSentry.class, ItemSmithingTemplateArmorTrimSentry::new, V1_20_0);
        registerNewItem(ItemNames.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE, DUNE_ARMOR_TRIM_SMITHING_TEMPLATE, ItemSmithingTemplateArmorTrimDune.class, ItemSmithingTemplateArmorTrimDune::new, V1_20_0);
        registerNewItem(ItemNames.COAST_ARMOR_TRIM_SMITHING_TEMPLATE, COAST_ARMOR_TRIM_SMITHING_TEMPLATE, ItemSmithingTemplateArmorTrimCoast.class, ItemSmithingTemplateArmorTrimCoast::new, V1_20_0);
        registerNewItem(ItemNames.WILD_ARMOR_TRIM_SMITHING_TEMPLATE, WILD_ARMOR_TRIM_SMITHING_TEMPLATE, ItemSmithingTemplateArmorTrimWild.class, ItemSmithingTemplateArmorTrimWild::new, V1_20_0);
        registerNewItem(ItemNames.WARD_ARMOR_TRIM_SMITHING_TEMPLATE, WARD_ARMOR_TRIM_SMITHING_TEMPLATE, ItemSmithingTemplateArmorTrimWard.class, ItemSmithingTemplateArmorTrimWard::new, V1_20_0);
        registerNewItem(ItemNames.EYE_ARMOR_TRIM_SMITHING_TEMPLATE, EYE_ARMOR_TRIM_SMITHING_TEMPLATE, ItemSmithingTemplateArmorTrimEye.class, ItemSmithingTemplateArmorTrimEye::new, V1_20_0);
        registerNewItem(ItemNames.VEX_ARMOR_TRIM_SMITHING_TEMPLATE, VEX_ARMOR_TRIM_SMITHING_TEMPLATE, ItemSmithingTemplateArmorTrimVex.class, ItemSmithingTemplateArmorTrimVex::new, V1_20_0);
        registerNewItem(ItemNames.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, ItemSmithingTemplateArmorTrimTide.class, ItemSmithingTemplateArmorTrimTide::new, V1_20_0);
        registerNewItem(ItemNames.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, ItemSmithingTemplateArmorTrimSnout.class, ItemSmithingTemplateArmorTrimSnout::new, V1_20_0);
        registerNewItem(ItemNames.RIB_ARMOR_TRIM_SMITHING_TEMPLATE, RIB_ARMOR_TRIM_SMITHING_TEMPLATE, ItemSmithingTemplateArmorTrimRib.class, ItemSmithingTemplateArmorTrimRib::new, V1_20_0);
        registerNewItem(ItemNames.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, ItemSmithingTemplateArmorTrimSpire.class, ItemSmithingTemplateArmorTrimSpire::new, V1_20_0);
        registerNewItem(ItemNames.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, ItemSmithingTemplateArmorTrimSilence.class, ItemSmithingTemplateArmorTrimSilence::new, V1_20_0);
        registerNewItem(ItemNames.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE, WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE, ItemSmithingTemplateArmorTrimWayfinder.class, ItemSmithingTemplateArmorTrimWayfinder::new, V1_20_0);
        registerNewItem(ItemNames.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE, RAISER_ARMOR_TRIM_SMITHING_TEMPLATE, ItemSmithingTemplateArmorTrimRaiser.class, ItemSmithingTemplateArmorTrimRaiser::new, V1_20_0);
        registerNewItem(ItemNames.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE, SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE, ItemSmithingTemplateArmorTrimShaper.class, ItemSmithingTemplateArmorTrimShaper::new, V1_20_0);
        registerNewItem(ItemNames.HOST_ARMOR_TRIM_SMITHING_TEMPLATE, HOST_ARMOR_TRIM_SMITHING_TEMPLATE, ItemSmithingTemplateArmorTrimHost.class, ItemSmithingTemplateArmorTrimHost::new, V1_20_0);

        registerNewItem(ItemNames.ARMADILLO_SCUTE, ARMADILLO_SCUTE, ItemArmadilloScute.class, ItemArmadilloScute::new, V1_20_80);
        registerNewItem(ItemNames.WOLF_ARMOR, WOLF_ARMOR, ItemWolfArmor.class, ItemWolfArmor::new, V1_20_80);

        registerNewItem(ItemNames.TRIAL_KEY, TRIAL_KEY, ItemTrialKey.class, ItemTrialKey::new, V1_21_0);
        registerNewItem(ItemNames.OMINOUS_TRIAL_KEY, OMINOUS_TRIAL_KEY, ItemTrialKeyOminous.class, ItemTrialKeyOminous::new, V1_21_0);
        registerNewItem(ItemNames.OMINOUS_BOTTLE, OMINOUS_BOTTLE, ItemOminousBottle.class, ItemOminousBottle::new, ItemOminousBottle.MAXIMUM_AMPLIFIER, V1_21_0);
        registerNewItem(ItemNames.WIND_CHARGE, WIND_CHARGE, ItemWindCharge.class, ItemWindCharge::new, V1_21_0);
        registerNewItem(ItemNames.FLOW_BANNER_PATTERN, FLOW_BANNER_PATTERN, ItemBannerPatternFlow.class, ItemBannerPatternFlow::new, V1_21_0);
        registerNewItem(ItemNames.GUSTER_BANNER_PATTERN, GUSTER_BANNER_PATTERN, ItemBannerPatternGuster.class, ItemBannerPatternGuster::new, V1_21_0);
        registerNewItem(ItemNames.FLOW_POTTERY_SHERD, FLOW_POTTERY_SHERD, ItemPotterySherdFlow.class, ItemPotterySherdFlow::new, V1_21_0);
        registerNewItem(ItemNames.GUSTER_POTTERY_SHERD, GUSTER_POTTERY_SHERD, ItemPotterySherdGuster.class, ItemPotterySherdGuster::new, V1_21_0);
        registerNewItem(ItemNames.SCRAPE_POTTERY_SHERD, SCRAPE_POTTERY_SHERD, ItemPotterySherdScrape.class, ItemPotterySherdScrape::new, V1_21_0);
        registerNewItem(ItemNames.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE, FLOW_ARMOR_TRIM_SMITHING_TEMPLATE, ItemSmithingTemplateArmorTrimFlow.class, ItemSmithingTemplateArmorTrimFlow::new, V1_21_0);
        registerNewItem(ItemNames.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE, BOLT_ARMOR_TRIM_SMITHING_TEMPLATE, ItemSmithingTemplateArmorTrimBolt.class, ItemSmithingTemplateArmorTrimBolt::new, V1_21_0);

        registerNewItem(ItemNames.BUNDLE, BUNDLE, ItemBundle.class, ItemBundle::new, V1_21_40); //TODO
        registerNewItem(ItemNames.BLACK_BUNDLE, BLACK_BUNDLE, ItemBundleBlack.class, ItemBundleBlack::new, V1_21_40);
        registerNewItem(ItemNames.BLUE_BUNDLE, BLUE_BUNDLE, ItemBundleBlue.class, ItemBundleBlue::new, V1_21_40);
        registerNewItem(ItemNames.BROWN_BUNDLE, BROWN_BUNDLE, ItemBundleBrown.class, ItemBundleBrown::new, V1_21_40);
        registerNewItem(ItemNames.CYAN_BUNDLE, CYAN_BUNDLE, ItemBundleCyan.class, ItemBundleCyan::new, V1_21_40);
        registerNewItem(ItemNames.GRAY_BUNDLE, GRAY_BUNDLE, ItemBundleGray.class, ItemBundleGray::new, V1_21_40);
        registerNewItem(ItemNames.GREEN_BUNDLE, GREEN_BUNDLE, ItemBundleGreen.class, ItemBundleGreen::new, V1_21_40);
        registerNewItem(ItemNames.LIGHT_BLUE_BUNDLE, LIGHT_BLUE_BUNDLE, ItemBundleLightBlue.class, ItemBundleLightBlue::new, V1_21_40);
        registerNewItem(ItemNames.LIGHT_GRAY_BUNDLE, LIGHT_GRAY_BUNDLE, ItemBundleLightGray.class, ItemBundleLightGray::new, V1_21_40);
        registerNewItem(ItemNames.LIME_BUNDLE, LIME_BUNDLE, ItemBundleLime.class, ItemBundleLime::new, V1_21_40);
        registerNewItem(ItemNames.MAGENTA_BUNDLE, MAGENTA_BUNDLE, ItemBundleMagenta.class, ItemBundleMagenta::new, V1_21_40);
        registerNewItem(ItemNames.ORANGE_BUNDLE, ORANGE_BUNDLE, ItemBundleOrange.class, ItemBundleOrange::new, V1_21_40);
        registerNewItem(ItemNames.PINK_BUNDLE, PINK_BUNDLE, ItemBundlePink.class, ItemBundlePink::new, V1_21_40);
        registerNewItem(ItemNames.PURPLE_BUNDLE, PURPLE_BUNDLE, ItemBundlePurple.class, ItemBundlePurple::new, V1_21_40);
        registerNewItem(ItemNames.RED_BUNDLE, RED_BUNDLE, ItemBundleRed.class, ItemBundleRed::new, V1_21_40);
        registerNewItem(ItemNames.WHITE_BUNDLE, WHITE_BUNDLE, ItemBundleWhite.class, ItemBundleWhite::new, V1_21_40);
        registerNewItem(ItemNames.YELLOW_BUNDLE, YELLOW_BUNDLE, ItemBundleYellow.class, ItemBundleYellow::new, V1_21_40);

        registerNewItem(ItemNames.PALE_OAK_SIGN, PALE_OAK_SIGN, ItemSignPaleOak.class, ItemSignPaleOak::new, V1_21_50);
        registerNewItem(ItemNames.RESIN_BRICK, RESIN_BRICK, ItemBrickResin.class, ItemBrickResin::new, V1_21_50);

        registerNewItem(ItemNames.BLUE_EGG, BLUE_EGG, ItemEggBlue.class, ItemEggBlue::new, V1_21_70);
        registerNewItem(ItemNames.BROWN_EGG, BROWN_EGG, ItemEggBrown.class, ItemEggBrown::new, V1_21_70);

        registerNewItem(ItemNames.BLACK_HARNESS, BLACK_HARNESS, ItemHarnessBlack.class, ItemHarnessBlack::new, V1_21_90);
        registerNewItem(ItemNames.BLUE_HARNESS, BLUE_HARNESS, ItemHarnessBlue.class, ItemHarnessBlue::new, V1_21_90);
        registerNewItem(ItemNames.BROWN_HARNESS, BROWN_HARNESS, ItemHarnessBrown.class, ItemHarnessBrown::new, V1_21_90);
        registerNewItem(ItemNames.CYAN_HARNESS, CYAN_HARNESS, ItemHarnessCyan.class, ItemHarnessCyan::new, V1_21_90);
        registerNewItem(ItemNames.GRAY_HARNESS, GRAY_HARNESS, ItemHarnessGray.class, ItemHarnessGray::new, V1_21_90);
        registerNewItem(ItemNames.GREEN_HARNESS, GREEN_HARNESS, ItemHarnessGreen.class, ItemHarnessGreen::new, V1_21_90);
        registerNewItem(ItemNames.LIGHT_BLUE_HARNESS, LIGHT_BLUE_HARNESS, ItemHarnessLightBlue.class, ItemHarnessLightBlue::new, V1_21_90);
        registerNewItem(ItemNames.LIGHT_GRAY_HARNESS, LIGHT_GRAY_HARNESS, ItemHarnessLightGray.class, ItemHarnessLightGray::new, V1_21_90);
        registerNewItem(ItemNames.LIME_HARNESS, LIME_HARNESS, ItemHarnessLime.class, ItemHarnessLime::new, V1_21_90);
        registerNewItem(ItemNames.MAGENTA_HARNESS, MAGENTA_HARNESS, ItemHarnessMagenta.class, ItemHarnessMagenta::new, V1_21_90);
        registerNewItem(ItemNames.ORANGE_HARNESS, ORANGE_HARNESS, ItemHarnessOrange.class, ItemHarnessOrange::new, V1_21_90);
        registerNewItem(ItemNames.PINK_HARNESS, PINK_HARNESS, ItemHarnessPink.class, ItemHarnessPink::new, V1_21_90);
        registerNewItem(ItemNames.PURPLE_HARNESS, PURPLE_HARNESS, ItemHarnessPurple.class, ItemHarnessPurple::new, V1_21_90);
        registerNewItem(ItemNames.RED_HARNESS, RED_HARNESS, ItemHarnessRed.class, ItemHarnessRed::new, V1_21_90);
        registerNewItem(ItemNames.WHITE_HARNESS, WHITE_HARNESS, ItemHarnessWhite.class, ItemHarnessWhite::new, V1_21_90);
        registerNewItem(ItemNames.YELLOW_HARNESS, YELLOW_HARNESS, ItemHarnessYellow.class, ItemHarnessYellow::new, V1_21_90);

        registerNewItem(ItemNames.COPPER_NUGGET, COPPER_NUGGET, ItemNuggetCopper.class, ItemNuggetCopper::new, V1_21_111);
        registerNewItem(ItemNames.COPPER_AXE, COPPER_AXE, ItemAxeCopper.class, ItemAxeCopper::new, V1_21_111);
        registerNewItem(ItemNames.COPPER_HOE, COPPER_HOE, ItemHoeCopper.class, ItemHoeCopper::new, V1_21_111);
        registerNewItem(ItemNames.COPPER_PICKAXE, COPPER_PICKAXE, ItemPickaxeCopper.class, ItemPickaxeCopper::new, V1_21_111);
        registerNewItem(ItemNames.COPPER_SHOVEL, COPPER_SHOVEL, ItemShovelCopper.class, ItemShovelCopper::new, V1_21_111);
        registerNewItem(ItemNames.COPPER_SWORD, COPPER_SWORD, ItemSwordCopper.class, ItemSwordCopper::new, V1_21_111);
        registerNewItem(ItemNames.COPPER_HELMET, COPPER_HELMET, ItemHelmetCopper.class, ItemHelmetCopper::new, V1_21_111);
        registerNewItem(ItemNames.COPPER_CHESTPLATE, COPPER_CHESTPLATE, ItemChestplateCopper.class, ItemChestplateCopper::new, V1_21_111);
        registerNewItem(ItemNames.COPPER_LEGGINGS, COPPER_LEGGINGS, ItemLeggingsCopper.class, ItemLeggingsCopper::new, V1_21_111);
        registerNewItem(ItemNames.COPPER_BOOTS, COPPER_BOOTS, ItemBootsCopper.class, ItemBootsCopper::new, V1_21_111);
        registerNewItem(ItemNames.COPPER_HORSE_ARMOR, COPPER_HORSE_ARMOR, ItemHorseArmorCopper.class, ItemHorseArmorCopper::new, V1_21_111);

        registerNewItem(ItemNames.WOODEN_SPEAR, WOODEN_SPEAR, ItemSpearWooden.class, ItemSpearWooden::new, V1_21_130);
        registerNewItem(ItemNames.STONE_SPEAR, STONE_SPEAR, ItemSpearStone.class, ItemSpearStone::new, V1_21_130);
        registerNewItem(ItemNames.COPPER_SPEAR, COPPER_SPEAR, ItemSpearCopper.class, ItemSpearCopper::new, V1_21_130);
        registerNewItem(ItemNames.IRON_SPEAR, IRON_SPEAR, ItemSpearIron.class, ItemSpearIron::new, V1_21_130);
        registerNewItem(ItemNames.GOLDEN_SPEAR, GOLDEN_SPEAR, ItemSpearGolden.class, ItemSpearGolden::new, V1_21_130);
        registerNewItem(ItemNames.DIAMOND_SPEAR, DIAMOND_SPEAR, ItemSpearDiamond.class, ItemSpearDiamond::new, V1_21_130);
        registerNewItem(ItemNames.NETHERITE_SPEAR, NETHERITE_SPEAR, ItemSpearNetherite.class, ItemSpearNetherite::new, V1_21_130);
        registerNewItem(ItemNames.COPPER_NAUTILUS_ARMOR, COPPER_NAUTILUS_ARMOR, ItemNautilusArmorCopper.class, ItemNautilusArmorCopper::new, V1_21_130);
        registerNewItem(ItemNames.IRON_NAUTILUS_ARMOR, IRON_NAUTILUS_ARMOR, ItemNautilusArmorIron.class, ItemNautilusArmorIron::new, V1_21_130);
        registerNewItem(ItemNames.GOLDEN_NAUTILUS_ARMOR, GOLDEN_NAUTILUS_ARMOR, ItemNautilusArmorGolden.class, ItemNautilusArmorGolden::new, V1_21_130);
        registerNewItem(ItemNames.DIAMOND_NAUTILUS_ARMOR, DIAMOND_NAUTILUS_ARMOR, ItemNautilusArmorDiamond.class, ItemNautilusArmorDiamond::new, V1_21_130);
        registerNewItem(ItemNames.NETHERITE_NAUTILUS_ARMOR, NETHERITE_NAUTILUS_ARMOR, ItemNautilusArmorNetherite.class, ItemNautilusArmorNetherite::new, V1_21_130);
        registerNewItem(ItemNames.NETHERITE_HORSE_ARMOR, NETHERITE_HORSE_ARMOR, ItemHorseArmorNetherite.class, ItemHorseArmorNetherite::new, V1_21_130);

        registerComplexAliases();

        LootTables.registerVanillaLootTables();
    }

    @SuppressWarnings("deprecation")
    private static void registerSimpleAliases() {
        registerAlias(ItemNames.PRISMARINESHARD, ItemNames.PRISMARINE_SHARD, V1_6_0);
        registerAlias(ItemNames.NAMETAG, ItemNames.NAME_TAG, V1_6_0);

        registerAlias(ItemNames.SIGN, ItemNames.OAK_SIGN, V1_16_100);
        registerAlias(ItemNames.REEDS, ItemNames.SUGAR_CANE, V1_16_100);
        registerAlias(ItemNames.FISH, ItemNames.COD, V1_16_100);
        registerAlias(ItemNames.COOKED_FISH, ItemNames.COOKED_COD, V1_16_100);
        registerAlias(ItemNames.MAP, ItemNames.FILLED_MAP, V1_16_100);
        registerAlias(ItemNames.MELON, ItemNames.MELON_SLICE, V1_16_100);
        registerAlias(ItemNames.SPECKLED_MELON, ItemNames.GLISTERING_MELON_SLICE, V1_16_100);
        registerAlias(ItemNames.FIREBALL, ItemNames.FIRE_CHARGE, V1_16_100);
        registerAlias(ItemNames.EMPTYMAP, ItemNames.EMPTY_MAP, V1_16_100);
        registerAlias(ItemNames.CARROTONASTICK, ItemNames.CARROT_ON_A_STICK, V1_16_100);
        registerAlias(ItemNames.NETHERSTAR, ItemNames.NETHER_STAR, V1_16_100);
        registerAlias(ItemNames.FIREWORKS, ItemNames.FIREWORK_ROCKET, V1_16_100);
        registerAlias(ItemNames.FIREWORKSCHARGE, ItemNames.FIREWORK_STAR, V1_16_100);
        registerAlias(ItemNames.HORSEARMORLEATHER, ItemNames.LEATHER_HORSE_ARMOR, V1_16_100);
        registerAlias(ItemNames.HORSEARMORIRON, ItemNames.IRON_HORSE_ARMOR, V1_16_100);
        registerAlias(ItemNames.HORSEARMORGOLD, ItemNames.GOLDEN_HORSE_ARMOR, V1_16_100);
        registerAlias(ItemNames.HORSEARMORDIAMOND, ItemNames.DIAMOND_HORSE_ARMOR, V1_16_100);
        registerAlias(ItemNames.MUTTONRAW, ItemNames.MUTTON, V1_16_100);
        registerAlias(ItemNames.MUTTONCOOKED, ItemNames.COOKED_MUTTON, V1_16_100);
        registerAlias(ItemNames.CHORUS_FRUIT_POPPED, ItemNames.POPPED_CHORUS_FRUIT, V1_16_100);
        registerAlias(ItemNames.TOTEM, ItemNames.TOTEM_OF_UNDYING, V1_16_100);
        registerAlias(ItemNames.CLOWNFISH, ItemNames.TROPICAL_FISH, V1_16_100);
        registerAlias(ItemNames.APPLEENCHANTED, ItemNames.ENCHANTED_GOLDEN_APPLE, V1_16_100);
        registerAlias(ItemNames.TURTLE_SHELL_PIECE, ItemNames.SCUTE, V1_16_100);
        registerAlias(ItemNames.DARKOAK_SIGN, ItemNames.DARK_OAK_SIGN, V1_16_100);
        registerAlias(ItemNames.RECORD_13, ItemNames.MUSIC_DISC_13, V1_16_100);
        registerAlias(ItemNames.RECORD_CAT, ItemNames.MUSIC_DISC_CAT, V1_16_100);
        registerAlias(ItemNames.RECORD_BLOCKS, ItemNames.MUSIC_DISC_BLOCKS, V1_16_100);
        registerAlias(ItemNames.RECORD_CHIRP, ItemNames.MUSIC_DISC_CHIRP, V1_16_100);
        registerAlias(ItemNames.RECORD_FAR, ItemNames.MUSIC_DISC_FAR, V1_16_100);
        registerAlias(ItemNames.RECORD_MALL, ItemNames.MUSIC_DISC_MALL, V1_16_100);
        registerAlias(ItemNames.RECORD_MELLOHI, ItemNames.MUSIC_DISC_MELLOHI, V1_16_100);
        registerAlias(ItemNames.RECORD_STAL, ItemNames.MUSIC_DISC_STAL, V1_16_100);
        registerAlias(ItemNames.RECORD_STRAD, ItemNames.MUSIC_DISC_STRAD, V1_16_100);
        registerAlias(ItemNames.RECORD_WARD, ItemNames.MUSIC_DISC_WARD, V1_16_100);
        registerAlias(ItemNames.RECORD_11, ItemNames.MUSIC_DISC_11, V1_16_100);
        registerAlias(ItemNames.RECORD_WAIT, ItemNames.MUSIC_DISC_WAIT, V1_16_100);
        registerAlias(ItemNames.LODESTONECOMPASS, ItemNames.LODESTONE_COMPASS, V1_16_100);
        registerAlias(ItemNames.RECORD_PIGSTEP, ItemNames.MUSIC_DISC_PIGSTEP, V1_16_100);

        registerAlias(ItemNames.RECORD_OTHERSIDE, ItemNames.MUSIC_DISC_OTHERSIDE, true, V1_18_0);

        registerAlias(ItemNames.RECORD_5, ItemNames.MUSIC_DISC_5, true, V1_19_0);

        registerAlias(ItemNames.RECORD_RELIC, ItemNames.MUSIC_DISC_RELIC, true, V1_20_0);

        registerAlias(ItemNames.SCUTE, ItemNames.TURTLE_SCUTE, V1_20_60);

        registerAlias(ItemNames.RECORD_CREATOR, ItemNames.MUSIC_DISC_CREATOR, true, V1_21_0);
        registerAlias(ItemNames.RECORD_CREATOR_MUSIC_BOX, ItemNames.MUSIC_DISC_CREATOR_MUSIC_BOX, true, V1_21_0);
        registerAlias(ItemNames.RECORD_PRECIPICE, ItemNames.MUSIC_DISC_PRECIPICE, true, V1_21_0);

        registerAlias(ItemNames.RECORD_TEARS, ItemNames.MUSIC_DISC_TEARS, true, V1_21_90);

        registerAlias(ItemNames.RECORD_LAVA_CHICKEN, ItemNames.MUSIC_DISC_LAVA_CHICKEN, true, V1_21_93);

        registerAlias(ItemNames.CHAIN, ItemBlockNames.IRON_CHAIN, V1_21_111);

    }

    @SuppressWarnings("deprecation")
    private static void registerComplexAliases() {
        registerComplexAlias(CREEPER_BANNER_PATTERN, ItemNames.BANNER_PATTERN, BANNER_PATTERN, ItemBannerPattern.CREEPER_CHARGE, V1_16_100);
        registerComplexAlias(SKULL_BANNER_PATTERN, ItemNames.BANNER_PATTERN, BANNER_PATTERN, ItemBannerPattern.SKULL_CHARGE, V1_16_100);
        registerComplexAlias(FLOWER_BANNER_PATTERN, ItemNames.BANNER_PATTERN, BANNER_PATTERN, ItemBannerPattern.FLOWER_CHARGE, V1_16_100);
        registerComplexAlias(MOJANG_BANNER_PATTERN, ItemNames.BANNER_PATTERN, BANNER_PATTERN, ItemBannerPattern.MOJANG, V1_16_100);
        registerComplexAlias(FIELD_MASONED_BANNER_PATTERN, ItemNames.BANNER_PATTERN, BANNER_PATTERN, ItemBannerPattern.FIELD_MASONED, V1_16_100);
        registerComplexAlias(BORDURE_INDENTED_BANNER_PATTERN, ItemNames.BANNER_PATTERN, BANNER_PATTERN, ItemBannerPattern.BORDURE_INDENTED, V1_16_100);
        registerComplexAlias(PIGLIN_BANNER_PATTERN, ItemNames.BANNER_PATTERN, BANNER_PATTERN, ItemBannerPattern.PIGLIN, V1_16_100);
        registerComplexAlias(GLOBE_BANNER_PATTERN, ItemNames.BANNER_PATTERN, BANNER_PATTERN, ItemBannerPattern.GLOBE, V1_18_10);

        registerComplexAlias(OAK_BOAT, ItemNames.BOAT, BOAT, ItemBoat.OAK, V1_16_100);
        registerComplexAlias(SPRUCE_BOAT, ItemNames.BOAT, BOAT, ItemBoat.SPRUCE, V1_16_100);
        registerComplexAlias(BIRCH_BOAT, ItemNames.BOAT, BOAT, ItemBoat.BIRCH, V1_16_100);
        registerComplexAlias(JUNGLE_BOAT, ItemNames.BOAT, BOAT, ItemBoat.JUNGLE, V1_16_100);
        registerComplexAlias(ACACIA_BOAT, ItemNames.BOAT, BOAT, ItemBoat.ACACIA, V1_16_100);
        registerComplexAlias(DARK_OAK_BOAT, ItemNames.BOAT, BOAT, ItemBoat.DARK_OAK, V1_16_100);
        registerComplexAlias(MANGROVE_BOAT, ItemNames.BOAT, BOAT, ItemBoat.MANGROVE, V1_19_0);
        registerComplexAlias(BAMBOO_RAFT, ItemNames.BOAT, BOAT, ItemBoat.RAFT, V1_20_0);
        registerComplexAlias(CHERRY_BOAT, ItemNames.BOAT, BOAT, ItemBoat.CHERRY, V1_20_0);
        registerComplexAlias(PALE_OAK_BOAT, ItemNames.BOAT, BOAT, ItemBoat.PALE_OAK, V1_21_50);

        registerComplexAlias(MILK_BUCKET, ItemNames.BUCKET, BUCKET, ItemBucket.TYPE_MILK_BUCKET, V1_16_100);
        registerComplexAlias(COD_BUCKET, ItemNames.BUCKET, BUCKET, ItemBucket.TYPE_COD_BUCKET, V1_16_100);
        registerComplexAlias(SALMON_BUCKET, ItemNames.BUCKET, BUCKET, ItemBucket.TYPE_SALMON_BUCKET, V1_16_100);
        registerComplexAlias(TROPICAL_FISH_BUCKET, ItemNames.BUCKET, BUCKET, ItemBucket.TYPE_TROPICAL_FISH_BUCKET, V1_16_100);
        registerComplexAlias(PUFFERFISH_BUCKET, ItemNames.BUCKET, BUCKET, ItemBucket.TYPE_PUFFERFISH_BUCKET, V1_16_100);
        registerComplexAlias(WATER_BUCKET, ItemNames.BUCKET, BUCKET, ItemBucket.TYPE_WATER_BUCKET, V1_16_100);
        registerComplexAlias(LAVA_BUCKET, ItemNames.BUCKET, BUCKET, ItemBucket.TYPE_LAVA_BUCKET, V1_16_100);
        registerComplexAlias(POWDER_SNOW_BUCKET, ItemNames.BUCKET, BUCKET, ItemBucket.TYPE_POWDER_SNOW_BUCKET, V1_17_0);
        registerComplexAlias(AXOLOTL_BUCKET, ItemNames.BUCKET, BUCKET, ItemBucket.TYPE_AXOLOTL_BUCKET, V1_17_0);
        registerComplexAlias(TADPOLE_BUCKET, ItemNames.BUCKET, BUCKET, ItemBucket.TYPE_TADPOLE_BUCKET, V1_19_0);

        registerComplexAlias(CHARCOAL, ItemNames.COAL, COAL, ItemCoal.TYPE_CHARCOAL, V1_16_100);

        registerComplexAlias(INK_SAC, ItemNames.DYE, DYE, ItemDye.BLACK, V1_16_100);
        registerComplexAlias(RED_DYE, ItemNames.DYE, DYE, ItemDye.RED, V1_16_100);
        registerComplexAlias(GREEN_DYE, ItemNames.DYE, DYE, ItemDye.GREEN, V1_16_100);
        registerComplexAlias(COCOA_BEANS, ItemNames.DYE, DYE, ItemDye.BROWN, V1_16_100);
        registerComplexAlias(LAPIS_LAZULI, ItemNames.DYE, DYE, ItemDye.BLUE, V1_16_100);
        registerComplexAlias(PURPLE_DYE, ItemNames.DYE, DYE, ItemDye.PURPLE, V1_16_100);
        registerComplexAlias(CYAN_DYE, ItemNames.DYE, DYE, ItemDye.CYAN, V1_16_100);
        registerComplexAlias(LIGHT_GRAY_DYE, ItemNames.DYE, DYE, ItemDye.LIGHT_GRAY, V1_16_100);
        registerComplexAlias(GRAY_DYE, ItemNames.DYE, DYE, ItemDye.GRAY, V1_16_100);
        registerComplexAlias(PINK_DYE, ItemNames.DYE, DYE, ItemDye.PINK, V1_16_100);
        registerComplexAlias(LIME_DYE, ItemNames.DYE, DYE, ItemDye.LIME, V1_16_100);
        registerComplexAlias(YELLOW_DYE, ItemNames.DYE, DYE, ItemDye.YELLOW, V1_16_100);
        registerComplexAlias(LIGHT_BLUE_DYE, ItemNames.DYE, DYE, ItemDye.LIGHT_BLUE, V1_16_100);
        registerComplexAlias(MAGENTA_DYE, ItemNames.DYE, DYE, ItemDye.MAGENTA, V1_16_100);
        registerComplexAlias(ORANGE_DYE, ItemNames.DYE, DYE, ItemDye.ORANGE, V1_16_100);
        registerComplexAlias(BONE_MEAL, ItemNames.DYE, DYE, ItemDye.WHITE, V1_16_100);
        registerComplexAlias(BLACK_DYE, ItemNames.DYE, DYE, ItemDye.BLACK_NEW, V1_16_100);
        registerComplexAlias(BROWN_DYE, ItemNames.DYE, DYE, ItemDye.BROWN_NEW, V1_16_100);
        registerComplexAlias(BLUE_DYE, ItemNames.DYE, DYE, ItemDye.BLUE_NEW, V1_16_100);
        registerComplexAlias(WHITE_DYE, ItemNames.DYE, DYE, ItemDye.WHITE_NEW, V1_16_100);

        registerComplexAlias(CHICKEN_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.CHICKEN, V1_16_100);
        registerComplexAlias(COW_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.COW, V1_16_100);
        registerComplexAlias(PIG_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.PIG, V1_16_100);
        registerComplexAlias(SHEEP_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.SHEEP, V1_16_100);
        registerComplexAlias(WOLF_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.WOLF, V1_16_100);
        registerComplexAlias(VILLAGER_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.VILLAGER, V1_16_100);
        registerComplexAlias(MOOSHROOM_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.MOOSHROOM, V1_16_100);
        registerComplexAlias(SQUID_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.SQUID, V1_16_100);
        registerComplexAlias(RABBIT_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.RABBIT, V1_16_100);
        registerComplexAlias(BAT_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.BAT, V1_16_100);
        registerComplexAlias(OCELOT_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.OCELOT, V1_16_100);
        registerComplexAlias(HORSE_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.HORSE, V1_16_100);
        registerComplexAlias(DONKEY_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.DONKEY, V1_16_100);
        registerComplexAlias(MULE_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.MULE, V1_16_100);
        registerComplexAlias(SKELETON_HORSE_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.SKELETON_HORSE, V1_16_100);
        registerComplexAlias(ZOMBIE_HORSE_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.ZOMBIE_HORSE, V1_16_100);
        registerComplexAlias(POLAR_BEAR_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.POLAR_BEAR, V1_16_100);
        registerComplexAlias(LLAMA_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.LLAMA, V1_16_100);
        registerComplexAlias(PARROT_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.PARROT, V1_16_100);
        registerComplexAlias(DOLPHIN_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.DOLPHIN, V1_16_100);
        registerComplexAlias(ZOMBIE_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.ZOMBIE, V1_16_100);
        registerComplexAlias(CREEPER_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.CREEPER, V1_16_100);
        registerComplexAlias(SKELETON_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.SKELETON, V1_16_100);
        registerComplexAlias(SPIDER_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.SPIDER, V1_16_100);
        registerComplexAlias(ZOMBIE_PIGMAN_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.ZOMBIE_PIGMAN, V1_16_100);
        registerComplexAlias(SLIME_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.SLIME, V1_16_100);
        registerComplexAlias(ENDERMAN_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.ENDERMAN, V1_16_100);
        registerComplexAlias(SILVERFISH_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.SILVERFISH, V1_16_100);
        registerComplexAlias(CAVE_SPIDER_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.CAVE_SPIDER, V1_16_100);
        registerComplexAlias(GHAST_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.GHAST, V1_16_100);
        registerComplexAlias(MAGMA_CUBE_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.MAGMA_CUBE, V1_16_100);
        registerComplexAlias(BLAZE_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.BLAZE, V1_16_100);
        registerComplexAlias(ZOMBIE_VILLAGER_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.ZOMBIE_VILLAGER, V1_16_100);
        registerComplexAlias(WITCH_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.WITCH, V1_16_100);
        registerComplexAlias(STRAY_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.STRAY, V1_16_100);
        registerComplexAlias(HUSK_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.HUSK, V1_16_100);
        registerComplexAlias(WITHER_SKELETON_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.WITHER_SKELETON, V1_16_100);
        registerComplexAlias(GUARDIAN_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.GUARDIAN, V1_16_100);
        registerComplexAlias(ELDER_GUARDIAN_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.ELDER_GUARDIAN, V1_16_100);
        registerComplexAlias(NPC_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.NPC, V1_16_100);
        registerComplexAlias(SHULKER_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.SHULKER, V1_16_100);
        registerComplexAlias(ENDERMITE_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.ENDERMITE, V1_16_100);
        registerComplexAlias(AGENT_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.AGENT, V1_16_100);
        registerComplexAlias(VINDICATOR_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.VINDICATOR, V1_16_100);
        registerComplexAlias(PHANTOM_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.PHANTOM, V1_16_100);
        registerComplexAlias(RAVAGER_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.RAVAGER, V1_16_100);
        registerComplexAlias(TURTLE_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.TURTLE, V1_16_100);
        registerComplexAlias(CAT_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.CAT, V1_16_100);
        registerComplexAlias(EVOKER_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.EVOCATION_ILLAGER, V1_16_100);
        registerComplexAlias(VEX_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.VEX, V1_16_100);
        registerComplexAlias(PUFFERFISH_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.PUFFERFISH, V1_16_100);
        registerComplexAlias(SALMON_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.SALMON, V1_16_100);
        registerComplexAlias(DROWNED_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.DROWNED, V1_16_100);
        registerComplexAlias(TROPICAL_FISH_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.TROPICALFISH, V1_16_100);
        registerComplexAlias(COD_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.COD, V1_16_100);
        registerComplexAlias(PANDA_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.PANDA, V1_16_100);
        registerComplexAlias(PILLAGER_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.PILLAGER, V1_16_100);
        registerComplexAlias(VILLAGER_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.VILLAGER_V2, V1_16_100);
        registerComplexAlias(ZOMBIE_VILLAGER_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.ZOMBIE_VILLAGER_V2, V1_16_100);
        registerComplexAlias(WANDERING_TRADER_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.WANDERING_TRADER, V1_16_100);
        registerComplexAlias(FOX_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.FOX, V1_16_100);
        registerComplexAlias(BEE_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.BEE, V1_16_100);
        registerComplexAlias(PIGLIN_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.PIGLIN, V1_16_100);
        registerComplexAlias(HOGLIN_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.HOGLIN, V1_16_100);
        registerComplexAlias(STRIDER_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.STRIDER, V1_16_100);
        registerComplexAlias(ZOGLIN_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.ZOGLIN, V1_16_100);
        registerComplexAlias(PIGLIN_BRUTE_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.PIGLIN_BRUTE, V1_16_100);
        registerComplexAlias(GOAT_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.GOAT, V1_17_0);
        registerComplexAlias(GLOW_SQUID_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.GLOW_SQUID, V1_17_0);
        registerComplexAlias(AXOLOTL_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.AXOLOTL, V1_17_0);
        registerComplexAlias(WARDEN_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.WARDEN, V1_19_0);
        registerComplexAlias(FROG_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.FROG, V1_19_0);
        registerComplexAlias(TADPOLE_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.TADPOLE, V1_19_0);
        registerComplexAlias(ALLAY_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.ALLAY, V1_19_0);
        registerComplexAlias(TRADER_LLAMA_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.TRADER_LLAMA, V1_19_10);
        registerComplexAlias(IRON_GOLEM_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.IRON_GOLEM, V1_19_60);
        registerComplexAlias(SNOW_GOLEM_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.SNOW_GOLEM, V1_19_60);
        registerComplexAlias(WITHER_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.WITHER, V1_19_60);
        registerComplexAlias(ENDER_DRAGON_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.ENDER_DRAGON, V1_19_60);
        registerComplexAlias(CAMEL_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.CAMEL, V1_20_0);
        registerComplexAlias(SNIFFER_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.SNIFFER, V1_20_0);
        registerComplexAlias(ARMADILLO_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.ARMADILLO, V1_20_80);
        registerComplexAlias(BREEZE_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.BREEZE, V1_21_0);
        registerComplexAlias(BOGGED_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.BOGGED, V1_21_0);
        registerComplexAlias(CREAKING_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.CREAKING, V1_21_50);
        registerComplexAlias(HAPPY_GHAST_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.HAPPY_GHAST, V1_21_90);
        registerComplexAlias(COPPER_GOLEM_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.COPPER_GOLEM, V1_21_111);
        registerComplexAlias(NAUTILUS_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.NAUTILUS, V1_21_130);
        registerComplexAlias(ZOMBIE_NAUTILUS_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.ZOMBIE_NAUTILUS, V1_21_130);
        registerComplexAlias(PARCHED_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.PARCHED, V1_21_130);
        registerComplexAlias(CAMEL_HUSK_SPAWN_EGG, ItemNames.SPAWN_EGG, SPAWN_EGG, EntityID.CAMEL_HUSK, V1_21_130);

        registerComplexAlias(OAK_CHEST_BOAT, ItemNames.CHEST_BOAT, CHEST_BOAT, ItemBoatChest.OAK, V1_19_0);
        registerComplexAlias(SPRUCE_CHEST_BOAT, ItemNames.CHEST_BOAT, CHEST_BOAT, ItemBoatChest.SPRUCE, V1_19_0);
        registerComplexAlias(BIRCH_CHEST_BOAT, ItemNames.CHEST_BOAT, CHEST_BOAT, ItemBoatChest.BIRCH, V1_19_0);
        registerComplexAlias(JUNGLE_CHEST_BOAT, ItemNames.CHEST_BOAT, CHEST_BOAT, ItemBoatChest.JUNGLE, V1_19_0);
        registerComplexAlias(ACACIA_CHEST_BOAT, ItemNames.CHEST_BOAT, CHEST_BOAT, ItemBoatChest.ACACIA, V1_19_0);
        registerComplexAlias(DARK_OAK_CHEST_BOAT, ItemNames.CHEST_BOAT, CHEST_BOAT, ItemBoatChest.DARK_OAK, V1_19_0);
        registerComplexAlias(MANGROVE_CHEST_BOAT, ItemNames.CHEST_BOAT, CHEST_BOAT, ItemBoatChest.MANGROVE, V1_19_0);
        registerComplexAlias(BAMBOO_CHEST_RAFT, ItemNames.CHEST_BOAT, CHEST_BOAT, ItemBoatChest.RAFT, V1_20_0);
        registerComplexAlias(CHERRY_CHEST_BOAT, ItemNames.CHEST_BOAT, CHEST_BOAT, ItemBoatChest.CHERRY, V1_20_0);
        registerComplexAlias(PALE_OAK_CHEST_BOAT, ItemNames.CHEST_BOAT, CHEST_BOAT, ItemBoatChest.PALE_OAK, V1_21_50);

        registerComplexAlias(ItemBlockNames.SKELETON_SKULL, SKULL, ItemSkull.HEAD_SKELETON, V1_21_40);
        registerComplexAlias(ItemBlockNames.WITHER_SKELETON_SKULL, SKULL, ItemSkull.HEAD_WITHER_SKELETON, V1_21_40);
        registerComplexAlias(ItemBlockNames.ZOMBIE_HEAD, SKULL, ItemSkull.HEAD_ZOMBIE, V1_21_40);
        registerComplexAlias(ItemBlockNames.PLAYER_HEAD, SKULL, ItemSkull.HEAD_PLAYER, V1_21_40);
        registerComplexAlias(ItemBlockNames.CREEPER_HEAD, SKULL, ItemSkull.HEAD_CREEPER, V1_21_40);
        registerComplexAlias(ItemBlockNames.DRAGON_HEAD, SKULL, ItemSkull.HEAD_DRAGON, V1_21_40);
        registerComplexAlias(ItemBlockNames.PIGLIN_HEAD, SKULL, ItemSkull.HEAD_PIGLIN, V1_21_40);

    }

    private static Class<? extends Item> registerItem(String name, int id, Class<? extends Item> clazz, ItemFactory factory) {
        return registerItem(name, id, clazz, factory, 0);
    }

    private static Class<? extends Item> registerItem(String name, int id, Class<? extends Item> clazz, ItemFactory factory, int maxAuxVal) {
        String[] split = name.split(":", 2);
        String fullName;
        String namespace;
        String identifier;
        boolean vanilla;
        if (split.length == 1) {
            fullName = "minecraft:" + name;
            namespace = "minecraft";
            identifier = name;
            vanilla = true;
        } else {
            fullName = name;
            namespace = split[0];
            identifier = split[1];
            vanilla = false;
        }

        if (Item.list[id] != null) {
            throw new IllegalArgumentException("Duplicate item id: " + id);
        }
        if (FULL_NAME_TO_ID.containsKey(fullName)) {
            throw new IllegalArgumentException("Duplicate item full name: " + fullName);
        }
        if (NAME_TO_ID.containsKey(name)) {
            throw new IllegalArgumentException("Duplicate item short name: " + name);
        }

        NAME_TO_ID.put(name, id);
        FULL_NAME_TO_ID.put(fullName, id);
        ID_TO_NAME[id] = name;
        ID_TO_FULL_NAME[id] = fullName;

        Item.list[id] = clazz;
        ITEM_FACTORIES[id] = factory;

        Item item = factory.create(0, 1);

        int variantCount;
        if (item instanceof ItemDurable) {
            variantCount = item.getMaxDurability() + 1;
        } else {
            variantCount = Math.abs(maxAuxVal) + 1;
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
    private static Class<? extends Item> registerItem(String name, int id, Class<? extends Item> clazz, ItemFactory factory, GameVersion version) {
        return registerItem(name, id, clazz, factory, 0, version);
    }

    /**
     * @param version min required base game version
     */
    private static Class<? extends Item> registerItem(String name, int id, Class<? extends Item> clazz, ItemFactory factory, int maxAuxVal, GameVersion version) {
        if (!version.isAvailable()) {
            return null;
        }
        return registerItem(name, id, clazz, factory, maxAuxVal);
    }

    /**
     * @param version min required base game version
     */
    private static Class<? extends Item> registerNewItem(String name, int id, Class<? extends Item> clazz, ItemFactory factory, GameVersion version) {
        if (!version.isAvailable()) {
            return null;
        }
        ItemSerializer.registerItem("minecraft:" + name, id);

        registerCommandItemEnum(name);

        return registerItem(name, id, clazz, factory);
    }

    /**
     * @param version min required base game version
     */
    private static Class<? extends Item> registerNewItem(String name, int id, Class<? extends Item> clazz, ItemFactory factory, int maxAuxVal, GameVersion version) {
        if (!version.isAvailable()) {
            return null;
        }
        ItemSerializer.registerItem("minecraft:" + name, id, maxAuxVal);

        registerCommandItemEnum(name);

        return registerItem(name, id, clazz, factory, maxAuxVal);
    }

    private static void registerCommandItemEnum(String name) {
        Map<String, Set<CommandEnumConstraint>> enums = CommandEnum.ENUM_ITEM.getValues();
        enums.put(name, Collections.emptySet());
        enums.put("minecraft:" + name, Collections.emptySet());
    }

    /**
     * @param version min required base game version
     */
    private static void registerAlias(String oldName, String newName, GameVersion version) {
        registerAlias(oldName, newName, false, version);
    }

    /**
     * @param version min required base game version
     */
    private static void registerAlias(String oldName, String newName, boolean add, GameVersion version) {
        if (!add && !version.isAvailable()) {
            String name = oldName;
            oldName = newName;
            newName = name;
        }

        for (Entry<String, String> entry : SIMPLE_ALIASES_MAP.entrySet()) {
            if (entry.getValue().equals(oldName)) {
                SIMPLE_ALIASES_MAP.put(entry.getKey(), newName);
            }
        }

        SIMPLE_ALIASES_MAP.put(oldName, newName);
    }

    private static void registerComplexAlias(String alias, int id, int meta, GameVersion version) {
        COMPLEX_ALIASES_MAP.put(alias, new int[]{Item.getFullId(id, meta)});
    }

    private static void registerComplexAlias(int newId, String alias, int id, int meta, GameVersion version) {
        int[] auxValues = COMPLEX_ALIASES_MAP.getOrDefault(alias, new int[0]);
        if (auxValues.length <= meta) {
            auxValues = Arrays.copyOf(auxValues, meta + 1);
            COMPLEX_ALIASES_MAP.put(alias, auxValues);
        }
        auxValues[meta] = Item.getFullId(newId);

        if (version.isAvailable()) {
            ITEM_CACHE[id][meta] = ITEM_CACHE[newId][0];
        }
    }

    public static void registerItemTag(String itemTag, int itemId) {
        registerItemTags(itemTag, Item.getFullId(itemId, 0xffff));
    }

    public static void registerItemTags(String itemTag, int... itemFullIds) {
        IntSet fullIds = TAG_TO_ITEM_FULL_IDS.computeIfAbsent(itemTag, k -> new IntOpenHashSet());
        for (int fullId : itemFullIds) {
            fullIds.add(fullId);

            Set<String> tags;
            int itemId = Item.getIdFromFullId(fullId);
            if (itemId <= 0xff && itemId != GLOW_STICK) {
                int blockId = Block.itemIdToBlockId(itemId);
                tags = BLOCK_ID_TO_TAGS[blockId];
                if (tags == null) {
                    tags = new HashSet<>();
                    BLOCK_ID_TO_TAGS[blockId] = tags;
                }
            } else {
                int itemMeta = Item.getMetaFromFullId(fullId);
                boolean hasMeta = itemMeta != 0xffff;
                Set<String>[] data = ITEM_ID_TO_TAGS[itemId];
                if (data == null) {
                    data = new Set[hasMeta ? 256 : 1];
                    ITEM_ID_TO_TAGS[itemId] = data;
                } else if (hasMeta && data.length == 1) {
                    Set<String>[] oldData = data;
                    data = new Set[256];
                    data[0] = oldData[0];
                    ITEM_ID_TO_TAGS[itemId] = data;
                }
                int index = hasMeta ? itemMeta : 0;
                tags = data[index];
                if (tags == null) {
                    tags = new HashSet<>();
                    data[index] = tags;
                }
            }
            tags.add(itemTag);
        }
    }

    public static Class<? extends Item> registerCustomItem(String fullName, int id, Class<? extends Item> clazz, ItemFactory factory) {
        return registerCustomItem(fullName, id, clazz, factory, (CompoundTag) null);
    }

    public static Class<? extends Item> registerCustomItem(String fullName, int id, Class<? extends Item> clazz, ItemFactory factory, @Nullable CompoundTag components) {
        return registerCustomItem(fullName, id, clazz, factory, components != null ? protocol -> components : null);
    }

    public static Class<? extends Item> registerCustomItem(String fullName, int id, Class<? extends Item> clazz, ItemFactory factory, @Nullable IntFunction<CompoundTag> componentsSupplier) {
        return registerCustomItem(fullName, id, clazz, factory, componentsSupplier != null ? (protocol, netease) -> componentsSupplier.apply(protocol) : null);
    }

    public static Class<? extends Item> registerCustomItem(String fullName, int id, Class<? extends Item> clazz, ItemFactory factory, @Nullable BiFunction<Integer, Boolean, CompoundTag> componentsSupplier) {
        Objects.requireNonNull(clazz, "class");
        Objects.requireNonNull(factory, "factory");
        if (fullName.split(":").length != 2) {
            throw new IllegalArgumentException("Invalid namespaced identifier: " + fullName);
        }
        if (fullName.startsWith("minecraft:")) {
            throw new IllegalArgumentException("Invalid identifier: " + fullName);
        }

        if (log.isTraceEnabled()) {
            CompoundTag components;
            if (componentsSupplier != null) {
                components = componentsSupplier.apply(GameVersion.getFeatureVersion().getProtocol(), false);
            } else {
                components = null;
            }
            log.trace("Register custom item {} ({}) {} : {}", fullName, id, clazz, components);
        }

        ItemSerializer.registerCustomItem(fullName, id, componentsSupplier);

        CommandEnum.ENUM_ITEM.getValues().put(fullName, Collections.emptySet());

        return registerItem(fullName, id, clazz, factory);
    }

    public static CompoundTag getDefaultComponents() {
        return new CompoundTag()
                .putCompound("item_properties", new CompoundTag()
                        .putBoolean("allow_off_hand", false)
                        .putBoolean("animates_in_toolbar", false)
                        .putBoolean("can_destroy_in_creative", true)
                        .putInt("creative_category", 4)
                        .putString("creative_group", "")
                        .putInt("damage", 0)
                        .putString("enchantable_slot", "none")
                        .putInt("enchantable_value", 0)
                        .putBoolean("explodable", true)
                        .putBoolean("foil", false)
                        .putInt("frame_count", 1)
                        .putBoolean("hand_equipped", false)
                        .putByte("hidden_in_commands", 2)
                        .putBoolean("ignores_permission", false)
                        .putBoolean("liquid_clipped", false)
                        .putInt("max_stack_size", 64)
                        .putFloat("mining_speed", 1)
                        .putBoolean("mirrored_art", false)
                        .putBoolean("requires_interact", false)
                        .putBoolean("should_despawn", true)
                        .putBoolean("stacked_by_data", false)
                        .putInt("use_animation", 0)
                        .putInt("use_duration", 0)
                )
                .putList("item_tags", new ListTag<StringTag>());
    }

    public static void registerCustomBlockItem(String fullName, int blockId) {
        int itemId = Block.getItemId(blockId);

        ItemSerializer.registerCustomBlockItem(fullName, itemId);

        CommandEnum.ENUM_ITEM.getValues().put(fullName, Collections.emptySet());

        createBlockItemCache(blockId, 1);
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
//                    item = new Item(id, meta, count);
                    return air();
                }
            }
        } catch (Exception e) {
//            item = new Item(id, meta, count);
            return air();
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
//                    item = new Item(id, meta, count);
                    return air();
                }
            }
        } catch (Exception e) {
//            item = new Item(id, meta, count);
            return air();
        }

        if (tags.length != 0) {
            item.setCompoundTag(tags);
        }

        return item;
    }

    public static Object2IntMap<String> getNameToIdMap() {
        return NAME_TO_ID;
    }

    public static Object2IntMap<String> getFullNameToIdMap() {
        return FULL_NAME_TO_ID;
    }

    public static int getIdByName(String name) {
        return getIdByName(name, true);
    }

    public static int getIdByName(String name, boolean lookupBlock) {
        return getIdByName(name, lookupBlock, false);
    }

    public static int getIdByName(String name, boolean lookupBlock, boolean lookupAlias) {
        return getIdByShortName(name.startsWith("minecraft:") ? name.substring(10) : name, lookupBlock, lookupAlias);
    }

    public static int getIdByShortName(String shortName) {
        return getIdByShortName(shortName, true);
    }

    public static int getIdByShortName(String shortName, boolean lookupBlock) {
        return getIdByShortName(shortName, lookupBlock, false);
    }

    public static int getIdByShortName(String shortName, boolean lookupBlock, boolean lookupAlias) {
        int id = NAME_TO_ID.getInt(shortName);
        if (id != -1) {
            return id;
        }

        if (lookupBlock) {
            id = Blocks.getIdByItemShortName(shortName, lookupAlias);
            if (id != -1) {
                return Block.getItemId(id);
            }
        }

        if (lookupAlias) {
            String alias = SIMPLE_ALIASES_MAP.get(shortName);
            if (alias != null) {
                return NAME_TO_ID.getInt(alias);
            }
        }
        return -1;
    }

    public static int getFullIdByName(String name) {
        return getFullIdByName(name, true);
    }

    public static int getFullIdByName(String name, boolean lookupBlock) {
        return getFullIdByName(name, lookupBlock, false);
    }

    public static int getFullIdByName(String name, boolean lookupBlock, boolean lookupAlias) {
        return getFullIdByShortName(name.startsWith("minecraft:") ? name.substring(10) : name, lookupBlock, lookupAlias);
    }

    public static int getFullIdByShortName(String shortName) {
        return getFullIdByShortName(shortName, true);
    }

    public static int getFullIdByShortName(String shortName, boolean lookupBlock) {
        return getFullIdByShortName(shortName, lookupBlock, false);
    }

    public static int getFullIdByShortName(String shortName, boolean lookupBlock, boolean lookupAlias) {
        int id = NAME_TO_ID.getInt(shortName);
        if (id != -1) {
            return Item.getFullId(id);
        }

        if (lookupBlock) {
            int blockFullId = Blocks.getFullIdByItemShortName(shortName, lookupAlias);
            if (blockFullId != -1) {
                return Item.getFullId(Block.getItemId(Block.getIdFromFullId(blockFullId)), Block.getDamageFromFullId(blockFullId));
            }
        }

        if (lookupAlias) {
            String alias = SIMPLE_ALIASES_MAP.get(shortName);
            if (alias != null) {
                id = NAME_TO_ID.getInt(alias);
                if (id != -1) {
                    return Item.getFullId(id);
                }
            }

            int[] fullIds = COMPLEX_ALIASES_MAP.get(shortName);
            if (fullIds != null) {
                return fullIds[0];
            }
        }
        return Integer.MIN_VALUE;
    }

    @Nullable
    public static String getNameById(int id) {
        if (id >= Short.MAX_VALUE) {
            return null;
        }
        if (id <= 0xff && id != GLOW_STICK) {
            return Blocks.getItemNameById(id < 0 ? 0xff - id : id);
        }
        return ID_TO_NAME[id];
    }

    @Nullable
    public static String getFullNameById(int id) {
        if (id >= Short.MAX_VALUE) {
            return null;
        }
        if (id <= 0xff && id != GLOW_STICK) {
            return Blocks.getItemFullNameById(id < 0 ? 0xff - id : id);
        }
        return ID_TO_FULL_NAME[id];
    }

    public static Map<String, String> getSimpleAliasesMap() {
        return SIMPLE_ALIASES_MAP;
    }

    public static Map<String, int[]> getComplexAliasesMap() {
        return COMPLEX_ALIASES_MAP;
    }

    public static Set<String> getTags(int itemId) {
        return getTags(itemId, 0);
    }

    public static Set<String> getTags(int itemId, int itemMeta) {
        Set<String> tags;
        if (itemId <= 0xff && itemId != GLOW_STICK) {
            tags = BLOCK_ID_TO_TAGS[Block.itemIdToBlockId(itemId)];
        } else {
            Set<String>[] data = ITEM_ID_TO_TAGS[itemId];
            if (data == null) {
                return Collections.emptySet();
            }
            tags = data[data.length == 1 ? 0 : itemMeta & 0xff];
        }
        if (tags == null) {
            return Collections.emptySet();
        }
        return tags;
    }

    public static boolean hasTag(String itemTag, int itemId) {
        return hasTag(itemTag, itemId, 0);
    }

    public static boolean hasTag(String itemTag, int itemId, int itemMeta) {
        return getTags(itemId, itemMeta).contains(itemTag);
    }

    public static IntSet getItemFullIdsByTag(String itemTag) {
        return TAG_TO_ITEM_FULL_IDS.getOrDefault(itemTag, IntSets.emptySet());
    }

    public static Item air() {
        return Item.get(AIR, 0, 0).setItemStackId(0);
    }

    private Items() {
        throw new IllegalStateException();
    }
}
