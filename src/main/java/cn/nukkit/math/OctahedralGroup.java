package cn.nukkit.math;

import cn.nukkit.math.BlockFace.Axis;
import cn.nukkit.math.BlockFace.AxisDirection;

import java.util.EnumMap;
import java.util.Map;

public enum OctahedralGroup {
    IDENTITY(SymmetricGroup3.P123, false, false, false),
    ROT_180_FACE_XY(SymmetricGroup3.P123, true, true, false),
    ROT_180_FACE_XZ(SymmetricGroup3.P123, true, false, true),
    ROT_180_FACE_YZ(SymmetricGroup3.P123, false, true, true),
    ROT_120_NNN(SymmetricGroup3.P231, false, false, false),
    ROT_120_NNP(SymmetricGroup3.P312, true, false, true),
    ROT_120_NPN(SymmetricGroup3.P312, false, true, true),
    ROT_120_NPP(SymmetricGroup3.P231, true, false, true),
    ROT_120_PNN(SymmetricGroup3.P312, true, true, false),
    ROT_120_PNP(SymmetricGroup3.P231, true, true, false),
    ROT_120_PPN(SymmetricGroup3.P231, false, true, true),
    ROT_120_PPP(SymmetricGroup3.P312, false, false, false),
    ROT_180_EDGE_XY_NEG(SymmetricGroup3.P213, true, true, true),
    ROT_180_EDGE_XY_POS(SymmetricGroup3.P213, false, false, true),
    ROT_180_EDGE_XZ_NEG(SymmetricGroup3.P321, true, true, true),
    ROT_180_EDGE_XZ_POS(SymmetricGroup3.P321, false, true, false),
    ROT_180_EDGE_YZ_NEG(SymmetricGroup3.P132, true, true, true),
    ROT_180_EDGE_YZ_POS(SymmetricGroup3.P132, true, false, false),
    ROT_90_X_NEG(SymmetricGroup3.P132, false, false, true),
    ROT_90_X_POS(SymmetricGroup3.P132, false, true, false),
    ROT_90_Y_NEG(SymmetricGroup3.P321, true, false, false),
    ROT_90_Y_POS(SymmetricGroup3.P321, false, false, true),
    ROT_90_Z_NEG(SymmetricGroup3.P213, false, true, false),
    ROT_90_Z_POS(SymmetricGroup3.P213, true, false, false),
    INVERSION(SymmetricGroup3.P123, true, true, true),
    INVERT_X(SymmetricGroup3.P123, true, false, false),
    INVERT_Y(SymmetricGroup3.P123, false, true, false),
    INVERT_Z(SymmetricGroup3.P123, false, false, true),
    ROT_60_REF_NNN(SymmetricGroup3.P312, true, true, true),
    ROT_60_REF_NNP(SymmetricGroup3.P231, true, false, false),
    ROT_60_REF_NPN(SymmetricGroup3.P231, false, false, true),
    ROT_60_REF_NPP(SymmetricGroup3.P312, false, false, true),
    ROT_60_REF_PNN(SymmetricGroup3.P231, false, true, false),
    ROT_60_REF_PNP(SymmetricGroup3.P312, true, false, false),
    ROT_60_REF_PPN(SymmetricGroup3.P312, false, true, false),
    ROT_60_REF_PPP(SymmetricGroup3.P231, true, true, true),
    SWAP_XY(SymmetricGroup3.P213, false, false, false),
    SWAP_YZ(SymmetricGroup3.P132, false, false, false),
    SWAP_XZ(SymmetricGroup3.P321, false, false, false),
    SWAP_NEG_XY(SymmetricGroup3.P213, true, true, false),
    SWAP_NEG_YZ(SymmetricGroup3.P132, false, true, true),
    SWAP_NEG_XZ(SymmetricGroup3.P321, true, false, true),
    ROT_90_REF_X_NEG(SymmetricGroup3.P132, true, false, true),
    ROT_90_REF_X_POS(SymmetricGroup3.P132, true, true, false),
    ROT_90_REF_Y_NEG(SymmetricGroup3.P321, true, true, false),
    ROT_90_REF_Y_POS(SymmetricGroup3.P321, false, true, true),
    ROT_90_REF_Z_NEG(SymmetricGroup3.P213, false, true, true),
    ROT_90_REF_Z_POS(SymmetricGroup3.P213, true, false, true);

    private static final OctahedralGroup[] VALUES = values();

    private final Map<BlockFace, BlockFace> rotatedDirections = new EnumMap<>(BlockFace.class);
    private final boolean invertX;
    private final boolean invertY;
    private final boolean invertZ;

    OctahedralGroup(SymmetricGroup3 symmetric, boolean invertX, boolean invertY, boolean invertZ) {
        this.invertX = invertX;
        this.invertY = invertY;
        this.invertZ = invertZ;

        for (BlockFace direction : BlockFace.getValues()) {
            AxisDirection axisDirection = direction.getAxisDirection();
            Axis axis = Axis.getValues()[symmetric.permutation(direction.getAxis().ordinal())];
            rotatedDirections.put(direction, BlockFace.fromAxis(inverts(axis) ?
                    axisDirection == AxisDirection.POSITIVE ? AxisDirection.NEGATIVE : AxisDirection.POSITIVE
                    : axisDirection, axis));
        }
    }

    public boolean inverts(Axis axis) {
        return switch (axis) {
            case X -> invertX;
            case Y -> invertY;
            case Z -> invertZ;
        };
    }

    public BlockFace rotate(BlockFace direction) {
        return rotatedDirections.get(direction);
    }

    public Orientation rotate(Orientation orientation) {
        return Orientation.fromFrontAndTop(rotate(orientation.getFront()), rotate(orientation.getTop()));
    }

    public static OctahedralGroup[] getValues() {
        return VALUES;
    }
}
