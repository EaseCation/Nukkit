package cn.nukkit.command.data;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum CommandVersion {
    INVALID("invalid", -1),
    INITIAL("initial", 1),
    TP_ROTATIONCLAMPING("tprotationclamping", 2),
    NEW_BEDROCKCMD_SYSTEM("newbedrockcmdsystem", 3),
    EXECUTE_USESVEC3("executesusevec3", 4),
    CLONE_FIXES("clonefixes", 5),
    UPDATE_AQUATIC("updateaquatic", 6),
    ENTITY_SELECTORUSES_VEC3("entityselectorusesvec3", 7),
    CONTAINERS_DONTDROP_ITEMSANYMORE("containersdontdropitemsanymore", 8),
    FILTERS_OBEYDIMENSIONS("filtersobeydimensions", 9),
    EXECUTE_ANDBLOCK_COMMANDAND_SELFSELECTOR_FIXES("executeandblockcommandandselffixer", 10),
    INSTANT_EFFECTSUSE_TICKS("instanteffectusesticks", 11),
    DONT_REGISTERBROKEN_FUNCTIONCOMMANDS("dontregisterbrokenfunctioncommands", 12),
    CLEAR_SPAWNPOINT_COMMAND("clearspawnpointcommand", 13),
    CLONE_ANDTELEPORT_ROTATIONFIXES("cloneandteleportrotationfixes", 14),
    TELEPORT_DIMENSIONFIXES("teleportdimensionfixes", 15),
    CLONE_UPDATEBLOCK_ANDTIME_FIXES("cloneupdateblockandtimefixes", 16),
    CLONE_INTERSECTFIX("cloneintersectfix", 17),
    FUNCTION_EXECUTEORDER_ANDCHEST_SLOTFIX("functionexecuteorderandchestslotfix", 18),
    NON_TICKINGAREAS_NOLONGER_CONSIDEREDLOADED("nontickingareasnolongerconsideredloaded", 19),
    SPREADPLAYERS_HAZARDAND_RESOLVEPLAYER_BYNAME_FIX("spreadplayershazardandresolveplayerbynamefix", 20),
    NEW_EXECUTECOMMAND_SYNTAXEXPERIMENT_ANDCHEST_LOOTTABLE_FIXAND_TELEPORTFACING_VERTICALUNCLAMPED_ANDLOCATE_BIOMEAND_FEATUREMERGED("newexecutecommandsyntaxexperimentandchestloottablefixandteleportfacingverticalunclampedandlocatebiomeandfeaturemerged", 21),
    WATERLOGGING_ADDEDTO_STRUCTURECOMMAND("waterloggingaddedtostructurecommand", 22),
    SELECTOR_DISTANCEFILTERED_AND_RELATIVE_ROTATIONFIX("selectordistancefilteredandrelativerotationfix", 23),
    NEW_SUMMONCOMMAND_ADDEDROTATION_OPTIONSAND_BUBBLE_COLUMN_CLONEFIX_ANDEXECUTE_INDIMENSION_TELEPORTFIX_ANDNEW_EXECUTEROTATION_FIX("newsummoncommandaddedrotationoptionsandbubblecolumnclonefixandexecuteindimensionteleportfixandnewexecuterotationfix", 24),
    NEW_EXECUTECOMMAND_RELEASEENCHANT_COMMANDLEVEL_FIXAND_HASITEM_DATAFIX_ANDCOMMAND_DEFERRED("newexecutecommandreleaseenchantcommandlevelfixandhasitemdatafixandcommanddeferred", 25),
    EXECUTE_IFSCORE_FIXES("executeifscorefixes", 26),
    REPLACE_ITEMAND_LOOTREPLACE_BLOCKCOMMANDS_DONOT_PLACE_ITEMS_INTOCAULDRONS_FIX("replaceitemandlootreplaceblockcommandsdontplaceitemsintocauldronsfix", 27),
    CHANGES_TOCOMMAND_ORIGINROTATION("changestocommandoriginrotation", 28),
    REMOVE_AUXVALUE_PARAMETERFROM_BLOCKCOMMANDS("removeauxvalueparameterfromblockcommands", 29),
    VOLUME_SELECTORFIXES("volumeselectorfixes", 30),
    ENABLE_SUMMONROTATION("enablesummonrotation", 31),
    SUMMON_COMMANDDEFAULT_ROTATION("summoncommanddefaultrotation", 32),
    POSITIONAL_DIMENSIONFILTERING("positionaldimensionfiltering", 33),
    COMMAND_SELECTORHAS_ITEMFILTER_NOLONGER_CALLSSAME_ITEMFUNCTION("commandselectorhasitemfilternolongercallssameitemfunction", 34),
    AGENT_SWEEPINGBLOCK_TEST("agentsweepingblocktest", 34),
    BLOCK_STATEEQUALS("blockstateequals", 35),
    COMMAND_POSITIONFIX("commandpositionfix", 35),
    COMMAND_SELECTORHAS_ITEMFILTER_USESDATA_ASDAMAGE_FORSELECTING_DAMAGEABLEITEMS("commandselectorhasitemfilterusesdataasdamageforselectingdamageableitems", 36),
    EXECUTE_DETECTCONDITION_SUBCOMMANDNOT_ALLOWNON_LOADEDBLOCKS("executedetectconditionsubcommandnotallownonloadedblocks", 37),
    REMOVE_SUICIDEKEYWORD("removesuicidekeyword", 38),
    CLONE_CONTAINERBLOCK_ENTITYREMOVAL_FIX("clonecontainerblockentityremovalfix", 39),
    STOP_SOUNDMUSIC_FIX("stopsoundmusicfix", 40),
    SPREAD_PLAYERSSTUCK_INGROUND_FIXAND_MAXHEIGHT_PARAMETER("spreadplayersstuckingroundfixandmaxheightparameter", 41),
    LOCATE_STRUCTUREOUTPUT("locatestructureoutput", 42),
    POST_BLOCKFLATTENING("postblockflattening", 43),
    TEST_FORBLOCK_COMMANDDOES_NOTIGNORE_BLOCKSTATE("testforblockcommanddoesnotignoreblockstate", 44),
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
