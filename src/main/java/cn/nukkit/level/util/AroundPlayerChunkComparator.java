package cn.nukkit.level.util;

import cn.nukkit.Player;
import cn.nukkit.level.Level;
import it.unimi.dsi.fastutil.longs.LongComparator;

public class AroundPlayerChunkComparator implements LongComparator {

    private final Player player;

    public AroundPlayerChunkComparator(Player player) {
        this.player = player;
    }

    @Override
    public int compare(long o1, long o2) {
        int x1 = Level.getHashX(o1);
        int z1 = Level.getHashZ(o1);
        int x2 = Level.getHashX(o2);
        int z2 = Level.getHashZ(o2);

        int spawnX = this.player.getChunkX();
        int spawnZ = this.player.getChunkZ();

        return Integer.compare(distance(spawnX, spawnZ, x1, z1), distance(spawnX, spawnZ, x2, z2));
    }

    private static int distance(int centerX, int centerZ, int x, int z) {
        int dx = centerX - x;
        int dz = centerZ - z;
        return dx * dx + dz * dz;
    }
}
