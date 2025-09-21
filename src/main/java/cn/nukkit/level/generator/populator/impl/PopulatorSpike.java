package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockBedrock;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.Mth;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.utils.Utils;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PopulatorSpike extends Populator {
    private static final LoadingCache<Long, EndSpike[]> SPIKE_CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build(new SpikeCacheLoader());

    public static EndSpike[] getSpikesForLevel(ChunkManager level) {
        return SPIKE_CACHE.getUnchecked(new NukkitRandom(level.getSeed()).nextLong() & 0xffff);
    }

    private final EndSpike spike;

    public PopulatorSpike(EndSpike spike) {
        this.spike = spike;
    }

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        if (spike.centerX >> 4 != chunkX || spike.centerZ >> 4 != chunkZ) {
            return;
        }

        int cx = spike.centerX;
        int cz = spike.centerZ;
        int height = spike.height;

        int radius = spike.radius;
        int rs = Mth.square(radius) + 1;
        for (int y = 0; y < height; y++) {
            for (int xx = -radius; xx <= radius; xx++) {
                int x = cx + xx;
                int xxs = Mth.square(xx);
                for (int zz = -radius; zz <= radius; zz++) {
                    if (xxs + Mth.square(zz) <= rs) {
                        level.setBlockAt(0, x, y, cz + zz, Block.OBSIDIAN);
                    } else if (y > 65) {
                        level.setBlockAt(0, x, y, cz + zz, Block.AIR);
                    }
                }
            }
        }

        if (spike.guarded) {
            for (int xx = -2; xx <= 2; ++xx) {
                int x = cx + xx;
                for (int zz = -2; zz <= 2; ++zz) {
                    int z = cz + zz;
                    if (Math.abs(xx) == 2 || Math.abs(zz) == 2) {
                        for (int yy = 0; yy < 3; ++yy) {
                            level.setBlockAt(0, x, height + yy, z, Block.IRON_BARS);
                        }
                    }
                    level.setBlockAt(0, x, height + 3, z, Block.IRON_BARS);
                }
            }
        }

        level.setBlockAt(0, cx, height, cz, Block.BEDROCK, BlockBedrock.INFINIBURN_BIT);
        level.setBlockAt(0, cx, height + 1, cz, Block.FIRE);
        //TODO: spawn end crystal entity
    }

    private static class SpikeCacheLoader extends CacheLoader<Long, EndSpike[]> {
        @Override
        public EndSpike[] load(Long key) {
            List<Integer> sizes = IntStream.range(0, 10).boxed().collect(Collectors.toList());
            Utils.shuffle(sizes, new NukkitRandom(key));
            EndSpike[] spikes = new EndSpike[10];
            for (int i = 0; i < 10; i++) {
                int size = sizes.get(i);
                spikes[i] = new EndSpike(
                        (int) (Mth.cos((i * Mth.PI / 10 - Mth.PI) * 2) * 42),
                        (int) (Mth.sin((i * Mth.PI / 10 - Mth.PI) * 2) * 42),
                        size / 3 + 2,
                        3 * size + 76,
                        size == 1 || size == 2);
            }
            return spikes;
        }
    }

    public record EndSpike(int centerX, int centerZ, int radius, int height, boolean guarded) {
    }
}
