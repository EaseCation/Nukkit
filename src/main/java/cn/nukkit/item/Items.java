package cn.nukkit.item;

import cn.nukkit.GameVersion;
import cn.nukkit.block.Block;
import cn.nukkit.block.Blocks;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.entity.EntityID;
import cn.nukkit.loot.LootTables;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.potion.PotionID;
import cn.nukkit.utils.DyeColor;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
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
    private static final Object2IntMap<String> COMPLEX_ALIASES_MAP = new Object2IntOpenHashMap<>();

    private static final AtomicInteger CUSTOM_ITEM_ID_ALLOCATOR = new AtomicInteger(CUSTOM_ITEM);

    static {
        NAME_TO_ID.defaultReturnValue(-1);
        FULL_NAME_TO_ID.defaultReturnValue(-1);
        COMPLEX_ALIASES_MAP.defaultReturnValue(Integer.MIN_VALUE);
    }

    static void registerVanillaItems() {
        registerItem(ItemNames.IRON_SHOVEL, IRON_SHOVEL, ItemShovelIron.class, ItemShovelIron::new);
        registerItem(ItemNames.IRON_PICKAXE, IRON_PICKAXE, ItemPickaxeIron.class, ItemPickaxeIron::new);
        registerItem(ItemNames.IRON_AXE, IRON_AXE, ItemAxeIron.class, ItemAxeIron::new);
        registerItem(ItemNames.FLINT_AND_STEEL, FLINT_AND_STEEL, ItemFlintSteel.class, ItemFlintSteel::new);
        registerItem(ItemNames.APPLE, APPLE, ItemApple.class, ItemApple::new);
        registerItem(ItemNames.BOW, BOW, ItemBow.class, ItemBow::new);
        registerItem(ItemNames.ARROW, ARROW, ItemArrow.class, ItemArrow::new, ItemArrow.TIPPED_ARROW + PotionID.UNDEFINED - 1);
        registerItem(ItemNames.COAL, COAL, ItemCoal.class, ItemCoal::new, 1);
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
        registerItem(ItemNames.BUCKET, BUCKET, ItemBucket.class, ItemBucket::new, ItemBucket.UNDEFINED_BUCKET - 1);

        registerItem(ItemNames.MINECART, MINECART, ItemMinecart.class, ItemMinecart::new);
        registerItem(ItemNames.SADDLE, SADDLE, ItemSaddle.class, ItemSaddle::new);
        registerItem(ItemNames.IRON_DOOR, IRON_DOOR, ItemDoorIron.class, ItemDoorIron::new);
        registerItem(ItemNames.REDSTONE, REDSTONE, ItemRedstone.class, ItemRedstone::new);
        registerItem(ItemNames.SNOWBALL, SNOWBALL, ItemSnowball.class, ItemSnowball::new);
        registerItem(ItemNames.BOAT, BOAT, ItemBoat.class, ItemBoat::new, ItemBoat.UNDEFINED_BOAT - 1);
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
        registerItem(ItemNames.DYE, DYE, ItemDye.class, ItemDye::new, ItemDye.DYE_COUNT - 1);
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

        registerItem(ItemNames.BANNER_PATTERN, BANNER_PATTERN, ItemBannerPattern.class, ItemBannerPattern::new, ItemBannerPattern.UNDEFINED_BANNER_PATTERN - 1, V1_10_0);
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
        registerComplexAliases();

        initializeItemBlockCache();
    }

    /**
     * deferred
     */
    public static void registerVanillaNewItems() {
        registerNewItem(ItemNames.COPPER_INGOT, COPPER_INGOT, ItemIngotCopper.class, ItemIngotCopper::new, V1_17_0);
        registerNewItem(ItemNames.GLOW_BERRIES, GLOW_BERRIES, ItemGlowBerries.class, ItemGlowBerries::new, V1_17_0);

        registerNewItem(ItemNames.CHEST_BOAT, CHEST_BOAT, ItemBoatChest.class, ItemBoatChest::new, ItemBoatChest.UNDEFINED_BOAT - 1, V1_19_0);
        registerNewItemAux(ItemNames.OAK_CHEST_BOAT, CHEST_BOAT, ItemBoatChest.OAK_BOAT, V1_19_0);
        registerNewItemAux(ItemNames.SPRUCE_CHEST_BOAT, CHEST_BOAT, ItemBoatChest.SPRUCE_BOAT, V1_19_0);
        registerNewItemAux(ItemNames.BIRCH_CHEST_BOAT, CHEST_BOAT, ItemBoatChest.BIRCH_BOAT, V1_19_0);
        registerNewItemAux(ItemNames.JUNGLE_CHEST_BOAT, CHEST_BOAT, ItemBoatChest.JUNGLE_BOAT, V1_19_0);
        registerNewItemAux(ItemNames.ACACIA_CHEST_BOAT, CHEST_BOAT, ItemBoatChest.ACACIA_BOAT, V1_19_0);
        registerNewItemAux(ItemNames.DARK_OAK_CHEST_BOAT, CHEST_BOAT, ItemBoatChest.DARK_OAK_BOAT, V1_19_0);
        registerNewItemAux(ItemNames.MANGROVE_CHEST_BOAT, CHEST_BOAT, ItemBoatChest.MANGROVE_BOAT, V1_19_0);
        registerNewItemAux(ItemNames.BAMBOO_CHEST_RAFT, CHEST_BOAT, ItemBoatChest.BAMBOO_RAFT, V1_20_0);
        registerNewItemAux(ItemNames.CHERRY_CHEST_BOAT, CHEST_BOAT, ItemBoatChest.CHERRY_BOAT, V1_20_0);
        registerNewItemAux(ItemNames.PALE_OAK_CHEST_BOAT, CHEST_BOAT, ItemBoatChest.PALE_OAK_BOAT, V1_21_50);

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
/*
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
*/
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

    private static void registerComplexAliases() {
        registerComplexAlias(ItemNames.CREEPER_BANNER_PATTERN, BANNER_PATTERN, ItemBannerPattern.CREEPER_BANNER_PATTERN, V1_16_100);
        registerComplexAlias(ItemNames.SKULL_BANNER_PATTERN, BANNER_PATTERN, ItemBannerPattern.SKULL_BANNER_PATTERN, V1_16_100);
        registerComplexAlias(ItemNames.FLOWER_BANNER_PATTERN, BANNER_PATTERN, ItemBannerPattern.FLOWER_BANNER_PATTERN, V1_16_100);
        registerComplexAlias(ItemNames.MOJANG_BANNER_PATTERN, BANNER_PATTERN, ItemBannerPattern.MOJANG_BANNER_PATTERN, V1_16_100);
        registerComplexAlias(ItemNames.FIELD_MASONED_BANNER_PATTERN, BANNER_PATTERN, ItemBannerPattern.FIELD_MASONED_BANNER_PATTERN, V1_16_100);
        registerComplexAlias(ItemNames.BORDURE_INDENTED_BANNER_PATTERN, BANNER_PATTERN, ItemBannerPattern.BORDURE_INDENTED_BANNER_PATTERN, V1_16_100);
        registerComplexAlias(ItemNames.PIGLIN_BANNER_PATTERN, BANNER_PATTERN, ItemBannerPattern.PIGLIN_BANNER_PATTERN, V1_16_100);
        registerComplexAlias(ItemNames.GLOBE_BANNER_PATTERN, BANNER_PATTERN, ItemBannerPattern.GLOBE_BANNER_PATTERN, V1_18_10);
        registerComplexAlias(ItemNames.FLOW_BANNER_PATTERN, BANNER_PATTERN, ItemBannerPattern.FLOW_BANNER_PATTERN, V1_21_0);
        registerComplexAlias(ItemNames.GUSTER_BANNER_PATTERN, BANNER_PATTERN, ItemBannerPattern.GUSTER_BANNER_PATTERN, V1_21_0);

        registerComplexAlias(ItemNames.OAK_BOAT, BOAT, ItemBoat.OAK_BOAT, V1_16_100);
        registerComplexAlias(ItemNames.SPRUCE_BOAT, BOAT, ItemBoat.SPRUCE_BOAT, V1_16_100);
        registerComplexAlias(ItemNames.BIRCH_BOAT, BOAT, ItemBoat.BIRCH_BOAT, V1_16_100);
        registerComplexAlias(ItemNames.JUNGLE_BOAT, BOAT, ItemBoat.JUNGLE_BOAT, V1_16_100);
        registerComplexAlias(ItemNames.ACACIA_BOAT, BOAT, ItemBoat.ACACIA_BOAT, V1_16_100);
        registerComplexAlias(ItemNames.DARK_OAK_BOAT, BOAT, ItemBoat.DARK_OAK_BOAT, V1_16_100);
        registerComplexAlias(ItemNames.MANGROVE_BOAT, BOAT, ItemBoat.MANGROVE_BOAT, V1_19_0);
        registerComplexAlias(ItemNames.BAMBOO_RAFT, BOAT, ItemBoat.BAMBOO_RAFT, V1_20_0);
        registerComplexAlias(ItemNames.CHERRY_BOAT, BOAT, ItemBoat.CHERRY_BOAT, V1_20_0);
        registerComplexAlias(ItemNames.PALE_OAK_BOAT, BOAT, ItemBoat.PALE_OAK_BOAT, V1_21_50);

        registerComplexAlias(ItemNames.MILK_BUCKET, BUCKET, ItemBucket.MILK_BUCKET, V1_16_100);
        registerComplexAlias(ItemNames.COD_BUCKET, BUCKET, ItemBucket.COD_BUCKET, V1_16_100);
        registerComplexAlias(ItemNames.SALMON_BUCKET, BUCKET, ItemBucket.SALMON_BUCKET, V1_16_100);
        registerComplexAlias(ItemNames.TROPICAL_FISH_BUCKET, BUCKET, ItemBucket.TROPICAL_FISH_BUCKET, V1_16_100);
        registerComplexAlias(ItemNames.PUFFERFISH_BUCKET, BUCKET, ItemBucket.PUFFERFISH_BUCKET, V1_16_100);
        registerComplexAlias(ItemNames.WATER_BUCKET, BUCKET, ItemBucket.WATER_BUCKET, V1_16_100);
        registerComplexAlias(ItemNames.LAVA_BUCKET, BUCKET, ItemBucket.LAVA_BUCKET, V1_16_100);
        registerComplexAlias(ItemNames.POWDER_SNOW_BUCKET, BUCKET, ItemBucket.POWDER_SNOW_BUCKET, V1_17_0);
        registerComplexAlias(ItemNames.AXOLOTL_BUCKET, BUCKET, ItemBucket.AXOLOTL_BUCKET, V1_17_0);
        registerComplexAlias(ItemNames.TADPOLE_BUCKET, BUCKET, ItemBucket.TADPOLE_BUCKET, V1_19_0);

        registerComplexAlias(ItemNames.CHARCOAL, COAL, ItemCoal.TYPE_CHARCOAL, V1_16_100);

        registerComplexAlias(ItemNames.INK_SAC, DYE, ItemDye.INK_SAC, V1_16_100);
        registerComplexAlias(ItemNames.RED_DYE, DYE, DyeColor.RED.getDyeData(), V1_16_100);
        registerComplexAlias(ItemNames.GREEN_DYE, DYE, DyeColor.GREEN.getDyeData(), V1_16_100);
        registerComplexAlias(ItemNames.COCOA_BEANS, DYE, ItemDye.COCOA_BEANS, V1_16_100);
        registerComplexAlias(ItemNames.LAPIS_LAZULI, DYE, ItemDye.LAPIS_LAZULI, V1_16_100);
        registerComplexAlias(ItemNames.PURPLE_DYE, DYE, DyeColor.PURPLE.getDyeData(), V1_16_100);
        registerComplexAlias(ItemNames.CYAN_DYE, DYE, DyeColor.CYAN.getDyeData(), V1_16_100);
        registerComplexAlias(ItemNames.LIGHT_GRAY_DYE, DYE, DyeColor.LIGHT_GRAY.getDyeData(), V1_16_100);
        registerComplexAlias(ItemNames.GRAY_DYE, DYE, DyeColor.GRAY.getDyeData(), V1_16_100);
        registerComplexAlias(ItemNames.PINK_DYE, DYE, DyeColor.PINK.getDyeData(), V1_16_100);
        registerComplexAlias(ItemNames.LIME_DYE, DYE, DyeColor.LIME.getDyeData(), V1_16_100);
        registerComplexAlias(ItemNames.YELLOW_DYE, DYE, DyeColor.YELLOW.getDyeData(), V1_16_100);
        registerComplexAlias(ItemNames.LIGHT_BLUE_DYE, DYE, DyeColor.LIGHT_BLUE.getDyeData(), V1_16_100);
        registerComplexAlias(ItemNames.MAGENTA_DYE, DYE, DyeColor.MAGENTA.getDyeData(), V1_16_100);
        registerComplexAlias(ItemNames.ORANGE_DYE, DYE, DyeColor.ORANGE.getDyeData(), V1_16_100);
        registerComplexAlias(ItemNames.BONE_MEAL, DYE, ItemDye.BONE_MEAL, V1_16_100);
        registerComplexAlias(ItemNames.BLACK_DYE, DYE, ItemDye.BLACK_NEW, V1_16_100);
        registerComplexAlias(ItemNames.BROWN_DYE, DYE, ItemDye.BROWN_NEW, V1_16_100);
        registerComplexAlias(ItemNames.BLUE_DYE, DYE, ItemDye.BLUE_NEW, V1_16_100);
        registerComplexAlias(ItemNames.WHITE_DYE, DYE, ItemDye.WHITE_NEW, V1_16_100);

        registerComplexAlias(ItemNames.CHICKEN_SPAWN_EGG, SPAWN_EGG, EntityID.CHICKEN, V1_16_100);
        registerComplexAlias(ItemNames.COW_SPAWN_EGG, SPAWN_EGG, EntityID.COW, V1_16_100);
        registerComplexAlias(ItemNames.PIG_SPAWN_EGG, SPAWN_EGG, EntityID.PIG, V1_16_100);
        registerComplexAlias(ItemNames.SHEEP_SPAWN_EGG, SPAWN_EGG, EntityID.SHEEP, V1_16_100);
        registerComplexAlias(ItemNames.WOLF_SPAWN_EGG, SPAWN_EGG, EntityID.WOLF, V1_16_100);
        registerComplexAlias(ItemNames.VILLAGER_SPAWN_EGG, SPAWN_EGG, EntityID.VILLAGER_V2, V1_16_100);
        registerComplexAlias(ItemNames.MOOSHROOM_SPAWN_EGG, SPAWN_EGG, EntityID.MOOSHROOM, V1_16_100);
        registerComplexAlias(ItemNames.SQUID_SPAWN_EGG, SPAWN_EGG, EntityID.SQUID, V1_16_100);
        registerComplexAlias(ItemNames.RABBIT_SPAWN_EGG, SPAWN_EGG, EntityID.RABBIT, V1_16_100);
        registerComplexAlias(ItemNames.BAT_SPAWN_EGG, SPAWN_EGG, EntityID.BAT, V1_16_100);
        registerComplexAlias(ItemNames.OCELOT_SPAWN_EGG, SPAWN_EGG, EntityID.OCELOT, V1_16_100);
        registerComplexAlias(ItemNames.HORSE_SPAWN_EGG, SPAWN_EGG, EntityID.HORSE, V1_16_100);
        registerComplexAlias(ItemNames.DONKEY_SPAWN_EGG, SPAWN_EGG, EntityID.DONKEY, V1_16_100);
        registerComplexAlias(ItemNames.MULE_SPAWN_EGG, SPAWN_EGG, EntityID.MULE, V1_16_100);
        registerComplexAlias(ItemNames.SKELETON_HORSE_SPAWN_EGG, SPAWN_EGG, EntityID.SKELETON_HORSE, V1_16_100);
        registerComplexAlias(ItemNames.ZOMBIE_HORSE_SPAWN_EGG, SPAWN_EGG, EntityID.ZOMBIE_HORSE, V1_16_100);
        registerComplexAlias(ItemNames.POLAR_BEAR_SPAWN_EGG, SPAWN_EGG, EntityID.POLAR_BEAR, V1_16_100);
        registerComplexAlias(ItemNames.LLAMA_SPAWN_EGG, SPAWN_EGG, EntityID.LLAMA, V1_16_100);
        registerComplexAlias(ItemNames.PARROT_SPAWN_EGG, SPAWN_EGG, EntityID.PARROT, V1_16_100);
        registerComplexAlias(ItemNames.DOLPHIN_SPAWN_EGG, SPAWN_EGG, EntityID.DOLPHIN, V1_16_100);
        registerComplexAlias(ItemNames.ZOMBIE_SPAWN_EGG, SPAWN_EGG, EntityID.ZOMBIE, V1_16_100);
        registerComplexAlias(ItemNames.CREEPER_SPAWN_EGG, SPAWN_EGG, EntityID.CREEPER, V1_16_100);
        registerComplexAlias(ItemNames.SKELETON_SPAWN_EGG, SPAWN_EGG, EntityID.SKELETON, V1_16_100);
        registerComplexAlias(ItemNames.SPIDER_SPAWN_EGG, SPAWN_EGG, EntityID.SPIDER, V1_16_100);
        registerComplexAlias(ItemNames.ZOMBIE_PIGMAN_SPAWN_EGG, SPAWN_EGG, EntityID.ZOMBIE_PIGMAN, V1_16_100);
        registerComplexAlias(ItemNames.SLIME_SPAWN_EGG, SPAWN_EGG, EntityID.SLIME, V1_16_100);
        registerComplexAlias(ItemNames.ENDERMAN_SPAWN_EGG, SPAWN_EGG, EntityID.ENDERMAN, V1_16_100);
        registerComplexAlias(ItemNames.SILVERFISH_SPAWN_EGG, SPAWN_EGG, EntityID.SILVERFISH, V1_16_100);
        registerComplexAlias(ItemNames.CAVE_SPIDER_SPAWN_EGG, SPAWN_EGG, EntityID.CAVE_SPIDER, V1_16_100);
        registerComplexAlias(ItemNames.GHAST_SPAWN_EGG, SPAWN_EGG, EntityID.GHAST, V1_16_100);
        registerComplexAlias(ItemNames.MAGMA_CUBE_SPAWN_EGG, SPAWN_EGG, EntityID.MAGMA_CUBE, V1_16_100);
        registerComplexAlias(ItemNames.BLAZE_SPAWN_EGG, SPAWN_EGG, EntityID.BLAZE, V1_16_100);
        registerComplexAlias(ItemNames.ZOMBIE_VILLAGER_SPAWN_EGG, SPAWN_EGG, EntityID.ZOMBIE_VILLAGER_V2, V1_16_100);
        registerComplexAlias(ItemNames.WITCH_SPAWN_EGG, SPAWN_EGG, EntityID.WITCH, V1_16_100);
        registerComplexAlias(ItemNames.STRAY_SPAWN_EGG, SPAWN_EGG, EntityID.STRAY, V1_16_100);
        registerComplexAlias(ItemNames.HUSK_SPAWN_EGG, SPAWN_EGG, EntityID.HUSK, V1_16_100);
        registerComplexAlias(ItemNames.WITHER_SKELETON_SPAWN_EGG, SPAWN_EGG, EntityID.WITHER_SKELETON, V1_16_100);
        registerComplexAlias(ItemNames.GUARDIAN_SPAWN_EGG, SPAWN_EGG, EntityID.GUARDIAN, V1_16_100);
        registerComplexAlias(ItemNames.ELDER_GUARDIAN_SPAWN_EGG, SPAWN_EGG, EntityID.ELDER_GUARDIAN, V1_16_100);
        registerComplexAlias(ItemNames.NPC_SPAWN_EGG, SPAWN_EGG, EntityID.NPC, V1_16_100);
        registerComplexAlias(ItemNames.SHULKER_SPAWN_EGG, SPAWN_EGG, EntityID.SHULKER, V1_16_100);
        registerComplexAlias(ItemNames.ENDERMITE_SPAWN_EGG, SPAWN_EGG, EntityID.ENDERMITE, V1_16_100);
        registerComplexAlias(ItemNames.AGENT_SPAWN_EGG, SPAWN_EGG, EntityID.AGENT, V1_16_100);
        registerComplexAlias(ItemNames.VINDICATOR_SPAWN_EGG, SPAWN_EGG, EntityID.VINDICATOR, V1_16_100);
        registerComplexAlias(ItemNames.PHANTOM_SPAWN_EGG, SPAWN_EGG, EntityID.PHANTOM, V1_16_100);
        registerComplexAlias(ItemNames.RAVAGER_SPAWN_EGG, SPAWN_EGG, EntityID.RAVAGER, V1_16_100);
        registerComplexAlias(ItemNames.TURTLE_SPAWN_EGG, SPAWN_EGG, EntityID.TURTLE, V1_16_100);
        registerComplexAlias(ItemNames.CAT_SPAWN_EGG, SPAWN_EGG, EntityID.CAT, V1_16_100);
        registerComplexAlias(ItemNames.EVOKER_SPAWN_EGG, SPAWN_EGG, EntityID.EVOCATION_ILLAGER, V1_16_100);
        registerComplexAlias(ItemNames.VEX_SPAWN_EGG, SPAWN_EGG, EntityID.VEX, V1_16_100);
        registerComplexAlias(ItemNames.PUFFERFISH_SPAWN_EGG, SPAWN_EGG, EntityID.PUFFERFISH, V1_16_100);
        registerComplexAlias(ItemNames.SALMON_SPAWN_EGG, SPAWN_EGG, EntityID.SALMON, V1_16_100);
        registerComplexAlias(ItemNames.DROWNED_SPAWN_EGG, SPAWN_EGG, EntityID.DROWNED, V1_16_100);
        registerComplexAlias(ItemNames.TROPICAL_FISH_SPAWN_EGG, SPAWN_EGG, EntityID.TROPICALFISH, V1_16_100);
        registerComplexAlias(ItemNames.COD_SPAWN_EGG, SPAWN_EGG, EntityID.COD, V1_16_100);
        registerComplexAlias(ItemNames.PANDA_SPAWN_EGG, SPAWN_EGG, EntityID.PANDA, V1_16_100);
        registerComplexAlias(ItemNames.PILLAGER_SPAWN_EGG, SPAWN_EGG, EntityID.PILLAGER, V1_16_100);
        registerComplexAlias(ItemNames.WANDERING_TRADER_SPAWN_EGG, SPAWN_EGG, EntityID.WANDERING_TRADER, V1_16_100);
        registerComplexAlias(ItemNames.FOX_SPAWN_EGG, SPAWN_EGG, EntityID.FOX, V1_16_100);
        registerComplexAlias(ItemNames.BEE_SPAWN_EGG, SPAWN_EGG, EntityID.BEE, V1_16_100);
        registerComplexAlias(ItemNames.PIGLIN_SPAWN_EGG, SPAWN_EGG, EntityID.PIGLIN, V1_16_100);
        registerComplexAlias(ItemNames.HOGLIN_SPAWN_EGG, SPAWN_EGG, EntityID.HOGLIN, V1_16_100);
        registerComplexAlias(ItemNames.STRIDER_SPAWN_EGG, SPAWN_EGG, EntityID.STRIDER, V1_16_100);
        registerComplexAlias(ItemNames.ZOGLIN_SPAWN_EGG, SPAWN_EGG, EntityID.ZOGLIN, V1_16_100);
        registerComplexAlias(ItemNames.PIGLIN_BRUTE_SPAWN_EGG, SPAWN_EGG, EntityID.PIGLIN_BRUTE, V1_16_100);
        registerComplexAlias(ItemNames.GOAT_SPAWN_EGG, SPAWN_EGG, EntityID.GOAT, V1_17_0);
        registerComplexAlias(ItemNames.GLOW_SQUID_SPAWN_EGG, SPAWN_EGG, EntityID.GLOW_SQUID, V1_17_0);
        registerComplexAlias(ItemNames.AXOLOTL_SPAWN_EGG, SPAWN_EGG, EntityID.AXOLOTL, V1_17_0);
        registerComplexAlias(ItemNames.WARDEN_SPAWN_EGG, SPAWN_EGG, EntityID.WARDEN, V1_19_0);
        registerComplexAlias(ItemNames.FROG_SPAWN_EGG, SPAWN_EGG, EntityID.FROG, V1_19_0);
        registerComplexAlias(ItemNames.TADPOLE_SPAWN_EGG, SPAWN_EGG, EntityID.TADPOLE, V1_19_0);
        registerComplexAlias(ItemNames.ALLAY_SPAWN_EGG, SPAWN_EGG, EntityID.ALLAY, V1_19_0);
        registerComplexAlias(ItemNames.TRADER_LLAMA_SPAWN_EGG, SPAWN_EGG, EntityID.TRADER_LLAMA, V1_19_10);
        registerComplexAlias(ItemNames.IRON_GOLEM_SPAWN_EGG, SPAWN_EGG, EntityID.IRON_GOLEM, V1_19_60);
        registerComplexAlias(ItemNames.SNOW_GOLEM_SPAWN_EGG, SPAWN_EGG, EntityID.SNOW_GOLEM, V1_19_60);
        registerComplexAlias(ItemNames.WITHER_SPAWN_EGG, SPAWN_EGG, EntityID.WITHER, V1_19_60);
        registerComplexAlias(ItemNames.ENDER_DRAGON_SPAWN_EGG, SPAWN_EGG, EntityID.ENDER_DRAGON, V1_19_60);
        registerComplexAlias(ItemNames.CAMEL_SPAWN_EGG, SPAWN_EGG, EntityID.CAMEL, V1_20_0);
        registerComplexAlias(ItemNames.SNIFFER_SPAWN_EGG, SPAWN_EGG, EntityID.SNIFFER, V1_20_0);
        registerComplexAlias(ItemNames.ARMADILLO_SPAWN_EGG, SPAWN_EGG, EntityID.ARMADILLO, V1_20_80);
        registerComplexAlias(ItemNames.BREEZE_SPAWN_EGG, SPAWN_EGG, EntityID.BREEZE, V1_21_0);
        registerComplexAlias(ItemNames.BOGGED_SPAWN_EGG, SPAWN_EGG, EntityID.BOGGED, V1_21_0);
        registerComplexAlias(ItemNames.CREAKING_SPAWN_EGG, SPAWN_EGG, EntityID.CREAKING, V1_21_50);
        registerComplexAlias(ItemNames.HAPPY_GHAST_SPAWN_EGG, SPAWN_EGG, EntityID.HAPPY_GHAST, V1_21_90);
        registerComplexAlias(ItemNames.COPPER_GOLEM_SPAWN_EGG, SPAWN_EGG, EntityID.COPPER_GOLEM, V1_21_111);
/*
        registerComplexAlias(ItemNames.NAUTILUS_SPAWN_EGG, SPAWN_EGG, EntityID.NAUTILUS, V1_21_130);
        registerComplexAlias(ItemNames.ZOMBIE_NAUTILUS_SPAWN_EGG, SPAWN_EGG, EntityID.ZOMBIE_NAUTILUS, V1_21_130);
        registerComplexAlias(ItemNames.PARCHED_SPAWN_EGG, SPAWN_EGG, EntityID.PARCHED, V1_21_130);
        registerComplexAlias(ItemNames.CAMEL_HUSK_SPAWN_EGG, SPAWN_EGG, EntityID.CAMEL_HUSK, V1_21_130);
*/
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
        return registerItem(name, id, clazz, factory, maxAuxVal);
    }

    /**
     * @param version min required base game version
     */
    private static void registerNewItemAux(String name, int id, int meta, GameVersion version) {
        if (!version.isAvailable()) {
            return;
        }
        ItemSerializer.registerItemAux("minecraft:" + name, id, meta);
        registerComplexAlias(name, id, meta, version);
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
        COMPLEX_ALIASES_MAP.put(alias, Item.getFullId(id, meta));
    }

    public static Class<? extends Item> registerCustomItem(String fullName, int id, Class<? extends Item> clazz, ItemFactory factory) {
        return registerCustomItem(fullName, id, clazz, factory, (CompoundTag) null);
    }

    public static Class<? extends Item> registerCustomItem(String fullName, int id, Class<? extends Item> clazz, ItemFactory factory, @Nullable CompoundTag components) {
        return registerCustomItem(fullName, id, clazz, factory, components != null ? protocol -> components : null);
    }

    public static Class<? extends Item> registerCustomItem(String fullName, int id, Class<? extends Item> clazz, ItemFactory factory, @Nullable IntFunction<CompoundTag> componentsSupplier) {
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
                components = componentsSupplier.apply(GameVersion.getFeatureVersion().getProtocol());
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

            return COMPLEX_ALIASES_MAP.getInt(shortName);
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

    public static Object2IntMap<String> getComplexAliasesMap() {
        return COMPLEX_ALIASES_MAP;
    }

    public static Item air() {
        return Item.get(AIR, 0, 0);
    }

    private Items() {
        throw new IllegalStateException();
    }
}
