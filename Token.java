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
    public final int type;

    private static final String[] typeNames = new String[] {
        null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null,
        "OPEN_P", "CLOSE_P", "OPEN_B", "CLOSE_B", "LESS", "LEQ", "MORE",
        "GEQ", "NEQ", "SEMICOLON", "MULT", "DIV", "INT_LITERAL",
        "FLOAT_LITERAL", "EQUAL", "DOUBLE_EQUAL", "MINUS", "DOUBLE_MINUS",
        "PLUS", "DOUBLE_PLUS", "ELSE", "INT", "IF", "WHILE", "FLOAT", "FOR",
        "IDENTIFIER"
    };

    public Token(String value, int type) {
        this.value = value;
        this.type = type;
    }
    
    @Override
    public String toString() {
        return String.format("%s <%s>", tokenTypeToString(type), value);
    }
    
    public static String tokenTypeToString(int type) {
        return typeNames[type];
    }
    
        public static class Type {
            public static final int OPEN_P =           17;
            public static final int CLOSE_P =          18;
            public static final int OPEN_B =           19;
            public static final int CLOSE_B =          20;
            public static final int LESS =             21;
            public static final int LEQ =              22;
            public static final int MORE =             23;
            public static final int GEQ =              24;
            public static final int NEQ =              25;
            public static final int SEMICOLON =        26;
            public static final int MULT =             27;
            public static final int DIV =              28;
            public static final int INT_LITERAL =      29;
            public static final int FLOAT_LITERAL =    30;
            public static final int EQUAL =            31;
            public static final int DOUBLE_EQUAL =     32;
            public static final int MINUS =            33;
            public static final int DOUBLE_MINUS =     34;
            public static final int PLUS =             35;
            public static final int DOUBLE_PLUS =      36;
            public static final int ELSE =             37;
            public static final int INT =              38;
            public static final int IF =               39;
            public static final int WHILE =            40;
            public static final int FLOAT =            41;
            public static final int FOR =              42;
            public static final int IDENTIFIER =       43;
        }
}
