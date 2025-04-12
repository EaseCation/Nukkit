package cn.nukkit.level;

import java.util.Optional;

import static cn.nukkit.GameVersion.*;

public enum GameRule {
    COMMAND_BLOCK_OUTPUT("commandBlockOutput"),
    DO_DAYLIGHT_CYCLE("doDaylightCycle"),
    DO_ENTITY_DROPS("doEntityDrops"),
    DO_FIRE_TICK("doFireTick"),
    RECIPES_UNLOCK("recipesUnlock", V1_20_30.getProtocol()),
    DO_LIMITED_CRAFTING("doLimitedCrafting", V1_20_30.getProtocol()),
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
    LOCATOR_BAR("locatorBar", V1_21_80.getProtocol()),
    SHOW_DAYS_PLAYED("showDaysPlayed", V1_21_0.getProtocol()),
    NATURAL_REGENERATION("naturalRegeneration"),
    TNT_EXPLODES("tntExplodes"),
    SEND_COMMAND_FEEDBACK("sendCommandFeedback"),
    MAX_COMMAND_CHAIN_LENGTH("maxCommandChainLength"),
    DO_INSOMNIA("doInsomnia", V1_6_0.getProtocol()),
    COMMAND_BLOCKS_ENABLED("commandBlocksEnabled", V1_7_0.getProtocol()),
    RANDOM_TICK_SPEED("randomTickSpeed", V1_8_0.getProtocol()),
    DO_IMMEDIATE_RESPAWN("doImmediateRespawn", V1_9_0.getProtocol()),
    SHOW_DEATH_MESSAGES("showDeathMessages", V1_9_0.getProtocol()),
    FUNCTION_COMMAND_LIMIT("functionCommandLimit", V1_9_0.getProtocol()),
    SPAWN_RADIUS("spawnRadius", V1_12_0.getProtocol()),
    SHOW_TAGS("showTags", V1_14_0.getProtocol()),
    FREEZE_DAMAGE("freezeDamage", V1_16_210.getProtocol()),
    RESPAWN_BLOCKS_EXPLODE("respawnBlocksExplode", V1_17_30.getProtocol()),
    SHOW_BORDER_EFFECT("showBorderEffect", V1_17_30.getProtocol()),
    SHOW_RECIPE_MESSAGES("showRecipeMessages", V1_20_50.getProtocol()),
    PLAYERS_SLEEPING_PERCENTAGE("playersSleepingPercentage", V1_20_30.getProtocol()),
    PROJECTILES_CAN_BREAK_BLOCKS("projectilesCanBreakBlocks", V1_20_50.getProtocol()),
    TNT_EXPLOSION_DROP_DECAY("tntExplosionDropDecay", V1_21_0.getProtocol()),
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
