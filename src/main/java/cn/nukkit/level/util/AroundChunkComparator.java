package cn.nukkit.level.util;

import java.util.Comparator;

public class AroundChunkComparator implements Comparator<IChunkPos> {
    public static final AroundChunkComparator ORIGIN = new AroundChunkComparator(0, 0);

    private final int centerChunkX;
    private final int centerChunkZ;

    private AroundChunkComparator(int centerChunkX, int centerChunkZ) {
        this.centerChunkX = centerChunkX;
        this.centerChunkZ = centerChunkZ;
    }

    public static AroundChunkComparator create(int centerChunkX, int centerChunkZ) {
        if (centerChunkX == 0 && centerChunkZ == 0) {
            return ORIGIN;
        }
        return new AroundChunkComparator(centerChunkX, centerChunkZ);
    }

    @Override
    public int compare(IChunkPos o1, IChunkPos o2) {
        return Integer.compare(distance(centerChunkX, centerChunkZ, o1.getChunkX(), o1.getChunkZ()),
                distance(centerChunkX, centerChunkZ, o2.getChunkX(), o2.getChunkZ()));
    }

    static int distance(int centerX, int centerZ, int x, int z) {
        int dx = centerX - x;
        int dz = centerZ - z;
        return dx * dx + dz * dz;
    }
}
