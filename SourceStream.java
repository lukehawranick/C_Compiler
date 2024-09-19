import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.NoSuchElementException;

/**
 * @file Token.java
 * @brief A stream of source code.
 * @authors Garrett Williams
 */
public class SourceStream {
    private final Reader reader;
    private boolean hasNext;
    private char next;

    public SourceStream(Reader reader) throws IOException {
        this.reader = reader;
        
        int temp = reader.read();
        hasNext = temp != -1;
        next = (char)temp;
    }

    public static SourceStream fromFile(String fileName) throws FileNotFoundException, IOException {
        return new SourceStream(new BufferedReader(new FileReader(fileName)));
    }

    public static SourceStream fromString(String sourceCode) throws IOException {
        return new SourceStream(new StringReader(sourceCode));
    }

    public boolean hasNext() {
        return hasNext;
    }

    public char next() throws NoSuchElementException, IOException {
        if (!hasNext)
            throw new NoSuchElementException();

        char toReturn = next;
        int temp = reader.read();
        hasNext = temp != -1;
        next = (char)temp;

        return toReturn;
    }

    public char peek() throws NoSuchElementException, IOException {
        if (!hasNext)
            throw new NoSuchElementException();

        return next;
    }

    public void close() throws IOException {
        reader.close();
    }
}
