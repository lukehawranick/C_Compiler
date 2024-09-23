import java.io.IOException;
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
        try {
            next = FSM.tokenize(input);
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
            next = FSM.tokenize(input);
        }
        catch (IOException e) {
            next = null;
            e.printStackTrace();
        }

        return toReturn;
    }
}
