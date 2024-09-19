import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @file Scanner.java
 * @brief 
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

    public Scanner(SourceStream input) throws NullPointerException {
        if (input == null)
            throw new NullPointerException();
        
        this.input = input;
        next = scan();
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
        next = scan();
        return toReturn;
    }
    
    /**
     * Reads input until a valid Token is found or end of input is reached.
     * @return The next Token read from input, or null if end of input is
     * reached.
     */
    private Token scan() {
        // TODO: This is where the FSM comes into play. Run input through the FSM until we get a valid Token or reach end of input.
        return null;
    }
}
