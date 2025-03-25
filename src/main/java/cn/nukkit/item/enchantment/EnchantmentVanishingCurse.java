package cn.nukkit.item.enchantment;

public class EnchantmentVanishingCurse extends Enchantment {
    protected EnchantmentVanishingCurse() {
        super(VANISHING, EnchantmentNames.VANISHING, "curse.vanishing", Rarity.VERY_RARE, EnchantmentType.ALL);
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
