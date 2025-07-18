package cn.nukkit.math;

import com.google.common.collect.Iterators;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.stream.Stream;

public enum BlockFace {
    DOWN(0, 1, -1, "down", AxisDirection.NEGATIVE, new BlockVector3(0, -1, 0)),
    UP(1, 0, -1, "up", AxisDirection.POSITIVE, new BlockVector3(0, 1, 0)),
    NORTH(2, 3, 2, "north", AxisDirection.NEGATIVE, new BlockVector3(0, 0, -1)),
    SOUTH(3, 2, 0, "south", AxisDirection.POSITIVE, new BlockVector3(0, 0, 1)),
    WEST(4, 5, 1, "west", AxisDirection.NEGATIVE, new BlockVector3(-1, 0, 0)),
    EAST(5, 4, 3, "east", AxisDirection.POSITIVE, new BlockVector3(1, 0, 0));

    /**
     * All faces in D-U-N-S-W-E order
     */
    private static final BlockFace[] VALUES = new BlockFace[6];

    /**
     * All faces with horizontal axis in order S-W-N-E
     */
    private static final BlockFace[] HORIZONTALS = new BlockFace[4];

    static {
        //Circular dependency
        DOWN.axis = Axis.Y;
        UP.axis = Axis.Y;
        NORTH.axis = Axis.Z;
        SOUTH.axis = Axis.Z;
        WEST.axis = Axis.X;
        EAST.axis = Axis.X;

        for (BlockFace face : values()) {
            VALUES[face.index] = face;

            if (face.getAxis().isHorizontal()) {
                HORIZONTALS[face.horizontalIndex] = face;
            }
        }
    }

    /**
     * Ordering index for D-U-N-S-W-E
     */
    private final int index;

    /**
     * Index of the opposite BlockFace in the VALUES array
     */
    private final int opposite;

    /**
     * Ordering index for the HORIZONTALS field (S-W-N-E)
     */
    private final int horizontalIndex;

    /**
     * The name of this BlockFace (up, down, north, etc.)
     */
    private final String name;


    private Axis axis;
    private final AxisDirection axisDirection;

    /**
     * Normalized vector that points in the direction of this BlockFace
     */
    private final BlockVector3 unitVector;

    BlockFace(int index, int opposite, int horizontalIndex, String name, AxisDirection axisDirection, BlockVector3 unitVector) {
        this.index = index;
        this.opposite = opposite;
        this.horizontalIndex = horizontalIndex;
        this.name = name;
        this.axisDirection = axisDirection;
        this.unitVector = unitVector;
    }

    /**
     * Get a BlockFace by it's index (0-5). The order is D-U-N-S-W-E
     *
     * @param index BlockFace index
     * @return block face
     */
    public static BlockFace fromIndex(int index) {
        return VALUES[Math.abs(index % VALUES.length)];
    }

    public static BlockFace fromOrdinal(int ordinal) {
        return VALUES[ordinal];
    }

    /**
     * Get a BlockFace by it's horizontal index (0-3). The order is S-W-N-E
     *
     * @param index BlockFace index
     * @return block face
     */
    public static BlockFace fromHorizontalIndex(int index) {
        return HORIZONTALS[index & 0x3];
    }

    public static BlockFace fromReversedHorizontalIndex(int index) {
        return VALUES[5 - (index & 0x3)];
    }

    /**
     * Get the BlockFace corresponding to the given angle (0-360). An angle of 0 is SOUTH, an angle of 90 would be WEST
     *
     * @param angle horizontal angle
     * @return block face
     */
    public static BlockFace fromHorizontalAngle(double angle) {
        return fromHorizontalIndex(Mth.floor(angle / 90.0D + 0.5D) & 3);
    }

    public static BlockFace fromAxis(AxisDirection axisDirection, Axis axis) {
        for (BlockFace face : VALUES) {
            if (face.getAxisDirection() == axisDirection && face.getAxis() == axis) {
                return face;
            }
        }

        throw new RuntimeException("Unable to get face from axis: " + axisDirection + " " + axis);
    }

    /**
     * Choose a random BlockFace using the given Random
     *
     * @param rand random number generator
     * @return block face
     */
    public static BlockFace random(Random rand) {
        return VALUES[rand.nextInt(VALUES.length)];
    }

    public static BlockFace random() {
        return random(ThreadLocalRandom.current());
    }

    public static BlockFace random(RandomSource rand) {
        return VALUES[rand.nextBoundedInt(VALUES.length)];
    }

    /**
     * Get the index of this BlockFace (0-5). The order is D-U-N-S-W-E
     *
     * @return index
     */
    public int getIndex() {
        return index;
    }

    /**
     * Get the horizontal index of this BlockFace (0-3). The order is S-W-N-E
     *
     * @return horizontal index
     */
    public int getHorizontalIndex() {
        return horizontalIndex;
    }

    public int getReversedHorizontalIndex() {
        return 5 - index;
    }

    /**
     * Get the angle of this BlockFace (0-360)
     *
     * @return horizontal angle
     */
    public float getHorizontalAngle() {
        return (float) ((horizontalIndex & 3) * 90);
    }

    /**
     * Get the name of this BlockFace (up, down, north, etc.)
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the Axis of this BlockFace
     *
     * @return axis
     */
    public Axis getAxis() {
        return axis;
    }

    /**
     * Get the AxisDirection of this BlockFace
     *
     * @return axis direction
     */
    public AxisDirection getAxisDirection() {
        return axisDirection;
    }

    /**
     * Get the unit vector of this BlockFace
     *
     * @return vector
     */
    public BlockVector3 getUnitVector() {
        return unitVector;
    }

