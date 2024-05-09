package cn.nukkit.level;

import java.util.Optional;

public enum GameRule {
    COMMAND_BLOCK_OUTPUT("commandBlockOutput"),
    DO_DAYLIGHT_CYCLE("doDaylightCycle"),
    DO_ENTITY_DROPS("doEntityDrops"),
    DO_FIRE_TICK("doFireTick"),
    RECIPES_UNLOCK("recipesUnlock", 618),
    DO_LIMITED_CRAFTING("doLimitedCrafting", 618),
    DO_MOB_LOOT("doMobLoot"),
    DO_MOB_SPAWNING("doMobSpawning"),
    DO_TILE_DROPS("doTileDrops"),
    DO_WEATHER_CYCLE("doWeatherCycle"),
    DROWNING_DAMAGE("drowningDamage"),
    FALL_DAMAGE("fallDamage"),
    FIRE_DAMAGE("fireDamage"),
    KEEP_INVENTORY("keepInventory"),
    MOB_GRIEFING("mobGriefing"),
    PVP("pvp"),
    SHOW_COORDINATES("showCoordinates"),
    SHOW_DAYS_PLAYED("showDaysPlayed", 685),
    NATURAL_REGENERATION("naturalRegeneration"),
    TNT_EXPLODES("tntExplodes"),
    SEND_COMMAND_FEEDBACK("sendCommandFeedback"),
    MAX_COMMAND_CHAIN_LENGTH("maxCommandChainLength"),
    DO_INSOMNIA("doInsomnia", 282),
    COMMAND_BLOCKS_ENABLED("commandBlocksEnabled", 290),
    RANDOM_TICK_SPEED("randomTickSpeed", 312),
    DO_IMMEDIATE_RESPAWN("doImmediateRespawn", 332),
    SHOW_DEATH_MESSAGES("showDeathMessages", 332),
    FUNCTION_COMMAND_LIMIT("functionCommandLimit", 332),
    SPAWN_RADIUS("spawnRadius", 361),
    SHOW_TAGS("showTags", 389),
    FREEZE_DAMAGE("freezeDamage", 428),
    RESPAWN_BLOCKS_EXPLODE("respawnBlocksExplode", 465),
    SHOW_BORDER_EFFECT("showBorderEffect", 465),
    SHOW_RECIPE_MESSAGES("showRecipeMessages", 630),
    PLAYERS_SLEEPING_PERCENTAGE("playersSleepingPercentage", 618),
    PROJECTILES_CAN_BREAK_BLOCKS("projectilesCanBreakBlocks", 630),
    TNT_EXPLOSION_DROP_DECAY("tntExplosionDropDecay", 685),
    ;

    private static final GameRule[] VALUES = values();

    private final String name;
    private final String bedrockName;
    private final int protocol;

    GameRule(String name) {
        this(name, 0);
    }

    GameRule(String name, int protocol) {
        this.name = name;
        this.bedrockName = name.toLowerCase();
        this.protocol = protocol;
    }

    public static Optional<GameRule> parseString(String gameRuleString) {
        for (GameRule gameRule: VALUES) {
            if (gameRule.getName().equalsIgnoreCase(gameRuleString)) {
                return Optional.of(gameRule);
            }
        }
        return Optional.empty();
    }

    public String getName() {
        return name;
    }

    public String getBedrockName() {
        return bedrockName;
    }

    public int getProtocol() {
        return this.protocol;
    }

    public static GameRule[] getValues() {
        return VALUES;
    }
}
