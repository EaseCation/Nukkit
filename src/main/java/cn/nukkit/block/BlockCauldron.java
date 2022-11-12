package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityCauldron;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.player.PlayerBucketEmptyEvent;
import cn.nukkit.event.player.PlayerBucketFillEvent;
import cn.nukkit.item.*;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.network.protocol.LevelEventPacket;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * author: CreeperFace
 * Nukkit Project
 */
public class BlockCauldron extends BlockTransparentMeta {
    public static final int FILL_LEVEL_MASK = 0b111;
    public static final int LIQUID_TYPE_MASK = 0b11000;
    public static final int LIQUID_TYPE_OFFSET = 3;

    public static final int FILL_LEVEL_EMPTY = 0;
    public static final int FILL_LEVEL_FULL = 6;

    public static final int LIQUID_WATER = 0;
    public static final int LIQUID_LAVA = 1;
    public static final int LIQUID_POWDER_SNOW = 2;

    public BlockCauldron() {
        this(0);
    }

    public BlockCauldron(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BLOCK_CAULDRON;
    }

    @Override
    public String getName() {
        return "Cauldron Block";
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.CAULDRON;
    }

    @Override
    public double getResistance() {
        return 10;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    public boolean isFull() {
        return this.getFillLevel() == FILL_LEVEL_FULL;
    }

    public boolean isEmpty() {
        return this.getFillLevel() == FILL_LEVEL_EMPTY;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, Player player) {
        BlockEntity be = this.level.getBlockEntity(this);

        if (!(be instanceof BlockEntityCauldron)) {
            return false;
        }

        BlockEntityCauldron cauldron = (BlockEntityCauldron) be;

        switch (item.getId()) {
            case Item.BUCKET:
                if (item.getDamage() == ItemBucket.EMPTY_BUCKET) {
                    if (!isFull() || cauldron.getPotionType() != BlockEntityCauldron.POTION_TYPE_NONE) {
                        return true;
                    }

                    ItemBucket bucket = (ItemBucket) item.clone();
                    bucket.setCount(1);

                    int levelEvent;
                    switch (getCauldronType()) {
                        case LIQUID_WATER:
                            bucket.setDamage(ItemBucket.WATER_BUCKET);
                            levelEvent = LevelEventPacket.EVENT_CAULDRON_TAKE_WATER;
                            break;
                        case LIQUID_LAVA:
                            bucket.setDamage(ItemBucket.LAVA_BUCKET);
                            levelEvent = LevelEventPacket.EVENT_CAULDRON_TAKE_LAVA;
                            break;
                        case LIQUID_POWDER_SNOW:
                            bucket.setDamage(ItemBucket.POWDER_SNOW_BUCKET);
                            levelEvent = LevelEventPacket.EVENT_CAULDRON_TAKE_POWDER_SNOW;
                            break;
                        default:
                            return true;
                    }

                    PlayerBucketFillEvent ev = new PlayerBucketFillEvent(player, this, null, item, bucket);
                    this.level.getServer().getPluginManager().callEvent(ev);
                    if (ev.isCancelled()) {
                        return true;
                    }

                    replaceBucket(item, player, ev.getItem());

                    this.level.setBlock(this, get(BLOCK_CAULDRON), true);

                    cauldron.clearCustomColor();
                    cauldron.spawnToAll();

                    this.getLevel().addLevelEvent(this.add(0.5, 0.375, 0.5), levelEvent);
                } else if (item.getDamage() == ItemBucket.WATER_BUCKET) {
                    ItemBucket bucket = (ItemBucket) item.clone();
                    bucket.setCount(1);
                    bucket.setDamage(ItemBucket.EMPTY_BUCKET);

                    PlayerBucketEmptyEvent ev = new PlayerBucketEmptyEvent(player, this, null, item, bucket);
                    this.level.getServer().getPluginManager().callEvent(ev);
                    if (ev.isCancelled()) {
                        return true;
                    }

                    replaceBucket(item, player, ev.getItem());

                    int fillLevel = getFillLevel();
                    if (fillLevel != FILL_LEVEL_EMPTY && getCauldronType() != LIQUID_WATER || cauldron.getPotionType() != BlockEntityCauldron.POTION_TYPE_NONE) {
                        mix(cauldron);
                        break;
                    }

                    if (fillLevel != FILL_LEVEL_FULL) {
                        this.level.setBlock(this, get(BLOCK_CAULDRON, FILL_LEVEL_FULL), true);
                    }

                    cauldron.clearCustomColor();
                    cauldron.spawnToAll();

                    this.level.addLevelEvent(this.add(0.5, 0.375 + FILL_LEVEL_FULL * 0.125, 0.5), LevelEventPacket.EVENT_SOUND_SPLASH);
                } else if (item.getDamage() == ItemBucket.LAVA_BUCKET) {
                    ItemBucket bucket = (ItemBucket) item.clone();
                    bucket.setCount(1);
                    bucket.setDamage(ItemBucket.EMPTY_BUCKET);

                    PlayerBucketEmptyEvent ev = new PlayerBucketEmptyEvent(player, this, null, item, bucket);
                    level.getServer().getPluginManager().callEvent(ev);
                    if (ev.isCancelled()) {
                        return true;
                    }

                    replaceBucket(item, player, ev.getItem());

                    int fillLevel = getFillLevel();
                    if (fillLevel != FILL_LEVEL_EMPTY && getCauldronType() != LIQUID_LAVA) {
                        mix(cauldron);
                        break;
                    }

                    if (fillLevel != FILL_LEVEL_FULL) {
                        setCauldronType(LIQUID_LAVA);
                        setFillLevel(FILL_LEVEL_FULL);
                        level.setBlock(this, get(LAVA_CAULDRON, getDamage()), true);
                    }

                    level.addLevelEvent(add(0.5, 0.375 + FILL_LEVEL_FULL * 0.125, 0.5), LevelEventPacket.EVENT_CAULDRON_FILL_LAVA);
                } else if (item.getDamage() == ItemBucket.POWDER_SNOW) {
                    ItemBucket bucket = (ItemBucket) item.clone();
                    bucket.setCount(1);
                    bucket.setDamage(ItemBucket.EMPTY_BUCKET);

                    PlayerBucketEmptyEvent ev = new PlayerBucketEmptyEvent(player, this, null, item, bucket);
                    level.getServer().getPluginManager().callEvent(ev);
                    if (ev.isCancelled()) {
                        return true;
                    }

                    replaceBucket(item, player, ev.getItem());

                    int fillLevel = getFillLevel();
                    if (fillLevel != FILL_LEVEL_EMPTY && getCauldronType() != LIQUID_POWDER_SNOW) {
                        mix(cauldron);
                        break;
                    }

                    if (fillLevel != FILL_LEVEL_FULL) {
                        setCauldronType(LIQUID_POWDER_SNOW);
                        setFillLevel(FILL_LEVEL_FULL);
                        level.setBlock(this, get(BLOCK_CAULDRON, getDamage()), true);
                    }

                    level.addLevelEvent(add(0.5, 0.375 + FILL_LEVEL_FULL * 0.125, 0.5), LevelEventPacket.EVENT_CAULDRON_FILL_POWDER_SNOW);
                }
                break;
            case Item.DYE:
                if (getCauldronType() != LIQUID_WATER) {
                    return true;
                }
                //TODO
                return true;
            case Item.LEATHER_HELMET:
            case Item.LEATHER_CHESTPLATE:
            case Item.LEATHER_LEGGINGS:
            case Item.LEATHER_BOOTS:
            case Item.LEATHER_HORSE_ARMOR:
                if (getCauldronType() != LIQUID_WATER) {
                    return true;
                }
                //TODO
                return true;
//                break;
            case Item.SHULKER_BOX:
                if (getCauldronType() != LIQUID_WATER) {
                    return true;
                }
                //TODO
                return true;
//                break;
            case Item.BANNER:
                if (getCauldronType() != LIQUID_WATER) {
                    return true;
                }
                //TODO
                return true;
//                break;
            case Item.POTION:
                if (!fillPotion(cauldron, BlockEntityCauldron.POTION_TYPE_NORMAL, item, player)) {
                    return true;
                }
                break;
            case Item.SPLASH_POTION:
                if (!fillPotion(cauldron, BlockEntityCauldron.POTION_TYPE_SPLASH, item, player)) {
                    return true;
                }
                break;
            case Item.LINGERING_POTION:
                if (!fillPotion(cauldron, BlockEntityCauldron.POTION_TYPE_LINGERING, item, player)) {
                    return true;
                }
                break;
            case Item.GLASS_BOTTLE:
                if (getCauldronType() != LIQUID_WATER) {
                    return true;
                }

                int fillLevel = getFillLevel();
                if (fillLevel <= 1) {
                    return true;
                }
                fillLevel -= 2;
                this.setFillLevel(fillLevel);
                level.setBlock(this, this, true);

                Item potion;
                switch (cauldron.getPotionType()) {
                    default:
                    case BlockEntityCauldron.POTION_TYPE_NONE:
                    case BlockEntityCauldron.POTION_TYPE_NORMAL:
                        potion = Item.get(Item.POTION);
                        break;
                    case BlockEntityCauldron.POTION_TYPE_SPLASH:
                        potion = Item.get(Item.SPLASH_POTION);
                        break;
                    case BlockEntityCauldron.POTION_TYPE_LINGERING:
                        potion = Item.get(Item.LINGERING_POTION);
                        break;
                }

                int potionId = cauldron.getPotionId();
                if (potionId != -1) {
                    potion.setDamage(potionId);
                }

                if (item.getCount() == 1 && !player.isCreative()) {
                    player.getInventory().setItemInHand(potion);
                } else {
                    if (!player.isCreative()) {
                        item.pop();
                    }
                    player.getInventory().setItemInHand(item);

                    for (Item drop : player.getInventory().addItem(potion)) {
                        player.level.dropItem(player.add(0, 1.3, 0), drop, player.getDirectionVector().multiply(0.4));
                    }
                }

                if (fillLevel == FILL_LEVEL_EMPTY) {
                    cauldron.resetCauldron();
                    cauldron.spawnToAll();
                }

                this.level.addLevelEvent(this.add(0.5, 0.375 + fillLevel * 0.125, 0.5), LevelEventPacket.EVENT_CAULDRON_TAKE_POTION);
                break;
            default:
                return false;
        }

        this.level.updateComparatorOutputLevel(this);
        return true;
    }

    protected void mix(BlockEntityCauldron cauldron) {
        level.setBlock(this, get(BLOCK_CAULDRON), true);

        cauldron.resetCauldron();
        cauldron.spawnToAll();

        level.addLevelEvent(add(0.5, 0.375, 0.5), LevelEventPacket.EVENT_SOUND_EXPLODE);
    }

    private boolean fillPotion(BlockEntityCauldron cauldron, int potionType, Item potion, Player player) {
        if (getCauldronType() != LIQUID_WATER) {
            mix(cauldron);
            return true;
        }

        int fillLevel = getFillLevel();

        int potionId = potion.getDamage();
        if (potionId == ItemPotion.NO_EFFECTS) {
            if (cauldron.getPotionType() != BlockEntityCauldron.POTION_TYPE_NONE) {
                mix(cauldron);
                return true;
            }
        } else if (fillLevel != FILL_LEVEL_EMPTY && cauldron.getPotionId() != potionId) {
            mix(cauldron);
            return true;
        }

        if (fillLevel == FILL_LEVEL_FULL && potionId != ItemPotion.NO_EFFECTS) {
            return false;
        }
        fillLevel = Math.min(fillLevel + 2, FILL_LEVEL_FULL);
        setFillLevel(fillLevel);
        level.setBlock(this, this, true);

        if (potionId != ItemPotion.NO_EFFECTS) {
            cauldron.setPotionType(potionType);
            cauldron.setPotionId(potionId);
            cauldron.spawnToAll();
        }

        if (potion.getCount() == 1 && !player.isCreative()) {
            player.getInventory().setItemInHand(new ItemBlock(Blocks.air()));
        } else {
            if (!player.isCreative()) {
                potion.pop();
            }
            player.getInventory().setItemInHand(potion);

            for (Item drop : player.getInventory().addItem(Item.get(Item.GLASS_BOTTLE))) {
                player.level.dropItem(player.add(0, 1.3, 0), drop, player.getDirectionVector().multiply(0.4));
            }
        }

        level.addLevelEvent(add(0.5, 0.375 + fillLevel * 0.125, 0.5), LevelEventPacket.EVENT_CAULDRON_FILL_POTION);
        return true;
    }

    protected void replaceBucket(Item oldBucket, Player player, Item newBucket) {
        if (oldBucket.getCount() == 1 && !player.isCreative()) {
            player.getInventory().setItemInHand(newBucket);
        } else {
            if (!player.isCreative()) {
                oldBucket.pop();
            }

            for (Item drop : player.getInventory().addItem(newBucket)) {
                player.level.dropItem(player.add(0, 1.3, 0), drop, player.getDirectionVector().multiply(0.4));
            }
        }
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.CAULDRON)
                .putShort("PotionId", -1)
                .putShort("PotionType", BlockEntityCauldron.POTION_TYPE_NONE);

        if (item.hasCustomBlockData()) {
            Map<String, Tag> customData = item.getCustomBlockData().getTags();
            for (Map.Entry<String, Tag> tag : customData.entrySet()) {
                nbt.put(tag.getKey(), tag.getValue());
            }
        }

        BlockEntityCauldron cauldron = (BlockEntityCauldron) BlockEntity.createBlockEntity(BlockEntity.CAULDRON, this.getChunk(), nbt);
        if (cauldron == null) {
            return false;
        }
        this.getLevel().setBlock(block, this, true, true);
        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{new ItemCauldron()};
        }

