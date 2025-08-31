package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.EntityInteractable;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.data.IntEntityData;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.Items;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.network.protocol.MobEquipmentPacket;
import cn.nukkit.network.protocol.types.ContainerIds;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIntPair;

import javax.annotation.Nullable;
import java.util.List;

public class EntityArmorStand extends EntityLiving implements EntityInteractable {
    public static final int NETWORK_ID = EntityID.ARMOR_STAND;

    public static final int POSE_DEFAULT = 0;
    public static final int POSE_ZERO_ROTATION = 1;
    public static final int POSE_SOLEMN = 2;
    public static final int POSE_ATHENA = 3;
    public static final int POSE_BRANDISH = 4;
    public static final int POSE_HONOR = 5;
    public static final int POSE_ENTERTAIN = 6;
    public static final int POSE_SALUTE = 7;
    public static final int POSE_RIPOSTE = 8;
    public static final int POSE_ZOMBIE = 9;
    public static final int POSE_CANCAN_A = 10;
    public static final int POSE_CANCAN_B = 11;
    public static final int POSE_HERO = 12;
    public static final int POSE_COUNT = 13;

    private static final int EQUIPMENT_HAND_SLOT = 0;
    public static final int EQUIPMENT_SLOT_MAINHAND = EQUIPMENT_HAND_SLOT + 0;
    public static final int EQUIPMENT_SLOT_OFFHAND = EQUIPMENT_HAND_SLOT + 1;
    private static final int HAND_SLOT_COUNT = 2;
    private static final int EQUIPMENT_ARMOR_SLOT = 2;
    public static final int EQUIPMENT_SLOT_HEAD = EQUIPMENT_ARMOR_SLOT + 0;
    public static final int EQUIPMENT_SLOT_TORSO = EQUIPMENT_ARMOR_SLOT + 1;
    public static final int EQUIPMENT_SLOT_LEGS = EQUIPMENT_ARMOR_SLOT + 2;
    public static final int EQUIPMENT_SLOT_FEET = EQUIPMENT_ARMOR_SLOT + 3;
    private static final int ARMOR_SLOT_COUNT = 4;
    private static final int EQUIPMENT_SLOT_COUNT = HAND_SLOT_COUNT + ARMOR_SLOT_COUNT;

    private int poseIndex;
    private int lastCircuitStrength;

    //TODO: general mob stuff
    private Item[] equipments;

    private long lastHit;
    private int hurtTime;
    private boolean hurtDirection;

    public EntityArmorStand(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        needLivingBaseTick = false;
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.5f;
    }

    @Override
    public float getHeight() {
        return 1.975f;
    }

