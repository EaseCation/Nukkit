package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityCampfire;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.item.EntityArmorStand;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.block.BlockIgniteEvent;
import cn.nukkit.event.block.BlockIgniteEvent.BlockIgniteCause;
import cn.nukkit.event.entity.EntityCombustByBlockEvent;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.inventory.RecipeTag;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.enchantment.EnchantmentID;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

import javax.annotation.Nullable;

import static cn.nukkit.GameVersion.*;
import static cn.nukkit.SharedConstants.*;

public class BlockCampfire extends BlockTransparent implements Faceable {

    public static final int DIRECTION_MASK = 0b11;
    public static final int EXTINGUISHED_BIT = 0b100;

    public BlockCampfire() {
        this(0);
    }

    public BlockCampfire(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BLOCK_CAMPFIRE;
    }

    @Override
    public String getName() {
        return "Campfire";
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.CAMPFIRE;
    }

    @Override
    public int getToolType() {
        return BlockToolType.AXE;
    }

    @Override
    public float getHardness() {
        if (ENABLE_BLOCK_DESTROY_SPEED_COMPATIBILITY || V1_20_30.isAvailable()) {
            return 2;
        }
        return 5;
    }

    @Override
    public float getResistance() {
        return 10;
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 20;
    }

    @Override
    public int getLightLevel() {
        return isExtinguished() ? 0 : 15;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public boolean sticksToPiston() {
        return false;
    }

    @Override
    public double getMaxY() {
        return this.y + (7 + 1) / 16.0; // + 1 pixel
    }

    @Override
    public double getMinX() {
        return this.x + 1 / 16.0; // + 1 pixel
    }

    @Override
    public double getMinZ() {
        return this.z + 1 / 16.0; // + 1 pixel
    }

    @Override
    public double getMaxX() {
        return this.x + 1 - 1 / 16.0; // - 1 pixel
    }

    @Override
    public double getMaxZ() {
        return this.z + 1 - 1 / 16.0; // - 1 pixel
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return face == BlockFace.DOWN;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PODZOL_BLOCK_COLOR;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public Item toItem(boolean addUserData) {
        Item item = Item.get(ItemID.CAMPFIRE);
        if (addUserData) {
            BlockEntityCampfire blockEntity = getBlockEntity();
            if (blockEntity != null) {
                item.setCustomName(blockEntity.getName());
                item.setRepairCost(blockEntity.getRepairCost());
            }
        }
        return item;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        return new Item[]{
                Item.get(ItemID.COAL, 1, 2)
        };
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (player != null) {
            setDamage(player.getHorizontalFacing().getHorizontalIndex());
        }

        if (block.isWaterSource()) {
            setExtinguished(true);
        }

        if (!super.place(item, block, target, face, fx, fy, fz, player)) {
            return false;
        }
        createBlockEntity(item);
        return true;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (item.isShovel()) {
            if (!tryDouseFire()) {
                return false;
            }

            if (player != null && player.isSurvivalLike() && item.hurtAndBreak(1) < 0) {
                item.pop();
                player.level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_BREAK);
            }

            return true;
        }

        int id = item.getId();
        if (id == Item.FLINT_AND_STEEL) {
            if (player != null && player.isSurvivalLike() && item.hurtAndBreak(1) < 0) {
                item.pop();
                player.level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_BREAK);
            }

            tryLightFire();

            return true;
        }
        if (id == Item.FIRE_CHARGE) {
            if (!tryLightFire()) {
                return true;
            }

            level.addLevelEvent(this, LevelEventPacket.EVENT_SOUND_GHAST_SHOOT, 78642);

            if (player != null && !player.isCreative()) {
                item.count--;
            }

            return true;
        }
        if (item.isSword() && item.hasEnchantment(Enchantment.FIRE_ASPECT)) {
            if (!tryLightFire()) {
                return false;
            }

            if (player != null && player.isSurvivalLike() && item.hurtAndBreak(1) < 0) {
                item.pop();
                player.level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_BREAK);
            }

            return true;
        }

        if (level.getExtraBlock(this).isWater()) {
            return false;
        }

        BlockEntityCampfire blockEntity = getBlockEntity();
        if (blockEntity == null) {
            blockEntity = createBlockEntity(null);
            if (blockEntity == null) {
                return false;
            }
        }

