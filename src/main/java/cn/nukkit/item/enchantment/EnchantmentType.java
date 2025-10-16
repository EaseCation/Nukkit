package cn.nukkit.item.enchantment;

import cn.nukkit.item.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public enum EnchantmentType {
    UNKNOWN,
    ALL,
    ARMOR,
    ARMOR_HEAD,
    ARMOR_TORSO,
    ARMOR_LEGS,
    ARMOR_FEET,
    SWORD,
    DIGGER,
    FISHING_ROD,
    BREAKABLE,
    BOW,
    WEARABLE,
    TRIDENT,
    CROSSBOW,
    MACE,
    MELEE_WEAPON,
    SPEAR,
	;

    public boolean canEnchantItem(Item item) {
        if (this == UNKNOWN) {
            return true;
        }
        if (this == ALL && (item.getMaxDurability() >= 0 || item.is(Item.SKULL) || item.is(ItemBlockID.CARVED_PUMPKIN) || item.is(Item.COMPASS) || item.is(Item.LODESTONE_COMPASS) || item.is(Item.RECOVERY_COMPASS)) && !item.is(Item.WOLF_ARMOR)) {
            return true;
        }
        if (this == BREAKABLE && item.getMaxDurability() >= 0 && !item.is(Item.WOLF_ARMOR)) {
            return true;
        }
        if (item instanceof ItemArmor) {
            if (this == ARMOR || this == WEARABLE) {
                return true;
            }

            switch (this) {
                case ARMOR_HEAD:
                    return item.isHelmet();
                case ARMOR_TORSO:
                    return item.isChestplate();
                case ARMOR_LEGS:
                    return item.isLeggings();
                case ARMOR_FEET:
                    return item.isBoots();
            }
        } else {
            switch (this) {
                case SWORD:
                    return !item.is(Item.MACE) && (item.isSword() || item.isSpear());
                case DIGGER:
                    return item.isPickaxe() || item.isShovel() || item.isAxe() || item.isHoe();
                case BOW:
                    return item.is(Item.BOW);
                case FISHING_ROD:
                    return item.is(Item.FISHING_ROD);
                case WEARABLE:
                    return item.is(Item.SKULL) || item.is(ItemBlockID.CARVED_PUMPKIN);
                case TRIDENT:
                    return item.is(Item.TRIDENT);
                case CROSSBOW:
                    return item.is(Item.CROSSBOW);
                case MACE:
                    return item.is(Item.MACE);
                case SPEAR:
                    return item.isSpear();
                case MELEE_WEAPON:
                    return item.isSword() || item.isSpear() || item.is(Item.MACE);
            }
        }
        return false;
    }
}
