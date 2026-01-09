package cn.nukkit.block;

import cn.nukkit.Server;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockFace.Plane;
import cn.nukkit.utils.Faceable;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public abstract class BlockStem extends BlockCrops implements Faceable {
    public static final int GROWTH_MASK = 0b111;
    public static final int GROWTH_BITS = 3;
    public static final int FACING_DIRECTION_MASK = 0b111000;

    @Override
    public int getBlockDefaultMeta() {
        return 3;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (down().getId() != FARMLAND) {
                level.useBreakOn(this, true);
                return Level.BLOCK_UPDATE_NORMAL;
            }

            BlockFace facing = getBlockFace();
            if (facing.isHorizontal() && getSide(facing).getId() != getFruitBlockId()) {
                setBlockFace(BlockFace.DOWN);
                level.setBlock(this, this, true);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            Random random = ThreadLocalRandom.current();
            if (random.nextInt(2) == 0) {
                if (getGrowth() < GROWTH_MASK) {
                    Block block = clone();
                    block.setDamage(block.getDamage() + 1);
                    BlockGrowEvent event = new BlockGrowEvent(this, block);
                    Server.getInstance().getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        level.setBlock(this, event.getNewState(), true);
                    }
                    return Level.BLOCK_UPDATE_RANDOM;
                }
                int fruit = getFruitBlockId();

                BlockFace facing = getBlockFace();
                if (facing.isHorizontal() && getSide(facing).getId() == fruit) {
                    return Level.BLOCK_UPDATE_RANDOM;
                }

                BlockFace face = Plane.HORIZONTAL.random(random);
                Block block = getSide(face);
                if (!block.isAir()) {
                    return Level.BLOCK_UPDATE_RANDOM;
                }

                int below = block.down().getId();
                if (below == FARMLAND || below == GRASS_BLOCK || below == DIRT || below == COARSE_DIRT || below == PODZOL || below == MYCELIUM || below == MOSS_BLOCK || below == PALE_MOSS_BLOCK || below == DIRT_WITH_ROOTS || below == MUD || below == MUDDY_MANGROVE_ROOTS) {
                    BlockGrowEvent ev = new BlockGrowEvent(block, get(fruit));
                    Server.getInstance().getPluginManager().callEvent(ev);
                    if (!ev.isCancelled()) {
                        level.setBlock(block, ev.getNewState(), true);

                        setBlockFace(face);
                        level.setBlock(this, this, true);
                    }
                }
            }
            return Level.BLOCK_UPDATE_RANDOM;
        }
        return 0;
    }

    @Override
    public boolean isStem() {
        return true;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromIndex((getDamage() & FACING_DIRECTION_MASK) >> GROWTH_BITS);
    }

    public void setBlockFace(BlockFace face) {
        setDamage((getDamage() & ~FACING_DIRECTION_MASK) | (face.getIndex() << GROWTH_BITS));
    }

    public int getGrowth() {
        return getDamage() & GROWTH_MASK;
    }

    public void setGrowth(int growth) {
        setDamage((getDamage() & ~GROWTH_MASK) | (growth & GROWTH_MASK));
    }

    public abstract int getFruitBlockId();
}
