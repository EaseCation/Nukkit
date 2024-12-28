package cn.nukkit.block.state.enumeration;

public enum AttachmentState {
    STANDING("standing"),
    HANGING("hanging"),
    SIDE("side"),
    MULTIPLE("multiple"),
    ;

    private final String name;

    AttachmentState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
