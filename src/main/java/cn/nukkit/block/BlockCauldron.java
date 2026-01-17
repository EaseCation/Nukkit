package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntities;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityCauldron;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityCombustByBlockEvent;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.player.PlayerBucketEmptyEvent;
import cn.nukkit.event.player.PlayerBucketFillEvent;
import cn.nukkit.item.*;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.network.protocol.UpdateBlockPacket;
import cn.nukkit.potion.Potion;
import cn.nukkit.potion.PotionID;

import javax.annotation.Nullable;
import java.awt.Color;

import static cn.nukkit.GameVersion.*;

/**
 * author: CreeperFace
 * Nukkit Project
 */
public class BlockCauldron extends BlockTransparent {
    public static final int FILL_LEVEL_MASK = 0b111;
    public static final int LIQUID_TYPE_MASK = 0b11000;
    public static final int LIQUID_TYPE_OFFSET = 3;

    public static final int FILL_LEVEL_EMPTY = 0;
    public static final int FILL_LEVEL_FULL = 6;

    public static final int LIQUID_WATER = 0;
    public static final int LIQUID_LAVA = 1;
    public static final int LIQUID_POWDER_SNOW = 2;

    BlockCauldron() {

    }

    @Override
    public int getId() {
        return CAULDRON;
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
    public float getResistance() {
        return 10;
    }

    @Override
    public float getHardness() {
        return 2;
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
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
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        BlockEntityCauldron cauldron = getBlockEntity();
        if (cauldron == null) {
            cauldron = createBlockEntity(null);
            if (cauldron == null) {
                return true;
            }
        }

        switch (item.getId()) {
            case Item.BUCKET: {
                if (!isFull() || cauldron.hasPotion()) {
                    return true;
                }

                int bucketId;
                int levelEvent;
                switch (getCauldronType()) {
                    case LIQUID_WATER:
                        bucketId = Item.WATER_BUCKET;
                        levelEvent = LevelEventPacket.EVENT_CAULDRON_TAKE_WATER;
                        break;
                    case LIQUID_LAVA:
                        bucketId = Item.LAVA_BUCKET;
                        levelEvent = LevelEventPacket.EVENT_CAULDRON_TAKE_LAVA;
                        break;
                    case LIQUID_POWDER_SNOW:
                        bucketId = Item.POWDER_SNOW_BUCKET;
                        levelEvent = LevelEventPacket.EVENT_CAULDRON_TAKE_POWDER_SNOW;
                        break;
                    default:
                        return true;
                }

                PlayerBucketFillEvent ev = new PlayerBucketFillEvent(player, this, null, item, Item.get(bucketId));
                this.level.getServer().getPluginManager().callEvent(ev);
                if (ev.isCancelled()) {
                    return true;
                }

                replaceBucket(item, player, ev.getItem());

                this.level.setBlock(this, get(CAULDRON), true);

                if (cauldron.clearCustomColor()) {
                    cauldron.spawnToAll();
                }

                this.getLevel().addLevelEvent(this.add(0.5, 0.375, 0.5), levelEvent);
                break;
            }
            case Item.WATER_BUCKET: {
                PlayerBucketEmptyEvent ev = new PlayerBucketEmptyEvent(player, this, null, item, Item.get(Item.BUCKET));
                this.level.getServer().getPluginManager().callEvent(ev);
                if (ev.isCancelled()) {
                    return true;
                }

                replaceBucket(item, player, ev.getItem());

                int fillLevel = getFillLevel();
                if (fillLevel != FILL_LEVEL_EMPTY && (getCauldronType() != LIQUID_WATER || cauldron.hasPotion())) {
                    mix(cauldron);
                    break;
                }

                if (fillLevel != FILL_LEVEL_FULL) {
                    this.level.setBlock(this, get(CAULDRON, FILL_LEVEL_FULL), true);
                }

                if (cauldron.clearCustomColor()) {
                    cauldron.spawnToAll();
                }

                this.level.addLevelEvent(this.add(0.5, 0.375 + FILL_LEVEL_FULL * 0.125, 0.5), LevelEventPacket.EVENT_SOUND_SPLASH);
                break;
            }
            case Item.LAVA_BUCKET: {
                PlayerBucketEmptyEvent ev = new PlayerBucketEmptyEvent(player, this, null, item, Item.get(Item.BUCKET));
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
                    level.setBlock(this, V1_20_0.isAvailable() ? this : get(LAVA_CAULDRON, getDamage()), true);
                }

                level.addLevelEvent(add(0.5, 0.375 + FILL_LEVEL_FULL * 0.125, 0.5), LevelEventPacket.EVENT_CAULDRON_FILL_LAVA);
                break;
            }
            case Item.POWDER_SNOW_BUCKET: {
                PlayerBucketEmptyEvent ev = new PlayerBucketEmptyEvent(player, this, null, item, Item.get(Item.BUCKET));
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
                    level.setBlock(this, get(CAULDRON, getDamage()), true);
                }

                level.addLevelEvent(add(0.5, 0.375 + FILL_LEVEL_FULL * 0.125, 0.5), LevelEventPacket.EVENT_CAULDRON_FILL_POWDER_SNOW);
                break;
            }
            case Item.LEATHER_HELMET:
            case Item.LEATHER_CHESTPLATE:
            case Item.LEATHER_LEGGINGS:
            case Item.LEATHER_BOOTS: {
                if (getCauldronType() != LIQUID_WATER) {
                    return true;
                }
                if (cauldron.hasPotion()) {
                    return true;
                }
                int fillLevel = getFillLevel();
                if (fillLevel == FILL_LEVEL_EMPTY) {
                    return true;
                }

                Color color = cauldron.getCustomColor();
                ItemColorArmor armor = (ItemColorArmor) item;
                if (color != null) {
                    armor.setColor(color);
                    level.addLevelEvent(add(0.5, 0.375 + fillLevel * 0.125, 0.5), LevelEventPacket.EVENT_CAULDRON_DYE_ARMOR, color.getRGB());
                } else if (!armor.clearColor()) {
                    return true;
                } else {
                    level.addLevelEvent(add(0.5, 0.375 + fillLevel * 0.125, 0.5), LevelEventPacket.EVENT_CAULDRON_CLEAN_ARMOR);
                }

                fillLevel -= 1;
                if (fillLevel < 2) {
                    fillLevel = 0;
                }
                setFillLevel(fillLevel);
                level.setBlock(this, this, true);
                if (fillLevel == FILL_LEVEL_EMPTY && cauldron.clearCustomColor()) {
                    cauldron.spawnToAll();
                }

                player.getInventory().setItemInHand(item);
                break;
            }
            case Item.LEATHER_HORSE_ARMOR: {
                if (getCauldronType() != LIQUID_WATER) {
                    return true;
                }
                if (cauldron.hasPotion()) {
                    return true;
                }
                int fillLevel = getFillLevel();
                if (fillLevel == FILL_LEVEL_EMPTY) {
                    return true;
                }

                Color color = cauldron.getCustomColor();
                ItemHorseArmorLeather armor = (ItemHorseArmorLeather) item;
                if (color != null) {
                    armor.setColor(color);
                    level.addLevelEvent(add(0.5, 0.375 + fillLevel * 0.125, 0.5), LevelEventPacket.EVENT_CAULDRON_DYE_ARMOR, color.getRGB());
                } else if (!armor.clearColor()) {
                    return true;
                } else {
                    level.addLevelEvent(add(0.5, 0.375 + fillLevel * 0.125, 0.5), LevelEventPacket.EVENT_CAULDRON_CLEAN_ARMOR);
                }

                fillLevel -= 1;
                if (fillLevel < 2) {
                    fillLevel = 0;
                }
                setFillLevel(fillLevel);
                level.setBlock(this, this, true);
                if (fillLevel == FILL_LEVEL_EMPTY && cauldron.clearCustomColor()) {
                    cauldron.spawnToAll();
                }

                player.getInventory().setItemInHand(item);
                break;
            }
            case Item.WOLF_ARMOR: {
                if (getCauldronType() != LIQUID_WATER) {
                    return true;
                }
                if (cauldron.hasPotion()) {
                    return true;
                }
                int fillLevel = getFillLevel();
                if (fillLevel == FILL_LEVEL_EMPTY) {
                    return true;
                }

                Color color = cauldron.getCustomColor();
                ItemWolfArmor armor = (ItemWolfArmor) item;
                if (color != null) {
                    armor.setColor(color);
                    level.addLevelEvent(add(0.5, 0.375 + fillLevel * 0.125, 0.5), LevelEventPacket.EVENT_CAULDRON_DYE_ARMOR, color.getRGB());
                } else if (!armor.clearColor()) {
                    return true;
                } else {
                    level.addLevelEvent(add(0.5, 0.375 + fillLevel * 0.125, 0.5), LevelEventPacket.EVENT_CAULDRON_CLEAN_ARMOR);
                }

                fillLevel -= 1;
                if (fillLevel < 2) {
                    fillLevel = 0;
                }
                setFillLevel(fillLevel);
                level.setBlock(this, this, true);
                if (fillLevel == FILL_LEVEL_EMPTY && cauldron.clearCustomColor()) {
                    cauldron.spawnToAll();
                }

                player.getInventory().setItemInHand(item);
                break;
            }
            case ItemBlockID.WHITE_SHULKER_BOX:
            case ItemBlockID.ORANGE_SHULKER_BOX:
            case ItemBlockID.MAGENTA_SHULKER_BOX:
            case ItemBlockID.LIGHT_BLUE_SHULKER_BOX:
            case ItemBlockID.YELLOW_SHULKER_BOX:
            case ItemBlockID.LIME_SHULKER_BOX:
            case ItemBlockID.PINK_SHULKER_BOX:
            case ItemBlockID.GRAY_SHULKER_BOX:
            case ItemBlockID.LIGHT_GRAY_SHULKER_BOX:
            case ItemBlockID.CYAN_SHULKER_BOX:
            case ItemBlockID.PURPLE_SHULKER_BOX:
            case ItemBlockID.BLUE_SHULKER_BOX:
            case ItemBlockID.BROWN_SHULKER_BOX:
            case ItemBlockID.GREEN_SHULKER_BOX:
            case ItemBlockID.RED_SHULKER_BOX:
            case ItemBlockID.BLACK_SHULKER_BOX: {
                if (getCauldronType() != LIQUID_WATER) {
                    return true;
                }
                if (cauldron.hasPotion()) {
                    return true;
                }
                if (cauldron.isCustomColor()) {
                    return true;
                }
                int fillLevel = getFillLevel();
                if (fillLevel == FILL_LEVEL_EMPTY) {
                    return true;
                }

                fillLevel -= 1;
                setFillLevel(fillLevel);
                level.setBlock(this, this, true);

                player.getInventory().setItemInHand(Item.get(ItemBlockID.UNDYED_SHULKER_BOX, 0, 1, item.getCompoundTag()));

                level.addLevelEvent(add(0.5, 0.375 + fillLevel * 0.125, 0.5), LevelEventPacket.EVENT_CAULDRON_CLEAN_ARMOR);
                break;
            }
            case Item.BANNER: {
                if (getCauldronType() != LIQUID_WATER) {
                    return true;
                }
                if (cauldron.hasPotion()) {
                    return true;
                }
                if (cauldron.isCustomColor()) {
                    return true;
                }
                int fillLevel = getFillLevel();
                if (fillLevel == FILL_LEVEL_EMPTY) {
                    return true;
                }

                ItemBanner banner = (ItemBanner) item;
                if (!banner.isDefaultBanner()) {
                    return true;
                }

                ItemBanner cleanBanner = (ItemBanner) banner.clone();
                if (!cleanBanner.removeLastPattern()) {
                    return true;
                }
                cleanBanner.setCount(1);

                fillLevel -= 1;
                setFillLevel(fillLevel);
                level.setBlock(this, this, true);

                if (!player.isCreative() && item.getCount() == 1) {
                    player.getInventory().setItemInHand(cleanBanner);
                } else {
                    if (!player.isCreative()) {
                        item.pop();
                        player.getInventory().setItemInHand(item);
                    }

                    player.getInventory().addItemOrDrop(cleanBanner);
                }

                level.addLevelEvent(add(0.5, 0.375 + fillLevel * 0.125, 0.5), LevelEventPacket.EVENT_CAULDRON_CLEAN_BANNER);
                break;
            }
            case Item.ARROW: {
                if (item.getDamage() != ItemArrow.NORMAL_ARROW) {
                    return true;
                }
                if (getCauldronType() != LIQUID_WATER) {
                    return true;
                }
                if (cauldron.isCustomColor()) {
                    return true;
                }
                int potionId = cauldron.getPotionId();
                if (!Potion.isValidPotion(potionId)) {
                    return true;
                }
                int fillLevel = getFillLevel();
                if (fillLevel < 1) {
                    return true;
                }

                int arrowCount = item.getCount();
                int convertCount = Math.min(arrowCount, (fillLevel < 3 ? 1 : fillLevel - 2) << 4);

                fillLevel -= (convertCount + 15) >> 4;
                if (fillLevel <= 2) {
                    fillLevel = 0;
                }
                setFillLevel(fillLevel);
                level.setBlock(this, this, true);
                if (fillLevel == FILL_LEVEL_EMPTY) {
                    cauldron.resetCauldron();
                    cauldron.spawnToAll();
                }

                Item tippedArrow = Item.get(Item.ARROW, ItemArrow.TIPPED_ARROW + potionId, convertCount);
                if (!player.isCreative() && arrowCount == convertCount) {
                    player.getInventory().setItemInHand(tippedArrow);
                } else {
                    if (!player.isCreative()) {
                        item.shrink(convertCount);
                        player.getInventory().setItemInHand(item);
                    }

                    player.getInventory().addItemOrDrop(tippedArrow);
                }
                break;
            }
            case Item.POTION: {
                if (!fillPotion(cauldron, BlockEntityCauldron.POTION_TYPE_NORMAL, item, player)) {
                    return true;
                }
                break;
            }
            case Item.SPLASH_POTION: {
                if (!fillPotion(cauldron, BlockEntityCauldron.POTION_TYPE_SPLASH, item, player)) {
                    return true;
                }
                break;
            }
            case Item.LINGERING_POTION: {
                if (!fillPotion(cauldron, BlockEntityCauldron.POTION_TYPE_LINGERING, item, player)) {
                    return true;
                }
                break;
            }
            case Item.GLASS_BOTTLE: {
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

                    player.getInventory().addItemOrDrop(potion);
                }

                if (fillLevel == FILL_LEVEL_EMPTY) {
                    cauldron.resetCauldron();
                    cauldron.spawnToAll();
                }

                Color color = cauldron.getCustomColor();
                this.level.addLevelEvent(this.add(0.5, 0.375 + fillLevel * 0.125, 0.5), LevelEventPacket.EVENT_CAULDRON_TAKE_POTION, color != null ? color.getRGB() : 0);
                break;
            }
            default:
                if (item.isDye()) {
                    if (getCauldronType() != LIQUID_WATER) {
                        return true;
                    }
                    if (cauldron.hasPotion()) {
                        return true;
                    }
                    int fillLevel = getFillLevel();
                    if (fillLevel == FILL_LEVEL_EMPTY) {
                        return true;
                    }

                    if (cauldron.mixColor(((ItemDye) item).getDyeColor().getColor())) {
                        Player[] players = level.getChunkPlayers(getChunkX(), getChunkZ()).values().toArray(new Player[0]);
                        BlockCauldron hackBlock = (BlockCauldron) clone();
                        hackBlock.setFillLevel(fillLevel - 1);
                        level.sendBlocks(players, new Block[]{hackBlock}, UpdateBlockPacket.FLAG_ALL, 0); // vanilla hack...
                        level.sendBlocks(players, new Block[]{this}, UpdateBlockPacket.FLAG_ALL_PRIORITY, 0);
                        cauldron.spawnToAll();
                    }

                    if (!player.isCreative()) {
                        item.pop();
                        player.getInventory().setItemInHand(item);
                    }

                    level.addLevelEvent(add(0.5, 0.375 + fillLevel * 0.125, 0.5), LevelEventPacket.EVENT_CAULDRON_ADD_DYE, cauldron.getCustomColor().getRGB());
                    return true;
                }
                return false;
        }

        this.level.updateComparatorOutputLevel(this);
        return true;
    }

    protected void mix(BlockEntityCauldron cauldron) {
        level.setBlock(this, get(CAULDRON), true);

        cauldron.resetCauldron();
        cauldron.spawnToAll();

        level.addLevelEvent(add(0.5, 0.375, 0.5), LevelEventPacket.EVENT_SOUND_EXPLODE);
    }

    private boolean fillPotion(BlockEntityCauldron cauldron, int potionType, Item potion, Player player) {
        int potionId = potion.getDamage();
        if (potionId > Potion.WATER && potionId <= Potion.AWKWARD) {
            return false;
        }

        if (getCauldronType() != LIQUID_WATER) {
            mix(cauldron);
            return true;
        }

        int fillLevel = getFillLevel();

        if (potionId == PotionID.WATER) {
            if (fillLevel != FILL_LEVEL_EMPTY && cauldron.hasPotion()) {
                mix(cauldron);
                return true;
            }
        } else if (fillLevel != FILL_LEVEL_EMPTY && (cauldron.isCustomColor() || cauldron.getPotionId() != potionId)) {
            mix(cauldron);
            return true;
        }

        if (fillLevel == FILL_LEVEL_FULL && potionId != PotionID.WATER) {
            return false;
        }

        fillLevel = Math.min(fillLevel + 2, FILL_LEVEL_FULL);
        setFillLevel(fillLevel);
        level.setBlock(this, this, true);

        if (potionId != PotionID.WATER) {
            cauldron.setPotionType(potionType);
            cauldron.setPotionId(potionId);
            cauldron.spawnToAll();
        } else if (cauldron.clearCustomColor()) {
            cauldron.spawnToAll();
        }

        Item glassBottle = Item.get(Item.GLASS_BOTTLE);
        if (potion.getCount() == 1 && !player.isCreative()) {
            player.getInventory().setItemInHand(glassBottle);
        } else {
            if (!player.isCreative()) {
                potion.pop();
            }
            player.getInventory().setItemInHand(potion);

            player.getInventory().addItemOrDrop(glassBottle);
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

            player.getInventory().addItemOrDrop(newBucket);
        }
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (!super.place(item, block, target, face, fx, fy, fz, player)) {
            return false;
        }
        createBlockEntity(item);
        return true;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{Item.get(Item.CAULDRON)};
        }

        return new Item[0];
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.CAULDRON);
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
            if (cauldron == null) {
                return 0;
            }
            if (!cauldron.hasPotion() && getFillLevel() == FILL_LEVEL_FULL) {
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
    public AxisAlignedBB[] getCollisionShape(int flags) {
        return new AxisAlignedBB[]{
                new SimpleAxisAlignedBB(x, y, z, x + 1, y + 5 / 16f, z + 1), // bottom
                new SimpleAxisAlignedBB(x, y, z, x + 2 / 16f, y + 1, z + 1), // west
                new SimpleAxisAlignedBB(x, y, z, x + 1, y + 1, z + 2 / 16f), // north
                new SimpleAxisAlignedBB(x + 1 - 2 / 16f, y, z, x + 1, y + 1, z + 1), // east
                new SimpleAxisAlignedBB(x, y, z + 1 - 2 / 16f, x + 1, y + 1, z + 1), // south
        };
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return this.shrink(2 / 16.0, 1 / 16.0, 2 / 16.0);
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        int fillLevel = getFillLevel();
        if (fillLevel == 0) {
            return;
        }

        int type = getCauldronType();
        switch (type) {
            case LIQUID_LAVA:
                EntityCombustByBlockEvent event = new EntityCombustByBlockEvent(this, entity, 8);
                event.call();
                if (!event.isCancelled() && entity.isAlive() && entity.noDamageTicks == 0) {
                    entity.setOnFire(event.getDuration());
                }

                if (!(entity instanceof Player) || entity.level.gameRules.getBoolean(GameRule.FIRE_DAMAGE)) {
                    entity.attack(new EntityDamageByBlockEvent(this, entity, DamageCause.LAVA, 4));
                }
                break;
            case LIQUID_WATER:
            case LIQUID_POWDER_SNOW:
                if (!entity.isOnFire()) {
                    break;
                }

                if (type == LIQUID_WATER) {
                    BlockEntityCauldron cauldron = getBlockEntity();
                    if (cauldron != null && cauldron.hasPotion()) {
                        break;
                    }
                }

                level.addLevelSoundEvent(entity.upVec(), LevelSoundEventPacket.SOUND_FIZZ);
                level.addLevelEvent(entity, LevelEventPacket.EVENT_PARTICLE_FIZZ_EFFECT, 513);
                entity.extinguish();

                setFillLevel(fillLevel - 1);
                setCauldronType(LIQUID_WATER);
                level.setBlock(this, this, true);
                break;
        }
    }

    protected BlockEntityCauldron createBlockEntity(@Nullable Item item) {
        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.CAULDRON)
                .putShort("PotionId", -1)
                .putShort("PotionType", BlockEntityCauldron.POTION_TYPE_NONE);

        if (item != null && item.hasCustomBlockData()) {
            for (Tag tag : item.getCustomBlockData().getAllTags()) {
                nbt.put(tag.getName(), tag);
            }
        }

        return (BlockEntityCauldron) BlockEntities.createBlockEntity(BlockEntityType.CAULDRON, getChunk(), nbt);
    }

    @Nullable
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

    @Override
    public int getLightLevel() {
        return isEmpty() ? 0 : getCauldronType() == LIQUID_LAVA ? 15 : 0;
    }

    @Override
    public int getLightBlock() {
        return isEmpty() ? 3 : getCauldronType() == LIQUID_LAVA ? 14 : 3;
    }

    @Override
    public boolean isCauldron() {
        return true;
    }
}
