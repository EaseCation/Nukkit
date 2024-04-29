package cn.nukkit.item.enchantment.mace;

import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.enchantment.EnchantmentType;

public abstract class EnchantmentMace extends Enchantment {
    protected EnchantmentMace(int id, String identifier, String name, Rarity rarity) {
        super(id, identifier, name, rarity, EnchantmentType.MACE);
    }
}
