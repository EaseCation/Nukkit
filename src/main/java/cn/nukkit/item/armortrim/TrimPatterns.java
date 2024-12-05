package cn.nukkit.item.armortrim;

import cn.nukkit.GameVersion;
import cn.nukkit.item.ItemFullNames;
import cn.nukkit.item.ItemID;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

import static cn.nukkit.GameVersion.*;

public final class TrimPatterns {
    private static final Map<String, TrimPattern> REGISTRY = new HashMap<>();

    public static final TrimPattern WARD = register(new TrimPattern(TrimPatternNames.WARD, ItemFullNames.WARD_ARMOR_TRIM_SMITHING_TEMPLATE, ItemID.WARD_ARMOR_TRIM_SMITHING_TEMPLATE));
    public static final TrimPattern SENTRY = register(new TrimPattern(TrimPatternNames.SENTRY, ItemFullNames.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, ItemID.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE));
    public static final TrimPattern SNOUT = register(new TrimPattern(TrimPatternNames.SNOUT, ItemFullNames.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, ItemID.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE));
    public static final TrimPattern DUNE = register(new TrimPattern(TrimPatternNames.DUNE, ItemFullNames.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE, ItemID.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE));
    public static final TrimPattern SPIRE = register(new TrimPattern(TrimPatternNames.SPIRE, ItemFullNames.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, ItemID.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE));
    public static final TrimPattern TIDE = register(new TrimPattern(TrimPatternNames.TIDE, ItemFullNames.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, ItemID.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE));
    public static final TrimPattern WILD = register(new TrimPattern(TrimPatternNames.WILD, ItemFullNames.WILD_ARMOR_TRIM_SMITHING_TEMPLATE, ItemID.WILD_ARMOR_TRIM_SMITHING_TEMPLATE));
    public static final TrimPattern RIB = register(new TrimPattern(TrimPatternNames.RIB, ItemFullNames.RIB_ARMOR_TRIM_SMITHING_TEMPLATE, ItemID.RIB_ARMOR_TRIM_SMITHING_TEMPLATE));
    public static final TrimPattern COAST = register(new TrimPattern(TrimPatternNames.COAST, ItemFullNames.COAST_ARMOR_TRIM_SMITHING_TEMPLATE, ItemID.COAST_ARMOR_TRIM_SMITHING_TEMPLATE));
    public static final TrimPattern SHAPER = register(new TrimPattern(TrimPatternNames.SHAPER, ItemFullNames.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE, ItemID.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE));
    public static final TrimPattern EYE = register(new TrimPattern(TrimPatternNames.EYE, ItemFullNames.EYE_ARMOR_TRIM_SMITHING_TEMPLATE, ItemID.EYE_ARMOR_TRIM_SMITHING_TEMPLATE));
    public static final TrimPattern VEX = register(new TrimPattern(TrimPatternNames.VEX, ItemFullNames.VEX_ARMOR_TRIM_SMITHING_TEMPLATE, ItemID.VEX_ARMOR_TRIM_SMITHING_TEMPLATE));
    public static final TrimPattern SILENCE = register(new TrimPattern(TrimPatternNames.SILENCE, ItemFullNames.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, ItemID.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE));
    public static final TrimPattern WAYFINDER = register(new TrimPattern(TrimPatternNames.WAYFINDER, ItemFullNames.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE, ItemID.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE));
    /**
     * @since 1.21.50
     */
    public static final TrimPattern RAISER = register(new TrimPattern(TrimPatternNames.RAISER, ItemFullNames.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE, ItemID.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE), V1_21_50);
    public static final TrimPattern HOST = register(new TrimPattern(TrimPatternNames.HOST, ItemFullNames.HOST_ARMOR_TRIM_SMITHING_TEMPLATE, ItemID.HOST_ARMOR_TRIM_SMITHING_TEMPLATE));
    /**
     * @since 1.21.0
     */
    public static final TrimPattern FLOW = register(new TrimPattern(TrimPatternNames.FLOW, ItemFullNames.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE, ItemID.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE), V1_21_0);
    /**
     * @since 1.21.0
     */
    public static final TrimPattern BOLT = register(new TrimPattern(TrimPatternNames.BOLT, ItemFullNames.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE, ItemID.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE), V1_21_0);

    private static TrimPattern register(TrimPattern pattern) {
        REGISTRY.put(pattern.name(), pattern);
        return pattern;
    }

    /**
     * @param version min required base game version
     */
    private static TrimPattern register(TrimPattern pattern, GameVersion version) {
        if (!version.isAvailable()) {
            return null;
        }
        return register(pattern);
    }

    @Nullable
    public static TrimPattern get(String name) {
        return REGISTRY.get(name);
    }

    public static Map<String, TrimPattern> getRegistry() {
        return REGISTRY;
    }

    public static void registerVanillaTrimPatterns() {
    }

    private TrimPatterns() {
        throw new IllegalStateException();
    }
}
