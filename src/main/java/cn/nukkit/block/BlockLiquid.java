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
    private static final int FLOW_WEST = 1;
    private static final int FLOW_EAST = 1 << 1;
    private static final int FLOW_NORTH = 1 << 2;
    private static final int FLOW_SOUTH = 1 << 3;

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

    @Override
    public AxisAlignedBB[] getCollisionShape(int flags) {
        if (!ClipFlag.has(flags, ClipFlag.LIQUID)) {
            return null;
        }
        return new AxisAlignedBB[]{getCollisionBoundingBox()};
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

    private int getFlowDecay(int fullId) {
        if (!isSameLiquidFullId(fullId)) {
            return -1;
        }
        return Block.getDamageFromFullId(fullId);
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

    private int getFullBlockFast(int layer, int x, int y, int z) {
        if (!level.getHeightRange().isValidBlockY(y)) {
            return Block.getFullId(BlockID.AIR);
        }
        return level.getBlockFullIdAt(layer, x, y, z);
    }

    private boolean isSameLiquidFullId(int fullId) {
        return isWater() ? isWaterFullId(fullId) : isLavaFullId(fullId);
    }

    private static boolean isAirFullId(int fullId) {
        return Block.getIdFromFullId(fullId) == BlockID.AIR;
    }

    private static boolean isWaterFullId(int fullId) {
        int id = Block.getIdFromFullId(fullId);
        return id == BlockID.FLOWING_WATER || id == BlockID.WATER;
    }

    private static boolean isLavaFullId(int fullId) {
        int id = Block.getIdFromFullId(fullId);
        return id == BlockID.FLOWING_LAVA || id == BlockID.LAVA;
    }

    private static boolean isLiquidFullId(int fullId) {
        return isWaterFullId(fullId) || isLavaFullId(fullId);
    }

    private static boolean isWaterSourceFullId(int fullId) {
        return isWaterFullId(fullId) && Block.getDamageFromFullId(fullId) == 0;
    }

    private static int getSimpleCanFlowInto(int fullId) {
        if (isAirFullId(fullId)) {
            return 1;
        }
        if (isLiquidFullId(fullId)) {
            return Block.getDamageFromFullId(fullId) == 0 ? 0 : 1;
        }
        return -1;
    }

    private static int getSimpleLiquidContainer(int fullId) {
        return isAirFullId(fullId) || isLiquidFullId(fullId) ? 1 : -1;
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
            boolean equivalentOptimization = this.level.getOptimizationSettings().getLiquidFlow().isEquivalentOptimizationEnabled();

            int decay = this.getFlowDecay(this);
            int multiplier = this.getFlowDecayPerBlock();
            Block cachedBottomBlock = null;
            Block cachedWestBlock = null;
            Block cachedEastBlock = null;
            Block cachedNorthBlock = null;
            Block cachedSouthBlock = null;

            if (decay > 0) {
                int smallestFlowDecay = -100;
                int adjacentSources = 0;

                if (equivalentOptimization) {
                    int westFlowDecay = this.getFlowDecay(this.getFullBlockFast(0, x - 1, y, z));
                    if (westFlowDecay >= 0) {
                        adjacentSources += this.getAdjacentSourceCount(westFlowDecay);
                        smallestFlowDecay = this.getSmallestFlowDecay(westFlowDecay, smallestFlowDecay);
                    } else {
                        cachedWestBlock = this.level.getBlock(x - 1, y, z);
                        Block westFlowBlock = this.getHorizontalFlowDecayBlock(cachedWestBlock, BlockFace.WEST);
                        if (westFlowBlock != null) {
                            adjacentSources += this.getAdjacentSourceCount(westFlowBlock);
                            smallestFlowDecay = this.getSmallestFlowDecay(westFlowBlock, smallestFlowDecay);
                        }
                    }

                    int eastFlowDecay = this.getFlowDecay(this.getFullBlockFast(0, x + 1, y, z));
                    if (eastFlowDecay >= 0) {
                        adjacentSources += this.getAdjacentSourceCount(eastFlowDecay);
                        smallestFlowDecay = this.getSmallestFlowDecay(eastFlowDecay, smallestFlowDecay);
                    } else {
                        cachedEastBlock = this.level.getBlock(x + 1, y, z);
                        Block eastFlowBlock = this.getHorizontalFlowDecayBlock(cachedEastBlock, BlockFace.EAST);
                        if (eastFlowBlock != null) {
                            adjacentSources += this.getAdjacentSourceCount(eastFlowBlock);
                            smallestFlowDecay = this.getSmallestFlowDecay(eastFlowBlock, smallestFlowDecay);
                        }
                    }

                    int northFlowDecay = this.getFlowDecay(this.getFullBlockFast(0, x, y, z - 1));
                    if (northFlowDecay >= 0) {
                        adjacentSources += this.getAdjacentSourceCount(northFlowDecay);
                        smallestFlowDecay = this.getSmallestFlowDecay(northFlowDecay, smallestFlowDecay);
                    } else {
                        cachedNorthBlock = this.level.getBlock(x, y, z - 1);
                        Block northFlowBlock = this.getHorizontalFlowDecayBlock(cachedNorthBlock, BlockFace.NORTH);
                        if (northFlowBlock != null) {
                            adjacentSources += this.getAdjacentSourceCount(northFlowBlock);
                            smallestFlowDecay = this.getSmallestFlowDecay(northFlowBlock, smallestFlowDecay);
                        }
                    }

                    int southFlowDecay = this.getFlowDecay(this.getFullBlockFast(0, x, y, z + 1));
                    if (southFlowDecay >= 0) {
                        adjacentSources += this.getAdjacentSourceCount(southFlowDecay);
                        smallestFlowDecay = this.getSmallestFlowDecay(southFlowDecay, smallestFlowDecay);
                    } else {
                        cachedSouthBlock = this.level.getBlock(x, y, z + 1);
                        Block southFlowBlock = this.getHorizontalFlowDecayBlock(cachedSouthBlock, BlockFace.SOUTH);
                        if (southFlowBlock != null) {
                            adjacentSources += this.getAdjacentSourceCount(southFlowBlock);
                            smallestFlowDecay = this.getSmallestFlowDecay(southFlowBlock, smallestFlowDecay);
                        }
                    }
                } else {
                    for (BlockFace side : Plane.HORIZONTAL) {
                        Block block = getSide(side);
                        if (!isSameLiquid(block)) {
                            Block block0 = block;
                            block = level.getExtraBlock(block);
                            if (block.isWater() && block0 instanceof BlockStairs stairs && calculateStairsHorizontalOcclusions(stairs).contains(side.getOpposite())) {
                                continue;
                            }
                        }
                        adjacentSources += this.getAdjacentSourceCount(block);
                        smallestFlowDecay = this.getSmallestFlowDecay(block, smallestFlowDecay);
                    }
                }

                int newDecay = smallestFlowDecay + multiplier;
                if (newDecay >= DOWNWARD_BIT || smallestFlowDecay < 0) {
                    newDecay = -1;
                }

                Block above = null;
                int topFlowDecay;
                if (equivalentOptimization) {
                    topFlowDecay = this.getFlowDecay(this.getFullBlockFast(0, x, y + 1, z));
                    if (topFlowDecay == -1) {
                        topFlowDecay = this.getFlowDecay(this.getFullBlockFast(1, x, y + 1, z));
                        if (topFlowDecay >= 0) {
                            above = this.level.getBlock(x, y + 1, z);
                            if (above instanceof BlockStairs stairs && !stairs.isUpsideDown() || above instanceof BlockSlab slab && !slab.isTopSlot()) {
                                // fix https://bugs.mojang.com/browse/MCPE-45071
                                topFlowDecay = -1;
                            }
                        }
                    }
                } else {
                    above = this.level.getBlock(x, y + 1, z);
                    topFlowDecay = this.getFlowDecay(above);
                    if (topFlowDecay == -1) {
                        topFlowDecay = this.getFlowDecay(this.level.getExtraBlock(above));
                        if (topFlowDecay >= 0 && (above instanceof BlockStairs stairs && !stairs.isUpsideDown() || above instanceof BlockSlab slab && !slab.isTopSlot())) {
                            // fix https://bugs.mojang.com/browse/MCPE-45071
                            topFlowDecay = -1;
                        }
                    }
                }
                if (topFlowDecay >= 0) {
                    newDecay = topFlowDecay | DOWNWARD_BIT;
                }

                if (adjacentSources >= 2 && this.isWater()) {
                    if (equivalentOptimization) {
                        int bottomFullId = this.getFullBlockFast(0, x, y - 1, z);
                        if (isWaterSourceFullId(bottomFullId) || isWaterSourceFullId(this.getFullBlockFast(1, x, y - 1, z))) {
                            newDecay = 0;
                        } else if (!isAirFullId(bottomFullId) && !isLiquidFullId(bottomFullId)) {
                            Block bottomBlock = this.level.getBlock(x, y - 1, z);
                            cachedBottomBlock = bottomBlock;
                            if (bottomBlock.isSolid()) {
                                newDecay = 0;
                            }
                        }
                    } else {
                        Block bottomBlock = this.level.getBlock(x, y - 1, z);
                        if (bottomBlock.isSolid()) {
                            newDecay = 0;
                        } else if (bottomBlock.isWaterSource() || this.level.getExtraBlock(bottomBlock).isWaterSource()) {
                            newDecay = 0;
                        }
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
                    cachedBottomBlock = null;
                    cachedWestBlock = null;
                    cachedEastBlock = null;
                    cachedNorthBlock = null;
                    cachedSouthBlock = null;
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

                Block bottomBlock = cachedBottomBlock != null ? cachedBottomBlock : this.level.getBlock(x, y - 1, z);
                boolean result = true;
                if ((stairs == null || stairs.isUpsideDown()) && (!(container instanceof BlockSlab slab) || slab.isTopSlot())) {
                    result = this.flowIntoBlock(bottomBlock, decay | DOWNWARD_BIT);
                }

                if (decay == 0 || !result || !isLiquidContainer(bottomBlock)) {
                    int adjacentDecay;
                    if (decay >= DOWNWARD_BIT) {
                        adjacentDecay = 1;
                    } else {
                        adjacentDecay = decay + multiplier;
                    }

                    if (adjacentDecay < DOWNWARD_BIT) {
                        Set<BlockFace> occlusions = stairs != null ? calculateStairsHorizontalOcclusions(stairs) : Collections.emptySet();
                        int flowDirectionMask = this.getOptimalFlowDirections(occlusions, equivalentOptimization);
                        if ((flowDirectionMask & FLOW_WEST) != 0 && !occlusions.contains(BlockFace.WEST)) {
                            this.flowIntoBlock(cachedWestBlock != null ? cachedWestBlock : this.level.getBlock(x - 1, y, z), adjacentDecay);
                        }
                        if ((flowDirectionMask & FLOW_EAST) != 0 && !occlusions.contains(BlockFace.EAST)) {
                            this.flowIntoBlock(cachedEastBlock != null ? cachedEastBlock : this.level.getBlock(x + 1, y, z), adjacentDecay);
                        }
                        if ((flowDirectionMask & FLOW_NORTH) != 0 && !occlusions.contains(BlockFace.NORTH)) {
                            this.flowIntoBlock(cachedNorthBlock != null ? cachedNorthBlock : this.level.getBlock(x, y, z - 1), adjacentDecay);
                        }
                        if ((flowDirectionMask & FLOW_SOUTH) != 0 && !occlusions.contains(BlockFace.SOUTH)) {
                            this.flowIntoBlock(cachedSouthBlock != null ? cachedSouthBlock : this.level.getBlock(x, y, z + 1), adjacentDecay);
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

    protected boolean flowIntoBlock(Block block, int newFlowDecay) {
        if (!this.canFlowInto(block) || block.isLiquid()) {
            return true;
        }

        if (isEquivalentLiquidOptimizationEnabled()
                && isLiquidFullId(this.getFullBlockFast(1, block.getFloorX(), block.getFloorY(), block.getFloorZ()))) {
            return true;
        }

        Block extra = level.getExtraBlock(block);
        if (!extra.isLiquid()) {
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
        return true;
    }

    private int calculateFlowCost(int blockX, int blockY, int blockZ, int accumulatedCost, int maxCost, int originOpposite, int lastOpposite, Long2ByteMap flowCostVisited) {
        return calculateFlowCost(blockX, blockY, blockZ, accumulatedCost, maxCost, originOpposite, lastOpposite, flowCostVisited, false);
    }

    private int calculateFlowCost(int blockX, int blockY, int blockZ, int accumulatedCost, int maxCost, int originOpposite, int lastOpposite, Long2ByteMap flowCostVisited, boolean equivalentOptimization) {
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
                if (equivalentOptimization) {
                    status = this.getFlowCostStatus(x, y, z);
                } else {
                    Block blockSide = this.level.getBlock(x, y, z);
                    if (!this.canFlowInto(blockSide) || this.level.getExtraBlock(x, y, z).isLiquid()) {
                        status = BLOCKED;
                    } else if (isLiquidContainer(this.level.getBlock(x, y - 1, z))) {
                        status = CAN_FLOW_DOWN;
                    } else {
                        status = CAN_FLOW;
                    }
                }
                flowCostVisited.put(hash, status);
            }

            if (status == BLOCKED) {
                continue;
            } else if (status == CAN_FLOW_DOWN) {
                return accumulatedCost;
            }

            if (accumulatedCost >= maxCost) {
                continue;
            }

            int realCost = this.calculateFlowCost(x, y, z, accumulatedCost + 1, maxCost, originOpposite, j ^ 0x01, flowCostVisited, equivalentOptimization);
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

    private int getOptimalFlowDirections(Set<BlockFace> occlusions, boolean equivalentOptimization) {
        int westFlowCost = 1000;
        int eastFlowCost = 1000;
        int northFlowCost = 1000;
        int southFlowCost = 1000;

        Long2ByteMap flowCostVisited = new Long2ByteOpenHashMap();
        flowCostVisited.defaultReturnValue(Byte.MIN_VALUE);
        int maxCost = 4 / this.getFlowDecayPerBlock();
        for (int j = 0; j < 4; ++j) {
            BlockFace side;
            int currentFlowCost = 1000;
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

            long hash = Hash.hashBlockPos(x, y, z);
            byte status;
            if (occlusions.contains(side)) {
                status = BLOCKED;
            } else if (equivalentOptimization) {
                status = this.getFlowCostStatus(x, y, z);
            } else {
                Block blockSide = this.level.getBlock(x, y, z);
                if (!this.canFlowInto(blockSide) || this.level.getExtraBlock(x, y, z).isLiquid()) {
                    status = BLOCKED;
                } else if (isLiquidContainer(this.level.getBlock(x, y - 1, z))) {
                    status = CAN_FLOW_DOWN;
                } else {
                    status = CAN_FLOW;
                }
            }

            flowCostVisited.put(hash, status);
            if (status == CAN_FLOW_DOWN) {
                currentFlowCost = maxCost = 0;
            } else if (status == CAN_FLOW && maxCost > 0) {
                currentFlowCost = this.calculateFlowCost(x, y, z, 1, maxCost, j ^ 0x01, j ^ 0x01, flowCostVisited, equivalentOptimization);
                maxCost = Math.min(maxCost, currentFlowCost);
            }

            if (j == 0) {
                westFlowCost = currentFlowCost;
            } else if (j == 1) {
                eastFlowCost = currentFlowCost;
            } else if (j == 2) {
                northFlowCost = currentFlowCost;
            } else {
                southFlowCost = currentFlowCost;
            }
        }

        int minCost = Math.min(Math.min(westFlowCost, eastFlowCost), Math.min(northFlowCost, southFlowCost));

        int optimalFlowDirectionMask = 0;
        if (westFlowCost == minCost) {
            optimalFlowDirectionMask |= FLOW_WEST;
        }
        if (eastFlowCost == minCost) {
            optimalFlowDirectionMask |= FLOW_EAST;
        }
        if (northFlowCost == minCost) {
            optimalFlowDirectionMask |= FLOW_NORTH;
        }
        if (southFlowCost == minCost) {
            optimalFlowDirectionMask |= FLOW_SOUTH;
        }
        return optimalFlowDirectionMask;
    }

    private byte getFlowCostStatus(int x, int y, int z) {
        int fullId = this.getFullBlockFast(0, x, y, z);
        int canFlowInto = getSimpleCanFlowInto(fullId);
        if (canFlowInto == 0) {
            return BLOCKED;
        }

        int extraFullId = this.getFullBlockFast(1, x, y, z);
        if (isLiquidFullId(extraFullId) || !isAirFullId(extraFullId) && this.level.getExtraBlock(x, y, z).isLiquid()) {
            return BLOCKED;
        }

        if (canFlowInto < 0 && !this.canFlowInto(this.level.getBlock(x, y, z))) {
            return BLOCKED;
        }

        int bottomContainer = getSimpleLiquidContainer(this.getFullBlockFast(0, x, y - 1, z));
        if (bottomContainer > 0 || bottomContainer < 0 && isLiquidContainer(this.level.getBlock(x, y - 1, z))) {
            return CAN_FLOW_DOWN;
        }
        return CAN_FLOW;
    }

    private boolean isEquivalentLiquidOptimizationEnabled() {
        return this.level.getOptimizationSettings().getLiquidFlow().isEquivalentOptimizationEnabled();
    }

    private Block getHorizontalFlowDecayBlock(Block block, BlockFace side) {
        if (!isSameLiquid(block)) {
            Block block0 = block;
            block = level.getExtraBlock(block);
            if (block.isWater() && block0 instanceof BlockStairs stairs && calculateStairsHorizontalOcclusions(stairs).contains(side.getOpposite())) {
                return null;
            }
        }
        return block;
    }

    private int getAdjacentSourceCount(Block block) {
        return this.getFlowDecay(block) == 0 ? 1 : 0;
    }

    private int getAdjacentSourceCount(int blockDecay) {
        return blockDecay == 0 ? 1 : 0;
    }

    private int getSmallestFlowDecay(Block block, int decay) {
        int blockDecay = this.getFlowDecay(block);
        return this.getSmallestFlowDecay(blockDecay, decay);
    }

    private int getSmallestFlowDecay(int blockDecay, int decay) {
        if (blockDecay < 0) {
            return decay;
        }

        if (blockDecay >= DOWNWARD_BIT) {
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
