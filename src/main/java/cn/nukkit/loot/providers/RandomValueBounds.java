package cn.nukkit.loot.providers;

import cn.nukkit.math.Mth;
import cn.nukkit.math.RandomSource;
import tools.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = RandomValueBounds.class)
public record RandomValueBounds(
        float min,
        float max
) implements NumberProvider {
    @Override
    public float getFloat(RandomSource random) {
        return random.nextFloat(min, max);
    }

    @Override
    public int getInt(RandomSource random) {
        return random.nextIntInclusive(Mth.floor(min), Mth.floor(max));
    }

    @Override
    public boolean matchesValue(float value) {
        return max >= value && value >= min;
    }

    @Override
    public boolean matchesValue(int value) {
        return max >= value && value >= min;
    }
}
