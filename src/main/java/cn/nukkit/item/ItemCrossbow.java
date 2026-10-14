package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.inventory.BaseInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventorySlotReference;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Mth;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.potion.Effect;
import cn.nukkit.potion.Potion;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ItemCrossbow extends ItemTool {
    private static final float ARROW_POWER = 3.15f;
    private static final float MULTISHOT_ANGLE_DELTA = 10;

    public ItemCrossbow() {
        this(0, 1);
    }

    public ItemCrossbow(Integer meta) {
        this(meta, 1);
    }

    public ItemCrossbow(Integer meta, int count) {
        super(CROSSBOW, meta, count, "Crossbow");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_CROSSBOW;
    }

    @Override
    public int getEnchantAbility() {
        return 1;
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

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        Vector3 pos = player.getEyePosition();

        Item chargedItem = getChargedItem();
        if (!chargedItem.isNull()) {
            if (player.isUsingItem()) {
                return false;
            }

            InventorySlotReference source = player.isMainThreadInputEnabled() ? player.getInventory().captureHeldItem() : null;
            long movementEpoch = source == null ? 0 : player.getMovementEpoch();
            if (source != null && (!player.canContinueItemUse(movementEpoch)
                    || !source.getSnapshot().equalsExact(this))) {
                return false;
            }

            Vector3 aimDir = Vector3.directionFromRotation(player.pitch, player.yaw);

            int count = Math.min(chargedItem.getCount(), 3);
            // 新模式先完成发射回调和原槽结算，再发布；关闭模式保留原逐箭顺序。
            List<EntityArrow> pendingProjectiles = source == null ? null : new ArrayList<>(count);
            if (chargedItem.getId() == ARROW) {
                int penetrationLevel = getEnchantmentLevel(Enchantment.PIERCING);

                FullChunk chunk = player.getChunk();
                ThreadLocalRandom random = ThreadLocalRandom.current();
                for (int i = 0; i < count; i++) {
                    float angleOffset = count == 1 ? 0 : i * MULTISHOT_ANGLE_DELTA - MULTISHOT_ANGLE_DELTA;
                    Vector3 dir = aimDir.yRot(angleOffset * Mth.DEG_TO_RAD)
                            .add(0.0075 * random.nextGaussian(), 0.0075 * random.nextGaussian(), 0.0075 * random.nextGaussian());
                    CompoundTag nbt = Entity.getDefaultNBT(pos, dir.multiply(ARROW_POWER), (float) dir.yRotFromDirection(), (float) dir.xRotFromDirection())
                            .putByte("PierceLevel", penetrationLevel)
                            .putByte("auxValue", chargedItem.getDamage());

                    if (chargedItem.getDamage() != ItemArrow.NORMAL_ARROW) {
                        Potion potion = Potion.getPotion(chargedItem.getDamage() - ItemArrow.TIPPED_ARROW);
                        if (potion != null) {
                            Effect[] effects = potion.getEffects();
                            ListTag<CompoundTag> mobEffects = new ListTag<>("mobEffects");
                            for (Effect effect : effects) {
                                mobEffects.add(effect.save());
                            }
                            nbt.putList(mobEffects);
                        }
                    }

                    if (player.isCreative() || count > 1 && i != 1) {
                        nbt.putByte("pickup", EntityArrow.PICKUP_CREATIVE);
                    }

                    EntityArrow arrow = new EntityArrow(chunk, nbt, player, true);
                    ProjectileLaunchEvent event = new ProjectileLaunchEvent(arrow);
                    event.call();
                    if (source != null && (!player.canContinueItemUse(movementEpoch)
                            || !source.isSelectedBy(player.getInventory()) || !source.isCurrent())) {
                        arrow.close();
                        pendingProjectiles.forEach(Entity::close);
                        source.sendContents(player);
                        return false;
                    }
                    if (event.isCancelled()) {
                        arrow.close();
                    } else if (source != null) {
                        pendingProjectiles.add(arrow);
                    } else {
                        arrow.spawnToAll();
                    }
                }
            } else if (chargedItem instanceof ItemFirework && source == null) {
                for (int i = 0; i < count; i++) {
                    float angleOffset = count == 1 ? 0 : i * MULTISHOT_ANGLE_DELTA - MULTISHOT_ANGLE_DELTA;
                    Vector3 dir = aimDir.yRot(angleOffset * Mth.DEG_TO_RAD);
                    ((ItemFirework) chargedItem).spawnFirework(player.level, pos, dir);
                }
            }

            if (source != null) {
                if (!player.canContinueItemUse(movementEpoch)
                        || !source.isSelectedBy(player.getInventory()) || !source.isCurrent()) {
                    pendingProjectiles.forEach(Entity::close);
                    source.sendContents(player);
                    return false;
                }
                clearChargedItem();
                if (!source.setItemAndVerify(this) || !player.canContinueItemUse(movementEpoch)) {
                    pendingProjectiles.forEach(Entity::close);
                    source.sendContents(player);
                    return false;
                }
                for (EntityArrow arrow : pendingProjectiles) {
                    if (player.canContinueItemUse(movementEpoch)) {
                        arrow.spawnToAll();
                    } else {
                        arrow.close();
                    }
                }
                if (chargedItem instanceof ItemFirework firework) {
                    for (int i = 0; i < count && player.canContinueItemUse(movementEpoch); i++) {
                        float angleOffset = count == 1 ? 0 : i * MULTISHOT_ANGLE_DELTA - MULTISHOT_ANGLE_DELTA;
                        firework.spawnFirework(player.level, pos, aimDir.yRot(angleOffset * Mth.DEG_TO_RAD));
                    }
                }
                if (player.canContinueItemUse(movementEpoch)) {
                    player.level.addLevelSoundEvent(pos, LevelSoundEventPacket.SOUND_CROSSBOW_SHOOT);
                }
                return false;
            }

            player.level.addLevelSoundEvent(pos, LevelSoundEventPacket.SOUND_CROSSBOW_SHOOT);

            clearChargedItem();
            player.getInventory().setItemInHand(this);
            return false;
        }

        if (player.isCreative()) {
            return true;
        }

        Inventory offhand = player.getOffhandInventory();
        if (!offhand.peek(LazyHolder.ARROW).isNull()) {
            return true;
        }
        if (!offhand.peek(LazyHolder.FIREWORK_ROCKET).isNull()) {
            return true;
        }

        return !player.getInventory().peek(LazyHolder.ARROW).isNull();
    }

    @Override
    public void onUsing(Player player, int ticksUsed) {
        int maxUseDuration = getUseDuration();

        int quickChargeLevel = getValidEnchantmentLevel(Enchantment.QUICK_CHARGE);
        boolean quickCharge = quickChargeLevel > 0;
        if (quickCharge) {
            maxUseDuration -= 5 * quickChargeLevel;
        }

        int sound = -1;
        if (ticksUsed == (int) (0.9f * maxUseDuration)) {
            sound = quickCharge ? LevelSoundEventPacket.SOUND_CROSSBOW_QUICK_CHARGE_END : LevelSoundEventPacket.SOUND_CROSSBOW_LOADING_END;
        } else if (ticksUsed == (int) (0.5f * maxUseDuration)) {
            sound = quickCharge ? LevelSoundEventPacket.SOUND_CROSSBOW_QUICK_CHARGE_MIDDLE : LevelSoundEventPacket.SOUND_CROSSBOW_LOADING_MIDDLE;
        } else if (ticksUsed == (int) (0.1f * maxUseDuration)) {
            sound = quickCharge ? LevelSoundEventPacket.SOUND_CROSSBOW_QUICK_CHARGE_START : LevelSoundEventPacket.SOUND_CROSSBOW_LOADING_START;
        }
        if (sound != -1) {
            player.level.addLevelSoundEvent(player.getEyePosition(), sound);
        }
    }

    @Override
    public boolean onUse(Player player, int ticksUsed) {
        int maxUseDuration = getUseDuration();

        int quickChargeLevel = getValidEnchantmentLevel(Enchantment.QUICK_CHARGE);
        boolean quickCharge = quickChargeLevel > 0;
        if (quickCharge) {
            maxUseDuration -= 5 * quickChargeLevel;
        }

        if ((ticksUsed + 2) < maxUseDuration) {
            return false;
        }

        InventorySlotReference source = player.isMainThreadInputEnabled() ? player.getInventory().captureHeldItem() : null;
        long movementEpoch = source == null ? 0 : player.getMovementEpoch();
        if (source != null && (!player.canContinueItemUse(movementEpoch)
                || !source.getSnapshot().equalsExact(this) || !getChargedItem().isNull())) {
            return false;
        }

        Item matched;
        BaseInventory inventory = player.getOffhandInventory();
        InventorySlotReference ammoSource = source == null ? null : inventory.captureFirstItem(LazyHolder.FIREWORK_ROCKET);
        matched = source == null ? inventory.peek(LazyHolder.FIREWORK_ROCKET)
                : ammoSource == null ? Items.air() : ammoSource.getSnapshot();
        if (matched.isNull()) {
            ammoSource = source == null ? null : inventory.captureFirstItem(LazyHolder.ARROW);
            matched = source == null ? inventory.peek(LazyHolder.ARROW)
                    : ammoSource == null ? Items.air() : ammoSource.getSnapshot();
            if (matched.isNull()) {
                inventory = player.getInventory();
                ammoSource = source == null ? null : inventory.captureFirstItem(LazyHolder.ARROW);
                matched = source == null ? inventory.peek(LazyHolder.ARROW)
                        : ammoSource == null ? Items.air() : ammoSource.getSnapshot();
                if (matched.isNull()) {
                    if (player.isCreative()) {
                        matched = LazyHolder.CREATIVE_ARROW;
                    } else {
                        player.getOffhandInventory().sendContents(player);
                        inventory.sendContents(player);
                        return false;
                    }
                }
            }
        }
        matched = matched.clone();
        matched.setCount(1);

        Item chargedItem = matched.clone();

        boolean multishot = getEnchantmentLevel(Enchantment.MULTISHOT) > 0;
        if (multishot) {
            chargedItem.setCount(3);
        }

        if (source == null) {
            setChargedItem(chargedItem);
        }

        if (player.isSurvivalLike()) {
            if (source == null) {
                inventory.removeItem(matched);
            } else if (ammoSource == null || !ammoSource.consume(1)) {
                source.sendContents(player);
                inventory.sendContents(player);
                return false;
            }

            // 扣箭事件成立后可换选槽，仍只装填原弩；替换或转移玩家不能继承旧动作。
            if (source != null && (!player.canContinueItemUse(movementEpoch) || !source.isCurrent())) {
                source.sendContents(player);
                return false;
            }

            if (hurtAndBreak(multishot || chargedItem.getId() == Item.FIREWORK_ROCKET ? 3 : 1) < 0) {
                pop();
                player.level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_BREAK);
            }
        }

        if (source != null) {
            setChargedItem(chargedItem);
            if (!source.setItemAndVerify(this) || !player.canContinueItemUse(movementEpoch)) {
                source.sendContents(player);
                return false;
            }
        }

        EntityEventPacket packet = new EntityEventPacket();
        packet.event = EntityEventPacket.CHARGED_ITEM;
        packet.eid = player.getId();
        player.dataPacket(packet);

        if (source == null) {
            player.getInventory().setItemInHand(this);
        }
        return true;
    }

    @Override
    public boolean onRelease(Player player, int ticksUsed, Vector3 rotation) {
        return true;
    }

    @Override
    public boolean canRelease() {
        return true;
    }

    @Override
    public boolean canContinueUsing() {
        return this.getChargedItem().isNull();
    }

    @Override
    public int getUseDuration() {
        return 25;
    }

    public Item getChargedItem() {
        CompoundTag nbt = getNamedTag();
        if (nbt == null) {
            return Items.air();
        }

        Tag chargedItem = nbt.get("chargedItem");
        if (!(chargedItem instanceof CompoundTag)) {
            return Items.air();
        }

        return NBTIO.getItemHelper((CompoundTag) chargedItem);
    }

    public void setChargedItem(Item item) {
        CompoundTag nbt = getNamedTag();
        if (nbt == null) {
            nbt = new CompoundTag();
        }

        setNamedTag(nbt.putCompound("chargedItem", NBTIO.putItemHelper(item)));
    }

    public void clearChargedItem() {
        CompoundTag nbt = getNamedTag();
        if (nbt == null) {
            return;
        }

        if (nbt.removeAndGet("chargedItem") == null) {
            return;
        }

        setNamedTag(nbt);
    }

    private static class LazyHolder {
        private static final Item ARROW = Item.get(Item.ARROW, null, 1).clearCompoundTag();
        private static final Item FIREWORK_ROCKET = Item.get(Item.FIREWORK_ROCKET, null, 1).clearCompoundTag();

        private static final Item CREATIVE_ARROW = Item.get(Item.ARROW);
    }
}
