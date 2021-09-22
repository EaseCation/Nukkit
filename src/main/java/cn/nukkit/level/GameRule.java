package cn.nukkit.level;

import java.util.Optional;

public enum GameRule {
    COMMAND_BLOCK_OUTPUT("commandBlockOutput"),
    DO_DAYLIGHT_CYCLE("doDaylightCycle"),
    DO_ENTITY_DROPS("doEntityDrops"),
    DO_FIRE_TICK("doFireTick"),
    DO_IMMEDIATE_RESPAWN("doImmediateRespawn", 332),
    DO_MOB_LOOT("doMobLoot"),
    DO_MOB_SPAWNING("doMobSpawning"),
    DO_TILE_DROPS("doTileDrops"),
    DO_WEATHER_CYCLE("doWeatherCycle"),
    DROWNING_DAMAGE("drowningDamage"),
    FALL_DAMAGE("fallDamage"),
    FIRE_DAMAGE("fireDamage"),
    KEEP_INVENTORY("keepInventory"),
    MOB_GRIEFING("mobGriefing"),
    NATURAL_REGENERATION("naturalRegeneration"),
    PVP("pvp"),
    RANDOM_TICK_SPEED("randomTickSpeed", 312),
    SEND_COMMAND_FEEDBACK("sendCommandFeedback"),
    SHOW_COORDINATES("showCoordinates"),
    TNT_EXPLODES("tntExplodes"),
    SHOW_DEATH_MESSAGES("showDeathMessages", 332),
    MAX_COMMAND_CHAIN_LENGTH("maxCommandChainLength"),
    DO_INSOMNIA("doInsomnia", 282),
    COMMAND_BLOCKS_ENABLED("commandBlocksEnabled", 290),
    FUNCTION_COMMAND_LIMIT("functionCommandLimit", 332),
    SPAWN_RADIUS("spawnRadius", 361),
    SHOW_TAGS("showTags", 389),
    FREEZE_DAMAGE("freezeDamage", 428),
    RESPAWN_BLOCKS_EXPLODE("respawnBlocksExplode", 465),
    SHOW_BORDER_EFFECT("showBorderEffect", 465),
    ;

    private final String name;
    private final int protocol;

    GameRule(String name) {
        this(name, 0);
    }

    GameRule(String name, int protocol) {
        this.name = name;
        this.protocol = protocol;
    }

    public static Optional<GameRule> parseString(String gameRuleString) {
        for (GameRule gameRule: values()) {
            if (gameRule.getName().equalsIgnoreCase(gameRuleString)) {
                return Optional.of(gameRule);
            }
        }
        return Optional.empty();
    }

    public static String[] getNames() {
        String[] stringValues = new String[values().length];

        for (int i = 0; i < values().length; i++) {
            stringValues[i] = values()[i].getName();
        }
        return stringValues;
    }

    public String getName() {
        return name;
    }

    public int getProtocol() {
        return this.protocol;
    }
}
