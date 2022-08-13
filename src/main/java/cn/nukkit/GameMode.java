package cn.nukkit;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import javax.annotation.Nullable;
import java.util.Map;

public enum GameMode {
    SURVIVAL("Survival", "%gameMode.survival", "s"),
    CREATIVE("Creative", "%gameMode.creative", "c"),
    ADVENTURE("Adventure", "%gameMode.adventure", "a"),
    SPECTATOR("Spectator", "%gameMode.spectator", "spc", "v", "view"),
    ;

    private static final GameMode[] VALUES = values();
    private static final Map<String, GameMode> BY_IDENTIFIER = new Object2ObjectOpenHashMap<>();
    private static final Map<String, GameMode> BY_ALIAS = new Object2ObjectOpenHashMap<>();

    static {
        for (GameMode mode : VALUES) {
            BY_IDENTIFIER.put(mode.identifier, mode);
            for (String alias : mode.aliases) {
                BY_ALIAS.put(alias, mode);
            }
        }
    }

    private final String name;
    private final String translationKey;
    private final String identifier;
    private final String[] aliases;

    GameMode(String name, String translationKey, String... aliases) {
        this.name = name;
        this.translationKey = translationKey;
        this.identifier = name.toLowerCase();
        this.aliases = aliases;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String[] getAliases() {
        return aliases;
    }

    public int toVanillaId() {
        return this != SPECTATOR ? ordinal() : CREATIVE.ordinal();
    }

    @Nullable
    public static GameMode byId(int id) {
        if (id < 0 || id >= VALUES.length) {
            return null;
        }
        return VALUES[id];
    }

    @Nullable
    public static GameMode fromVanilla(int id) {
        switch (id) {
            case 0:
                return SURVIVAL;
            case 1:
                return CREATIVE;
            case 2:
                return ADVENTURE;
            case 3:
            case 4:
            case 6:
                return SPECTATOR;
            case 5:
            default:
                return null;
        }
    }

    @Nullable
    public static GameMode byIdentifier(String identifier) {
        return byIdentifier(identifier, true);
    }

    @Nullable
    public static GameMode byIdentifier(String identifier, boolean alias) {
        GameMode mode = BY_IDENTIFIER.get(identifier);
        if (mode == null && alias) {
            return BY_ALIAS.get(identifier);
        }
        return mode;
    }

    public static GameMode[] getValues() {
        return VALUES;
    }
}
