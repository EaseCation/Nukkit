package cn.nukkit.level.biome.impl.end;

import cn.nukkit.level.biome.Biome;

public class EndBiome extends Biome {
    @Override
    public String getName() {
        return "End";
    }

    @Override
    public boolean canRain() {
        return false;
    }
}
