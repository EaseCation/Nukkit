package cn.nukkit.item.enchantment;

import cn.nukkit.item.Item;
import cn.nukkit.math.LocalRandom;
import cn.nukkit.math.RandomSource;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class EnchantmentHelper {

    public static void randomlyEnchant(Item item, int cost, boolean treasure) {
        randomlyEnchant(new LocalRandom(), item, cost, treasure);
    }

    public static void randomlyEnchant(RandomSource random, Item item, int cost, boolean treasure) {
        item.addEnchantment(selectEnchantments(random, item, cost, treasure).toArray(Enchantment.EMPTY));
    }

    public static List<Enchantment> selectEnchantments(Item item, int cost, boolean treasure) {
        return selectEnchantments(new LocalRandom(), item, cost, treasure);
    }

    public static List<Enchantment> selectEnchantments(RandomSource random, Item item, int cost, boolean treasure) {
        int enchantAbility = item.getEnchantAbility();
        int enchantingPower = cost + random.nextRange(0, enchantAbility >> 2) + random.nextRange(0, enchantAbility >> 2) + 1;
        // Random bonus for enchanting power between 0.85 and 1.15
        float bonus = 1 + (random.nextFloat() + random.nextFloat() - 1) * 0.15f;
        enchantingPower = Math.round(enchantingPower * bonus);

        List<Enchantment> resultEnchantments = new ArrayList<>();
        List<Enchantment> availableEnchantments = getAvailableEnchantmentResults(item, enchantingPower, treasure);

        Enchantment lastEnchantment = getRandomWeightedEnchantment(random, availableEnchantments);
        if (lastEnchantment != null) {
            resultEnchantments.add(lastEnchantment);

            // With probability (power + 1) / 50, continue adding enchantments
            while (random.nextFloat() <= (enchantingPower + 1) / 50f) {
                // Remove from the list of available enchantments anything that conflicts with previously-chosen enchantments
                List<Enchantment> nextAvailableEnchantments = new ArrayList<>();
                for (Enchantment enchantment : availableEnchantments) {
                    if (lastEnchantment.getId() == enchantment.getId() || !lastEnchantment.isCompatibleWith(enchantment)) {
                        continue;
                    }
                    nextAvailableEnchantments.add(enchantment);
                }
                availableEnchantments = nextAvailableEnchantments;

                lastEnchantment = getRandomWeightedEnchantment(random, availableEnchantments);
                if (lastEnchantment == null) {
                    break;
                }

                resultEnchantments.add(lastEnchantment);
                enchantingPower >>= 1;
            }
        }

        return resultEnchantments;
    }

    public static List<Enchantment> getAvailableEnchantmentResults(Item item, int enchantingPower, boolean treasure) {
        if (item.hasEnchantments()) {
            return Collections.emptyList();
        }

        List<Enchantment> enchantments = new ArrayList<>();
        for (Enchantment enchantment : Enchantments.getEnchantments().values()) {
            if (!enchantment.isLootable() || !treasure && enchantment.isTreasureOnly()) {
                continue;
            }

            if (!item.is(Item.ENCHANTED_BOOK) && !enchantment.canEnchant(item)) {
                continue;
            }

            for (int lvl = enchantment.getMaxLevel(); lvl > 0; lvl--) {
                if (enchantingPower >= enchantment.getMinEnchantAbility(lvl) && enchantingPower <= enchantment.getMaxEnchantAbility(lvl)) {
                    enchantments.add(enchantment.clone().setLevel(lvl));
                    break;
                }
            }
        }
        return enchantments;
    }

    @Nullable
    public static Enchantment getRandomWeightedEnchantment(List<Enchantment> enchantments) {
        return getRandomWeightedEnchantment(new LocalRandom(), enchantments);
    }

    @Nullable
    public static Enchantment getRandomWeightedEnchantment(RandomSource random, List<Enchantment> enchantments) {
        if (enchantments.isEmpty()) {
            return null;
        }

        int totalWeight = 0;
        for (Enchantment enchantment : enchantments) {
            totalWeight += enchantment.getRarity().getWeight();
        }

        int randomWeight = random.nextRange(1, totalWeight);
        for (Enchantment enchantment : enchantments) {
            randomWeight -= enchantment.getRarity().getWeight();
            if (randomWeight <= 0) {
                return enchantment;
            }
        }
        return null;
    }

    private EnchantmentHelper() {
        throw new IllegalStateException();
    }
}
