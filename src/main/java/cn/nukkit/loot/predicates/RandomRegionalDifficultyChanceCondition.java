package cn.nukkit.loot.predicates;

import cn.nukkit.loot.LootTableContext;
import cn.nukkit.math.RandomSource;
import com.fasterxml.jackson.annotation.JsonProperty;

public record RandomRegionalDifficultyChanceCondition(
        @JsonProperty("max_chance")
        float maxChance
) implements LootItemCondition {
    @Override
    public boolean applies(RandomSource random, LootTableContext context) {
        return maxChance * context.getLevel().getSpecialMultiplier() > random.nextFloat();
    }
}
