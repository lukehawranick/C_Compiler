package mainpackage;
/**
 * @file Scanner.java
 * @brief A Scanner that tokenizes source code input.
 * @authors Garrett Williams, Luke Hawranick
 * @reviewers Koren Spell, Mallory Anderson
 * @date 09/27/2024
 */

import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Scanner implements Iterator<Token> {
    private final SourceStream input;
    /**
     * @brief The next token to be returned by next(), or null if end of input was
     * reached. This 1 token buffer will allow hasNext() to easily determine
     * if there is a token available.
     */
    private Token next;

    /**
     * @brief Constructs a Scanner instance and prepares the first token to be
     * collected.
     * @param input The source code stream to tokenize.
     * @throws NullPointerException If the input is null.
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
    
    /**
     * @brief Determines if there are more tokens in the source code stream.
     * @returns True if there are more tokens, false otherwise.
     */
    @Override
    public boolean hasNext() {
        return next != null;
    }
    
    /**
     * @brief Returns the next token in the source code stream after assigning the new token to the class field.
     * @returns The next token in the source code stream.
     * @throws NoSuchElementException If there are no more tokens in the source code stream.
     */
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

    public Token peek() throws NoSuchElementException {
        if (next == null)
            throw new NoSuchElementException();
        return next;
    }

    /**
     * @brief Reads input until a valid token is built or the end of input is reached.
     * @returns The token, or null when there are no more valid tokens in the source code stream.
     * @throws IOException If there is an error reading the source code stream.
    */
    public static Token tokenize(SourceStream code) throws IOException {
        // begin at the starting state
        int currentState = FSM.State.START;
        StringBuilder tokenValue = new StringBuilder();

        if (!code.hasNext()) return null; // end of input
        
        while (Character.isWhitespace(code.peek())) code.next();

        if (!code.hasNext()) return null; // end of input

        while (code.hasNext()) {
            char peek = code.peek();

            // whitespace = end of token. Submit if in accepting state, otherwise, raise an error and continue.
            if (Character.isWhitespace(peek)) {
                code.next();
                if (FSM.finalState(currentState) != FSM.State.INVALID){
                    return new Token(tokenValue.toString(), FSM.finalState(currentState));
                } else {
                    tokenValue.append(peek);
                    printInvalidToken(System.out, code, tokenValue);
                    return tokenize(code);
                }
            }
            
            // otherwise, the token has not ended.
            int nextState = FSM.nextState(currentState, peek);

            // there is not a transition given by the next character
            if (nextState == FSM.State.INVALID) {
                // if the current state is a final state, return the token
                if (FSM.finalState(currentState) != FSM.State.INVALID){
                    return new Token(tokenValue.toString(), FSM.finalState(currentState));
                } else {
                    // On invalid input and non-accepting state, abandon word. Continue with next word.
                    tokenValue.append(peek);
                    printInvalidToken(System.out, code, tokenValue);
                    while (code.hasNext() && !Character.isWhitespace(code.peek())) code.next();
                    return tokenize(code);
                }
            }
            // continue building the token otherwise.
            else {
                tokenValue.append(code.next());
                currentState = nextState;
            }
        }

        // no more input - check if the current state is a final state and handle the result appropriately
        if (FSM.finalState(currentState) != FSM.State.INVALID)
            return new Token(tokenValue.toString(), FSM.finalState(currentState));
            printInvalidToken(System.out, code, tokenValue);
        return null; // end of input, there were characters that were not part of a valid token
    }

    public String getPos() {
        return String.format("(%d, %d)", input.getColumn(), input.getRow());
    }

    /**
     * @brief Prints an error message to the given PrintStream when an invalid token is found.
     * @param to The PrintStream to print the error message to.
     * @param code The current source code stream.
     * @param tokenChars The characters that are not part of a valid token.
     */
    private static void printInvalidToken(PrintStream to, SourceStream code, StringBuilder tokenChars) {
        throw new ScannerException("There are characters that are not part of a valid token: '" +
        tokenChars.toString() + "' at (c, r) = (" + code.getColumn() +
        ", " + code.getRow() + ").");
    }

    public static class ScannerException extends RuntimeException {
        public ScannerException(String msg) {
            super(msg);
        }
    }
}
