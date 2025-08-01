package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.item.armortrim.TrimMaterialNames;
import cn.nukkit.item.armortrim.TrimPatternNames;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.ByteTag;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import it.unimi.dsi.fastutil.Pair;

import javax.annotation.Nullable;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class ItemArmor extends Item implements ItemDurable {

    public static final int TIER_LEATHER = 1;
    public static final int TIER_COPPER = 2;
    public static final int TIER_GOLD = 3;
    public static final int TIER_CHAIN = 4;
    public static final int TIER_IRON = 5;
    public static final int TIER_DIAMOND = 6;
    public static final int TIER_NETHERITE = 7;
    public static final int TIER_OTHER = 8;

    protected ItemArmor(int id) {
        super(id);
    }

    protected ItemArmor(int id, Integer meta) {
        super(id, meta);
    }

    protected ItemArmor(int id, Integer meta, int count) {
        super(id, meta, count);
    }

    protected ItemArmor(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean isArmor() {
        return true;
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        boolean equip = false;
        Item oldSlotItem;
        if (this.isHelmet()) {
            oldSlotItem = player.getArmorInventory().getHelmet();
            if (!oldSlotItem.hasEnchantment(Enchantment.BINDING) && player.getArmorInventory().setHelmet(this)) {
                equip = true;
            }
        } else if (this.isChestplate()) {
            oldSlotItem = player.getArmorInventory().getChestplate();
            if (!oldSlotItem.hasEnchantment(Enchantment.BINDING) && player.getArmorInventory().setChestplate(this)) {
                equip = true;
            }
        } else if (this.isLeggings()) {
            oldSlotItem = player.getArmorInventory().getLeggings();
            if (!oldSlotItem.hasEnchantment(Enchantment.BINDING) && player.getArmorInventory().setLeggings(this)) {
                equip = true;
            }
        } else if (this.isBoots()) {
            oldSlotItem = player.getArmorInventory().getBoots();
            if (!oldSlotItem.hasEnchantment(Enchantment.BINDING) && player.getArmorInventory().setBoots(this)) {
                equip = true;
            }
        } else {
            oldSlotItem = Items.air();
        }
        if (equip) {
            player.getInventory().setItem(player.getInventory().getHeldItemIndex(), oldSlotItem);

//            player.getLevel().addLevelSoundEvent(player, getEquippingSound());
        }

        return this.getCount() == 0;
    }

    @Override
    public int getEnchantAbility() {
        switch (this.getTier()) {
            case TIER_CHAIN:
                return 12;
            case TIER_LEATHER:
            case TIER_NETHERITE:
                return 15;
            case TIER_DIAMOND:
                return 10;
            case TIER_GOLD:
                return 25;
            case TIER_IRON:
            case TIER_OTHER:
                return 9;
            case TIER_COPPER:
                return 8;
        }

        return 0;
    }

    @Override
    public boolean isUnbreakable() {
        Tag tag = this.getNamedTagEntry("Unbreakable");
        return tag instanceof ByteTag && ((ByteTag) tag).data > 0;
    }

    @Override
    public int getDamageChance(int unbreaking) {
        return 40 / (unbreaking + 1) + 60;
    }

    @Override
    public int getEquippingSound() {
        return switch (getTier()) {
            case TIER_CHAIN -> LevelSoundEventPacket.SOUND_ARMOR_EQUIP_CHAIN;
            case TIER_DIAMOND -> LevelSoundEventPacket.SOUND_ARMOR_EQUIP_DIAMOND;
            case TIER_GOLD -> LevelSoundEventPacket.SOUND_ARMOR_EQUIP_GOLD;
            case TIER_IRON -> LevelSoundEventPacket.SOUND_ARMOR_EQUIP_IRON;
            case TIER_LEATHER -> LevelSoundEventPacket.SOUND_ARMOR_EQUIP_LEATHER;
            case TIER_NETHERITE -> LevelSoundEventPacket.SOUND_ARMOR_EQUIP_NETHERITE;
            case TIER_COPPER -> LevelSoundEventPacket.SOUND_ARMOR_EQUIP_COPPER;
            default -> LevelSoundEventPacket.SOUND_ARMOR_EQUIP_GENERIC;
        };
    }

    /**
     * @see TrimPatternNames
     * @see TrimMaterialNames
     */
    public ItemArmor setTrim(String pattern, String material) {
        setNamedTag(getOrCreateNamedTag().putCompound("Trim", new CompoundTag()
                .putString("Pattern", pattern)
                .putString("Material", material)));
        return this;
    }

    /**
     * @return pattern and material
     */
    @Nullable
    public Pair<String, String> getTrim() {
        CompoundTag nbt = getNamedTag();
        if (nbt == null) {
            return null;
        }
        CompoundTag trim = nbt.getCompound("Trim", null);
        if (trim == null) {
            return null;
        }
        return Pair.of(trim.getString("Pattern"), trim.getString("Material"));
    }

    @Nullable
    public String getTrimPattern() {
        CompoundTag nbt = getNamedTag();
        if (nbt == null) {
            return null;
        }
        CompoundTag trim = nbt.getCompound("Trim", null);
        if (trim == null) {
            return null;
        }
        return trim.getString("Pattern", null);
    }

    @Nullable
    public String getTrimMaterial() {
        CompoundTag nbt = getNamedTag();
        if (nbt == null) {
            return null;
        }
        CompoundTag trim = nbt.getCompound("Trim", null);
        if (trim == null) {
            return null;
        }
        return trim.getString("Material", null);
    }

    public boolean clearTrim() {
        CompoundTag nbt = getNamedTag();
        if (nbt == null) {
            return false;
        }
        if (nbt.remove("Trim") == null) {
            return false;
        }
        setNamedTag(nbt);
        return true;
    }
}
