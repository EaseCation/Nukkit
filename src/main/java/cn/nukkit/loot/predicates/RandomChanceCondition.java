package cn.nukkit.loot.predicates;

import cn.nukkit.loot.LootTableContext;
import cn.nukkit.math.RandomSource;

public record RandomChanceCondition(
        float chance
) implements LootItemCondition {
    @Override
    public boolean applies(RandomSource random, LootTableContext context) {
        return chance > random.nextFloat();
    }
}
