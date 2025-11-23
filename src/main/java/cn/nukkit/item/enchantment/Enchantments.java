package cn.nukkit.item.enchantment;

import cn.nukkit.GameVersion;
import cn.nukkit.item.enchantment.Enchantment.UnknownEnchantment;
import cn.nukkit.item.enchantment.bow.*;
import cn.nukkit.item.enchantment.crossbow.*;
import cn.nukkit.item.enchantment.damage.*;
import cn.nukkit.item.enchantment.loot.*;
import cn.nukkit.item.enchantment.mace.*;
import cn.nukkit.item.enchantment.protection.*;
import cn.nukkit.item.enchantment.spear.*;
import cn.nukkit.item.enchantment.trident.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cn.nukkit.GameVersion.*;
import static cn.nukkit.item.enchantment.EnchantmentID.*;

public final class Enchantments {
    private static final Enchantment[] ENCHANTMENTS = new Enchantment[256];
    private static final Map<String, Enchantment> IDENTIFIER_TO_ENCHANTMENT = new Object2ObjectOpenHashMap<>();
    private static final List<Enchantment> LOOTABLE_ENCHANTMENTS = new ArrayList<>();

    public static void registerVanillaEnchantments() {
        registerEnchantment(PROTECTION, new EnchantmentProtectionAll());
        registerEnchantment(FIRE_PROTECTION, new EnchantmentProtectionFire());
        registerEnchantment(FEATHER_FALLING, new EnchantmentProtectionFall());
        registerEnchantment(BLAST_PROTECTION, new EnchantmentProtectionExplosion());
        registerEnchantment(PROJECTILE_PROTECTION, new EnchantmentProtectionProjectile());
        registerEnchantment(THORNS, new EnchantmentThorns());
        registerEnchantment(RESPIRATION, new EnchantmentWaterBreath());
        registerEnchantment(AQUA_AFFINITY, new EnchantmentWaterWorker());
        registerEnchantment(DEPTH_STRIDER, new EnchantmentWaterWalker());
        registerEnchantment(SHARPNESS, new EnchantmentDamageAll());
        registerEnchantment(SMITE, new EnchantmentDamageSmite());
        registerEnchantment(BANE_OF_ARTHROPODS, new EnchantmentDamageArthropods());
        registerEnchantment(KNOCKBACK, new EnchantmentKnockback());
        registerEnchantment(FIRE_ASPECT, new EnchantmentFireAspect());
        registerEnchantment(LOOTING, new EnchantmentLootWeapon());
        registerEnchantment(EFFICIENCY, new EnchantmentEfficiency());
        registerEnchantment(SILK_TOUCH, new EnchantmentSilkTouch());
        registerEnchantment(UNBREAKING, new EnchantmentDurability());
        registerEnchantment(FORTUNE, new EnchantmentLootDigging());
        registerEnchantment(POWER, new EnchantmentBowPower());
        registerEnchantment(PUNCH, new EnchantmentBowKnockback());
        registerEnchantment(FLAME, new EnchantmentBowFlame());
        registerEnchantment(INFINITY, new EnchantmentBowInfinity());
        registerEnchantment(LUCK_OF_THE_SEA, new EnchantmentLootFishing());
        registerEnchantment(LURE, new EnchantmentLure());
        registerEnchantment(FROST_WALKER, new EnchantmentFrostWalker());
        registerEnchantment(MENDING, new EnchantmentMending());
        registerEnchantment(BINDING, new EnchantmentBindingCurse());
        registerEnchantment(VANISHING, new EnchantmentVanishingCurse());

        registerEnchantment(IMPALING, new EnchantmentTridentImpaling(), V1_4_0);
        registerEnchantment(RIPTIDE, new EnchantmentTridentRiptide(), V1_4_0);
        registerEnchantment(LOYALTY, new EnchantmentTridentLoyalty(), V1_4_0);
        registerEnchantment(CHANNELING, new EnchantmentTridentChanneling(), V1_4_0);

        registerEnchantment(MULTISHOT, new EnchantmentCrossbowMultishot(), V1_10_0);
        registerEnchantment(PIERCING, new EnchantmentCrossbowPiercing(), V1_10_0);
        registerEnchantment(QUICK_CHARGE, new EnchantmentCrossbowQuickCharge(), V1_10_0);

        registerEnchantment(SOUL_SPEED, new EnchantmentSoulSpeed(), V1_16_0);

        registerEnchantment(SWIFT_SNEAK, new EnchantmentSwiftSneak(), V1_19_0);

        registerEnchantment(WIND_BURST, new EnchantmentMaceWindBurst(), V1_21_0);
        registerEnchantment(DENSITY, new EnchantmentMaceDensity(), V1_21_0);
        registerEnchantment(BREACH, new EnchantmentMaceBreach(), V1_21_0);

        registerEnchantment(LUNGE, new EnchantmentSpearLunge(), V1_21_130);

    }

    private static Enchantment registerEnchantment(int id, Enchantment enchantment) {
        if (ENCHANTMENTS[id] == null) {
            ENCHANTMENTS[id] = enchantment;
        }
        IDENTIFIER_TO_ENCHANTMENT.put(enchantment.getIdentifier(), enchantment);
        if (enchantment.isLootable()) {
            LOOTABLE_ENCHANTMENTS.add(enchantment);
        }
        return ENCHANTMENTS[id];
    }

    /**
     * @param version min required base game version
     */
    private static Enchantment registerEnchantment(int id, Enchantment enchantment, GameVersion version) {
        if (!version.isAvailable()) {
            return null;
        }
        return registerEnchantment(id, enchantment);
    }

    public static Enchantment get(int id) {
        Enchantment enchantment = null;
        if (id >= 0 && id < ENCHANTMENTS.length) {
            enchantment = ENCHANTMENTS[id];
        }
        if (enchantment == null) {
            return new UnknownEnchantment(id);
        }
        return enchantment.clone();
    }

    public static Enchantment getUnsafe(int id) {
        Enchantment enchantment = null;
        if (id >= 0 && id < ENCHANTMENTS.length) {
            enchantment = ENCHANTMENTS[id];
        }
        if (enchantment == null) {
            return new UnknownEnchantment(id);
        }
        return enchantment;
    }

    @Nullable
    public static Enchantment getEnchantmentByIdentifier(String identifier) {
        return getEnchantmentByIdentifier(identifier, true);
    }

    @Nullable
    public static Enchantment getEnchantmentByIdentifier(String identifier, boolean namespaced) {
        return getEnchantmentByIdentifier(identifier, namespaced, false);
    }

    @Nullable
    public static Enchantment getEnchantmentByIdentifier(String identifier, boolean namespaced, boolean allowUnknown) {
        if (namespaced && identifier.startsWith("minecraft:")) {
            identifier = identifier.substring(10);
        }

        Enchantment enchantment = IDENTIFIER_TO_ENCHANTMENT.get(identifier);
        if (enchantment == null) {
            if (!allowUnknown) {
                return null;
            }
            return new UnknownEnchantment(Byte.MAX_VALUE);
        }
        return enchantment.clone();
    }

    public static Map<String, Enchantment> getEnchantments() {
        return IDENTIFIER_TO_ENCHANTMENT;
    }

    public static List<Enchantment> getLootableEnchantments() {
        return LOOTABLE_ENCHANTMENTS;
    }

    private Enchantments() {
        throw new IllegalStateException();
    }
}
