package cn.nukkit.molang.compiler;

import cn.nukkit.molang.MolangCompileOptions;
import cn.nukkit.molang.parser.ast.Expr;
import cn.nukkit.molang.parser.ast.Stmt;
import cn.nukkit.molang.runtime.MolangQueryDefinition;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 将私有临时标量及不逃逸结构的叶字段放入 JVM 局部槽；观察者和父子覆盖按根回退。
 */
final class NumericLocalPlan {
    private final MolangCompileOptions options;
    private final Set<String> names = new LinkedHashSet<>();
    private final Set<String> compositeRoots = new HashSet<>();
    private final Set<String> nonNumeric = new HashSet<>();
    private final Set<String> numericTemps;
    private final Set<String> objectTemps;
    private final boolean needsRuntimeTemps;
    private boolean externalObserver;
    private boolean hostQuery;

    NumericLocalPlan(Stmt.Program program, MolangCompileOptions options) {
        this.options = options;
        visit(program);
        for (String name : names) {
            int dot = name.indexOf('.');
            while (dot >= 0) {
                if (names.contains(name.substring(0, dot))) {
                    compositeRoots.add(root(name));
                    break;
                }
                dot = name.indexOf('.', dot + 1);
            }
        }
        Set<String> local = new LinkedHashSet<>(names);
        local.removeIf(name -> compositeRoots.contains(root(name)));
        if (externalObserver) {
            local.clear();
        }
        Set<String> numeric = new LinkedHashSet<>(local);
        numeric.removeAll(nonNumeric);
        Set<String> objects = new LinkedHashSet<>(local);
        objects.removeAll(numeric);
        numericTemps = Collections.unmodifiableSet(numeric);
        objectTemps = Collections.unmodifiableSet(objects);
        needsRuntimeTemps = hostQuery || externalObserver || local.size() != names.size();
    }

    Set<String> numericTemps() {
        return numericTemps;
    }

    Set<String> objectTemps() {
        return objectTemps;
    }

    boolean needsRuntimeTemps() {
        return needsRuntimeTemps;
    }

    private static String root(String name) {
        int dot = name.indexOf('.');
        return dot < 0 ? name : name.substring(0, dot);
    }

    private void temp(String scope, String name, boolean scalar) {
        if ("temp".equals(scope)) {
            names.add(name);
            if (!scalar) {
                compositeRoots.add(root(name));
            }
        }
    }

    private void visit(Stmt statement) {
        switch (statement) {
            case Stmt.Program value -> value.statements().forEach(this::visit);
            case Stmt.Block value -> value.statements().forEach(this::visit);
            case Stmt.ExpressionStmt value -> visit(value.expression());
            case Stmt.ReturnStmt value -> visit(value.value());
            case Stmt.Loop value -> { visit(value.count()); visit(value.body()); }
            case Stmt.ForEach value -> {
                temp(value.target().scope(), value.target().name(), false);
                excludeComposite(value.array());
                visit(value.array());
                visit(value.body());
            }
            case Stmt.Break _, Stmt.Continue _ -> { }
        }
    }

    private void visit(Expr expression) {
        switch (expression) {
            case Expr.VariableAccess value -> temp(value.scope(), value.name(), true);
            case Expr.VariableAssign value -> {
                temp(value.scope(), value.name(), true);
                if ("temp".equals(value.scope()) && !hasNumericResult(value.value())) {
                    nonNumeric.add(value.name());
                }
                visit(value.value());
            }
            case Expr.CompoundAssign value -> { temp(value.scope(), value.name(), true); visit(value.value()); }
            case Expr.ArrayAccess value -> { temp(value.scope(), value.name(), false); visit(value.index()); }
            case Expr.ArrayAssign value -> {
                temp(value.scope(), value.name(), false);
                visit(value.index());
                visit(value.value());
            }
            case Expr.IndexAccess value -> { excludeComposite(value.target()); visit(value.target()); visit(value.index()); }
            case Expr.MemberAccess value -> { excludeComposite(value.target()); visit(value.target()); }
            case Expr.MemberAssign value -> { excludeComposite(value.target()); visit(value.target()); visit(value.value()); }
            case Expr.ContextSwitch value -> {
                externalObserver = true;
                excludeComposite(value.target());
                visit(value.target());
                visit(value.expression());
            }
            case Expr.QueryCall value -> {
                if (MolangFunctions.effectfulQuery(value.name())) {
                    hostQuery = true;
                    externalObserver = true;
                }
                if (!MolangFunctions.PURE_QUERIES.contains(value.name())) {
                    hostQuery = true;
                    MolangQueryDefinition definition = options.querySchema().get(value.name());
                    if (definition == null) {
                        externalObserver = true;
                    } else {
                        definition.reads().forEach(this::observe);
                        definition.writes().forEach(this::observe);
                    }
                }
                value.args().forEach(this::visit);
            }
            case Expr.Scope value -> visit(value.block());
            default -> AstNodes.children(expression).forEach(this::visit);
        }
    }

    private void observe(String dependency) {
        String name = dependency.startsWith("t.") ? "temp." + dependency.substring(2) : dependency;
        if (name.equals("*") || name.equals("temp") || name.equals("temp.*")) {
            externalObserver = true;
        } else if (name.startsWith("temp.")) {
            compositeRoots.add(root(name.substring(5)));
        }
    }

    private void excludeComposite(Expr expression) {
        switch (expression) {
            case Expr.VariableAccess value -> temp(value.scope(), value.name(), false);
            case Expr.ArrayAccess value -> temp(value.scope(), value.name(), false);
            case Expr.MemberAccess value -> excludeComposite(value.target());
            case Expr.IndexAccess value -> excludeComposite(value.target());
            case Expr.Group value -> excludeComposite(value.expression());
            case Expr.Ternary value -> { excludeComposite(value.thenBranch()); excludeComposite(value.elseBranch()); }
            case Expr.BinaryConditional value -> excludeComposite(value.thenBranch());
            case Expr.NullCoalescing value -> { excludeComposite(value.left()); excludeComposite(value.right()); }
            default -> { }
        }
    }

    private boolean hasNumericResult(Expr expression) {
        return switch (expression) {
            case Expr.NumberLiteral _, Expr.This _, Expr.Unary _, Expr.Binary _, Expr.MathCall _, Expr.CompoundAssign _ -> true;
            case Expr.QueryCall value -> {
                MolangQueryDefinition definition = options.querySchema().get(value.name());
                yield MolangFunctions.PURE_QUERIES.contains(value.name()) || definition != null && definition.returnType() == MolangQueryDefinition.ValueType.NUMBER && !definition.nullable();
            }
            case Expr.VariableAssign value -> hasNumericResult(value.value());
            case Expr.ArrayAssign value -> hasNumericResult(value.value());
            case Expr.MemberAssign value -> hasNumericResult(value.value());
            case Expr.Group value -> hasNumericResult(value.expression());
            case Expr.Ternary value -> hasNumericResult(value.thenBranch()) && hasNumericResult(value.elseBranch());
            case Expr.BinaryConditional value -> hasNumericResult(value.thenBranch());
            case Expr.NullCoalescing value -> hasNumericResult(value.left()) && hasNumericResult(value.right());
            default -> false;
        };
    }
}
