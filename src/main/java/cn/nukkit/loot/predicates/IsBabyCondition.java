package cn.nukkit.loot.predicates;

import cn.nukkit.entity.Entity;
import cn.nukkit.loot.LootTableContext;
import cn.nukkit.math.RandomSource;

public record IsBabyCondition(
) implements LootItemCondition {
    @Override
    public boolean applies(RandomSource random, LootTableContext context) {
        Entity entity = context.getThisEntity();
        return entity != null && entity.getDataFlag(Entity.DATA_FLAG_BABY);
    }
}
