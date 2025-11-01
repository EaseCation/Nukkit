package cn.nukkit.loot;

import cn.nukkit.item.Item;
import cn.nukkit.loot.entries.LootPoolEntry;
import cn.nukkit.loot.predicates.LootItemCondition;
import cn.nukkit.loot.predicates.LootItemConditions;
import cn.nukkit.loot.providers.NumberProvider;
import cn.nukkit.math.Mth;
import cn.nukkit.math.RandomSource;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public record LootPool(
        LootPoolEntry[] entries,
        @Nullable
        LootItemCondition[] conditions,
        @Nullable
        LootPoolTiers tiers,
        NumberProvider rolls,
        @JsonProperty("bonus_rolls")
        @Nullable
        NumberProvider bonusRolls
) {
    private void addRandomItem(List<Item> result, RandomSource random, LootTableContext context) {
        List<LootPoolEntry> validEntries = new ArrayList<>();
        int totalWeight = 0;
        for (LootPoolEntry entry : entries) {
            if (!LootItemConditions.allApply(entry.getConditions(), random, context)) {
                continue;
            }
            int weight = entry.getWeight(context.getLuck());
            if (weight <= 0) {
                continue;
            }
            validEntries.add(entry);
            totalWeight += weight;
        }
        if (totalWeight == 0 || validEntries.isEmpty()) {
            return;
        }

        int index = random.nextInt(totalWeight);
        for (LootPoolEntry entry : validEntries) {
            index -= entry.getWeight(context.getLuck());
            if (index < 0) {
                entry.createItem(result, random, context);
                break;
            }
        }
    }

    private void addTier(List<Item> result, RandomSource random, LootTableContext context, int tier) {
        if (tier < 0 || tier >= entries.length) {
            return;
        }
        entries[tier].createItem(result, random, context);
    }

    public void addRandomItems(List<Item> result, RandomSource random, LootTableContext context) {
        if (entries == null) {
            return;
        }

        if (!LootItemConditions.allApply(conditions, random, context)) {
            return;
        }

        if (tiers != null) {
            int tierToUse = random.nextInt(0, tiers.initialRange());
            for (int i = 0; i < tiers.bonusRolls(); i++) {
                if (random.nextFloat() > tiers.bonusChance()) {
                    tierToUse++;
                }
            }
            addTier(result, random, context, tierToUse);
            return;
        }

        int count = rolls != null ? rolls.getInt(random) : 0;
        if (bonusRolls != null) {
            count += Mth.floor(bonusRolls.getFloat(random) * context.getLuck());
        }
        for (int i = 0; i < count; i++) {
            addRandomItem(result, random, context);
        }
    }
}
