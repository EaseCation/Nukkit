package cn.nukkit.item.enchantment;

public class EnchantmentFrostWalker extends Enchantment {
    protected EnchantmentFrostWalker() {
        super(FROST_WALKER, "frost_walker", "frostwalker", Rarity.RARE, EnchantmentType.ARMOR_FEET);
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return this.getMinEnchantAbility(level) + 15;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }
}
