package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.nbt.tag.ByteTag;
import cn.nukkit.nbt.tag.Tag;

import java.util.concurrent.ThreadLocalRandom;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class ItemTool extends Item implements ItemDurable {
    public static final int TIER_WOODEN = 1;
    public static final int TIER_GOLD = 2;
    public static final int TIER_STONE = 3;
    public static final int TIER_IRON = 4;
    public static final int TIER_DIAMOND = 5;
    public static final int TIER_NETHERITE = 6;

    public static final int TYPE_NONE = 0;
    public static final int TYPE_SWORD = 1;
    public static final int TYPE_SHOVEL = 2;
    public static final int TYPE_PICKAXE = 3;
    public static final int TYPE_AXE = 4;
    public static final int TYPE_SHEARS = 5;
    public static final int TYPE_HOE = 6;

    public static final int DURABILITY_WOODEN = 59;
    public static final int DURABILITY_GOLD = 32;
    public static final int DURABILITY_STONE = 131;
    public static final int DURABILITY_IRON = 250;
    public static final int DURABILITY_DIAMOND = 1561;
    public static final int DURABILITY_NETHERITE = 2031;
    public static final int DURABILITY_FLINT_AND_STEEL = 64;
    public static final int DURABILITY_SHEARS = 238;
    public static final int DURABILITY_BOW = 384;
    public static final int DURABILITY_TRIDENT = 250;
    public static final int DURABILITY_FISHING_ROD = 384;
    public static final int DURABILITY_CROSSBOW = 464;
    public static final int DURABILITY_SHIELD = 336;
    public static final int DURABILITY_CARROT_ON_A_STICK = 26;
    public static final int DURABILITY_WARPED_FUNGUS_ON_A_STICK = 100;
    public static final int DURABILITY_SPARKLER = 100;
    public static final int DURABILITY_GLOW_STICK = 100;

    protected ItemTool(int id) {
        this(id, 0, 1, UNKNOWN_STR);
    }

    protected ItemTool(int id, Integer meta) {
        this(id, meta, 1, UNKNOWN_STR);
    }

    protected ItemTool(int id, Integer meta, int count) {
        this(id, meta, count, UNKNOWN_STR);
    }

    protected ItemTool(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean useOn(Block block) {
        if (this.isUnbreakable() || isDurable() || noDamageOnBreak()) {
            return true;
        }

        if (this.additionalDamageOnBreak()) {
            if (block.getBreakTime(this) > 0) {
                this.setDamage(this.getDamage() + 2);
            }
        } else if (this.isShears() || block.getBreakTime(this) > 0) {
            this.setDamage(this.getDamage() + 1);
        }
        return true;
    }

    @Override
    public boolean useOn(Entity entity) {
        if (this.isUnbreakable() || isDurable() || noDamageOnAttack()) {
            return true;
        }

        if (entity != null && !this.isSword()) {
            this.setDamage(this.getDamage() + 2);
        } else {
            this.setDamage(this.getDamage() + 1);
        }

        return true;
    }

    protected boolean isDurable() {
        if (!hasEnchantments()) {
            return false;
        }

        Enchantment durability = getEnchantment(Enchantment.UNBREAKING);
        return durability != null && durability.getLevel() > 0 && ThreadLocalRandom.current().nextInt(100) >= getDamageChance(durability.getLevel());
    }

    @Override
    public boolean isUnbreakable() {
        Tag tag = this.getNamedTagEntry("Unbreakable");
        return tag instanceof ByteTag && ((ByteTag) tag).data > 0;
    }

    @Override
    public boolean isTool() {
        return true;
    }

    @Override
    public int getEnchantAbility() {
        switch (this.getTier()) {
            case TIER_STONE:
                return 5;
            case TIER_WOODEN:
                return 15;
            case TIER_DIAMOND:
                return 10;
            case TIER_GOLD:
                return 22;
            case TIER_IRON:
                return 14;
        }

        return 0;
    }

    /**
     * No damage to item when it's used to attack entities
     * @return whether the item should take damage when used to attack entities
     */
    public boolean noDamageOnAttack() {
        return false;
    }

    /**
     * No damage to item when it's used to break blocks
     * @return whether the item should take damage when used to break blocks
     */
    public boolean noDamageOnBreak() {
        return false;
    }

    public boolean additionalDamageOnBreak() {
        return false;
    }

    public static ItemTool getUniversalTool() {
        return UniversalTool.INSTANCE;
    }

    /**
     * Internal item.
     */
    private static class UniversalTool extends ItemTool {

        private static final UniversalTool INSTANCE = new UniversalTool();

        public UniversalTool() {
            super(AIR, 0, 0);
        }

        @Override
        public boolean isUnbreakable() {
            return true;
        }

        @Override
        public int getTier() {
            return ItemTool.TIER_DIAMOND;
        }

        @Override
        public boolean isPickaxe() {
            return true;
        }

        @Override
        public boolean isSword() {
            return true;
        }

        @Override
        public boolean isShovel() {
            return true;
        }
    }
}
