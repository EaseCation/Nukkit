package cn.nukkit.block.state.enumeration;

import cn.nukkit.math.BlockFace.Axis;

public enum PillarAxisState implements AxisState {
    Y("y", Axis.Y),
    X("x", Axis.X),
    Z("z", Axis.Z),
    ;

    private final String name;
    private final Axis axis;

    PillarAxisState(String name, Axis axis) {
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
