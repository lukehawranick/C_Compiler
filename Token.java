/**
 * @file Token.java
 * @brief
 * This class represents a token that the Scanner has generated from source
 * code. The source code that resulted in this token is stored into the value
 * field so that further information can be extracted from it such as a number's
 * numerical value or an identifier's name.
 * @authors Garrett Williams
 */
public class Token {
    public final String value;
    public final Type type;

    public Token(String value, Type type) {
        this.value = value;
        this.type = type;
    }

    public enum Type {
        FOR,
        WHILE,
        IF,
        ELSE,
        OPEN_PARENTHESIS,
        CLOSE_PARENTHESIS,
        OPEN_CURLY,
        CLOSE_CURLY,
        PLUS,
        MINUS,
        MULTIPLY,
        DIVIDE,
        NOT,
        DECIMAL_POINT,
        ASSIGNMENT,
        EQUALITY,
        SEMICOLON,
        NUMBER
    }
}
