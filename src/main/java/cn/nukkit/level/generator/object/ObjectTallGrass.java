package cn.nukkit.level.generator.object;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockTallGrass;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockVector3;

import java.util.concurrent.ThreadLocalRandom;

/**
 * author: ItsLucas
 * Nukkit Project
 */
public class ObjectTallGrass {
    public static void growGrass(Level level, BlockVector3 pos) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 16; i < 64; ++i) {
            int num = 0;

            int x = pos.getX();
            int y = pos.getY() + 1;
            int z = pos.getZ();

            while (true) {
                if (num >= i / 16) {
                    if (level.getBlock(x, y, z).isAir()) {
                        int randomValue = random.nextInt(8);
                        if (randomValue == 0) {
                            //TODO: biomes have specific flower types that can grow in them
                            int flowerType = random.nextInt(12);
                            if (flowerType == 11) {
                                level.setBlock(x, y, z, Block.get(Block.DANDELION));
                            } else {
                                level.setBlock(x, y, z, Block.get(Block.RED_FLOWER, flowerType));
                            }
                        } else {
                            level.setBlock(x, y, z, Block.get(Block.SHORT_GRASS, randomValue == 2 ? BlockTallGrass.TYPE_FERN : BlockTallGrass.TYPE_GRASS));
                        }
                    }

                    break;
                }

                x += random.nextInt(-1, 2);
                y += random.nextInt(-1, 2) * random.nextInt(3) / 2;
                z += random.nextInt(-1, 2);

                if (!level.getHeightRange().isValidBlockY(y)) {
                    break;
                }
                if (level.getBlock(0, x, y - 1, z).getId() != Block.GRASS_BLOCK) {
                    break;
                }

                ++num;
            }
        }
    }
}
