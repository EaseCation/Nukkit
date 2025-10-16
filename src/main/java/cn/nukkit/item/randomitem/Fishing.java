package cn.nukkit.item.randomitem;

import cn.nukkit.GameVersion;
import cn.nukkit.entity.item.EntityFishingHook;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.math.Mth;
import cn.nukkit.potion.Potion;
import cn.nukkit.utils.DyeColor;

import static cn.nukkit.GameVersion.*;
import static cn.nukkit.item.randomitem.RandomItem.*;

/**
 * Created by Snake1999 on 2016/1/15.
 * Package cn.nukkit.item.randomitem in project nukkit.
 */
//TODO: loot table
public final class Fishing {
    private static final float FISH_TOTAL_WEIGHT = 100;
    private static final float TREASURE_TOTAL_WEIGHT = 36;
    private static final float JUNK_TOTAL_WEIGHT = 83;

    private static final float JUNGLE_FISH_TOTAL_WEIGHT = FISH_TOTAL_WEIGHT + 15;
    private static final float JUNGLE_JUNK_TOTAL_WEIGHT = JUNK_TOTAL_WEIGHT + 20;

    public static final Selector ROOT_FISHING = putSelector(new Selector(ROOT));
    public static final Selector ROOT_JUNGLE_FISHING = putSelector(new Selector(ROOT));

    public static final Selector FISHES = putSelector(new Selector(ROOT_FISHING), 0.85F);
    public static final Selector TREASURES = putSelector(new Selector(ROOT_FISHING), 0.05F);
    public static final Selector JUNKS = putSelector(new Selector(ROOT_FISHING), 0.1F);

    public static final Selector JUNGLE_FISHES = putSelector(new Selector(ROOT_JUNGLE_FISHING), 0.85f);
    public static final Selector JUNGLE_TREASURES = putSelector(new Selector(ROOT_JUNGLE_FISHING), 0.05f);
    public static final Selector JUNGLE_JUNKS = putSelector(new Selector(ROOT_JUNGLE_FISHING), 0.1f);

    public static final Selector FISH = putSelector(new ConstantItemSelector(Item.COD, FISHES), 60 / FISH_TOTAL_WEIGHT);
    public static final Selector SALMON = putSelector(new ConstantItemSelector(Item.SALMON, FISHES), 25 / FISH_TOTAL_WEIGHT);
    public static final Selector CLOWNFISH = putSelector(new ConstantItemSelector(Item.TROPICAL_FISH, FISHES), 2 / FISH_TOTAL_WEIGHT);
    public static final Selector PUFFERFISH = putSelector(new ConstantItemSelector(Item.PUFFERFISH, FISHES), 13 / FISH_TOTAL_WEIGHT);

    public static final Selector JUNGLE_FISH = putSelector(new ConstantItemSelector(Item.COD, JUNGLE_FISHES), 60 / JUNGLE_FISH_TOTAL_WEIGHT);
    public static final Selector JUNGLE_SALMON = putSelector(new ConstantItemSelector(Item.SALMON, JUNGLE_FISHES), 40 / JUNGLE_FISH_TOTAL_WEIGHT);
    public static final Selector JUNGLE_CLOWNFISH = putSelector(new ConstantItemSelector(Item.TROPICAL_FISH, JUNGLE_FISHES), 2 / JUNGLE_FISH_TOTAL_WEIGHT);
    public static final Selector JUNGLE_PUFFERFISH = putSelector(new ConstantItemSelector(Item.PUFFERFISH, JUNGLE_FISHES), 13 / JUNGLE_FISH_TOTAL_WEIGHT);

    public static final Selector TREASURE_BOW = putSelector(new ConstantItemSelector(Item.BOW, TREASURES).setDamage(0, 0.25f).enchantWithLevels(30, true), 5 / TREASURE_TOTAL_WEIGHT);
    public static final Selector TREASURE_ENCHANTED_BOOK = putSelector(new ConstantItemSelector(Item.ENCHANTED_BOOK, TREASURES).enchantWithLevels(30, true),  6 / TREASURE_TOTAL_WEIGHT);
    public static final Selector TREASURE_FISHING_ROD = putSelector(new ConstantItemSelector(Item.FISHING_ROD, TREASURES).setDamage(0, 0.25f).enchantWithLevels(30, true), 5 / TREASURE_TOTAL_WEIGHT);
    public static final Selector TREASURE_NAME_TAG = putSelector(new ConstantItemSelector(Item.NAME_TAG, TREASURES), 5 / TREASURE_TOTAL_WEIGHT);
    public static final Selector TREASURE_SADDLE = putSelector(new ConstantItemSelector(Item.SADDLE, TREASURES), 5 / TREASURE_TOTAL_WEIGHT);
    public static final Selector TREASURE_NAUTILUS_SHELL = registerFishingSelector(new ConstantItemSelector(Item.NAUTILUS_SHELL, TREASURES), 5 / TREASURE_TOTAL_WEIGHT, V1_4_0);

