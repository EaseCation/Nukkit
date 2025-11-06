package cn.nukkit.item.enchantment;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.item.Item;
import cn.nukkit.math.Mth;
import cn.nukkit.math.RandomSource;
import lombok.ToString;

import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public abstract class Enchantment implements Cloneable, EnchantmentID {
    public static final Enchantment[] EMPTY = new Enchantment[0];

    public static void init() {
        Enchantments.registerVanillaEnchantments();
    }

    public static Enchantment getEnchantment(int id) {
        return Enchantments.get(id);
    }

    public final int id;
    private final String identifier;
    private final Rarity rarity;
    public EnchantmentType type;

    protected int level = 1;

    protected final String name;

    protected Enchantment(int id, String identifier, String name, Rarity rarity, EnchantmentType type) {
        this.id = id;
        this.identifier = identifier;
        this.rarity = rarity;
        this.type = type;

        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public int getValidLevel() {
        return Mth.clamp(level, 0, getMaxLevel());
    }

    public Enchantment setLevel(int level) {
        return this.setLevel(level, true);
    }

    public Enchantment setLevel(int level, boolean safe) {
        if (!safe) {
            this.level = level;
            return this;
        }

        this.level = Mth.clamp(level, this.getMinLevel(), this.getMaxLevel());

        return this;
    }

    public int getId() {
        return id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Rarity getRarity() {
        return this.rarity;
    }

    /**
     * @deprecated use {@link Rarity#getWeight()} instead
     */
    @Deprecated
    public int getWeight() {
        return this.rarity.getWeight();
    }

    public int getMinLevel() {
        return 1;
    }

    public int getMaxLevel() {
        return 1;
    }

    public int getMaxEnchantableLevel() {
        return getMaxLevel();
    }

    public int getMinEnchantAbility(int level) {
        return 1 + level * 10;
    }

    public int getMaxEnchantAbility(int level) {
        return this.getMinEnchantAbility(level) + 5;
    }

    public int getProtectionFactor(EntityDamageEvent event) {
        return 0;
    }

    public float getDamageBonus(Entity attacker, Entity entity) {
        return 0;
    }

    public void doPostAttack(Item item, Entity attacker, Entity entity, @Nullable DamageCause cause) {

    }

    public void doAttack(Entity attacker, Entity entity) {

    }

    public void doPostHurt(Entity attacker, Entity entity) {

    }

    public final boolean isCompatibleWith(Enchantment enchantment) {
        return this.checkCompatibility(enchantment) && enchantment.checkCompatibility(this);
    }

    protected boolean checkCompatibility(Enchantment enchantment) {
        return this != enchantment;
    }

    public String getName() {
        return "%enchantment." + this.name;
    }

    public boolean canEnchant(Item item) {
        return this.type.canEnchantItem(item);
    }

    public boolean isMajor() {
        return false;
    }

    public boolean isCurse() {
        return false;
    }

    public boolean isTreasureOnly() {
        return false;
    }

    public boolean isLootable() {
        return true;
    }

    @Override
    public Enchantment clone() {
        try {
            return (Enchantment) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public static final String[] words = {"the", "elder", "scrolls", "klaatu", "berata", "niktu", "xyzzy", "bless", "curse", "light", "darkness", "fire", "air", "earth", "water", "hot", "dry", "cold", "wet", "ignite", "snuff", "embiggen", "twist", "shorten", "stretch", "fiddle", "destroy", "imbue", "galvanize", "enchant", "free", "limited", "range", "of", "towards", "inside", "sphere", "cube", "self", "other", "ball", "mental", "physical", "grow", "shrink", "demon", "elemental", "spirit", "animal", "creature", "beast", "humanoid", "undead", "fresh", "stale", "phnglui", "mglwnafh", "cthulhu", "rlyeh", "wgahnagl", "fhtagn", "baguette"};

    public static String getRandomName(RandomSource random) {
        StringBuilder name = new StringBuilder();
        int count = random.nextInt(3, 5);
        for (int i = 0; i < count; i++) {
            if (i != 0) {
                name.append(" ");
            }
            name.append(Enchantment.words[random.nextInt(Enchantment.words.length)]);
        }
        return name.toString();
    }

    public static String getRandomName() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        StringBuilder name = new StringBuilder();
        for (int i = random.nextInt(9, 16); i > 0; i--) {
            name.appendCodePoint(random.nextInt('a', 'z' + 1));
        }
        return name.toString();
    }

    static class UnknownEnchantment extends Enchantment {

        protected UnknownEnchantment(int id) {
            super(id, "unknown", "unknown", Rarity.VERY_RARE, EnchantmentType.UNKNOWN);
        }
    }

    public enum Rarity {
        COMMON(10),
        UNCOMMON(5),
        RARE(2),
        VERY_RARE(1);

        private final int weight;

        Rarity(int weight) {
            this.weight = weight;
        }

        public int getWeight() {
            return this.weight;
        }

        public static Rarity fromWeight(int weight) {
            if (weight < 2) {
                return VERY_RARE;
            } else if (weight < 5) {
                return RARE;
            } else if (weight < 10) {
                return UNCOMMON;
            }
            return COMMON;
        }
    }
}
