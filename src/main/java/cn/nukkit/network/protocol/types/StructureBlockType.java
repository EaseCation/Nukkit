package cn.nukkit.network.protocol.types;

public enum StructureBlockType {
    DATA,
    SAVE,
    LOAD,
    CORNER,
    INVALID,
    EXPORT,
    ;

    private static final StructureBlockType[] VALUES = values();

    public static StructureBlockType[] getValues() {
        return VALUES;
    }
}
