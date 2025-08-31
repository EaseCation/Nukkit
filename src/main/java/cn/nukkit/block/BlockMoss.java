package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class BlockMoss extends BlockSolid {
    public BlockMoss() {
    }

    @Override
    public int getId() {
        return MOSS_BLOCK;
    }

    @Override
    public String getName() {
        return "Moss Block";
    }

    @Override
    public float getHardness() {
        return 0.1f;
    }

    @Override
    public float getResistance() {
        return 0.5f;
    }

    @Override
    public int getToolType() {
        return BlockToolType.HOE;
    }

    @Override
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public boolean sticksToPiston() {
        return false;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (item.isFertilizer()) {
            if (!up().isAir()) {
                return false;
            }

            if (player != null && !player.isCreative()) {
                item.pop();
            }

            level.addParticle(new BoneMealParticle(this));

            int thisId = getId();
            int thisX = getFloorX();
            int thisY = getFloorY();
            int thisZ = getFloorZ();
            ThreadLocalRandom random = ThreadLocalRandom.current();
            int xRadius = random.nextInt(2, 4);
            int zRadius = random.nextInt(2, 4);
            for (int xOffset = -xRadius; xOffset <= xRadius; xOffset++) {
                int x = thisX + xOffset;
                boolean xEdge = xOffset == -xRadius || xOffset == xRadius;
                for (int zOffset = -zRadius; zOffset <= zRadius; zOffset++) {
                    boolean zEdge = zOffset == -zRadius || zOffset == zRadius;
                    if (xEdge && zEdge || (xEdge || zEdge) && random.nextInt(4) == 0) {
                        continue;
                    }
                    int z = thisZ + zOffset;
                    int y = thisY;
                    for (int i = 0; level.getBlock(x, y, z).isAir() && i < 5; i++) {
                        y--;
                    }
                    for (int i = 0; !level.getBlock(x, y, z).isAir() && i < 5; i++) {
                        y++;
                    }
                    int groundY = y - 1;

                    int id = level.getBlock(x, groundY, z).getId();
                    if (!canReplace(id) || !level.getBlock(x, y, z).isAir()) {
                        continue;
                    }
                    if (id != thisId) {
                        level.setBlock(x, groundY, z, get(thisId), true);
                    }

                    if (random.nextInt(5) < 2) {
                        continue;
                    }
                    placeVegetation(x, y, z, random);
                }
            }
            return true;
        }

        return false;
    }

    private static boolean canReplace(int id) {
        switch (id) {
            case STONE:
            case GRASS_BLOCK:
            case DIRT:
            case MYCELIUM:
            case PODZOL:
            case DIRT_WITH_ROOTS:
            case MOSS_BLOCK:
            case TUFF:
            case DEEPSLATE:
            case PALE_MOSS_BLOCK:
                return true;
        }
        return false;
    }

    protected void placeVegetation(int x, int y, int z, Random random) {
        Block block;
        Block blockAbove = null;
        int rand = random.nextInt(96);
        if (rand < 4) {
            block = Block.get(FLOWERING_AZALEA);
        } else if (rand < 11) {
            block = Block.get(AZALEA);
        } else if (rand < 21) {
            if (level.getBlock(x, y + 1, z).isAir()) {
                block = Block.get(DOUBLE_PLANT, BlockDoublePlant.TYPE_TALL_GRASS);
                blockAbove = Block.get(DOUBLE_PLANT, BlockDoublePlant.TYPE_TALL_GRASS | BlockDoublePlant.TOP_HALF_BITMASK);
            } else {
                block = Block.get(SHORT_GRASS, BlockTallGrass.TYPE_GRASS);
            }
        } else if (rand < 46) {
            block = Block.get(MOSS_CARPET);
        } else {
            block = Block.get(SHORT_GRASS, BlockTallGrass.TYPE_GRASS);
        }
        if (blockAbove != null) {
            level.setBlock(x, y + 1, z, blockAbove, true, false);
        }
        level.setBlock(x, y, z, block, true);
    }

    @Override
    public boolean isFertilizable() {
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GREEN_BLOCK_COLOR;
    }

    @Override
    public int getCompostableChance() {
        return 65;
    }

    @Override
    public boolean isDirt() {
        return true;
    }
}
