package cn.nukkit.scheduler;

import cn.nukkit.block.Block;
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
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

public class BlockUpdateScheduler {
    private final Level level;
    private long lastTick;
    private final Long2ObjectMap<Set<BlockUpdateEntry>> queuedUpdates;

    private Set<BlockUpdateEntry> pendingUpdates;

    public BlockUpdateScheduler(Level level, long currentTick) {
        queuedUpdates = new Long2ObjectOpenHashMap<>(); // Change to ConcurrentHashMap if this needs to be concurrent
        lastTick = currentTick;
        this.level = level;
    }

    public synchronized void tick(long currentTick) {
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
            Set<BlockUpdateEntry> updates = pendingUpdates = queuedUpdates.remove(tick);
            if (updates != null) {
                Iterator<BlockUpdateEntry> updateIterator = updates.iterator();

                while (updateIterator.hasNext()) {
                    BlockUpdateEntry entry = updateIterator.next();

                    Vector3 pos = entry.pos;
                    if (level.isChunkLoaded(Mth.floor(pos.x) >> 4, Mth.floor(pos.z) >> 4)) {
                        Block block = level.getBlock(entry.pos);

                        updateIterator.remove();
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
                }
            }
        } finally {
            pendingUpdates = null;
        }
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
        long time = getMinTime(entry);
        Set<BlockUpdateEntry> updateSet = queuedUpdates.get(time);
        if (updateSet == null) {
            updateSet = new ObjectLinkedOpenHashSet<>();
            queuedUpdates.put(time, updateSet);
        }
        updateSet.add(entry);
    }

    public boolean contains(BlockUpdateEntry entry) {
        for (Long2ObjectMap.Entry<Set<BlockUpdateEntry>> tickUpdateSet : queuedUpdates.long2ObjectEntrySet()) {
            if (tickUpdateSet.getValue().contains(entry)) {
                return true;
            }
        }
        return false;
    }

    public boolean remove(BlockUpdateEntry entry) {
        for (Long2ObjectMap.Entry<Set<BlockUpdateEntry>> tickUpdateSet : queuedUpdates.long2ObjectEntrySet()) {
            if (tickUpdateSet.getValue().remove(entry)) {
                return true;
            }
        }
        return false;
    }

    public boolean remove(Vector3 pos) {
        for (Long2ObjectMap.Entry<Set<BlockUpdateEntry>> tickUpdateSet : queuedUpdates.long2ObjectEntrySet()) {
            if (tickUpdateSet.getValue().remove(pos)) {
                return true;
            }
        }
        return false;
    }

    /**
     * internal.
     */
    public void setLastTick(long currentTick) {
        lastTick = currentTick;
    }
}
