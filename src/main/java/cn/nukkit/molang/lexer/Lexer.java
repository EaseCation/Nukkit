package cn.nukkit.molang.lexer;

import cn.nukkit.molang.compiler.MolangCompileException;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int pos = 0;

    public Lexer(String source) {
        this.source = source;
    }

    public List<Token> tokenize() {
        while (pos < source.length()) {
            char c = source.charAt(pos);
            if (Character.isWhitespace(c)) {
                pos++;
            } else if (c == '/' && pos + 1 < source.length() && source.charAt(pos + 1) == '/') {
                // Line comment
                while (pos < source.length() && source.charAt(pos) != '\n') {
                    pos++;
                }
            } else if (Character.isDigit(c) || (c == '.' && pos + 1 < source.length() && Character.isDigit(source.charAt(pos + 1)))) {
                readNumber();
            } else if (c == '\'' || c == '"') {
                readString();
            } else if (Character.isLetter(c) || c == '_') {
                readIdentifier();
            } else {
                readOperator();
            }
        }
        tokens.add(new Token(TokenType.EOF, "", pos));
        return tokens;
    }

    private void readNumber() {
        int start = pos;
        while (pos < source.length() && Character.isDigit(source.charAt(pos))) {
            pos++;
        }
        if (pos < source.length() && source.charAt(pos) == '.') {
            pos++;
            while (pos < source.length() && Character.isDigit(source.charAt(pos))) {
                pos++;
            }
        }
        tokens.add(new Token(TokenType.NUMBER, source.substring(start, pos), start));
    }

    private void readString() {
        int start = pos;
        char quote = source.charAt(pos++);
        int contentStart = pos;
        while (pos < source.length() && source.charAt(pos) != quote) {
            pos++;
        }
        if (pos == source.length()) {
            throw new MolangCompileException("Unterminated string at position " + start);
        }
        tokens.add(new Token(TokenType.STRING, source.substring(contentStart, pos), start));
        pos++;
    }

    private void readIdentifier() {
        int start = pos;
        StringBuilder sb = new StringBuilder();
        while (pos < source.length() && (Character.isLetterOrDigit(source.charAt(pos)) || source.charAt(pos) == '_')) {
            sb.append(Character.toLowerCase(source.charAt(pos)));
            pos++;
        }
        String word = sb.toString();

        if (!tokens.isEmpty() && tokens.getLast().type() == TokenType.DOT) {
            tokens.add(new Token(TokenType.IDENTIFIER, word, start));
            return;
        }
        int wordEnd = pos;
        skipWhitespace();

        // Check if this is a scoped identifier (e.g., query.xxx, q.xxx, variable.xxx, v.xxx, etc.)
        if (pos < source.length() && source.charAt(pos) == '.') {
            TokenType scopeType = getScopeType(word);
            if (scopeType != null) {
                pos++; // skip the dot
                skipWhitespace();
                StringBuilder nameSb = new StringBuilder();
                while (pos < source.length() && (Character.isLetterOrDigit(source.charAt(pos)) || source.charAt(pos) == '_')) {
                    nameSb.append(Character.toLowerCase(source.charAt(pos)));
                    pos++;
                }
                String name = nameSb.toString();
                if (name.isEmpty()) {
                    throw new MolangCompileException("Expected a name after scope at position " + start);
                }
                tokens.add(new Token(scopeType, name, start));
                return;
            }
        }

        pos = wordEnd;

        // Keywords
        switch (word) {
            case "return" -> tokens.add(new Token(TokenType.RETURN, word, start));
            case "true" -> tokens.add(new Token(TokenType.NUMBER, "1", start));
            case "false" -> tokens.add(new Token(TokenType.NUMBER, "0", start));
            case "for_each" -> tokens.add(new Token(TokenType.FOR_EACH, word, start));
            case "loop" -> tokens.add(new Token(TokenType.LOOP, word, start));
            case "break" -> tokens.add(new Token(TokenType.BREAK, word, start));
            case "continue" -> tokens.add(new Token(TokenType.CONTINUE, word, start));
            case "this" -> tokens.add(new Token(TokenType.THIS, word, start));
            default -> {
                tokens.add(new Token(TokenType.IDENTIFIER, word, start));
            }
        }
    }

    private void skipWhitespace() {
        while (pos < source.length() && Character.isWhitespace(source.charAt(pos))) {
            pos++;
        }
    }

    private TokenType getScopeType(String prefix) {
        return switch (prefix) {
            case "query", "q" -> TokenType.QUERY;
            case "variable", "v" -> TokenType.VARIABLE;
            case "temp", "t" -> TokenType.TEMP;
            case "context", "c" -> TokenType.CONTEXT;
            case "math" -> TokenType.MATH;
            case "array" -> TokenType.ARRAY;
            default -> null;
        };
    }

    private void readOperator() {
        int start = pos;
        char c = source.charAt(pos);
        switch (c) {
            case '+' -> {
                pos++;
                if (pos < source.length() && source.charAt(pos) == '=') {
                    pos++;
                    tokens.add(new Token(TokenType.PLUS_EQUAL, "+=", start));
                } else {
                    tokens.add(new Token(TokenType.PLUS, "+", start));
                }
            }
            case '-' -> {
                pos++;
                if (pos < source.length() && source.charAt(pos) == '>') {
                    pos++;
                    tokens.add(new Token(TokenType.ARROW, "->", start));
                } else if (pos < source.length() && source.charAt(pos) == '=') {
                    pos++;
                    tokens.add(new Token(TokenType.MINUS_EQUAL, "-=", start));
                } else {
                    tokens.add(new Token(TokenType.MINUS, "-", start));
                }
            }
            case '*' -> {
                pos++;
                if (pos < source.length() && source.charAt(pos) == '=') {
                    pos++;
                    tokens.add(new Token(TokenType.STAR_EQUAL, "*=", start));
                } else {
                    tokens.add(new Token(TokenType.STAR, "*", start));
                }
            }
            case '/' -> {
                pos++;
                if (pos < source.length() && source.charAt(pos) == '=') {
                    pos++;
                    tokens.add(new Token(TokenType.SLASH_EQUAL, "/=", start));
                } else {
                    tokens.add(new Token(TokenType.SLASH, "/", start));
                }
            }
            case '%' -> { pos++; tokens.add(new Token(TokenType.PERCENT, "%", start)); }
            case '(' -> { pos++; tokens.add(new Token(TokenType.LEFT_PAREN, "(", start)); }
            case ')' -> { pos++; tokens.add(new Token(TokenType.RIGHT_PAREN, ")", start)); }
            case '{' -> { pos++; tokens.add(new Token(TokenType.LEFT_BRACE, "{", start)); }
            case '}' -> { pos++; tokens.add(new Token(TokenType.RIGHT_BRACE, "}", start)); }
            case '[' -> { pos++; tokens.add(new Token(TokenType.LEFT_BRACKET, "[", start)); }
            case ']' -> { pos++; tokens.add(new Token(TokenType.RIGHT_BRACKET, "]", start)); }
            case ';' -> { pos++; tokens.add(new Token(TokenType.SEMICOLON, ";", start)); }
            case ',' -> { pos++; tokens.add(new Token(TokenType.COMMA, ",", start)); }
            case '.' -> { pos++; tokens.add(new Token(TokenType.DOT, ".", start)); }
            case ':' -> { pos++; tokens.add(new Token(TokenType.COLON, ":", start)); }
            case '?' -> {
                pos++;
                if (pos < source.length() && source.charAt(pos) == '?') {
                    pos++;
                    tokens.add(new Token(TokenType.QUESTION_QUESTION, "??", start));
                } else {
                    tokens.add(new Token(TokenType.QUESTION, "?", start));
                }
            }
            case '!' -> {
                pos++;
                if (pos < source.length() && source.charAt(pos) == '=') {
                    pos++;
                    tokens.add(new Token(TokenType.BANG_EQUAL, "!=", start));
                } else {
                    tokens.add(new Token(TokenType.BANG, "!", start));
                }
            }
            case '=' -> {
                pos++;
                if (pos < source.length() && source.charAt(pos) == '=') {
                    pos++;
                    tokens.add(new Token(TokenType.EQUAL_EQUAL, "==", start));
                } else {
                    tokens.add(new Token(TokenType.EQUAL, "=", start));
                }
            }
            case '<' -> {
                pos++;
                if (pos < source.length() && source.charAt(pos) == '=') {
                    pos++;
                    tokens.add(new Token(TokenType.LESS_EQUAL, "<=", start));
                } else {
                    tokens.add(new Token(TokenType.LESS, "<", start));
                }
            }
            case '>' -> {
                pos++;
                if (pos < source.length() && source.charAt(pos) == '=') {
                    pos++;
                    tokens.add(new Token(TokenType.GREATER_EQUAL, ">=", start));
                } else {
                    tokens.add(new Token(TokenType.GREATER, ">", start));
                }
            }
            case '&' -> {
                pos++;
                if (pos < source.length() && source.charAt(pos) == '&') {
                    pos++;
                    tokens.add(new Token(TokenType.AND_AND, "&&", start));
                } else {
                    throw new MolangCompileException("Unexpected character '&' at position " + start);
                }
            }
            case '|' -> {
                pos++;
                if (pos < source.length() && source.charAt(pos) == '|') {
                    pos++;
                    tokens.add(new Token(TokenType.OR_OR, "||", start));
                } else {
                    throw new MolangCompileException("Unexpected character '|' at position " + start);
                }
            }
            default -> throw new MolangCompileException("Unexpected character '" + c + "' at position " + start);
        }
    }
}
