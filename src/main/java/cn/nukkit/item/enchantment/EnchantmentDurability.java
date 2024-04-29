package cn.nukkit.item.enchantment;

import cn.nukkit.item.Item;

import java.util.Random;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EnchantmentDurability extends Enchantment {
    protected EnchantmentDurability() {
        super(UNBREAKING, EnchantmentNames.UNBREAKING, "durability", Rarity.UNCOMMON, EnchantmentType.BREAKABLE);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 5 + (level - 1) * 8;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return super.getMinEnchantAbility(level) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    public static boolean negateDamage(Item item, int level, Random random) {
        return !(item.isArmor() && random.nextFloat() < 0.6f) && random.nextInt(level + 1) > 0;
    }
}
