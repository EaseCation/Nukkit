package cn.nukkit.molang.runtime;

import java.util.List;

/**
 * 一个已编译程序所需的路径和查询，绑定数组的索引仅在该计划内有效。
 */
public final class MolangBindingPlan {
    public static final MolangBindingPlan EMPTY = new MolangBindingPlan(List.of(), List.of());

    private final List<MolangPath> paths;
    private final List<MolangQueryRef> queries;

    public MolangBindingPlan(List<MolangPath> paths, List<MolangQueryRef> queries) {
        this.paths = List.copyOf(paths);
        this.queries = List.copyOf(queries);
    }

    public int pathCount() {
        return paths.size();
    }

    public MolangPath path(int index) {
        return paths.get(index);
    }

    public int queryCount() {
        return queries.size();
    }

    public MolangQueryRef query(int index) {
        return queries.get(index);
    }
}
