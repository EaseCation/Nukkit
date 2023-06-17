package cn.nukkit.network.protocol.types;

import lombok.ToString;

import java.util.UUID;

/**
 * @author SupremeMortal
 * Nukkit project
 */
@ToString
public final class CommandOriginData {
    public final Origin type;
    public final UUID uuid;
    public final String requestId;
    public final long playerEntityUniqueId;

    public CommandOriginData(Origin type, UUID uuid, String requestId) {
        this(type, uuid, requestId, 0);
    }

    public CommandOriginData(Origin type, UUID uuid, String requestId, long playerEntityUniqueId) {
        this.type = type;
        this.uuid = uuid;
        this.requestId = requestId;
        this.playerEntityUniqueId = playerEntityUniqueId;
    }

    public enum Origin {
        PLAYER,
        BLOCK,
        MINECART_BLOCK,
        DEV_CONSOLE,
        TEST,
        AUTOMATION_PLAYER,
        CLIENT_AUTOMATION,
        DEDICATED_SERVER,
        ENTITY,
        VIRTUAL,
        GAME_ARGUMENT,
        ENTITY_SERVER,
        PRECOMPILED,
        GAME_DIRECTOR_ENTITY_SERVER,
        SCRIPT,
        EXECUTE_CONTEXT,
        ;

        private static final Origin[] $VALUES0 = values();

        public static Origin[] values0() {
            return $VALUES0;
        }
    }
}