package cn.nukkit.item.enchantment;

public class EnchantmentFrostWalker extends Enchantment {
    protected EnchantmentFrostWalker() {
        super(FROST_WALKER, EnchantmentNames.FROST_WALKER, "frostwalker", Rarity.RARE, EnchantmentType.ARMOR_FEET);
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return this.getMinEnchantAbility(level) + 15;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    @Override
    public boolean checkCompatibility(Enchantment enchantment) {
        return super.checkCompatibility(enchantment) && enchantment.id != DEPTH_STRIDER;
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }
}
