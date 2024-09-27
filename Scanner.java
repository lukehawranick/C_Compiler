import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @file Scanner.java
 * @brief A Scanner that tokenizes source code input.
 * @authors Garrett Williams
 */
public class Scanner implements Iterator<Token> {
    private final SourceStream input;
    /**
     * The next token to be returned by next(), or null if end of input was
     * reached. This 1 token buffer will allow hasNext() to easily determine
     * if there is a token available.
     */
    private Token next;

    /**
     * Constructs a Scanner instance and prepares the first token to be
     * collected.
     * @param input
     * @throws NullPointerException
     */
    public Scanner(SourceStream input) throws NullPointerException {
        if (input == null)
            throw new NullPointerException();
        
        this.input = input;
        try {
            next = tokenize(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public boolean hasNext() {
        return next != null;
    }
    
    @Override
    public Token next() throws NoSuchElementException {
        if (next == null)
            throw new NoSuchElementException();

        Token toReturn = next;
        try {
            next = tokenize(input);
        }
        catch (IOException e) {
            next = null;
            e.printStackTrace();
        }

        return toReturn;
    }

    // Reads input until a valid token is built or the end of input is reached.
    // Returns null only when there are no more valid tokens
    //   in the source code stream.
    private static Token tokenize(SourceStream code) throws IOException {
        int currentState = FSM.State.START;
        StringBuilder tokenValue = new StringBuilder();

        if (!code.hasNext()) return null; // end of input
        
        while (Character.isWhitespace(code.peek())) code.next();

        if (!code.hasNext()) return null; // end of input

        while (code.hasNext()) {
            char peek = code.peek();
            if (Character.isWhitespace(peek)) {
                code.next();
                int tokenType = FSM.finalState(currentState);
                if (tokenType != Token.Type.INVALID)
                    return new Token(tokenValue.toString(), tokenType);
                else {
                    printInvalidToken(System.out, code, tokenValue);
                    return tokenize(code);
                }
            }
            
            int nextState = FSM.nextState(currentState, peek);
            if (nextState == FSM.State.INVALID) {
                int tokenType = FSM.finalState(currentState);
                if (tokenType != Token.Type.INVALID)
                    return new Token(tokenValue.toString(), tokenType);
                else {
                    tokenValue.append(peek);
                    printInvalidToken(System.out, code, tokenValue);
                    code.next();
                    return tokenize(code);
                }
            }
            else {
                tokenValue.append(code.next());
                currentState = nextState;
            }
        }

        int tokenType = FSM.finalState(currentState);
        if (tokenType != Token.Type.INVALID)
            return new Token(tokenValue.toString(), tokenType);

        printInvalidToken(System.out, code, tokenValue);
        return null; // end of input, there were characters that were not part of a valid token
    }

    private static void printInvalidToken(PrintStream to, SourceStream code, StringBuilder tokenChars) {
        to.println("There are characters that are not part of a valid token: '" +
        tokenChars.toString() + "' at (column, row) = (" + code.getColumn() +
        ", " + code.getRow() + ").");
    }
}
