package cn.nukkit.command.data;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum CommandVersion {
    INVALID("invalid", -1),
    INITIAL("initial", 1),
    TP_ROTATION_CLAMPING("tprotationclamping", 2),
    NEW_BEDROCK_CMD_SYSTEM("newbedrockcmdsystem", 3),
    EXECUTE_USES_VEC3("executesusevec3", 4),
    CLONE_FIXES("clonefixes", 5),
    UPDATE_AQUATIC("updateaquatic", 6),
    ENTITY_SELECTOR_USES_VEC3("entityselectorusesvec3", 7),
    CONTAINERS_DONT_DROP_ITEMS_ANYMORE("containersdontdropitemsanymore", 8),
    FILTERS_OBEY_DIMENSIONS("filtersobeydimensions", 9),
    EXECUTE_AND_BLOCK_COMMANDAND_SELF_SELECTOR_FIXES("executeandblockcommandandselffixer", 10),
    INSTANT_EFFECTS_USE_TICKS("instanteffectusesticks", 11),
    DONT_REGISTER_BROKEN_FUNCTION_COMMANDS("dontregisterbrokenfunctioncommands", 12),
    CLEAR_SPAWNPOINT_COMMAND("clearspawnpointcommand", 13),
    CLONE_AND_TELEPORT_ROTATION_FIXES("cloneandteleportrotationfixes", 14),
    TELEPORT_DIMENSION_FIXES("teleportdimensionfixes", 15),
    CLONE_UPDATE_BLOCK_AND_TIME_FIXES("cloneupdateblockandtimefixes", 16),
    CLONE_INTERSECT_FIX("cloneintersectfix", 17),
    FUNCTION_EXECUTE_ORDER_AND_CHEST_SLOT_FIX("functionexecuteorderandchestslotfix", 18),
    NON_TICKING_AREAS_NO_LONGER_CONSIDERED_LOADED("nontickingareasnolongerconsideredloaded", 19),
    SPREAD_PLAYERS_HAZARD_AND_RESOLVE_PLAYER_BY_NAME_FIX("spreadplayershazardandresolveplayerbynamefix", 20),
    NEW_EXECUTE_COMMAND_SYNTAX_EXPERIMENT_AND_CHEST_LOOT_TABLE_FIX_AND_TELEPORT_FACING_VERTICAL_UNCLAMPED_AND_LOCATE_BIOME_AND_FEATURE_MERGED("newexecutecommandsyntaxexperimentandchestloottablefixandteleportfacingverticalunclampedandlocatebiomeandfeaturemerged", 21),
    WATERLOGGING_ADDED_TO_STRUCTURE_COMMAND("waterloggingaddedtostructurecommand", 22),
    SELECTOR_DISTANCE_FILTERED_AND_RELATIVE_ROTATION_FIX("selectordistancefilteredandrelativerotationfix", 23),
    NEW_SUMMON_COMMAND_ADDED_ROTATION_OPTIONS_AND_BUBBLE_COLUMN_CLONE_FIX_AND_EXECUTE_IN_DIMENSION_TELEPORT_FIX_AND_NEW_EXECUTE_ROTATION_FIX("newsummoncommandaddedrotationoptionsandbubblecolumnclonefixandexecuteindimensionteleportfixandnewexecuterotationfix", 24),
    NEW_EXECUTE_COMMAND_RELEASE_ENCHANT_COMMAND_LEVEL_FIXAND_HAS_ITEM_DATA_FIX_AND_COMMAND_DEFERRED("newexecutecommandreleaseenchantcommandlevelfixandhasitemdatafixandcommanddeferred", 25),
    EXECUTE_IF_SCORE_FIXES("executeifscorefixes", 26),
    REPLACE_ITEM_AND_LOOT_REPLACE_BLOCK_COMMANDS_DO_NOT_PLACE_ITEMS_INTO_CAULDRONS_FIX("replaceitemandlootreplaceblockcommandsdontplaceitemsintocauldronsfix", 27),
    CHANGES_TO_COMMAND_ORIGIN_ROTATION("changestocommandoriginrotation", 28),
    REMOVE_AUX_VALUE_PARAMETER_FROM_BLOCK_COMMANDS("removeauxvalueparameterfromblockcommands", 29),
    VOLUME_SELECTOR_FIXES("volumeselectorfixes", 30),
    ENABLE_SUMMON_ROTATION("enablesummonrotation", 31),
    SUMMON_COMMAND_DEFAULT_ROTATION("summoncommanddefaultrotation", 32),
    POSITIONAL_DIMENSION_FILTERING("positionaldimensionfiltering", 33),
    COMMAND_SELECTOR_HAS_ITEM_FILTER_NO_LONGER_CALLS_SAME_ITEM_FUNCTION("commandselectorhasitemfilternolongercallssameitemfunction", 34),
    AGENT_SWEEPING_BLOCK_TEST("agentsweepingblocktest", 34),
    BLOCK_STATE_EQUALS("blockstateequals", 35),
    COMMAND_POSITION_FIX("commandpositionfix", 35),
    COMMAND_SELECTOR_HAS_ITEM_FILTER_USES_DATA_AS_DAMAGE_FOR_SELECTING_DAMAGEABLE_ITEMS("commandselectorhasitemfilterusesdataasdamageforselectingdamageableitems", 36),
    EXECUTE_DETECT_CONDITION_SUBCOMMAND_NOT_ALLOW_NON_LOADED_BLOCKS("executedetectconditionsubcommandnotallownonloadedblocks", 37),
    REMOVE_SUICIDE_KEYWORD("removesuicidekeyword", 38),
    CLONE_CONTAINER_BLOCK_ENTITY_REMOVAL_FIX("clonecontainerblockentityremovalfix", 39),
    STOP_SOUND_MUSIC_FIX("stopsoundmusicfix", 40),
    SPREAD_PLAYERS_STUCK_IN_GROUND_FIX_AND_MAX_HEIGHT_PARAMETER("spreadplayersstuckingroundfixandmaxheightparameter", 41),
    LOCATE_STRUCTURE_OUTPUT("locatestructureoutput", 42),
    POST_BLOCK_FLATTENING("postblockflattening", 43),
    TESTFOR_BLOCK_COMMAND_DOES_NOT_IGNORE_BLOCK_STATE("testforblockcommanddoesnotignoreblockstate", 44),
    LATEST("latest", 45),
    ;

    private static final Map<String, CommandVersion> BY_NAME = Arrays.stream(values())
            .collect(Collectors.toMap(CommandVersion::getName, Function.identity()));

    private final String name;
    private final int version;

    CommandVersion(String name, int version) {
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public int getVersion() {
        return version;
    }

    @Nullable
    public static CommandVersion byName(String name) {
        return BY_NAME.get(name);
    }
}
