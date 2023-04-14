package cn.nukkit.block;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.block.BlockFromToEvent;
import cn.nukkit.event.block.LiquidFlowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.Items;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class BlockLiquid extends BlockTransparentMeta {

    public static final int DOWNWARD_BIT = 0b1000;

    private final byte CAN_FLOW_DOWN = 1;
    private final byte CAN_FLOW = 0;
    private final byte BLOCKED = -1;
    public int adjacentSources = 0;
    protected Vector3 flowVector = null;
    private final Long2ByteMap flowCostVisited = new Long2ByteOpenHashMap() {
        {
            defaultReturnValue(Byte.MIN_VALUE);
        }
    };

    protected BlockLiquid(int meta) {
        super(meta);
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
    public Item[] getDrops(Item item) {
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
    public AxisAlignedBB getBoundingBox() {
        return null;
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
        float d = (float) this.getDamage();
        if (d >= 8) {
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
        if (decay >= 8) {
            decay = 0;
        }
        return decay;
    }

    public void clearCaches() {
        this.flowVector = null;
        this.flowCostVisited.clear();
    }

    public Vector3 getFlowVector() {
        if (this.flowVector != null) {
            return this.flowVector;
        }

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
                    int realDecay = blockDecay - (decay - 8);
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

        if (this.getDamage() >= 0x8) {
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
        return this.flowVector = vector.normalize();
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
            int decay = this.getFlowDecay(this);
            int multiplier = this.getFlowDecayPerBlock();

            if (decay > 0) {
                int smallestFlowDecay = -100;
                this.adjacentSources = 0;

                Block block = this.level.getBlock((int) this.x, (int) this.y, (int) this.z - 1);
                if (!isSameLiquid(block)) {
                    block = this.level.getExtraBlock((int) this.x, (int) this.y, (int) this.z - 1);
                }
                smallestFlowDecay = this.getSmallestFlowDecay(block, smallestFlowDecay);
                block = this.level.getBlock((int) this.x, (int) this.y, (int) this.z + 1);
                if (!isSameLiquid(block)) {
                    block = this.level.getExtraBlock((int) this.x, (int) this.y, (int) this.z + 1);
                }
                smallestFlowDecay = this.getSmallestFlowDecay(block, smallestFlowDecay);
                block = this.level.getBlock((int) this.x - 1, (int) this.y, (int) this.z);
                if (!isSameLiquid(block)) {
                    block = this.level.getExtraBlock((int) this.x - 1, (int) this.y, (int) this.z);
                }
                smallestFlowDecay = this.getSmallestFlowDecay(block, smallestFlowDecay);
                block = this.level.getBlock((int) this.x + 1, (int) this.y, (int) this.z);
                if (!isSameLiquid(block)) {
                    block = this.level.getExtraBlock((int) this.x + 1, (int) this.y, (int) this.z);
                }
                smallestFlowDecay = this.getSmallestFlowDecay(block, smallestFlowDecay);

                int newDecay = smallestFlowDecay + multiplier;
                if (newDecay >= 8 || smallestFlowDecay < 0) {
                    newDecay = -1;
                }

                int topFlowDecay = this.getFlowDecay(this.level.getBlock((int) this.x, (int) this.y + 1, (int) this.z));
                if (topFlowDecay == -1) {
                    topFlowDecay = this.getFlowDecay(this.level.getExtraBlock((int) this.x, (int) this.y + 1, (int) this.z));
                }
                if (topFlowDecay >= 0) {
                    newDecay = topFlowDecay | 0x08;
                }

                if (this.adjacentSources >= 2 && this.isWater()) {
                    Block bottomBlock = this.level.getBlock((int) this.x, (int) this.y - 1, (int) this.z);
                    if (bottomBlock.isSolid()) {
                        newDecay = 0;
                    } else if (bottomBlock.isWaterSource() || this.level.getExtraBlock((int) this.x, (int) this.y - 1, (int) this.z).isWaterSource()) {
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
                Block bottomBlock = this.level.getBlock((int) this.x, (int) this.y - 1, (int) this.z);
                this.flowIntoBlock(bottomBlock, decay | 0x08);

                if (decay == 0 || !isLiquidContainer(bottomBlock)) {
                    int adjacentDecay;
                    if (decay >= 8) {
                        adjacentDecay = 1;
                    } else {
                        adjacentDecay = decay + multiplier;
                    }

                    if (adjacentDecay < 8) {
                        boolean[] flags = this.getOptimalFlowDirections();
                        if (flags[0]) {
                            this.flowIntoBlock(this.level.getBlock((int) this.x - 1, (int) this.y, (int) this.z), adjacentDecay);
                        }
                        if (flags[1]) {
                            this.flowIntoBlock(this.level.getBlock((int) this.x + 1, (int) this.y, (int) this.z), adjacentDecay);
                        }
                        if (flags[2]) {
                            this.flowIntoBlock(this.level.getBlock((int) this.x, (int) this.y, (int) this.z - 1), adjacentDecay);
                        }
                        if (flags[3]) {
                            this.flowIntoBlock(this.level.getBlock((int) this.x, (int) this.y, (int) this.z + 1), adjacentDecay);
                        }
                    }
                }
                this.checkForHarden();
            }
        }
        return 0;
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

    private int calculateFlowCost(int blockX, int blockY, int blockZ, int accumulatedCost, int maxCost, int originOpposite, int lastOpposite) {
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

            long hash = Level.blockHash(x, y, z);
            byte status = this.flowCostVisited.get(hash);
            if (status == Byte.MIN_VALUE) {
                Block blockSide = this.level.getBlock(x, y, z);
                if (!this.canFlowInto(blockSide) || this.level.getExtraBlock(x, y, z).isLiquid()) {
                    status = BLOCKED;
                    this.flowCostVisited.put(hash, status);
                } else if (isLiquidContainer(this.level.getBlock(x, y - 1, z))) {
                    status = CAN_FLOW_DOWN;
                    this.flowCostVisited.put(hash, status);
                } else {
                    status = CAN_FLOW;
                    this.flowCostVisited.put(hash, status);
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

            int realCost = this.calculateFlowCost(x, y, z, accumulatedCost + 1, maxCost, originOpposite, j ^ 0x01);
            if (realCost < cost) {
                cost = realCost;
            }
        }
        return cost;
    }

    @Override
    public double getHardness() {
        return 100d;
    }

    @Override
    public double getResistance() {
        return 500;
    }

    private boolean[] getOptimalFlowDirections() {
        int[] flowCost = new int[]{
                1000,
                1000,
                1000,
                1000
        };

        int maxCost = 4 / this.getFlowDecayPerBlock();
        for (int j = 0; j < 4; ++j) {
            int x = (int) this.x;
            int y = (int) this.y;
            int z = (int) this.z;
            if (j == 0) {
                --x;
            } else if (j == 1) {
                ++x;
            } else if (j == 2) {
                --z;
            } else {
                ++z;
            }

            Block block = this.level.getBlock(x, y, z);
            if (!this.canFlowInto(block) || this.level.getExtraBlock(x, y, z).isLiquid()) {
                this.flowCostVisited.put(Level.blockHash(x, y, z), BLOCKED);
            } else if (isLiquidContainer(this.level.getBlock(x, y - 1, z))) {
                this.flowCostVisited.put(Level.blockHash(x, y, z), CAN_FLOW_DOWN);
                flowCost[j] = maxCost = 0;
            } else if (maxCost > 0) {
                this.flowCostVisited.put(Level.blockHash(x, y, z), CAN_FLOW);
                flowCost[j] = this.calculateFlowCost(x, y, z, 1, maxCost, j ^ 0x01, j ^ 0x01);
                maxCost = Math.min(maxCost, flowCost[j]);
            }
        }
        this.flowCostVisited.clear();

        double minCost = Double.MAX_VALUE;
        for (int i = 0; i < 4; i++) {
            double d = flowCost[i];
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

    private int getSmallestFlowDecay(Block block, int decay) {
        int blockDecay = this.getFlowDecay(block);
        if (blockDecay < 0) {
            return decay;
        }

        if (blockDecay == 0) {
            ++this.adjacentSources;
        } else if (blockDecay >= 8) {
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
