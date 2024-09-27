/**
 * @file SourceStream.java
 * @brief A SourceStream that reads source code input.
 * @authors Garrett Williams
 */

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.NoSuchElementException;

public class SourceStream implements Closeable {
    // The source code reader.
    private final Reader reader;
    // If there is a next character to read.
    private boolean hasNext;
    // The next character to read.
    private char next;
    // The column of the most recently read character.
    private int column;
    // The row of the most recently read character.
    private int row;

    /**
     * Constructs a SourceStream from a Reader.
     * @param reader
     * @throws IOException
     */
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
     * @param fileName The name of the file to read.
     * @return The SourceStream built for the file.
     * @throws FileNotFoundException If the file does not exist.
     * @throws IOException If there is an error reading the file
     */
    public static SourceStream fromFile(String fileName) throws FileNotFoundException, IOException {
        return new SourceStream(new BufferedReader(new FileReader(fileName)));
    }

    /**
     * Builds a SourceStream from the contents of a string.
     * @param sourceCode The source code to read in the form of a String
     * @return The SourceStream built for the string.
     * @throws IOException If there is an error reading the string.
     */
    public static SourceStream fromString(String sourceCode) throws IOException {
        return new SourceStream(new StringReader(sourceCode));
    }

    /**
     * Determines if there is another character to read.
     * @return True if there is another character to read, false otherwise.
     */
    public boolean hasNext() {
        return hasNext;
    }

    /**
     * Reads the next character from the source code stream.
     * @return The next character in the source code stream.
     * @throws NoSuchElementException If there are no more characters to read.
     * @throws IOException If there is an error reading the source code stream.
     */
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

    /**
     * Peeks at the next character in the source code stream.
     * @return The next character in the source code stream.
     * @throws NoSuchElementException If there are no more characters to read.
     * @throws IOException If there is an error reading the source code stream.
     */
    public char peek() throws NoSuchElementException, IOException {
        if (!hasNext)
            throw new NoSuchElementException();

        return next;
    }

    /**
     * Closes the SourceStream.
     * @throws IOException If there is an error closing the SourceStream.
     */
    public void close() throws IOException {
        reader.close();
    }

    /**
     * Gets the row of the most recently read character.
     * @return The row of the most recently read character.
     */
    public int getRow() {
        return row;
    }

    /**
     * Gets the column of the most recently read character.
     * @return The column of the most recently read character.
     */
    public int getColumn() {
        return column;
    }
}
