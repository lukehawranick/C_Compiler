import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

public class FSM {
    public static class State {
        // The number of states, excluding the special state INVALID.
        public static final int STATE_COUNT = 44;

        // Special states
        /**
         * Means that a transition is invalid.
         */
        public static final int INVALID =         -1;

        // These are not final states
        public static final int START =            0;
        public static final int EXCLAMATION =      1;
        public static final int DECIMAL_POINT =    2;

        // These all result in IDENTIFIER token types
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

        // These directly correlate to token types
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

    // [current state][input] -> next state
    private static final int[][] TRANSITIONS = new int[State.STATE_COUNT][];

    // [state] -> Token type or -1 if not an exit state
    private static final int[] FINAL_STATES = new int[State.STATE_COUNT];
    
    static {
        // Setup final states
        for (int i = 0; i < 3; i++)
            FINAL_STATES[i] = -1;
        for (int i = 3; i < 17; i++)
            FINAL_STATES[i] = State.IDENTIFIER;
        for (int i = 17; i < 44; i++)
            FINAL_STATES[i] = i;

        // Init all transitions to INVALID
        for (int i = 0; i < State.STATE_COUNT; i++) {
            TRANSITIONS[i] = new int[127];
            for (int j = 0; j < 127; j++)
                TRANSITIONS[i][j] = State.INVALID;
        }
        
        // From START state
        TRANSITIONS[State.START]['!'] = State.EXCLAMATION;
        TRANSITIONS[State.START]['('] = State.OPEN_P;
        TRANSITIONS[State.START][')'] = State.CLOSE_P;
        TRANSITIONS[State.START]['{'] = State.OPEN_B;
        TRANSITIONS[State.START]['}'] = State.CLOSE_B;
        TRANSITIONS[State.START]['*'] = State.MULT;
        TRANSITIONS[State.START]['+'] = State.PLUS;
        TRANSITIONS[State.START]['-'] = State.MINUS;
        TRANSITIONS[State.START]['/'] = State.DIV;
        TRANSITIONS[State.START][';'] = State.SEMICOLON;
        TRANSITIONS[State.START]['<'] = State.LESS;
        TRANSITIONS[State.START]['='] = State.EQUAL;
        TRANSITIONS[State.START]['>'] = State.MORE;
        for (int i = '0'; i <= '9'; i++)
            TRANSITIONS[State.START][i] = State.INT_LITERAL;
        for (int i = 'A'; i <= 'Z'; i++)
            TRANSITIONS[State.START][i] = State.IDENTIFIER;
        for (int i = 'a'; i <= 'z'; i++)
            TRANSITIONS[State.START][i] = State.IDENTIFIER;
        TRANSITIONS[State.START]['e'] = State.E;
        TRANSITIONS[State.START]['i'] = State.I;
        TRANSITIONS[State.START]['f'] = State.F;
        TRANSITIONS[State.START]['w'] = State.W;

        // From INT_LITERAL to INT_LITERAL
        for (int i = '0'; i <= '9'; i++)
            TRANSITIONS[State.INT_LITERAL][i] = State.INT_LITERAL;
        
        // From INT_LITERAL to DECIMAL_POINT
        TRANSITIONS[State.INT_LITERAL]['.'] = State.DECIMAL_POINT;

        // From DECIMAL_POINT to FLOAT_LITERAL
        for (int i = '0'; i <= '9'; i++)
            TRANSITIONS[State.DECIMAL_POINT][i] = State.FLOAT_LITERAL;

        // From operator to operator
        TRANSITIONS[State.PLUS]['+'] = State.DOUBLE_PLUS;
        TRANSITIONS[State.MINUS]['-'] = State.DOUBLE_MINUS;
        TRANSITIONS[State.EQUAL]['='] = State.DOUBLE_EQUAL;
        TRANSITIONS[State.LESS]['='] = State.LEQ;
        TRANSITIONS[State.MORE]['='] = State.GEQ;
        TRANSITIONS[State.EXCLAMATION]['='] = State.NEQ;

        // From keyword to keyword
        TRANSITIONS[State.E]['l'] = State.EL;
        TRANSITIONS[State.EL]['s'] = State.ELS;
        TRANSITIONS[State.ELS]['e'] = State.ELSE;
        TRANSITIONS[State.I]['f'] = State.IF;
        TRANSITIONS[State.I]['n'] = State.IN;
        TRANSITIONS[State.IN]['t'] = State.INT;
        TRANSITIONS[State.F]['o'] = State.FO;
        TRANSITIONS[State.FO]['r'] = State.FOR;
        TRANSITIONS[State.F]['l'] = State.FL;
        TRANSITIONS[State.FL]['o'] = State.FLO;
        TRANSITIONS[State.FLO]['a'] = State.FLOA;
        TRANSITIONS[State.FLOA]['t'] = State.FLOAT;
        TRANSITIONS[State.W]['h'] = State.WH;
        TRANSITIONS[State.WH]['i'] = State.WHI;
        TRANSITIONS[State.WHI]['l'] = State.WHIL;
        TRANSITIONS[State.WHILE]['e'] = State.WHILE;

        // From keyword to identifier
        fillId(State.E, 'l');
        fillId(State.EL, 's');
        fillId(State.ELS, 'e');
        fillId(State.ELSE);
        fillId(State.I, 'f', 'n');
        fillId(State.IN, 't');
        fillId(State.INT);
        fillId(State.F, 'o', 'l');
        fillId(State.FO, 'r');
        fillId(State.FOR);
        fillId(State.FL, 'o');
        fillId(State.FLO, 'a');
        fillId(State.FLOA, 't');
        fillId(State.FLOAT);
        fillId(State.W, 'h');
        fillId(State.WH, 'i');
        fillId(State.WHI, 'l');
        fillId(State.WHIL, 'e');
        fillId(State.WHILE);

        // From identifier to identifier
        fillId(State.IDENTIFIER);
    }

