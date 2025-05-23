package cn.nukkit.item.enchantment;

/**
 * @author Rover656
 */
public class EnchantmentMending extends Enchantment {
    protected EnchantmentMending() {
        super(MENDING, EnchantmentNames.MENDING, "mending", Rarity.RARE, EnchantmentType.BREAKABLE);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 25 * level;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return this.getMinEnchantAbility(level) + 50;
    }

    @Override
    public boolean checkCompatibility(Enchantment enchantment) {
        return super.checkCompatibility(enchantment) && enchantment.id != INFINITY;
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }
}
