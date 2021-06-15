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
        V1_16_100(419),
        V1_16_210(428),
        V1_17(440),
        ;

        private static final HardcodedVersion[] VALUES = HardcodedVersion.values();
        private static final Int2ObjectMap<HardcodedVersion> versionMap = new Int2ObjectOpenHashMap<>();

        static {
            for (HardcodedVersion version : VALUES) {
                versionMap.put(version.protocol, version);
            }
        }

        private final int protocol;

        HardcodedVersion(int protocol) {
            this.protocol = protocol;
        }

        public int getProtocol() {
            return this.protocol;
        }

        public static HardcodedVersion fromProtocol(int protocol) {
            return versionMap.getOrDefault(protocol, VALUES[VALUES.length - 1]);
        }

        public static HardcodedVersion[] values0() {
            return VALUES;
        }
    }

    /*enum HardcodedVersion {
        V1_16_100(419, false),
        V1_16_200_NE(422, false),
        V1_16_210(428, false),
        V1_17(440, false),
        ;

        private static final HardcodedVersion[] VALUES = HardcodedVersion.values();
        private static final Int2ObjectMap<HardcodedVersion[]> versionMap = new Int2ObjectOpenHashMap<>();

        static {
            for (HardcodedVersion version : VALUES) {
                versionMap.putIfAbsent(version.protocol, new HardcodedVersion[2]);
                if (!version.netease) {
                    versionMap.get(version.protocol)[0] = version;
                } else {
                    versionMap.get(version.protocol)[1] = version;
                }
            }
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
            HardcodedVersion[] versions = versionMap.get(protocol);
            if (versions == null) return VALUES[VALUES.length - 1];
            if (!netease || versions[1] == null) return versions[0];
            else return versions[1];
        }

        public static HardcodedVersion[] values0() {
            return VALUES;
        }
    }*/
}
