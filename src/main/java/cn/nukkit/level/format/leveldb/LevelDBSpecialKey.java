package cn.nukkit.level.format.leveldb;

import cn.nukkit.level.Level;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;
import java.util.function.Predicate;

public final class LevelDBSpecialKey {
    private static final int UUID_LENGTH = 32 + 4;

    public static final byte[] OVERWORLD = {'O', 'v', 'e', 'r', 'w', 'o', 'r', 'l', 'd'};
    public static final byte[] NETHER = {'N', 'e', 't', 'h', 'e', 'r'};
    public static final byte[] THE_END = {'T', 'h', 'e', 'E', 'n', 'd'};
    public static final byte[] BIOME_DATA = {'B', 'i', 'o', 'm', 'e', 'D', 'a', 't', 'a'};
    public static final byte[] PORTALS = {'p', 'o', 'r', 't', 'a', 'l', 's'};
    public static final byte[] AUTONOMOUS_ENTITIES = {'A', 'u', 't', 'o', 'n', 'o', 'm', 'o', 'u', 's', 'E', 'n', 't', 'i', 't', 'i', 'e', 's'};
    public static final byte[] SCOREBOARD = {'s', 'c', 'o', 'r', 'e', 'b', 'o', 'a', 'r', 'd'};
    public static final byte[] MOB_EVENTS = {'m', 'o', 'b', 'e', 'v', 'e', 'n', 't', 's'};
    public static final byte[] LEVEL_CHUNK_META_DATA_DICTIONARY = {'L', 'e', 'v', 'e', 'l', 'C', 'h', 'u', 'n', 'k', 'M', 'e', 't', 'a', 'D', 'a', 't', 'a', 'D', 'i', 'c', 't', 'i', 'o', 'n', 'a', 'r', 'y'};
    public static final byte[] SCHEDULER_WT = {'s', 'c', 'h', 'e', 'd', 'u', 'l', 'e', 'r', 'W', 'T'};
    public static final byte[] BIOME_IDS_TABLE = {'B', 'i', 'o', 'm', 'e', 'I', 'd', 's', 'T', 'a', 'b', 'l', 'e'};
    public static final byte[] DYNAMIC_PROPERTIES = {'D', 'y', 'n', 'a', 'm', 'i', 'c', 'P', 'r', 'o', 'p', 'e', 'r', 't', 'i', 'e', 's'};
    public static final byte[] WORLD_CLOCKS = {'W', 'o', 'r', 'l', 'd', 'C', 'l', 'o', 'c', 'k', 's'};
    public static final byte[] LOCAL_PLAYER = {'~', 'l', 'o', 'c', 'a', 'l', '_', 'p', 'l', 'a', 'y', 'e', 'r'};

    private static byte[] prefix(byte[] prefix, byte[] key) {
        byte[] result = new byte[prefix.length + key.length];
        System.arraycopy(prefix, 0, result, 0, prefix.length);
        System.arraycopy(key, 0, result, prefix.length, key.length);
        return result;
    }

    private static final byte[] PREFIX_PLAYER = {'p', 'l', 'a', 'y', 'e', 'r', '_'};

    public static final Predicate<byte[]> PLAYER_PREDICATE = key -> {
        if (key.length != PREFIX_PLAYER.length + UUID_LENGTH) {
            return false;
        }
        return startsWith(PREFIX_PLAYER, key);
    };

    public static byte[] player(byte[] msaOrSelfSignedUuid) {
        return prefix(PREFIX_PLAYER, msaOrSelfSignedUuid);
    }

    public static byte[] player(String msaOrSelfSignedUuid) {
        return player(msaOrSelfSignedUuid.getBytes(StandardCharsets.US_ASCII));
    }

    public static byte[] player(UUID msaOrSelfSignedUuid) {
        return player(msaOrSelfSignedUuid.toString());
    }

    private static final byte[] PREFIX_SERVER_PLAYER = {'p', 'l', 'a', 'y', 'e', 'r', '_', 's', 'e', 'r', 'v', 'e', 'r', '_'};

    public static final Predicate<byte[]> SERVER_PLAYER_PREDICATE = key -> {
        if (key.length != PREFIX_SERVER_PLAYER.length + UUID_LENGTH) {
            return false;
        }
        return startsWith(PREFIX_SERVER_PLAYER, key);
    };

    public static byte[] serverPlayer(byte[] serverUuid) {
        return prefix(PREFIX_SERVER_PLAYER, serverUuid);
    }

    public static byte[] serverPlayer(String serverUuid) {
        return serverPlayer(serverUuid.getBytes(StandardCharsets.US_ASCII));
    }

    public static byte[] serverPlayer(UUID serverUuid) {
        return serverPlayer(serverUuid.toString());
    }

    private static final byte[] PREFIX_MAP = {'m', 'a', 'p', '_'};

