package cn.nukkit.level;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public interface GlobalBlockPaletteInterface {

    int getOrCreateRuntimeId0(int id, int meta);

    int getOrCreateRuntimeId0(int legacyId) throws NoSuchElementException;

    default int getOrCreateRuntimeIdGeneral0(int id, int meta) {
        return getOrCreateRuntimeId0(id, meta);
    }

    default int getOrCreateRuntimeIdGeneral0(int legacyId) throws NoSuchElementException {
        return getOrCreateRuntimeId0(legacyId);
    }

    int getLegacyId0(int runtimeId);

    default GlobalBlockPaletteInterface getStaticBlockPalette0(StaticVersion version) {
        return this;
    }

    enum StaticVersion {
        V1_16_100(419, false),
        V1_16_200_NETEASE(422, true),
        V1_16_210(428, false),
        V1_17(440, false),
        V1_17_NETEASE(440, true),
        V1_17_10(448, false),
        V1_17_30(465, false),
        V1_17_40(471, false),
        V1_18(475, false), // same palette, but chunk format changed
        V1_18_NETEASE(475, true),
        V1_18_10(486, false),
        V1_18_30(503, false),
        V1_18_30_NETEASE(504, true),
        V1_19(527, false),
        V1_19_20(544, false),
        V1_19_50(560, false),
        V1_19_60(567, false),
        V1_19_70(575, false),
        V1_19_80(582, false),
        V1_20_0(589, false),
        V1_20_10(594, false),
        V1_20_10_NETEASE(594, true),
        V1_20_30(618, false),
        V1_20_40(622, false),
        V1_20_50(630, false),
        V1_20_60(649, false),
        V1_20_70(662, false),
        V1_20_80(671, false),
        V1_21_0(685, false),
        V1_21_20(712, false),
        ;

        private static final StaticVersion MINIMUM_AVAILABLE_VERSION = V1_18_30;
        private static final StaticVersion[] AVAILABLE_VERSIONS;

        private static final StaticVersion[] VALUES = StaticVersion.values();

        private static final Int2ObjectMap<StaticVersion> versionMap = new Int2ObjectOpenHashMap<>();
        private static final Int2ObjectMap<StaticVersion> versionMapNetEase = new Int2ObjectOpenHashMap<>();

        static {
            List<StaticVersion> availableVersions = new ArrayList<>(VALUES.length);
            for (StaticVersion version : VALUES) {
                if (version.getProtocol() >= MINIMUM_AVAILABLE_VERSION.getProtocol()) {
                    availableVersions.add(version);
                }

                if (!version.netease) {
                    versionMap.put(version.protocol, version);
                    StaticVersion nextIntl = findNextVersion(version, false);
                    if (nextIntl != null) { //还有下一个
                        for (int p = version.protocol; p < nextIntl.protocol; p++) {
                            versionMap.put(p, version);
                        }
                    }
                }
                StaticVersion nextNE = findNextVersion(version, true);
                versionMapNetEase.put(version.protocol, version);
                if (nextNE != null) { //还有下一个
                    for (int p = version.protocol; p < nextNE.protocol; p++) {
                        versionMapNetEase.put(p, version);
                    }
                }
            }
            AVAILABLE_VERSIONS = availableVersions.toArray(new StaticVersion[0]);
        }

        private static StaticVersion findNextVersion(StaticVersion version, boolean netease) {
            for (int i = version.ordinal() + 1; i < VALUES.length; i++) {
                StaticVersion v = VALUES[i];
                if (netease || !v.netease) return v;
            }
            return null;
        }

        private final int protocol;
        private final boolean netease;

        StaticVersion(int protocol, boolean netease) {
            this.protocol = protocol;
            this.netease = netease;
        }

        public int getProtocol() {
            return this.protocol;
        }

        public static StaticVersion fromProtocol(int protocol, boolean netease) {
            if (netease) {
                return versionMapNetEase.getOrDefault(protocol, VALUES[VALUES.length - 1]);
            }
            else {
                return versionMap.getOrDefault(protocol, VALUES[VALUES.length - 1]);
            }
        }

        public static StaticVersion[] getValues() {
            return VALUES;
        }

        public static StaticVersion[] getAvailableVersions() {
            return AVAILABLE_VERSIONS;
        }
    }

}