    @Override
    protected void initEntity() {
        setMaxHealth(6);
        super.initEntity();

        equipments = new Item[EQUIPMENT_SLOT_COUNT];

        ListTag<CompoundTag> mainhand = namedTag.getList("Mainhand", CompoundTag.class);
        equipments[EQUIPMENT_SLOT_MAINHAND] = !mainhand.isEmpty() ? NBTIO.getItemHelper(mainhand.get(0)) : Items.air();

        ListTag<CompoundTag> offhand = namedTag.getList("Offhand", CompoundTag.class);
        equipments[EQUIPMENT_SLOT_OFFHAND] = !offhand.isEmpty() ? NBTIO.getItemHelper(offhand.get(0)) : Items.air();

        ListTag<CompoundTag> armors = namedTag.getList("Armor", CompoundTag.class);
        for (int i = 0; i < ARMOR_SLOT_COUNT; i++) {
            equipments[EQUIPMENT_ARMOR_SLOT + i] = i < armors.size() ? NBTIO.getItemHelper(armors.get(i)) : Items.air();
        }

        CompoundTag pose = namedTag.getCompound("Pose");
        lastCircuitStrength = pose.getInt("LastSignal");
        poseIndex = pose.getInt("PoseIndex");
        dataProperties.putInt(DATA_ARMOR_STAND_POSE_INDEX, poseIndex);

        hurtTime = 0;
        dataProperties.putInt(DATA_HURT_TIME, hurtTime);
        hurtDirection = true;
        dataProperties.putInt(DATA_HURT_DIRECTION, 1);

        setDataFlag(DATA_FLAG_HIDDEN_WHEN_INVISIBLE, false, false);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        namedTag.putList(new ListTag<>("Mainhand")
                        .add(NBTIO.putItemHelper(equipments[EQUIPMENT_SLOT_MAINHAND])))
                .putList(new ListTag<>("Offhand")
                        .add(NBTIO.putItemHelper(equipments[EQUIPMENT_SLOT_OFFHAND])))
                .putList(new ListTag<>("Armor")
                        .add(NBTIO.putItemHelper(equipments[EQUIPMENT_SLOT_HEAD]))
                        .add(NBTIO.putItemHelper(equipments[EQUIPMENT_SLOT_TORSO]))
                        .add(NBTIO.putItemHelper(equipments[EQUIPMENT_SLOT_LEGS]))
                        .add(NBTIO.putItemHelper(equipments[EQUIPMENT_SLOT_FEET])));

        namedTag.putCompound("Pose", new CompoundTag()
                .putInt("PoseIndex", poseIndex)
                .putInt("LastSignal", lastCircuitStrength));
    }

    @Override
    public void spawnTo(Player player) {
        if (getViewers().containsKey(player.getLoaderId())) {
            return;
        }

        player.dataPacket(createAddEntityPacket());

        sendHeldItems(player);

        super.spawnTo(player);
    }

    private void sendHeldItems(Player player) {
        for (int slot = 0; slot < EQUIPMENT_SLOT_COUNT; slot++) {
            sendEquipment(slot, player);
        }
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (isClosed()) {
            return false;
        }

        int tickDiff = currentTick - lastUpdate;
        if (tickDiff <= 0 && !justCreated) {
            return true;
        }
        lastUpdate = currentTick;

        boolean hasUpdate = entityBaseTick(tickDiff);

        if (isAlive()) {
            motionY -= getGravity();

            move(motionX, motionY, motionZ);

            double friction = 1 - getDrag();
            motionX *= friction;
            motionY *= friction;
            motionZ *= friction;

            if (level.isRedstoneEnabled()) {
                boolean signalFound = checkSignal(this);
                if (!signalFound) {
                    signalFound = checkSignal(downVec());
                }
                if (!signalFound) {
                    lastCircuitStrength = 0;
                }
            }

            Block block = this.level.getBlock(this);
            if (block.isCampfire()) {
                block.onEntityCollide(this);
            }

            if (hurtTime > 0) {
                setHurtTime(hurtTime - 1);
            }

            updateMovement();
        }

        return hasUpdate || !onGround || Math.abs(motionX) > 0.00001 || Math.abs(motionY) > 0.00001 || Math.abs(motionZ) > 0.00001;
    }

