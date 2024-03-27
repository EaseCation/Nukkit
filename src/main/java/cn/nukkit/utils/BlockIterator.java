package cn.nukkit.utils;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Mth;
import cn.nukkit.math.Vector3;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This class performs ray tracing and iterates along blocks on a line.
 *
 * @author MagicDroidX
 * Nukkit Project
 */
public class BlockIterator implements Iterator<Block> {
    private final int maxDistance;

    private static final int gridSize = 1 << 24;

    private boolean end = false;

    private final Block[] blockQueue;
    private int currentBlock;

    private int currentDistance;
    private final int maxDistanceInt;

    private int secondError;
    private int thirdError;

    private final int secondStep;
    private final int thirdStep;

    private BlockFace mainFace;
    private BlockFace secondFace;
    private BlockFace thirdFace;

    /**
     * Constructs the BlockIterator.
     * <p>
     * This considers all blocks as 1x1x1 in size.
     *
     * @param level The level to use for tracing
     * @param start A Vector giving the initial location for the trace
     * @param direction A Vector pointing in the direction for the trace
     * @param yOffset The trace begins vertically offset from the start vector by this value
     * @param maxDistance This is the maximum distance in blocks for the trace.
     *                    Setting this value above 140 may lead to problems with unloaded chunks.
     *                    A value of 0 indicates no limit
     *
     */
    public BlockIterator(Level level, Vector3 start, Vector3 direction, double yOffset, int maxDistance) {
        this.maxDistance = maxDistance;
        this.blockQueue = new Block[3];

        Vector3 startClone = new Vector3(start.x, start.y, start.z);
        startClone.y += yOffset;

        this.currentDistance = 0;

        double mainDirection = 0;
        double secondDirection = 0;
        double thirdDirection = 0;

        double mainPosition = 0;
        double secondPosition = 0;
        double thirdPosition = 0;

        Vector3 pos = new Vector3(startClone.x, startClone.y, startClone.z);
        Block startBlock = level.getBlock(Mth.floor(pos.x), Mth.floor(pos.y), Mth.floor(pos.z));

        if (this.getXLength(direction) > mainDirection) {
            this.mainFace = this.getXFace(direction);
            mainDirection = this.getXLength(direction);
            mainPosition = this.getXPosition(direction, startClone, startBlock);

            this.secondFace = this.getYFace(direction);
            secondDirection = this.getYLength(direction);
            secondPosition = this.getYPosition(direction, startClone, startBlock);

            this.thirdFace = this.getZFace(direction);
            thirdDirection = this.getZLength(direction);
            thirdPosition = this.getZPosition(direction, startClone, startBlock);
        }
        if (this.getYLength(direction) > mainDirection) {
            this.mainFace = this.getYFace(direction);
            mainDirection = this.getYLength(direction);
            mainPosition = this.getYPosition(direction, startClone, startBlock);

            this.secondFace = this.getZFace(direction);
            secondDirection = this.getZLength(direction);
            secondPosition = this.getZPosition(direction, startClone, startBlock);

            this.thirdFace = this.getXFace(direction);
            thirdDirection = this.getXLength(direction);
            thirdPosition = this.getXPosition(direction, startClone, startBlock);
        }
        if (this.getZLength(direction) > mainDirection) {
            this.mainFace = this.getZFace(direction);
            mainDirection = this.getZLength(direction);
            mainPosition = this.getZPosition(direction, startClone, startBlock);

            this.secondFace = this.getXFace(direction);
            secondDirection = this.getXLength(direction);
            secondPosition = this.getXPosition(direction, startClone, startBlock);

            this.thirdFace = this.getYFace(direction);
            thirdDirection = this.getYLength(direction);
            thirdPosition = this.getYPosition(direction, startClone, startBlock);
        }

        // trace line backwards to find intercept with plane perpendicular to the main axis

        double d = mainPosition / mainDirection; // how far to hit face behind
        double secondd = secondPosition - secondDirection * d;
        double thirdd = thirdPosition - thirdDirection * d;

        // Guarantee that the ray will pass though the start block.
        // It is possible that it would miss due to rounding
        // This should only move the ray by 1 grid position
        this.secondError = Mth.floor(secondd * gridSize);
        this.secondStep = (int) Math.round(secondDirection / mainDirection * gridSize);
        this.thirdError = Mth.floor(thirdd * gridSize);
        this.thirdStep = (int) Math.round(thirdDirection / mainDirection * gridSize);

        if (this.secondError + this.secondStep <= 0) {
            this.secondError = -this.secondStep + 1;
        }

        if (this.thirdError + this.thirdStep <= 0) {
            this.thirdError = -this.thirdStep + 1;
        }

        Block lastBlock = startBlock.getSide(this.mainFace.getOpposite());

        if (this.secondError < 0) {
            this.secondError += gridSize;
            lastBlock = lastBlock.getSide(this.secondFace.getOpposite());
        }

        if (this.thirdError < 0) {
            this.thirdError += gridSize;
            lastBlock = lastBlock.getSide(this.thirdFace.getOpposite());
        }

        // This means that when the variables are positive, it means that the coord=1 boundary has been crossed
        this.secondError -= gridSize;
        this.thirdError -= gridSize;

        this.blockQueue[0] = lastBlock;
        this.currentBlock = -1;

        this.scan();

        boolean startBlockFound = false;

        for (int cnt = this.currentBlock; cnt >= 0; --cnt) {
            if (this.blockEquals(this.blockQueue[cnt], startBlock)) {
                this.currentBlock = cnt;
                startBlockFound = true;
                break;
            }
        }

        if (!startBlockFound) {
            throw new IllegalStateException("Start block missed in BlockIterator");
        }

        this.maxDistanceInt = (int) Math.round(maxDistance / (Math.sqrt(mainDirection * mainDirection + secondDirection * secondDirection + thirdDirection * thirdDirection) / mainDirection));
    }

