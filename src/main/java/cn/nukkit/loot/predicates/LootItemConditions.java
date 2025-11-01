package cn.nukkit.loot.predicates;

import cn.nukkit.loot.LootTableContext;
import cn.nukkit.math.RandomSource;

import javax.annotation.Nullable;

public final class LootItemConditions {
    public static boolean allApply(@Nullable LootItemCondition[] predicates, RandomSource random, LootTableContext context) {
        if (predicates == null) {
            return true;
        }
        for (LootItemCondition predicate : predicates) {
            if (!predicate.applies(random, context)) {
                return false;
            }
        }
        return true;
    }

    private LootItemConditions() {
    }
}