    private boolean checkSignal(Vector3 pos) {
        int strength = level.getRedstonePower(pos, BlockFace.UP);
        if (strength <= 0) {
            return false;
        }

        if (strength != lastCircuitStrength) {
            int poseIndex = Math.min(strength, POSE_COUNT - 1);
            if (poseIndex != this.poseIndex) {
                setPoseIndex(poseIndex);
            }
        }

        return true;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        switch (source.getCause()) {
            case FALL:
            case STALAGMITE:
                level.addLevelEvent(this, LevelEventPacket.EVENT_SOUND_ARMOR_STAND_FALL);
                return false;
            case FIRE:
                source.setDamage(0.05f);
                break;
            case FIRE_TICK:
                if (level.containsBlock(getBoundingBox(), block -> block.isFire() || block.isCampfire(), false)) {
                    return false;
                }
                source.setDamage(1);
                break;
            case CAMPFIRE:
            case SOUL_CAMPFIRE:
                source.setDamage(0.5f);
                break;
            case PROJECTILE:
                if (source.getDamage() <= 0) {
                    return false;
                }
            case BLOCK_EXPLOSION:
            case ENTITY_EXPLOSION:
                source.setDamage(1000);
                break;
            case ENTITY_ATTACK:
                if (source instanceof EntityDamageByEntityEvent) {
                    Entity damager = ((EntityDamageByEntityEvent) source).getDamager();
                    if (damager instanceof Player) {
                        level.addLevelEvent(this, LevelEventPacket.EVENT_SOUND_ARMOR_STAND_HIT);

                        if (!((Player) damager).isCreative() && level.getCurrentTick() - lastHit > 5) {
                            lastHit = level.getCurrentTick();

                            setDataProperty(new IntEntityData(DATA_HURT_DIRECTION, hurtDirection ? 1 : -1));
                            hurtDirection = !hurtDirection;
                            setHurtTime(9);

                            source.setCancelled();
                        } else {
                            source.setDamage(1000);
                        }
                    }
                }
                break;
            case VOID:
            case SUICIDE:
            case MAGIC:
            case WITHER:
            case CUSTOM:
                break;
            default:
                return false;
        }

        return super.attack(source);
    }

    @Override
    protected void onHurt(EntityDamageEvent source) {
        switch (source.getCause()) {
            case ENTITY_ATTACK:
                doMagicCriticalHit(source);
            case PROJECTILE:
            case BLOCK_EXPLOSION:
            case ENTITY_EXPLOSION:
                return;
        }

        super.onHurt(source);
    }

    @Override
    protected float getKnockbackResistance() {
        return 1;
    }

    @Override
    protected ObjectIntPair<Inventory> getEquippedTotem() {
        return null;
    }

    @Override
    public void kill() {
        super.kill();

        if (lastDamageCause != null) {
            switch (lastDamageCause.getCause()) {
                case VOID:
                case SUICIDE:
                case BLOCK_EXPLOSION:
                case ENTITY_EXPLOSION:
                case FIRE:
                case FIRE_TICK:
                case CAMPFIRE:
                case SOUL_CAMPFIRE:
                    return;
            }
        }

        level.addLevelEvent(this, LevelEventPacket.EVENT_PARTICLE_DESTROY_ARMOR_STAND);
        level.addLevelEvent(this, LevelEventPacket.EVENT_SOUND_ARMOR_STAND_BREAK);
    }

