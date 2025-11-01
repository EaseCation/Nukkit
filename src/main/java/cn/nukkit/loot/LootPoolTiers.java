package cn.nukkit.loot;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LootPoolTiers(
        @JsonProperty("initial_range")
        int initialRange,
        @JsonProperty("bonus_rolls")
        int bonusRolls,
        @JsonProperty("bonus_chance")
        int bonusChance
) {
}
