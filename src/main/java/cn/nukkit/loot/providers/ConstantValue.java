package cn.nukkit.loot.providers;

import cn.nukkit.math.RandomSource;

public record ConstantValue(
        float value
) implements NumberProvider {
    @Override
    public float getFloat(RandomSource random) {
        return value;
    }

    @Override
    public boolean matchesValue(float value) {
        return this.value == value;
    }

    @Override
    public boolean matchesValue(int value) {
        return this.value == value;
    }
}