    public static final Predicate<byte[]> MAP_PREDICATE = key -> {
        if (key.length <= PREFIX_MAP.length) {
            return false;
        }
        return startsWith(PREFIX_MAP, key);
    };

    public static byte[] map(byte[] mapId) {
        return prefix(PREFIX_MAP, mapId);
    }

    public static byte[] map(String mapId) {
        return map(mapId.getBytes(StandardCharsets.US_ASCII));
    }

    public static byte[] map(long mapId) {
        return map(String.valueOf(mapId));
    }

    private static final byte[] PREFIX_TICKING_AREA = {'t', 'i', 'c', 'k', 'i', 'n', 'g', 'a', 'r', 'e', 'a', '_'};

    public static final Predicate<byte[]> TICKING_AREA_PREDICATE = key -> {
        if (key.length != PREFIX_TICKING_AREA.length + UUID_LENGTH) {
            return false;
        }
        return startsWith(PREFIX_TICKING_AREA, key);
    };

    public static byte[] tickingArea(byte[] uuid) {
        return prefix(PREFIX_TICKING_AREA, uuid);
    }

    public static byte[] tickingArea(String uuid) {
        return tickingArea(uuid.getBytes(StandardCharsets.US_ASCII));
    }

    public static byte[] tickingArea(UUID uuid) {
        return tickingArea(uuid.toString());
    }

    private static final byte[] PREFIX_STRUCTURE_TEMPLATE = {'s', 't', 'r', 'u', 'c', 't', 'u', 'r', 'e', 't', 'e', 'm', 'p', 'l', 'a', 't', 'e', '_'};

    public static final Predicate<byte[]> STRUCTURE_TEMPLATE_PREDICATE = key -> {
        if (key.length <= PREFIX_STRUCTURE_TEMPLATE.length) {
            return false;
        }
        return startsWith(PREFIX_STRUCTURE_TEMPLATE, key);
    };

    public static byte[] structureTemplate(byte[] structureName) {
        return prefix(PREFIX_STRUCTURE_TEMPLATE, structureName);
    }

    public static byte[] structureTemplate(String structureName) {
        return structureTemplate(structureName.getBytes(StandardCharsets.UTF_8));
    }

    private static final byte[] PREFIX_VILLAGE = {'V', 'I', 'L', 'L', 'A', 'G', 'E', '_', 'O', 'v', 'e', 'r', 'w', 'o', 'r', 'l', 'd', '_'};

    private static byte[] village(byte[] key, byte[] postfix) {
        byte[] result = new byte[PREFIX_VILLAGE.length + key.length +  postfix.length];
        System.arraycopy(PREFIX_VILLAGE, 0, result, 0, PREFIX_VILLAGE.length);
        System.arraycopy(key, 0, result, PREFIX_VILLAGE.length, key.length);
        System.arraycopy(postfix, 0, result, PREFIX_VILLAGE.length + key.length, postfix.length);
        return result;
    }

    private static final byte[] POSTFIX_INFO = {'_', 'I', 'N', 'F', 'O'};

    public static final Predicate<byte[]> VILLAGE_INFO_PREDICATE = key -> {
        if (key.length != PREFIX_VILLAGE.length + UUID_LENGTH + POSTFIX_INFO.length) {
            return false;
        }
        return startsWith(PREFIX_VILLAGE, key) && endsWith(key, POSTFIX_INFO);
    };

    public static byte[] villageInfo(byte[] uuid) {
        return village(uuid, POSTFIX_INFO);
    }

    public static byte[] villageInfo(String uuid) {
        return villageInfo(uuid.getBytes(StandardCharsets.US_ASCII));
    }

    public static byte[] villageInfo(UUID uuid) {
        return villageInfo(uuid.toString());
    }

    private static final byte[] POSTFIX_DWELLERS = {'_', 'D', 'W', 'E', 'L', 'L', 'E', 'R', 'S'};

    public static final Predicate<byte[]> VILLAGE_DWELLERS_PREDICATE = key -> {
        if (key.length != PREFIX_VILLAGE.length + UUID_LENGTH + POSTFIX_DWELLERS.length) {
            return false;
        }
        return startsWith(PREFIX_VILLAGE, key) && endsWith(key, POSTFIX_DWELLERS);
    };

    public static byte[] villageDwellers(byte[] uuid) {
        return village(uuid, POSTFIX_DWELLERS);
    }

    public static byte[] villageDwellers(String uuid) {
        return villageDwellers(uuid.getBytes(StandardCharsets.US_ASCII));
    }

    public static byte[] villageDwellers(UUID uuid) {
        return villageDwellers(uuid.toString());
    }

    private static final byte[] POSTFIX_POI = {'_', 'P', 'O', 'I'};

    public static final Predicate<byte[]> VILLAGE_POI_PREDICATE = key -> {
        if (key.length != PREFIX_VILLAGE.length + UUID_LENGTH + POSTFIX_POI.length) {
            return false;
        }
        return startsWith(PREFIX_VILLAGE, key) && endsWith(key, POSTFIX_POI);
    };

