package cn.nukkit.item.armortrim;

import cn.nukkit.GameVersion;
import cn.nukkit.item.ItemDye;
import cn.nukkit.item.ItemFullNames;
import cn.nukkit.item.ItemID;
import cn.nukkit.utils.TextFormat;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

import static cn.nukkit.GameVersion.*;

public final class TrimMaterials {
    private static final Map<String, TrimMaterial> REGISTRY = new HashMap<>();

    public static final TrimMaterial QUARTZ = register(new TrimMaterial(TrimMaterialNames.QUARTZ, TextFormat.MATERIAL_QUARTZ, ItemFullNames.QUARTZ, ItemID.QUARTZ, 0));
    public static final TrimMaterial IRON = register(new TrimMaterial(TrimMaterialNames.IRON, TextFormat.MATERIAL_IRON, ItemFullNames.IRON_INGOT, ItemID.IRON_INGOT, 0));
    public static final TrimMaterial NETHERITE = register(new TrimMaterial(TrimMaterialNames.NETHERITE, TextFormat.MATERIAL_NETHERITE, ItemFullNames.NETHERITE_INGOT, ItemID.NETHERITE_INGOT, 0));
    public static final TrimMaterial REDSTONE = register(new TrimMaterial(TrimMaterialNames.REDSTONE, TextFormat.MATERIAL_REDSTONE, ItemFullNames.REDSTONE, ItemID.REDSTONE, 0));
    public static final TrimMaterial GOLD = register(new TrimMaterial(TrimMaterialNames.GOLD, TextFormat.MATERIAL_GOLD, ItemFullNames.GOLD_INGOT, ItemID.GOLD_INGOT, 0));
    public static final TrimMaterial COPPER = register(new TrimMaterial(TrimMaterialNames.COPPER, TextFormat.MATERIAL_COPPER, ItemFullNames.COPPER_INGOT, ItemID.COPPER_INGOT, 0));
    public static final TrimMaterial EMERALD = register(new TrimMaterial(TrimMaterialNames.EMERALD, TextFormat.MATERIAL_EMERALD, ItemFullNames.EMERALD, ItemID.EMERALD, 0));
    public static final TrimMaterial DIAMOND = register(new TrimMaterial(TrimMaterialNames.DIAMOND, TextFormat.MATERIAL_DIAMOND, ItemFullNames.DIAMOND, ItemID.DIAMOND, 0));
    public static final TrimMaterial LAPIS = register(new TrimMaterial(TrimMaterialNames.LAPIS, TextFormat.MATERIAL_LAPIS, ItemFullNames.LAPIS_LAZULI, ItemID.DYE, ItemDye.LAPIS_LAZULI));
    public static final TrimMaterial AMETHYST = register(new TrimMaterial(TrimMaterialNames.AMETHYST, TextFormat.MATERIAL_AMETHYST, ItemFullNames.AMETHYST_SHARD, ItemID.AMETHYST_SHARD, 0));
    /**
     * @since 1.21.50
     */
    public static final TrimMaterial RESIN = register(new TrimMaterial(TrimMaterialNames.RESIN, TextFormat.MATERIAL_RESIN, ItemFullNames.RESIN_BRICK, ItemID.RESIN_BRICK, 0), V1_21_50);

    private static TrimMaterial register(TrimMaterial material) {
        REGISTRY.put(material.name(), material);
        return material;
    }

    /**
     * @param version min required base game version
     */
    private static TrimMaterial register(TrimMaterial material, GameVersion version) {
        if (!version.isAvailable()) {
            return null;
        }
        return register(material);
    }

    @Nullable
    public static TrimMaterial get(String name) {
        return REGISTRY.get(name);
    }

    public static Map<String, TrimMaterial> getRegistry() {
        return REGISTRY;
    }

    public static void registerVanillaTrimMaterials() {
    }

    private TrimMaterials() {
        throw new IllegalStateException();
    }
}
