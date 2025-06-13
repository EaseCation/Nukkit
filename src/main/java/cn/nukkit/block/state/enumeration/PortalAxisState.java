package cn.nukkit.block.state.enumeration;

import cn.nukkit.math.BlockFace.Axis;

public enum PortalAxisState implements AxisState {
    UNKNOWN("unknown", Axis.Y),
    X("x", Axis.X),
    Z("z", Axis.Z),
    ;

    private final String name;
    private final Axis axis;

    PortalAxisState(String name, Axis axis) {
        this.name = name;
        this.axis = axis;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Axis getAxis() {
        return axis;
    }
}
