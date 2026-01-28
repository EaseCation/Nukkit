package cn.nukkit.network.protocol.types;

public enum StructureAnimationMode {
    NONE,
    LAYER,
    BLOCKS,
    ;

    private static final StructureAnimationMode[] VALUES = values();

    public static StructureAnimationMode[] getValues() {
        return VALUES;
    }
}
