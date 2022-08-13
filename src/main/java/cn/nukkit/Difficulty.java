package cn.nukkit;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import javax.annotation.Nullable;
import java.util.Map;

public enum Difficulty {
    PEACEFUL("Peaceful", "%options.difficulty.peaceful", "p"),
    EASY("Easy", "%options.difficulty.easy", "e"),
    NORMAL("Normal", "%options.difficulty.normal", "n"),
    HARD("Hard", "%options.difficulty.hard", "h"),
    ;

    private static final Difficulty[] VALUES = values();
    private static final Map<String, Difficulty> BY_IDENTIFIER = new Object2ObjectOpenHashMap<>();
    private static final Map<String, Difficulty> BY_ALIAS = new Object2ObjectOpenHashMap<>();

    static {
        for (Difficulty difficulty : VALUES) {
            BY_IDENTIFIER.put(difficulty.identifier, difficulty);
            for (String alias : difficulty.aliases) {
                BY_ALIAS.put(alias, difficulty);
            }
        }
    }

    private final String name;
    private final String translationKey;
    private final String identifier;
    private final String[] aliases;

    Difficulty(String name, String translationKey, String... aliases) {
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

    @Nullable
    public static Difficulty byId(int id) {
        if (id < 0 || id >= VALUES.length) {
            return null;
        }
        return VALUES[id];
    }

    @Nullable
    public static Difficulty byIdentifier(String identifier) {
        return byIdentifier(identifier, true);
    }

    @Nullable
    public static Difficulty byIdentifier(String identifier, boolean alias) {
        Difficulty difficulty = BY_IDENTIFIER.get(identifier);
        if (difficulty == null && alias) {
            return BY_ALIAS.get(identifier);
        }
        return difficulty;
    }

    public static Difficulty[] getValues() {
        return VALUES;
    }
}
