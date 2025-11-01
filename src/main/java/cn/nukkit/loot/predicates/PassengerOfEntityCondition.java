package cn.nukkit.loot.predicates;

import cn.nukkit.entity.Entity;
import cn.nukkit.loot.LootTableContext;
import cn.nukkit.math.RandomSource;
import com.fasterxml.jackson.annotation.JsonProperty;

public record PassengerOfEntityCondition(
        @JsonProperty("entity_type")
        String entityType
) implements LootItemCondition {
    @Override
    public boolean applies(RandomSource random, LootTableContext context) {
        Entity entity = context.getThisEntity();
        if (entity == null) {
            return false;
        }
        Entity vehicle = entity.getRiding();
        if (vehicle == null) {
            return false;
        }
        return vehicle.getIdentifier().equals(entityType);
    }
}
