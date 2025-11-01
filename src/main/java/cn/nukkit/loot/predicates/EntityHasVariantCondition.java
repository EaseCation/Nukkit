package cn.nukkit.loot.predicates;

import cn.nukkit.entity.Entity;
import cn.nukkit.loot.LootTableContext;
import cn.nukkit.math.RandomSource;

public record EntityHasVariantCondition(
        int value
) implements LootItemCondition {
    @Override
    public boolean applies(RandomSource random, LootTableContext context) {
        Entity entity = context.getThisEntity();
        return entity != null && entity.getDataPropertyInt(Entity.DATA_VARIANT) == value;
    }
}
