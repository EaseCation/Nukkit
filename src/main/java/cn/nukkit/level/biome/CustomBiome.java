package cn.nukkit.level.biome;

import java.awt.*;
import java.util.Collections;
import java.util.List;

public abstract class CustomBiome extends Biome {
    private static final Color DEFAULT_WATER_COLOR = new Color(0xA660B7FF, true);

    @Override
    public String getName() {
        return getFullIdentifier();
    }

    @Override
    public final boolean isVanilla() {
        return false;
    }

    public float getTemperature() {
        return 0.5f;
    }

    public float getDownfall() {
        return 0.5f;
    }

    public float getMinSnowAccumulation() {
        return 0;
    }

    public float getMaxSnowAccumulation() {
        return 0.125f;
    }

    /**
     * @return density of ash precipitation visuals
     */
    public float getAsh() {
        return 0;
    }

    /**
     * @return density of white ash precipitation visuals
     */
    public float getWhiteAsh() {
        return 0;
    }

    /**
     * @return density of blue spore precipitation visuals
     */
    public float getBlueSpores() {
        return 0;
    }

    /**
     * @return density of blue spore precipitation visuals
     */
    public float getRedSpores() {
        return 0;
    }

    public Color getWaterColor() {
        return DEFAULT_WATER_COLOR;
    }

    public List<String> getTags() {
        return Collections.emptyList();
    }
}
