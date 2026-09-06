package cn.nukkit.scheduler;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.level.Level;
import cn.nukkit.level.LevelOptimizationSettings;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockUpdateEntry;
import cn.nukkit.utils.Hash;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class BlockUpdateScheduler {
    private static final int QUEUED_UPDATE_INDEX_EMPTY_KEY_REBUILD_THRESHOLD = 4096;

    private final Level level;
    private long lastTick;
    private final Long2ObjectMap<Set<BlockUpdateEntry>> queuedUpdates;
    private Long2ObjectMap<IntSet> queuedUpdateIndex;
    private boolean queuedUpdateIndexEnabled;
    private int queuedUpdateIndexEmptyKeys;

    private Set<BlockUpdateEntry> pendingUpdates;

    public BlockUpdateScheduler(Level level, long currentTick) {
        queuedUpdates = new Long2ObjectOpenHashMap<>(); // Change to ConcurrentHashMap if this needs to be concurrent
        lastTick = currentTick;
        this.level = level;
    }

    public void tick(long currentTick) {
        // Should only perform once, unless ticks were skipped
        if (currentTick - lastTick < Short.MAX_VALUE) {// Arbitrary
            for (long tick = lastTick + 1; tick <= currentTick; tick++) {
                perform(tick);
            }
        } else {
            LongList times = new LongArrayList(queuedUpdates.keySet());
            Collections.sort(times);
            LongIterator iter = times.iterator();
            while (iter.hasNext()) {
                long tick = iter.nextLong();
                if (tick <= currentTick) {
                    perform(tick);
                } else {
                    break;
                }
            }
        }
        lastTick = currentTick;
    }

    private void perform(long tick) {
        try {
            lastTick = tick;
            boolean useQueuedUpdateIndex = syncQueuedUpdateIndex();
            Set<BlockUpdateEntry> updates = pendingUpdates = queuedUpdates.remove(tick);
            if (updates != null) {
                int maxLiquidScheduledUpdates = getMaxLiquidScheduledUpdatesPerTick();
                int remainingLiquidScheduledUpdates = countLiquidScheduledUpdates(updates, maxLiquidScheduledUpdates);
                int remainingLiquidScheduledSelections = Math.min(maxLiquidScheduledUpdates, remainingLiquidScheduledUpdates);
                Iterator<BlockUpdateEntry> updateIterator = updates.iterator();

                while (updateIterator.hasNext()) {
                    BlockUpdateEntry entry = updateIterator.next();
                    if (useQueuedUpdateIndex) {
                        removeFromQueuedUpdateIndex(entry);
                    }

                    if (shouldDeferLiquidScheduledEntry(entry, maxLiquidScheduledUpdates, remainingLiquidScheduledUpdates, remainingLiquidScheduledSelections)) {
                        remainingLiquidScheduledUpdates--;
                        updateIterator.remove();
                        rescheduleNextTick(entry, tick);
                        continue;
                    }
                    if (isLiquidScheduledEntry(entry) && maxLiquidScheduledUpdates > 0 && remainingLiquidScheduledUpdates > 0) {
                        remainingLiquidScheduledUpdates--;
                        remainingLiquidScheduledSelections--;
                    }

                    Vector3 pos = entry.pos;
                    int x = pos.getFloorX();
                    int y = pos.getFloorY();
                    int z = pos.getFloorZ();
                    if (level.isChunkLoaded(x >> 4, z >> 4)) {
                        Block block = level.getBlock(x, y, z);

                        updateIterator.remove();
                        if (Block.equals(block, entry.block, false)) {
                            processScheduledUpdate(block);
                        } else {
                            block = level.getExtraBlock(x, y, z);
                            if (Block.equals(block, entry.block, false)) {
                                processScheduledUpdate(block);
                            }
                        }
                    } else {
                        level.scheduleUpdate(entry.block, entry.pos, 0);
                    }
                }
            }
            if (useQueuedUpdateIndex) {
                compactQueuedUpdateIndex();
            }
        } finally {
            pendingUpdates = null;
        }
    }

    private int getMaxLiquidScheduledUpdatesPerTick() {
        return level.getOptimizationSettings().getLiquidFlow().getMaxLiquidScheduledUpdatesPerTick();
    }

    private int countLiquidScheduledUpdates(Set<BlockUpdateEntry> updates, int maxLiquidScheduledUpdates) {
        if (maxLiquidScheduledUpdates <= 0) {
            return 0;
        }

        int liquidUpdates = 0;
        for (BlockUpdateEntry entry : updates) {
            if (isLiquidScheduledEntry(entry)) {
                liquidUpdates++;
            }
        }
        return liquidUpdates;
    }

    private boolean shouldDeferLiquidScheduledEntry(BlockUpdateEntry entry, int maxLiquidScheduledUpdates,
                                                   int remainingLiquidScheduledUpdates, int remainingLiquidScheduledSelections) {
        if (maxLiquidScheduledUpdates <= 0 || !isLiquidScheduledEntry(entry)) {
            return false;
        }
        if (remainingLiquidScheduledUpdates <= remainingLiquidScheduledSelections) {
            return false;
        }
        if (remainingLiquidScheduledSelections <= 0) {
            return true;
        }

        // 对同 tick 到期的液体更新做等概率抽样，避免固定坐标先执行导致半边水流停滞。
        return ThreadLocalRandom.current().nextInt(remainingLiquidScheduledUpdates) >= remainingLiquidScheduledSelections;
    }

    private boolean isLiquidScheduledEntry(BlockUpdateEntry entry) {
        return entry.block instanceof BlockLiquid;
    }

    private void processScheduledUpdate(Block block) {
        if (shouldDeferLiquidUpdate(block)) {
            level.scheduleUpdate(block, block, block.tickRate());
            return;
        }

        block.onUpdate(Level.BLOCK_UPDATE_SCHEDULED);
    }

    private void rescheduleNextTick(BlockUpdateEntry entry, long tick) {
        long nextTick = Math.max(tick + 1, level.getCurrentTick() + 1);
        add(new BlockUpdateEntry(entry.pos, entry.block, nextTick, entry.priority));
    }

    private boolean shouldDeferLiquidUpdate(Block block) {
        if (!(block instanceof BlockLiquid)) {
            return false;
        }

        LevelOptimizationSettings.LiquidFlowSettings settings = level.getOptimizationSettings().getLiquidFlow();
        if (!settings.isVisibilitySchedulingEnabled()) {
            return false;
        }

        return !level.hasChunkPlayersNear(
                block.getChunkX(),
                block.getChunkZ(),
                settings.getVisibilityChunkRadius(),
                block.getFloorY(),
                settings.getVisibilitySubChunkRadius()
        );
    }

    public Set<BlockUpdateEntry> getPendingBlockUpdates(AxisAlignedBB boundingBox) {
        Set<BlockUpdateEntry> set = null;

        for (Long2ObjectMap.Entry<Set<BlockUpdateEntry>> tickEntries : this.queuedUpdates.long2ObjectEntrySet()) {
            Set<BlockUpdateEntry> tickSet = tickEntries.getValue();
            for (BlockUpdateEntry update : tickSet) {
                Vector3 pos = update.pos;

                if (pos.getX() >= boundingBox.getMinX() && pos.getX() < boundingBox.getMaxX() && pos.getZ() >= boundingBox.getMinZ() && pos.getZ() < boundingBox.getMaxZ()) {
                    if (set == null) {
                        set = new ObjectLinkedOpenHashSet<>();
                    }

                    set.add(update);
                }
            }
        }

        return set;
    }

    public boolean isBlockTickPending(Vector3 pos, Block block) {
        Set<BlockUpdateEntry> tmpUpdates = pendingUpdates;
        if (tmpUpdates == null || tmpUpdates.isEmpty()) return false;
        return tmpUpdates.contains(new BlockUpdateEntry(pos, block));
    }

    private long getMinTime(BlockUpdateEntry entry) {
        return Math.max(entry.delay, lastTick + 1);
    }

    public void add(BlockUpdateEntry entry) {
        boolean useQueuedUpdateIndex = syncQueuedUpdateIndex();
        long time = getMinTime(entry);
        Set<BlockUpdateEntry> updateSet = queuedUpdates.get(time);
        if (updateSet == null) {
            updateSet = new ObjectLinkedOpenHashSet<>();
            queuedUpdates.put(time, updateSet);
        }
        if (updateSet.add(entry) && useQueuedUpdateIndex) {
            addToQueuedUpdateIndex(entry);
        }
    }

    public boolean contains(BlockUpdateEntry entry) {
        if (syncQueuedUpdateIndex()) {
            return containsQueuedUpdateIndex(entry);
        }

        for (Long2ObjectMap.Entry<Set<BlockUpdateEntry>> tickUpdateSet : queuedUpdates.long2ObjectEntrySet()) {
            if (tickUpdateSet.getValue().contains(entry)) {
                return true;
            }
        }
        return false;
    }

    public boolean remove(BlockUpdateEntry entry) {
        boolean useQueuedUpdateIndex = syncQueuedUpdateIndex();
        for (Long2ObjectMap.Entry<Set<BlockUpdateEntry>> tickUpdateSet : queuedUpdates.long2ObjectEntrySet()) {
            if (tickUpdateSet.getValue().remove(entry)) {
                if (useQueuedUpdateIndex) {
                    removeFromQueuedUpdateIndex(entry);
                }
                return true;
            }
        }
        return false;
    }

    public boolean remove(Vector3 pos) {
        boolean useQueuedUpdateIndex = syncQueuedUpdateIndex();
        for (Long2ObjectMap.Entry<Set<BlockUpdateEntry>> tickUpdateSet : queuedUpdates.long2ObjectEntrySet()) {
            Iterator<BlockUpdateEntry> updateIterator = tickUpdateSet.getValue().iterator();
            while (updateIterator.hasNext()) {
                BlockUpdateEntry update = updateIterator.next();
                if (update.pos.equals(pos)) {
                    updateIterator.remove();
                    if (useQueuedUpdateIndex) {
                        removeFromQueuedUpdateIndex(update);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private boolean syncQueuedUpdateIndex() {
        boolean enabled = level.getOptimizationSettings().getLiquidFlow().isEquivalentOptimizationEnabled();
        if (enabled == queuedUpdateIndexEnabled) {
            return enabled;
        }

        queuedUpdateIndexEnabled = enabled;
        if (!enabled) {
            if (queuedUpdateIndex != null) {
                queuedUpdateIndex.clear();
            }
            queuedUpdateIndexEmptyKeys = 0;
            return false;
        }

        if (queuedUpdateIndex == null) {
            queuedUpdateIndex = new Long2ObjectOpenHashMap<>();
        } else {
            queuedUpdateIndex.clear();
        }
        queuedUpdateIndexEmptyKeys = 0;

        for (Set<BlockUpdateEntry> tickUpdateSet : queuedUpdates.values()) {
            for (BlockUpdateEntry entry : tickUpdateSet) {
                addToQueuedUpdateIndex(entry);
            }
        }
        return true;
    }

    private void addToQueuedUpdateIndex(BlockUpdateEntry entry) {
        long key = getQueuedUpdateIndexKey(entry);
        IntSet blockIds = queuedUpdateIndex.get(key);
        if (blockIds == null) {
            blockIds = new IntOpenHashSet(1);
            queuedUpdateIndex.put(key, blockIds);
        } else if (blockIds.isEmpty()) {
            queuedUpdateIndexEmptyKeys--;
        }
        blockIds.add(entry.block.getId());
    }

    private boolean containsQueuedUpdateIndex(BlockUpdateEntry entry) {
        IntSet blockIds = queuedUpdateIndex.get(getQueuedUpdateIndexKey(entry));
        return blockIds != null && blockIds.contains(entry.block.getId());
    }

    private void removeFromQueuedUpdateIndex(BlockUpdateEntry entry) {
        long key = getQueuedUpdateIndexKey(entry);
        IntSet blockIds = queuedUpdateIndex.get(key);
        if (blockIds == null) {
            return;
        }

        if (blockIds.remove(entry.block.getId()) && blockIds.isEmpty()) {
            queuedUpdateIndexEmptyKeys++;
        }
    }

    private void compactQueuedUpdateIndex() {
        if (queuedUpdateIndex == null || queuedUpdateIndex.isEmpty()) {
            queuedUpdateIndexEmptyKeys = 0;
            return;
        }

        if (queuedUpdates.isEmpty()) {
            queuedUpdateIndex.clear();
            queuedUpdateIndexEmptyKeys = 0;
            return;
        }

        if (queuedUpdateIndexEmptyKeys >= QUEUED_UPDATE_INDEX_EMPTY_KEY_REBUILD_THRESHOLD
                && queuedUpdateIndexEmptyKeys * 2 >= queuedUpdateIndex.size()) {
            queuedUpdateIndex.clear();
            queuedUpdateIndexEmptyKeys = 0;
            for (Set<BlockUpdateEntry> tickUpdateSet : queuedUpdates.values()) {
                for (BlockUpdateEntry entry : tickUpdateSet) {
                    addToQueuedUpdateIndex(entry);
                }
            }
        }
    }

    private long getQueuedUpdateIndexKey(BlockUpdateEntry entry) {
        Vector3 pos = entry.pos;
        return Hash.hashBlockPos(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ());
    }

    public void refreshOptimizationSettings() {
        syncQueuedUpdateIndex();
    }

    /**
     * internal.
     */
    public void setLastTick(long currentTick) {
        lastTick = currentTick;
    }
}
