package cn.nukkit.item.enchantment;

import cn.nukkit.item.Item;
import cn.nukkit.math.LocalRandom;
import cn.nukkit.math.RandomSource;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
        List<Enchantment> resultEnchantments = new ArrayList<>();
        int enchantAbility = item.getEnchantAbility();
        if (enchantAbility <= 0) {
            return resultEnchantments;
        }
        int randomBound = (enchantAbility >> 2) + 1;
        int enchantingPower = cost + random.nextInt(randomBound) + random.nextInt(randomBound) + 1;
        // Random bonus for enchanting power between 0.85 and 1.15
        float bonus = 1 + random.nextFloat(-0.15f, 0.15f);
        enchantingPower = Math.max(1, Math.round(enchantingPower * bonus));

        List<Enchantment> availableEnchantments = getAvailableEnchantmentResults(item, enchantingPower, treasure);
        int candidateCount = availableEnchantments.size();
        Enchantment firstEnchantment = drawAndRemoveWeightedEnchantment(random, availableEnchantments);
        if (firstEnchantment == null) {
            return resultEnchantments;
        }
        resultEnchantments.add(firstEnchantment);

        int attempts = 1;
        // With probability min(1, (power + 1) / 50), continue trying enchantments while attempts remain.
        while (random.nextInt(50) <= enchantingPower && attempts < candidateCount) {
            Enchantment selected = drawAndRemoveWeightedEnchantment(random, availableEnchantments);
            if (selected == null) {
                break;
            }
            // Skip the selected enchantment if it conflicts with previously-chosen enchantments.
            boolean compatible = true;
            for (Enchantment existing : resultEnchantments) {
                if (selected.getId() == existing.getId() || !selected.isCompatibleWith(existing)) {
                    compatible = false;
                    break;
                }
            }
            if (compatible) {
                resultEnchantments.add(selected);
            }
            enchantingPower = (int) ((long) enchantingPower * 4 / 5 + 1);
            attempts++;
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

            for (int lvl = enchantment.getMaxLevel(); lvl >= enchantment.getMinLevel(); lvl--) {
                if (enchantingPower >= enchantment.getMinEnchantAbility(lvl)) {
                    enchantments.add(enchantment.clone().setLevel(lvl));
                    break;
                }
            }
        }
        enchantments.sort(Comparator.comparingInt(Enchantment::getId));
        return enchantments;
    }

    @Nullable
    private static Enchantment drawAndRemoveWeightedEnchantment(RandomSource random, List<Enchantment> enchantments) {
        if (enchantments.isEmpty()) {
            return null;
        }
        int totalWeight = 0;
        for (Enchantment enchantment : enchantments) {
            totalWeight += enchantment.getRarity().getWeight();
        }
        int randomWeight = random.nextInt(totalWeight);
        for (int i = 0; i < enchantments.size(); i++) {
            randomWeight -= enchantments.get(i).getRarity().getWeight();
            if (randomWeight < 0) {
                return enchantments.remove(i);
            }
        }
        return null;
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