    private boolean blockEquals(Block a, Block b) {
        return a.getFloorX() == b.getFloorX() && a.getFloorY() == b.getFloorY() && a.getFloorZ() == b.getFloorZ();
    }

    private BlockFace getXFace(Vector3 direction) {
        return ((direction.x) > 0) ? BlockFace.EAST : BlockFace.WEST;
    }

    private BlockFace getYFace(Vector3 direction) {
        return ((direction.y) > 0) ? BlockFace.UP : BlockFace.DOWN;
    }

    private BlockFace getZFace(Vector3 direction) {
        return ((direction.z) > 0) ? BlockFace.SOUTH : BlockFace.NORTH;
    }

    private double getXLength(Vector3 direction) {
        return Math.abs(direction.x);
    }

    private double getYLength(Vector3 direction) {
        return Math.abs(direction.y);
    }

    private double getZLength(Vector3 direction) {
        return Math.abs(direction.z);
    }

    private double getPosition(double direction, double position, double blockPosition) {
        return direction > 0 ? (position - blockPosition) : (blockPosition + 1 - position);
    }

    private double getXPosition(Vector3 direction, Vector3 position, Block block) {
        return this.getPosition(direction.x, position.x, block.getFloorX());
    }

    private double getYPosition(Vector3 direction, Vector3 position, Block block) {
        return this.getPosition(direction.y, position.y, block.getFloorY());
    }

    private double getZPosition(Vector3 direction, Vector3 position, Block block) {
        return this.getPosition(direction.z, position.z, block.getFloorZ());
    }

    /**
     * Returns the next Block in the trace
     *
     * @return the next Block in the trace
     */
    @Override
    public Block next() throws NoSuchElementException {
        this.scan();

        if (this.currentBlock <= -1) {
            throw new NoSuchElementException();
        } else {
            return this.blockQueue[this.currentBlock--];
        }
    }

    /**
     * Returns true if the iteration has more elements
     */
    @Override
    public boolean hasNext() {
        this.scan();
        return this.currentBlock != -1;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("[BlockIterator] doesn't support block removal");
    }

    private void scan() {
        if (this.currentBlock >= 0) {
            return;
        }

        if (this.maxDistance != 0 && this.currentDistance > this.maxDistanceInt) {
            this.end = true;
            return;
        }

        if (this.end) {
            return;
        }

        ++this.currentDistance;

        this.secondError += this.secondStep;
        this.thirdError += this.thirdStep;

        if (this.secondError > 0 && this.thirdError > 0) {
            this.blockQueue[2] = this.blockQueue[0].getSide(this.mainFace);

            if ((this.secondStep * this.thirdError) < (this.thirdStep * this.secondError)) {
                this.blockQueue[1] = this.blockQueue[2].getSide(this.secondFace);
                this.blockQueue[0] = this.blockQueue[1].getSide(this.thirdFace);
            } else {
                this.blockQueue[1] = this.blockQueue[2].getSide(this.thirdFace);
                this.blockQueue[0] = this.blockQueue[1].getSide(this.secondFace);
            }

            this.thirdError -= gridSize;
            this.secondError -= gridSize;
            this.currentBlock = 2;
        } else if (this.secondError > 0) {
            this.blockQueue[1] = this.blockQueue[0].getSide(this.mainFace);
            this.blockQueue[0] = this.blockQueue[1].getSide(this.secondFace);
            this.secondError -= gridSize;
            this.currentBlock = 1;
        } else if (this.thirdError > 0) {
            this.blockQueue[1] = this.blockQueue[0].getSide(this.mainFace);
            this.blockQueue[0] = this.blockQueue[1].getSide(this.thirdFace);
            this.thirdError -= gridSize;
            this.currentBlock = 1;
        } else {
            this.blockQueue[0] = this.blockQueue[0].getSide(this.mainFace);
            this.currentBlock = 0;
        }
    }
}
