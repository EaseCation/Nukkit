package cn.nukkit.item.enchantment;

public class EnchantmentVanishingCurse extends Enchantment {
    protected EnchantmentVanishingCurse() {
        super(VANISHING, EnchantmentNames.VANISHING, "curse.vanishing", Rarity.VERY_RARE, EnchantmentType.ALL);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 25;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return 50;
    }

    @Override
    public boolean isCurse() {
        return true;
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }
}