    /**
     * Returns an offset that addresses the block in front of this BlockFace
     *
     * @return x offset
     */
    public int getXOffset() {
        return axis == Axis.X ? axisDirection.getOffset() : 0;
    }

    /**
     * Returns an offset that addresses the block in front of this BlockFace
     *
     * @return y offset
     */
    public int getYOffset() {
        return axis == Axis.Y ? axisDirection.getOffset() : 0;
    }

    /**
     * Returns an offset that addresses the block in front of this BlockFace
     *
     * @return x offset
     */
    public int getZOffset() {
        return axis == Axis.Z ? axisDirection.getOffset() : 0;
    }

    /**
     * Get the opposite BlockFace (e.g. DOWN ==&gt; UP)
     *
     * @return block face
     */
    public BlockFace getOpposite() {
        return fromIndex(opposite);
    }

    /**
     * Rotate this BlockFace around the Y axis clockwise (NORTH =&gt; EAST =&gt; SOUTH =&gt; WEST =&gt; NORTH)
     *
     * @return block face
     */
    public BlockFace rotateY() {
        switch (this) {
            case NORTH:
                return EAST;
            case EAST:
                return SOUTH;
            case SOUTH:
                return WEST;
            case WEST:
                return NORTH;
            default:
                throw new RuntimeException("Unable to get Y-rotated face of " + this);
        }
    }

    /**
     * Rotate this BlockFace around the Y axis counter-clockwise (NORTH =&gt; WEST =&gt; SOUTH =&gt; EAST =&gt; NORTH)
     *
     * @return block face
     */
    public BlockFace rotateYCCW() {
        switch (this) {
            case NORTH:
                return WEST;
            case EAST:
                return NORTH;
            case SOUTH:
                return EAST;
            case WEST:
                return SOUTH;
            default:
                throw new RuntimeException("Unable to get counter-clockwise Y-rotated face of " + this);
        }
    }

    public boolean isHorizontal() {
        return horizontalIndex != -1;
    }

    public boolean isVertical() {
        return horizontalIndex == -1;
    }

    @Override
    public String toString() {
        return name;
    }

    public enum Axis implements Predicate<BlockFace> {
        X(1, "x"),
        Y(0, "y"),
        Z(2, "z");

        private static final Axis[] VALUES = values();
        private static final Axis[] BY_INDEX = new Axis[3];

        private final int index;
        private final String name;
        private Plane plane;

        static {
            //Circular dependency
            X.plane = Plane.HORIZONTAL;
            Y.plane = Plane.VERTICAL;
            Z.plane = Plane.HORIZONTAL;

            for (Axis axis : VALUES) {
                BY_INDEX[axis.index] = axis;
            }
        }

        Axis(int index, String name) {
            this.index = index;
            this.name = name;
        }

        public static Axis fromOrdinal(int ordinal) {
            return VALUES[ordinal];
        }

        public static Axis fromIndex(int index) {
            return BY_INDEX[index];
        }

        public int getIndex() {
            return index;
        }

        public boolean isVertical() {
            return plane == Plane.VERTICAL;
        }

        public boolean isHorizontal() {
            return plane == Plane.HORIZONTAL;
        }

        public Plane getPlane() {
            return plane;
        }

        public String getName() {
            return name;
        }

        public Axis random(RandomSource random) {
            return VALUES[random.nextBoundedInt(VALUES.length)];
        }

        public Axis random(Random random) {
            return VALUES[random.nextInt(VALUES.length)];
        }

        public Axis random() {
            return random(ThreadLocalRandom.current());
        }

        @Override
        public boolean test(BlockFace face) {
            return face != null && face.getAxis() == this;
        }

        @Override
        public String toString() {
            return name;
        }

        public static Axis[] getValues() {
            return VALUES;
        }
    }

    public enum AxisDirection {
        POSITIVE(1, "Towards positive"),
        NEGATIVE(-1, "Towards negative");

        private final int offset;
        private final String description;

        AxisDirection(int offset, String description) {
            this.offset = offset;
            this.description = description;
        }

        public int getOffset() {
            return offset;
        }

        @Override
        public String toString() {
            return description;
        }

        public AxisDirection opposite() {
            return this == POSITIVE ? NEGATIVE : POSITIVE;
        }
    }

    public enum Plane implements Predicate<BlockFace>, Iterable<BlockFace> {
        HORIZONTAL,
        VERTICAL;

        static {
            //Circular dependency
            HORIZONTAL.faces = new BlockFace[]{NORTH, EAST, SOUTH, WEST};
            HORIZONTAL.axis = new Axis[]{Axis.X, Axis.Z};
            VERTICAL.faces = new BlockFace[]{UP, DOWN};
            VERTICAL.axis = new Axis[]{Axis.Y};
        }

        private BlockFace[] faces;
        private Axis[] axis;

        public BlockFace random(RandomSource rand) {
            return faces[rand.nextBoundedInt(faces.length)];
        }

        public BlockFace random(Random rand) {
            return faces[rand.nextInt(faces.length)];
        }

        public BlockFace random() {
            return random(ThreadLocalRandom.current());
        }

        public Axis randomAxis(RandomSource random) {
            return axis[random.nextBoundedInt(axis.length)];
        }

        public Axis randomAxis(Random random) {
            return axis[random.nextInt(axis.length)];
        }

        public Axis randomAxis() {
            return randomAxis(ThreadLocalRandom.current());
        }

        @Override
        public boolean test(BlockFace face) {
            return face != null && face.getAxis().getPlane() == this;
        }

        @Override
        public Iterator<BlockFace> iterator() {
            return Iterators.forArray(faces);
        }

        public Stream<BlockFace> stream() {
            return Arrays.stream(faces);
        }

        public int length() {
            return faces.length;
        }
    }

    public static BlockFace[] getValues() {
        return VALUES;
    }
}
