package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.RandomSource;

public class ObjectCherryTree extends ObjectTree {
    protected float beehiveProbability;

    public ObjectCherryTree() {
        this(0);
    }

    public ObjectCherryTree(float beehiveProbability) {
        this.beehiveProbability = beehiveProbability;
    }

    @Override
    protected boolean overridable(int id) {
        switch (id) {
            case BlockID.AIR:
            case BlockID.SNOW_LAYER:
            case BlockID.CHERRY_LOG:
            case BlockID.CHERRY_LEAVES:
                return true;
            default:
                return false;
        }
    }

    @Override
    public int getTrunkBlock() {
        return BlockID.CHERRY_LOG;
    }

    @Override
    public int getLeafBlock() {
        return BlockID.CHERRY_LEAVES;
    }

    @Override
    public void placeObject(ChunkManager level, int x, int y, int z, RandomSource random) {
        int treeHeight = random.nextBoundedInt(2) + 9;

        int i2 = y + treeHeight;
        int maxBlockY = level.getHeightRange().getMaxY();

        if (i2 + 2 >= maxBlockY) {
            return;
        }

        int x0 = x;
        int z0 = z;
        int lowestLeaves = i2;
        int lowestLog = y;

        level.setBlockAt(0, x, y, z, BlockID.AIR);

        for (int il = 0; il < treeHeight + 1; il++) {
            placeLogAt(level, x, il + y, z);
        }

        y++;

        for (int yy = y - 3 + treeHeight; yy <= y + treeHeight; ++yy) {
            double yOff = yy - (y + treeHeight);
            int mid = (int) (1 - yOff / 2) + 1;
            for (int xx = x - mid; xx <= x + mid; ++xx) {
                int xOff = Math.abs(xx - x);
                for (int zz = z - mid; zz <= z + mid; ++zz) {
                    int zOff = Math.abs(zz - z);
                    if (xOff == mid && zOff == mid && (yOff == 0 || random.nextBoundedInt(2) == 0)) {
                        continue;
                    }
                    int posY = yy - 1;
                    if (!Block.solid[level.getBlockIdAt(0, xx, posY, zz)]) {
                        level.setBlockAt(0, xx, posY, zz, this.getLeafBlock(), this.getType());
                        lowestLeaves = Math.min(lowestLeaves, posY);
                    }
                }
            }
        }

        level.setBlockAt(0, x, i2, z, this.getLeafBlock(), this.getType());

        y--;

        int h = treeHeight >> 1;

        switch (random.nextInt(0, 4)) {
            case 0:
                for (int xp = 0; xp < h + 3; xp++) {
                    x++;
                    level.setBlockAt(0, x, y + h, z, this.getTrunkBlock(), 1);
                }
                break;
            case 1:
                for (int xp = 0; xp < h + 3; xp++) {
                    x--;
                    level.setBlockAt(0, x, y + h, z, this.getTrunkBlock(), 1);
                }
                break;
            case 2:
                for (int xp = 0; xp < h + 3; xp++) {
                    z++;
                    level.setBlockAt(0, x, y + h, z, this.getTrunkBlock(), 2);
                }
                break;
            case 3:
                for (int xp = 0; xp < h + 3; xp++) {
                    z--;
                    level.setBlockAt(0, x, y + h, z, this.getTrunkBlock(), 2);
                }
                break;
        }

        for (int il = h + 1; il < treeHeight + 1; il++) {
            placeLogAt(level, x, il + y, z);
        }

        y++;

        for (int yy = y - 3 + treeHeight; yy <= y + treeHeight; ++yy) {
            double yOff = yy - (y + treeHeight);
            int mid = (int) (1 - yOff / 2) + 1;
            for (int xx = x - mid; xx <= x + mid; ++xx) {
                int xOff = Math.abs(xx - x);
                for (int zz = z - mid; zz <= z + mid; ++zz) {
                    int zOff = Math.abs(zz - z);
                    if (xOff == mid && zOff == mid && (yOff == 0 || random.nextBoundedInt(2) == 0)) {
                        continue;
                    }
                    int posY = yy - 1;
                    if (!Block.solid[level.getBlockIdAt(0, xx, posY, zz)]) {
                        level.setBlockAt(0, xx, posY, zz, this.getLeafBlock(), this.getType());
                        lowestLeaves = Math.min(lowestLeaves, posY);
                    }
                }
            }
        }

        level.setBlockAt(0, x, i2, z, this.getLeafBlock(), this.getType());

        if (canPlaceBeehive(random)) {
            placeBeehive(level, x0, z0, random, lowestLeaves, lowestLog);
        }
    }

    private void placeLogAt(ChunkManager level, int x, int y, int z) {
        if (overridable(level.getBlockIdAt(0, x, y, z))) {
            level.setBlockAt(0, x, y, z, this.getTrunkBlock(), 0);
        }
    }

    @Override
    protected boolean canPlaceBeehive(RandomSource random) {
        return beehiveProbability != 0 && random.nextFloat() < beehiveProbability;
    }
}
