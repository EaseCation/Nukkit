package cn.nukkit.level.format.leveldb;

import cn.nukkit.level.Level;

public enum LevelDBKey {
    HEIGHTMAP_AND_3D_BIOMES('+'),
    NEW_VERSION(','), // since 1.16.100?
    HEIGHTMAP_AND_2D_BIOMES('-'), // obsolete since 1.18
    HEIGHTMAP_AND_2D_BIOME_COLORS('.'), // obsolete since 1.0
    SUBCHUNK('/'),
    LEGACY_TERRAIN('0'), // obsolete since 1.0
    BLOCK_ENTITIES('1'),
    ENTITIES('2'),
    PENDING_SCHEDULED_TICKS('3'),
    LEGACY_BLOCK_EXTRA_DATA('4'), // obsolete since 1.2.13
    BIOME_STATES('5'), //TODO: is this still applicable to 1.18.0?
    FINALIZATION('6'),
    CONVERTER_TAG('7'), // ???
    BORDER_BLOCKS('8'),
    HARDCODED_SPAWNERS('9'),
    PENDING_RANDOM_TICKS(':'),
    XXHASH_CHECKSUMS(';'), // obsolete since 1.18
    GENERATION_SEED('<'),
    GENERATED_BEFORE_CNC_BLENDING('='),
    BLENDING_BIOME_HEIGHT('>'),
    META_DATA_HASH('?'),
    BLENDING_DATA('@'),
    ENTITY_DIGEST_VERSION('A'),
    OLD_VERSION('v'),
    NUKKIT_DATA('f'), // Nukkit only
    ;

    private final byte encoded;

    LevelDBKey(char encoded) {
        this.encoded = (byte) encoded;
    }

    public byte getCode() {
        return this.encoded;
    }

    public byte[] getKey(int chunkX, int chunkZ) {
        return new byte[]{
                (byte) (chunkX & 0xff),
                (byte) ((chunkX >>> 8) & 0xff),
                (byte) ((chunkX >>> 16) & 0xff),
                (byte) ((chunkX >>> 24) & 0xff),
                (byte) (chunkZ & 0xff),
                (byte) ((chunkZ >>> 8) & 0xff),
                (byte) ((chunkZ >>> 16) & 0xff),
                (byte) ((chunkZ >>> 24) & 0xff),
                this.encoded,
        };
    }

    public byte[] getKey(int chunkX, int chunkZ, int dimension) {
        if (dimension == Level.DIMENSION_OVERWORLD) {
            return getKey(chunkX, chunkZ);
        }
        return new byte[]{
                (byte) (chunkX & 0xff),
                (byte) ((chunkX >>> 8) & 0xff),
                (byte) ((chunkX >>> 16) & 0xff),
                (byte) ((chunkX >>> 24) & 0xff),
                (byte) (chunkZ & 0xff),
                (byte) ((chunkZ >>> 8) & 0xff),
                (byte) ((chunkZ >>> 16) & 0xff),
                (byte) ((chunkZ >>> 24) & 0xff),
                (byte) (dimension & 0xff),
                this.encoded,
        };
    }

    public byte[] getSubKey(int chunkX, int chunkZ, int chunkY) {
        return new byte[]{
                (byte) (chunkX & 0xff),
                (byte) ((chunkX >>> 8) & 0xff),
                (byte) ((chunkX >>> 16) & 0xff),
                (byte) ((chunkX >>> 24) & 0xff),
                (byte) (chunkZ & 0xff),
                (byte) ((chunkZ >>> 8) & 0xff),
                (byte) ((chunkZ >>> 16) & 0xff),
                (byte) ((chunkZ >>> 24) & 0xff),
                this.encoded,
                (byte) (chunkY & 0xff),
        };
    }

    public byte[] getSubKey(int chunkX, int chunkZ, int dimension, int chunkY) {
        if (dimension == Level.DIMENSION_OVERWORLD) {
            return getSubKey(chunkX, chunkZ, chunkY);
        }
        return new byte[]{
                (byte) (chunkX & 0xff),
                (byte) ((chunkX >>> 8) & 0xff),
                (byte) ((chunkX >>> 16) & 0xff),
                (byte) ((chunkX >>> 24) & 0xff),
                (byte) (chunkZ & 0xff),
                (byte) ((chunkZ >>> 8) & 0xff),
                (byte) ((chunkZ >>> 16) & 0xff),
                (byte) ((chunkZ >>> 24) & 0xff),
                (byte) (dimension & 0xff),
                this.encoded,
                (byte) (chunkY & 0xff),
        };
    }
}
