import java.io.BufferedReader;
import java.io.Closeable;
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
public class SourceStream implements Closeable {
    private final Reader reader;
    private boolean hasNext;
    private char next;
    // The column of the most recently read character.
    private int column;
    // The row of the most recently read character.
    private int row;

    public SourceStream(Reader reader) throws IOException {
        this.reader = reader;
        
        int temp = reader.read();
        hasNext = temp != -1;
        next = (char)temp;

        column = 0;
        row = 0;
    }

    /**
     * Builds a SourceStream from the contents of a file.
     * @param fileName
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static SourceStream fromFile(String fileName) throws FileNotFoundException, IOException {
        return new SourceStream(new BufferedReader(new FileReader(fileName)));
    }

    /**
     * Builds a SourceStream from the contents of a string.
     * @param sourceCode
     * @return
     * @throws IOException
     */
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
        
        if (next == '\n') {
            row++;
            column = 0;
        }
        else
            column++;

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

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}
