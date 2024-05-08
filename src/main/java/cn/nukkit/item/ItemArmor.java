package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.ByteTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class ItemArmor extends Item implements ItemDurable {

    public static final int TIER_LEATHER = 1;
    public static final int TIER_IRON = 2;
    public static final int TIER_CHAIN = 3;
    public static final int TIER_GOLD = 4;
    public static final int TIER_DIAMOND = 5;
    public static final int TIER_NETHERITE = 6;
    public static final int TIER_OTHER = 7;

    protected ItemArmor(int id) {
        super(id);
    }

    protected ItemArmor(int id, Integer meta) {
        super(id, meta);
    }

    protected ItemArmor(int id, Integer meta, int count) {
        super(id, meta, 1);
    }

    protected ItemArmor(int id, Integer meta, int count, String name) {
        super(id, meta, 1, name);
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
            default -> LevelSoundEventPacket.SOUND_ARMOR_EQUIP_GENERIC;
        };
    }
}
