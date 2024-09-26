import java.io.IOException;

public class FSM {
    private static class State {
        // Special states
        /**
         * Means that a transition is invalid.
         */
        public static final int INVALID =         -1;
        public static final int i =               INVALID;

        // Not exit states
        public static final int START =            0;
        public static final int EXCLAMATION =      1;
        public static final int DECEMAL_POINT =    2;
        public static final int E =                3;
        public static final int EL =               4;
        public static final int ELS =              5;
        public static final int I =                6;
        public static final int IN =               7;
        public static final int W =                8;
        public static final int WH =               9;
        public static final int WHI =              10;
        public static final int WHIL =             11;
        public static final int F =                12;
        public static final int FO =               13;
        public static final int FL =               14;
        public static final int FLO =              15;
        public static final int FLOA =             16;

        // Exit states. These are also the codes that tokens hold to denote
        // the token's type.
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

    private static final int i = State.i;

    // [input][current state] -> next state
    // This table contains input arrays for ascii codes 33-126
    //   (ascii printable characters excluding space and DEL)
    private static final int[][] TABLE =
    new int[][] {
        new int[] {State.EXCLAMATION,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i}, // ASCII !
        new int[] {i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // "
        new int[] {i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // #
        new int[] {i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // $
        new int[] {i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // %
        new int[] {i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // &
        new int[] {i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // '
        new int[] {State.OPEN_P,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i}, // (
        new int[] {State.CLOSE_P,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // )
        new int[] {State.MULT,i,i,i,i,i,i,i,i,i,   i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // *
        new int[] {State.PLUS,i,i,i,i,i,i,i,i,i,   i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,State.DOUBLE_PLUS,i,i,i,i,    i,i,i,i}, // +
        new int[] {i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // ,
        new int[] {State.MINUS,i,i,i,i,i,i,i,i,i,  i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.DOUBLE_MINUS,i,i,i,i,i,i,   i,i,i,i}, // -
        new int[] {i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,State.DECEMAL_POINT,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // .
        new int[] {State.DIV,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // /
        new int[] {State.INT_LITERAL,i,State.FLOAT_LITERAL,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,State.INT_LITERAL,    State.FLOAT_LITERAL,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // 0
        new int[] {State.INT_LITERAL,i,State.FLOAT_LITERAL,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,State.INT_LITERAL,    State.FLOAT_LITERAL,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // 1
        new int[] {State.INT_LITERAL,i,State.FLOAT_LITERAL,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,State.INT_LITERAL,    State.FLOAT_LITERAL,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // 2
        new int[] {State.INT_LITERAL,i,State.FLOAT_LITERAL,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,State.INT_LITERAL,    State.FLOAT_LITERAL,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // 3
        new int[] {State.INT_LITERAL,i,State.FLOAT_LITERAL,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,State.INT_LITERAL,    State.FLOAT_LITERAL,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // 4
        new int[] {State.INT_LITERAL,i,State.FLOAT_LITERAL,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,State.INT_LITERAL,    State.FLOAT_LITERAL,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // 5
        new int[] {State.INT_LITERAL,i,State.FLOAT_LITERAL,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,State.INT_LITERAL,    State.FLOAT_LITERAL,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // 6
        new int[] {State.INT_LITERAL,i,State.FLOAT_LITERAL,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,State.INT_LITERAL,    State.FLOAT_LITERAL,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // 7
        new int[] {State.INT_LITERAL,i,State.FLOAT_LITERAL,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,State.INT_LITERAL,    State.FLOAT_LITERAL,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // 8
        new int[] {State.INT_LITERAL,i,State.FLOAT_LITERAL,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,State.INT_LITERAL,    State.FLOAT_LITERAL,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // 9
        new int[] {i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // :
        new int[] {State.SEMICOLON,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // ;
        new int[] {State.LESS,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // <
        new int[] {State.EQUAL,State.NEQ,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,State.LEQ,i,State.GEQ,i,i,i,i,i,i,    i,State.DOUBLE_EQUAL,i,i,i,i,i,i,i,i,    i,i,i,i}, // =
        new int[] {State.MORE,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // >
        new int[] {i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // ?
        new int[] {i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // @
        new int[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // A
        new int[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // B
        new int[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // C
        new int[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // D
        new int[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // E
        new int[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // F
        new int[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // G
        new int[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // H
        new int[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // I
        new int[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // J
        new int[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // K
        new int[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // L
        new int[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // M
        new int[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // N
        new int[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // O
        new int[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // P
        new int[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // Q
        new int[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // R
        new int[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // S
        new int[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // T
        new int[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // U
        new int[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // V
        new int[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // W
        new int[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // X
        new int[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // Y
        new int[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // Z
        new int[] {i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // [
        new int[] {i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // \
        new int[] {i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // ]
        new int[] {i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // ^
        new int[] {i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // _
        new int[] {i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // `
        new int[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,State.FLOA,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // a
        new int[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // b
        new int[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // c
        new int[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // d
        new int[] {State.E,i,i,i,i,State.ELSE,i,i,i,i,    i,State.WHILE,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // e
        new int[] {State.F,i,i,i,i,i,State.IF,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // f
        new int[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // g
        new int[] {State.IDENTIFIER,i,i,i,i,i,i,i,State.WH,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // h
        new int[] {State.I,i,i,i,i,i,i,i,i,State.WHI,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // i
        new int[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // j
        new int[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // k
        new int[] {State.IDENTIFIER,i,i,State.EL,i,i,i,i,i,i,    State.WHIL,i,State.FL,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // l
        new int[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // m
        new int[] {State.IDENTIFIER,i,i,i,i,i,State.IN,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // n
        new int[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,State.FO,i,State.FLO,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // o
        new int[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // p
        new int[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // q
        new int[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,State.FOR,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // r
        new int[] {State.IDENTIFIER,i,i,i,State.ELS,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // s
        new int[] {State.IDENTIFIER,i,i,i,i,i,i,State.INT,i,i,    i,i,i,i,i,i,State.FLOAT,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // t
        new int[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // u
        new int[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // v
        new int[] {State.W,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // w
        new int[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // x
        new int[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // y
        new int[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // z
        new int[] {State.OPEN_B,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // {
        new int[] {i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // |
        new int[] {State.CLOSE_B,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // }
        new int[] {i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // ASCII ~
    };

    // [state] -> true if this state is an exit state
    private static boolean isExit(int state) {
        return state > 16;
    }

    // Reads input until a valid token is built or the end of input is reached.
    // Returns null only when there are no more valid tokens
    //   in the source code stream.
    public static Token tokenize(SourceStream code) throws IOException {
        int currentState = State.START;
        StringBuilder tokenValue = new StringBuilder();

        if (!code.hasNext()) return null; // end of input
        
        while (Character.isWhitespace(code.peek())) code.next();

        if (!code.hasNext()) return null; // end of input

        while (code.hasNext()) {
            char peek = code.peek();
            if (Character.isWhitespace(peek)) {
                code.next();
                if (isExit(currentState))
                    return new Token(tokenValue.toString(), currentState);
                else {
                    System.out.print("There are characters that are not part of a valid token: '" + tokenValue.toString() + "'");
                    System.out.println(" at Column, Row = " + code.getColumn() + ", " + code.getRow());
                    return tokenize(code);
                }
            }
            
            int nextState = TABLE[peek - 33][currentState];
            if (nextState == State.INVALID) {
                if (isExit(currentState))
                    return new Token(tokenValue.toString(), currentState);
                else {
                    System.out.print("There are characters that are not part of a valid token: '" + tokenValue.toString() + "'");
                    System.out.println(" at Column, Row = " + code.getColumn() + ", " + code.getRow());
                    return tokenize(code);
                }
            }
            else {
                tokenValue.append(code.next());
                currentState = nextState;
            }
        }

        if (isExit(currentState))
            return new Token(tokenValue.toString(), currentState);

        System.out.print("There are characters that are not part of a valid token: '" + tokenValue.toString() + "'");
        System.out.println(" at Column, Row = " + code.getColumn() + ", " + code.getRow());
        return null; // end of input, there were characters that were not part of a valid token
    }
}



// public class FSM {

//     private static class State {
//         // Special states
//         public static final int INVALID = -1;

//         // Not exit states
//         public static final int START = 0;
//         public static final int EXCLAMATION = 1;
//         public static final int DECIMAL_POINT = 2; // Corrected name
//         public static final int E = 3;
//         public static final int EL = 4;
//         public static final int ELS = 5;
//         public static final int I = 6;
//         public static final int IN = 7;
//         public static final int W = 8;
//         public static final int WH = 9;
//         public static final int WHI = 10;
//         public static final int WHIL = 11;
//         public static final int F = 12;
//         public static final int FO = 13;
//         public static final int FL = 14;
//         public static final int FLO = 15;
//         public static final int FLOA = 16;

//         // Exit states
//         public static final int OPEN_P = 17;
//         public static final int CLOSE_P = 18;
//         public static final int OPEN_B = 19;
//         public static final int CLOSE_B = 20;
//         public static final int LESS = 21;
//         public static final int LEQ = 22;
//         public static final int MORE = 23;
//         public static final int GEQ = 24;
//         public static final int NEQ = 25;
//         public static final int SEMICOLON = 26;
//         public static final int MULT = 27;
//         public static final int DIV = 28;
//         public static final int INT_LITERAL = 29;
//         public static final int FLOAT_LITERAL = 30;
//         public static final int EQUAL = 31;
//         public static final int DOUBLE_EQUAL = 32;
//         public static final int MINUS = 33;
//         public static final int DOUBLE_MINUS = 34;
//         public static final int PLUS = 35;
//         public static final int DOUBLE_PLUS = 36;
//         public static final int ELSE = 37;
//         public static final int INT = 38;
//         public static final int IF = 39;
//         public static final int WHILE = 40;
//         public static final int FLOAT = 41;
//         public static final int FOR = 42;
//         public static final int IDENTIFIER = 43;
//     }

//     private static final int INVALID = State.INVALID;

//     // Simplify and restructure the input state table
//     private static final int[][] TABLE = new int[94][];
    
//     static {
//         for (int i = 0; i < 94; i++) {
//             TABLE[i] = new int[44];
//             for (int j = 0; j < 44; j++) {
//                 TABLE[i][j] = INVALID;
//             }
//         }
        
//         TABLE['!'][State.START] = State.EXCLAMATION;
//         TABLE['('][State.START] = State.OPEN_P;
//         TABLE[')'][State.START] = State.CLOSE_P;
//         TABLE['*'][State.START] = State.MULT;
//         TABLE['+'][State.START] = State.PLUS;
//         TABLE['-'][State.START] = State.MINUS;
//         TABLE['.'][State.START] = State.DECIMAL_POINT;
//         TABLE['/'][State.START] = State.DIV;
//         TABLE[';'][State.START] = State.SEMICOLON;
//         TABLE['<'][State.START] = State.LESS;
//         TABLE['='][State.START] = State.EQUAL;
//         TABLE['>'][State.START] = State.MORE;
//         TABLE['0'][State.START] = State.INT_LITERAL;
        
//         for (int i = '1'; i <= '9'; i++) {
//             TABLE[i][State.START] = State.INT_LITERAL;
//         }
        
//         for (int i = 'A'; i <= 'Z'; i++) {
//             TABLE[i][State.START] = State.IDENTIFIER;
//         }
        
//         for (int i = 'a'; i <= 'z'; i++) {
//             TABLE[i][State.START] = State.IDENTIFIER;
//         }
//     }

//     public static int getNextState(int currentState, char input) {
//         if (input < 33 || input > 126) {
//             return INVALID;
//         }
//         return TABLE[input - 33][currentState];
//     }

//     public static void main(String[] args) throws IOException {
//         // Example usage of FSM
//         int state = State.START;
//         String input = "(a+b)*c";
        
//         for (char c : input.toCharArray()) {
//             state = getNextState(state, c);
//             if (state == INVALID) {
//                 System.out.println("Invalid transition at: " + c);
//                 return;
//             }
//         }
//         System.out.println("Final state: " + state);
//     }
// }
