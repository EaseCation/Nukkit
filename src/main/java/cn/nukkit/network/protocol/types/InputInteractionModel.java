package cn.nukkit.network.protocol.types;

public enum InputInteractionModel {
    TOUCH,
    CROSSHAIR,
    CLASSIC;

    private static final InputInteractionModel[] VALUES = values();

    public static InputInteractionModel[] getValues() {
        return VALUES;
    }
}
