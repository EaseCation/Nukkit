package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.enchantment.Enchantment;
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
    public boolean onClickAir(Player player, Vector3 directionVector) {
        Vector3 pos = player.getEyePosition();

        Item chargedItem = getChargedItem();
        if (!chargedItem.isNull()) {
            if (player.isUsingItem()) {
                return false;
            }

            Vector3 aimDir = Vector3.directionFromRotation(player.pitch, player.yaw);

            int count = Math.min(chargedItem.getCount(), 3);
            if (chargedItem.getId() == ARROW) {
                int penetrationLevel = getEnchantmentLevel(Enchantment.PIERCING);

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

                    EntityArrow arrow = new EntityArrow(player.chunk, nbt, player, true);
                    ProjectileLaunchEvent event = new ProjectileLaunchEvent(arrow);
                    event.call();
                    if (event.isCancelled()) {
                        arrow.close();
                    } else {
                        arrow.spawnToAll();
                    }
                }
            } else if (chargedItem instanceof ItemFirework) {
                for (int i = 0; i < count; i++) {
                    float angleOffset = count == 1 ? 0 : i * MULTISHOT_ANGLE_DELTA - MULTISHOT_ANGLE_DELTA;
                    Vector3 dir = aimDir.yRot(angleOffset * Mth.DEG_TO_RAD);
                    ((ItemFirework) chargedItem).spawnFirework(player.level, pos, dir);
                }
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
        int maxUseDuration = 25;

        int quickChargeLevel = getEnchantmentLevel(Enchantment.QUICK_CHARGE);
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
        int maxUseDuration = 25;

        int quickChargeLevel = getEnchantmentLevel(Enchantment.QUICK_CHARGE);
        boolean quickCharge = quickChargeLevel > 0;
        if (quickCharge) {
            maxUseDuration -= 5 * quickChargeLevel;
        }

        if ((ticksUsed + 1) < maxUseDuration) {
            return false;
        }

        Item matched;
        Inventory inventory = player.getOffhandInventory();
        matched = inventory.peek(LazyHolder.FIREWORK_ROCKET);
        if (matched.isNull()) {
            matched = inventory.peek(LazyHolder.ARROW);
            if (matched.isNull()) {
                inventory = player.getInventory();
                matched = inventory.peek(LazyHolder.ARROW);
                if (matched.isNull() && !player.isCreative()) {
                    player.getOffhandInventory().sendContents(player);
                    return false;
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

        setChargedItem(chargedItem);

        if (player.isSurvivalLike()) {
            inventory.removeItem(matched);

            if (!isUnbreakable()) {
                int unbreaking = getEnchantmentLevel(Enchantment.UNBREAKING);
                if (unbreaking <= 0 || ThreadLocalRandom.current().nextInt(100) < chargedItem.getDamageChance(unbreaking)) {
                    int damage = getDamage() + (multishot || chargedItem.getId() == Item.FIREWORK_ROCKET ? 3 : 1);
                    if (damage < getMaxDurability()) {
                        setDamage(damage);
                    } else {
                        pop();
                    }
                }
            }
        }

        EntityEventPacket packet = new EntityEventPacket();
        packet.event = EntityEventPacket.CHARGED_ITEM;
        packet.eid = player.getId();
        player.dataPacket(packet);

        player.getInventory().setItemInHand(this);
        return true;
    }

    @Override
    public boolean onRelease(Player player, int ticksUsed) {
        return true;
    }

    @Override
    public boolean canRelease() {
        return true;
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
    }
}
