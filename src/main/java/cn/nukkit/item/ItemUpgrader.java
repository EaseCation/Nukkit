package cn.nukkit.item;

import cn.nukkit.nbt.tag.CompoundTag;

import java.util.Objects;

public class ItemUpgrader {
    private static BedrockItemUpgrader INSTANCE;

    public static void setUpgrader(BedrockItemUpgrader upgrader) {
        Objects.requireNonNull(upgrader, "upgrader");
        INSTANCE = upgrader;
    }

    public static void upgrade(CompoundTag tag) {
        INSTANCE.upgrade(tag);
    }

    public interface BedrockItemUpgrader {
        void upgrade(CompoundTag tag);
    }
}
