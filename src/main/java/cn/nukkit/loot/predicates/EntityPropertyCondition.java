package cn.nukkit.loot.predicates;

import cn.nukkit.entity.Entity;
import cn.nukkit.loot.LootTableContext;
import cn.nukkit.loot.LootTableContext.EntityTarget;
import cn.nukkit.math.RandomSource;
import com.fasterxml.jackson.annotation.JsonProperty;

public record EntityPropertyCondition(
        EntityTarget entity,
        ActorProperties properties
) implements LootItemCondition {
    @Override
    public boolean applies(RandomSource random, LootTableContext context) {
        Entity targetEntity = context.getEntity(entity);
        if (targetEntity == null) {
            return false;
        }
        if (properties == null) {
            return true;
        }
        if (properties.onFire != null && properties.onFire != targetEntity.isOnFire()) {
            return false;
        }
        return true;
    }

    public record ActorProperties(
            @JsonProperty("on_fire")
            Boolean onFire
    ) {
    }
}
