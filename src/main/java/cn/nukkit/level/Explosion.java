package cn.nukkit.level;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFire;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockTNT;
import cn.nukkit.block.Blocks;
import cn.nukkit.blockentity.*;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.entity.item.EntityMinecartTNT;
import cn.nukkit.entity.item.EntityPrimedTNT;
import cn.nukkit.entity.item.EntityXPOrb;
import cn.nukkit.event.block.BlockExplodeEvent;
import cn.nukkit.event.block.BlockIgniteEvent.BlockIgniteCause;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityExplodeEvent;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.*;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.Hash;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class Explosion {
    private static final int RAYS = 16;
    private static final float STEP_LEN = 0.3f;

    private final Level level;
    private final Position source;
    private final double size;

    private Collection<Block> affectedBlocks = new ArrayList<>();
    private final LongSet affectedAirs = new LongOpenHashSet();
    private boolean fire;
    private boolean allowUnderwater;

    private final Object what;

    /**
     * Creates explosion at given position with given power.
     *
     * @param center center position
     * @param size the power of explosion
     */
    public Explosion(Position center, double size) {
        this(center, size, null, false);
    }

    /**
     * Creates explosion at given position with given power.
     *
     * @param center center position
     * @param size the power of explosion
     * @param what the source object, used for tracking damage
     */
    public Explosion(Position center, double size, Object what) {
        this(center, size, what, false);
    }

    /**
     * Creates explosion at given position with given power and optionally setting blocks on fire.
     *
     * @param center center position
     * @param size the power of explosion
     * @param fire set blocks on fire
     */
    public Explosion(Position center, double size, boolean fire) {
        this(center, size, null, fire);
    }

    /**
     * Creates explosion at given position with given power and optionally setting blocks on fire.
     *
     * @param center center position
     * @param size the power of explosion
     * @param what the source object, used for tracking damage
     * @param fire set blocks on fire
     */
    public Explosion(Position center, double size, Object what, boolean fire) {
        this.level = center.getLevel();
        this.source = center;
        this.size = Math.max(size, 0);
        this.what = what;
        this.fire = fire;
    }

    public Explosion setAllowUnderwater(boolean allowUnderwater) {
        this.allowUnderwater = allowUnderwater;
        return this;
    }

    /**
     * @return {@code false} if explosion was canceled, otherwise {@code true}
     * @deprecated use {@link #explodeA()} and {@link #explodeB()} instead
     */
    @Deprecated
    public boolean explode() {
        if (explodeA()) {
            return explodeB();
        }
        return false;
    }

    /**
     * Calculates which blocks will be destroyed by this explosion. If {@link #explodeB()} is called without calling this,
     * no blocks will be destroyed.
     *
     * @return {@code true} if success
     */
    public boolean explodeA() {
        if (!allowUnderwater && what instanceof Entity) {
            Entity entity = (Entity) what;
            Block block = level.getBlock(entity);
            if (block.isWater() || level.getExtraBlock(entity).isWater()) {
                return true;
            }
        }

        if (this.size < 0.1) {
            return false;
        }

        Vector3 vector = new Vector3(0, 0, 0);
        ThreadLocalRandom random = ThreadLocalRandom.current();

        Long2ObjectMap<Block> affectedBlocks = new Long2ObjectOpenHashMap<>();
        int mRays = RAYS - 1;
        for (int i = 0; i < RAYS; ++i) {
            for (int j = 0; j < RAYS; ++j) {
                for (int k = 0; k < RAYS; ++k) {
                    if (i == 0 || i == mRays || j == 0 || j == mRays || k == 0 || k == mRays) {
                        vector.setComponents((double) i / (double) mRays * 2d - 1, (double) j / (double) mRays * 2d - 1, (double) k / (double) mRays * 2d - 1);
                        double len = vector.length();
                        vector.setComponents((vector.x / len) * STEP_LEN, (vector.y / len) * STEP_LEN, (vector.z / len) * STEP_LEN);
                        double pointerX = this.source.x;
                        double pointerY = this.source.y;
                        double pointerZ = this.source.z;

                        for (double blastForce = this.size * (random.nextInt(700, 1301)) / 1000d; blastForce > 0; blastForce -= STEP_LEN * 0.75d) {
                            int x = (int) pointerX;
                            int y = (int) pointerY;
                            int z = (int) pointerZ;
                            int blockX = pointerX >= x ? x : x - 1;
                            int blockY = pointerY >= y ? y : y - 1;
                            int blockZ = pointerZ >= z ? z : z - 1;
                            if (!level.getHeightRange().isValidBlockY(blockY)) {
                                break;
                            }
                            Block block = this.level.getBlock(blockX, blockY, blockZ);

                            if (!block.isAir()) {
                                boolean notWaterOnly = false;
                                float resistance = 0;
                                if (!allowUnderwater || !block.isWater()) {
                                    notWaterOnly = true;
                                    resistance = block.getResistance();
                                }
                                Block extraBlock = this.level.getExtraBlock(block);
                                if (!allowUnderwater || !extraBlock.isWater()) {
                                    notWaterOnly |= !extraBlock.isAir();
                                    resistance += extraBlock.getResistance();
                                }

                                if (notWaterOnly) {
                                    blastForce -= (resistance / 5 + 0.3f) * STEP_LEN;
                                }

                                if (blastForce > 0) {
                                    affectedBlocks.putIfAbsent(Hash.hashBlockPos(blockX, blockY, blockZ), block);
                                }
                            } else {
                                this.affectedAirs.add(Hash.hashBlockPos(blockX, blockY, blockZ));
                            }

                            pointerX += vector.x;
                            pointerY += vector.y;
                            pointerZ += vector.z;
                        }
                    }
                }
            }
        }
        this.affectedBlocks.addAll(affectedBlocks.values());
        return true;
    }

    /**
     * Executes the explosion's effects on the world. This includes destroying blocks (if any),
     * harming and knocking back entities, and creating sounds and particles.
     *
     * @return {@code false} if explosion was canceled, otherwise {@code true}
     */
    public boolean explodeB() {
        boolean underwater = false;
        boolean dealDamage = true;
        double yield = (1d / this.size) * 100d;

        if (this.what instanceof Entity) {
            EntityExplodeEvent ev = new EntityExplodeEvent((Entity) this.what, this.source, this.affectedBlocks, yield, this.fire);
            this.level.getServer().getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return false;
            }
            yield = ev.getYield();
            this.affectedBlocks = ev.getBlockList();
            this.fire = ev.isOnFire();

            Entity entity = (Entity) this.what;
            Block block = this.level.getBlock(entity);
            if (block.isWater() || !block.isAir() && block.canContainWater() && this.level.getExtraBlock(block).isWater()) {
                underwater = true;
                if (!allowUnderwater) {
                    dealDamage = false;
                }
            }
        } else if (this.what instanceof Block) {
            BlockExplodeEvent event = new BlockExplodeEvent((Block) this.what, this.affectedBlocks, yield, this.fire);
            this.level.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return false;
            }
            yield = event.getYield();
            this.fire = event.isOnFire();

            Block block = (Block) this.what;
            if (block.canContainWater() && this.level.getExtraBlock(block).isWater()) {
                underwater = true;
            }
        }

        double explosionSize = this.size * 2d;
        double minX = Mth.floor(this.source.x - explosionSize - 1);
        double maxX = Mth.floor(this.source.x + explosionSize + 1);
        double minY = Mth.floor(this.source.y - explosionSize - 1);
        double maxY = Mth.floor(this.source.y + explosionSize + 1);
        double minZ = Mth.floor(this.source.z - explosionSize - 1);
        double maxZ = Mth.floor(this.source.z + explosionSize + 1);

        AxisAlignedBB explosionBB = new SimpleAxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
        Entity[] list = this.level.getNearbyEntities(explosionBB, this.what instanceof Entity ? (Entity) this.what : null);
        for (Entity entity : list) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                if (player.isSpectator()) {
                    continue;
                }
            }

            double distance = entity.distance(this.source) / explosionSize;
            if (distance <= 1) {
                Vector3 motion = entity.subtract(this.source).normalize();
                int exposure = 1;
                double impact = (1 - distance) * exposure;
                int damage = dealDamage || entity instanceof EntityXPOrb ? (int) (((impact * impact + impact) / 2) * 7 * explosionSize + 1) : 0;

                if (this.what instanceof Entity) {
                    entity.attack(new EntityDamageByEntityEvent((Entity) this.what, entity, DamageCause.ENTITY_EXPLOSION, damage));
                } else if (this.what instanceof Block) {
                    entity.attack(new EntityDamageByBlockEvent((Block) this.what, entity, DamageCause.BLOCK_EXPLOSION, damage));
                } else {
                    entity.attack(new EntityDamageEvent(entity, DamageCause.BLOCK_EXPLOSION, damage));
                }

                if (!(entity instanceof EntityXPOrb) && (!(entity instanceof EntityItem) || entity.isInsideOfWater())) {
                    entity.setMotion(motion.multiply(impact));
                }
            }
        }

        boolean blockDrop = this.level.getGameRules().getBoolean(GameRule.DO_TILE_DROPS);
        boolean dropDecay = !(what instanceof EntityPrimedTNT) && !(what instanceof EntityMinecartTNT) || level.gameRules.getBoolean(GameRule.TNT_EXPLOSION_DROP_DECAY);
        List<Vector3> smokePositions = this.affectedBlocks.isEmpty() ? Collections.emptyList() : new ObjectArrayList<>();
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (Block block : this.affectedBlocks) {
            Block extraBlock = level.getExtraBlock(block);
            int id = block.getId();
            if (id == Block.TNT) {
                ((BlockTNT) block).prime(random.nextInt(10, 31), this.what instanceof Entity ? (Entity) this.what : null);
            } else if (block.isChest()) {
                if (blockDrop && (!dropDecay || random.nextFloat() * 100 < yield)) {
                    for (Item drop : block.getDrops(ItemTool.getUniversalTool())) {
                        this.level.dropItem(block.blockCenter(), drop);
                    }
                }
                BlockEntity chest = block.getLevel().getBlockEntity(block);
                if (chest instanceof BlockEntityChest lootable) {
                    lootable.unpackLootTable();
                    if (blockDrop) {
                        for (Item drop : ((BlockEntityChest) chest).getInventory().getContents().values()) {
                            this.level.dropItem(block.blockCenter(), drop);
                        }
                    }
                    ((BlockEntityChest) chest).getInventory().clearAll();
                }
            } else if (block.isFurnace()) {
                if (blockDrop && (!dropDecay || random.nextFloat() * 100 < yield)) {
                    for (Item drop : block.getDrops(ItemTool.getUniversalTool())) {
                        this.level.dropItem(block.blockCenter(), drop);
                    }
                    int xp = block.getDropExp();
                    if (xp > 0) {
                        level.dropExpOrb(block.blockCenter(), xp);
                    }
                }
                BlockEntity container = block.getLevel().getBlockEntity(block);
                if (container instanceof BlockEntityFurnace lootable) {
                    lootable.unpackLootTable();
                    if (blockDrop) {
                        for (Item drop : ((InventoryHolder) container).getInventory().getContents().values()) {
                            this.level.dropItem(block.blockCenter(), drop);
                        }
                    }
                    ((InventoryHolder) container).getInventory().clearAll();
                }
            } else if (id == Block.BREWING_STAND) {
                if (blockDrop && (!dropDecay || random.nextFloat() * 100 < yield)) {
                    for (Item drop : block.getDrops(ItemTool.getUniversalTool())) {
                        this.level.dropItem(block.blockCenter(), drop);
                    }
                }
                BlockEntity container = block.getLevel().getBlockEntity(block);
                if (container instanceof BlockEntityBrewingStand lootable) {
                    lootable.unpackLootTable();
                    if (blockDrop) {
                        for (Item drop : ((InventoryHolder) container).getInventory().getContents().values()) {
                            this.level.dropItem(block.blockCenter(), drop);
                        }
                    }
                    ((InventoryHolder) container).getInventory().clearAll();
                }
            } else if (id == Block.HOPPER) {
                if (blockDrop && (!dropDecay || random.nextFloat() * 100 < yield)) {
                    for (Item drop : block.getDrops(ItemTool.getUniversalTool())) {
                        this.level.dropItem(block.blockCenter(), drop);
                    }
                }
                BlockEntity container = block.getLevel().getBlockEntity(block);
                if (container instanceof BlockEntityHopper lootable) {
                    lootable.unpackLootTable();
                    if (blockDrop) {
                        for (Item drop : ((InventoryHolder) container).getInventory().getContents().values()) {
                            this.level.dropItem(block.blockCenter(), drop);
                        }
                    }
                    ((InventoryHolder) container).getInventory().clearAll();
                }
            } else if (id == Block.DROPPER) {
                if (blockDrop && (!dropDecay || random.nextFloat() * 100 < yield)) {
                    for (Item drop : block.getDrops(ItemTool.getUniversalTool())) {
                        this.level.dropItem(block.blockCenter(), drop);
                    }
                }
                BlockEntity container = block.getLevel().getBlockEntity(block);
                if (container instanceof BlockEntityDropper lootable) {
                    lootable.unpackLootTable();
                    if (blockDrop) {
                        for (Item drop : ((InventoryHolder) container).getInventory().getContents().values()) {
                            this.level.dropItem(block.blockCenter(), drop);
                        }
                    }
                    ((InventoryHolder) container).getInventory().clearAll();
                }
            } else if (id == Block.DISPENSER) {
                if (blockDrop && (!dropDecay || random.nextFloat() * 100 < yield)) {
                    for (Item drop : block.getDrops(ItemTool.getUniversalTool())) {
                        this.level.dropItem(block.blockCenter(), drop);
                    }
                }
                BlockEntity container = block.getLevel().getBlockEntity(block);
                if (container instanceof BlockEntityDispenser lootable) {
                    lootable.unpackLootTable();
                    if (blockDrop) {
                        for (Item drop : ((InventoryHolder) container).getInventory().getContents().values()) {
                            this.level.dropItem(block.blockCenter(), drop);
                        }
                    }
                    ((InventoryHolder) container).getInventory().clearAll();
                }
            } else if (block.isShulkerBox()) {
                BlockEntity shulkerBox = block.getLevel().getBlockEntity(block);
                if (shulkerBox instanceof BlockEntityShulkerBox lootable) {
                    lootable.unpackLootTable();
                    if (blockDrop) {
                        this.level.dropItem(block.blockCenter(), block.toItem(true));
                    }
                    ((BlockEntityShulkerBox) shulkerBox).getInventory().clearAll();
                }
            } else if (id == BlockID.BARREL) {
                if (blockDrop && (!dropDecay || random.nextFloat() * 100 < yield)) {
                    for (Item drop : block.getDrops(ItemTool.getUniversalTool())) {
                        this.level.dropItem(block.blockCenter(), drop);
                    }
                }
                BlockEntity container = block.getLevel().getBlockEntity(block);
                if (container instanceof BlockEntityBarrel lootable) {
                    lootable.unpackLootTable();
                    if (blockDrop) {
                        for (Item drop : ((InventoryHolder) container).getInventory().getContents().values()) {
                            this.level.dropItem(block.blockCenter(), drop);
                        }
                    }
                    ((InventoryHolder) container).getInventory().clearAll();
                }
            } else if (blockDrop) {
                //TODO: merge items before dropping
                if (!dropDecay || id == BlockID.DRAGON_EGG || id == BlockID.BEACON || block.isSkull() || random.nextFloat() * 100 < yield) {
                    for (Item drop : block.getDrops(ItemTool.getUniversalTool())) {
                        this.level.dropItem(block.blockCenter(), drop);
                    }
                    int xp = block.getDropExp();
                    if (xp > 0) {
                        level.dropExpOrb(block.blockCenter(), xp);
                    }
                }
                if (!extraBlock.isAir() && (!dropDecay || random.nextFloat() * 100 < yield)) {
                    for (Item drop : extraBlock.getDrops(ItemTool.getUniversalTool())) {
                        this.level.dropItem(extraBlock.blockCenter(), drop);
                    }
                    int xp = extraBlock.getDropExp();
                    if (xp > 0) {
                        level.dropExpOrb(extraBlock.blockCenter(), xp);
                    }
                }
            }

            if (random.nextInt(8) == 0) {
                smokePositions.add(block);
            }

            if (!extraBlock.isAir()) {
                this.level.setExtraBlock(block, Blocks.air(), true, false);
            }
            if (extraBlock.isWater()) {
                this.level.setBlock(block, extraBlock, true);
            } else if (!block.isWater()) {
                this.level.setBlock(block, Blocks.air(), true);
            }
        }

        if (this.fire) {
            Block sourceBlock = null;
            Entity sourceEntity = null;
            if (what instanceof Block) {
                sourceBlock = (Block) what;
            } else if (what instanceof Entity) {
                sourceEntity = (Entity) what;
            }

            for (Block block : this.affectedBlocks) {
                if (random.nextInt(3) == 0 && (block = this.level.getBlock(block)).isAir()) {
                    BlockFire.tryIgnite(block, sourceBlock, sourceEntity, BlockIgniteCause.EXPLOSION);
                }
            }
            for (long pos : this.affectedAirs) {
                Block block;
                if (random.nextInt(3) == 0 && (block = this.level.getBlock(Hash.hashBlockPosX(pos), Hash.hashBlockPosY(pos), Hash.hashBlockPosZ(pos))).isAir()) {
                    BlockFire.tryIgnite(block, sourceBlock, sourceEntity, BlockIgniteCause.EXPLOSION);
                }
            }
        }

        int count = smokePositions.size();
        CompoundTag data = new CompoundTag()
                .putFloat("originX", (float) this.source.x)
                .putFloat("originY", (float) this.source.y)
                .putFloat("originZ", (float) this.source.z)
                .putFloat("radius", (float) this.size)
                .putInt("size", count);
        if (!underwater) {
            for (int i = 0; i < count; i++) {
                Vector3 pos = smokePositions.get(i);
                String prefix = "pos" + i;
                data.putFloat(prefix + "x", (float) pos.x);
                data.putFloat(prefix + "y", (float) pos.y);
                data.putFloat(prefix + "z", (float) pos.z);
            }
        }
        this.level.addLevelSoundEvent(this.source, LevelSoundEventPacket.SOUND_EXPLODE);
        this.level.addLevelEvent(this.source, LevelEventPacket.EVENT_PARTICLE_EXPLOSION, Math.round((float) this.size));
        this.level.addLevelEvent(this.source, LevelEventPacket.EVENT_PARTICLE_BLOCK_EXPLOSION, data);

        return true;
    }

}
