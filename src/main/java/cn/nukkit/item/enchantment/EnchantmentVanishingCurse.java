package cn.nukkit.item.enchantment;

import cn.nukkit.item.Item;

public class EnchantmentVanishingCurse extends Enchantment {
    protected EnchantmentVanishingCurse() {
        super(VANISHING, "vanishing", "curse.vanishing", Rarity.VERY_RARE, EnchantmentType.BREAKABLE);
    }

    @Override
    public boolean canEnchant(Item item) {
        return item.getId() == Item.SKULL || item.getId() == Item.COMPASS || super.canEnchant(item);
    }

    @Override
    public boolean isCurse() {
        return true;
    }
}
