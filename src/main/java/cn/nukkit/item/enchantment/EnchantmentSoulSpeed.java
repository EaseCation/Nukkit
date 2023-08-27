package cn.nukkit.item.enchantment;

public class EnchantmentSoulSpeed extends Enchantment {

    protected EnchantmentSoulSpeed() {
        super(SOUL_SPEED, "soul_speed", "soul_speed", Rarity.VERY_RARE, EnchantmentType.ARMOR_FEET);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 10 * level;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    public float getSpeedBoost() {
        return getSpeedBoost(level);
    }

    public static float getSpeedBoost(int level) {
        return level * 0.0105f + 0.03f;
    }
}
