package cn.nukkit.loot.predicates;

import cn.nukkit.loot.LootTableContext;
import cn.nukkit.math.RandomSource;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "condition")
@JsonSubTypes({
        @JsonSubTypes.Type(value = KilledByPlayerCondition.class, name = "killed_by_player"),
        @JsonSubTypes.Type(value = KilledByPlayerOrPetsCondition.class, name = "killed_by_player_or_pets"),
        @JsonSubTypes.Type(value = KilledByEntityCondition.class, name = "killed_by_entity"),
        @JsonSubTypes.Type(value = EntityKilledCondition.class, name = "entity_killed"),
        @JsonSubTypes.Type(value = RandomChanceCondition.class, name = "random_chance"),
        @JsonSubTypes.Type(value = RandomDifficultyChanceCondition.class, name = "random_difficulty_chance"),
        @JsonSubTypes.Type(value = RandomChanceWithLootingCondition.class, name = "random_chance_with_looting"),
        @JsonSubTypes.Type(value = RandomRegionalDifficultyChanceCondition.class, name = "random_regional_difficulty_chance"),
        @JsonSubTypes.Type(value = EntityHasMarkVariantCondition.class, name = "has_mark_variant"),
        @JsonSubTypes.Type(value = EntityHasVariantCondition.class, name = "has_variant"),
        @JsonSubTypes.Type(value = MatchToolCondition.class, name = "match_tool"),
        @JsonSubTypes.Type(value = EntityPropertyCondition.class, name = "entity_properties"),
        @JsonSubTypes.Type(value = DamagedByEntityCondition.class, name = "damaged_by_entity"),
        @JsonSubTypes.Type(value = IsBabyCondition.class, name = "is_baby"),
        @JsonSubTypes.Type(value = PassengerOfEntityCondition.class, name = "passenger_of_entity"),
})
@FunctionalInterface
public interface LootItemCondition {
    boolean applies(RandomSource random, LootTableContext context);
}