    @Override
    public Item[] getDrops() {
        if (lastDamageCause instanceof EntityDamageByEntityEvent) {
            Entity damager = ((EntityDamageByEntityEvent) lastDamageCause).getDamager();
            if (damager instanceof Player && ((Player) damager).isCreative()) {
                return new Item[0];
            }
        }

        List<Item> drops = new ObjectArrayList<>(1 + 4 + 2);

        boolean dropResource = true;
        if (lastDamageCause != null) {
            switch (lastDamageCause.getCause()) {
                case VOID:
                    return new Item[0];
                case SUICIDE:
                case BLOCK_EXPLOSION:
                case ENTITY_EXPLOSION:
                case FIRE:
                case FIRE_TICK:
                case CAMPFIRE:
                case SOUL_CAMPFIRE:
                    dropResource = false;
            }
        }
        if (dropResource) {
            drops.add(Item.get(Item.ARMOR_STAND));
        }

        for (Item item : equipments) {
            if (item.isNull()) {
                continue;
            }
            drops.add(item.clone());
        }

        return drops.toArray(new Item[0]);
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (player.isSneaking()) {
            setPoseIndex((poseIndex + 1) % POSE_COUNT);
            return false;
        }

        if (item.getId() == Item.ARMOR_STAND) {
            return false;
        }
        if (item.hasLock()) {
            return false;
        }

        int slot = EQUIPMENT_SLOT_MAINHAND;
        boolean playerHasItem = true;
        if (item.isNull()) {
            double height = clickedPos.y - y;
            if (height < 0.1 || height >= 0.55) {
                if (height < 0.9 || height >= 1.6) {
                    if (height < 0.4 || height >= 1.2) {
                        if (height >= 1.6) {
                            slot = EQUIPMENT_SLOT_HEAD;
                        }
                    } else {
                        slot = EQUIPMENT_SLOT_LEGS;
                    }
                } else {
                    slot = EQUIPMENT_SLOT_TORSO;
                }
            } else {
                slot = EQUIPMENT_SLOT_FEET;
            }

//            if (slot == EQUIPMENT_SLOT_MAINHAND) {
//                //TODO: pose hand position
//                return false;
//            }

            playerHasItem = false;
        } else if (item.isArmor()) {
            if (item.isHelmet()) {
                slot = EQUIPMENT_SLOT_HEAD;
            } else if (item.isChestplate()) {
                slot = EQUIPMENT_SLOT_TORSO;
            } else if (item.isLeggings()) {
                slot = EQUIPMENT_SLOT_LEGS;
            } else if (item.isBoots()) {
                slot = EQUIPMENT_SLOT_FEET;
            }
        } else if (item.getId() == ItemID.SKULL || item.getId() == ItemBlockID.CARVED_PUMPKIN) {
            slot = EQUIPMENT_SLOT_HEAD;
        }

        Item heldItem = getEquipment(slot);
        boolean hasHeldItem = !heldItem.isNull();

        if (!playerHasItem && !hasHeldItem) {
            slot = EQUIPMENT_SLOT_MAINHAND;
            heldItem = getEquipment(slot);
            hasHeldItem = !heldItem.isNull();
        }

        if (playerHasItem) {
            Item newItem = item.clone();
            newItem.setCount(1);
            setEquipment(slot, newItem);

            if (!player.isCreative()) {
                item.pop();
                player.getInventory().setItemInHand(item);
            }
        } else if (hasHeldItem) {
            setEquipment(slot, null);
        } else {
            return false;
        }

        if (hasHeldItem) {
            heldItem.setCount(1);
            player.getInventory().addItem(heldItem);
        }

        level.addLevelSoundEvent(add(0.5, 0.5, 0.5), LevelSoundEventPacket.SOUND_MOB_ARMOR_STAND_PLACE);
        return false;
    }

    @Override
    public String getInteractButtonText(Player player) {
        if (player.isSneaking()) {
            return "action.interact.armorstand.pose";
        }
        return "action.interact.armorstand.equip";
    }

    @Override
    public boolean canDoInteraction(Player player) {
        return true;
    }

    private void setHurtTime(int hurtTime) {
        this.hurtTime = hurtTime;
        setDataProperty(new IntEntityData(DATA_HURT_TIME, hurtTime));
    }

    public int getPoseIndex() {
        return poseIndex;
    }

    public void setPoseIndex(int poseIndex) {
        this.poseIndex = poseIndex;
        setDataProperty(new IntEntityData(DATA_ARMOR_STAND_POSE_INDEX, poseIndex));
    }

    public Item getEquipment(int slot) {
        return equipments[slot];
    }

    public void setEquipment(int slot, @Nullable Item item) {
        if (item == null) {
            item = Items.air();
        }

        equipments[slot] = item;

        for (Player player : getViewers().values()) {
            sendEquipment(slot, player);
        }
    }

    private void sendEquipment(int slot, Player player) {
        MobEquipmentPacket packet = new MobEquipmentPacket();
        packet.eid = getId();
        packet.item = getEquipment(slot);
        packet.inventorySlot = slot;
        packet.hotbarSlot = slot;
        packet.windowId = slot == EQUIPMENT_SLOT_OFFHAND ? ContainerIds.OFFHAND : ContainerIds.INVENTORY;
        player.dataPacket(packet);
    }
}
