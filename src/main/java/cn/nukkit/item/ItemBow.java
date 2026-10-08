package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.entity.EntityShootBowEvent;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.inventory.BaseInventory;
import cn.nukkit.inventory.InventorySlotReference;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.potion.Effect;
import cn.nukkit.potion.Potion;

import java.util.concurrent.ThreadLocalRandom;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemBow extends ItemTool {

    public ItemBow() {
        this(0, 1);
    }

    public ItemBow(Integer meta) {
        this(meta, 1);
    }

    public ItemBow(Integer meta, int count) {
        super(BOW, meta, count, "Bow");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_BOW;
    }

    @Override
    public int getEnchantAbility() {
        return 1;
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return player.getOffhandInventory().contains(LazyHolder.ARROW) || player.getInventory().contains(LazyHolder.ARROW) || player.isCreative();
    }

    @Override
    public boolean onUse(Player player, int ticksUsed) {
        return true;
    }

    @Override
    public boolean onRelease(Player player, int ticksUsed, Vector3 rotation) {
        // 仅持有本次释放的来源，不把物品对象身份锁定到整段蓄力。
        InventorySlotReference source = player.isMainThreadInputEnabled() ? player.getInventory().captureHeldItem() : null;
        long movementEpoch = source == null ? 0 : player.getMovementEpoch();
        if (source != null && (!player.canContinueItemUse(movementEpoch)
                || !player.getInventory().getItemInHand().equalsExact(this))) {
            return false;
        }
        Item matched;

        BaseInventory inventory = player.getOffhandInventory();
        InventorySlotReference ammoSource = source == null ? null : inventory.captureFirstItem(LazyHolder.ARROW);
        matched = source == null ? inventory.peek(LazyHolder.ARROW)
                : ammoSource == null ? Items.air() : ammoSource.getSnapshot();
        if (matched.isNull()) {
            inventory = player.getInventory();
            ammoSource = source == null ? null : inventory.captureFirstItem(LazyHolder.ARROW);
            matched = source == null ? inventory.peek(LazyHolder.ARROW)
                    : ammoSource == null ? Items.air() : ammoSource.getSnapshot();
            if (matched.isNull() && !player.isCreative()) {
                player.getOffhandInventory().sendContents(player);
                inventory.sendContents(player);
                return false;
            }
        }
        matched = matched.clone();
        matched.setCount(1);

        double damage = 2;

        int bowDamage = this.getEnchantmentLevel(Enchantment.POWER);
        if (bowDamage > 0) {
            damage += (double) bowDamage * 0.5 + 0.5;
        }

        int flameEnchant = this.getEnchantmentLevel(Enchantment.FLAME);
        boolean flame = flameEnchant > 0;

        int knockbackEnchant = this.getEnchantmentLevel(Enchantment.PUNCH);

        ThreadLocalRandom random = ThreadLocalRandom.current();
        Vector3 dir = Vector3.directionFromRotation(player.pitch, player.yaw)
                .add(0.0075 * random.nextGaussian(), 0.0075 * random.nextGaussian(), 0.0075 * random.nextGaussian());
        CompoundTag nbt = Entity.getDefaultNBT(player.getEyePosition(), dir.multiply(1.2), (float) dir.yRotFromDirection(), (float) dir.xRotFromDirection()) //TODO: pow
                .putShort("Fire", flame ? 45 * 60 : 0)
                .putDouble("damage", damage)
                .putByte("auxValue", matched.getDamage());
        // 附魔等级单独存储，base 值在命中时从受害者 Profile 获取
        if (knockbackEnchant > 0) {
            nbt.putInt("KnockbackEnchantLevel", knockbackEnchant);
        }

        if (matched.getDamage() != ItemArrow.NORMAL_ARROW) {
            Potion potion = Potion.getPotion(matched.getDamage() - ItemArrow.TIPPED_ARROW);
            if (potion != null) {
                Effect[] effects = potion.getEffects();
                ListTag<CompoundTag> mobEffects = new ListTag<>("mobEffects");
                for (Effect effect : effects) {
                    mobEffects.add(effect.save());
                }
                nbt.putList(mobEffects);
            }
        }

        double p = (double) ticksUsed / 20;
        double f = Math.min((p * p + p * 2) / 3, 1) * 2;

        EntityArrow arrow = new EntityArrow(player.getChunk(), nbt, player, f == 2);

        EntityShootBowEvent entityShootBowEvent = new EntityShootBowEvent(player, this, arrow, f);

        if (f < 0.1 || ticksUsed < 3) {
            entityShootBowEvent.setCancelled();
        }

        Server.getInstance().getPluginManager().callEvent(entityShootBowEvent);
        if (entityShootBowEvent.isCancelled()) {
            entityShootBowEvent.getProjectile().close();
            player.getInventory().sendContents(player);
            player.getOffhandInventory().sendContents(player);
        } else {
            entityShootBowEvent.getProjectile().setMotion(entityShootBowEvent.getProjectile().getMotion().multiply(entityShootBowEvent.getForce()));
            // 射箭与motion事件都能换槽或转服，开始扣箭前再次确认本次来源。
            if (source != null && (!player.canContinueItemUse(movementEpoch)
                    || !source.isSelectedBy(player.getInventory()) || !source.isCurrent()
                    || ammoSource != null && !ammoSource.isCurrent())) {
                entityShootBowEvent.getProjectile().close();
                source.sendContents(player);
                inventory.sendContents(player);
                return false;
            }
            int infinityEnchant = this.getEnchantmentLevel(Enchantment.INFINITY);
            boolean infinity = infinityEnchant > 0 && matched.getDamage() == ItemArrow.NORMAL_ARROW;
            EntityProjectile projectile;
            if ((infinity || player.isCreative()) && (projectile = entityShootBowEvent.getProjectile()) instanceof EntityArrow) {
                ((EntityArrow) projectile).setPickupMode(EntityArrow.PICKUP_CREATIVE);
            }
            if (player.isSurvivalLike()) {
                if (!infinity) {
                    if (source == null) {
                        inventory.removeItem(matched);
                    } else if (ammoSource == null || !ammoSource.consume(1)) {
                        entityShootBowEvent.getProjectile().close();
                        inventory.sendContents(player);
                        return false;
                    }
                }
                if (source != null && !player.canContinueItemUse(movementEpoch)) {
                    entityShootBowEvent.getProjectile().close();
                    return false;
                }
                int itemDamaged = hurtAndBreak(1);
                if (itemDamaged != 0) {
                    if (itemDamaged < 0) {
                        pop();
                        player.level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_BREAK);
                    }
                    if (source == null) {
                        player.getInventory().setItemInHand(this);
                    } else if (!source.setItem(this)) {
                        source.sendContents(player);
                    }
                } else if (!player.isServerAuthoritativeInventoryEnabled()) {
                    player.getInventory().sendHeldItem(player); // sync durability to correct client predictions
                }
            }
            if (entityShootBowEvent.getProjectile() != null) {
                if (source != null && !player.canContinueItemUse(movementEpoch)) {
                    entityShootBowEvent.getProjectile().close();
                    return false;
                }
                ProjectileLaunchEvent projectev = new ProjectileLaunchEvent(entityShootBowEvent.getProjectile());
                Server.getInstance().getPluginManager().callEvent(projectev);
                if (projectev.isCancelled() || source != null && !player.canContinueItemUse(movementEpoch)) {
                    entityShootBowEvent.getProjectile().close();
                } else {
                    entityShootBowEvent.getProjectile().spawnToAll();
                    player.getLevel().addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_BOW);
                }
            }
        }

        return true;
    }

    @Override
    public boolean canRelease() {
        return true;
    }

    @Override
    public int getUseDuration() {
        return 72000;
    }

    @Override
    public boolean noDamageOnAttack() {
        return true;
    }

    @Override
    public boolean noDamageOnBreak() {
        return true;
    }

    @Override
    public int getFuelTime() {
        return 200;
    }

    private static class LazyHolder {
        private static final Item ARROW = Item.get(Item.ARROW, null, 1).clearCompoundTag();
    }
}
