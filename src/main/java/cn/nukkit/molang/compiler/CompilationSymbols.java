package cn.nukkit.molang.compiler;

import cn.nukkit.molang.runtime.MolangPath;
import cn.nukkit.molang.runtime.MolangQueryRef;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.util.ArrayList;
import java.util.List;

/**
 * 一个逻辑程序共用的绑定索引，分段编译也保持路径和 query 的身份一致。
 */
final class CompilationSymbols {
    final Object2IntMap<String> pathIndexes = new Object2IntOpenHashMap<>();
    final Object2IntMap<String> queryIndexes = new Object2IntOpenHashMap<>();
    final List<MolangPath> paths = new ArrayList<>();
    final List<MolangQueryRef> queries = new ArrayList<>();
    boolean switchesContext;

    CompilationSymbols() {
        pathIndexes.defaultReturnValue(-1);
        queryIndexes.defaultReturnValue(-1);
    }
}
