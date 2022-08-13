package cn.nukkit.level;

import cn.nukkit.Difficulty;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.generator.Normal;
import cn.nukkit.math.Vector3;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Builder
@Data
public class LevelCreationOptions {
    @Default
    private long seed = ThreadLocalRandom.current().nextLong();
    @Default
    private Class<? extends Generator> generator = Normal.class;
    @Default
    private Map<String, Object> options = new Object2ObjectOpenHashMap<>();
    @Default
    private Vector3 spawnPosition = new Vector3(128, 70, 128);
    @Default
    private Difficulty difficulty = Difficulty.NORMAL;
    @Default
    private GameRules gameRules = GameRules.getDefault();
}
