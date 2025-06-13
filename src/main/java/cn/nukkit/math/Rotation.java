package cn.nukkit.math;

import cn.nukkit.math.BlockFace.Axis;

public enum Rotation {
    NONE(OctahedralGroup.IDENTITY) {
        @Override
        public Rotation getRotated(Rotation rotation) {
            return rotation;
        }

        @Override
        protected BlockFace rotateHorizontal(BlockFace direction) {
            return direction;
        }

        @Override
        public Axis rotate(Axis axis) {
            return axis;
        }

        @Override
        public float rotate(float degrees) {
            return degrees;
        }

        @Override
        public int rotate(int direction) {
            return direction;
        }
    },
    CLOCKWISE_90(OctahedralGroup.ROT_90_Y_NEG) {
        @Override
        public Rotation getRotated(Rotation rotation) {
            return switch (rotation) {
                case NONE -> this;
                case CLOCKWISE_90 -> CLOCKWISE_180;
                case CLOCKWISE_180 -> COUNTERCLOCKWISE_90;
                case COUNTERCLOCKWISE_90 -> NONE;
            };
        }

        @Override
        protected BlockFace rotateHorizontal(BlockFace direction) {
            return direction.rotateY();
        }

        @Override
        public Axis rotate(Axis axis) {
            return switch (axis) {
                case Y -> axis;
                case X -> Axis.Z;
                case Z -> Axis.X;
            };
        }

        @Override
        public float rotate(float degrees) {
            return Mth.wrapDegrees(degrees + 90);
        }

        @Override
        public int rotate(int direction) {
            return (direction + 16 / 4) % 16;
        }
    },
    CLOCKWISE_180(OctahedralGroup.ROT_180_FACE_XZ) {
        @Override
        public Rotation getRotated(Rotation rotation) {
            return switch (rotation) {
                case NONE -> this;
                case CLOCKWISE_90 -> COUNTERCLOCKWISE_90;
                case CLOCKWISE_180 -> NONE;
                case COUNTERCLOCKWISE_90 -> CLOCKWISE_90;
            };
        }

        @Override
        protected BlockFace rotateHorizontal(BlockFace direction) {
            return direction.getOpposite();
        }

        @Override
        public Axis rotate(Axis axis) {
            return axis;
        }

        @Override
        public float rotate(float degrees) {
            return Mth.wrapDegrees(degrees + 180);
        }

        @Override
        public int rotate(int direction) {
            return (direction + 16 / 2) % 16;
        }
    },
    COUNTERCLOCKWISE_90(OctahedralGroup.ROT_90_Y_POS) {
        @Override
        public Rotation getRotated(Rotation rotation) {
            return switch (rotation) {
                case NONE -> this;
                case CLOCKWISE_90 -> NONE;
                case CLOCKWISE_180 -> CLOCKWISE_90;
                case COUNTERCLOCKWISE_90 -> CLOCKWISE_180;
            };
        }

        @Override
        protected BlockFace rotateHorizontal(BlockFace direction) {
            return direction.rotateYCCW();
        }

        @Override
        public Axis rotate(Axis axis) {
            return switch (axis) {
                case Y -> axis;
                case X -> Axis.Z;
                case Z -> Axis.X;
            };
        }

        @Override
        public float rotate(float degrees) {
            return Mth.wrapDegrees(degrees - 90);
        }

        @Override
        public int rotate(int direction) {
            return (direction + 16 * 3 / 4) % 16;
        }
    };

    private static final Rotation[] VALUES = values();

    private final OctahedralGroup rotation;

    Rotation(OctahedralGroup rotation) {
        this.rotation = rotation;
    }

    public OctahedralGroup rotation() {
        return rotation;
    }

    public abstract Rotation getRotated(Rotation rotation);

    protected abstract BlockFace rotateHorizontal(BlockFace direction);

    public final BlockFace rotate(BlockFace direction) {
        if (direction.getAxis() == Axis.Y) {
            return direction;
        }
        return rotateHorizontal(direction);
    }

    public abstract Axis rotate(Axis axis);

    public abstract float rotate(float degrees);

    public abstract int rotate(int direction);

    public static Rotation[] getValues() {
        return VALUES;
    }
}