    public static final Selector JUNGLE_TREASURE_BOW = putSelector(new ConstantItemSelector(Item.BOW, JUNGLE_TREASURES).setDamage(0, 0.25f).enchantWithLevels(30, true), 5 / TREASURE_TOTAL_WEIGHT);
    public static final Selector JUNGLE_TREASURE_ENCHANTED_BOOK = putSelector(new ConstantItemSelector(Item.ENCHANTED_BOOK, JUNGLE_TREASURES).enchantWithLevels(30, true),  6 / TREASURE_TOTAL_WEIGHT);
    public static final Selector JUNGLE_TREASURE_FISHING_ROD = putSelector(new ConstantItemSelector(Item.FISHING_ROD, JUNGLE_TREASURES).setDamage(0, 0.25f).enchantWithLevels(30, true), 5 / TREASURE_TOTAL_WEIGHT);
    public static final Selector JUNGLE_TREASURE_NAME_TAG = putSelector(new ConstantItemSelector(Item.NAME_TAG, JUNGLE_TREASURES), 5 / TREASURE_TOTAL_WEIGHT);
    public static final Selector JUNGLE_TREASURE_SADDLE = putSelector(new ConstantItemSelector(Item.SADDLE, JUNGLE_TREASURES), 5 / TREASURE_TOTAL_WEIGHT);
    public static final Selector JUNGLE_TREASURE_NAUTILUS_SHELL = registerFishingSelector(new ConstantItemSelector(Item.NAUTILUS_SHELL, JUNGLE_TREASURES), 5 / TREASURE_TOTAL_WEIGHT, V1_4_0);

    public static final Selector JUNK_BOWL = putSelector(new ConstantItemSelector(Item.BOWL, JUNKS), 10 / JUNK_TOTAL_WEIGHT);
    public static final Selector JUNK_FISHING_ROD = putSelector(new ConstantItemSelector(Item.FISHING_ROD, JUNKS).setDamage(0, 0.9f), 2 / JUNK_TOTAL_WEIGHT);
    public static final Selector JUNK_LEATHER = putSelector(new ConstantItemSelector(Item.LEATHER, JUNKS), 10 / JUNK_TOTAL_WEIGHT);
    public static final Selector JUNK_LEATHER_BOOTS = putSelector(new ConstantItemSelector(Item.LEATHER_BOOTS, JUNKS).setDamage(0, 0.9f), 10 / JUNK_TOTAL_WEIGHT);
    public static final Selector JUNK_ROTTEN_FLESH = putSelector(new ConstantItemSelector(Item.ROTTEN_FLESH, JUNKS), 10 / JUNK_TOTAL_WEIGHT);
    public static final Selector JUNK_STICK = putSelector(new ConstantItemSelector(Item.STICK, JUNKS), 5 / JUNK_TOTAL_WEIGHT);
    public static final Selector JUNK_STRING_ITEM = putSelector(new ConstantItemSelector(Item.STRING, JUNKS), 5 / JUNK_TOTAL_WEIGHT);
    public static final Selector JUNK_WATTER_BOTTLE = putSelector(new ConstantItemSelector(Item.POTION, Potion.WATER, JUNKS), 10 / JUNK_TOTAL_WEIGHT);
    public static final Selector JUNK_BONE = putSelector(new ConstantItemSelector(Item.BONE, JUNKS), 10 / JUNK_TOTAL_WEIGHT);
    public static final Selector JUNK_INK_SAC = putSelector(new ConstantItemSelector(Item.DYE, DyeColor.BLACK.getDyeData(), 10, JUNKS), 1 / JUNK_TOTAL_WEIGHT);
    public static final Selector JUNK_TRIPWIRE_HOOK = putSelector(new ConstantItemSelector(ItemBlockID.TRIPWIRE_HOOK, JUNKS), 10 / JUNK_TOTAL_WEIGHT);
    public static final Selector JUNK_WATERLILY = putSelector(new ConstantItemSelector(ItemBlockID.WATERLILY, JUNKS), 17 / JUNK_TOTAL_WEIGHT);

