package cn.nukkit.loot.predicates;

import cn.nukkit.entity.Entity;
import cn.nukkit.loot.LootTableContext;
import cn.nukkit.math.RandomSource;
import com.fasterxml.jackson.annotation.JsonProperty;

public record KilledByEntityCondition(
        @JsonProperty("entity_type")
        String entityType
) implements LootItemCondition {
    @Override
    public boolean applies(RandomSource random, LootTableContext context) {
        Entity killer = context.getKillerEntity();
        if (killer == null) {
            return false;
        }
        return killer.getIdentifier().equals(entityType);
    }
}
