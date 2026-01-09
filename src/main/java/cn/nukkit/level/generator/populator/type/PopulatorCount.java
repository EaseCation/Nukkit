package cn.nukkit.level.generator.populator.type;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.RandomSource;

/**
 * A populator that generates an object a certain amount of times.
 * This prevents the exact same code from being repeated in nearly every single populator
 * @author DaPorkchop_
 */
public abstract class PopulatorCount extends Populator {
    private int randomAmount;
    private int baseAmount;

    public final void setRandomAmount(int randomAmount) {
        this.randomAmount = randomAmount + 1;
    }

    public final void setBaseAmount(int baseAmount) {
        this.baseAmount = baseAmount;
    }

    @Override
    public final void populate(ChunkManager level, int chunkX, int chunkZ, RandomSource random, FullChunk chunk) {
        int count = baseAmount;
        if (randomAmount > 0) {
            count += random.nextInt(randomAmount);
        }
        for (int i = 0; i < count; i++) {
            populateCount(level, chunkX, chunkZ, random, chunk);
        }
    }

    protected abstract void populateCount(ChunkManager level, int chunkX, int chunkZ, RandomSource random, FullChunk chunk);
}