        if (!blockEntity.tryAddItem(item, getRecipeTag())) {
            return false;
        }

        if (player != null && !player.isCreative()) {
            item.count--;
        }

        level.addLevelEvent(this, LevelEventPacket.EVENT_SOUND_ITEM_FRAME_ITEM_ADDED);

        blockEntity.spawnToAll();
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            boolean containWater = level.getExtraBlock(this).isWater();

            if (containWater) {
                BlockEntityCampfire blockEntity = getBlockEntity();
                if (blockEntity != null) {
                    blockEntity.dropAllItems();
                }
            }

            if (!isExtinguished() && (containWater || up().isWater() || level.getExtraBlock(upVec()).isWater())) {
                tryDouseFire();
                return type;
            }
        }
        return 0;
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        if (isExtinguished()) {
            if ((entity instanceof EntityLiving || entity instanceof EntityProjectile) && entity.isOnFire()) {
                tryLightFire();
            }
            return;
        }

        if (!(entity instanceof EntityLiving) || entity instanceof Player && ((Player) entity).getArmorInventory().getBoots().hasEnchantment(EnchantmentID.FROST_WALKER)) {
            return;
        }

        if (!(entity instanceof Player) || entity.level.gameRules.getBoolean(GameRule.FIRE_DAMAGE)) {
            entity.attack(new EntityDamageByBlockEvent(this, entity, getId() == BLOCK_SOUL_CAMPFIRE ? DamageCause.SOUL_CAMPFIRE : DamageCause.CAMPFIRE, getEntityDamage()));
        }

        if (!(entity instanceof EntityArmorStand)) {
            return;
        }

        EntityCombustByBlockEvent event = new EntityCombustByBlockEvent(this, entity, 5);
        level.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled() && entity.isAlive() && entity.noDamageTicks == 0) {
            entity.setOnFire(event.getDuration());
        }
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(getDamage() & DIRECTION_MASK);
    }

    protected BlockEntityCampfire createBlockEntity(@Nullable Item item) {
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.CAMPFIRE);

        if (item != null && item.hasCustomBlockData()) {
            for (Tag tag : item.getCustomBlockData().getAllTags()) {
                nbt.put(tag.getName(), tag);
            }
        }

        return (BlockEntityCampfire) BlockEntities.createBlockEntity(BlockEntityType.CAMPFIRE, getChunk(), nbt);
    }

    @Nullable
    protected BlockEntityCampfire getBlockEntity() {
        if (level == null) {
            return null;
        }
        BlockEntity blockEntity = level.getBlockEntity(this);
        if (blockEntity instanceof BlockEntityCampfire) {
            return (BlockEntityCampfire) blockEntity;
        }
        return null;
    }

    public boolean isExtinguished() {
        return (getDamage() & EXTINGUISHED_BIT) == EXTINGUISHED_BIT;
    }

    public void setExtinguished(boolean extinguished) {
        setDamage(extinguished ? getDamage() | EXTINGUISHED_BIT : getDamage() & DIRECTION_MASK);
    }

    public boolean tryLightFire() {
        if (!isExtinguished()) {
            return false;
        }

        if (level.getExtraBlock(this).isWaterSource()) {
            return false;
        }

        level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_IGNITE);

        setExtinguished(false);
        if (!level.setBlock(this, this, true)) {
            return false;
        }

        BlockEntityCampfire blockEntity = getBlockEntity();
        if (blockEntity != null) {
            blockEntity.scheduleUpdate();
        }

        return true;
    }

    public boolean tryDouseFire() {
        if (isExtinguished()) {
            return false;
        }

        level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_EXTINGUISH_FIRE);

        setExtinguished(true);
        return level.setBlock(this, this, true);
    }

    @Override
    public boolean isCampfire() {
        return true;
    }

    public boolean tryLightFire(@Nullable Block sourceBlock, @Nullable Entity sourceEntity, BlockIgniteCause cause) {
        if (!isExtinguished()) {
            return false;
        }

        BlockIgniteEvent event = new BlockIgniteEvent(this, sourceBlock, sourceEntity, cause);
        level.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }

        return tryLightFire();
    }

    protected int getEntityDamage() {
        return 1;
    }

    protected RecipeTag getRecipeTag() {
        return RecipeTag.CAMPFIRE;
    }
}
