package cn.nukkit.loot.predicates;

import cn.nukkit.Difficulty;
import cn.nukkit.loot.LootTableContext;
import cn.nukkit.math.RandomSource;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record RandomDifficultyChanceCondition(
        @JsonProperty("default_chance")
        float defaultChance,
        float peaceful,
        float easy,
        float normal,
        float hard
) implements LootItemCondition {
    @JsonCreator
    public static RandomDifficultyChanceCondition fromJson(
            @JsonProperty("default_chance")
            Float defaultChance,
            @JsonProperty("peaceful")
            Float peaceful,
            @JsonProperty("easy")
            Float easy,
            @JsonProperty("normal")
            Float normal,
            @JsonProperty("hard")
            Float hard
    ) {
        return new RandomDifficultyChanceCondition(
                defaultChance == null ? 0f : defaultChance,
                peaceful == null ? -1f : peaceful,
                easy == null ? -1f : easy,
                normal == null ? -1f : normal,
                hard == null ? -1f : hard
        );
    }

    @Override
    public boolean applies(RandomSource random, LootTableContext context) {
        int difficulty = context.getLevel().getDifficulty();
        return switch (Difficulty.byId(difficulty)) {
            case PEACEFUL -> peaceful < 0 ? defaultChance : peaceful;
            case EASY -> easy < 0 ? defaultChance : easy;
            case NORMAL -> normal < 0 ? defaultChance : normal;
            case HARD -> hard < 0 ? defaultChance : hard;
        } > random.nextFloat();
    }
}
