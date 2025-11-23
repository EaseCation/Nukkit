package cn.nukkit.command.data;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum CommandPermission {
    ALL("any"),
    GAME_DIRECTORS("gamedirectors"),
    ADMIN("admin"),
    HOST("host"),
    OWNER("owner"),
    INTERNAL("internal"),
    ;

    private static final CommandPermission[] VALUES = values();
    private static final Map<String, CommandPermission> BY_NAME = Arrays.stream(VALUES)
            .collect(Collectors.toMap(CommandPermission::getName, Function.identity()));

    private final String name;

    CommandPermission(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Nullable
    public static CommandPermission byName(String name) {
        return BY_NAME.get(name);
    }

    public static CommandPermission[] getValues() {
        return VALUES;
    }
}
