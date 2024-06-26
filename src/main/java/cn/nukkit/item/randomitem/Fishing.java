package cn.nukkit.item.randomitem;

import cn.nukkit.GameVersion;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.math.Mth;
import cn.nukkit.potion.Potion;
import cn.nukkit.utils.DyeColor;

import static cn.nukkit.GameVersion.*;
import static cn.nukkit.item.randomitem.RandomItem.*;

/**
 * Created by Snake1999 on 2016/1/15.
 * Package cn.nukkit.item.randomitem in project nukkit.
 */
public final class Fishing {

    public static final Selector ROOT_FISHING = putSelector(new Selector(ROOT));

    public static final Selector FISHES = putSelector(new Selector(ROOT_FISHING), 0.85F);
    public static final Selector TREASURES = putSelector(new Selector(ROOT_FISHING), 0.05F);
    public static final Selector JUNKS = putSelector(new Selector(ROOT_FISHING), 0.1F);
    public static final Selector FISH = putSelector(new ConstantItemSelector(Item.COD, FISHES), 0.6F);
    public static final Selector SALMON = putSelector(new ConstantItemSelector(Item.SALMON, FISHES), 0.25F);
    public static final Selector CLOWNFISH = putSelector(new ConstantItemSelector(Item.TROPICAL_FISH, FISHES), 0.02F);
    public static final Selector PUFFERFISH = putSelector(new ConstantItemSelector(Item.PUFFERFISH, FISHES), 0.13F);
    public static final Selector TREASURE_BOW = putSelector(new ConstantItemSelector(Item.BOW, TREASURES), 0.1667F);
    public static final Selector TREASURE_ENCHANTED_BOOK = putSelector(new ConstantItemSelector(Item.ENCHANTED_BOOK, TREASURES),  0.1667F);
    public static final Selector TREASURE_FISHING_ROD = putSelector(new ConstantItemSelector(Item.FISHING_ROD, TREASURES), 0.1667F);
    public static final Selector TREASURE_NAME_TAG = putSelector(new ConstantItemSelector(Item.NAME_TAG, TREASURES), 0.1667F);
    public static final Selector TREASURE_SADDLE = putSelector(new ConstantItemSelector(Item.SADDLE, TREASURES), 0.1667F);
    public static final Selector JUNK_BOWL = putSelector(new ConstantItemSelector(Item.BOWL, JUNKS), 0.12F);
    public static final Selector JUNK_FISHING_ROD = putSelector(new ConstantItemSelector(Item.FISHING_ROD, JUNKS), 0.024F);
    public static final Selector JUNK_LEATHER = putSelector(new ConstantItemSelector(Item.LEATHER, JUNKS), 0.12F);
    public static final Selector JUNK_LEATHER_BOOTS = putSelector(new ConstantItemSelector(Item.LEATHER_BOOTS, JUNKS), 0.12F);
    public static final Selector JUNK_ROTTEN_FLESH = putSelector(new ConstantItemSelector(Item.ROTTEN_FLESH, JUNKS), 0.12F);
    public static final Selector JUNK_STICK = putSelector(new ConstantItemSelector(Item.STICK, JUNKS), 0.06F);
    public static final Selector JUNK_STRING_ITEM = putSelector(new ConstantItemSelector(Item.STRING, JUNKS), 0.06F);
    public static final Selector JUNK_WATTER_BOTTLE = putSelector(new ConstantItemSelector(Item.POTION, Potion.WATER, JUNKS), 0.12F);
    public static final Selector JUNK_BONE = putSelector(new ConstantItemSelector(Item.BONE, JUNKS), 0.12F);
    public static final Selector JUNK_INK_SAC = putSelector(new ConstantItemSelector(Item.DYE, DyeColor.BLACK.getDyeData(), 10, JUNKS), 0.012F);
    public static final Selector JUNK_TRIPWIRE_HOOK = putSelector(new ConstantItemSelector(Item.TRIPWIRE_HOOK, JUNKS), 0.12F);

    public static final Selector TREASURE_NAUTILUS_SHELL = registerFishingSelector(new ConstantItemSelector(Item.NAUTILUS_SHELL, TREASURES), 0.1667F, V1_4_0);

//    public static final Selector JUNK_COCOA = registerFishingSelector(new ConstantItemSelector(Item.COCOA, JUNKS), 0.06f, V1_8_0); //TODO: jungle only
//    public static final Selector JUNK_BAMBOO = registerFishingSelector(new ConstantItemSelector(Item.BAMBOO, JUNKS), 0.06f, V1_8_0); //TODO: jungle only

    private static Selector registerFishingSelector(Selector selector, float chance) {
        return putSelector(selector, chance);
    }

    private static Selector registerFishingSelector(Selector selector, float chance, GameVersion version) {
        if (!version.isAvailable()) {
            return null;
        }
        return registerFishingSelector(selector, chance);
    }

    public static Item getFishingResult(Item rod) {
        int fortuneLevel = 0;
        int lureLevel = 0;
        if (rod != null) {
            if (rod.getEnchantment(Enchantment.LUCK_OF_THE_SEA) != null) {
                fortuneLevel = rod.getEnchantment(Enchantment.LUCK_OF_THE_SEA).getLevel();
            } else if (rod.getEnchantment(Enchantment.LURE) != null) {
                lureLevel = rod.getEnchantment(Enchantment.LURE).getLevel();
            }
        }
        return getFishingResult(fortuneLevel, lureLevel);
    }

    public static Item getFishingResult(int fortuneLevel, int lureLevel) {
        float treasureChance = Mth.clamp(0.05f + 0.01f * fortuneLevel - 0.01f * lureLevel, 0, 1);
        float junkChance = Mth.clamp(0.05f - 0.025f * fortuneLevel - 0.01f * lureLevel, 0, 1);
        float fishChance = Mth.clamp(1 - treasureChance - junkChance, 0, 1);
        putSelector(FISHES, fishChance);
        putSelector(TREASURES, treasureChance);
        putSelector(JUNKS, junkChance);
        Object result = selectFrom(ROOT_FISHING);
        if (result instanceof Item) return (Item) result;
        return null;
    }
}
