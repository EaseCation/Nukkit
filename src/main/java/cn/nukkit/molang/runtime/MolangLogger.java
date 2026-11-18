package cn.nukkit.molang.runtime;

public interface MolangLogger {
    MolangLogger DEFAULT = new MolangLogger() {
    };

    default void log(float value) {
    }

    default void debugOutput(float value) {
    }
}
