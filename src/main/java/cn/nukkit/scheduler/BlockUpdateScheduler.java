package cn.nukkit.scheduler;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.Mth;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockUpdateEntry;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

public class BlockUpdateScheduler {
    private final Level level;
    private long lastTick;
    private final Long2ObjectMap<ObjectLinkedOpenHashSet<BlockUpdateEntry>> queuedUpdates;
    private final Object2IntOpenHashMap<BlockUpdateEntry> queuedEntryCounts;
    private final ArrayDeque<BlockUpdateEntry> deferredLiquidUpdates;

    private Set<BlockUpdateEntry> pendingUpdates;

    public BlockUpdateScheduler(Level level, long currentTick) {
        queuedUpdates = new Long2ObjectOpenHashMap<>(); // Change to ConcurrentHashMap if this needs to be concurrent
        queuedEntryCounts = new Object2IntOpenHashMap<>();
        deferredLiquidUpdates = new ArrayDeque<>();
        lastTick = currentTick;
        this.level = level;
    }

    public void tick(long currentTick) {
        int liquidBudget = level.getMaxLiquidScheduledUpdatesPerTick();
        int liquidExecuted = drainDeferredLiquidUpdates(liquidBudget);

        // Should only perform once, unless ticks were skipped
        if (currentTick - lastTick < Short.MAX_VALUE) {// Arbitrary
            for (long tick = lastTick + 1; tick <= currentTick; tick++) {
                liquidExecuted = perform(tick, liquidBudget, liquidExecuted);
            }
        } else {
            LongList times = new LongArrayList(queuedUpdates.keySet());
            Collections.sort(times);
            LongIterator iter = times.iterator();
            while (iter.hasNext()) {
                long tick = iter.nextLong();
                if (tick <= currentTick) {
                    liquidExecuted = perform(tick, liquidBudget, liquidExecuted);
                } else {
                    break;
                }
            }
        }
        lastTick = currentTick;
    }

    private int drainDeferredLiquidUpdates(int liquidBudget) {
        int liquidExecuted = 0;
        while (!deferredLiquidUpdates.isEmpty() && (liquidBudget <= 0 || liquidExecuted < liquidBudget)) {
            BlockUpdateEntry entry = deferredLiquidUpdates.poll();
            decrementQueuedCount(entry);
            if (executeEntry(entry)) {
                liquidExecuted++;
            }
        }
        return liquidExecuted;
    }

    private int perform(long tick, int liquidBudget, int liquidExecuted) {
        Set<BlockUpdateEntry> updates = pendingUpdates = queuedUpdates.remove(tick);
        try {
            lastTick = tick;
            if (updates != null) {
                Iterator<BlockUpdateEntry> updateIterator = updates.iterator();

                while (updateIterator.hasNext()) {
                    BlockUpdateEntry entry = updateIterator.next();
                    updateIterator.remove();

                    if (liquidBudget > 0 && entry.block instanceof BlockLiquid && liquidExecuted >= liquidBudget) {
                        deferredLiquidUpdates.addLast(entry);
                        continue;
                    }

                    decrementQueuedCount(entry);
                    if (executeEntry(entry)) {
                        liquidExecuted++;
                    }
                }
            }
        } finally {
            if (updates != null && !updates.isEmpty()) {
                for (BlockUpdateEntry remaining : updates) {
                    decrementQueuedCount(remaining);
                }
            }
            pendingUpdates = null;
        }
        return liquidExecuted;
    }

    private boolean executeEntry(BlockUpdateEntry entry) {
        boolean isLiquid = entry.block instanceof BlockLiquid;
        Vector3 pos = entry.pos;
        if (level.isChunkLoaded(Mth.floor(pos.x) >> 4, Mth.floor(pos.z) >> 4)) {
            Block block = level.getBlock(entry.pos);

            if (Block.equals(block, entry.block, false)) {
                block.onUpdate(Level.BLOCK_UPDATE_SCHEDULED);
            } else {
                block = level.getExtraBlock(entry.pos);
                if (Block.equals(block, entry.block, false)) {
                    block.onUpdate(Level.BLOCK_UPDATE_SCHEDULED);
                }
            }
        } else {
            level.scheduleUpdate(entry.block, entry.pos, 0);
        }
        return isLiquid;
    }

