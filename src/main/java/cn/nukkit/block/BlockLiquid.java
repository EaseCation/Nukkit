package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.block.BlockFromToEvent;
import cn.nukkit.event.block.LiquidFlowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.Items;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockFace.Plane;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.Hash;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class BlockLiquid extends BlockTransparent {

    public static final int DOWNWARD_BIT = 0b1000;

    private static final byte CAN_FLOW_DOWN = 1;
    private static final byte CAN_FLOW = 0;
    private static final byte BLOCKED = -1;

    BlockLiquid() {
    }

    @Override
    public boolean canBeFlowedInto() {
        return true;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return null;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        return new Item[0];
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
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
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public double getMaxY() {
        return this.y + 1 - getFluidHeightPercent();
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return this;
    }

    public float getFluidHeightPercent() {
        int d = this.getDamage();
        if (d >= DOWNWARD_BIT) {
            d = 0;
        }

        return (d + 1) / 9f;
    }

    protected int getFlowDecay(Block block) {
        if (!isSameLiquid(block)) {
            return -1;
        }
        return block.getDamage();
    }

    protected int getEffectiveFlowDecay(Block block) {
        if (!isSameLiquid(block)) {
            return -1;
        }
        int decay = block.getDamage();
        if (decay >= DOWNWARD_BIT) {
            decay = 0;
        }
        return decay;
    }

    public Vector3 getFlowVector() {
        Vector3 vector = new Vector3(0, 0, 0);
        int decay = this.getEffectiveFlowDecay(this);
        for (int j = 0; j < 4; ++j) {
            int x = (int) this.x;
            int y = (int) this.y;
            int z = (int) this.z;

            switch (j) {
                case 0:
                    --x;
                    break;
                case 1:
                    x++;
                    break;
                case 2:
                    z--;
                    break;
                default:
                    z++;
                    break;
            }

            Block sideBlock = this.level.getBlock(x, y, z);
            int blockDecay = this.getEffectiveFlowDecay(sideBlock);
            if (blockDecay == -1) {
                Block block = this.level.getExtraBlock(x, y, z);
                blockDecay = this.getEffectiveFlowDecay(block);
            }

            if (blockDecay < 0) {
                if (!isLiquidContainer(sideBlock)) {
                    continue;
                }

                blockDecay = this.getEffectiveFlowDecay(this.level.getBlock(x, y - 1, z));
                if (blockDecay == -1) {
                    blockDecay = this.getEffectiveFlowDecay(this.level.getExtraBlock(x, y - 1, z));
                }

                if (blockDecay >= 0) {
                    int realDecay = blockDecay - (decay - DOWNWARD_BIT);
                    vector.x += (sideBlock.x - this.x) * realDecay;
                    vector.y += (sideBlock.y - this.y) * realDecay;
                    vector.z += (sideBlock.z - this.z) * realDecay;
                }
            } else {
                int realDecay = blockDecay - decay;
                vector.x += (sideBlock.x - this.x) * realDecay;
                vector.y += (sideBlock.y - this.y) * realDecay;
                vector.z += (sideBlock.z - this.z) * realDecay;
            }
        }

        if (this.getDamage() >= DOWNWARD_BIT) {
            if (!this.canFlowInto(this.level.getBlock((int) this.x, (int) this.y, (int) this.z - 1)) && !level.getExtraBlock((int) this.x, (int) this.y, (int) this.z - 1).isLiquidSource() ||
                    !this.canFlowInto(this.level.getBlock((int) this.x, (int) this.y, (int) this.z + 1)) && !level.getExtraBlock((int) this.x, (int) this.y, (int) this.z + 1).isLiquidSource() ||
                    !this.canFlowInto(this.level.getBlock((int) this.x - 1, (int) this.y, (int) this.z)) && !level.getExtraBlock((int) this.x - 1, (int) this.y, (int) this.z).isLiquidSource() ||
                    !this.canFlowInto(this.level.getBlock((int) this.x + 1, (int) this.y, (int) this.z)) && !level.getExtraBlock((int) this.x + 1, (int) this.y, (int) this.z).isLiquidSource() ||
                    !this.canFlowInto(this.level.getBlock((int) this.x, (int) this.y + 1, (int) this.z - 1)) && !level.getExtraBlock((int) this.x, (int) this.y + 1, (int) this.z - 1).isLiquidSource() ||
                    !this.canFlowInto(this.level.getBlock((int) this.x, (int) this.y + 1, (int) this.z + 1)) && !level.getExtraBlock((int) this.x, (int) this.y + 1, (int) this.z + 1).isLiquidSource() ||
                    !this.canFlowInto(this.level.getBlock((int) this.x - 1, (int) this.y + 1, (int) this.z)) && !level.getExtraBlock((int) this.x - 1, (int) this.y + 1, (int) this.z).isLiquidSource() ||
                    !this.canFlowInto(this.level.getBlock((int) this.x + 1, (int) this.y + 1, (int) this.z)) && !level.getExtraBlock((int) this.x + 1, (int) this.y + 1, (int) this.z).isLiquidSource()) {
                vector = vector.normalize().add(0, -6, 0);
            }
        }
        return vector.normalize();
    }

    @Override
    public void addVelocityToEntity(Entity entity, Vector3 vector) {
        if (entity.canBeMovedByCurrents()) {
            Vector3 flow = this.getFlowVector();
            vector.x += flow.x;
            vector.y += flow.y;
            vector.z += flow.z;
        }
    }

    public int getFlowDecayPerBlock() {
        return 1;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            this.checkForHarden();
            this.level.scheduleUpdate(this, this.tickRate());
            return 0;
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            int x = getFloorX();
            int y = getFloorY();
            int z = getFloorZ();

            int decay = this.getFlowDecay(this);
            int multiplier = this.getFlowDecayPerBlock();

            if (decay > 0) {
                int smallestFlowDecay = -100;
                MutableInt adjacentSources = new MutableInt();

                for (BlockFace side : Plane.HORIZONTAL) {
                    Block block = getSide(side);
                    if (!isSameLiquid(block)) {
                        Block block0 = block;
                        block = level.getExtraBlock(block);
                        if (block.isWater() && block0 instanceof BlockStairs stairs && calculateStairsHorizontalOcclusions(stairs).contains(side.getOpposite())) {
                            continue;
                        }
                    }
                    smallestFlowDecay = this.getSmallestFlowDecay(block, smallestFlowDecay, adjacentSources);
                }

                int newDecay = smallestFlowDecay + multiplier;
                if (newDecay >= DOWNWARD_BIT || smallestFlowDecay < 0) {
                    newDecay = -1;
                }

                Block above = this.level.getBlock(x, y + 1, z);
                int topFlowDecay = this.getFlowDecay(above);
                if (topFlowDecay == -1) {
                    topFlowDecay = this.getFlowDecay(this.level.getExtraBlock(above));
                    if (topFlowDecay >= 0 && (above instanceof BlockStairs stairs && !stairs.isUpsideDown() || above instanceof BlockSlab slab && !slab.isTopSlot())) {
                        // fix https://bugs.mojang.com/browse/MCPE-45071
                        topFlowDecay = -1;
                    }
                }
                if (topFlowDecay >= 0) {
                    newDecay = topFlowDecay | DOWNWARD_BIT;
                }

                if (adjacentSources.intValue() >= 2 && this.isWater()) {
                    Block bottomBlock = this.level.getBlock(x, y - 1, z);
                    if (bottomBlock.isSolid()) {
                        newDecay = 0;
                    } else if (bottomBlock.isWaterSource() || this.level.getExtraBlock(bottomBlock).isWaterSource()) {
                        newDecay = 0;
                    }
                }

                if (newDecay != decay) {
                    decay = newDecay;
                    boolean decayed = decay < 0;
                    Block to;
                    if (decayed) {
                        to = Block.get(BlockID.AIR);
                    } else {
                        to = getBlock(decay);
                    }

                    int layer = 0;
                    if (isWater() && (to.isWater() || to.isAir())) {
                        Block block0 = level.getBlock(this);
                        if (!block0.isWater()) {
                            if (block0.canContainFlowingWater()) {
                                layer = 1;
                            } else if (block0.canContainWater() && to.isLiquidSource()) {
                                layer = 1;
                            } else if (!to.isAir()) {
                                to = get(AIR);
                            }
                        }
                    }

                    BlockFromToEvent event = new BlockFromToEvent(this, layer, to);
                    level.getServer().getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        this.level.setBlock(event.getLayer(), this, event.getTo(), true);

                        if (!decayed) {
                            this.level.scheduleUpdate(this, this.tickRate());
                        }
                    }
                }
            }

            if (decay >= 0) {
                Block container = isWaterSource() ? level.getBlock(this) : null;
                BlockStairs stairs = container instanceof BlockStairs ? (BlockStairs) container : null;

                Block bottomBlock = this.level.getBlock(x, y - 1, z);
                if ((stairs == null || stairs.isUpsideDown()) && (!(container instanceof BlockSlab slab) || slab.isTopSlot())) {
                    this.flowIntoBlock(bottomBlock, decay | DOWNWARD_BIT);
                }

                if (decay == 0 || !isLiquidContainer(bottomBlock)) {
                    int adjacentDecay;
                    if (decay >= DOWNWARD_BIT) {
                        adjacentDecay = 1;
                    } else {
                        adjacentDecay = decay + multiplier;
                    }

                    if (adjacentDecay < DOWNWARD_BIT) {
                        Set<BlockFace> occlusions = stairs != null ? calculateStairsHorizontalOcclusions(stairs) : Collections.emptySet();
                        boolean[] flags = this.getOptimalFlowDirections(occlusions);
                        if (flags[0] && !occlusions.contains(BlockFace.WEST)) {
                            this.flowIntoBlock(this.level.getBlock(x - 1, y, z), adjacentDecay);
                        }
                        if (flags[1] && !occlusions.contains(BlockFace.EAST)) {
                            this.flowIntoBlock(this.level.getBlock(x + 1, y, z), adjacentDecay);
                        }
                        if (flags[2] && !occlusions.contains(BlockFace.NORTH)) {
                            this.flowIntoBlock(this.level.getBlock(x, y, z - 1), adjacentDecay);
                        }
                        if (flags[3] && !occlusions.contains(BlockFace.SOUTH)) {
                            this.flowIntoBlock(this.level.getBlock(x, y, z + 1), adjacentDecay);
                        }
                    }
                }
                this.checkForHarden();
            }
        }
        return 0;
    }

    private static Set<BlockFace> calculateStairsHorizontalOcclusions(BlockStairs container) {
        BlockFace facing = container.getBlockFace();
        BlockFace neighborFacing;
        if (!(container.getSide(facing.getOpposite()) instanceof BlockStairs neighbor)
                || container.isUpsideDown() != neighbor.isUpsideDown()
                || facing.getAxis() == (neighborFacing = neighbor.getBlockFace()).getAxis()) {
            return Collections.singleton(facing);
        }
        return EnumSet.of(facing, neighborFacing);
    }

    protected void flowIntoBlock(Block block, int newFlowDecay) {
        Block extra = level.getExtraBlock(block);
        if (this.canFlowInto(block) && !block.isLiquid() && !extra.isLiquid()) {
            LiquidFlowEvent event = new LiquidFlowEvent(block, extra, this, newFlowDecay);
            level.getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                if (!block.isAir()) {
                    this.level.useBreakOn(block, ItemTool.getUniversalTool());
                }

                this.level.setBlock(block, getBlock(newFlowDecay), true, true);

                this.level.scheduleUpdate(block, this.tickRate());
            }
        }
    }

    private int calculateFlowCost(int blockX, int blockY, int blockZ, int accumulatedCost, int maxCost, int originOpposite, int lastOpposite, Long2ByteMap flowCostVisited) {
        int cost = 1000;
        for (int j = 0; j < 4; ++j) {
            if (j == originOpposite || j == lastOpposite) {
                continue;
            }

            int x = blockX;
            int y = blockY;
            int z = blockZ;

            if (j == 0) {
                --x;
            } else if (j == 1) {
                ++x;
            } else if (j == 2) {
                --z;
            } else if (j == 3) {
                ++z;
            }

            long hash = Hash.hashBlockPos(x, y, z);
            byte status = flowCostVisited.get(hash);
            if (status == Byte.MIN_VALUE) {
                Block blockSide = this.level.getBlock(x, y, z);
                if (!this.canFlowInto(blockSide) || this.level.getExtraBlock(x, y, z).isLiquid()) {
                    status = BLOCKED;
                    flowCostVisited.put(hash, status);
                } else if (isLiquidContainer(this.level.getBlock(x, y - 1, z))) {
                    status = CAN_FLOW_DOWN;
                    flowCostVisited.put(hash, status);
                } else {
                    status = CAN_FLOW;
                    flowCostVisited.put(hash, status);
                }
            }

            if (status == BLOCKED) {
                continue;
            } else if (status == CAN_FLOW_DOWN) {
                return accumulatedCost;
            }

            if (accumulatedCost >= maxCost) {
                continue;
            }

            int realCost = this.calculateFlowCost(x, y, z, accumulatedCost + 1, maxCost, originOpposite, j ^ 0x01, flowCostVisited);
            if (realCost < cost) {
                cost = realCost;
            }
        }
        return cost;
    }

    @Override
    public float getHardness() {
        return 100;
    }

    @Override
    public float getResistance() {
        return 500;
    }

    private boolean[] getOptimalFlowDirections(Set<BlockFace> occlusions) {
        int[] flowCost = new int[]{
                1000,
                1000,
                1000,
                1000
        };

        Long2ByteMap flowCostVisited = new Long2ByteOpenHashMap();
        flowCostVisited.defaultReturnValue(Byte.MIN_VALUE);
        int maxCost = 4 / this.getFlowDecayPerBlock();
        for (int j = 0; j < 4; ++j) {
            BlockFace side;
            int x = (int) this.x;
            int y = (int) this.y;
            int z = (int) this.z;
            if (j == 0) {
                side = BlockFace.WEST;
                --x;
            } else if (j == 1) {
                side = BlockFace.EAST;
                ++x;
            } else if (j == 2) {
                side = BlockFace.NORTH;
                --z;
            } else {
                side = BlockFace.SOUTH;
                ++z;
            }

            Block block;
            if (occlusions.contains(side) || !(block = this.level.getBlock(x, y, z)).canContainFlowingWater() || !this.canFlowInto(block) || this.level.getExtraBlock(x, y, z).isLiquid()) {
                flowCostVisited.put(Hash.hashBlockPos(x, y, z), BLOCKED);
            } else if (isLiquidContainer(this.level.getBlock(x, y - 1, z))) {
                flowCostVisited.put(Hash.hashBlockPos(x, y, z), CAN_FLOW_DOWN);
                flowCost[j] = maxCost = 0;
            } else if (maxCost > 0) {
                flowCostVisited.put(Hash.hashBlockPos(x, y, z), CAN_FLOW);
                flowCost[j] = this.calculateFlowCost(x, y, z, 1, maxCost, j ^ 0x01, j ^ 0x01, flowCostVisited);
                maxCost = Math.min(maxCost, flowCost[j]);
            }
        }

        int minCost = Integer.MAX_VALUE;
        for (int i = 0; i < 4; i++) {
            int d = flowCost[i];
            if (d < minCost) {
                minCost = d;
            }
        }

        boolean[] isOptimalFlowDirection = new boolean[4];
        for (int i = 0; i < 4; ++i) {
            isOptimalFlowDirection[i] = (flowCost[i] == minCost);
        }
        return isOptimalFlowDirection;
    }

    private int getSmallestFlowDecay(Block block, int decay, MutableInt adjacentSources) {
        int blockDecay = this.getFlowDecay(block);
        if (blockDecay < 0) {
            return decay;
        }

        if (blockDecay == 0) {
            adjacentSources.increment();
        } else if (blockDecay >= DOWNWARD_BIT) {
            blockDecay = 0;
        }
        return (decay >= 0 && blockDecay >= decay) ? decay : blockDecay;
    }

    protected void checkForHarden() {
    }

    protected void triggerLavaMixEffects(Vector3 pos) {
        Random random = ThreadLocalRandom.current();
        this.getLevel().addLevelEvent(pos.add(0.5, 0.5, 0.5), LevelEventPacket.EVENT_SOUND_FIZZ, (int) ((random.nextFloat() - random.nextFloat()) * 800) + 2600);
    }

    public abstract BlockLiquid getBlock(int meta);

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        entity.resetFallDistance();
    }

    protected boolean liquidCollide(Block cause, Block result, int layer) {
        BlockFromToEvent event = new BlockFromToEvent(this, layer, result);
        this.level.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }
        this.level.setBlock(this, event.getTo(), true, true);
        this.getLevel().addLevelSoundEvent(this.add(0.5, 0.5, 0.5), LevelSoundEventPacket.SOUND_FIZZ);
        return true;
    }

    protected boolean canFlowInto(Block block) {
        return isLiquidContainer(block) && !block.isLiquidSource();
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Items.air();
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
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return false;
    }

    @Override
    public boolean isLiquid() {
        return true;
    }

    @Override
    public boolean isFullLiquid() {
        return isLiquidSource() || isDownwardLiquid();
    }

    @Override
    public boolean isLiquidSource() {
        return getDamage() == 0;
    }

    @Override
    public boolean isDownwardLiquid() {
        return (getDamage() & DOWNWARD_BIT) == DOWNWARD_BIT;
    }

    protected boolean isLiquidContainer(Block block) {
        return block.canBeFlowedInto();
    }
}
