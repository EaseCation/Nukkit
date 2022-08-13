package cn.nukkit.item.enchantment;

import cn.nukkit.GameVersion;
import cn.nukkit.item.enchantment.crossbow.EnchantmentCrossbowMultishot;
import cn.nukkit.item.enchantment.crossbow.EnchantmentCrossbowPiercing;
import cn.nukkit.item.enchantment.crossbow.EnchantmentCrossbowQuickCharge;
import cn.nukkit.item.enchantment.trident.*;

import static cn.nukkit.GameVersion.*;
import static cn.nukkit.item.enchantment.EnchantmentID.*;

public final class Enchantments {

    public static void registerVanillaEnchantments() {
        registerEnchantment(ID_IMPALING, new EnchantmentTridentImpaling(), V1_4_0);
        registerEnchantment(ID_RIPTIDE, new EnchantmentTridentRiptide(), V1_4_0);
        registerEnchantment(ID_LOYALTY, new EnchantmentTridentLoyalty(), V1_4_0);
        registerEnchantment(ID_CHANNELING, new EnchantmentTridentChanneling(), V1_4_0);

        registerEnchantment(ID_MULTISHOT, new EnchantmentCrossbowMultishot(), V1_10_0);
        registerEnchantment(ID_PIERCING, new EnchantmentCrossbowPiercing(), V1_10_0);
        registerEnchantment(ID_QUICK_CHARGE, new EnchantmentCrossbowQuickCharge(), V1_10_0);

        registerEnchantment(ID_SOUL_SPEED, new EnchantmentSoulSpeed(), V1_16_0);

        registerEnchantment(ID_SWIFT_SNEAK, new EnchantmentSwiftSneak(), V1_19_0);

    }

    private static Enchantment registerEnchantment(int id, Enchantment enchantment) {
        if (Enchantment.enchantments[id] == null) {
            Enchantment.enchantments[id] = enchantment;
        }
        return Enchantment.enchantments[id];
    }

    /**
     * @param version min required base game version
     */
    private static Enchantment registerEnchantment(int id, Enchantment enchantment, GameVersion version) {
        if (!version.isAvailable()) {
//            return null;
        }
        return registerEnchantment(id, enchantment);
    }

    private Enchantments() {
        throw new IllegalStateException();
    }
}
