package cn.nukkit.level.generator.object.mushroom;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockHugeMushroom;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.HeightRange;
import cn.nukkit.level.generator.object.BasicGenerator;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitRandom;

import static cn.nukkit.GameVersion.*;

public class BigMushroom extends BasicGenerator {

    public static final int BROWN = 0;
    public static final int RED = 1;

    /**
     * The mushroom type. 0 for brown, 1 for red.
     */
    private final int mushroomType;

    public BigMushroom(int mushroomType) {
        this.mushroomType = mushroomType;
    }

    public BigMushroom() {
        this.mushroomType = -1;
    }

    public boolean generate(ChunkManager level, NukkitRandom rand, BlockVector3 position) {
        int block = this.mushroomType;
        if (block < 0) {
            block = rand.nextBoolean() ? RED : BROWN;
        }

        Block mushroom = block == 0 ? Block.get(BlockID.BROWN_MUSHROOM_BLOCK) : Block.get(BlockID.RED_MUSHROOM_BLOCK);
        Block stem = V1_21_40.isAvailable() ? Block.get(BlockID.MUSHROOM_STEM, BlockHugeMushroom.STEM) : mushroom;

        int i = rand.nextBoundedInt(3) + 4;

        if (rand.nextBoundedInt(12) == 0) {
            i *= 2;
        }

        boolean flag = true;

        HeightRange heightRange = level.getHeightRange();
        if (position.getY() > heightRange.getMinY() && position.getY() + i + 1 < heightRange.getMaxY()) {
            for (int y = position.getY(); y <= position.getY() + 1 + i; ++y) {
                int k = 3;

                if (y <= position.getY() + 3) {
                    k = 0;
                }

                BlockVector3 pos = new BlockVector3();

                for (int l = position.getX() - k; l <= position.getX() + k && flag; ++l) {
                    for (int i1 = position.getZ() - k; i1 <= position.getZ() + k && flag; ++i1) {
                        if (y >= heightRange.getMinY() && y < heightRange.getMaxY()) {
                            pos.setComponents(l, y, i1);
                            int material = level.getBlockIdAt(0, pos.getX(), pos.getY(), pos.getZ());

                            if (material != Block.AIR && material != Block.LEAVES) {
                                flag = false;
                            }
                        } else {
                            flag = false;
                        }
                    }
                }
            }

            if (!flag) {
                return false;
            } else {
                BlockVector3 pos2 = position.down();
                int block1 = level.getBlockIdAt(0, pos2.getX(), pos2.getY(), pos2.getZ());

                if (block1 != Block.DIRT && block1 != Block.GRASS_BLOCK && block1 != Block.MYCELIUM) {
                    return false;
                } else {
                    int k2 = position.getY() + i;

                    if (block == RED) {
                        k2 = position.getY() + i - 3;
                    }

                    for (int l2 = k2; l2 <= position.getY() + i; ++l2) {
                        int j3 = 1;

                        if (l2 < position.getY() + i) {
                            ++j3;
                        }

                        if (block == BROWN) {
                            j3 = 3;
                        }

                        int k3 = position.getX() - j3;
                        int l3 = position.getX() + j3;
                        int j1 = position.getZ() - j3;
                        int k1 = position.getZ() + j3;

                        for (int l1 = k3; l1 <= l3; ++l1) {
                            for (int i2 = j1; i2 <= k1; ++i2) {
                                int j2 = 5;

                                if (l1 == k3) {
                                    --j2;
                                } else if (l1 == l3) {
                                    ++j2;
                                }

                                if (i2 == j1) {
                                    j2 -= 3;
                                } else if (i2 == k1) {
                                    j2 += 3;
                                }

                                int meta = j2;

                                if (block == BROWN || l2 < position.getY() + i) {
                                    if ((l1 == k3 || l1 == l3) && (i2 == j1 || i2 == k1)) {
                                        continue;
                                    }

                                    if (l1 == position.getX() - (j3 - 1) && i2 == j1) {
                                        meta = BlockHugeMushroom.NORTH_WEST;
                                    }

                                    if (l1 == k3 && i2 == position.getZ() - (j3 - 1)) {
                                        meta = BlockHugeMushroom.NORTH_WEST;
                                    }

                                    if (l1 == position.getX() + (j3 - 1) && i2 == j1) {
                                        meta = BlockHugeMushroom.NORTH_EAST;
                                    }

                                    if (l1 == l3 && i2 == position.getZ() - (j3 - 1)) {
                                        meta = BlockHugeMushroom.NORTH_EAST;
                                    }

                                    if (l1 == position.getX() - (j3 - 1) && i2 == k1) {
                                        meta = BlockHugeMushroom.SOUTH_WEST;
                                    }

                                    if (l1 == k3 && i2 == position.getZ() + (j3 - 1)) {
                                        meta = BlockHugeMushroom.SOUTH_WEST;
                                    }

                                    if (l1 == position.getX() + (j3 - 1) && i2 == k1) {
                                        meta = BlockHugeMushroom.SOUTH_EAST;
                                    }

                                    if (l1 == l3 && i2 == position.getZ() + (j3 - 1)) {
                                        meta = BlockHugeMushroom.SOUTH_EAST;
                                    }
                                }

                                if (meta == BlockHugeMushroom.CENTER && l2 < position.getY() + i) {
                                    meta = BlockHugeMushroom.ALL_INSIDE;
                                }

                                if (position.getY() >= position.getY() + i - 1 || meta != BlockHugeMushroom.ALL_INSIDE) {
                                    BlockVector3 blockPos = new BlockVector3(l1, l2, i2);

                                    if (!Block.solid[level.getBlockIdAt(0, blockPos.getX(), blockPos.getY(), blockPos.getZ())]) {
                                        mushroom.setDamage(meta);
                                        this.setBlockAndNotifyAdequately(level, blockPos, mushroom);
                                    }
                                }
                            }
                        }
                    }

                    for (int i3 = 0; i3 < i; ++i3) {
                        BlockVector3 pos = position.up(i3);
                        int id = level.getBlockIdAt(0, pos.getX(), pos.getY(), pos.getZ());

                        if (!Block.solid[id]) {
                            stem.setDamage(BlockHugeMushroom.STEM);
                            this.setBlockAndNotifyAdequately(level, pos, stem);
                        }
                    }

                    return true;
                }
            }
        } else {
            return false;
        }
    }
}
