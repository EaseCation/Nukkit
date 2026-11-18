package cn.nukkit.molang.parser.ast;

import java.util.List;

public sealed interface Expr {
    record NumberLiteral(float value) implements Expr {}

    record StringLiteral(String value) implements Expr {}

    record Binary(Expr left, String operator, Expr right) implements Expr {}

    record Unary(String operator, Expr operand) implements Expr {}

    record Ternary(Expr condition, Expr thenBranch, Expr elseBranch) implements Expr {}

    record BinaryConditional(Expr condition, Expr thenBranch) implements Expr {}

    record NullCoalescing(Expr left, Expr right) implements Expr {}

    record VariableAccess(String scope, String name) implements Expr {}

    record VariableAssign(String scope, String name, Expr value) implements Expr {}

    record CompoundAssign(String scope, String name, String operator, Expr value) implements Expr {}

    record ArrayAccess(String scope, String name, Expr index) implements Expr {}

    record ArrayAssign(String scope, String name, Expr index, Expr value) implements Expr {}

    record MathCall(String function, List<Expr> args) implements Expr {}

    record QueryCall(String name, List<Expr> args) implements Expr {}

    record MemberAccess(Expr target, String name) implements Expr {}

    record MemberAssign(Expr target, String name, Expr value) implements Expr {}

    record ContextSwitch(Expr target, Expr expression) implements Expr {}

    record Scope(Stmt.Block block) implements Expr {}

    record IndexAccess(Expr target, Expr index) implements Expr {}

    record This() implements Expr {}

    record Break() implements Expr {}

    record Continue() implements Expr {}

    record Group(Expr expression) implements Expr {}
}
