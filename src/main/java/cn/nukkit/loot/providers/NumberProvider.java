package cn.nukkit.loot.providers;

import cn.nukkit.math.Mth;
import cn.nukkit.math.RandomSource;
import tools.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = NumberProviders.Deserializer.class)
public interface NumberProvider {
    float getFloat(RandomSource random);

    default int getInt(RandomSource random) {
        return Mth.floor(getFloat(random));
    }

    boolean matchesValue(float value);

    boolean matchesValue(int value);

    static NumberProvider constant(float value) {
        return new ConstantValue(value);
    }

    static NumberProvider uniform(float min, float max) {
        return new RandomValueBounds(min, max);
    }

    static NumberProvider unbounded() {
        return new RandomValueBounds(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }
}
