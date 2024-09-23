public class FSM {
    private static class State {
        // Special states
        /**
         * Means that a transition is invalid.
         */
        public static final int i =          -1;

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

        // Exit states
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
    private static final byte[][] TABLE =
    new byte[][] {
        new byte[] {State.EXCLAMATION,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i}, // ASCII !
        new byte[] {i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // "
        new byte[] {i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // #
        new byte[] {i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // $
        new byte[] {i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // %
        new byte[] {i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // &
        new byte[] {i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // '
        new byte[] {State.OPEN_P,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i,i}, // (
        new byte[] {State.CLOSE_P,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // )
        new byte[] {State.MULT,i,i,i,i,i,i,i,i,i,   i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // *
        new byte[] {State.PLUS,i,i,i,i,i,i,i,i,i,   i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,State.DOUBLE_PLUS,i,i,i,i,    i,i,i,i}, // +
        new byte[] {i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // ,
        new byte[] {State.MINUS,i,i,i,i,i,i,i,i,i,  i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.DOUBLE_MINUS,i,i,i,i,i,i,   i,i,i,i}, // -
        new byte[] {i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,State.DECEMAL_POINT,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // .
        new byte[] {i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // /
        new byte[] {State.INT_LITERAL,i,State.FLOAT_LITERAL,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    State.FLOAT_LITERAL,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // 0
        new byte[] {State.INT_LITERAL,i,State.FLOAT_LITERAL,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    State.FLOAT_LITERAL,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // 1
        new byte[] {State.INT_LITERAL,i,State.FLOAT_LITERAL,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    State.FLOAT_LITERAL,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // 2
        new byte[] {State.INT_LITERAL,i,State.FLOAT_LITERAL,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    State.FLOAT_LITERAL,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // 3
        new byte[] {State.INT_LITERAL,i,State.FLOAT_LITERAL,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    State.FLOAT_LITERAL,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // 4
        new byte[] {State.INT_LITERAL,i,State.FLOAT_LITERAL,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    State.FLOAT_LITERAL,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // 5
        new byte[] {State.INT_LITERAL,i,State.FLOAT_LITERAL,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    State.FLOAT_LITERAL,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // 6
        new byte[] {State.INT_LITERAL,i,State.FLOAT_LITERAL,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    State.FLOAT_LITERAL,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // 7
        new byte[] {State.INT_LITERAL,i,State.FLOAT_LITERAL,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    State.FLOAT_LITERAL,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // 8
        new byte[] {State.INT_LITERAL,i,State.FLOAT_LITERAL,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    State.FLOAT_LITERAL,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // 9
        new byte[] {i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // :
        new byte[] {State.SEMICOLON,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // ;
        new byte[] {State.LESS,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // <
        new byte[] {State.EQUAL,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,State.DOUBLE_EQUAL,i,i,i,i,i,i,i,i,    i,i,i,i}, // =
        new byte[] {State.MORE,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // >
        new byte[] {i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // ?
        new byte[] {i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // @
        new byte[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // A
        new byte[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // B
        new byte[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // C
        new byte[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // D
        new byte[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // E
        new byte[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // F
        new byte[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // G
        new byte[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // H
        new byte[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // I
        new byte[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // J
        new byte[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // K
        new byte[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // L
        new byte[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // M
        new byte[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // N
        new byte[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // O
        new byte[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // P
        new byte[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // Q
        new byte[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // R
        new byte[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // S
        new byte[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // T
        new byte[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // U
        new byte[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // V
        new byte[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // W
        new byte[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // X
        new byte[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // Y
        new byte[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // Z
        new byte[] {i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // [
        new byte[] {i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // \
        new byte[] {i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // ]
        new byte[] {i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // ^
        new byte[] {i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // _
        new byte[] {i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // `
        new byte[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // a
        new byte[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // b
        new byte[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // c
        new byte[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // d
        new byte[] {State.E,i,i,i,i,State.ELSE,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // e
        new byte[] {State.F,i,i,i,i,i,State.IF,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // f
        new byte[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // g
        new byte[] {State.IDENTIFIER,i,i,i,i,i,i,i,State.WH,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // h
        new byte[] {State.I,i,i,i,i,i,i,i,i,State.WHI,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // i
        new byte[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // j
        new byte[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // k
        new byte[] {State.IDENTIFIER,i,i,State.EL,i,i,i,i,i,i,    State.WHIL,i,State.FL,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // l
        new byte[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // m
        new byte[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // n
        new byte[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,State.FO,i,State.FLO,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // o
        new byte[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // p
        new byte[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // q
        new byte[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // r
        new byte[] {State.IDENTIFIER,i,i,i,State.ELS,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // s
        new byte[] {State.IDENTIFIER,i,i,i,i,i,i,State.IN,i,i,    i,i,i,i,i,i,State.FLOA,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // t
        new byte[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // u
        new byte[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // v
        new byte[] {State.W,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // w
        new byte[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // x
        new byte[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // y
        new byte[] {State.IDENTIFIER,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,State.IDENTIFIER}, // z
        new byte[] {State.OPEN_B,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // {
        new byte[] {i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // |
        new byte[] {State.CLOSE_B,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // }
        new byte[] {i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i,i,i,i,i,i,i,    i,i,i,i}, // ASCII ~
    };
}
