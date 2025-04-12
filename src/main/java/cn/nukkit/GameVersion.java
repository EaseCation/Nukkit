package cn.nukkit;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;

public enum GameVersion {
    V1_4_0(261, "1.4.0", "1.4"),
    V1_5_0(270, "1.5.0", "1.5"),
    V1_6_0(282, "1.6.0", "1.6"),
    V1_7_0(290, "1.7.0", "1.7"),
    V1_8_0(312, "1.8.0", "1.8"),
    V1_9_0(332, "1.9.0", "1.9"),
    V1_10_0(340, "1.10.0", "1.10"),
    V1_11_0(354, "1.11.0", "1.11"),
    V1_12_0(361, "1.12.0", "1.12"),
    V1_13_0(388, "1.13.0", "1.13"),
    V1_14_0(389, "1.14.0", "1.14"),
    V1_14_60(390, "1.14.60"),
    V1_16_0(407, "1.16.0", "1.16"),
    V1_16_20(408, "1.16.20"),
    V1_16_100(419, "1.16.100"),
    V1_16_200(422, "1.16.200"),
    V1_16_210(428, "1.16.210"),
    V1_16_220(431, "1.16.220"),
    V1_17_0(440, "1.17.0", "1.17"),
    V1_17_10(448, "1.17.10"),
    V1_17_30(465, "1.17.30"),
    V1_17_40(471, "1.17.40"),
    V1_18_0(475, "1.18.0", "1.18"),
    V1_18_10(486, "1.18.10"),
    V1_18_30(503, "1.18.30"),
    V1_19_0(527, "1.19.0", "1.19"),
    V1_19_10(534, "1.19.10"),
    V1_19_20(544, "1.19.20"),
    V1_19_21(545, "1.19.21"),
    V1_19_30(554, "1.19.30"),
    V1_19_40(557, "1.19.40"),
    V1_19_50(560, "1.19.50"),
    V1_19_60(567, "1.19.60"),
    V1_19_63(568, "1.19.63"),
    V1_19_70(575, "1.19.70"),
    V1_19_80(582, "1.19.80"),
    V1_20_0(589, "1.20.0", "1.20"),
    V1_20_10(594, "1.20.10"),
    V1_20_30(618, "1.20.30"),
    V1_20_40(622, "1.20.40"),
    V1_20_50(630, "1.20.50"),
    V1_20_60(649, "1.20.60"),
    V1_20_70(662, "1.20.70"),
    V1_20_80(671, "1.20.80"),
    V1_21_0(685, "1.21.0", "1.21"),
    V1_21_2(686, "1.21.2"),
    V1_21_20(712, "1.21.20"),
    V1_21_30(729, "1.21.30"),
    V1_21_40(748, "1.21.40"),
    V1_21_50(766, "1.21.50"),
    V1_21_60(776, "1.21.60"),
    V1_21_70(786, "1.21.70"),
    V1_21_80(800, "1.21.80"),
    ;

    private static GameVersion FEATURE_VERSION = GameVersion.V1_20_10;

    private final int protocol;
    private final String name;
    private final String[] aliases;

    private boolean disabled;

    GameVersion(int protocol, String name, String... aliases) {
        this.protocol = protocol;
        this.name = name;
        this.aliases = aliases;
    }

    public int getProtocol() {
        return protocol;
    }

    @Override
    public String toString() {
        return name;
    }

    public String[] getAliases() {
        return aliases;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disable) {
        this.disabled = disable;
    }

    public boolean isAvailable() {
        return ordinal() <= FEATURE_VERSION.ordinal();
    }

    private static final GameVersion[] VALUES = values();
    private static final GameVersion LAST_VERSION = VALUES[VALUES.length - 1];
    private static final Map<String, GameVersion> BY_NAME = new Object2ObjectOpenHashMap<>();
    private static final Map<String, GameVersion> BY_ALIAS = new Object2ObjectOpenHashMap<>();
    private static final GameVersion[] BY_PROTOCOL;

    static {
        BY_PROTOCOL = new GameVersion[LAST_VERSION.protocol + 1];
        for (int i = 0; i < VALUES.length; i++) {
            GameVersion version = VALUES[i];

            BY_NAME.put(version.name, version);
            String[] aliases = version.aliases;
            if (aliases != null) {
                for (String alias : aliases) {
                    BY_ALIAS.put(alias, version);
                }
            }

            int next = i + 1;
            GameVersion nextVersion = next < VALUES.length ? VALUES[next] : null;
            if (nextVersion == null) {
                continue;
            }
            for (int j = version.protocol; j < nextVersion.protocol; j++) {
                BY_PROTOCOL[j] = version;
            }
        }
    }

    @Nullable
    public static GameVersion byName(String name) {
        return byName(name, true);
    }

    @Nullable
    public static GameVersion byName(String name, boolean alias) {
        GameVersion version = BY_NAME.get(name);
        if (version == null && alias) {
            return BY_ALIAS.get(name);
        }
        return version;
    }

    @Nullable
    public static GameVersion byProtocol(int protocol) {
        if (protocol < 0 || protocol > LAST_VERSION.protocol) {
            return null;
        }
        return BY_PROTOCOL[protocol];
    }

    public static GameVersion[] getValues() {
        return VALUES;
    }

    public static GameVersion getFeatureVersion() {
        return FEATURE_VERSION;
    }

    static void setFeatureVersion(GameVersion featureVersion) {
        Objects.requireNonNull(featureVersion, "version");
        FEATURE_VERSION = featureVersion;
    }

    public static GameVersion getLastVersion() {
        return LAST_VERSION;
    }
}
