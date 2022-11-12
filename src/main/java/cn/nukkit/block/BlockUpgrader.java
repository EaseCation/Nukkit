package cn.nukkit.block;

import cn.nukkit.nbt.tag.CompoundTag;

import java.util.Objects;

public final class BlockUpgrader {
    private static BedrockBlockUpgrader INSTANCE;

    public static void setUpgrader(BedrockBlockUpgrader upgrader) {
        Objects.requireNonNull(upgrader, "upgrader");
        INSTANCE = upgrader;
    }

    public static void upgrade(CompoundTag tag) {
        INSTANCE.upgrade(tag);
    }

    public static int getCurrentVersion() {
        return INSTANCE.getCurrentVersion();
    }

    public interface BedrockBlockUpgrader {
        void upgrade(CompoundTag tag);

        int getCurrentVersion();
    }
}
