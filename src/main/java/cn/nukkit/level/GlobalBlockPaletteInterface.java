package cn.nukkit.level;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.NoSuchElementException;

public interface GlobalBlockPaletteInterface {

    int getOrCreateRuntimeId0(int id, int meta);

    int getOrCreateRuntimeId0(int legacyId) throws NoSuchElementException;

    int getLegacyId0(int runtimeId);

    default GlobalBlockPaletteInterface getHardcodedBlockPalette0(HardcodedVersion version) {
        return this;
    }

    enum HardcodedVersion {
//        V1_16_100(419),
        V1_16_210(428),
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
}
