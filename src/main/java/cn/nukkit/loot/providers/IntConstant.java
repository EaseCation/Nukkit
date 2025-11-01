package cn.nukkit.loot.providers;

import cn.nukkit.math.RandomSource;

public record IntConstant(
        int value
) implements ValueProvider {
    @Override
    public ValueProvider initWithDefaultValue(int rangeMin, int rangeMax) {
        return this;
    }

    @Override
    public int getValue(RandomSource random) {
        return value;
    }

    @Override
    public int getValueInclusive(RandomSource random) {
        return value;
    }

    @Override
    public boolean isInRangeInclusive(int value) {
        return this.value == value;
    }

    @Override
    public boolean isInRangeExclusive(int value) {
        return false;
    }
}
