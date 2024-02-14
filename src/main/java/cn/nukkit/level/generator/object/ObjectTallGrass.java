package cn.nukkit.level.generator.object;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitRandom;

/**
 * author: ItsLucas
 * Nukkit Project
 */
public class ObjectTallGrass {
    public static void growGrass(ChunkManager level, BlockVector3 pos, NukkitRandom random) {
        for (int i = 0; i < 128; ++i) {
            int num = 0;

            int x = pos.getX();
            int y = pos.getY() + 1;
            int z = pos.getZ();

            while (true) {
                if (num >= i / 16) {
                    if (level.getBlockIdAt(0, x, y, z) == Block.AIR) {
                        if (random.nextBoundedInt(8) == 0) {
                            //porktodo: biomes have specific flower types that can grow in them
                            if (random.nextBoolean()) {
                                level.setBlockAt(0, x, y, z, Block.YELLOW_FLOWER);
                            } else {
                                level.setBlockAt(0, x, y, z, Block.RED_FLOWER);
                            }
                        } else {
                            level.setBlockAt(0, x, y, z, Block.TALLGRASS, 1);
                        }
                    }

                    break;
                }

                x += random.nextRange(-1, 1);
                y += random.nextRange(-1, 1) * random.nextBoundedInt(3) / 2;
                z += random.nextRange(-1, 1);

                if (level.getBlockIdAt(0, x, y - 1, z) != Block.GRASS || !level.getHeightRange().isValidBlockY(y)) {
                    break;
                }

                ++num;
            }
        }
    }
}