    public Set<BlockUpdateEntry> getPendingBlockUpdates(AxisAlignedBB boundingBox) {
        Set<BlockUpdateEntry> set = null;

        for (Long2ObjectMap.Entry<ObjectLinkedOpenHashSet<BlockUpdateEntry>> tickEntries : this.queuedUpdates.long2ObjectEntrySet()) {
            Set<BlockUpdateEntry> tickSet = tickEntries.getValue();
            for (BlockUpdateEntry update : tickSet) {
                set = addPendingIfWithin(set, update, boundingBox);
            }
        }
        for (BlockUpdateEntry update : this.deferredLiquidUpdates) {
            set = addPendingIfWithin(set, update, boundingBox);
        }

        return set;
    }

    private static Set<BlockUpdateEntry> addPendingIfWithin(Set<BlockUpdateEntry> set, BlockUpdateEntry update, AxisAlignedBB boundingBox) {
        Vector3 pos = update.pos;
        if (pos.getX() >= boundingBox.getMinX() && pos.getX() < boundingBox.getMaxX() && pos.getZ() >= boundingBox.getMinZ() && pos.getZ() < boundingBox.getMaxZ()) {
            if (set == null) {
                set = new ObjectLinkedOpenHashSet<>();
            }
            set.add(update);
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
        long time = getMinTime(entry);
        ObjectLinkedOpenHashSet<BlockUpdateEntry> updateSet = queuedUpdates.get(time);
        if (updateSet == null) {
            updateSet = new ObjectLinkedOpenHashSet<>();
            queuedUpdates.put(time, updateSet);
        }
        if (updateSet.add(entry)) {
            queuedEntryCounts.addTo(entry, 1);
        }
    }

    public boolean contains(BlockUpdateEntry entry) {
        return queuedEntryCounts.containsKey(entry);
    }

    public boolean remove(BlockUpdateEntry entry) {
        if (!queuedEntryCounts.containsKey(entry)) {
            return false;
        }
        for (Long2ObjectMap.Entry<ObjectLinkedOpenHashSet<BlockUpdateEntry>> tickUpdateSet : queuedUpdates.long2ObjectEntrySet()) {
            if (tickUpdateSet.getValue().remove(entry)) {
                decrementQueuedCount(entry);
                return true;
            }
        }
        if (deferredLiquidUpdates.remove(entry)) {
            decrementQueuedCount(entry);
            return true;
        }
        return false;
    }

    public boolean remove(Vector3 pos) {
        if (pos == null) {
            return false;
        }
        for (Long2ObjectMap.Entry<ObjectLinkedOpenHashSet<BlockUpdateEntry>> tickUpdateSet : queuedUpdates.long2ObjectEntrySet()) {
            ObjectLinkedOpenHashSet<BlockUpdateEntry> updateSet = tickUpdateSet.getValue();
            BlockUpdateEntry matched = updateSet.get(pos);
            if (matched != null && updateSet.remove(pos)) {
                decrementQueuedCount(matched);
                return true;
            }
        }
        Iterator<BlockUpdateEntry> deferredIterator = deferredLiquidUpdates.iterator();
        while (deferredIterator.hasNext()) {
            BlockUpdateEntry entry = deferredIterator.next();
            if (pos.equals(entry)) {
                deferredIterator.remove();
                decrementQueuedCount(entry);
                return true;
            }
        }
        return false;
    }

    private void decrementQueuedCount(BlockUpdateEntry entry) {
        int count = queuedEntryCounts.removeInt(entry);
        if (count > 1) {
            queuedEntryCounts.put(entry, count - 1);
        }
    }

    /**
     * internal.
     */
    public void setLastTick(long currentTick) {
        lastTick = currentTick;
    }
}
