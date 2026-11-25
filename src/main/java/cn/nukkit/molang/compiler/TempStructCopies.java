package cn.nukkit.molang.compiler;

import cn.nukkit.molang.parser.ast.Expr;
import cn.nukkit.molang.parser.ast.Stmt;

import java.util.*;

/**
 * 将直线代码中的私有临时结构赋值展开为逐字段赋值。字段随后可使用普通 JVM 局部变量；
 * 可观察的宿主和带引用的输入使用原程序，保留复制容器或解析实体引用时的回调顺序。
 */
final class TempStructCopies {
    private static final int MAX_EXPANDED_FIELDS = 64;

    static Set<String> inputs(Stmt.Program program) {
        Set<String> names = new LinkedHashSet<>();
        AstNodes.visit(program, ignored -> {}, expression -> {
            if (expression instanceof Expr.VariableAccess(String scope, String name) && "variable".equals(scope)) {
                names.add(name);
            }
        });
        return names;
    }

    static Stmt.Program expand(Stmt.Program program) {
        Map<String, Set<String>> fields = new HashMap<>();
        List<Stmt> statements = new ArrayList<>(program.statements().size());
        int expanded = 0;
        for (Stmt statement : program.statements()) {
            if (statement instanceof Stmt.ReturnStmt(Expr returned) && fieldExpression(returned)) {
                statements.add(statement);
                continue;
            }
            if (!(statement instanceof Stmt.ExpressionStmt(Expr.VariableAssign assignment))
                    || !"temp".equals(assignment.scope())) {
                return program;
            }

            String target = assignment.name();
            if (directField(target) && fieldExpression(assignment.value())) {
                int dot = target.indexOf('.');
                fields.computeIfAbsent(target.substring(0, dot), ignored -> new LinkedHashSet<>())
                        .add(target.substring(dot + 1));
                statements.add(statement);
                continue;
            }

            if (target.indexOf('.') >= 0 || fields.containsKey(target)
                    || !(assignment.value() instanceof Expr.VariableAccess(String scope, String source))
                    || !"temp".equals(scope)) {
                return program;
            }
            Set<String> members = fields.get(source);
            if (members == null || expanded + members.size() > MAX_EXPANDED_FIELDS) {
                return program;
            }
            // 目标首次出现，源字段集已知；两个根的后续扩展彼此独立。
            fields.put(target, new LinkedHashSet<>(members));
            for (String member : members) {
                Expr read = new Expr.VariableAccess("temp", source + "." + member);
                statements.add(new Stmt.ExpressionStmt(new Expr.VariableAssign("temp", target + "." + member, read)));
            }
            expanded += members.size();
        }
        return expanded == 0 ? program : new Stmt.Program(List.copyOf(statements), program.isComplex());
    }

    private static boolean directField(String name) {
        int dot = name.indexOf('.');
        return dot > 0 && dot == name.lastIndexOf('.');
    }

    private static boolean fieldExpression(Expr expression) {
        return switch (expression) {
            case Expr.NumberLiteral _, Expr.StringLiteral _ -> true;
            case Expr.VariableAccess value -> "variable".equals(value.scope()) || "temp".equals(value.scope()) && directField(value.name());
            case Expr.Group value -> fieldExpression(value.expression());
            case Expr.Unary value -> fieldExpression(value.operand());
            case Expr.Binary value -> fieldExpression(value.left()) && fieldExpression(value.right());
            case Expr.NullCoalescing value -> fieldExpression(value.left()) && fieldExpression(value.right());
            default -> false;
        };
    }

    private TempStructCopies() {
    }
}
