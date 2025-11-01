package cn.nukkit.loot.providers;

import cn.nukkit.math.RandomSource;
import tools.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = ValueProviders.Deserializer.class)
public interface ValueProvider {
    ValueProvider initWithDefaultValue(int rangeMin, int rangeMax);

    int getValue(RandomSource random);

    int getValueInclusive(RandomSource random);

    boolean isInRangeInclusive(int value);

    boolean isInRangeExclusive(int value);

    static ValueProvider constant(int value) {
        return new IntConstant(value);
    }

    static ValueProvider uniform(int min, int max) {
        return IntRange.uniform(min, max);
    }

    static ValueProvider unbounded() {
        return IntRange.unbounded();
    }
}
