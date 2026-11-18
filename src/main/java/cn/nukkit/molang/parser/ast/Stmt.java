package cn.nukkit.molang.parser.ast;

import java.util.List;

public sealed interface Stmt {
    record ExpressionStmt(Expr expression) implements Stmt {}

    record ReturnStmt(Expr value) implements Stmt {}

    record Block(List<Stmt> statements) implements Stmt {}

    record Loop(Expr count, Stmt body) implements Stmt {}

    record ForEach(Expr.VariableAccess target, Expr array, Stmt body) implements Stmt {}

    record Break() implements Stmt {}

    record Continue() implements Stmt {}

    record Program(List<Stmt> statements, boolean isComplex) implements Stmt {}
}
