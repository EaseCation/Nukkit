package cn.nukkit.math;

import cn.nukkit.math.BlockFace.Axis;

public enum Mirror {
    NONE(OctahedralGroup.IDENTITY) {
        @Override
        public Rotation getRotation(BlockFace direction) {
            return Rotation.NONE;
        }

        @Override
        public BlockFace mirror(BlockFace direction) {
            return direction;
        }

        @Override
        public float mirror(float degrees) {
            return degrees;
        }

        @Override
        public int mirror(int direction) {
            return direction;
        }
    },
    X(OctahedralGroup.INVERT_Z) {
        @Override
        public Rotation getRotation(BlockFace direction) {
            return direction.getAxis() != Axis.Z ? Rotation.NONE : Rotation.CLOCKWISE_180;
        }

        @Override
        public BlockFace mirror(BlockFace direction) {
            return direction.getAxis() == Axis.Z ? direction.getOpposite() : direction;
        }

        @Override
        public float mirror(float degrees) {
            return Mth.wrapDegrees(-degrees);
        }

        @Override
        public int mirror(int direction) {
            return (8 - (direction > 8 ? direction - 16 : direction) + 16) % 16;
        }
    },
    Z(OctahedralGroup.INVERT_X) {
        @Override
        public Rotation getRotation(BlockFace direction) {
            return direction.getAxis() != Axis.X ? Rotation.NONE : Rotation.CLOCKWISE_180;
        }

        @Override
        public BlockFace mirror(BlockFace direction) {
            return direction.getAxis() == Axis.X ? direction.getOpposite() : direction;
        }

        @Override
        public float mirror(float degrees) {
            return Mth.wrapDegrees(180 - degrees);
        }

        @Override
        public int mirror(int direction) {
            return (16 - (direction > 8 ? direction - 16 : direction)) % 16;
        }
    },
    /*XZ(OctahedralGroup.ROT_180_FACE_XZ) {
        @Override
        public Rotation getRotation(BlockFace direction) {
            return Rotation.CLOCKWISE_180;
        }

        @Override
        public BlockFace mirror(BlockFace direction) {
            return Rotation.CLOCKWISE_180.rotate(direction);
        }

        @Override
        public float mirror(float degrees) {
            return Rotation.CLOCKWISE_180.rotate(degrees);
        }

        @Override
        public int mirror(int direction) {
            return Rotation.CLOCKWISE_180.rotate(direction);
        }
    }*/;

    private static final Mirror[] VALUES = values();

    private final OctahedralGroup rotation;

    Mirror(OctahedralGroup rotation) {
        this.rotation = rotation;
    }

    public OctahedralGroup rotation() {
        return rotation;
    }

    public abstract Rotation getRotation(BlockFace direction);

    public abstract BlockFace mirror(BlockFace direction);

    public abstract float mirror(float degrees);

    public abstract int mirror(int direction);

    public static Mirror[] getValues() {
        return VALUES;
    }
}
