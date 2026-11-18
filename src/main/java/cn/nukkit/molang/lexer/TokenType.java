package cn.nukkit.molang.lexer;

public enum TokenType {
    // Literals
    NUMBER,
    STRING,
    IDENTIFIER,

    // Identifiers with scope prefix
    QUERY,      // query.xxx or q.xxx
    VARIABLE,   // variable.xxx or v.xxx
    TEMP,       // temp.xxx or t.xxx
    CONTEXT,    // context.xxx or c.xxx
    ARRAY,
    MATH,       // math.xxx

    // Keywords
    RETURN,
    LOOP,
    FOR_EACH,
    BREAK,
    CONTINUE,
    THIS,

    // Operators
    PLUS,           // +
    MINUS,          // -
    STAR,           // *
    SLASH,          // /
    PERCENT,        // %
    BANG,           // !
    AND_AND,        // &&
    OR_OR,          // ||
    LESS,           // <
    LESS_EQUAL,     // <=
    GREATER,        // >
    GREATER_EQUAL,  // >=
    EQUAL_EQUAL,    // ==
    BANG_EQUAL,     // !=
    EQUAL,          // =
    PLUS_EQUAL,     // +=
    MINUS_EQUAL,    // -=
    STAR_EQUAL,     // *=
    SLASH_EQUAL,    // /=
    QUESTION,       // ?
    COLON,          // :
    QUESTION_QUESTION, // ??
    ARROW,          // ->

    // Delimiters
    LEFT_PAREN,     // (
    RIGHT_PAREN,    // )
    LEFT_BRACE,     // {
    RIGHT_BRACE,    // }
    LEFT_BRACKET,   // [
    RIGHT_BRACKET,  // ]
    SEMICOLON,      // ;
    COMMA,          // ,
    DOT,            // .

    EOF,
}
