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
	;

    public boolean canEnchantItem(Item item) {
        if (this == UNKNOWN) {
            return true;
        }
        if (this == ALL && (item.getMaxDurability() >= 0 || item.is(Item.SKULL) || item.is(ItemBlockID.CARVED_PUMPKIN) || item.is(Item.COMPASS) || item.is(Item.LODESTONE_COMPASS) || item.is(Item.RECOVERY_COMPASS))) {
            return true;
        }
        if (this == BREAKABLE && item.getMaxDurability() >= 0) {
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
                    return item.isSword();
                case DIGGER:
                    return item.isPickaxe() || item.isShovel() || item.isAxe() || item.isHoe();
                case BOW:
                    return item instanceof ItemBow;
                case FISHING_ROD:
                    return item instanceof ItemFishingRod;
                case WEARABLE:
                    return item instanceof ItemSkull || item.getId() == ItemBlockID.CARVED_PUMPKIN;
                case TRIDENT:
                    return item instanceof ItemTrident;
                case CROSSBOW:
                    return item instanceof ItemCrossbow;
            }
        }
        return false;
    }
}
