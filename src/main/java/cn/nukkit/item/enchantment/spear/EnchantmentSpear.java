package cn.nukkit.item.enchantment.spear;

import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.enchantment.EnchantmentType;

public abstract class EnchantmentSpear extends Enchantment {
    protected EnchantmentSpear(int id, String identifier, String name, Rarity rarity) {
        super(id, identifier, name, rarity, EnchantmentType.SPEAR);
    }
}
