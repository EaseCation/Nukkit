package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityDamageEvent.DamageModifier;
import cn.nukkit.inventory.*;
import cn.nukkit.item.Item;
import cn.nukkit.item.Items;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Mth;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.potion.Effect;
import it.unimi.dsi.fastutil.objects.ObjectIntPair;

import java.util.ArrayList;
import java.util.List;

public abstract class EntityHumanType extends EntityCreature implements InventoryHolder {

    protected ArmorInventory armorInventory; //TODO: move to Mob

    protected PlayerInventory inventory;
    protected PlayerEnderChestInventory enderChestInventory;
    protected PlayerOffhandInventory offhandInventory;

    public EntityHumanType(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    public ArmorInventory getArmorInventory() {
        return armorInventory;
    }

    @Override
    public PlayerInventory getInventory() {
        return inventory;
    }

    public PlayerEnderChestInventory getEnderChestInventory() {
        return enderChestInventory;
    }

    public PlayerOffhandInventory getOffhandInventory() {
        return offhandInventory;
    }

    @Override
    protected void initEntity() {
        this.armorInventory = new ArmorInventory(this);
        this.offhandInventory = new PlayerOffhandInventory(this);
        this.inventory = new PlayerInventory(this);

        if (this.namedTag.contains("Inventory") && this.namedTag.get("Inventory") instanceof ListTag) {
            ListTag<CompoundTag> inventoryList = this.namedTag.getList("Inventory", CompoundTag.class);
            for (CompoundTag item : inventoryList.getAll()) {
                int slot = item.getByte("Slot");
                if (slot >= 0 && slot < 9) { //hotbar
                    //Old hotbar saving stuff, remove it (useless now)
                    inventoryList.remove(item);
                } else if (slot >= 100 && slot < 104) {
                    this.armorInventory.setItem(slot - 100, NBTIO.getItemHelper(item));
                } else if (slot == -106) {
                    this.offhandInventory.setItem(0, NBTIO.getItemHelper(item));
                } else {
                    this.inventory.setItem(slot - 9, NBTIO.getItemHelper(item));
                }
            }
        }

        this.enderChestInventory = new PlayerEnderChestInventory(this);

        if (this.namedTag.contains("EnderItems") && this.namedTag.get("EnderItems") instanceof ListTag) {
            ListTag<CompoundTag> inventoryList = this.namedTag.getList("EnderItems", CompoundTag.class);
            for (CompoundTag item : inventoryList.getAll()) {
                this.enderChestInventory.setItem(item.getByte("Slot"), NBTIO.getItemHelper(item));
            }
        }

        super.initEntity();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        ListTag<CompoundTag> inventoryTag = null;
        if (this.inventory != null) {
            inventoryTag = new ListTag<>("Inventory");
            this.namedTag.putList(inventoryTag);

/*
            for (int slot = 0; slot < 9; ++slot) {
                inventoryTag.add(new CompoundTag()
                        .putByte("Count", 0)
                        .putShort("Damage", 0)
                        .putByte("Slot", slot)
                        .putByte("TrueSlot", -1)
                        .putShort("id", 0)
                );
            }
*/

            int slotCount = Player.SURVIVAL_SLOTS + 9;
            for (int slot = 9; slot < slotCount; ++slot) {
                Item item = this.inventory.getItem(slot - 9);
                if (item.isNull()) {
                    continue;
                }
                inventoryTag.add(NBTIO.putItemHelper(item, slot));
            }

            for (int slot = 0; slot < 4; ++slot) {
                Item item = this.armorInventory.getItem(slot);
                if (!item.isNull()) {
                    inventoryTag.add(NBTIO.putItemHelper(item, slot + 100));
                }
            }
        }

        if (this.offhandInventory != null) {
            Item item = this.offhandInventory.getItem(0);
            if (!item.isNull()) {
                if (inventoryTag == null) {
                    inventoryTag = new ListTag<>("Inventory");
                    this.namedTag.putList(inventoryTag);
                }
                inventoryTag.add(NBTIO.putItemHelper(item, -106));
            }
        }

        this.namedTag.putList(new ListTag<CompoundTag>("EnderItems"));
        if (this.enderChestInventory != null) {
            for (int slot = 0; slot < 27; ++slot) {
                Item item = this.enderChestInventory.getItem(slot);
                if (!item.isNull()) {
                    this.namedTag.getList("EnderItems", CompoundTag.class).add(NBTIO.putItemHelper(item, slot));
                }
            }
        }
    }

    @Override
    public Item[] getDrops() {
        if (this.inventory != null) {
            List<Item> drops = new ArrayList<>(this.inventory.getContents().values());
            drops.addAll(this.armorInventory.getContents().values());
            drops.addAll(this.offhandInventory.getContents().values());
            return drops.toArray(new Item[0]);
        }
        return new Item[0];
    }

    @Override
    protected boolean damageEntity0(EntityDamageEvent source) {
        if (this.isClosed() || !this.isAlive()) {
            return false;
        }

        int armorDamage = 0;

        if (source.getCause() != DamageCause.VOID && source.getCause() != DamageCause.SUICIDE && source.getCause() != DamageCause.CUSTOM && source.getCause() != DamageCause.SONIC_BOOM && source.getCause() != DamageCause.HUNGER) {
            int breach = 0;
            if (source instanceof EntityDamageByEntityEvent damageByEntityEvent) {
                Enchantment[] enchantments = damageByEntityEvent.getWeaponEnchantments();
                if (enchantments != null) {
                    for (Enchantment enchantment : enchantments) {
                        if (enchantment.getId() == Enchantment.BREACH) {
                            breach = enchantment.getLevel();
                            break;
                        }
                    }
                }
            }

            int armorPoints = getBaseArmorValue();
            int toughness = 0;
            int epf = 0;

            for (Item armor : armorInventory.getContents().values()) {
                armorPoints += armor.getArmorPoints();
                toughness += armor.getToughness();
                epf += calculateEnchantmentProtectionFactor(armor, source);
            }

            if (source.canBeReducedByArmor()) {
                armorDamage = Math.max(1, (int) source.getDamage() / 4);

                armorPoints = Mth.clamp(armorPoints, 0, 20);
                float damage = source.getFinalDamage();
                float armorFraction;
                if (level.isNewArmorMechanics()) {
                    armorFraction = Mth.clamp(armorPoints - damage / (2 + toughness / 4f), armorPoints * 0.2f, 20) / 25;
                } else {
                    armorFraction = armorPoints / 25f;
                }
                if (breach > 0) {
                    armorFraction = Math.max(armorFraction - breach * 0.15f, 0);
                }
                source.setDamage(-damage * armorFraction, DamageModifier.ARMOR);
            }

            source.setDamage(-source.getFinalDamage() * (Mth.clamp(epf, 0, 20) / 25f), DamageModifier.ARMOR_ENCHANTMENTS);
        }

        if (source.getCause() != DamageCause.SUICIDE) {
            source.setDamage(-Math.min(this.getAbsorption(), source.getFinalDamage()), DamageModifier.ABSORPTION);
        }

        if (super.damageEntity0(source)) {
            if (source.getCause() == DamageCause.SUICIDE) {
                return true;
            }

            Entity damager = null;

            if (source instanceof EntityDamageByEntityEvent) {
                damager = ((EntityDamageByEntityEvent) source).getDamager();
            }

            for (int slot = 0; slot < 4; slot++) {
                Item armor = this.armorInventory.getItem(slot);

                if (armor.hasEnchantments()) {
                    if (damager != null) {
                        for (Enchantment enchantment : armor.getEnchantments()) {
                            enchantment.doPostAttack(armor, damager, this, source.getCause());
                        }

                        if (armor.getDamage() > armor.getMaxDurability()) {
                            armorInventory.setItem(slot, Items.air());
                            level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_BREAK);
                            continue;
                        } else {
                            armorInventory.setItem(slot, armor, true);
                        }
                    }
                }

                if (armorDamage == 0) {
                    continue;
                }

                if (source.getCause() != DamageCause.VOID
                        && source.getCause() != DamageCause.MAGIC
                        && source.getCause() != DamageCause.WITHER
                        && source.getCause() != DamageCause.HUNGER
                        && source.getCause() != DamageCause.DROWNING
                        && source.getCause() != DamageCause.SUFFOCATION
                        && source.getCause() != DamageCause.FIRE_TICK
                        && source.getCause() != DamageCause.FREEZE
                        && source.getCause() != DamageCause.TEMPERATURE
                        && source.getCause() != DamageCause.FALL
                        && source.getCause() != DamageCause.STALAGMITE
                        && source.getCause() != DamageCause.FLY_INTO_WALL
                        && source.getCause() != DamageCause.SONIC_BOOM
                ) { // No armor damage
                    if (!armor.isArmor() || armor.getId() == Item.ELYTRA) {
                        continue;
                    }

                    if (armor.isFireResistant() && (source.getCause() == DamageCause.FIRE || source.getCause() == DamageCause.LAVA || source.getCause() == DamageCause.MAGMA || source.getCause() == DamageCause.CAMPFIRE || source.getCause() == DamageCause.SOUL_CAMPFIRE)) {
                        continue;
                    }

                    int itemDamaged = armor.hurtAndBreak(armorDamage);
                    if (itemDamaged == 0) {
                        continue;
                    }

                    if (itemDamaged < 0) {
                        armorInventory.setItem(slot, Items.air());
                        level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_BREAK);
                    } else {
                        armorInventory.setItem(slot, armor, true);
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    protected int calculateEnchantmentProtectionFactor(Item item, EntityDamageEvent source) {
        if (!item.hasEnchantments()) {
            return 0;
        }

        int epf = 0;

        for (Enchantment ench : item.getEnchantments()) {
            epf += ench.getProtectionFactor(source);
        }

        return epf;
    }

    @Override
    public void setOnFire(int seconds) {
        int level = 0;
        for (Item armor : this.armorInventory.getContents().values()) {
            level += armor.getEnchantmentLevel(Enchantment.FIRE_PROTECTION);
        }

        int ticks = (int) (seconds * 20 * (level * -0.15 + 1));

        if (ticks > 0 && (hasEffect(Effect.FIRE_RESISTANCE) || !isAlive())) {
            extinguish();
            return;
        }

        if (ticks > fireTicks) {
            fireTicks = ticks;
        }
    }

    @Override
    protected boolean checkTurtleHelmet(boolean breathing) {
        if (breathing && armorInventory.getHelmet().getId() == Item.TURTLE_HELMET) {
            turtleTicks = 200;
            return true;
        }

        if (turtleTicks > 0) {
            turtleTicks--;
            return true;
        }

        return breathing;
    }

    @Override
    protected float getKnockbackResistance() {
        float total = 0;
        for (Item armor : armorInventory.getContents().values()) {
            total += armor.getKnockbackResistance();
        }
        return total;
    }

    @Override
    protected ObjectIntPair<Inventory> getEquippedTotem() {
        if (offhandInventory.getItem().getId() == Item.TOTEM_OF_UNDYING) {
            return ObjectIntPair.of(offhandInventory, 0);
        }
        if (inventory.getItemInHand().getId() == Item.TOTEM_OF_UNDYING) {
            return ObjectIntPair.of(inventory, inventory.getHeldItemIndex());
        }
        return null;
    }
}
