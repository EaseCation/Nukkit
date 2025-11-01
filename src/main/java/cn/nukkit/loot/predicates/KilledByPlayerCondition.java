package cn.nukkit.loot.predicates;

import cn.nukkit.loot.LootTableContext;
import cn.nukkit.math.RandomSource;

public record KilledByPlayerCondition(
) implements LootItemCondition {
    @Override
    public boolean applies(RandomSource random, LootTableContext context) {
        return context.getKillerPlayer() != null;
    }
}
