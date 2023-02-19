package cn.nukkit.item.enchantment.trident;

import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.enchantment.EnchantmentType;

public abstract class EnchantmentTrident extends Enchantment {
    protected EnchantmentTrident(int id, String identifier, String name, Rarity rarity) {
        super(id, identifier, name, rarity, EnchantmentType.TRIDENT);
    }
}