    public static byte[] villagePoi(byte[] uuid) {
        return village(uuid, POSTFIX_POI);
    }

    public static byte[] villagePoi(String uuid) {
        return villagePoi(uuid.getBytes(StandardCharsets.US_ASCII));
    }

    public static byte[] villagePoi(UUID uuid) {
        return villagePoi(uuid.toString());
    }

    private static final byte[] POSTFIX_PLAYERS = {'_', 'P', 'L', 'A', 'Y', 'E', 'R', 'S'};

    public static final Predicate<byte[]> VILLAGE_PLAYERS_PREDICATE = key -> {
        if (key.length != PREFIX_VILLAGE.length + UUID_LENGTH + POSTFIX_PLAYERS.length) {
            return false;
        }
        return startsWith(PREFIX_VILLAGE, key) && endsWith(key, POSTFIX_PLAYERS);
    };

    public static byte[] villagePlayers(byte[] uuid) {
        return village(uuid, POSTFIX_PLAYERS);
    }

    public static byte[] villagePlayers(String uuid) {
        return villagePlayers(uuid.getBytes(StandardCharsets.US_ASCII));
    }

    public static byte[] villagePlayers(UUID uuid) {
        return villagePlayers(uuid.toString());
    }

    private static final byte[] PREFIX_DIGP = {'d', 'i', 'g', 'p'};

    public static final Predicate<byte[]> DIGP_PREDICATE = key -> {
        if (key.length != PREFIX_DIGP.length + 4 + 4 && key.length != PREFIX_DIGP.length + 4 + 4 + 4) {
            return false;
        }
        return startsWith(PREFIX_DIGP, key);
    };

    /**
     * @see <a href="https://docs.microsoft.com/en-us/minecraft/creator/documents/actorstorage">Actor Storage in Minecraft: Bedrock Edition</a>
     * @since 1.18.30
     */
    public static byte[] digp(int chunkX, int chunkZ) {
        return new byte[]{
                'd', 'i', 'g', 'p',
                (byte) chunkX,
                (byte) (chunkX >>> 8),
                (byte) (chunkX >>> 16),
                (byte) (chunkX >>> 24),
                (byte) chunkZ,
                (byte) (chunkZ >>> 8),
                (byte) (chunkZ >>> 16),
                (byte) (chunkZ >>> 24),
        };
    }

    /**
     * @see <a href="https://docs.microsoft.com/en-us/minecraft/creator/documents/actorstorage">Actor Storage in Minecraft: Bedrock Edition</a>
     * @since 1.18.30
     */
    public static byte[] digp(int chunkX, int chunkZ, int dimension) {
        if (dimension == Level.DIMENSION_OVERWORLD) {
            return digp(chunkX, chunkZ);
        }
        return new byte[]{
                'd', 'i', 'g', 'p',
                (byte) chunkX,
                (byte) (chunkX >>> 8),
                (byte) (chunkX >>> 16),
                (byte) (chunkX >>> 24),
                (byte) chunkZ,
                (byte) (chunkZ >>> 8),
                (byte) (chunkZ >>> 16),
                (byte) (chunkZ >>> 24),
                (byte) dimension,
                (byte) (dimension >>> 8),
                (byte) (dimension >>> 16),
                (byte) (dimension >>> 24),
        };
    }

    private static final byte[] PREFIX_ACTOR = {'a', 'c', 't', 'o', 'r', 'p', 'r', 'e', 'f', 'i', 'x'};

    public static final Predicate<byte[]> ACTOR_PREDICATE = key -> {
        if (key.length != PREFIX_ACTOR.length + 8) {
            return false;
        }
        return startsWith(PREFIX_ACTOR, key);
    };

    /**
     * @see <a href="https://docs.microsoft.com/en-us/minecraft/creator/documents/actorstorage">Actor Storage in Minecraft: Bedrock Edition</a>
     * @since 1.18.30
     */
    public static byte[] actor(long entityUniqueId) {
        return new byte[]{
                'a', 'c', 't', 'o', 'r', 'p', 'r', 'e', 'f', 'i', 'x',
                (byte) entityUniqueId,
                (byte) (entityUniqueId >>> 8),
                (byte) (entityUniqueId >>> 16),
                (byte) (entityUniqueId >>> 24),
                (byte) (entityUniqueId >>> 32),
                (byte) (entityUniqueId >>> 40),
                (byte) (entityUniqueId >>> 48),
                (byte) (entityUniqueId >>> 56),
        };
    }

    private static boolean startsWith(byte[] prefix, byte[] key) {
        return Arrays.equals(prefix, 0, prefix.length, key, 0, prefix.length);
    }

    private static boolean endsWith(byte[] key, byte[] postfix) {
        return Arrays.equals(postfix, 0, postfix.length, key, key.length - postfix.length, key.length);
    }

    private LevelDBSpecialKey() {
        throw new IllegalStateException();
    }
}