        return new Item[0];
    }

    @Override
    public Item toItem(boolean addUserData) {
        Item item = Item.get(Item.CAULDRON);
        if (addUserData) {
            BlockEntity blockEntity = getBlockEntity();
            if (blockEntity != null) {
                item.setCustomName(blockEntity.getName());
                item.setRepairCost(blockEntity.getRepairCost());
            }
        }
        return item;
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        return this.getFillLevel();
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            level.scheduleUpdate(this, 1);
            return Level.BLOCK_UPDATE_NORMAL;
        }

        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (getCauldronType() != LIQUID_WATER) {
                return 0;
            }

            Block above = up();
            if (above.isAir() || !above.isWater() && (!above.canContainWater() || !level.getExtraBlock(above).isWater())) {
                return 0;
            }

            BlockEntityCauldron cauldron = getBlockEntity();
            if (cauldron.getPotionType() == BlockEntityCauldron.POTION_TYPE_NONE && getFillLevel() == FILL_LEVEL_FULL) {
                return 0;
            }

            setFillLevel(FILL_LEVEL_FULL);
            level.setBlock(this, this, true);

            cauldron.resetCauldron();
            cauldron.spawnToAll();

            return Level.BLOCK_UPDATE_NORMAL;
        }

        return 0;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        //TODO
    }

    protected BlockEntityCauldron createBlockEntity(@Nullable Item item) {
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.CAULDRON);

        if (item != null && item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
        }

        return (BlockEntityCauldron) BlockEntity.createBlockEntity(BlockEntity.CAULDRON, getChunk(), nbt);
    }

    protected BlockEntityCauldron getBlockEntity() {
        if (level == null) {
            return null;
        }
        BlockEntity blockEntity = level.getBlockEntity(this);
        if (blockEntity instanceof BlockEntityCauldron) {
            return (BlockEntityCauldron) blockEntity;
        }
        return null;
    }

    public int getFillLevel() {
        return getDamage() & FILL_LEVEL_MASK;
    }

    public void setFillLevel(int level) {
        setDamage((getDamage() & ~FILL_LEVEL_MASK) | (level & FILL_LEVEL_MASK));
    }

    public int getCauldronType() {
        return (getDamage() & LIQUID_TYPE_MASK) >> LIQUID_TYPE_OFFSET;
    }

    public void setCauldronType(int type) {
        setDamage((getDamage() & ~LIQUID_TYPE_MASK) | (type << LIQUID_TYPE_OFFSET));
    }
}