    public static final Selector JUNGLE_JUNK_BOWL = putSelector(new ConstantItemSelector(Item.BOWL, JUNGLE_JUNKS), 10 / JUNGLE_JUNK_TOTAL_WEIGHT);
    public static final Selector JUNGLE_JUNK_FISHING_ROD = putSelector(new ConstantItemSelector(Item.FISHING_ROD, JUNGLE_JUNKS).setDamage(0, 0.9f), 2 / JUNGLE_JUNK_TOTAL_WEIGHT);
    public static final Selector JUNGLE_JUNK_LEATHER = putSelector(new ConstantItemSelector(Item.LEATHER, JUNGLE_JUNKS), 10 / JUNGLE_JUNK_TOTAL_WEIGHT);
    public static final Selector JUNGLE_JUNK_LEATHER_BOOTS = putSelector(new ConstantItemSelector(Item.LEATHER_BOOTS, JUNGLE_JUNKS).setDamage(0, 0.9f), 10 / JUNGLE_JUNK_TOTAL_WEIGHT);
    public static final Selector JUNGLE_JUNK_ROTTEN_FLESH = putSelector(new ConstantItemSelector(Item.ROTTEN_FLESH, JUNGLE_JUNKS), 10 / JUNGLE_JUNK_TOTAL_WEIGHT);
    public static final Selector JUNGLE_JUNK_STICK = putSelector(new ConstantItemSelector(Item.STICK, JUNGLE_JUNKS), 5 / JUNGLE_JUNK_TOTAL_WEIGHT);
    public static final Selector JUNGLE_JUNK_STRING_ITEM = putSelector(new ConstantItemSelector(Item.STRING, JUNGLE_JUNKS), 5 / JUNGLE_JUNK_TOTAL_WEIGHT);
    public static final Selector JUNGLE_JUNK_WATTER_BOTTLE = putSelector(new ConstantItemSelector(Item.POTION, Potion.WATER, JUNGLE_JUNKS), 10 / JUNGLE_JUNK_TOTAL_WEIGHT);
    public static final Selector JUNGLE_JUNK_BONE = putSelector(new ConstantItemSelector(Item.BONE, JUNGLE_JUNKS), 10 / JUNGLE_JUNK_TOTAL_WEIGHT);
    public static final Selector JUNGLE_JUNK_INK_SAC = putSelector(new ConstantItemSelector(Item.DYE, DyeColor.BLACK.getDyeData(), 10, JUNGLE_JUNKS), 1 / JUNGLE_JUNK_TOTAL_WEIGHT);
    public static final Selector JUNGLE_JUNK_TRIPWIRE_HOOK = putSelector(new ConstantItemSelector(ItemBlockID.TRIPWIRE_HOOK, JUNGLE_JUNKS), 10 / JUNGLE_JUNK_TOTAL_WEIGHT);
    public static final Selector JUNGLE_JUNK_WATERLILY = putSelector(new ConstantItemSelector(ItemBlockID.WATERLILY, JUNGLE_JUNKS), 17 / JUNGLE_JUNK_TOTAL_WEIGHT);
    public static final Selector JUNGLE_JUNK_COCOA = registerFishingSelector(new ConstantItemSelector(Item.COCOA, JUNGLE_JUNKS), 10 / JUNGLE_JUNK_TOTAL_WEIGHT, V1_8_0);
    public static final Selector JUNGLE_JUNK_BAMBOO = registerFishingSelector(new ConstantItemSelector(ItemBlockID.BAMBOO, JUNGLE_JUNKS), 10 / JUNGLE_JUNK_TOTAL_WEIGHT, V1_8_0);

    private static Selector registerFishingSelector(Selector selector, float chance) {
        return putSelector(selector, chance);
    }

    private static Selector registerFishingSelector(Selector selector, float chance, GameVersion version) {
        if (!version.isAvailable()) {
            return null;
        }
        return registerFishingSelector(selector, chance);
    }

    public static Item getFishingResult() {
        return getFishingResult(0, false);
    }

    public static Item getFishingResult(EntityFishingHook hook) {
        float luck;
        boolean jungle;
        if (hook != null) {
            luck = Math.max(hook.rod.getEnchantmentLevel(Enchantment.LUCK_OF_THE_SEA), 0);
//            if (hook.shootingEntity instanceof Player player) {
//                luck += player.getAttribute(Attribute.LUCK).getValue(); //TODO: AttributeMap
//            }

            int biomeId = hook.level.getBiomeId(hook.getFloorX(), hook.getFloorY(), hook.getFloorZ());
            jungle = biomeId == BiomeID.JUNGLE
                    || biomeId == BiomeID.JUNGLE_HILLS
                    || biomeId == BiomeID.JUNGLE_EDGE
                    || biomeId == BiomeID.JUNGLE_MUTATED
                    || biomeId == BiomeID.JUNGLE_EDGE_MUTATED
                    || biomeId == BiomeID.BAMBOO_JUNGLE
                    || biomeId == BiomeID.BAMBOO_JUNGLE_HILLS;
        } else {
            luck = 0;
            jungle = false;
        }
        return getFishingResult(luck, jungle);
    }

    public static Item getFishingResult(float luck) {
        return getFishingResult(luck, false);
    }

    public static Item getFishingResult(float luck, boolean jungle) {
        int treasureWeight = Math.max(Mth.floor(5 + 2 * luck), 0);
        int junkWeight = Math.max(Mth.floor(10 + -2 * luck), 0);
        int fishWeight = Math.max(Mth.floor(85 + -1 * luck), 0);

        float totalWeight = treasureWeight + junkWeight + fishWeight;
        float treasureChance = treasureWeight / totalWeight;
        float junkChance = junkWeight / totalWeight;
        float fishChance = fishWeight / totalWeight;

        Object result;
        if (jungle) {
            putSelector(JUNGLE_FISHES, fishChance);
            putSelector(JUNGLE_TREASURES, treasureChance);
            putSelector(JUNGLE_JUNKS, junkChance);
            result = selectFrom(ROOT_JUNGLE_FISHING);
        } else {
            putSelector(FISHES, fishChance);
            putSelector(TREASURES, treasureChance);
            putSelector(JUNKS, junkChance);
            result = selectFrom(ROOT_FISHING);
        }

        if (result instanceof Item) return (Item) result;
        return null;
    }
}
