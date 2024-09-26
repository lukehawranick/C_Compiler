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
            public static final int OPEN_P =           FSM.State.OPEN_P;
            public static final int CLOSE_P =          FSM.State.CLOSE_P;
            public static final int OPEN_B =           FSM.State.OPEN_B;
            public static final int CLOSE_B =          FSM.State.CLOSE_B;
            public static final int LESS =             FSM.State.LESS;
            public static final int LEQ =              FSM.State.LEQ;
            public static final int MORE =             FSM.State.MORE;
            public static final int GEQ =              FSM.State.GEQ;
            public static final int NEQ =              FSM.State.NEQ;
            public static final int SEMICOLON =        FSM.State.SEMICOLON;
            public static final int MULT =             FSM.State.MULT;
            public static final int DIV =              FSM.State.DIV;
            public static final int INT_LITERAL =      FSM.State.INT_LITERAL;
            public static final int FLOAT_LITERAL =    FSM.State.FLOAT_LITERAL;
            public static final int EQUAL =            FSM.State.EQUAL;
            public static final int DOUBLE_EQUAL =     FSM.State.DOUBLE_EQUAL;
            public static final int MINUS =            FSM.State.MINUS;
            public static final int DOUBLE_MINUS =     FSM.State.DOUBLE_MINUS;
            public static final int PLUS =             FSM.State.PLUS;
            public static final int DOUBLE_PLUS =      FSM.State.DOUBLE_PLUS;
            public static final int ELSE =             FSM.State.ELSE;
            public static final int INT =              FSM.State.INT;
            public static final int IF =               FSM.State.IF;
            public static final int WHILE =            FSM.State.WHILE;
            public static final int FLOAT =            FSM.State.FLOAT;
            public static final int FOR =              FSM.State.FOR;
            public static final int IDENTIFIER =       FSM.State.IDENTIFIER;
        }
}
