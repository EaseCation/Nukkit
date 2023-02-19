package cn.nukkit.item.enchantment.trident;

import cn.nukkit.item.enchantment.Enchantment;

public class EnchantmentTridentLoyalty extends EnchantmentTrident {
    public EnchantmentTridentLoyalty() {
        super(Enchantment.LOYALTY, "loyalty", "tridentLoyalty", Rarity.UNCOMMON);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 7 * level + 5;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}
