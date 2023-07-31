package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.event.block.BlockBurnEvent;
import cn.nukkit.event.block.BlockFadeEvent;
import cn.nukkit.event.block.BlockIgniteEvent;
import cn.nukkit.event.block.BlockIgniteEvent.BlockIgniteCause;
import cn.nukkit.event.entity.EntityCombustByBlockEvent;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.item.Item;
import cn.nukkit.item.Items;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockFire extends BlockFlowable {

    public BlockFire() {
        this(0);
    }

    public BlockFire(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return FIRE;
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public String getName() {
        return "Fire";
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public boolean isBreakable(Item item) {
        return false;
    }

    @Override
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        if (!(entity instanceof Player) || entity.getLevel().getGameRules().getBoolean(GameRule.FIRE_DAMAGE)) {
            entity.attack(new EntityDamageByBlockEvent(this, entity, DamageCause.FIRE, 1));
        }

        EntityCombustByBlockEvent ev = new EntityCombustByBlockEvent(this, entity, 8);
        if (entity instanceof EntityArrow) {
            ev.setCancelled();
        }
        Server.getInstance().getPluginManager().callEvent(ev);
        if (!ev.isCancelled() && entity.isAlive() && entity.noDamageTicks == 0) {
            entity.setOnFire(ev.getDuration());
        }
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[0];
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_RANDOM) {
            if (!this.isValidBase(this.down()) && !this.canNeighborBurn()) {
                BlockFadeEvent event = new BlockFadeEvent(this, get(AIR));
                level.getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    level.setBlock(this, event.getNewState(), true);
                }
            }

            return Level.BLOCK_UPDATE_NORMAL;
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED && this.level.gameRules.getBoolean(GameRule.DO_FIRE_TICK)) {
            Block down = down();
            boolean forever = down.getId() == Block.NETHERRACK || down.getId() == Block.MAGMA;

            ThreadLocalRandom random = ThreadLocalRandom.current();

            //TODO: END

            if (!this.isValidBase(down) && !this.canNeighborBurn()) {
                BlockFadeEvent event = new BlockFadeEvent(this, get(AIR));
                level.getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    level.setBlock(this, event.getNewState(), true);
                }
            }

            if (!forever && this.getLevel().isRaining() &&
                    (this.getLevel().canBlockSeeSky(this) ||
                            this.getLevel().canBlockSeeSky(this.eastVec()) ||
                            this.getLevel().canBlockSeeSky(this.westVec()) ||
                            this.getLevel().canBlockSeeSky(this.southVec()) ||
                            this.getLevel().canBlockSeeSky(this.northVec()))
                    ) {
                BlockFadeEvent event = new BlockFadeEvent(this, get(AIR));
                level.getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    level.setBlock(this, event.getNewState(), true);
                }
            } else {
                int meta = this.getDamage();

                if (meta < 0xf) {
                    int newMeta = meta + random.nextInt(3);
                    this.setDamage(Math.min(newMeta, 0xf));
                    this.getLevel().setBlock(this, this, true);
                }

                this.getLevel().scheduleRandomUpdate(this, this.tickRate() + random.nextInt(10));

                if (!forever && !this.canNeighborBurn()) {
                    if (!this.isValidBase(this.down()) || meta > 3) {
                        BlockFadeEvent event = new BlockFadeEvent(this, get(AIR));
                        level.getServer().getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            level.setBlock(this, event.getNewState(), true);
                        }
                    }
                } else if (!forever && !(this.down().getBurnAbility() > 0) && meta == 0xf && random.nextInt(4) == 0) {
                    BlockFadeEvent event = new BlockFadeEvent(this, get(AIR));
                    level.getServer().getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        level.setBlock(this, event.getNewState(), true);
                    }
                } else {
                    int o = 0;

                    //TODO: decrease the o if the rainfall values are high

                    this.tryToCatchBlockOnFire(this.east(), 300 + o, meta);
                    this.tryToCatchBlockOnFire(this.west(), 300 + o, meta);
                    this.tryToCatchBlockOnFire(this.down(), 250 + o, meta);
                    this.tryToCatchBlockOnFire(this.up(), 250 + o, meta);
                    this.tryToCatchBlockOnFire(this.south(), 300 + o, meta);
                    this.tryToCatchBlockOnFire(this.north(), 300 + o, meta);

                    for (int x = (int) (this.x - 1); x <= (int) (this.x + 1); ++x) {
                        for (int z = (int) (this.z - 1); z <= (int) (this.z + 1); ++z) {
                            for (int y = (int) (this.y - 1); y <= (int) (this.y + 4); ++y) {
                                if (x != (int) this.x || y != (int) this.y || z != (int) this.z) {
                                    int k = 100;

                                    if (y > this.y + 1) {
                                        k += (y - (this.y + 1)) * 100;
                                    }

                                    Block block = this.getLevel().getBlock(new Vector3(x, y, z));
                                    int chance = this.getChanceOfNeighborsEncouragingFire(block);

                                    if (chance > 0) {
                                        int t = (chance + 40 + this.getLevel().getServer().getDifficulty() * 7) / (meta + 30);

                                        //TODO: decrease the t if the rainfall values are high

                                        if (t > 0 && random.nextInt(k) <= t) {
                                            int damage = meta + random.nextInt(5) / 4;

                                            if (damage > 0xf) {
                                                damage = 0xf;
                                            }

                                            BlockIgniteEvent e = new BlockIgniteEvent(block, this, null, BlockIgniteEvent.BlockIgniteCause.SPREAD);
                                            this.level.getServer().getPluginManager().callEvent(e);

                                            if (!e.isCancelled()) {
                                                this.getLevel().setBlock(block, Block.get(BlockID.FIRE, damage), true);
                                                this.getLevel().scheduleRandomUpdate(block, this.tickRate());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return 0;
    }

    private void tryToCatchBlockOnFire(Block block, int bound, int damage) {
        BlockCampfire campfire = block.isCampfire() ? (BlockCampfire) block : null;
        if (campfire != null && !campfire.isExtinguished()) {
            return;
        }

        int burnAbility = block.getBurnAbility();
        Random random = ThreadLocalRandom.current();

        if (random.nextInt(bound) < burnAbility) {
            if (random.nextInt(damage + 10) < 5) {
                int meta = damage + random.nextInt(5) / 4;

                if (meta > 0xf) {
                    meta = 0xf;
                }

                if (campfire != null) {
                    campfire.tryLightFire(this, null, BlockIgniteCause.SPREAD);
                    return;
                }

                tryIgnite(block, this, null, BlockIgniteCause.SPREAD, meta);
            } else if (campfire != null) {
                return;
            } else {
                BlockBurnEvent ev = new BlockBurnEvent(block);
                this.getLevel().getServer().getPluginManager().callEvent(ev);

                if (!ev.isCancelled()) {
                    this.getLevel().setBlock(block, Block.get(BlockID.AIR), true);
                }
            }

            if (block instanceof BlockTNT) {
                ((BlockTNT) block).prime();
            }
        }
    }

    private int getChanceOfNeighborsEncouragingFire(Block block) {
        if (block.getId() != AIR) {
            return 0;
        } else {
            int chance = 0;
            chance = Math.max(chance, block.east().getBurnChance());
            chance = Math.max(chance, block.west().getBurnChance());
            chance = Math.max(chance, block.down().getBurnChance());
            chance = Math.max(chance, block.up().getBurnChance());
            chance = Math.max(chance, block.south().getBurnChance());
            chance = Math.max(chance, block.north().getBurnChance());
            return chance;
        }
    }

    private boolean canNeighborBurn() {
        for (BlockFace face : BlockFace.getValues()) {
            if (this.getSide(face).getBurnChance() > 0) {
                return true;
            }
        }

        return false;
    }

    protected boolean isValidBase(Block below) {
        int id = below.getId();
        return id != ICE && id != FROSTED_ICE && id != SNOW_LAYER && !below.isGlass() && (id == MOB_SPAWNER
                || below.getBurnChance() > 0 || SupportType.hasFullSupport(below, BlockFace.UP));
    }

    @Override
    public int tickRate() {
        return 30;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.FIRE_BLOCK_COLOR;
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return this;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Items.air();
    }

    @Override
    public boolean isFire() {
        return true;
    }

    public static boolean tryIgnite(Block target, @Nullable Block sourceBlock, @Nullable Entity sourceEntity, BlockIgniteCause cause) {
        return tryIgnite(target, sourceBlock, sourceEntity, cause, 0);
    }

    private static boolean tryIgnite(Block target, @Nullable Block sourceBlock, @Nullable Entity sourceEntity, BlockIgniteCause cause, int fireAge) {
        Block below = target.down();
        int id = below.getId();

        if (id == OBSIDIAN && target.level.createPortal(below)) {
            return true;
        }

        boolean soulFire = id == SOUL_SAND || id == SOUL_SOIL;
        BlockFire fire = (BlockFire) (soulFire ? get(SOUL_FIRE) : get(FIRE, fireAge));
        fire.position(target);

        if (!soulFire && !fire.isValidBase(below) && !fire.canNeighborBurn()) {
            return false;
        }

        BlockIgniteEvent event = new BlockIgniteEvent(target, sourceBlock, sourceEntity, cause);
        target.level.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }

        target.level.setBlock(target, fire, true);

        if (!soulFire) {
            target.level.scheduleRandomUpdate(fire, fire.tickRate() + ThreadLocalRandom.current().nextInt(10));
        }

        return true;
    }
}
