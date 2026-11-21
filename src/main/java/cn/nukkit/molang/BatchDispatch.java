package cn.nukkit.molang;

import cn.nukkit.molang.compiler.MolangBatchCompiler;
import cn.nukkit.molang.runtime.MolangRuntime;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 为短批次的每个程序生成独立的 JVM 调用点。同一程序序列可在多个实体上共享调度器。
 */
final class BatchDispatch {
    @Nullable
    static Cache newCache(int capacity) {
        return capacity < 2 ? null : new Cache(capacity);
    }

    static final class Cache {
        private static final int MAX_BATCH_SIZE = 32;
        private static final int MAX_ENTRIES = 64;

        private final int maxSize;
        private final int maxEntries;
        private final Map<MolangBatchCompiler.Shape, MolangBatchCompiler.Factory> entries = new LinkedHashMap<>(16, 0.75f, true);

        private Cache(int capacity) {
            maxSize = Math.min(MAX_BATCH_SIZE, capacity);
            maxEntries = Math.min(MAX_ENTRIES, capacity);
        }

        boolean acceptsSize(int size) {
            return size >= 2 && size <= maxSize;
        }

        MolangBatch bind(List<MolangExpression> programs, MolangRuntime runtime, MolangRuntime[] views) {
            MolangBatchCompiler.Bound result = MolangBatchCompiler.bind(programs, runtime, views, this::resolve);
            return new MolangBatch(runtime, views.length, result);
        }

        private synchronized MolangBatchCompiler.Factory resolve(MolangBatchCompiler.Shape shape) {
            MolangBatchCompiler.Factory result = entries.get(shape);
            if (result == null) {
                result = shape.compile();
                if (entries.size() == maxEntries) {
                    entries.remove(entries.keySet().iterator().next());
                }
                entries.put(shape, result);
            }
            return result;
        }
    }

    private BatchDispatch() {
    }
}
