package cn.nukkit.network.protocol.types;

public enum StructureMirror {
    NONE,
    X,
    Z,
    XZ,
    ;

    private static final StructureMirror[] VALUES = values();

    public static StructureMirror[] getValues() {
        return VALUES;
    }
}
