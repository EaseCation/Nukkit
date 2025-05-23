package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityPrimedTNT;
import cn.nukkit.event.block.BlockIgniteEvent.BlockIgniteCause;
import cn.nukkit.event.entity.EntityCombustByBlockEvent;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.item.Item;
import cn.nukkit.level.Dimension;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockColor;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockLava extends BlockLiquid {

    public BlockLava() {
        this(0);
    }

    public BlockLava(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return FLOWING_LAVA;
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public String getName() {
        return "Flowing Lava";
    }

    @Override
    public void onEntityCollide(Entity entity) {
        entity.highestPosition -= (entity.highestPosition - entity.y) * 0.5;

        EntityCombustByBlockEvent ev = new EntityCombustByBlockEvent(this, entity, 8);
        Server.getInstance().getPluginManager().callEvent(ev);
        if (!ev.isCancelled()
                // Making sure the entity is actually alive and not invulnerable.
                && entity.isAlive()
                && entity.noDamageTicks == 0) {
            entity.setOnFire(ev.getDuration());
        }

        if (!(entity instanceof Player) || entity.level.gameRules.getBoolean(GameRule.FIRE_DAMAGE)) {
            entity.attack(new EntityDamageByBlockEvent(this, entity, DamageCause.LAVA, 4));
        }
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        boolean ret = this.getLevel().setBlock(this, this, true, false);
        this.getLevel().scheduleUpdate(this, this.tickRate());

        return ret;
    }

    @Override
    public int onUpdate(int type) {
        int result = super.onUpdate(type);

        if (type == Level.BLOCK_UPDATE_RANDOM && this.level.gameRules.getBoolean(GameRule.DO_FIRE_TICK)) {
            Random random = ThreadLocalRandom.current();

            int i = random.nextInt(3);

            if (i > 0) {
                for (int k = 0; k < i; ++k) {
                    Vector3 v = this.add(random.nextInt(3) - 1, 1, random.nextInt(3) - 1);
                    Block block = this.getLevel().getBlock(v);

                    if (block.getId() == AIR) {
                        if (this.isSurroundingBlockFlammable(block)) {
                            if (BlockFire.tryIgnite(block, this, null, BlockIgniteCause.LAVA)) {
                                return Level.BLOCK_UPDATE_RANDOM;
                            }

                            return 0;
                        }
                    } else if (block.isSolid()) {
                        return Level.BLOCK_UPDATE_RANDOM;
                    }
                }
            } else {
                for (int k = 0; k < 3; ++k) {
                    Vector3 v = this.add(random.nextInt(3) - 1, 0, random.nextInt(3) - 1);
                    Block block = this.getLevel().getBlock(v);

                    if (block.up().getId() == AIR && block.getBurnChance() > 0) {
                        BlockFire.tryIgnite(block, this, null, BlockIgniteCause.LAVA);
                    }
                }
            }
        }

        return result;
    }

    protected boolean isSurroundingBlockFlammable(Block block) {
        for (BlockFace face : BlockFace.getValues()) {
            if (block.getSide(face).getBurnChance() > 0) {
                return true;
            }
        }

        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.FIRE_BLOCK_COLOR;
    }

    @Override
    public BlockLiquid getBlock(int meta) {
        return (BlockLiquid) Block.get(BlockID.FLOWING_LAVA, meta);
    }

    @Override
    public int tickRate() {
        if (this.level.getDimension() == Dimension.NETHER) {
            return 1;
        }
        return 30;
    }

    @Override
    public int getFlowDecayPerBlock() {
        if (this.level.getDimension() == Dimension.NETHER) {
            return 1;
        }
        return 2;
    }

    @Override
    protected void checkForHarden() {
        Block colliding = null;
        int layer = -1;
        Block blueIce = null;

        for (int side = 1; side < 6; ++side) { //don't check downwards side
            Block blockSide = this.getSide(BlockFace.fromIndex(side));
            if (blockSide.isWater()) {
                colliding = blockSide;
                layer = 0;
                break;
            }

            Block extra = level.getExtraBlock(blockSide);
            if (extra.isWater()) {
                colliding = extra;
                layer = 1;
                break;
            }

            if (blockSide.getId() == BLUE_ICE) {
                blueIce = blockSide;
            }
        }

        if (colliding != null) {
            if (this.getDamage() == 0) {
                this.liquidCollide(colliding, Block.get(BlockID.OBSIDIAN), layer);
                return;
            }
            if (this.getDamage() < DOWNWARD_BIT) {
                this.liquidCollide(colliding, Block.get(BlockID.COBBLESTONE), layer);
                return;
            }
        }

        if (blueIce != null && down().getId() == SOUL_SOIL) {
            liquidCollide(blueIce, get(BASALT), 0);
        }
    }

    @Override
    protected void flowIntoBlock(Block block, int newFlowDecay) {
        if (block.isWater()) {
            ((BlockLiquid) block).liquidCollide(this, Block.get(BlockID.STONE), 0);
        } else {
            super.flowIntoBlock(block, newFlowDecay);
        }
    }

    @Override
    public void addVelocityToEntity(Entity entity, Vector3 vector) {
        if (!(entity instanceof EntityPrimedTNT)) {
            super.addVelocityToEntity(entity, vector);
        }
    }

    @Override
    public boolean isLava() {
        return true;
    }

    @Override
    public boolean isSameLiquid(Block block) {
        return block.isLava();
    }
}
