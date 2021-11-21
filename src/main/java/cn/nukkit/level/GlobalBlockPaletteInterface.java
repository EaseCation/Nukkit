package cn.nukkit.level;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

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

    default GlobalBlockPaletteInterface getHardcodedBlockPalette0(HardcodedVersion version) {
        return this;
    }

    enum HardcodedVersion {
        V1_16_100(419, false),
        V1_16_200_NETEASE(422, true),
        V1_16_210(428, false),
        V1_17(440, false),
        V1_17_10(448, false),
        V1_17_30(465, false),
        V1_17_40(471, false),
        V1_18(475, false),
        ;

        private static final HardcodedVersion[] VALUES = HardcodedVersion.values();
        private static final Int2ObjectMap<HardcodedVersion> versionMap = new Int2ObjectOpenHashMap<>();
        private static final Int2ObjectMap<HardcodedVersion> versionMapNetEase = new Int2ObjectOpenHashMap<>();

        static {
            for (int i = 0; i < VALUES.length; i++) {
                HardcodedVersion version = VALUES[i];
                if (!version.netease) {
                    versionMap.put(version.protocol, version);
                    HardcodedVersion nextIntl = findNextVersion(version, false);
                    if (nextIntl != null) { //还有下一个
                        for (int p = version.protocol; p < nextIntl.protocol; p++) {
                            versionMap.put(p, version);
                        }
                    }
                }
                HardcodedVersion nextNE = findNextVersion(version, true);
                versionMapNetEase.put(version.protocol, version);
                if (nextNE != null) { //还有下一个
                    for (int p = version.protocol; p < nextNE.protocol; p++) {
                        versionMapNetEase.put(p, version);
                    }
                }
            }
        }

        private static HardcodedVersion findNextVersion(HardcodedVersion version, boolean netease) {
            for (int i = version.ordinal() + 1; i < VALUES.length; i++) {
                HardcodedVersion v = VALUES[i];
                if (netease || !v.netease) return v;
            }
            return null;
        }

        private final int protocol;
        private final boolean netease;

        HardcodedVersion(int protocol, boolean netease) {
            this.protocol = protocol;
            this.netease = netease;
        }

        public int getProtocol() {
            return this.protocol;
        }

        public static HardcodedVersion fromProtocol(int protocol, boolean netease) {
            if (netease) {
                return versionMapNetEase.getOrDefault(protocol, VALUES[VALUES.length - 1]);
            }
            else {
                return versionMap.getOrDefault(protocol, VALUES[VALUES.length - 1]);
            }
        }

        public static HardcodedVersion[] values0() {
            return VALUES;
        }

    }

}
