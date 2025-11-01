package cn.nukkit.loot.providers;

import cn.nukkit.math.RandomSource;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = IntRange.class)
public record IntRange(
        @JsonProperty("range_min")
        int rangeMin,
        @JsonProperty("range_max")
        int rangeMax
) implements ValueProvider {
    @JsonCreator
    public static IntRange fromJson(
            @JsonProperty("range_min")
            Integer rangeMin,
            @JsonProperty("range_max")
            Integer rangeMax
    ) {
        return uniform(
                rangeMin == null ? Integer.MIN_VALUE : rangeMin,
                rangeMax == null ? Integer.MAX_VALUE : rangeMax
        );
    }

    public static IntRange uniform(
            int rangeMin,
            int rangeMax
    ) {
        if (rangeMax < rangeMin) {
            int tmp = rangeMin;
            rangeMin = rangeMax;
            rangeMax = tmp;
        }
        return new IntRange(rangeMin, rangeMax);
    }

    public static IntRange unbounded() {
        return new IntRange(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public ValueProvider initWithDefaultValue(int rangeMin, int rangeMax) {
        boolean changed = false;
        if (this.rangeMin != Integer.MIN_VALUE) {
            changed = true;
            rangeMin = this.rangeMin;
        }
        if (this.rangeMax != Integer.MAX_VALUE) {
            changed = true;
            rangeMax = this.rangeMax;
        }
        if (changed) {
            return new IntRange(rangeMin, rangeMax);
        }
        return this;
    }

    @Override
    public int getValue(RandomSource random) {
        return random.nextInt(rangeMin, rangeMax);
    }

    @Override
    public int getValueInclusive(RandomSource random) {
        return random.nextIntInclusive(rangeMin, rangeMax);
    }

    @Override
    public boolean isInRangeInclusive(int value) {
        return value >= rangeMin && value <= rangeMax;
    }

    @Override
    public boolean isInRangeExclusive(int value) {
        return value > rangeMin && value < rangeMax;
    }
}
