package cn.nukkit.molang.parser;

import cn.nukkit.molang.MolangVersion;
import cn.nukkit.molang.compiler.MolangCompileException;
import cn.nukkit.molang.lexer.Token;
import cn.nukkit.molang.lexer.TokenType;
import cn.nukkit.molang.parser.ast.Expr;
import cn.nukkit.molang.parser.ast.Stmt;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Parser {
    private final List<Token> tokens;
    private final boolean rightAssociativeTernary;
    private final boolean modernOperatorPrecedence;
    private int pos;

    public Parser(List<Token> tokens) {
        this(tokens, MolangVersion.LATEST);
    }

    public Parser(List<Token> tokens, MolangVersion version) {
        this.tokens = tokens;
        Objects.requireNonNull(version, "version");
        rightAssociativeTernary = version.supports(MolangVersion.CONDITIONAL_OPERATOR_ASSOCIATIVITY);
        modernOperatorPrecedence = version.supports(MolangVersion.COMPARISON_AND_LOGICAL_OPERATOR_PRECEDENCE);
    }

    public Stmt.Program parse() {
        boolean complex = tokens.stream().anyMatch(token -> token.type() == TokenType.SEMICOLON);
        List<Stmt> statements = new ArrayList<>();
        while (!check(TokenType.EOF)) {
            statements.add(parseStatement());
        }
        return new Stmt.Program(List.copyOf(statements), complex || statements.size() != 1);
    }

    private Token peek() {
        return tokens.get(pos);
    }

    private Token advance() {
        return tokens.get(pos++);
    }

    private boolean check(TokenType type) {
        return peek().type() == type;
    }

    private boolean match(TokenType type) {
        if (!check(type)) {
            return false;
        }
        advance();
        return true;
    }

    private Token expect(TokenType type, String message) {
        if (!check(type)) {
            throw error(message);
        }
        return advance();
    }

    private MolangCompileException error(String message) {
        return new MolangCompileException(message + " at position " + peek().position() + ", got " + peek().type());
    }

    private Stmt parseStatement() {
        if (match(TokenType.RETURN)) {
            Expr value = parseExpression();
            endStatement();
            return new Stmt.ReturnStmt(value);
        }
        if (match(TokenType.LOOP)) {
            expect(TokenType.LEFT_PAREN, "Expected '(' after loop");
            Expr count = parseExpression();
            expect(TokenType.COMMA, "Expected ',' in loop");
            Stmt body = parseBody();
            expect(TokenType.RIGHT_PAREN, "Expected ')' after loop");
            endStatement();
            return new Stmt.Loop(count, body);
        }
        if (match(TokenType.FOR_EACH)) {
            expect(TokenType.LEFT_PAREN, "Expected '(' after for_each");
            Expr target = parseExpression();
            if (!(target instanceof Expr.VariableAccess variable) || !isWritable(variable.scope())) {
                throw error("Expected a writable variable in for_each");
            }
            expect(TokenType.COMMA, "Expected ',' after for_each variable");
            Expr array = parseExpression();
            expect(TokenType.COMMA, "Expected ',' after for_each array");
            Stmt body = parseBody();
            expect(TokenType.RIGHT_PAREN, "Expected ')' after for_each");
            endStatement();
            return new Stmt.ForEach(variable, array, body);
        }
        if (match(TokenType.BREAK)) {
            endStatement();
            return new Stmt.Break();
        }
        if (match(TokenType.CONTINUE)) {
            endStatement();
            return new Stmt.Continue();
        }
        if (check(TokenType.LEFT_BRACE)) {
            Stmt.Block block = parseBlock();
            match(TokenType.SEMICOLON);
            return block;
        }
        Expr expression = parseExpression();
        if (!match(TokenType.SEMICOLON) && !check(TokenType.EOF) && !check(TokenType.RIGHT_BRACE)
                && !hasScopeTail(expression)) {
            throw error("Expected ';' between statements");
        }
        return new Stmt.ExpressionStmt(expression);
    }

    private static boolean hasScopeTail(Expr expression) {
        return switch (expression) {
            case Expr.Scope _ -> true;
            case Expr.BinaryConditional conditional -> hasScopeTail(conditional.thenBranch());
            case Expr.Ternary ternary -> hasScopeTail(ternary.elseBranch());
            default -> false;
        };
    }

    private void endStatement() {
        if (!match(TokenType.SEMICOLON) && !check(TokenType.EOF) && !check(TokenType.RIGHT_BRACE)) {
            throw error("Expected ';' between statements");
        }
    }

    private Stmt parseBody() {
        return check(TokenType.LEFT_BRACE) ? parseBlock() : new Stmt.ExpressionStmt(parseExpression());
    }

    private Stmt.Block parseBlock() {
        expect(TokenType.LEFT_BRACE, "Expected '{'");
        List<Stmt> statements = new ArrayList<>();
        while (!check(TokenType.RIGHT_BRACE) && !check(TokenType.EOF)) {
            statements.add(parseStatement());
        }
        expect(TokenType.RIGHT_BRACE, "Expected '}'");
        return new Stmt.Block(List.copyOf(statements));
    }

    private Expr parseExpression() {
        return parseAssignment(true);
    }

    private Expr parseAssignment(boolean coalescing) {
        Expr target = coalescing ? parseNullCoalescing() : parseTernary();
        if (check(TokenType.EQUAL) || check(TokenType.PLUS_EQUAL) || check(TokenType.MINUS_EQUAL)
                || check(TokenType.STAR_EQUAL) || check(TokenType.SLASH_EQUAL)) {
            String operator = advance().value();
            return assign(target, operator, parseAssignment(coalescing));
        }
        return target;
    }

    private static boolean isWritable(String scope) {
        return "variable".equals(scope) || "temp".equals(scope);
    }

    private static boolean isReadOnly(Expr expression) {
        return switch (expression) {
            case Expr.VariableAccess value -> !isWritable(value.scope());
            case Expr.ArrayAccess value -> !isWritable(value.scope());
            case Expr.MemberAccess value -> isReadOnly(value.target());
            case Expr.IndexAccess value -> isReadOnly(value.target());
            case Expr.Group value -> isReadOnly(value.expression());
            default -> true;
        };
    }

    private Expr assign(Expr target, String operator, Expr value) {
        if (target instanceof Expr.Group(Expr expression)) {
            return assign(expression, operator, value);
        }
        if (target instanceof Expr.ContextSwitch(Expr contextTarget, Expr expression)) {
            return new Expr.ContextSwitch(contextTarget, assign(expression, operator, value));
        }
        if (target instanceof Expr.VariableAccess(String scope, String name) && isWritable(scope)) {
            return "=".equals(operator)
                    ? new Expr.VariableAssign(scope, name, value)
                    : new Expr.CompoundAssign(scope, name, operator, value);
        }
        if (target instanceof Expr.ArrayAccess(String scope, String name, Expr index) && isWritable(scope) && "=".equals(operator)) {
            return new Expr.ArrayAssign(scope, name, index, value);
        }
        if (target instanceof Expr.MemberAccess(Expr memberTarget, String name) && !isReadOnly(memberTarget) && "=".equals(operator)) {
            return new Expr.MemberAssign(memberTarget, name, value);
        }
        throw error("Invalid assignment target");
    }

    private Expr parseNullCoalescing() {
        Expr expression = parseTernary();
        while (match(TokenType.QUESTION_QUESTION)) {
            expression = new Expr.NullCoalescing(expression, parseTernary());
        }
        return expression;
    }

    private Expr parseTernary() {
        if (!rightAssociativeTernary) {
            return parseLegacyTernary();
        }
        Expr condition = parseLogical();
        if (!match(TokenType.QUESTION)) {
            return condition;
        }
        Expr thenBranch = parseAssignment(false);
        return match(TokenType.COLON)
                ? new Expr.Ternary(condition, thenBranch, parseAssignment(false))
                : new Expr.BinaryConditional(condition, thenBranch);
    }

    private Expr parseLegacyTernary() {
        Expr expression = parseLogical();
        while (match(TokenType.QUESTION)) {
            Expr thenBranch = parseAssignment(false);
            Expr elseBranch = match(TokenType.COLON) ? parseLegacyBranch() : null;
            expression = elseBranch != null ? new Expr.Ternary(expression, thenBranch, elseBranch)
                    : new Expr.BinaryConditional(expression, thenBranch);
        }
        return expression;
    }

    private Expr parseLegacyBranch() {
        Expr target = parseLogical();
        if (check(TokenType.EQUAL) || check(TokenType.PLUS_EQUAL) || check(TokenType.MINUS_EQUAL)
                || check(TokenType.STAR_EQUAL) || check(TokenType.SLASH_EQUAL)) {
            String operator = advance().value();
            return assign(target, operator, parseLegacyBranch());
        }
        return target;
    }

    private Expr parseLogical() {
        return modernOperatorPrecedence ? parseOr() : parseLegacyAnd();
    }

    private Expr parseLegacyAnd() {
        Expr expression = parseLegacyOr();
        while (match(TokenType.AND_AND)) {
            expression = new Expr.Binary(expression, "&&", parseLegacyOr());
        }
        return expression;
    }

    private Expr parseLegacyOr() {
        Expr expression = parseLegacyComparison();
        while (match(TokenType.OR_OR)) {
            expression = new Expr.Binary(expression, "||", parseLegacyComparison());
        }
        return expression;
    }

    private Expr parseLegacyComparison() {
        return parseLegacyComparison(LEGACY_COMPARISON_ORDER.length - 1);
    }

    private Expr parseLegacyComparison(int priority) {
        if (priority < 0) {
            return parseAddition();
        }
        Expr expression = parseLegacyComparison(priority - 1);
        while (check(LEGACY_COMPARISON_ORDER[priority])) {
            String operator = advance().value();
            expression = new Expr.Binary(expression, operator, parseLegacyComparison(priority - 1));
        }
        return expression;
    }

    private Expr parseOr() {
        Expr expression = parseAnd();
        while (match(TokenType.OR_OR)) {
            expression = new Expr.Binary(expression, "||", parseAnd());
        }
        return expression;
    }

    private Expr parseAnd() {
        Expr expression = parseEquality();
        while (match(TokenType.AND_AND)) {
            expression = new Expr.Binary(expression, "&&", parseEquality());
        }
        return expression;
    }

    private Expr parseEquality() {
        Expr expression = parseComparison();
        while (check(TokenType.EQUAL_EQUAL) || check(TokenType.BANG_EQUAL)) {
            String operator = advance().value();
            expression = new Expr.Binary(expression, operator, parseComparison());
        }
        return expression;
    }

    private Expr parseComparison() {
        Expr expression = parseAddition();
        while (check(TokenType.LESS) || check(TokenType.LESS_EQUAL) || check(TokenType.GREATER) || check(TokenType.GREATER_EQUAL)) {
            String operator = advance().value();
            expression = new Expr.Binary(expression, operator, parseAddition());
        }
        return expression;
    }

    private Expr parseAddition() {
        Expr expression = parseMultiplication();
        while (check(TokenType.PLUS) || check(TokenType.MINUS)) {
            String operator = advance().value();
            expression = new Expr.Binary(expression, operator, parseMultiplication());
        }
        return expression;
    }

    private Expr parseMultiplication() {
        Expr expression = parseUnary();
        while (check(TokenType.STAR) || check(TokenType.SLASH) || check(TokenType.PERCENT)) {
            String operator = advance().value();
            expression = new Expr.Binary(expression, operator, parseUnary());
        }
        return expression;
    }

    private Expr parseUnary() {
        if (check(TokenType.BANG) || check(TokenType.MINUS)) {
            return new Expr.Unary(advance().value(), parseUnary());
        }
        if (match(TokenType.PLUS)) {
            return parseUnary();
        }
        return parsePostfix();
    }

    private Expr parsePostfix() {
        Expr expression = parsePrimary();
        while (true) {
            if (match(TokenType.DOT)) {
                Token member = expect(TokenType.IDENTIFIER, "Expected member name after '.'");
                expression = memberAccess(expression, member.value());
            } else if (match(TokenType.LEFT_BRACKET)) {
                Expr index = parseExpression();
                expect(TokenType.RIGHT_BRACKET, "Expected ']'");
                expression = expression instanceof Expr.VariableAccess(String scope, String name)
                        ? new Expr.ArrayAccess(scope, name, index) : new Expr.IndexAccess(expression, index);
            } else if (match(TokenType.ARROW)) {
                expression = new Expr.ContextSwitch(expression, parsePostfix());
            } else {
                return expression;
            }
        }
    }

    private static Expr memberAccess(Expr expression, String name) {
        return switch (expression) {
            case Expr.Group value -> memberAccess(value.expression(), name);
            case Expr.VariableAccess value -> new Expr.VariableAccess(value.scope(), value.name() + "." + name);
            case Expr.MemberAccess value -> new Expr.MemberAccess(value.target(), value.name() + "." + name);
            default -> new Expr.MemberAccess(expression, name);
        };
    }

    private Expr parsePrimary() {
        Token token = advance();
        return switch (token.type()) {
            case NUMBER -> new Expr.NumberLiteral(Float.parseFloat(token.value()));
            case STRING -> new Expr.StringLiteral(token.value());
            case THIS -> new Expr.This();
            case BREAK -> new Expr.Break();
            case CONTINUE -> new Expr.Continue();
            case LEFT_BRACE -> {
                pos--;
                yield new Expr.Scope(parseBlock());
            }
            case LEFT_PAREN -> {
                Expr expression = parseExpression();
                expect(TokenType.RIGHT_PAREN, "Expected ')'");
                yield new Expr.Group(expression);
            }
            case VARIABLE -> new Expr.VariableAccess("variable", token.value());
            case ARRAY -> new Expr.VariableAccess("array", token.value());
            case TEMP -> new Expr.VariableAccess("temp", token.value());
            case CONTEXT -> new Expr.VariableAccess("context", token.value());
            case QUERY -> new Expr.QueryCall(token.value(), match(TokenType.LEFT_PAREN) ? parseArguments() : List.of());
            case MATH -> {
                if ("pi".equals(token.value()) && !check(TokenType.LEFT_PAREN)) {
                    yield new Expr.MathCall("pi", List.of());
                }
                expect(TokenType.LEFT_PAREN, "Expected '(' after math function");
                yield new Expr.MathCall(token.value(), parseArguments());
            }
            default -> throw new MolangCompileException("Unexpected token " + token.type() + " at position " + token.position());
        };
    }

    private List<Expr> parseArguments() {
        List<Expr> arguments = new ArrayList<>();
        if (!check(TokenType.RIGHT_PAREN)) {
            do {
                arguments.add(parseExpression());
            } while (match(TokenType.COMMA));
        }
        expect(TokenType.RIGHT_PAREN, "Expected ')' after arguments");
        return List.copyOf(arguments);
    }

    // 兼容版本 < 6 逐操作归约的次序，高优先级在前。
    private static final TokenType[] LEGACY_COMPARISON_ORDER = {
            TokenType.LESS,
            TokenType.EQUAL_EQUAL,
            TokenType.GREATER_EQUAL,
            TokenType.GREATER,
            TokenType.LESS_EQUAL,
            TokenType.BANG_EQUAL,
    };
}
