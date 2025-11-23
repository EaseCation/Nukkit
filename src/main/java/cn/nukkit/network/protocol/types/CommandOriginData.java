package cn.nukkit.network.protocol.types;

import lombok.ToString;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        PLAYER("player"),
        BLOCK,
        MINECART_BLOCK,
        DEV_CONSOLE("devconsole"),
        TEST("test"),
        AUTOMATION_PLAYER("automationplayer"),
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

        private static final Origin[] VALUES = values();
        private static final Map<String, Origin> BY_NAME = Arrays.stream(VALUES)
                .filter(origin -> origin.name != null)
                .collect(Collectors.toMap(Origin::getName, Function.identity()));

        @Nullable
        private final String name;

        Origin() {
            this(null);
        }

        Origin(String name) {
            this.name = name;
        }

        @Nullable
        public String getName() {
            return name;
        }

        @Nullable
        public static Origin byName(String name) {
            return BY_NAME.get(name);
        }

        public static Origin[] getValues() {
            return VALUES;
        }
    }
}