    /**
     * skip - lowercase character to skip. will skip the uppercase version too.
     */
    private static void fillId(int state, char... skip) {
        if (skip.length == 0)
            skip = new char[] {'a' - 1};

        Arrays.sort(skip);

        char pos = 'a';
        for (int s = 0; s < skip.length; s++) {
            for (int i = pos; i < skip[s]; i++)
                TRANSITIONS[state][i] = State.IDENTIFIER;
            pos = (char)(skip[s] + 1);
        }
        for (int i = skip[skip.length-1] + 1; i <= 'z'; i++)
            TRANSITIONS[state][i] = State.IDENTIFIER;
        pos = 'A';
        for (int s = 0; s < skip.length; s++) {
            int stopAt = skip[s] - ('a' - 'A');
            for (int i = pos; i < stopAt; i++)
                TRANSITIONS[state][i] = State.IDENTIFIER;
            pos = (char)(skip[s] + 1);
        }
        for (int i = skip[skip.length-1] - ('a' - 'A') + 1; i <= 'Z'; i++)
            TRANSITIONS[state][i] = State.IDENTIFIER;

        for (int i = '0'; i < '9'; i++)
            TRANSITIONS[state][i] = State.IDENTIFIER;

        TRANSITIONS[state]['_'] = State.IDENTIFIER;
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
                if (FINAL_STATES[currentState] != State.INVALID)
                    return new Token(tokenValue.toString(), FINAL_STATES[currentState]);
                else {
                    printInvalidToken(System.out, code, tokenValue);
                    return tokenize(code);
                }
            }
            
            int nextState = TRANSITIONS[currentState][peek];
            if (nextState == State.INVALID) {
                if (FINAL_STATES[currentState] != State.INVALID)
                    return new Token(tokenValue.toString(), FINAL_STATES[currentState]);
                else {
                    printInvalidToken(System.out, code, tokenValue);
                    return tokenize(code);
                }
            }
            else {
                tokenValue.append(code.next());
                currentState = nextState;
            }
        }

        if (FINAL_STATES[currentState] != State.INVALID)
            return new Token(tokenValue.toString(), FINAL_STATES[currentState]);

            printInvalidToken(System.out, code, tokenValue);
        return null; // end of input, there were characters that were not part of a valid token
    }

    private static void printInvalidToken(PrintStream to, SourceStream code, StringBuilder tokenChars) {
        to.println("There are characters that are not part of a valid token: '" +
        tokenChars.toString() + "' at (column, row) = (" + code.getColumn() +
        ", " + code.getRow() + ").");
    }
}
