package cn.nukkit.molang.compiler;

import cn.nukkit.molang.MolangVersion;
import cn.nukkit.molang.parser.ast.Expr;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * 只在生成字节码时记录相邻采样属性程序之间可复用的 JVM 局部槽。
 */
final class BatchSubexpressions {
    private static final int FIRST_SHARED_LOCAL = 4;

    private final MolangBatchCompiler.Plan plan;
    private final Map<Key, Integer> computed = new HashMap<>();
    private Map<Key, Integer> previous = Map.of();
    @Nullable
    private BatchNumberPlan current;
    private int position;

    BatchSubexpressions(MolangBatchCompiler.Plan plan) {
        this.plan = plan;
    }

    void begin(int index, @Nullable BatchNumberPlan body) {
        position = index;
        current = !plan.scopes(index) ? body : null;
        if (current == null) {
            computed.clear();
        }
        previous = Map.copyOf(computed);
    }

    int previousLocal(Expr expression) {
        return current != null && current.contains(expression) ? previous.getOrDefault(new Key(current.version(), expression), -1) : -1;
    }

    int captureLocal(Expr expression) {
        if (current == null || !current.straightLine() || !current.contains(expression) || !usedLater(expression)) {
            return -1;
        }
        Key key = new Key(current.version(), expression);
        Integer existing = computed.get(key);
        if (existing != null) {
            return existing;
        }
        int next = FIRST_SHARED_LOCAL + computed.size();
        if (next >= BatchNumberPlan.FIRST_EXPRESSION_LOCAL) {
            return -1;
        }
        computed.put(key, next);
        return next;
    }

    private boolean usedLater(Expr expression) {
        for (int i = position + 1; i < plan.programs().size(); i++) {
            BatchNumberPlan next = plan.number(i);
            if (next == null || plan.scopes(i)) {
                break;
            }
            if (next.version().equals(current.version()) && next.contains(expression)) {
                return true;
            }
        }
        return false;
    }

    private record Key(MolangVersion version, Expr expression) {
    }
}
