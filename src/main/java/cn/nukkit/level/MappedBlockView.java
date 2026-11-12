package cn.nukkit.level;

import cn.nukkit.block.Block;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

/**
 * 基于替换表的 {@link BlockView} 默认实现：按方块整体替换，或精确到某个方块状态替换。
 * 构建后不可修改；替换表需要更新时应构建新实例并重新分配给玩家。
 */
public final class MappedBlockView implements BlockView {
    private static final int UNMAPPED = -1;

    private final String name;
    /**
     * 精确到方块状态的替换：完整方块 ID 到目标完整方块 ID
     */
    private final Int2IntMap fullIdRemap;
    /**
     * 不区分状态的替换：方块 ID 到目标完整方块 ID，在精确替换未命中时使用
     */
    private final Int2IntMap blockIdRemap;

    private MappedBlockView(String name, Int2IntMap fullIdRemap, Int2IntMap blockIdRemap) {
        this.name = name;
        this.fullIdRemap = fullIdRemap;
        this.blockIdRemap = blockIdRemap;
    }

    public static Builder builder(String name) {
        return new Builder(name);
    }

    @Override
    public int remap(int fullId) {
        int target = fullIdRemap.get(fullId);
        if (target != UNMAPPED) {
            return target;
        }
        target = blockIdRemap.get(fullId >> Block.BLOCK_META_BITS);
        return target != UNMAPPED ? target : fullId;
    }

    @Override
    public String getName() {
        return name;
    }

    public int size() {
        return fullIdRemap.size() + blockIdRemap.size();
    }

    @Override
    public String toString() {
        return "MappedBlockView{" + name + ", fullIds=" + fullIdRemap.size() + ", blockIds=" + blockIdRemap.size() + "}";
    }

    public static final class Builder {
        private final String name;
        private final Int2IntMap fullIdRemap = new Int2IntOpenHashMap();
        private final Int2IntMap blockIdRemap = new Int2IntOpenHashMap();

        private Builder(String name) {
            this.name = name;
            fullIdRemap.defaultReturnValue(UNMAPPED);
            blockIdRemap.defaultReturnValue(UNMAPPED);
        }

        /**
         * 替换方块的所有状态
         *
         * @throws IllegalArgumentException 目标方块不满足 {@link BlockView#isSupportedTarget(int)}
         */
        public Builder remapBlock(int blockId, int targetFullId) {
            checkTarget(targetFullId);
            blockIdRemap.put(blockId, targetFullId);
            return this;
        }

        /**
         * 只替换方块的某一个状态，优先于 {@link #remapBlock(int, int)}
         *
         * @throws IllegalArgumentException 目标方块不满足 {@link BlockView#isSupportedTarget(int)}
         */
        public Builder remapState(int fullId, int targetFullId) {
            checkTarget(targetFullId);
            fullIdRemap.put(fullId, targetFullId);
            return this;
        }

        public MappedBlockView build() {
            Int2IntMap fullIds = new Int2IntOpenHashMap(fullIdRemap);
            fullIds.defaultReturnValue(UNMAPPED);
            Int2IntMap blockIds = new Int2IntOpenHashMap(blockIdRemap);
            blockIds.defaultReturnValue(UNMAPPED);
            return new MappedBlockView(name, fullIds, blockIds);
        }

        private void checkTarget(int targetFullId) {
            if (!BlockView.isSupportedTarget(targetFullId)) {
                throw new IllegalArgumentException("Unsupported block view target " + (targetFullId >> Block.BLOCK_META_BITS)
                        + ":" + (targetFullId & Block.BLOCK_META_MASK) + " in view " + name);
            }
        }
    }
}
